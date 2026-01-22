package com.zetra.econsig.web.controller.usuario;

import java.io.File;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.MenuTO;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.dto.web.SSOToken;
import com.zetra.econsig.dto.web.TipoEntidadeVO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.CorrespondenteControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.MensagemControllerException;
import com.zetra.econsig.exception.MenuControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.SSOException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.CryptoUtil;
import com.zetra.econsig.helper.seguranca.GoogleAuthenticatorHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Correspondente;
import com.zetra.econsig.persistence.entity.Perfil;
import com.zetra.econsig.persistence.entity.TipoEntidade;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.correspondente.CorrespondenteController;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.service.menu.MenuController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoValidacaoTotpEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.controller.ajuda.ChatbotRestController;
import com.zetra.econsig.web.listener.SessionCounterListener;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;
import com.zetra.econsig.webclient.sso.SSOClient;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Title: AutenticarUsuarioWebController</p>
 * <p>Description: Controlador Web para o caso de uso Autenticar Usuários de papeis não servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 */
@Controller
public class AutenticarUsuarioWebController extends AbstractWebController {

	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutenticarUsuarioWebController.class);

	private static final String USERNAME_PARAM = "username";
	private static final String ACCESS_TOKEN_PARAM = "sso_token";
	private static final String USU_CENTRALIZADOR_PARAM = "usuCentralizador";
	private static final String URL_CENTRALIZADOR_ACESSO_PARAM = "urlCentralizadorAcesso";

    @Autowired
    private MenuController menuController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private MensagemController mensagemController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private CorrespondenteController correspondenteController;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Autowired
    private SessionCounterListener sessionManagment;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", JspHelper.getNomeSistema(responsavel));
    }

    @RequestMapping(value = { "/" })
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        try {
            final Short status = sistemaController.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            final boolean indisponivel = status.equals(CodedValues.STS_INDISP);
            final boolean boasVindasHabilitada = ParamSist.paramEquals(CodedValues.TPC_HABILITA_PAGINA_BOAS_VINDAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            if (indisponivel) {
                session.setAttribute(CodedValues.MSG_ERRO, LoginHelper.getMensagemSistemaIndisponivel());
                return "redirect:/v3/exibirMensagem?acao=exibirMsgSessao&tipo=indisponivel";
            } else if(boasVindasHabilitada) {
            	return "redirect:/v3/boasVindas?acao=iniciar";
            } else {
            	return redirecionarPaginaLogin();
            }
        } catch (final ConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(value = { "/v3/autenticarUsuario" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String telaValidacao = (model.containsAttribute("telaValidacao")) ? (String) model.asMap().get("telaValidacao") : (String) request.getAttribute("telaValidacao");
        final String usuCentralizador = JspHelper.verificaVarQryStr(request, USU_CENTRALIZADOR_PARAM);

        try {
            if (responsavel.isSessaoValida() && TextHelper.isNull(telaValidacao) && usuCentralizador.isEmpty()) {
                //recupera se o login veio do fluxo da DESENV-13252
                final Boolean termoUso = (Boolean) session.getAttribute("termo_usu");

                // Invalida a sessão do usuário, caso exista
                session.invalidate();
                session = request.getSession(true);

                if (termoUso != null) {
                    session.setAttribute("termo_usu", Boolean.TRUE);
                }

                // Obtém novamente o responsável
                responsavel = JspHelper.getAcessoSistema(request);
            }
        } catch (final IllegalStateException ex) {
            // Trata erro caso a sessão já esteja invalidada
        }

        if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_MENSAGEM_TELA_LOGIN_USUARIO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            try {
                final String mensagemTelaLoginUsuario = getTextoAutenticacaoUsuario(responsavel);
                if (!TextHelper.isNull(mensagemTelaLoginUsuario)) {
                    model.addAttribute("mensagemTelaLoginUsuario", mensagemTelaLoginUsuario);
                }
            } catch (final ViewHelperException e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        if(!TextHelper.isNull(request.getAttribute("urlCentralizador"))) {
            final String urlCentral = (String) request.getAttribute("urlCentralizador");
            session.setAttribute("urlCentralizador", urlCentral);
        }
        //com as novas interfaces do spring os cookies são gravados com domínios diferentes, logo
        //invalida ambos
        JspHelper.setaCookieLogin(response, request.getContextPath());

        model.addAttribute("nomeCse", LoginHelper.getCseNome(responsavel));
        model.addAttribute("nomeSistema", JspHelper.getNomeSistema(responsavel));
        model.addAttribute("usuBloqueado", (model.containsAttribute("usuBloqueado")) ?
                (String) model.asMap().get("usuBloqueado") : !TextHelper.isNull(request.getAttribute("usuBloqueado")) ?
                        (String) request.getAttribute("usuBloqueado") : "false");
        model.addAttribute("msgUsuBloqueado", LoginHelper.getMsgUsuarioBloq(request, responsavel));
        boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean exibeCaptchaDeficiente = false;
        // parâmetro para definir se o login é realizado em duas etapas
        final boolean validacaoSeguranca = ParamSist.paramEquals(CodedValues.TPC_VALIDACAO_SEGURANCA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        final String tipoCaptchaAvancado = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TIPO_CAPTCHA_AVANCADO_LOGIN, AcessoSistema.getAcessoUsuarioSistema());
        model.addAttribute("mascaraNomeLogin", ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_NOME_LOGIN, responsavel));
        model.addAttribute("mensagemMascaraNomeLogin", LoginHelper.getMensagemMascaraNomeLogin(responsavel));
        model.addAttribute("telaLogin", LoginHelper.getPaginaLogin());
        String usuLogin = null;
        String usuNome = null;
        String loginDefVisual = null;
        boolean exibeHcaptcha = false;
        boolean exibeRecaptcha = false;
        model.addAttribute("internetExplorer", false);
        if (validacaoSeguranca) {
            if (TextHelper.isNull(telaValidacao)) {
                model.addAttribute("telaValidacao", "1");
            } else {
                model.addAttribute("telaValidacao", telaValidacao);
            }
            usuLogin = (model.containsAttribute("usuLogin")) ? (String) model.asMap().get("usuLogin") : (String) request.getAttribute("usuLogin");
            usuNome = (model.containsAttribute("usuNome")) ? (String) model.asMap().get("usuNome") : (String) request.getAttribute("usuNome");
            loginDefVisual = (model.containsAttribute("usuDeficienteVisual")) ? (String) model.asMap().get("usuDeficienteVisual") : !TextHelper.isNull(request.getAttribute("usuDeficienteVisual")) ? (String) request.getAttribute("usuDeficienteVisual") : "N";
            if ("S".equals(loginDefVisual)) {
                exibeCaptcha = false;
                exibeCaptchaAvancado = false;
                exibeHcaptcha = false;
                exibeRecaptcha = false;
                exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            }
        }

        if (exibeCaptchaAvancado && CodedValues.TPC_H.equals(tipoCaptchaAvancado)) {
        	exibeHcaptcha = true;
        } else if (exibeCaptchaAvancado && CodedValues.TPC_R.equals(tipoCaptchaAvancado)) {
        	exibeRecaptcha = true;
        }

        model.addAttribute("validacaoSeguranca", validacaoSeguranca);
        model.addAttribute("usuLogin", usuLogin);
        model.addAttribute("usuNome", usuNome);
        model.addAttribute("exibeCaptcha", exibeCaptcha);
        model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
        model.addAttribute("exibeHcaptcha", exibeHcaptcha);
        model.addAttribute("exibeRecaptcha", exibeRecaptcha);
        model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
        model.addAttribute("ajudaCampoCaptcha", ApplicationResourcesHelper.getMessage("ajuda.campo.captcha", responsavel));
        model.addAttribute("tituloPaginaLoginCsa", ApplicationResourcesHelper.getMessage("rotulo.titulo.pagina.login.csa", responsavel));
        model.addAttribute("ajudaSenha", ApplicationResourcesHelper.getMessage("mensagem.informacao.digite.senha", responsavel));

        // Habilita o chatbot na página de login caso o parâmetro esteja habilitado
        if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_ID_CHATBOT_PAGINA_LOGIN_USU, responsavel)) ||
                !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_ID_CHATBOT_PAGINA_LOGIN_SER, responsavel))) {
            session.setAttribute(ChatbotRestController.CHATBOT_ORIGEM_LOGIN_SERVIDOR, Boolean.FALSE);
        }

        return viewRedirect("jsp/autenticarUsuario/autenticarUsuario", request, session, model, responsavel);

    }

    @RequestMapping(value = { "/v3/autenticarUsuario" }, params = { "acao=autenticar" })
    public String autenticar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        String usuId = JspHelper.verificaVarQryStr(request, USERNAME_PARAM);
        String ssoToken = request.getParameter(ACCESS_TOKEN_PARAM);

		final String params = request.getParameter("p");

		if (!TextHelper.isNull(params)) {
			try {
				final String urlDecoded = cryptoUtil.decrypt(TextHelper.decode64(params));

				final String[] parametros = urlDecoded.split("&");

				final String userName = parametros[0].substring(parametros[0].indexOf(USERNAME_PARAM) + USERNAME_PARAM.length() + 1, parametros[0].length());
				final String token = parametros[1].substring(parametros[1].indexOf(ACCESS_TOKEN_PARAM) + ACCESS_TOKEN_PARAM.length() + 1, parametros[1].length());
				final String urlCentralizador = parametros[2].substring(parametros[2].indexOf(USU_CENTRALIZADOR_PARAM) + USU_CENTRALIZADOR_PARAM.length() + 1, parametros[2].length());
				final String urlCentralizadorAcesso = parametros[3].substring(parametros[3].indexOf(URL_CENTRALIZADOR_ACESSO_PARAM) + URL_CENTRALIZADOR_ACESSO_PARAM.length() + 1, parametros[3].length());

				usuId = userName;
				ssoToken = token;
				request.setAttribute(USU_CENTRALIZADOR_PARAM, urlCentralizador);
				request.setAttribute(URL_CENTRALIZADOR_ACESSO_PARAM, urlCentralizadorAcesso);

			} catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
	            return iniciar(request, response, session, model);
			}
		}

        if (TextHelper.isNull(usuId)) {
            return iniciar(request, response, session, model);
        }

        final String senhaCriptografada = JspHelper.verificaVarQryStr(request, JspHelper.verificaVarQryStr(request, "cryptedPasswordFieldName"));

        final String msgErroLoginInvalido = LoginHelper.getMensagemErroLogin();
        // verifica se o login é realizado em duas etapas
        final boolean validacaoSeguranca = ParamSist.paramEquals(CodedValues.TPC_VALIDACAO_SEGURANCA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        final String telaValidacao = (!TextHelper.isNull(request.getParameter("telaValidacao"))) ? request.getParameter("telaValidacao") : "";
        String loginDefVisual = "N";

        try {
            // Pesquisa o usuário pelo login ou email informado
            List<TransferObject> usuarioList = null;

            if (!TextHelper.isNull(usuId)) {
                usuarioList = UsuarioHelper.localizarUsuario(usuId, responsavel);
            }

            // Usuário só é considerado deficiente visual quando existe login em duas etapas
            if (((usuarioList != null) && !usuarioList.isEmpty()) && validacaoSeguranca) {
                loginDefVisual = (usuarioList.get(0).getAttribute(Columns.USU_DEFICIENTE_VISUAL) != null ? usuarioList.get(0).getAttribute(Columns.USU_DEFICIENTE_VISUAL).toString() : "N");
            }

            // Se for centralizador de acesso, não deve validar captcha
            if (!TextHelper.isNull(request.getParameter(URL_CENTRALIZADOR_ACESSO_PARAM)) || !TextHelper.isNull(request.getAttribute(URL_CENTRALIZADOR_ACESSO_PARAM))) {
                session.setAttribute(com.zetra.econsig.values.CodedNames.ATTR_SESSION_CENTRALIZADOR, Boolean.TRUE);
            }

            // verifica se o login é feito em duas etapas e volta para a tela anterior para solicitar os outros dados
            if (validacaoSeguranca && (!TextHelper.isNull(telaValidacao) && "1".equals(telaValidacao)) && (session.getAttribute(CodedNames.ATTR_SESSION_CENTRALIZADOR) == null)) {
                if ((usuarioList != null) && !usuarioList.isEmpty()) {
                    // recupera informações do usuário
                    model.addAttribute("usuNome", TextHelper.formataNomeUsuario(usuarioList.get(0).getAttribute(Columns.USU_NOME).toString().toUpperCase(), responsavel));
                    model.addAttribute("usuLogin", usuId);
                    model.addAttribute("usuDeficienteVisual", loginDefVisual);
                } else {
                    // retorna um nome aleatório para dificultar ação de "robôs"
                    final int total = usuarioController.countNomeUsuario(responsavel);
                    // gera o número aleatório usando um seed para garantir que a consulta retornará sempre o mesmo nome para o login informado (para login incorreto)
                    final int seed = usuId.hashCode();
                    final Random aleatorio = new Random(seed);
                    final int offset = aleatorio.nextInt(total);
                    final CustomTransferObject usuarioRand = (CustomTransferObject) usuarioController.obtemNomeUsuario(null, null, offset, responsavel);
                    final String usuarioRandNome = usuarioRand.getAttribute(Columns.USU_NOME).toString();
                    model.addAttribute("usuNome", TextHelper.formataNomeUsuario(usuarioRandNome.toUpperCase(), responsavel));
                    model.addAttribute("usuLogin", usuId);
                    model.addAttribute("usuDeficienteVisual", "N");
                }

                boolean usuarioPodeAutoDesbloquear = verificarUsuarioPodeAutoDesbloquear (usuarioList, responsavel);

                model.addAttribute("usuarioPodeAutoDesbloquear", usuarioPodeAutoDesbloquear);
                model.addAttribute("telaValidacao", "2");

                iniciar(request, response, session, model);
                return viewRedirect("jsp/autenticarUsuario/autenticarUsuarioPasso2",  request, session, model, responsavel);
            }

            if (!"S".equals(loginDefVisual)) {
                if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, responsavel) &&
                        (session.getAttribute(CodedNames.ATTR_SESSION_CENTRALIZADOR) == null)) {
                    if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                        && !ImageCaptchaServlet.validaCaptcha(session.getId(), request.getParameter("captcha"))) {
                        throw new UsuarioControllerException("mensagem.erro.captcha.invalido", responsavel);
                    }
                    session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                } else if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_TIPO_CAPTCHA_AVANCADO_LOGIN, CodedValues.TPC_R, responsavel) &&
                        (session.getAttribute(CodedNames.ATTR_SESSION_CENTRALIZADOR) == null)) {
                    final String remoteAddr = request.getRemoteAddr();

                    if (!isValidCaptcha(request.getParameter("g-recaptcha-response"), remoteAddr, responsavel)) {
                        throw new UsuarioControllerException("mensagem.erro.captcha.invalido", responsavel);
                    }
                } else if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_TIPO_CAPTCHA_AVANCADO_LOGIN, CodedValues.TPC_H, responsavel) &&
                        (session.getAttribute(CodedNames.ATTR_SESSION_CENTRALIZADOR) == null)) {
                    final String remoteAddr = request.getRemoteAddr();

                    if (!isValidHcaptcha(request.getParameter("g-recaptcha-response"), remoteAddr, responsavel)) {
                        throw new UsuarioControllerException("mensagem.erro.captcha.invalido", responsavel);
                    }
                }
            } else{
                final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                if (exigeCaptchaDeficiente) {
                    final String captchaAnswer = request.getParameter("captcha");
                    final String captchaCode   = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                    if ((captchaCode == null) || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                        throw new UsuarioControllerException("mensagem.erro.captcha.invalido", responsavel);
                    }
                    session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                }
            }

            if ((usuarioList == null) || usuarioList.isEmpty()) {
                //DESENV-7733: Por causa de sistemas em que a mensagem de login inválido está configurado como mensagem direta em parâmetro de sistema
                //             não há como criar exceção com construtor passando chave do ApplicationResources
                throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
            }

            // Verifica a licença do eConsig
            final String licenca = (String) usuarioList.get(0).getAttribute(Columns.CSE_LICENCA);
            final String publicKeyCentralizador = (String) usuarioList.get(0).getAttribute(Columns.CSE_RSA_PUBLIC_KEY_CENTRALIZADOR);
            final String modulusCentralizador = (String) usuarioList.get(0).getAttribute(Columns.CSE_RSA_MODULUS_CENTRALIZADOR);
            if (UsuarioHelper.isLicencaExpirada(licenca, publicKeyCentralizador, modulusCentralizador)) {
                // Dá mensagem de erro genérica, com código de erro.
                throw new UsuarioControllerException("mensagem.licencaSistemaInvalida", responsavel);
            }
            // Fim da verificação da licença

            final AcessoSistema usuAcesso = new AcessoSistema(null, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));

            // remove os usuários excluídos. Se não sobrar nenhum usuário, retornar usuário ou senha inválidos.
            // Implementa stu_codigo = usuario.getAttribute(Columns.USU_STU_CODIGO) != null ? usuario.getAttribute(Columns.USU_STU_CODIGO).toString() : CodedValues.STU_ATIVO;
            usuarioList = usuarioList.stream().filter(usuario -> (TextHelper.isNull(usuario.getAttribute(Columns.USU_STU_CODIGO)) ||
                    !CodedValues.STU_EXCLUIDO.equals(usuario.getAttribute(Columns.USU_STU_CODIGO)))).collect(Collectors.toList());
            if ((usuarioList == null) || usuarioList.isEmpty()) {
                throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
            }

            // Decriptografa a senha informada
            final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
            String senhaAberta = null;
            if (!TextHelper.isNull(senhaCriptografada) || TextHelper.isNull(ssoToken)) {
                try {
                    senhaAberta = RSA.decrypt(senhaCriptografada, keyPair.getPrivate());
                    if (senhaAberta == null) {
                        throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                    }
                } catch (final BadPaddingException e) {
                    // Corresponde a tentativa de decriptografia com chave errada. A sessão pode ter expirado. Tentar novamente.
                    throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                }
            }

            final List<UsuarioTransferObject> usuTransFerList = usuarioList.stream().map(usu -> {final UsuarioTransferObject tmp = new UsuarioTransferObject();
            tmp.setAtributos(usu.getAtributos());
            return tmp;
            }).collect(Collectors.toList());


            List<TransferObject> usuariosAutenticados = null;
            try {
                usuariosAutenticados = UsuarioHelper.autenticarUsuarios(senhaAberta, usuTransFerList, ssoToken, usuAcesso);

                if ((usuariosAutenticados == null) || usuariosAutenticados.isEmpty()) {
                    throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                }
                if (!TextHelper.isNull(ssoToken) && TextHelper.isNull(usuAcesso.getSsoToken())) {
                    usuAcesso.setSsoToken(new SSOToken(ssoToken));
                }
            } catch (final ViewHelperException vex) {
                if ("mensagem.usuarioSenhaInvalidos".equals(vex.getMessageKey())) {
                    throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                } else {
                    throw new UsuarioControllerException(vex);
                }
            }

            for (final TransferObject usu: usuarioList) {
                JspHelper.limpaCacheTentativasLogin(usu.getAttribute(Columns.USU_CODIGO).toString());
            }

            if (usuariosAutenticados.size() > 1) {
                // Armazena na sessão o objeto AcessoSistema para este usuário parcial. Será completado ao ser escolhido o exato usuáro
                // com o qual se quer logar na próxima página
                session.setAttribute(AcessoSistema.SESSION_ATTR_NAME, usuAcesso);
                model.addAttribute("usuarioList", usuariosAutenticados);
                model.addAttribute("papeis", usuarioController.listarPapeis(usuAcesso));

                SynchronizerToken.saveToken(request);
                return viewRedirect("jsp/autenticarUsuario/selecionarUsuarioAutenticado",  request, session, model, responsavel);
            } else {
                final TransferObject usuario = !usuariosAutenticados.isEmpty() ? usuariosAutenticados.get(0) : usuarioList.get(0);

                return finalizarAutenticacao(request, response, session, model, usuAcesso, usuario);
            }
        } catch (final Exception ex) {
            return trataExcecao(request, response, session, model, responsavel, usuId, ex);
        }
    }

    private boolean verificarUsuarioPodeAutoDesbloquear(List<TransferObject> usuarioList, AcessoSistema responsavel) {

        boolean usuarioPodeAutoDesbloquear = false;
        
        for (TransferObject usuario : usuarioList) {
            
            TipoEntidadeVO resultadoTipoEntidadeVO = determinarTipoEntidade(usuario);
            if (usuarioController.usuarioPossuiPermissaoAutoDesbloqueio(usuario, resultadoTipoEntidadeVO.getTipoEntidade(), responsavel)) {
                usuarioPodeAutoDesbloquear = true;
            } 
        }

        return usuarioPodeAutoDesbloquear;
        
    }

    @RequestMapping(value = { "/v3/autenticarUsuario" }, params = { "acao=finalizarAutenticacao" })
    public String finalizarAutenticacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String usuCodigo = JspHelper.verificaVarQryStr(request, "usuCodigo");
        final AcessoSistema usuAcesso = (AcessoSistema) session.getAttribute(AcessoSistema.SESSION_ATTR_NAME);

        final List<TransferObject> usuarioList = (List<TransferObject>) session.getAttribute("usuarioList");
        final List<TransferObject> usuSelecionadoList = usuarioList.stream().filter(usu -> usu.getAttribute(Columns.USU_CODIGO).equals(usuCodigo)).collect(Collectors.toList());
        session.removeAttribute("usuarioList");

        if ((usuSelecionadoList == null) || usuSelecionadoList.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, LoginHelper.getMensagemErroLogin());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            return finalizarAutenticacao(request, response, session, model, usuAcesso, usuSelecionadoList.get(0));
        } catch (final Exception e) {
            return trataExcecao(request, response, session, model, responsavel, (String) usuSelecionadoList.get(0).getAttribute(Columns.USU_LOGIN), e);
        }
    }

    private String finalizarAutenticacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, AcessoSistema usuAcesso, TransferObject usuario) throws ConsignanteControllerException, UsuarioControllerException, ConsignatariaControllerException, ViewHelperException, MenuControllerException, LogControllerException, MensagemControllerException, ParametroControllerException, FindException, CorrespondenteControllerException {
        
        String tipo = "";
        String entidade = "";
        
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        // DESENV-20890 : seta no responsavel pois é esta variável que chega até o método de validação de ip.
        responsavel.setNavegadorExclusivo(JspHelper.getNavegadorExclusivo(request));
        final String msgErroLoginInvalido = LoginHelper.getMensagemErroLogin();
        // Obtém os dados do usuário
        final String usu_codigo = usuario.getAttribute(Columns.USU_CODIGO).toString();
        final String stu_codigo = usuario.getAttribute(Columns.USU_STU_CODIGO) != null ? usuario.getAttribute(Columns.USU_STU_CODIGO).toString() : CodedValues.STU_ATIVO;
        final String usu_ip_acesso = (usuario.getAttribute(Columns.USU_IP_ACESSO) != null ? usuario.getAttribute(Columns.USU_IP_ACESSO).toString() : "");
        final String usu_ddns_acesso = (usuario.getAttribute(Columns.USU_DDNS_ACESSO) != null ? usuario.getAttribute(Columns.USU_DDNS_ACESSO).toString() : "");
        final String usu_cpf = (usuario.getAttribute(Columns.USU_CPF) != null ? usuario.getAttribute(Columns.USU_CPF).toString() : "");
        final String usu_email = (usuario.getAttribute(Columns.USU_EMAIL) != null ? usuario.getAttribute(Columns.USU_EMAIL).toString() : "");
        final String usu_chave_validacao_totp = !TextHelper.isNull(usuario.getAttribute(Columns.USU_CHAVE_VALIDACAO_TOTP)) ? usuario.getAttribute(Columns.USU_CHAVE_VALIDACAO_TOTP).toString() : null;
        final String usu_permite_validacao_totp = !TextHelper.isNull(usuario.getAttribute(Columns.USU_PERMITE_VALIDACAO_TOTP)) ? usuario.getAttribute(Columns.USU_PERMITE_VALIDACAO_TOTP).toString() : null;
        final String usu_operacoes_validacao_totp = usuario.getAttribute(Columns.USU_OPERACOES_VALIDACAO_TOTP).toString();
        final String usu_data_valicacao_email = !TextHelper.isNull(usuario.getAttribute(Columns.USU_DATA_VALIDACAO_EMAIL)) ? usuario.getAttribute(Columns.USU_DATA_VALIDACAO_EMAIL).toString() : "";
        final String usu_per_descricao = usuario.getAttribute(Columns.PER_DESCRICAO) != null ? usuario.getAttribute(Columns.PER_DESCRICAO).toString() : ApplicationResourcesHelper.getMessage("rotulo.usuario.perfil.personalizado", responsavel);

        // Verifica se o sistema não está bloqueado
        final Short status = sistemaController.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
        final boolean indisponivel = status.equals(CodedValues.STS_INDISP);
        if (indisponivel && !usuarioController.usuarioTemPermissao(usu_codigo, CodedValues.FUN_EFETUAR_LOGIN_SISTEMA_BLOQUEADO, null, responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, LoginHelper.getMensagemSistemaIndisponivel());
            model.addAttribute("tipo", "indisponivel");
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // se sistema está configurado para bloquear automaticamente usuário na sua próxima autenticação, faz a verficação de bloqueio.
        boolean bloqueadoPorInatividade = false;
        if (ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_USU_INATIVIDADE_PROXIMA_AUTENTICACAO, responsavel)) {
            bloqueadoPorInatividade = UsuarioHelper.bloqueioAutomaticoPorInatividade(usu_codigo, responsavel);
        }

        if (bloqueadoPorInatividade || CodedValues.STU_CODIGOS_INATIVOS.contains(stu_codigo)) {
            model.addAttribute("usuarioBloqueado", "true");

            if (CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE.equals(stu_codigo)) {
                boolean redirectAutoDesbloqueio = false;

                if (ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) &&
                        (!TextHelper.isNull(usuario.getAttribute(Columns.UCE_CSE_CODIGO)) || !TextHelper.isNull(usuario.getAttribute(Columns.UOR_ORG_CODIGO)))) {
                    redirectAutoDesbloqueio = true;
                } else if (ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) &&
                        (!TextHelper.isNull(usuario.getAttribute(Columns.UCA_CSA_CODIGO)) || !TextHelper.isNull(usuario.getAttribute(Columns.UCO_COR_CODIGO)))) {
                    if (!TextHelper.isNull(usuario.getAttribute(Columns.UCA_CSA_CODIGO))) {
                        final String tpaAutoDesbloqueio = parametroController.getParamCsa((String) usuario.getAttribute(Columns.UCA_CSA_CODIGO), CodedValues.TPA_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, responsavel);
                        Perfil perfil = null;
                        if (!TextHelper.isNull(tpaAutoDesbloqueio) && CodedValues.TPA_SIM.equals(tpaAutoDesbloqueio)) {
                            final String perCodigo = usuario.getAttribute(Columns.UPE_PER_CODIGO) != null ? (String) usuario.getAttribute(Columns.UPE_PER_CODIGO) : null;
                            if (perCodigo != null) {
                                perfil = usuarioController.findPerfil(perCodigo, responsavel);
                                if ((perfil != null) && CodedValues.TPA_SIM.equals(perfil.getPerAutoDesbloqueio())) {
                                    redirectAutoDesbloqueio = true;
                                }
                            }
                        } else if (TextHelper.isNull(tpaAutoDesbloqueio) || CodedValues.TPA_NAO.equals(tpaAutoDesbloqueio)) {
                            redirectAutoDesbloqueio = true;
                        }
                    } else if (!TextHelper.isNull(usuario.getAttribute(Columns.UCO_COR_CODIGO))) {
                        final Correspondente cor = correspondenteController.findCorrespondenteByPrimaryKey((String) usuario.getAttribute(Columns.UCO_COR_CODIGO), responsavel);
                        final String tpaAutoDesbloqueio = parametroController.getParamCsa(cor.getCsaCodigo(), CodedValues.TPA_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, responsavel);
                        Perfil perfil = null;
                        if (!TextHelper.isNull(tpaAutoDesbloqueio) && CodedValues.TPA_SIM.equals(tpaAutoDesbloqueio)) {
                            final String perCodigo = usuario.getAttribute(Columns.UPE_PER_CODIGO) != null ? (String) usuario.getAttribute(Columns.UPE_PER_CODIGO) : null;
                            if (perCodigo != null) {
                                perfil = usuarioController.findPerfil((String) usuario.getAttribute(Columns.UPE_PER_CODIGO), responsavel);
                                if ((perfil != null) && CodedValues.TPA_SIM.equals(perfil.getPerAutoDesbloqueio())) {
                                    redirectAutoDesbloqueio = true;
                                }
                            }
                        } else if (TextHelper.isNull(tpaAutoDesbloqueio) || CodedValues.TPA_NAO.equals(tpaAutoDesbloqueio)) {
                            redirectAutoDesbloqueio = true;
                        }
                    }
                } else if (ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_SUP, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) &&
                        (!TextHelper.isNull(usuario.getAttribute(Columns.USP_CSE_CODIGO)))) {
                    redirectAutoDesbloqueio = true;
                }

                if (redirectAutoDesbloqueio) {
                    model.addAttribute("redirectAutoDesbloqueio", true);
                    return iniciar(request, response, session, model);
                }
            }

            throw new UsuarioControllerException("mensagem.usuarioBloqueado", responsavel);
        }

        usuAcesso.setUsuCodigo(usu_codigo);
        usuAcesso.setUsuChaveValidacaoTotp(usu_chave_validacao_totp);
        usuAcesso.setUsuPermiteValidacaoTotp(usu_permite_validacao_totp);
        usuAcesso.setUsuOperacoesValidacaoTotp(OperacaoValidacaoTotpEnum.get(usu_operacoes_validacao_totp));

        TipoEntidadeVO resultadoTipoEntidadeVO = determinarTipoEntidade(usuario);

        String cor_codigo = resultadoTipoEntidadeVO.getCorCodigo();
        String cse_codigo = resultadoTipoEntidadeVO.getCseCodigo();
        String csa_codigo = resultadoTipoEntidadeVO.getCsaCodigo();
        String org_codigo = resultadoTipoEntidadeVO.getOrgCodigo();
        tipo = resultadoTipoEntidadeVO.getTipoEntidade();
        entidade = resultadoTipoEntidadeVO.getCodigoEntidade();

         // Seta as informações sobre a entidade do usuário no AcessoSistema
        usuAcesso.setTipoEntidade(tipo);
        usuAcesso.setCodigoEntidade(entidade);

        //Seta as informações sobre o perfil do usuário no AcessoSistema.
        usuAcesso.setPerDescricao(usu_per_descricao);
        
        if (usuAcesso.isSer()) {
            throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
        }

        // Verifica se o sistema permite o login de usuário correspondente vinculado a uma entidade bloqueada
        final boolean permiteLoginUsuCorEntidadeBloq = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_LOGIN_USU_COR_ENTIDADE_BLOQ, responsavel);
        // Verifica se a consignatária do correspondente não está bloqueada
        if (!permiteLoginUsuCorEntidadeBloq && usuAcesso.isCor()) {
            final CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(cor_codigo, responsavel);
            final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(cor.getCsaCodigo(), responsavel);
            if (!csa.getCsaAtivo().equals(CodedValues.STS_ATIVO)) {
                throw new UsuarioControllerException("mensagem.informacao.consignataria.bloqueada", responsavel);
            }
            if (!cor.getCorAtivo().equals(CodedValues.STS_ATIVO)) {
                throw new UsuarioControllerException("mensagem.informacao.correspondente.bloqueado", responsavel);
            }
        }

        // Verifica se o perfil do usuário não está bloqueado
        final String perCodigo = (String) usuario.getAttribute(Columns.UPE_PER_CODIGO);
        if ((perCodigo != null) && !"".equals(perCodigo)) {
            final Short upeStatus = usuarioController.getStatusPerfil(tipo, entidade, perCodigo, responsavel);
            if ((upeStatus == null) || !upeStatus.equals(CodedValues.STS_ATIVO)) {
                model.addAttribute("usuarioBloqueado", "true");
                throw new UsuarioControllerException("mensagem.usuarioBloqueado", responsavel);
            }
        }

        // Verifica obrigatoriedade e validade do IP/DDNS de acesso
        UsuarioHelper.verificarIpDDNSAcesso(tipo, entidade, JspHelper.getRemoteAddr(request), usu_ip_acesso, usu_ddns_acesso, usu_codigo, responsavel);

        // Verifica se o usuário é obrigado a usar o centralizador para fazer login
        if ((usuario.getAttribute(Columns.USU_CENTRALIZADOR) != null)
                && CodedValues.TPC_SIM.equals(usuario.getAttribute(Columns.USU_CENTRALIZADOR).toString())
                && (session.getAttribute(CodedNames.ATTR_SESSION_CENTRALIZADOR) == null)) {
            throw new UsuarioControllerException("mensagem.informacao.usuario.deve.acessar.via.centralizador", responsavel);
        }

        // Verifica obrigatoriedade de CPF para acesso ao sistema
        UsuarioHelper.verificarCpfUsuario(tipo, usu_cpf, responsavel);

        // Verifica obrigatoriedade de email para acesso ao sistema
        UsuarioHelper.verificarEmailUsuario(tipo, usu_email, responsavel);

        // Verifica obrigatoriedade de validação de email para acesso ao sistema
        final boolean emailJaValidado = UsuarioHelper.usuarioValidouEmail(tipo, usu_data_valicacao_email, responsavel);

        if (!emailJaValidado) {
            session.setAttribute(AcessoSistema.SESSION_ATTR_NAME, usuAcesso);

            final boolean editarEmail = (ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_EDICAO_EMAIL, CodedValues.TPC_SIM, responsavel) && !TextHelper.isNull(usu_email));

            model.addAttribute("usuCodigo", usu_codigo);
            model.addAttribute("usuEmail", usu_email);
            model.addAttribute("editarEmail", editarEmail);
            model.addAttribute("msgValidacao", ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.validacao.email", responsavel));
            model.addAttribute("retornoErro", Boolean.FALSE);
            return viewRedirect("jsp/validarEmailUsuario/validarEmailUsuario", request, session, model, responsavel);
        }

        //recupera se o login veio do fluxo da DESENV-13252
        final Boolean termoUso = (Boolean) session.getAttribute("termo_usu");

        // Cria a sessão do usuário
        session.removeAttribute("msg");

        // Invalida e cria nova sessão para mudar o sessionId evitando ataque
        // de session fixation.
        session.invalidate();
        session = request.getSession(true);

        if (termoUso != null) {
            session.setAttribute("termo_usu", Boolean.TRUE);
        }

        if (!TextHelper.isNull(request.getParameter("urlCentralizador"))) {
            final String urlCentral = request.getParameter("urlCentralizador");
            final String attrSessionAcessoUrl = request.getParameter("attrSessionAcessoUrl");
            final String parametrosCentral = request.getParameter("parametrosCentral");

            session.setAttribute("attrSessionAcessoUrl", attrSessionAcessoUrl);
            session.setAttribute("urlCentralizador", urlCentral);
            session.setAttribute("parametrosCentral", parametrosCentral);
        }

        if (!TextHelper.isNull(request.getParameter(URL_CENTRALIZADOR_ACESSO_PARAM)) || !TextHelper.isNull(request.getAttribute(URL_CENTRALIZADOR_ACESSO_PARAM))) {
            final String urlCentralizadorAcesso = !TextHelper.isNull(request.getParameter(URL_CENTRALIZADOR_ACESSO_PARAM)) ? request.getParameter(URL_CENTRALIZADOR_ACESSO_PARAM) : (String) request.getAttribute(URL_CENTRALIZADOR_ACESSO_PARAM);

            session.setAttribute(URL_CENTRALIZADOR_ACESSO_PARAM, urlCentralizadorAcesso);
            final Cookie cookie = new Cookie(URL_CENTRALIZADOR_ACESSO_PARAM, urlCentralizadorAcesso);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
        } else {
            final Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (final Cookie cookie : cookies) {
                    if (URL_CENTRALIZADOR_ACESSO_PARAM.equals(cookie.getName())) {
                        // Remove o cookie caso ele tenha sobrado de uma sessão anterior
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
            }
        }

        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_TUTORIAL_PRIMEIRO_ACESSO, CodedValues.TPC_SIM, responsavel)) {
            final List<String> tutorialList = FileHelper.getFilesInDir(ParamSist.getDiretorioRaizArquivos() + "/imagem/tutorial");
            if (!tutorialList.isEmpty()) {
                Collections.sort(tutorialList);
                session.setAttribute("tutorialList", tutorialList);

                if (TextHelper.isNull(usuario.getAttribute(Columns.USU_DATA_ULT_ACESSO))) {
                    session.setAttribute("tutorialPrimeiroAcesso", Boolean.TRUE);
                }
            }
        }

        // Busca parametro de timeout dependendo do tipo do usuario
        int timeout = CodedValues.TEMPO_DEFAULT_EXPIRACAO_SESSAO;
        final ParamSist paramSist = ParamSist.getInstance();

        try {
            if (usuAcesso.isCseSupOrg()) {
                final Object objTimeout = paramSist.getParam(CodedValues.TPC_TEMPO_EXPIRACAO_SESSAO_CSE_ORG_SUP, responsavel);
                timeout = objTimeout != null ? Integer.parseInt(objTimeout.toString()) : CodedValues.TEMPO_DEFAULT_EXPIRACAO_SESSAO;
            } else if (usuAcesso.isCsaCor()) {
                final Object objTimeout = paramSist.getParam(CodedValues.TPC_TEMPO_EXPIRACAO_SESSAO_CSA_COR, responsavel);
                timeout = objTimeout != null ? Integer.parseInt(objTimeout.toString()) : CodedValues.TEMPO_DEFAULT_EXPIRACAO_SESSAO;
            }
        } catch (final NumberFormatException ex) {
            // Caso o parametro esta preenchido errado usa o valor default
            timeout = CodedValues.TEMPO_DEFAULT_EXPIRACAO_SESSAO;
        }

        // Qualquer valor igual ou menor que zero será considerado 20 minutos
        // Tempo máximo de timeout é de 20 minutos
        if ((timeout < 1) || (timeout > CodedValues.TEMPO_MAXIMO_EXPIRACAO_SESSAO)) {
            timeout = CodedValues.TEMPO_DEFAULT_EXPIRACAO_SESSAO;
        }

        // Tempo máximo inativo é em segundos
        session.setMaxInactiveInterval(timeout * 60);

        usuAcesso.setUsuNome(usuario.getAttribute(Columns.USU_NOME).toString());
        usuAcesso.setUsuLogin(usuario.getAttribute(Columns.USU_LOGIN).toString());
        usuAcesso.setUsuEmail((String) usuario.getAttribute(Columns.USU_EMAIL));

        // Verifica se acesso é via centralizador
        final String usuCentralizador = !TextHelper.isNull(request.getParameter(USU_CENTRALIZADOR_PARAM)) ? request.getParameter(USU_CENTRALIZADOR_PARAM) : (String) request.getAttribute(USU_CENTRALIZADOR_PARAM);
        if ("S".equals(usuCentralizador)) {
            usuAcesso.setUsuCentralizador(usuCentralizador);
        }
        
        // Verifica se acesso é via sso
        final String usuAutenticaSso = !TextHelper.isNull(usuario.getAttribute(Columns.USU_AUTENTICA_SSO)) ? (String) usuario.getAttribute(Columns.USU_AUTENTICA_SSO) : "";
        if ("S".equals(usuAutenticaSso)) {
            usuAcesso.setUsuAutenticaSso(usuAutenticaSso);
        }

        // Seta se é deficiente visual
        final String loginDefVisual = (usuario.getAttribute(Columns.USU_DEFICIENTE_VISUAL) != null ? usuario.getAttribute(Columns.USU_DEFICIENTE_VISUAL).toString() : "N");
        usuAcesso.setDeficienteVisual("S".equals(loginDefVisual));

        session.removeAttribute("AlterarSenha");
        final String expirou = usuario.getAttribute("EXPIROU") != null ? usuario.getAttribute("EXPIROU").toString() : "1";

        if ("1".equals(expirou)) {
            session.setAttribute("AlterarSenha", "1");
        }

        // Verifica se o usuário precisa aceitar o termo de uso.
        session.removeAttribute("AceitarTermoDeUso");
        String chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_TERMO_DE_USO_CSE;
        if (usuAcesso.isOrg()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_TERMO_DE_USO_ORG;
        } else if (usuAcesso.isSer()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_TERMO_DE_USO_SER;
        } else if (usuAcesso.isCsa()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_TERMO_DE_USO_CSA;
        } else if (usuAcesso.isCor()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_TERMO_DE_USO_COR;
        } else if (usuAcesso.isSup()) {
            chaveAceitacaoTermoDeUso = CodedValues.TPC_DATA_TERMO_DE_USO_SUP;
        }
        final Object paramAceitacaoTermoDeUso = ParamSist.getInstance().getParam(chaveAceitacaoTermoDeUso, usuAcesso);
        if (!TextHelper.isNull(paramAceitacaoTermoDeUso)) {
            try {
                final java.util.Date dataTermoDeUso = DateHelper.parse(paramAceitacaoTermoDeUso.toString(), "yyyy-MM-dd");
                final List<String> tocCodigos = new ArrayList<>();
                tocCodigos.add(CodedValues.TOC_ACEITACAO_TERMO_DE_USO);
                final CustomTransferObject filtro = new CustomTransferObject();
                filtro.setAttribute(Columns.OUS_USU_CODIGO, usuAcesso.getUsuCodigo());
                filtro.setAttribute("tocCodigos", tocCodigos);
                final List<TransferObject> ocorrencias = usuarioController.lstOcorrenciaUsuario(filtro, -1, -1, usuAcesso);
                if (ocorrencias.size() == 0) {
                    session.setAttribute("AceitarTermoDeUso", "1");
                } else {
                    final java.util.Date dataUltimaAceitacao = (java.util.Date) ocorrencias.get(0).getAttribute(Columns.OUS_DATA);
                    if ((dataUltimaAceitacao.compareTo(dataTermoDeUso) <= 0) || ((session.getAttribute("termo_usu") != null) && (Boolean) session.getAttribute("termo_usu"))) {
                        session.setAttribute("AceitarTermoDeUso", "1");
                    }
                }
            } catch (final java.text.ParseException e) {
                // Formato do parâmetro inválido
                LOG.error(e.getMessage(), e);
            }
        }

        // Verifica se o usuário precisa aceitar a política de privacidade.
        session.removeAttribute("AceitarPoliticaPrivacidade");
        String chaveAceitacaoPoliticaPrivacidade = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_CSE;
        if (usuAcesso.isOrg()) {
            chaveAceitacaoPoliticaPrivacidade = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_ORG;
        } else if (usuAcesso.isSer()) {
            chaveAceitacaoPoliticaPrivacidade = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_SER;
        } else if (usuAcesso.isCsa()) {
            chaveAceitacaoPoliticaPrivacidade = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_CSA;
        } else if (usuAcesso.isCor()) {
            chaveAceitacaoPoliticaPrivacidade = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_COR;
        } else if (usuAcesso.isSup()) {
            chaveAceitacaoPoliticaPrivacidade = CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_SUP;
        }
        final Object paramAceitacaoPoliticaPrivacidade = ParamSist.getInstance().getParam(chaveAceitacaoPoliticaPrivacidade, usuAcesso);
        if (!TextHelper.isNull(paramAceitacaoPoliticaPrivacidade)) {
            try {
                final java.util.Date dataPoliticaPrivacidade = DateHelper.parse(paramAceitacaoPoliticaPrivacidade.toString(), "yyyy-MM-dd");
                final List<String> tocCodigos = new ArrayList<>();
                tocCodigos.add(CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_SISTEMA);
                final CustomTransferObject filtro = new CustomTransferObject();
                filtro.setAttribute(Columns.OUS_USU_CODIGO, usuAcesso.getUsuCodigo());
                filtro.setAttribute("tocCodigos", tocCodigos);
                final List<TransferObject> ocorrencias = usuarioController.lstOcorrenciaUsuario(filtro, -1, -1, usuAcesso);
                if (ocorrencias.size() == 0) {
                    session.setAttribute("AceitarPoliticaPrivacidade", "1");
                } else {
                    final java.util.Date dataUltimaAceitacao = (java.util.Date) ocorrencias.get(0).getAttribute(Columns.OUS_DATA);
                    if (dataUltimaAceitacao.compareTo(dataPoliticaPrivacidade) <= 0) {
                        session.setAttribute("AceitarPoliticaPrivacidade", "1");
                    }
                }
            } catch (final java.text.ParseException e) {
                // Formato do parâmetro inválido
                LOG.error(e.getMessage(), e);
            }
        }

        usuAcesso.setQtdConsultasMargem((Integer) usuario.getAttribute(Columns.USU_QTD_CONSULTAS_MARGEM));
        if (usuAcesso.isCseSup()) {
            final ConsignanteTransferObject cse = consignanteController.findConsignante(cse_codigo, usuAcesso);

            usuAcesso.setNomeEntidade(cse.getCseNome());
            usuAcesso.setIdEntidade(cse.getCseIdentificador());

        } else if (usuAcesso.isCsa()) {
            final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csa_codigo, usuAcesso);

            String csa_nome = csa.getCsaNomeAbreviado();
            if ((csa_nome == null) || csa_nome.isBlank()) {
                csa_nome = csa.getCsaNome();
            }
            usuAcesso.setNomeEntidade(csa_nome);
            usuAcesso.setIdEntidade(csa.getCsaIdentificador());
            usuAcesso.setNcaCodigo(csa.getCsaNcaNatureza());
        } else if (usuAcesso.isCor()) {
            final CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(cor_codigo, usuAcesso);
            final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(cor.getCsaCodigo(), usuAcesso);

            String csa_nome = csa.getCsaNomeAbreviado();
            if ((csa_nome == null) || csa_nome.isBlank()) {
                csa_nome = csa.getCsaNome();
            }

            usuAcesso.setNomeEntidade(cor.getCorNome());
            usuAcesso.setIdEntidade(cor.getCorIdentificador());
            usuAcesso.setNomeEntidadePai(csa_nome);
            usuAcesso.setCodigoEntidadePai(csa.getCsaCodigo());
            usuAcesso.setNcaCodigo(csa.getCsaNcaNatureza());
        } else if (usuAcesso.isOrg()) {

            final OrgaoTransferObject org = consignanteController.findOrgao(org_codigo, usuAcesso);
            final EstabelecimentoTransferObject est = consignanteController.findEstabelecimento(org.getEstCodigo(), usuAcesso);

            usuAcesso.setNomeEntidade(org.getOrgNome());
            usuAcesso.setIdEntidade(org.getOrgIdentificador());
            usuAcesso.setNomeEntidadePai(est.getEstNome());
            usuAcesso.setCodigoEntidadePai(est.getEstCodigo());

        } else {
            return trataExcecao(request, response, session, model, responsavel, (String) usuario.getAttribute(Columns.USU_LOGIN), UsuarioControllerException.byMessage(msgErroLoginInvalido));
        }
        // Busca as permissões do usuário
        usuAcesso.setPermissoes(usuarioController.selectFuncoes(usu_codigo, entidade, tipo, usuAcesso));
        usuAcesso.setPermissaoUnidadesEdt(usuarioController.unidadesPermissaoEdtUsuario(usu_codigo, responsavel));

        // Verifica a necessidade de atualização cadastral para o papel CSE ou CSA
        session.removeAttribute("exigeAtualizacaoCadastral");
        boolean exigeAtualizacaoCadastral = false;
        final boolean exigeAtualizacaoCadastralCsaCnpj = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ATUALIZACAO_CADASTRAL_CSA_CNPJ, CodedValues.TPC_SIM, responsavel);

       if (!TextHelper.isNull(tipo) && AcessoSistema.ENTIDADE_CSE.equals(tipo) && usuAcesso.temPermissao(CodedValues.FUN_EDT_CONSIGNANTE) && TextHelper.isNum(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ATUALIZACAO_CADASTRAL_CSE, responsavel))) {
           final ConsignanteTransferObject cse = consignanteController.findConsignante(entidade, responsavel);
           final Date dataAtualizacaoCadatral = cse.getCseDataAtualizacaoCadastral();
           final int cicloDiasAtualizacao = Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ATUALIZACAO_CADASTRAL_CSE, responsavel).toString());

            if ((cicloDiasAtualizacao > 0) && (TextHelper.isNull(dataAtualizacaoCadatral) || (DateHelper.dayDiff(dataAtualizacaoCadatral) > cicloDiasAtualizacao))) {
                exigeAtualizacaoCadastral = true;
            }
        }

        if (!TextHelper.isNull(tipo) && AcessoSistema.ENTIDADE_CSA.equals(tipo)
                && usuAcesso.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIA)
                && (TextHelper.isNum(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ATUALIZACAO_CADASTRAL_CSA, responsavel))
                || exigeAtualizacaoCadastralCsaCnpj)) {

           final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(entidade, responsavel);
           final Date dataAtualizacaoCadatral = csa.getCsaDataAtualizacaoCadastral();

            if (exigeAtualizacaoCadastralCsaCnpj && !TextHelper.isNum(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ATUALIZACAO_CADASTRAL_CSA, responsavel))) {
                session.setAttribute(AcessoSistema.SESSION_ATTR_NAME, usuAcesso);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.atualizar.cadastro.erro.configuracao", responsavel));
                model.addAttribute("retornoErro", Boolean.TRUE);
                model.addAttribute("exigeAtualizacaoCadastralCsaCnpj", Boolean.FALSE);
                return viewRedirect("jsp/atualizarCadastro/atualizarCadastro", request, session, model, responsavel);
            }

            final int cicloDiasAtualizacao = Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ATUALIZACAO_CADASTRAL_CSA, responsavel).toString());

            if ((cicloDiasAtualizacao > 0) && (TextHelper.isNull(dataAtualizacaoCadatral) || (DateHelper.dayDiff(dataAtualizacaoCadatral) > cicloDiasAtualizacao))) {
                exigeAtualizacaoCadastral = true;
            }
        }

       if (exigeAtualizacaoCadastral) {
           session.setAttribute("exigeAtualizacaoCadastral", "1");
       }
       if (usuAcesso.isValidaTotp(false)) {
           session.setAttribute("exigeValidacaoTotp", "1");
       }

        final List<MenuTO> mnuLst = menuController.obterMenu(usuAcesso);
        verificarAcessoMenuDropDown(session, usuAcesso, mnuLst);

        usuAcesso.setMenu(mnuLst);

        // Seta data de última data de acesso ao sistema
        usuarioController.alteraDataUltimoAcessoSistema(usuAcesso);

        // Valida acesso simultâneo
        sessionManagment.validateNewSession(usu_codigo, session.getId());

        // Grava log de login sucesso
        final LogDelegate log = new LogDelegate(usuAcesso, Log.SISTEMA, Log.LOGIN, Log.LOG_LOGIN_SUCESSO);
        log.add("USER-AGENT: " + request.getHeader("user-agent"));
        log.write();

        // Armazena na sessão o objeto AcessoSistema para este usuário
        // com as informações sobre o usuário (usu_codigo, ip, tipo, entidade)
        session.setAttribute(AcessoSistema.SESSION_ATTR_NAME, usuAcesso);

        // Verifica se usuario deve confirmar leitura de alguma mensagem
        session.removeAttribute("mensagem_sem_leitura");
        final CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.USU_DATA_CAD, usuario.getAttribute(Columns.USU_DATA_CAD));
        session.setAttribute("usu_data_cad", usuario.getAttribute(Columns.USU_DATA_CAD));

        final Integer semLeitura = mensagemController.countMensagemUsuarioSemLeitura(criterio, usuAcesso);
        if (semLeitura.intValue() > 0) {
            session.setAttribute("mensagem_sem_leitura", semLeitura);
        }

        // Verifica se usuario utiliza certificado digital
        session.removeAttribute("valida_certificado_digital");
        if (UsuarioHelper.isUsuarioCertificadoDigital((String) usuario.getAttribute(Columns.USU_LOGIN), (String) usuario.getAttribute(Columns.USU_EXIGE_CERTIFICADO), usuAcesso.getTipoEntidade(), usuAcesso.getCodigoEntidade(), usuAcesso)) {
            session.setAttribute("valida_certificado_digital", "true");
        }

        //Para validar se não houve tentativa de copiar o id da sessão e logar em outro navegador, previsamos armazenar na sessão o User-Agent
        session.setAttribute("userAgentLogin", request.getHeader("user-agent"));

        // seta cookie indicando que sessão inválida deve direcionar para tela de login não servidor
        response.addCookie(new Cookie("LOGIN", ""));

        int tpcDias = 0;
        if (usuAcesso.isCseOrg()) {
            tpcDias = ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_CSE_ORG, 0, usuAcesso);
        } else if (usuAcesso.isCsaCor()) {
            tpcDias = ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_CSA_COR, 0, usuAcesso);
        }

        if (tpcDias > 0) {
            final TransferObject usuarioAutenticaSso = usuAutenticaSso(usuAcesso);
            if (usuarioAutenticaSso.getAttribute(Columns.USU_AUTENTICA_SSO) != null && "S".equals(usuarioAutenticaSso.getAttribute(Columns.USU_AUTENTICA_SSO))) {
                final String data = getDataExpiracao(usuAcesso.getSsoToken());
                final int diff = DateHelper.dateDiff(DateHelper.format(DateHelper.getSystemDate(), "yyyy-MM-dd"), data, "yyyy-MM-dd", null, "DIAS");
                if (diff <= tpcDias) {
                    session.setAttribute(CodedValues.MSG_EXPIRACAO_SENHA, ApplicationResourcesHelper.getMessage("alerta.expiracao.senha.tela", usuAcesso, String.valueOf(diff)));
                }
            } else {
                final int diff = DateHelper.dateDiff(DateHelper.format(DateHelper.getSystemDate(), "yyyy-MM-dd"), usuarioAutenticaSso.getAttribute(Columns.USU_DATA_EXP_SENHA).toString(), "yyyy-MM-dd", null, "DIAS");
                if (diff <= tpcDias) {
                    session.setAttribute(CodedValues.MSG_EXPIRACAO_SENHA, ApplicationResourcesHelper.getMessage("alerta.expiracao.senha.tela", usuAcesso, String.valueOf(diff)));
                }
            }
        }

        // Redireciona para a principal
        return redirecionarPaginaPrincipal();
    }

    @RequestMapping(value = { "/v3/autenticarUsuario" }, params = { "acao=validarTotp" })
    public String validarTotp(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Se não exige validação de TOTP então volta para interface de login
        if (session.getAttribute("exigeValidacaoTotp") == null
                || !"1".equals(session.getAttribute("exigeValidacaoTotp"))
                || !responsavel.isValidaTotp(false)) {
            return redirecionarPaginaLogin();
        }

        final String mensagemTotpCodigoInvalido = ApplicationResourcesHelper.getMessage("mensagem.totp.codigo.invalido", responsavel);

        final String usuChaveValidacaoTotp = responsavel.getUsuChaveValidacaoTotp();
        final String codigoTotpCriptografado = JspHelper.verificaVarQryStr(request, JspHelper.verificaVarQryStr(request, "segundaSenha"));

        if (TextHelper.isNull(codigoTotpCriptografado)) {
            // Se o código ainda não foi informado, redireciona à página para informação
            SynchronizerToken.saveToken(request);
            return viewRedirect("jsp/autenticarUsuario/validarTotp", request, session, model, responsavel);
        }

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return redirecionarPaginaLogin();
        }
        SynchronizerToken.saveToken(request);

        Integer qtdTentativasTotp = session.getAttribute("qtdTentativasTotp") != null ? (Integer) session.getAttribute("qtdTentativasTotp") : 1;
        if (qtdTentativasTotp >= 3) {
            // Invalida a sessão do usuário e recomeça o processo
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.totp.codigo.invalido.recomecar", responsavel));

            try {
                // Gerar log de erro de segurança
                final com.zetra.econsig.delegate.LogDelegate log = new com.zetra.econsig.delegate.LogDelegate(responsavel, Log.USUARIO, Log.LOGIN, Log.LOG_ERRO_SEGURANCA);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.erro.tentativa.validacao.totp.excedida", responsavel).toUpperCase());
                log.write();
            } catch (final LogControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            // Redireciona para o login
            return redirecionarPaginaLogin();
        }
        session.setAttribute("qtdTentativasTotp", ++qtdTentativasTotp);

        final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
        final String codigoTotp;
        try {
            codigoTotp = RSA.decrypt(codigoTotpCriptografado, keyPair.getPrivate());
        } catch (final BadPaddingException e) {
            // Corresponde a tentativa de decriptografia com chave errada. A sessão pode ter expirado. Tentar novamente.
            session.setAttribute(CodedValues.MSG_ERRO, mensagemTotpCodigoInvalido);
            return viewRedirect("jsp/autenticarUsuario/validarTotp", request, session, model, responsavel);
        }

        try {
            final GoogleAuthenticatorHelper authenticator = new GoogleAuthenticatorHelper();
            long timeInMilliseconds = 0;
            try {
                final String strTimeInMilliseconds = JspHelper.verificaVarQryStr(request, "timeInMilliseconds");
                timeInMilliseconds = !TextHelper.isNull(strTimeInMilliseconds) ? Long.parseLong(strTimeInMilliseconds) : 0;
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, mensagemTotpCodigoInvalido);
                return viewRedirect("jsp/autenticarUsuario/validarTotp", request, session, model, responsavel);
            }

            // Verificar código de segurança informado
            if (!authenticator.checkCode(usuChaveValidacaoTotp, Long.valueOf(codigoTotp), timeInMilliseconds)) {
                session.setAttribute(CodedValues.MSG_ERRO, mensagemTotpCodigoInvalido);
                return viewRedirect("jsp/autenticarUsuario/validarTotp", request, session, model, responsavel);
            }
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, mensagemTotpCodigoInvalido);
            return viewRedirect("jsp/autenticarUsuario/validarTotp", request, session, model, responsavel);
        }

        session.removeAttribute("qtdTentativasTotp");
        session.removeAttribute("exigeValidacaoTotp");
        return redirecionarPaginaPrincipal();
    }

    private String redirecionarPaginaPrincipal() {
        return "redirect:/v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";
    }

    private String redirecionarPaginaLogin() {
        return "redirect:/v3/autenticarUsuario?t=" + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");
    }

    private String trataExcecao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, AcessoSistema responsavel, String usuLogin, Exception exc) {
        if (exc.getClass().equals(UsuarioControllerException.class)
                || exc.getClass().equals(ViewHelperException.class)) {
            session.setAttribute(CodedValues.MSG_ERRO, exc.getMessage());
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            LOG.error(exc.getMessage(), exc);
        }

        AcessoSistema usuAcesso = JspHelper.getAcessoSistema(request);
        CustomTransferObject usuario = null;
        try {
            if (!TextHelper.isNull(usuLogin)) {
                usuario = usuarioController.findTipoUsuarioByLogin(usuLogin, responsavel);
            }
        } catch (final UsuarioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            LOG.error(e.getMessage(), e);
        }
        if (usuario != null) {
            usuAcesso = new AcessoSistema(usuario.getAttribute(Columns.USU_CODIGO).toString(), JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
        }
        final LogDelegate log = new LogDelegate (usuAcesso, Log.SISTEMA, Log.LOGIN, Log.LOG_LOGIN_ERRO);
        try {
            log.add("LOGIN: " + usuLogin);
            log.add("ERRO: " + session.getAttribute(CodedValues.MSG_ERRO));
            log.add("USER-AGENT: " + request.getHeader("user-agent"));
            log.write();
        } catch (final LogControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            LOG.error(e.getMessage(), e);
        }

        session.removeAttribute(com.zetra.econsig.values.CodedNames.ATTR_SESSION_CENTRALIZADOR);

        return iniciar(request, response, session, model);
    }

    private String getTextoAutenticacaoUsuario(AcessoSistema responsavel) throws ViewHelperException {
        final String autenticacaoUsuario = "autenticacao_usuario.msg";

        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        absolutePath += File.separatorChar + "autenticacao_usuario" + File.separatorChar + autenticacaoUsuario;

        final File arqMensagem = new File(absolutePath);
        if (!arqMensagem.exists()) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.texto.arquivo.autenticacao.usuario.nao.encontrado", responsavel, absolutePath));
            throw new ViewHelperException("mensagem.erro.interno.texto.arquivo.autenticacao.usuario.nao.encontrado", responsavel);
        }

        return FileHelper.readAll(absolutePath);
    }


    private TransferObject usuAutenticaSso(AcessoSistema responsavel) {
        try {
            return usuarioController.findTipoUsuarioByLogin(responsavel.getUsuLogin(), responsavel);
        } catch (final UsuarioControllerException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDataExpiracao(SSOToken ssoToken) {
        try {
            final SSOClient ssoClient = ApplicationContextProvider.getApplicationContext().getBean(SSOClient.class);
            return ssoClient.getDataExpiracao(ssoToken);
        } catch (final SSOException e) {
            throw new RuntimeException(e);
        }
    }

    private TipoEntidadeVO determinarTipoEntidade (TransferObject usuario) {

        String tipo = "";
        String entidade = "";

        String cse_codigo = usuario.getAttribute(Columns.UCE_CSE_CODIGO) != null ? usuario.getAttribute(Columns.UCE_CSE_CODIGO).toString() : "";
        final String csa_codigo = usuario.getAttribute(Columns.UCA_CSA_CODIGO) != null ? usuario.getAttribute(Columns.UCA_CSA_CODIGO).toString() : "";
        final String cor_codigo = usuario.getAttribute(Columns.UCO_COR_CODIGO) != null ? usuario.getAttribute(Columns.UCO_COR_CODIGO).toString() : "";
        final String org_codigo = usuario.getAttribute(Columns.UOR_ORG_CODIGO) != null ? usuario.getAttribute(Columns.UOR_ORG_CODIGO).toString() : "";
        final String ser_codigo = usuario.getAttribute(Columns.USE_SER_CODIGO) != null ? usuario.getAttribute(Columns.USE_SER_CODIGO).toString() : "";
        final String usp_cse_codigo = usuario.getAttribute(Columns.USP_CSE_CODIGO) != null ? usuario.getAttribute(Columns.USP_CSE_CODIGO).toString() : "";

        // Determina o tipo da entidade do usuário
        if (!"".equals(cse_codigo)) {
            tipo = AcessoSistema.ENTIDADE_CSE;
            entidade = cse_codigo;
        } else if (!"".equals(csa_codigo)) {
            tipo = AcessoSistema.ENTIDADE_CSA;
            entidade = csa_codigo;
        } else if (!"".equals(cor_codigo)) {
            tipo = AcessoSistema.ENTIDADE_COR;
            entidade = cor_codigo;
        } else if (!"".equals(org_codigo)) {
            tipo = AcessoSistema.ENTIDADE_ORG;
            entidade = org_codigo;
        } else if (!"".equals(ser_codigo)) {
            tipo = AcessoSistema.ENTIDADE_SER;
            entidade = ser_codigo;
        } else if (!"".equals(usp_cse_codigo)) {
            tipo = AcessoSistema.ENTIDADE_SUP;
            entidade = usp_cse_codigo;
            cse_codigo = usp_cse_codigo;
        }

        TipoEntidadeVO result = new TipoEntidadeVO(tipo, entidade, cse_codigo, csa_codigo, cor_codigo, org_codigo, ser_codigo, usp_cse_codigo);

        return result;

    }

}
