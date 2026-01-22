package com.zetra.econsig.web.controller.usuario;

import java.security.KeyPair;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: DefinirSenhaAppWebController</p>
 * <p>Description: Controlador Web para o caso de uso Definir Senha App</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/definirSenhaApp" })
public class DefinirSenhaAppWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DefinirSenhaAppWebController.class);

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        int tamMinSenha = 6;
        int tamMaxSenha = 15;

        try {
            if (responsavel.isSer()) {
                tamMinSenha = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel)) ?
                                Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel).toString()): 6;
                tamMaxSenha = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ?
                                Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel).toString()): 15;
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            tamMinSenha = 6;
            tamMaxSenha = 15;
        }
        model.addAttribute("tamMinSenha", tamMinSenha);
        model.addAttribute("tamMaxSenha", tamMaxSenha);

        // Nível de Severidade da nova senha dos usuários
        String pwdStrength = "3";
        if (responsavel.isSer()) {
            pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel) != null ?
                          ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Transforma o parâmetro em um número inteiro
        int intpwdStrength;
        try {
            intpwdStrength = Integer.parseInt(pwdStrength);
        } catch (NumberFormatException ex) {
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
        } else if (intpwdStrength >= 4) { // strong
            pwdStrengthLevel = 35;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.alto", responsavel);
            nivel = "alto";
        }
        String chave = "rotulo.ajuda.alteracaoSenha." + nivel + ".servidor";
        boolean senhaServidorNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);
        if (senhaServidorNumerica) {
            chave += ".numerica";
        }
        String strMensagemSenha = ApplicationResourcesHelper.getMessage(chave, responsavel);
        String strMensagemSenha1 = ApplicationResourcesHelper.getMessage(chave + ".1", responsavel);
        String strMensagemSenha2 = ApplicationResourcesHelper.getMessage(chave + ".2", responsavel);
        String strMensagemSenha3 = ApplicationResourcesHelper.getMessage(chave + ".3", responsavel);
        String strMensagemErroSenha = ApplicationResourcesHelper.getMessage("mensagem.erro.requisitos.minimos.seguranca.senha.informada." + nivel, responsavel);

        model.addAttribute("pwdStrengthLevel", pwdStrengthLevel);
        model.addAttribute("strpwdStrengthLevel", strpwdStrengthLevel);
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

            try {
                String serEmail = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel).getSerEmail();
                model.addAttribute("serEmail", serEmail);
            } catch (ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.definir.senha.app.titulo", responsavel));
        return viewRedirect("jsp/definirSenhaApp/definirSenhaApp", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=alterar" })
    public String alterar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String senhaNovaCriptografada = JspHelper.verificaVarQryStr(request, "senhaNovaRSA");
            String email = JspHelper.verificaVarQryStr(request, "email");

            UsuarioTransferObject usuario = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

            KeyPair keyPair = LoginHelper.getRSAKeyPair(request);

            boolean emailUsurioSer = ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_USUARIO_SER, CodedValues.TPC_SIM, responsavel);
            // Se exige cadastro de email do servidor no primeiro acesso ao sistema, verifica se é o primeiro acesso do servidor
            boolean senhaServidorNumerica = responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);

            if (responsavel.isSer()) {
                // Atualiza o e-mail do servidor.
                if (emailUsurioSer) {
                    if (TextHelper.isNull(email) || !TextHelper.isEmailValid(email)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.invalido", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    ServidorTransferObject servidor = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel);
                    servidor.setSerEmail(email);
                    servidorController.updateServidor(servidor, responsavel);
                }
            }

            // Decriptografa a nova senha
            String senhaNova = RSA.decrypt(senhaNovaCriptografada, keyPair.getPrivate());

            // se senha de servidor numérica, verifica se caracteres digitados são válidos
            if (senhaServidorNumerica) {
                try {
                    Integer.parseInt(senhaNova);
                } catch (NumberFormatException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.consulta.deve.ser.numerica", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            /*
             * Caso a troca ocorra com TPC_ALTERA_SENHA_TODOS_LOGINS_SERVIDOR = 'S' troca em todos os registros
             * levando em consideração o identificador sendo cpf ou e-mail por TPC_OMITE_CPF_SERVIDOR
             *
             */
            if((responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_ALTERA_SENHA_TODOS_LOGINS_SERVIDOR, CodedValues.TPC_SIM, responsavel))){
                boolean omiteCpf = ParamSist.getBoolParamSist(CodedValues.TPC_OMITE_CPF_SERVIDOR, responsavel);
                ServidorTransferObject responsavelServidor = servidorController.findServidorByUsuCodigo(usuario.getUsuCodigo(), responsavel);
                List<TransferObject> lstRegistroServidores = null;
                String usuAfetados = "";
                try {
                    if (omiteCpf) {
                        lstRegistroServidores = servidorController.lstRegistroServidorPorEmail(responsavelServidor.getSerEmail(), null, AcessoSistema.getAcessoUsuarioSistema());
                    } else {
                        lstRegistroServidores = servidorController.lstRegistroServidorPorCpf(responsavelServidor.getSerCpf(), null, AcessoSistema.getAcessoUsuarioSistema());
                    }

                } catch (ServidorControllerException ex) {
                    throw new UsuarioControllerException(ex);
                }

                if (lstRegistroServidores == null || lstRegistroServidores.isEmpty()) {
                    throw new UsuarioControllerException("mensagem.erro.servidor.nao.encontrado", responsavel);
                }
                for (TransferObject rseTO : lstRegistroServidores) {
                    CustomTransferObject usuTO = null;
                    try {
                        // Busca o usuário pela chave primária
                        usuTO = pesquisarServidorController.buscaUsuarioServidor(null, null, (String) rseTO.getAttribute(Columns.RSE_MATRICULA), (String) rseTO.getAttribute(Columns.ORG_IDENTIFICADOR), (String) rseTO.getAttribute(Columns.EST_IDENTIFICADOR), responsavel);
                    } catch (ServidorControllerException ex) {
                        throw new UsuarioControllerException("mensagem.erro.servidor.nao.encontrado", responsavel);
                    }
                    if (usuTO != null) {
                        // chama o método como se fosse mobile para trocar a senha app
                        usuarioController.alterarSenhaApp(usuTO.getAttribute(Columns.USU_CODIGO).toString(), senhaNova, false, true, responsavel);
                        usuAfetados += (usuAfetados.length() > 0 ? ", " : "") + usuTO.getAttribute(Columns.USU_LOGIN).toString();
                    }
                }

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senhas.usuarios.servidores.alteradas", responsavel, usuAfetados));

            } else {
                // chama o método como se fosse mobile para trocar a senha app
                usuarioController.alterarSenhaApp(usuario.getUsuCodigo(), senhaNova, false, true, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.alterada.sucesso", responsavel));
            }

            session.setAttribute(CodedValues.MSG_ALERT, null);
            session.removeAttribute("AlterarSenha");

            // Redireciona para a página de mensagem com botão voltar para a página principal
            model.addAttribute("tipo", "principal");
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
