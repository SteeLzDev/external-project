package com.zetra.econsig.web.controller.usuario;

import java.io.File;
import java.security.KeyPair;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.dto.web.SSOToken;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.JCrypt;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.webclient.sso.SSOClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AlterarSenhaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Alterar Senha.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/alterarSenha" })
public class AlterarSenhaWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AlterarSenhaWebController.class);

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private UsuarioController usuarioController;

    @Lazy(true)
    @Autowired
    private SSOClient ssoClient;

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        if (!TextHelper.isNull(absolutePath)) {
            absolutePath += File.separatorChar + "txt" + File.separatorChar + "principal";

            final String fileName = absolutePath + File.separatorChar + "senha.txt";
            final File arquivo = new File(fileName);
            if (arquivo.exists() && arquivo.canRead()) {
                final String msgAlteracaoSenha = FileHelper.readAll(arquivo.getAbsolutePath());
                model.addAttribute("msgAlteracaoSenha", msgAlteracaoSenha);
            }
        }

        int tamMinSenha = 6;
        int tamMaxSenha = 15;

        try {
            if (responsavel.isSer()) {
                tamMinSenha = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel)) ?
                                Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel).toString()): 6;
                tamMaxSenha = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ?
                                Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel).toString()): 15;
            } else {
                tamMinSenha = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_USUARIOS, responsavel)) ?
                                Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_USUARIOS, responsavel).toString()): 6;
                tamMaxSenha = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, responsavel)) ?
                                Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, responsavel).toString()): 15;
            }
        } catch (final NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            tamMinSenha = 6;
            tamMaxSenha = 15;
        }
        model.addAttribute("tamMinSenha", tamMinSenha);
        model.addAttribute("tamMaxSenha", tamMaxSenha);

        // Nível de Severidade da nova senha dos usuários
        String pwdStrength = "3";
        if (responsavel.isCseSupOrg()) {
            pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_CSE_ORG_PWD_STRENGTH_LEVEL, responsavel) != null ?
                          ParamSist.getInstance().getParam(CodedValues.TPC_CSE_ORG_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";
        } else if (responsavel.isCsaCor()) {
            pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_CSA_COR_PWD_STRENGTH_LEVEL, responsavel) != null ?
                          ParamSist.getInstance().getParam(CodedValues.TPC_CSA_COR_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";
        } else if (responsavel.isSer()) {
            pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel) != null ?
                          ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";
        }

        // Transforma o parâmetro em um número inteiro
        int intpwdStrength;
        try {
            intpwdStrength = Integer.parseInt(pwdStrength);
        } catch (final NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            intpwdStrength = 3;
        }
        if (intpwdStrength == 0) {
            model.addAttribute("ignoraSeveridade", Boolean.TRUE);
        }

        int pwdStrengthLevel = 1; // very weak
        String strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.muito.baixo", responsavel);
        String nivel = "muito.baixo";
        if (intpwdStrength == 2) { // weak
            pwdStrengthLevel = 16;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.baixo", responsavel);
            nivel = "baixo";
        } else if (intpwdStrength == 3) { // mediocre
            pwdStrengthLevel = 25;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.medio", responsavel);
            nivel = "medio";
        } else if (intpwdStrength == 4) { // strong
            pwdStrengthLevel = 35;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.alto", responsavel);
            nivel = "alto";
        } else if (intpwdStrength >= 5) { // very strong
            pwdStrengthLevel = 45;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.muito.alto", responsavel);
            nivel = "muito.alto";
        }
        String chave = "rotulo.ajuda.alteracaoSenha." + nivel;
        if (!responsavel.isSer()) {
            chave += ".geral";
        } else {
            chave += ".servidor";
            final boolean senhaServidorNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);
            if (senhaServidorNumerica) {
                chave += ".numerica";
            }
        }
        final String strMensagemSenha = ApplicationResourcesHelper.getMessage(chave, responsavel);
        final String strMensagemSenha1 = ApplicationResourcesHelper.getMessage(chave + ".1", responsavel);
        final String strMensagemSenha2 = ApplicationResourcesHelper.getMessage(chave + ".2", responsavel);
        final String strMensagemSenha3 = ApplicationResourcesHelper.getMessage(chave + ".3", responsavel);
        final String strMensagemErroSenha = ApplicationResourcesHelper.getMessage("mensagem.erro.requisitos.minimos.seguranca.senha.informada." + nivel, responsavel);
        model.addAttribute("pwdStrengthLevel", pwdStrengthLevel);
        model.addAttribute("strpwdStrengthLevel", strpwdStrengthLevel);
        model.addAttribute("intpwdStrength", intpwdStrength);
        model.addAttribute("strMensagemSenha", strMensagemSenha);
        model.addAttribute("strMensagemSenha1", strMensagemSenha1);
        model.addAttribute("strMensagemSenha2", strMensagemSenha2);
        model.addAttribute("strMensagemSenha3", strMensagemSenha3);
        model.addAttribute("strMensagemErroSenha", strMensagemErroSenha);

        if (responsavel.isSer()) {
            if (ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel)) {
                model.addAttribute("senhaServidorNumerica", Boolean.TRUE);
            }
            if (ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_USUARIO_SER, CodedValues.TPC_SIM, responsavel)) {
                model.addAttribute("emailUsurioSer", Boolean.TRUE);
            }
            if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST, CodedValues.TPC_SIM, responsavel)) {
                model.addAttribute("exigeCadEmailSerPrimeiroAcesso", Boolean.TRUE);
            }
            if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_CAD_TELEFONE_SER_PRIMEIRO_ACESSO_SIST, CodedValues.TPC_SIM, responsavel)) {
                model.addAttribute("exigeCadTelefoneSerPrimeiroAcesso", Boolean.TRUE);
            }

            //DESENV-14176 - Validação na tb_campo_sistema se é editável os campos e-mail, telefone e celular
            try {
                if (ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_SENHA_SER_EMAIL, responsavel)) {
                    model.addAttribute("emailSerEditavel", Boolean.TRUE);
                }

                if (ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_SENHA_SER_TELEFONE, responsavel)) {
                    model.addAttribute("telefoneSerEditavel", Boolean.TRUE);
                }

                if (ShowFieldHelper.canEdit(FieldKeysConstants.ALTERAR_SENHA_SER_CELULAR, responsavel)) {
                    model.addAttribute("celularSerEditavel", Boolean.TRUE);
                }

            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            boolean dataNascObrigatoria = false;
            boolean cpfObrigatorio = false;
            final String camposParaValidacao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CAMPO_OBRIG_ALT_SENHA_USU_SERVIDOR, responsavel);
            if (camposParaValidacao != null) {
                final String[] campos = camposParaValidacao.split(",");
                for (final String campo : campos) {
                    if (campo.trim().equalsIgnoreCase("SER_DATA_NASC")) {
                        dataNascObrigatoria = true;
                    } else if (campo.trim().equalsIgnoreCase("SER_CPF")) {
                        cpfObrigatorio = true;
                    }
                }
            }
            if (dataNascObrigatoria) {
                model.addAttribute("dataNascObrigatoria", Boolean.TRUE);
            }
            if (cpfObrigatorio) {
                model.addAttribute("cpfObrigatorio", Boolean.TRUE);
            }

            try {
                final ServidorTransferObject servidor = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel);
                final String serEmail = servidor.getSerEmail();
                final String serTel = servidor.getSerTel();
                final String serCel = servidor.getSerCelular();
                model.addAttribute("serEmail", serEmail);
                model.addAttribute("serTel", serTel);
                model.addAttribute("serCel", serCel);
            } catch (final ServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.alterar.senha.titulo", responsavel));
        return viewRedirect("jsp/alterarSenha/alterarSenha", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=alterar" })
    public String alterar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            final String senhaCriptografada = JspHelper.verificaVarQryStr(request, "senhaRSA");
            final String senhaNovaCriptografada = JspHelper.verificaVarQryStr(request, "senhaNovaRSA");
            final String dica = JspHelper.verificaVarQryStr(request, "dica");
            final String dataNasc = JspHelper.verificaVarQryStr(request, "dataNasc");
            final String cpf = JspHelper.verificaVarQryStr(request, "cpf");
            final String email = JspHelper.verificaVarQryStr(request, "email");
            final String ddd = JspHelper.verificaVarQryStr(request, "ddd");
            final String dddcel = JspHelper.verificaVarQryStr(request, "dddcel");
            String telefone = JspHelper.verificaVarQryStr(request, "telefone");
            String celular = JspHelper.verificaVarQryStr(request, "celular");

            if (!senhaCriptografada.equals("") && !senhaNovaCriptografada.equals("")) {
                // Define quais campos de validação são obrigatórios.
                boolean dataNascObrigatoria = false;
                boolean cpfObrigatorio = false;
                if (responsavel.isSer()) {
                    final String camposParaValidacao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CAMPO_OBRIG_ALT_SENHA_USU_SERVIDOR, responsavel);
                    if (camposParaValidacao != null) {
                        final String[] campos = camposParaValidacao.split(",");
                        for (final String campo : campos) {
                            if (campo.trim().equalsIgnoreCase("SER_DATA_NASC")) {
                                dataNascObrigatoria = true;
                            } else if (campo.trim().equalsIgnoreCase("SER_CPF")) {
                                cpfObrigatorio = true;
                            }
                        }
                    }
                }

                final TransferObject usuario = usuarioController.findTipoUsuarioByLogin(responsavel.getUsuLogin(), responsavel);

                final String senha = (String) usuario.getAttribute(Columns.USU_SENHA);

                // Se autenticação foi realizada pelo SSO e o usuário está alterando a sua própria senha, alteração de senha deve ser no SSO também
                final boolean autenticacaoSSO = !TextHelper.isNull(responsavel.getSsoToken()) && usuario.getAttribute(Columns.USU_CODIGO).equals(responsavel.getUsuCodigo()) && UsuarioHelper.usuarioAutenticaSso(usuario, responsavel);

                boolean acertou = false;
                boolean dataNascValida = true;
                boolean cpfValido = true;

                // Decriptografa a senha informada
                final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
                final String senhaAberta = RSA.decrypt(senhaCriptografada, keyPair.getPrivate());

                if (!autenticacaoSSO) {
                	acertou = JCrypt.verificaSenha(senhaAberta, senha);
                } else {
                	final UsuarioTransferObject usuTO = new UsuarioTransferObject((String) usuario.getAttribute(Columns.USU_CODIGO));
                	usuTO.setUsuEmail((String) usuario.getAttribute(Columns.USU_EMAIL));
                	usuTO.setUsuCPF((String) usuario.getAttribute(Columns.USU_CPF));
                	usuTO.setAtributos(usuario.getAtributos());

                	acertou = !UsuarioHelper.validarSenhaUsuarios(senhaAberta, Arrays.asList(usuTO), false, false, null, responsavel).isEmpty();
                }

                if (responsavel.isSer()) {
                	// Verifica a data de nascimento informada
                    if (dataNascObrigatoria) {
                        String serDataNasc = "";
                        try {
                            serDataNasc = DateHelper.toDateString(servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel).getSerDataNasc());
                        } catch (final Exception ex) {
                            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.data.nascimento.alteracao.senha", responsavel), ex);
                        }
                        if (serDataNasc.equals("") || !dataNasc.equals(serDataNasc)) {
                            dataNascValida = false;
                        }
                    }

                    // Verifica o CPF informado
                    if (cpfObrigatorio) {
                        String serCpf = "";
                        try {
                            serCpf = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel).getSerCpf();
                        } catch (final Exception ex) {
                            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.cpf.alteracao.senha", responsavel), ex);
                        }
                        if (serCpf.equals("") || !cpf.equals(serCpf)) {
                            cpfValido = false;
                        }
                    }
                }

                if (!autenticacaoSSO && !acertou) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.senha.atual.invalida", responsavel));
                } else if (!dataNascValida) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.data.nascimento.informada.invalida", responsavel));
                } else if (!cpfValido) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.cpf.informado.invalido", responsavel));
                } else {
                    final boolean emailUsurioSer = ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_USUARIO_SER, CodedValues.TPC_SIM, responsavel);
                    // Se exige cadastro de email do servidor no primeiro acesso ao sistema, verifica se é o primeiro acesso do servidor
                    final boolean exigeCadEmailSerPrimeiroAcesso = ParamSist.paramEquals(CodedValues.TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST, CodedValues.TPC_SIM, responsavel);
                    final boolean exigeCadTelefoneSerPrimeiroAcesso = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CAD_TELEFONE_SER_PRIMEIRO_ACESSO_SIST, responsavel);

                    final boolean primeiroAcesso = responsavel.isPrimeiroAcesso();
                    final boolean senhaServidorNumerica = responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);

                    if (responsavel.isSer() && (emailUsurioSer || exigeCadEmailSerPrimeiroAcesso || exigeCadTelefoneSerPrimeiroAcesso)) {
                        if (!validaCamposObrigatorios(request, session, responsavel)) {
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }

                        // Atualiza o e-mail do servidor.
                        final ServidorTransferObject servidor = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel);
                        if (emailUsurioSer || exigeCadEmailSerPrimeiroAcesso) {
                            if (exigeCadEmailSerPrimeiroAcesso && (TextHelper.isNull(email) || !TextHelper.isEmailValid(email))) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.invalido", responsavel));
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                            servidor.setSerEmail(email);

                        }

                        if (exigeCadTelefoneSerPrimeiroAcesso) {

                            // Atualiza telefone
                            if (TextHelper.isNull(telefone)) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.telefone", responsavel));
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }

                            telefone = TextHelper.isNull(ddd) ? telefone : ddd + '-' + telefone;
                            servidor.setSerTel(telefone);

                            // Atualiza celular
                            if (TextHelper.isNull(celular)) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.celular", responsavel));
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }

                            celular = TextHelper.isNull(dddcel) ? celular : dddcel + '-' + celular;
                            servidor.setSerCelular(celular);
                        }

                        servidorController.updateServidor(servidor, responsavel);

                    }

                    // Decriptografa a nova senha
                    final String senhaNova = RSA.decrypt(senhaNovaCriptografada, keyPair.getPrivate());

                    // se senha de servidor numérica, verifica se caracteres digitados são válidos
                    if (senhaServidorNumerica) {
                        try {
                            Integer.parseInt(senhaNova);
                        } catch (final NumberFormatException ex) {
                            LOG.error(ex.getMessage(), ex);
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.consulta.deve.ser.numerica", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }

                    usuarioController.alterarSenha((String) usuario.getAttribute(Columns.USU_CODIGO), senhaNova, dica, false, false, false, senhaAberta, responsavel);

                    // Se autentica no SSO e o token está nulo, é porque a senha estava expirada.
                    if (autenticacaoSSO && !TextHelper.isNull(responsavel.getSsoToken()) && TextHelper.isNull(responsavel.getSsoToken().access_token)) {
                    	// Realiza autenticação com a nova senha e salva o token no responsável
                        final SSOToken ssoToken = ssoClient.autenticar((String) usuario.getAttribute(Columns.USU_EMAIL), senhaNova);

                        responsavel.setSsoToken(ssoToken);
                        session.setAttribute(AcessoSistema.SESSION_ATTR_NAME, responsavel);
                    }

                    if (exigeCadEmailSerPrimeiroAcesso && primeiroAcesso) {
                        // Seta data de última data de acesso ao sistema
                        usuarioController.alteraDataUltimoAcessoSistema(responsavel);

                        responsavel.setPrimeiroAcesso(false);
                    }

                    final CustomTransferObject usuSer = usuarioController.findTipoUsuarioByCodigo((String) usuario.getAttribute(Columns.USU_CODIGO), responsavel);

                    if((!TextHelper.isNull(usuSer.getAttribute(Columns.USE_SER_CODIGO)) || responsavel.isSer()) && ParamSist.paramEquals(CodedValues.TPC_ALTERA_SENHA_TODOS_LOGINS_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                        final String[] usuLogins = usuarioController.matriculasUsuariosServidores((String) usuario.getAttribute(Columns.USU_CODIGO), null, false, responsavel);
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senhas.usuarios.servidores.alteradas", responsavel, Arrays.toString(usuLogins).replace("[", "").replace("]", "")));
                    } else {
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.alterada.sucesso", responsavel));
                    }
                    session.setAttribute(CodedValues.MSG_ALERT, null);
                    session.removeAttribute("AlterarSenha");

                    // Redireciona para a página de mensagem com botão voltar para a página principal
                    model.addAttribute("tipo", "principal");
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return iniciar(request, response, session, model);
    }

    public boolean validaCamposObrigatorios(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) throws ZetraException {

        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_SENHA_SER_CELULAR, responsavel) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "celular"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.celular", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_SENHA_SER_TELEFONE, responsavel) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "telefone"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.telefone", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_SENHA_SER_EMAIL, responsavel) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "email"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.email", responsavel));
            return false;
        }

        return true;
    }
}
