package com.zetra.econsig.web.controller.sistema;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
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
import com.zetra.econsig.helper.log.Log;
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
 * <p>Title: AutoDesbloquearServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Recuperar Senha.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/autoDesbloquearServidor" })
public class AutoDesbloquearServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutoDesbloquearServidorWebController.class);

    private static final String JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1 = "jsp/recuperarSenha/recuperarSenhaSerPasso1";
    private static final String JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_2 = "jsp/recuperarSenha/recuperarSenhaSerPasso2";
    private static final String JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_3 = "jsp/recuperarSenha/recuperarSenhaSerPasso3";
    private static final String JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_4 = "jsp/recuperarSenha/recuperarSenhaSerPasso4";
    private static final String JSP_VISUALIZAR_MENSAGEM_ERRO                    = "jsp/visualizarPaginaErro/visualizarMensagem";

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private UsuarioController usuarioController;

    private boolean autoDesbloqueioHabilitado(AcessoSistema responsavel) {
        return !ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_DESABILITADO, responsavel);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.auto.desbloqueio.servidor.titulo", responsavel));
        model.addAttribute("autodesbloqueio", Boolean.TRUE);
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=iniciarServidor" })
    public String iniciarServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);

        if (!autoDesbloqueioHabilitado(responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        final boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);
        final boolean recuperacaoSenhaServidorComCpf = ParamSist.paramEquals(CodedValues.TPC_RECUPERACAO_SENHA_USU_SERVIDOR_CPF, CodedValues.TPC_SIM, responsavel);

        // DESENV-18038: envia o usuário para o passo 3 para recuperação de senha com autodesbloqueio
        final boolean permiteAutoDesbloqueioOtp = request.getAttribute("permiteAutoDesbloqueio") != null;
        if (permiteAutoDesbloqueioOtp) {
            tipo = "recuperar";
        }

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
            model.addAttribute("infoPaginaRecuperarSenha", ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.confirmar", responsavel));
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

        String codRecuperar = JspHelper.verificaVarQryStr(request, "cod_recuperar");
        try {
            if (TextHelper.isNull(codRecuperar)) {
                codRecuperar = (String) request.getAttribute("codRecuperaOtp");
                request.setAttribute("codRecuperar", codRecuperar);
                model.addAttribute("codRecuperar", codRecuperar);
            }

            TransferObject usuario = null;
            if (!TextHelper.isNull(codRecuperar)) {
                usuario = usuarioController.buscarUsuarioPorCodRecuperarSenha(codRecuperar, responsavel);
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

        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_3, request, session, model, responsavel);
    }

    @PostMapping( params = { "acao=concluirServidor" })
    public String concluirServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!autoDesbloqueioHabilitado(responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }

        try {
            iniciarServidor(request, response, session, model);

            final boolean recuperacaoSenhaServidorComCpf = ParamSist.paramEquals(CodedValues.TPC_RECUPERACAO_SENHA_USU_SERVIDOR_CPF, CodedValues.TPC_SIM, responsavel);

            // DESENV-18038: Responsável por controlar o fluxo de recuperação de senha com autodesbloqueio de usuário servidor
            final boolean recuperaSenhaEmail = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_EMAIL, responsavel);
            final boolean recuperaSenhaSMS = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_SMS, responsavel);
            final boolean recuperaSenhaSMSEmail = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_EMAIL_SMS, responsavel);

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
                    return concluirServidoresComCPF(usuariosComMesmoCPF, request, session, model, responsavel);
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
                    // A validação dos campos abaixo será feita de acordo com o parametro 551 da DESENV-18038 quando autodesbloqueio for igual a true.
                    if (recuperaSenhaEmail) {
                        if (TextHelper.isNull(serEmail)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.servidor.nao.cadastrado", responsavel));
                            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                        }

                        final String campoEmail = JspHelper.verificaVarQryStr(request, "usuEmail");

                        if (!serEmail.equals(campoEmail)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.dados.nao.conferem", responsavel));
                            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
                        }
                    }
                } else {
                    if (recuperaSenhaEmail) {
                        if (TextHelper.isNull(serEmail)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.email.nao.cadastrado", responsavel));
                            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                        }
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

                    String link = request.getRequestURL().toString();
                    link += "?acao=iniciarServidor&enti=" + responsavel.getTipoEntidade();
                    // Gera uma nova codigo de recuparação de senha
                    final String cod_Senha = SynchronizerToken.generateToken();
                    // Quando o parametro 551 for diferente de 0
                    // Caso de uso Recuperação de senha com auto-desbloqueio e envio de link de recuperação para e-mail do servidor (Parametro 551 = 1)
                    if (recuperaSenhaEmail) {
                        if (Boolean.parseBoolean(request.getParameter("dadosIncorretos"))) {
                            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.SERVIDOR, null, Log.LOG_AVISO);
                            final String logObs = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.log.dados.nao.conferem", responsavel, TextHelper.escondeEmail(serEmail), "");
                            logDelegate.add(logObs);
                            logDelegate.write();
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.gentileza.atualizar.dados", responsavel));
                            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
                        }
                        if ("S".equals(confirmaDados)) {
                            return ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.confirmar.email", responsavel, TextHelper.escondeEmail(serEmail));
                        }
                        // Atualiza o codigo de recuperação de senha do usuário
                        usuarioController.alteraChaveRecupSenhaAutoDesbloqueio(usuCodigo, cod_Senha, responsavel);
                        // Envia e-mail com link para recuperação de senha
                        usuarioController.enviaLinkReinicializarSenhaSerAutoDesbloqueio(usuCodigo, matricula, link, cod_Senha, responsavel);
                        // Retorna mensagem de sucesso para o usuário
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.usuario.sucesso", responsavel));
                        model.addAttribute("linkRet64", TextHelper.encode64(LoginHelper.getPaginaLoginServidor()));
                        return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
                    }

                    // Caso de uso Recuperação de senha com auto-desbloqueio e envio de otp para o celular do servidor (Parametro 551 = 2)
                    if (recuperaSenhaSMS) {
                        final String serCelular = servidor.getSerCelular();
                        // Validação do celular do servidor para envio de OTP
                        if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                            if (TextHelper.isNull(serCelular)) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.celular.servidor.nao.cadastrado", responsavel));
                                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                            }

                            final String campoSerDDDCelular = JspHelper.verificaVarQryStr(request, "serDddCelular");
                            final String campoSerCelular = JspHelper.verificaVarQryStr(request, "serCelular");
                            final String campoCelular = campoSerDDDCelular + "-" + campoSerCelular;

                            if (!serCelular.equals(campoCelular)) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.dados.nao.conferem", responsavel));
                                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                            }
                        } else if (TextHelper.isNull(serCelular)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.celular.servidor.nao.cadastrado", responsavel));
                            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                        }

                        if (Boolean.parseBoolean(request.getParameter("dadosIncorretos"))) {
                            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.SERVIDOR, null, Log.LOG_AVISO);
                            final String logObs = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.log.dados.nao.conferem", responsavel, "", TextHelper.escondeTelefone(serCelular));
                            logDelegate.add(logObs);
                            logDelegate.write();
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.gentileza.atualizar.dados", responsavel));
                            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
                        }

                        if ("S".equals(confirmaDados)) {
                            return ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.confirmar.celular", responsavel, TextHelper.escondeTelefone(serCelular));
                        }

                        final String serCelularEscondido = TextHelper.escondeTelefone(serCelular);
                        final String msgOtp = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.sms.otp", responsavel, serCelularEscondido);
                        model.addAttribute("msgOtp", msgOtp);
                        model.addAttribute("usuCodigo", usuCodigo);

                        usuarioController.enviaOTPServidor(usuCodigo, cod_Senha, null, serCelular, false, true, false, false, responsavel);

                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_4, request, session, model, responsavel);
                    }

                    // Caso de uso Recuperação de senha com auto-desbloqueio e envio de otp para o celular e/ou e-mail do servidor (Parametro 551 = 3)
                    if (recuperaSenhaSMSEmail) {
                        final String serCelular = servidor.getSerCelular();

                        boolean enviaOtpEmail = true;
                        boolean enviaOtpCelular = true;

                        // Validação do e-mail e/ou do celular do servidor para envio de OTP
                        if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                            if (TextHelper.isNull(serEmail) && TextHelper.isNull(serCelular)) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.sem.celular.email", responsavel));
                                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                            }

                            final String campoSerDDDCelular = JspHelper.verificaVarQryStr(request, "serDddCelular");
                            final String campoSerCelular = JspHelper.verificaVarQryStr(request, "serCelular");
                            final String campoCelular = campoSerDDDCelular + "-" + campoSerCelular;
                            final String campoEmail = JspHelper.verificaVarQryStr(request, "usuEmail");

                            if (TextHelper.isNull(campoCelular) && TextHelper.isNull(campoEmail)) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.preenchimento.campos", responsavel));
                                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                            }

                            if (!TextHelper.isNull(campoEmail)) {
                                if (TextHelper.isNull(serEmail)) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.servidor.nao.cadastrado", responsavel));
                                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                                } else if (!serEmail.equals(campoEmail)) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.dados.nao.conferem", responsavel));
                                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                                }
                            } else {
                                enviaOtpEmail = false;
                            }

                            if (!TextHelper.isNull(campoCelular)) {
                                if (TextHelper.isNull(serCelular)) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.celular.servidor.nao.cadastrado", responsavel));
                                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                                } else if (!serCelular.equals(campoCelular)) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.dados.nao.conferem", responsavel));
                                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                                }
                            } else {
                                enviaOtpCelular = false;
                            }
                        } else if (TextHelper.isNull(serEmail) && TextHelper.isNull(serCelular)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.sem.celular.email", responsavel));
                            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                        }

                        String msgOtp = null;

                        if (!TextHelper.isNull(serEmail) && !TextHelper.isNull(serCelular) && enviaOtpEmail && enviaOtpCelular) {
                            msgOtp = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.sms.email.otp", responsavel, TextHelper.escondeTelefone(serCelular), TextHelper.escondeEmail(serEmail));
                        } else if (!TextHelper.isNull(serEmail) && enviaOtpEmail) {
                            msgOtp = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.email.otp", responsavel, TextHelper.escondeEmail(serEmail));
                        } else if (!TextHelper.isNull(serCelular) && enviaOtpCelular) {
                            msgOtp = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.sms.otp", responsavel, TextHelper.escondeTelefone(serCelular));
                        }

                        if (Boolean.parseBoolean(request.getParameter("dadosIncorretos"))) {
                            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.SERVIDOR, null, Log.LOG_AVISO);
                            final String logObs = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.log.dados.nao.conferem", responsavel, TextHelper.escondeEmail(serEmail), TextHelper.escondeTelefone(serCelular));
                            logDelegate.add(logObs);
                            logDelegate.write();
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.gentileza.atualizar.dados", responsavel));
                            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
                        }

                        if ("S".equals(confirmaDados)) {
                            String msgConfirmacao = null;
                            if (!TextHelper.isNull(serEmail) && !TextHelper.isNull(serCelular)) {
                                msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.confirmar.celular.email", responsavel, TextHelper.escondeTelefone(serCelular), TextHelper.escondeEmail(serEmail));
                            } else if (!TextHelper.isNull(serEmail)) {
                                msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.confirmar.email", responsavel, TextHelper.escondeEmail(serEmail));
                            } else {
                                msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.confirmar.celular", responsavel, TextHelper.escondeTelefone(serCelular));
                            }
                            return msgConfirmacao;
                        }

                        model.addAttribute("msgOtp", msgOtp);
                        model.addAttribute("usuCodigo", usuCodigo);

                        usuarioController.enviaOTPServidor(usuCodigo, cod_Senha, serEmail, serCelular, false, true, enviaOtpEmail, enviaOtpCelular, responsavel);

                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_4, request, session, model, responsavel);
                    }

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

        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
    }

    /**
    * DESENV-13969
    * Método criado para reinicializar senha de usuário servidor quando parâmetro de sistema 759 e o novo layout(v4) estão habilitados.
    * Foi criado um método separado do que já existia, para que este possa iterar quando um usuário servidor possuir duas ou mais matrículas, sem afetar o que já funcionava antes.
    **/
    private String concluirServidoresComCPF(List<TransferObject> usuariosComMesmoCPF, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ZetraException {
        // DESENV-18038: Responsável por controlar o fluxo de recuperação de senha com autodesbloqueio de usuário servidor
        final boolean recuperaSenhaEmail = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_EMAIL, responsavel);
        final boolean recuperaSenhaSMS = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_SMS, responsavel);
        final boolean recuperaSenhaSMSEmail = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_EMAIL_SMS, responsavel);

        final String confirmaDados = JspHelper.verificaVarQryStr(request, "confirmaDados");

        final Set<String> emailsServidores = new HashSet<>();

        // Início validação para saber se os usuarios servidores possuem o mesmo e-mail. Caso contrário retorna erro. Não é necessário validar quando houver autodesbloqueio e a recuperação de senha for por SMS
        if (!recuperaSenhaSMS) {
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
        }

        // Gera uma nova codigo de recuparação de senha
        final String codRecSenha = SynchronizerToken.generateToken();

        String usuLogin = null;
        String link = null;
        String usuCodigo = null;
        String serEmail = null;

        // Verifica se existe mais de um usuário com mesmo CPF, utilizando a mesma lógica do caso de uso AutenticarServidor com parâmetro de sistema 674
        for (final TransferObject usu : usuariosComMesmoCPF) {
            usuLogin = usu.getAttribute(Columns.USU_LOGIN).toString();

            // Seta responsavel
            try {
                responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(usuLogin, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
            }

            String serCodigo = null;

            // Envia email com link pra alteração de senha
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.info.login.servidor", responsavel, usuLogin));
            List<RegistroServidorTO> registroServidor = new ArrayList<>();

            try {
                // Busca o registro servidor que não deve estar na situação de excluído
                serCodigo = usu.getAttribute(Columns.SER_CODIGO).toString();

                final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);

                if (servidor == null) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }

                registroServidor = servidorController.findRegistroServidorBySerCodigo(serCodigo, responsavel);
                if (registroServidor.isEmpty() || (registroServidor == null)) {
                    // Não localizou o servidor, retorna mensagem de erro
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }

                // Encontrou Usuário e Servidor, verifica se possui e-mail cadastrado
                serEmail = servidor.getSerEmail();

                final String serCpf = servidor.getSerCpf();
                final String campoCpf = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "usuCpf")) ? JspHelper.verificaVarQryStr(request, "usuCpf") : JspHelper.verificaVarQryStr(request, "USU_CPF");

                if (!recuperaSenhaSMS) {
                    emailsServidores.add(serEmail);
                }

                if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    // A validação dos campos abaixo será feita de acordo com o parametro 551 da DESENV-18038 quando autodesbloqueio for igual a true.
                    if (recuperaSenhaEmail) {
                        if (TextHelper.isNull(serEmail)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.servidor.nao.cadastrado", responsavel));
                            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                        }

                        final String campoEmail = JspHelper.verificaVarQryStr(request, "usuEmail");

                        if (!serEmail.equals(campoEmail)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.dados.nao.conferem", responsavel));
                            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                        }
                    }
                } else {
                    if (recuperaSenhaEmail) {
                        if (TextHelper.isNull(serEmail)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.email.nao.cadastrado", responsavel));
                            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                        }
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
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);

            }

            // valida IP/DNS de acesso
            UsuarioHelper.verificarIpDDNSAcesso(AcessoSistema.ENTIDADE_SER, serCodigo, JspHelper.getRemoteAddr(request), (String) usu.getAttribute(Columns.USU_IP_ACESSO), (String) usu.getAttribute(Columns.USU_DDNS_ACESSO), (String) usu.getAttribute(Columns.USU_CODIGO), responsavel);

            // O servidor possui e-mail, então envia email com link para alterar senha
            usuCodigo = (String) usu.getAttribute(Columns.USU_CODIGO);

            if (recuperaSenhaEmail) {
                link = request.getRequestURL().toString();
                link += "?acao=iniciarServidor&enti=" + responsavel.getTipoEntidade();

                try {
                    // Atualiza o codigo de recuperação de senha do usuário
                    usuarioController.alteraChaveRecupSenhaAutoDesbloqueio(usuCodigo, codRecSenha, responsavel);
                } catch (final UsuarioControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }
            }
        }

        // If está fora da iteração pois deve-se enviar apenas um único e-mail e/ou SMS para reinicializar a senha. A condição de entrada é o parametro 551 ser diferente 0.
        // Caso de uso Recuperação de senha com auto-desbloqueio e envio de link de recuperação para e-mail do servidor (Parametro 551 = 1)
        if (recuperaSenhaEmail) {
            if (Boolean.parseBoolean(request.getParameter("dadosIncorretos"))) {
                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.SERVIDOR, null, Log.LOG_AVISO);
                final String logObs = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.log.dados.nao.conferem", responsavel, TextHelper.escondeEmail(serEmail), "");
                logDelegate.add(logObs);
                logDelegate.write();
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.gentileza.atualizar.dados", responsavel));
                return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
            }
            if ("S".equals(confirmaDados)) {
                return ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.confirmar.email", responsavel, TextHelper.escondeTelefone(serEmail));
            }
            // Envia e-mail com link para recuperação de senha
            usuarioController.enviaLinkReinicializarSenhaSerAutoDesbloqueio(usuCodigo, null, link, codRecSenha, responsavel);
            // Retorna mensagem de sucesso para o usuário
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.usuario.sucesso", responsavel));
            model.addAttribute("linkRet64", TextHelper.encode64(LoginHelper.getPaginaLoginServidor()));
            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_2, request, session, model, responsavel);
        }

        // Caso de uso Recuperação de senha com auto-desbloqueio e envio de otp para o celular do servidor (Parametro 551 = 2)
        if (recuperaSenhaSMS) {
            final Set<String> celularesServidores = new HashSet<>();
            String serCelular = null;
            String serCodigo = null;

            // Verificação para saber se os usuarios servidores possuem o mesmo  celular.
            for (final TransferObject usuarioCPF : usuariosComMesmoCPF) {
                try {
                    final ServidorTransferObject servidor = servidorController.findServidor(usuarioCPF.getAttribute(Columns.SER_CODIGO).toString(), AcessoSistema.recuperaAcessoSistemaByLogin(usuarioCPF.getAttribute(Columns.USU_LOGIN).toString(), JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request)));
                    celularesServidores.add(servidor.getSerCelular());
                } catch (final ZetraException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }
            }

            // Emitir uma mensagem de erro caso o CPF informado tenha mais de um registro na tabela "tb_servidor" com celulares diferentes
            if (celularesServidores.size() > 1) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.celular.servidor.diferente.mesmo.cpf", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
            }

            // Validações que verificam se existe número de celular cadastrado para os usuários dos servidores
            for (final TransferObject usu : usuariosComMesmoCPF) {
                serCodigo = usu.getAttribute(Columns.SER_CODIGO).toString();
                final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);
                serCelular = servidor.getSerCelular();

                // Validação do celular do servidor para envio de OTP
                if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    if (TextHelper.isNull(serCelular)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.celular.servidor.nao.cadastrado", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }

                    final String campoSerDDDCelular = JspHelper.verificaVarQryStr(request, "serDddCelular");
                    final String campoSerCelular = JspHelper.verificaVarQryStr(request, "serCelular");
                    final String campoCelular = campoSerDDDCelular + "-" + campoSerCelular;

                    if (!serCelular.equals(campoCelular)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.dados.nao.conferem", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }
                } else if (TextHelper.isNull(serCelular)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.celular.servidor.nao.cadastrado", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }
            }

            if (Boolean.parseBoolean(request.getParameter("dadosIncorretos"))) {
                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.SERVIDOR, null, Log.LOG_AVISO);
                final String logObs = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.log.dados.nao.conferem", responsavel, "", TextHelper.escondeTelefone(serCelular));
                logDelegate.add(logObs);
                logDelegate.write();
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.gentileza.atualizar.dados", responsavel));
                return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
            }

            if ("S".equals(confirmaDados)) {
                return ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.confirmar.celular", responsavel, TextHelper.escondeTelefone(serCelular));
            }

            final String msgOtp = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.sms.otp", responsavel, TextHelper.escondeTelefone(serCelular));
            model.addAttribute("msgOtp", msgOtp);
            model.addAttribute("usuCodigo", usuCodigo);

            usuarioController.enviaOTPServidor(usuCodigo, codRecSenha, null, serCelular, false, true, false, false, responsavel);

            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_4, request, session, model, responsavel);
        }

        // Caso de uso Recuperação de senha com auto-desbloqueio e envio de otp para o celular e/ou e-mail do servidor (Parametro 551 = 3)
        if (recuperaSenhaSMSEmail) {
            final Set<String> celularesServidores = new HashSet<>();
            String serCelular = null;
            String serCodigo = null;
            boolean enviaOtpEmail = true;
            boolean enviaOtpCelular = true;

            // Verificação para saber se os usuarios servidores possuem o mesmo  celular.
            for (final TransferObject usuarioCPF : usuariosComMesmoCPF) {
                try {
                    final ServidorTransferObject servidor = servidorController.findServidor(usuarioCPF.getAttribute(Columns.SER_CODIGO).toString(), AcessoSistema.recuperaAcessoSistemaByLogin(usuarioCPF.getAttribute(Columns.USU_LOGIN).toString(), JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request)));
                    celularesServidores.add(servidor.getSerCelular());
                } catch (final ZetraException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }
            }

            // Emitir uma mensagem de erro caso o CPF informado tenha mais de um registro na tabela "tb_servidor" com celulares diferentes
            if (celularesServidores.size() > 1) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.celular.servidor.diferente.mesmo.cpf", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
            }

            for (final TransferObject usu : usuariosComMesmoCPF) {
                serCodigo = usu.getAttribute(Columns.SER_CODIGO).toString();
                final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);
                serCelular = servidor.getSerCelular();

                // Validação do e-mail e/ou do celular do servidor para envio de OTP
                if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    if (TextHelper.isNull(serEmail) && TextHelper.isNull(serCelular)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.sem.celular.email", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }

                    final String campoSerDDDCelular = JspHelper.verificaVarQryStr(request, "serDddCelular");
                    final String campoSerCelular = JspHelper.verificaVarQryStr(request, "serCelular");
                    final String campoCelular = campoSerDDDCelular + "-" + campoSerCelular;
                    final String campoEmail = JspHelper.verificaVarQryStr(request, "usuEmail");

                    if (ParamSist.paramEquals(CodedValues.TPC_RECUPERACAO_SENHA_USU_SERVIDOR_CPF, CodedValues.TPC_SIM, responsavel)) {
                        if (TextHelper.isNull(campoCelular) && TextHelper.isNull(campoEmail)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.dados.nao.conferem", responsavel));
                            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                        }
                    } else if (TextHelper.isNull(campoCelular) && TextHelper.isNull(campoEmail)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.preenchimento.campos", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                    }

                    if (!TextHelper.isNull(campoEmail)) {
                        if (TextHelper.isNull(serEmail)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.servidor.nao.cadastrado", responsavel));
                            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                        } else if (!serEmail.equals(campoEmail)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.dados.nao.conferem", responsavel));
                            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                        }
                    } else {
                        enviaOtpEmail = false;
                    }

                    if (!TextHelper.isNull(campoCelular)) {
                        if (TextHelper.isNull(serCelular)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.celular.servidor.nao.cadastrado", responsavel));
                            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                        } else if (!serCelular.equals(campoCelular)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.dados.nao.conferem", responsavel));
                            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                        }
                    } else {
                        enviaOtpCelular = false;
                    }
                } else if (TextHelper.isNull(serEmail) && TextHelper.isNull(serCelular)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.sem.celular.email", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
                }
            }

            if (Boolean.parseBoolean(request.getParameter("dadosIncorretos"))) {
                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.SERVIDOR, null, Log.LOG_AVISO);
                final String logObs = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.log.dados.nao.conferem", responsavel, TextHelper.escondeEmail(serEmail), TextHelper.escondeTelefone(serCelular));
                logDelegate.add(logObs);
                logDelegate.write();
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.gentileza.atualizar.dados", responsavel));
                return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
            }

            if ("S".equals(confirmaDados)) {
                String msgConfirmacao = null;
                if (!TextHelper.isNull(serEmail) && !TextHelper.isNull(serCelular)) {
                    msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.confirmar.celular.email", responsavel, TextHelper.escondeTelefone(serCelular), TextHelper.escondeEmail(serEmail));
                } else if (!TextHelper.isNull(serEmail)) {
                    msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.confirmar.email", responsavel, TextHelper.escondeEmail(serEmail));
                } else {
                    msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.confirmar.celular", responsavel, TextHelper.escondeTelefone(serCelular));
                }
                return msgConfirmacao;
            }

            String msgOtp = null;
            if (!TextHelper.isNull(serEmail) && !TextHelper.isNull(serCelular) && enviaOtpEmail && enviaOtpCelular) {
                msgOtp = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.sms.email.otp", responsavel, TextHelper.escondeTelefone(serCelular), TextHelper.escondeEmail(serEmail));
            } else if (!TextHelper.isNull(serEmail) && enviaOtpEmail) {
                msgOtp = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.email.otp", responsavel, TextHelper.escondeEmail(serEmail));
            } else if (!TextHelper.isNull(serCelular) && enviaOtpCelular) {
                msgOtp = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.sms.otp", responsavel, TextHelper.escondeTelefone(serCelular));
            }

            model.addAttribute("msgOtp", msgOtp);
            model.addAttribute("usuCodigo", usuCodigo);

            usuarioController.enviaOTPServidor(usuCodigo, codRecSenha, serEmail, serCelular, false, true, enviaOtpEmail, enviaOtpCelular, responsavel);

            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_4, request, session, model, responsavel);
        }

        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_1, request, session, model, responsavel);
    }

    @PostMapping( params = { "acao=validarOtp" })
    public String validarOtpServidorAutoDesbloqueio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        if (!autoDesbloqueioHabilitado(responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }

        try {
            String token = null;

            final String usuCodigo = JspHelper.verificaVarQryStr(request, "usuCodigo");
            final String serOtp = JspHelper.verificaVarQryStr(request, "SER_OTP");

            final UsuarioTransferObject usu = usuarioController.findUsuario(usuCodigo, responsavel);
            final String usuOtp = (String) usu.getAttribute(Columns.USU_OTP_CODIGO);
            if (!TextHelper.isNull(usuOtp)) {
                token = usuarioController.validarOTPPortal(usu, serOtp, responsavel);
            }

            if (TextHelper.isNull(token)) {
                Integer qtdTentativasValidacaoOTP = session.getAttribute("qtdTentativasValidacaoOTP") != null ? (Integer) session.getAttribute("qtdTentativasValidacaoOTP") : 1;
                if (qtdTentativasValidacaoOTP >= 3) {
                    session.removeAttribute("qtdTentativasValidacaoOTP");
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.otp.invalido", responsavel));
                    model.addAttribute("linkRet64", TextHelper.encode64(LoginHelper.getPaginaLoginServidor()));
                    return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
                } else {
                    session.setAttribute("qtdTentativasValidacaoOTP", ++qtdTentativasValidacaoOTP);
                }

                model.addAttribute("msgOtp", ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.otp.invalido", responsavel));
                model.addAttribute("usuCodigo", usuCodigo);

                // Se é nulo, o OTP está incorreto, volta para a interface para o usuário tentar novamente
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_SER_PASSO_4, request, session, model, responsavel);
            }

            final List<String> usuCodigos = new ArrayList<>();
            usuCodigos.add(usuCodigo);

            // Verifica se a recuperação de senha do usuário servidor será com CPF, de acordo com o parâmetro de sistema 759, e caso seja,
            // define a chave de recuperação de senha para todos os usuários do mesmo CPF
            if (ParamSist.paramEquals(CodedValues.TPC_RECUPERACAO_SENHA_USU_SERVIDOR_CPF, CodedValues.TPC_SIM, responsavel)) {
                final TransferObject usuario = usuarioController.obtemUsuarioTipo(usuCodigo, null, responsavel);
                final ServidorTransferObject servidor = servidorController.findServidor(usuario.getAttribute("CODIGO").toString(), responsavel);

                String campoCpf = servidor.getSerCpf();
                if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    campoCpf = servidor.getSerEmail();
                }

                // Usando a mesma query/lógica do caso de uso ao autenticar servidor com parâmetro de sistema 674
                final List<TransferObject> usuariosComMesmoCPF = usuarioController.lstUsuariosSerLoginComCpf(null, null, campoCpf, null, null, true, responsavel);
                if ((usuariosComMesmoCPF != null) && !usuariosComMesmoCPF.isEmpty()) {
                    // Remove os elementos da lista e adiciona novamente
                    usuCodigos.clear();

                    // Adiciona todos os códigos novamente
                    usuariosComMesmoCPF.forEach(usuarioComMesmoCPF -> {
                        usuCodigos.add(usuarioComMesmoCPF.getAttribute(Columns.USU_CODIGO).toString());
                    });
                }
            }

            for (final String codigo : usuCodigos) {
                usuarioController.alteraChaveRecupSenhaAutoDesbloqueio(codigo, token, responsavel);
            }

            request.setAttribute("permiteAutoDesbloqueio", true);
            request.setAttribute("codRecuperaOtp", token);

            return iniciarServidor(request, response, session, model);

        } catch (final UsuarioControllerException | ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.nao.pode.desbloqueado", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }
    }

    @PostMapping( params = { "acao=recuperarServidor" })
    public String recuperarServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!autoDesbloqueioHabilitado(responsavel)) {
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

            // Localiza o usuario servidor no banco de dados
            // Inicio Concluir servidor
            final String senhaNovaCriptografada = JspHelper.verificaVarQryStr(request, "senhaNovaRSA");
            final String dica = JspHelper.verificaVarQryStr(request, "dica");
            final TransferObject usuario = usuarioController.findTipoUsuarioByCodigo(responsavel.getUsuCodigo(), responsavel);

            // Envia email com link pra alteração de senha
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.info.login.servidor", responsavel, usuLogin));

            if (!CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE.equals(usuario.getAttribute(Columns.USU_STU_CODIGO).toString())) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.servidor.nao.pode.desbloqueado", responsavel));
                return redirecionarPasso3Servidor(request, response, session, model);
            }

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
                    final ServidorTransferObject serTO = servidorController.findServidor(serCodigo, responsavel);
                    final boolean omiteCpf = ParamSist.getBoolParamSist(CodedValues.TPC_OMITE_CPF_SERVIDOR, responsavel);
                    usuarioController.recuperarSenha(omiteCpf ? serTO.getSerEmail() : serTO.getSerCpf(), matricula, null, otp, senhaNova, false, false, responsavel);
                } else if (recuperacaoSenhaServidorComCpf && !serCodigos.isEmpty()) {
                    // Executa a recuperação de senha
                    usuarioController.recuperarSenha(usuCodigos, AcessoSistema.ENTIDADE_SER, codRecuperacaoSenha, senhaNova, dica, true, responsavel);
                } else {
                    usuarioController.recuperarSenha(usuCodigo, AcessoSistema.ENTIDADE_SER, codRecuperacaoSenha, senhaNova, dica, true, responsavel);
                }
                // Retorna mensagem de sucesso para o usuário
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.alterada.auto.desbloqueio.servidor.sucesso", responsavel));
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

    @PostMapping( params = { "acao=concluirServidorAjax" })
    public ResponseEntity<String> concluirServidorAjax(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
        try {
            return ResponseEntity.ok(concluirServidor(request, response, session, model));
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
