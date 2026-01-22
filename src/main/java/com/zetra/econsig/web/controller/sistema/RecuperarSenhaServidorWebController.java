package com.zetra.econsig.web.controller.sistema;

import java.security.KeyPair;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.SenhaConfig;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: RecuperarSenhaServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Recuperar Senha.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/recuperarSenhaServidor" })
public class RecuperarSenhaServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RecuperarSenhaServidorWebController.class);

    private static final String JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1 = "jsp/recuperarSenha/recuperarSenhaSerPasso1";
    private static final String JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_2 = "jsp/recuperarSenha/recuperarSenhaSerPasso2";
    private static final String JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_3 = "jsp/recuperarSenha/recuperarSenhaSerPasso3";
    private static final String JSP_VISUALIZAR_MENSAGEM_ERRO                    = "jsp/visualizarPaginaErro/visualizarMensagem";

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private UsuarioController usuarioController;

    private boolean recuperarSenhaHabilitado(AcessoSistema responsavel) {
        return ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_RECUPERAR_SENHA_SER, CodedValues.TPC_SIM, responsavel);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.recuperar.senha.servidor.titulo", responsavel));
        model.addAttribute("autodesbloqueio", Boolean.FALSE);
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=iniciarServidor" })
    public String iniciarServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);

        if (!recuperarSenhaHabilitado(responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        final String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        final boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);
        final boolean recuperacaoSenhaServidorComCpf = ParamSist.paramEquals(CodedValues.TPC_RECUPERACAO_SENHA_USU_SERVIDOR_CPF, CodedValues.TPC_SIM, responsavel);

        // Servidor
        response.addCookie(new Cookie("LOGIN", "SERVIDOR"));

        try {
            if (!recuperacaoSenhaServidorComCpf) {
                if (loginComEstOrg) {
                    model.addAttribute("lstOrgao", consignanteController.lstOrgaos(null, responsavel));
                } else {
                    model.addAttribute("lstEstabelecimento", consignanteController.lstEstabelecimentos(null, responsavel));
                }
            }
        } catch (final ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }

        model.addAttribute("tipo", JspHelper.verificaVarQryStr(request, "tipo"));
        model.addAttribute("recuperacaoSenhaServidorComCpf", recuperacaoSenhaServidorComCpf);

        if ("".equals(tipo)) {
            model.addAttribute("infoPaginaRecuperarSenha", ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.recuperar.confirmar", responsavel));
            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);

        } else if ("recuperar".equals(tipo)) {
            return redirecionarPasso3Servidor(request, response, session, model);

        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }
    }

    private String redirecionarPasso3Servidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final String codRecuperacaoEnviado = JspHelper.verificaVarQryStr(request, "cod_recuperar");
            final String otpEnviado = JspHelper.verificaVarQryStr(request, "otp");

            TransferObject usuario = null;
            if (!ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_SERVIDOR, CodedValues.ENVIA_OTP_DESABILITADO, responsavel)) {
                if (model.getAttribute("usuarioOtp") != null) {
                    usuario = (TransferObject) model.getAttribute("usuarioOtp");
                } else if (!TextHelper.isNull(otpEnviado)) {
                    // Se está vindo de uma tentativa de informar o OTP e estava inválido, o objeto "usuarioOtp" não irá existir
                    // e o OTP enviado é inválido. Então carrega os dados do usuário pelo seu login para nova tentativa de informar o OTP
                    if (ParamSist.paramEquals(CodedValues.TPC_RECUPERACAO_SENHA_USU_SERVIDOR_CPF, CodedValues.TPC_SIM, responsavel)) {
                        final String campoCpf = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "usuCpf")) ? JspHelper.verificaVarQryStr(request, "usuCpf") : JspHelper.verificaVarQryStr(request, "USU_CPF");
                        final List<TransferObject> usuariosComMesmoCPF = usuarioController.lstUsuariosSerLoginComCpf(null, null, campoCpf, null, null, true, responsavel);
                        if ((usuariosComMesmoCPF != null) && !usuariosComMesmoCPF.isEmpty()) {
                            usuario = usuariosComMesmoCPF.get(0);
                        }
                    } else {
                        final String matricula = LoginHelper.getMatriculaRequisicao(request, "matricula", responsavel);
                        final String codigoEntidade = JspHelper.verificaVarQryStr(request, "codigoOrgao");
                        final String usuLogin = LoginHelper.getLoginServidor(matricula, codigoEntidade, responsavel);
                        usuario = usuarioController.findTipoUsuarioByLogin(usuLogin, responsavel);
                    }
                }
            } else if (!TextHelper.isNull(codRecuperacaoEnviado)) {
                usuario = usuarioController.buscarUsuarioPorCodRecuperarSenha(codRecuperacaoEnviado, responsavel);
            }
            if (usuario == null) {
                // Retorno mensagem de erro se o codigo de recuperação de senha for invalido
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.recuperar.senha.codigo.nao.localizado", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }

        SenhaConfig senhaConfig = SenhaConfig.getSenhaServidorConfig(responsavel);
        model.addAttribute("intpwdStrength", senhaConfig.forcaSenha());
        model.addAttribute("pwdStrengthLevel", senhaConfig.nivelForcaSenha());
        model.addAttribute("strpwdStrengthLevel", senhaConfig.rotuloForcaSenha());
        model.addAttribute("ignoraSeveridade", senhaConfig.ignoraSeveridade());
        model.addAttribute("strMensagemSenha", senhaConfig.mensagemSenha());
        model.addAttribute("strMensagemSenha1", senhaConfig.mensagemSenha1());
        model.addAttribute("strMensagemSenha2", senhaConfig.mensagemSenha2());
        model.addAttribute("strMensagemSenha3", senhaConfig.mensagemSenha3());
        model.addAttribute("strMensagemErroSenha", senhaConfig.mensagemErroSenha());
        model.addAttribute("tamMinSenhaServidor", senhaConfig.tamMinSenha());
        model.addAttribute("tamMaxSenhaServidor", senhaConfig.tamMaxSenha());

        // Verifica se o parâmetro para envio de otp está habilitado. Caso esteja, recireciona para tela de validação de otp e criação de nova senha
        if (!ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_SERVIDOR, CodedValues.ENVIA_OTP_DESABILITADO, responsavel)) {
            model.addAttribute("geraOtp", true);
        }

        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_3, request, session, model, responsavel);
    }

    @PostMapping( params = { "acao=concluirServidor" })
    public String concluirServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!recuperarSenhaHabilitado(responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }

        try {
            iniciarServidor(request, response, session, model);

            final boolean recuperacaoSenhaServidorComCpf = ParamSist.paramEquals(CodedValues.TPC_RECUPERACAO_SENHA_USU_SERVIDOR_CPF, CodedValues.TPC_SIM, responsavel);
            final String confirmaDados = JspHelper.verificaVarQryStr(request, "confirmaDados");

            // Valida captcha
            final String captchaAnswer = !TextHelper.isNull(request.getParameter("captcha")) ? request.getParameter("captcha") : !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "captcha")) ? JspHelper.verificaVarQryStr(request, "captcha") : null;
            final String captchaCode = (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
            final String captchaAudioAnswer = !TextHelper.isNull(request.getParameter("captchaAudio")) ? request.getParameter("captchaAudio") : !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "captchaAudio")) ? JspHelper.verificaVarQryStr(request, "captchaAudio") : null;
            final String captchaAudioCode = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);

            if ((captchaAudioAnswer == null) && (ImageCaptchaServlet.armazenaCaptcha(session.getId(), captchaCode)
                    && !ImageCaptchaServlet.validaCaptcha(session.getId(), captchaAnswer))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
            } else if ((captchaAnswer == null) && !captchaAudioCode.equalsIgnoreCase(captchaAudioAnswer)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
            }
            if (!"S".equals(confirmaDados)) {
                session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
            }
            final String campoCpf = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "usuCpf")) ? JspHelper.verificaVarQryStr(request, "usuCpf") : JspHelper.verificaVarQryStr(request, "USU_CPF");

            // DESENV-13969 - Verifica se a recuperação de senha do usuário servidor será com CPF, de acordo com o parâmetro de sistema 759, no layout v4.
            if (recuperacaoSenhaServidorComCpf) {
                // Usando a mesma query/lógica do caso de uso ao autenticar servidor com parâmetro de sistema 674
                final List<TransferObject> usuariosComMesmoCPF = usuarioController.lstUsuariosSerLoginComCpf(null, null, campoCpf, null, null, true, responsavel);
                if ((usuariosComMesmoCPF != null) && !usuariosComMesmoCPF.isEmpty()) {
                    return concluirServidoresComCPF(usuariosComMesmoCPF, request, response, session, model, responsavel);
                } else {
                    // Não localizou o servidor, retorna mensagem de erro
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }
            } else {
                final String matricula = LoginHelper.getMatriculaRequisicao(request, "matricula", responsavel);
                final String codigoEntidade = JspHelper.verificaVarQryStr(request, "codigoOrgao");
                final String usuLogin = LoginHelper.getLoginServidor(matricula, codigoEntidade, responsavel);

                // Seta responsavel
                try {
                    responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(usuLogin, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }

                final String orgCodigo = responsavel.getOrgCodigo();
                final String estCodigo = responsavel.getEstCodigo();

                // Localiza o usuario servidor no banco de dados
                final TransferObject usuario = usuarioController.findTipoUsuarioByCodigo(responsavel.getUsuCodigo(), responsavel);

                // Envia email com link pra alteração de senha
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.info.login.servidor", responsavel, usuLogin));

                // Busca o registro servidor que não deve estar na situação de excluído
                final String serCodigo = (String) usuario.getAttribute(Columns.USE_SER_CODIGO);
                final TransferObject registroServidor = servidorController.getRegistroServidorPelaMatricula(serCodigo, orgCodigo, estCodigo, matricula, responsavel);
                if (registroServidor == null) {
                    // Não localizou o servidor, retorna mensagem de erro
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }

                final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);
                // Encontrou Usuário e Servidor, verifica se possui e-mail cadastrado
                final String serEmail = servidor.getSerEmail();
                final String serCpf = servidor.getSerCpf();

                if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    if (TextHelper.isNull(serEmail)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.servidor.nao.cadastrado", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }

                    final String campoEmail = JspHelper.verificaVarQryStr(request, "usuEmail");

                    if (!serEmail.equals(campoEmail)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.dados.nao.conferem", responsavel));
                        return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
                    }

                } else {
                    if (TextHelper.isNull(serEmail)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.email.nao.cadastrado", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }
                    if (TextHelper.isNull(serCpf)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.cpf.nao.cadastrado", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }
                    if (!serCpf.equals(campoCpf)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }
                }

                try {
                    // valida IP/DNS de acesso
                    UsuarioHelper.verificarIpDDNSAcesso(AcessoSistema.ENTIDADE_SER, serCodigo, JspHelper.getRemoteAddr(request), (String) usuario.getAttribute(Columns.USU_IP_ACESSO), (String) usuario.getAttribute(Columns.USU_DDNS_ACESSO), (String) usuario.getAttribute(Columns.USU_CODIGO), responsavel);

                    // O servidor possui e-mail, então envia email com link para alterar senha
                    final String usuCodigo = (String) usuario.getAttribute(Columns.USU_CODIGO);

                    // Verifica se o parâmetro para envio de otp está habilitado. Caso esteja, recireciona para tela de validação de otp e criação de nova senha
                    if (!ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_SERVIDOR, CodedValues.ENVIA_OTP_DESABILITADO, responsavel)) {
                        final boolean omiteCpf = ParamSist.getBoolParamSist(CodedValues.TPC_OMITE_CPF_SERVIDOR, responsavel);
                        final UsuarioTransferObject usuarioOtp = usuarioController.recuperarSenha(omiteCpf ? serEmail : serCpf, matricula, List.of(orgCodigo), null, null, false, false, responsavel);
                        model.addAttribute("geraOtp", true);
                        model.addAttribute("usuarioOtp", usuarioOtp);
                        return redirecionarPasso3Servidor(request, response, session, model);
                    }

                    String link = request.getRequestURL().toString();
                    link += "?acao=iniciarServidor&enti=" + responsavel.getTipoEntidade();
                    // Gera uma nova codigo de recuparação de senha
                    final String cod_Senha = SynchronizerToken.generateToken();

                    // Atualiza o codigo de recuperação de senha do usuário
                    usuarioController.alteraChaveRecupSenha(usuCodigo, cod_Senha, responsavel);
                    // Envia e-mail com link para recuperação de senha
                    usuarioController.enviaLinkReinicializarSenhaSer(usuCodigo, matricula, link, cod_Senha, responsavel);
                    // Retorna mensagem de sucesso para o usuário
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.reinicializar.senha.usuario.sucesso", responsavel, TextHelper.escondeEmail(servidor.getSerEmail())));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_2, request, session, model, responsavel);

                } catch (final ZetraException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }
            }
        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
        } catch (ConsignanteControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
        }
    }

    /**
    * DESENV-13969
    * Método criado para reinicializar senha de usuário servidor quando parâmetro de sistema 759 e o novo layout(v4) estão habilitados.
    * Foi criado um método separado do que já existia, para que este possa iterar quando um usuário servidor possuir duas ou mais matrículas, sem afetar o que já funcionava antes.
    **/
    private String concluirServidoresComCPF(List<TransferObject> usuariosComMesmoCPF, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, AcessoSistema responsavel) throws ZetraException {
        final Set<String> emailsServidores = new HashSet<>();

        // Início validação para saber se os usuarios servidores possuem o mesmo e-mail. Caso contrário retorna erro.
        for (final TransferObject usuarioCPF : usuariosComMesmoCPF) {
            try {
                final ServidorTransferObject servidor = servidorController.findServidor(usuarioCPF.getAttribute(Columns.SER_CODIGO).toString(), AcessoSistema.recuperaAcessoSistemaByLogin(usuarioCPF.getAttribute(Columns.USU_LOGIN).toString(), JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request)));
                emailsServidores.add(servidor.getSerEmail());
            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
            }
        }

        // Emitir uma mensagem de erro caso o CPF informado tenha mais de um registro na tabela "tb_servidor", com e-mails diferentes, e matrículas ativas (caso de uma pessoa usando CPF de outra, como pai/filho, cônjuges, etc).
        if (emailsServidores.size() > 1) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.servidor.diferente.mesmo.cpf", responsavel));
            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
        }

        // Gera uma nova codigo de recuparação de senha
        final String codRecSenha = SynchronizerToken.generateToken();

        String link = null;
        String usuCodigo = null;
        String serEmail = null;
        String serCpf = null;

        // Verifica se existe mais de um usuário com mesmo CPF, utilizando a mesma lógica do caso de uso AutenticarServidor com parâmetro de sistema 674
        for (final TransferObject usu : usuariosComMesmoCPF) {
            final String usuLogin = usu.getAttribute(Columns.USU_LOGIN).toString();
            final String serCodigo = usu.getAttribute(Columns.SER_CODIGO).toString();

            // Seta responsavel
            try {
                responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(usuLogin, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
            }

            // Envia email com link pra alteração de senha
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.info.login.servidor", responsavel, usuLogin));

            try {
                // Busca o registro servidor que não deve estar na situação de excluído
                final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);
                if (servidor == null) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }

                final List<RegistroServidorTO> registroServidor = servidorController.findRegistroServidorBySerCodigo(serCodigo, responsavel);
                if (registroServidor.isEmpty() || (registroServidor == null)) {
                    // Não localizou o servidor, retorna mensagem de erro
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }

                // Encontrou Usuário e Servidor, verifica se possui e-mail cadastrado
                serEmail = servidor.getSerEmail();
                serCpf = servidor.getSerCpf();

                emailsServidores.add(serEmail);

                if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    if (TextHelper.isNull(serEmail)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.servidor.nao.cadastrado", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }

                    final String campoEmail = JspHelper.verificaVarQryStr(request, "usuEmail");

                    if (!serEmail.equals(campoEmail)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.dados.nao.conferem", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }

                } else {
                    if (TextHelper.isNull(serEmail)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.email.nao.cadastrado", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }
                    if (TextHelper.isNull(serCpf)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.cpf.nao.cadastrado", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }
                    final String campoCpf = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "usuCpf")) ? JspHelper.verificaVarQryStr(request, "usuCpf") : JspHelper.verificaVarQryStr(request, "USU_CPF");
                    if (!serCpf.equals(campoCpf)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }
                }
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);

            }

            // valida IP/DNS de acesso
            UsuarioHelper.verificarIpDDNSAcesso(AcessoSistema.ENTIDADE_SER, serCodigo, JspHelper.getRemoteAddr(request), (String) usu.getAttribute(Columns.USU_IP_ACESSO), (String) usu.getAttribute(Columns.USU_DDNS_ACESSO), (String) usu.getAttribute(Columns.USU_CODIGO), responsavel);

            // O servidor possui e-mail, então envia email com link para alterar senha
            usuCodigo = (String) usu.getAttribute(Columns.USU_CODIGO);

            link = request.getRequestURL().toString();
            link += "?acao=iniciarServidor&enti=" + responsavel.getTipoEntidade();

            try {
                // Atualiza o codigo de recuperação de senha do usuário
                usuarioController.alteraChaveRecupSenha(usuCodigo, codRecSenha, responsavel);
            } catch (final UsuarioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
            }
        }

        // Verifica se o parâmetro para envio de otp está habilitado. Caso esteja, recireciona para tela de validação de otp e criação de nova senha
        if (!ParamSist.paramEquals(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_SERVIDOR, CodedValues.ENVIA_OTP_DESABILITADO, responsavel)) {
            final boolean omiteCpf = ParamSist.getBoolParamSist(CodedValues.TPC_OMITE_CPF_SERVIDOR, responsavel);
            final UsuarioTransferObject usuarioOtp = usuarioController.recuperarSenha(omiteCpf ? serEmail : serCpf, null, null, null, null, false, false, responsavel);
            model.addAttribute("geraOtp", true);
            model.addAttribute("usuarioOtp", usuarioOtp);
            return redirecionarPasso3Servidor(request, response, session, model);
        }

        // Envia e-mail com link para recuperação de senha
        usuarioController.enviaLinkReinicializarSenhaSer(usuCodigo, null, link, codRecSenha, responsavel);
        // Retorna mensagem de sucesso para o usuário
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.reinicializar.senha.usuario.sucesso", responsavel, TextHelper.escondeEmail(serEmail)));
        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_2, request, session, model, responsavel);
    }

    @PostMapping( params = { "acao=recuperarServidor" })
    public String recuperarServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!recuperarSenhaHabilitado(responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }

        try {
            iniciarServidor(request, response, session, model);

            final String matricula = LoginHelper.getMatriculaRequisicao(request, "matricula", responsavel);
            String usuLogin = null;

            // Valida captcha
            if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                        && !ImageCaptchaServlet.validaCaptcha(session.getId(), request.getParameter("captcha"))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                return redirecionarPasso3Servidor(request, response, session, model);
            }
            session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);

            final boolean recuperacaoSenhaServidorComCpf = ParamSist.paramEquals(CodedValues.TPC_RECUPERACAO_SENHA_USU_SERVIDOR_CPF, CodedValues.TPC_SIM, responsavel);
            final String campoCpf = JspHelper.verificaVarQryStr(request, "USU_CPF");
            final Set<String> serCodigos = new HashSet<>();
            final Set<String> usuCodigos = new HashSet<>();

            // DESENV-13969 - Verifica se a recuperação de senha do usuário servidor será com CPF, de acordo com o parâmetro de sistema 759, no layout v4.
            if (recuperacaoSenhaServidorComCpf) {
                // Usando a mesma query/lógica do caso de uso ao autenticar servidor com parâmetro de sistema 674
                final List<TransferObject> usuariosComMesmoCPF = usuarioController.lstUsuariosSerLoginComCpf(null, null, campoCpf, null, null, true, responsavel);
                if ((usuariosComMesmoCPF != null) && !usuariosComMesmoCPF.isEmpty()) {
                    usuLogin = usuariosComMesmoCPF.get(0).getAttribute(Columns.USU_LOGIN).toString();
                    usuariosComMesmoCPF.forEach(usu -> {
                        serCodigos.add(usu.getAttribute(Columns.SER_CODIGO).toString());
                    });
                    usuariosComMesmoCPF.forEach(usu -> {
                        usuCodigos.add(usu.getAttribute(Columns.USU_CODIGO).toString());
                    });

                } else {
                    // Não localizou o servidor, retorna mensagem de erro
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }
            } else {
                final String codigoEntidade = JspHelper.verificaVarQryStr(request, "codigoOrgao");
                usuLogin = LoginHelper.getLoginServidor(matricula, codigoEntidade, responsavel);
            }

            // Seta responsavel
            try {
                responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(usuLogin, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
                return redirecionarPasso3Servidor(request, response, session, model);
            }

            final String orgCodigo = responsavel.getOrgCodigo();
            final String estCodigo = responsavel.getEstCodigo();

            final String senhaNovaCriptografada = JspHelper.verificaVarQryStr(request, "senhaNovaRSA");
            final String dica = JspHelper.verificaVarQryStr(request, "dica");
            final TransferObject usuario = usuarioController.findTipoUsuarioByCodigo(responsavel.getUsuCodigo(), responsavel);

            // Envia email com link pra alteração de senha
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.info.login.servidor", responsavel, usuLogin));

            // Busca o registro servidor que não deve estar na situação de excluído
            final String serCodigo = (String) usuario.getAttribute(Columns.USE_SER_CODIGO);

            try {
                if (recuperacaoSenhaServidorComCpf && !serCodigos.isEmpty()) {
                    for (final String scod : serCodigos) {
                        final List<RegistroServidorTO> registrosServidor = servidorController.findRegistroServidorBySerCodigo(scod, responsavel);
                        if ((registrosServidor == null) || registrosServidor.isEmpty()) {
                            // Não localizou o servidor, retorna mensagem de erro
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                            return redirecionarPasso3Servidor(request, response, session, model);
                        }
                        final ServidorTransferObject servidor = servidorController.findServidor(scod, responsavel);
                        if (servidor == null) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                            return redirecionarPasso3Servidor(request, response, session, model);
                        }

                        // valida IP/DNS de acesso
                        UsuarioHelper.verificarIpDDNSAcesso(AcessoSistema.ENTIDADE_SER, scod, JspHelper.getRemoteAddr(request), (String) usuario.getAttribute(Columns.USU_IP_ACESSO), (String) usuario.getAttribute(Columns.USU_DDNS_ACESSO), (String) usuario.getAttribute(Columns.USU_CODIGO), responsavel);
                    }
                } else {
                    final TransferObject registroServidor = servidorController.getRegistroServidorPelaMatricula(serCodigo, orgCodigo, estCodigo, matricula, responsavel);
                    if (registroServidor == null) {
                        // Não localizou o servidor, retorna mensagem de erro
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                        return redirecionarPasso3Servidor(request, response, session, model);
                    }
                    // Encontrou Usuário e Servidor.
                    final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);
                    if (servidor == null) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                        return redirecionarPasso3Servidor(request, response, session, model);
                    }

                    // valida IP/DNS de acesso
                    UsuarioHelper.verificarIpDDNSAcesso(AcessoSistema.ENTIDADE_SER, serCodigo, JspHelper.getRemoteAddr(request), (String) usuario.getAttribute(Columns.USU_IP_ACESSO), (String) usuario.getAttribute(Columns.USU_DDNS_ACESSO), (String) usuario.getAttribute(Columns.USU_CODIGO), responsavel);
                }

                // Busca usu_codigo do usuario
                final String usuCodigo = (String) usuario.getAttribute(Columns.USU_CODIGO);
                // Pega cod_senha do link
                final String codRecuperacaoSenha = JspHelper.verificaVarQryStr(request, "cod_recuperar");
                // Decriptografa a nova senha
                final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
                final String senhaNova = RSA.decrypt(senhaNovaCriptografada, keyPair.getPrivate());

                if (TextHelper.isNull(codRecuperacaoSenha)) {
                    final String otp = JspHelper.verificaVarQryStr(request, "otp");
                    if (TextHelper.isNull(otp)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.otp.codigo", responsavel));
                        return redirecionarPasso3Servidor(request, response, session, model);
                    }
                    final List<String> orgCodigos = (!recuperacaoSenhaServidorComCpf ? List.of(orgCodigo) : null);
                    final ServidorTransferObject serTO = servidorController.findServidor(serCodigo, responsavel);
                    final boolean omiteCpf = ParamSist.getBoolParamSist(CodedValues.TPC_OMITE_CPF_SERVIDOR, responsavel);
                    usuarioController.recuperarSenha(omiteCpf ? serTO.getSerEmail() : serTO.getSerCpf(), matricula, orgCodigos, otp, senhaNova, false, false, responsavel);
                } else if (recuperacaoSenhaServidorComCpf && !serCodigos.isEmpty()) {
                    // Executa a recuperação de senha
                    usuarioController.recuperarSenha(usuCodigos, AcessoSistema.ENTIDADE_SER, codRecuperacaoSenha, senhaNova, dica, false, responsavel);
                } else {
                    usuarioController.recuperarSenha(usuCodigo, AcessoSistema.ENTIDADE_SER, codRecuperacaoSenha, senhaNova, dica, false, responsavel);
                }
                // Retorna mensagem de sucesso para o usuário
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.recuperar.senha.servidor.sucesso", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_2, request, session, model, responsavel);

            } catch (ViewHelperException | UsuarioControllerException | ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return redirecionarPasso3Servidor(request, response, session, model);
            }
        } catch (final BadPaddingException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
            return redirecionarPasso3Servidor(request, response, session, model);
        } catch (final ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return redirecionarPasso3Servidor(request, response, session, model);
        }
    }
}
