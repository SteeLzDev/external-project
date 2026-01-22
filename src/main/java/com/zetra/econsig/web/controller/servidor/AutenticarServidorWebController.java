package com.zetra.econsig.web.controller.servidor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.zetra.econsig.dto.web.SSOToken;
import com.zetra.econsig.exception.SSOException;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaController;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webclient.sso.SSOClient;
import com.zetra.econsig.webclient.util.RestTemplateFactory;
import org.springframework.http.*;
import java.net.URLDecoder;
import java.security.KeyPair;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.crypto.BadPaddingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.MenuTO;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.SenhaExpiradaException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rede.HttpHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleLogin;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.senhaexterna.SenhaExterna;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.service.menu.MenuController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.MetodoSenhaExternaEnum;
import com.zetra.econsig.values.ParamSenhaExternaEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.controller.ajuda.ChatbotRestController;
import com.zetra.econsig.web.listener.SessionCounterListener;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;
import com.zetra.econsig.webclient.faces.FacesWebServiceClient;
import com.zetra.econsig.webservice.rest.request.UsuarioRestRequest;
import com.zetra.econsig.webservice.rest.service.UsuarioService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Response;
import org.springframework.web.client.RestTemplate;

/**
 * <p>Title: AutenticarServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Autenticar Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Alxandre Gonçalves, Marcelo Fortes, Leonel Martins
 */
@Controller
public class AutenticarServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutenticarServidorWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private MenuController menuController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private MensagemController mensagemController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private UsuarioService usuService;

    @Autowired
    private ArquivoController arquivoController;

    @Autowired
    private SessionCounterListener sessionManagment;

    @Autowired
    public FormularioPesquisaController formularioPesquisaController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", JspHelper.getNomeSistema(responsavel));

        final String acao = JspHelper.verificaVarQryStr(request, "acao");
        final boolean alteracaoLoginSer = acao.equals("iniciarAlteracaoLoginSer") || acao.equals("alterarLoginSer");
        if (alteracaoLoginSer) {
            // DESENV-18024: As configurações abaixo não se aplicam quando está se fazendo alteração
            // de login do servidor, incluvise atrapalham o fluxo de troca quando é usada
            // autenticação externa. Por isso, o fluxo deve ser interrompido.
            return;
        }

        // Parâmetros de configuração de exibição de captcha
        boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN_SER, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean exibeCaptchaDeficiente = false;

        // Parâmetros de configuração de exibição de teclado virtual
        boolean exibeVk = ParamSist.paramEquals(CodedValues.TPC_EXIBE_TCLD_VIRTUAL_LOGIN_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        // Verifica parâmetro que indica a forma do login de usuário servidor
        final boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);

        // Parâmetro para definir se o login é realizado em duas etapas
        final boolean validacaoSeguranca = ParamSist.paramEquals(CodedValues.TPC_VALIDACAO_SEGURANCA_TELA_LOGIN_SER, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        // Parâmetro para definir se o login é realizado com CPF
        final boolean loginComCfp = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_CPF, CodedValues.TPC_SIM, responsavel);

        // Parâmetro para definir se ao autenticar com servidor será exibido apenas EST e ORG ativos
        final boolean exibeEstOrgAtivos = ParamSist.paramEquals(CodedValues.TPC_EXIBE_ORG_EST_ATIVOS_AUTENTICACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        // Parâmetro para definir se omite escolha de órgão/estabelecimento da tela de login de servidor
        final boolean omiteEstOrgLogin = ParamSist.paramEquals(CodedValues.TPC_OMITIR_ORG_EST_LOGIN_SER, CodedValues.TPC_SIM, responsavel);

        // Parâmetros para definir URL para instalação do aplicativo nas lojas
        final String urlAppGoogleStore = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_APLICATIVO_SER_GOOGLE_STORE, responsavel);
        final String urlAppAppleStore = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_APLICATIVO_SER_APPLE_STORE, responsavel);

        String campoLabel = null;
        String campoValor = null;
        List<TransferObject> entidades = null;
        String telaValidacao = null;
        String usuLogin = null;
        String cpfLogin = null;
        String usuNome = null;
        final String loginValido = null;
        String loginDefVisual = null;
        String estOrgCodigo = null;
        String estOrgNome = null;

        if (validacaoSeguranca) {
            if (session.getAttribute("tela_validacao") == null) {
                session.setAttribute("tela_validacao", "1");
            }
            telaValidacao = session.getAttribute("tela_validacao").toString();
            usuLogin = (session.getAttribute("usu_login") != null) ? session.getAttribute("usu_login").toString() : "";
            usuNome = (session.getAttribute("usu_nome") != null) ? session.getAttribute("usu_nome").toString() : "";
            cpfLogin = (session.getAttribute("usu_cpf") != null) ? session.getAttribute("usu_cpf").toString() : "";
            estOrgCodigo = (session.getAttribute("est_org_codigo") != null) ? session.getAttribute("est_org_codigo").toString() : "";
            loginDefVisual = (session.getAttribute("ser_deficiente_visual") != null) ? session.getAttribute("ser_deficiente_visual").toString() : "N";
            if (loginDefVisual.equals("S")) {
                exibeVk = false;
                exibeCaptcha = false;
                exibeCaptchaAvancado = false;
                exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN_SER, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            }
            if (telaValidacao.equals("2")) {
                session.removeAttribute("tela_validacao");
                session.removeAttribute("est_org_codigo");
            }
            if (telaValidacao.equals("3")) {
                session.removeAttribute("tela_validacao");
            }
        }
        if (!validacaoSeguranca || (!TextHelper.isNull(telaValidacao) && !telaValidacao.equals("2") && !telaValidacao.equals("3"))) {
            session.removeAttribute("login_invalido");
        }

        try {
            CustomTransferObject criterio = new CustomTransferObject();

            if (exibeEstOrgAtivos) {
                criterio.setAttribute(Columns.ORG_ATIVO, CodedValues.STS_ATIVO);
                criterio.setAttribute(Columns.EST_ATIVO, CodedValues.STS_ATIVO);
            } else {
                criterio = null;
            }
            if (loginComEstOrg) {
                entidades = consignanteController.lstOrgaos(criterio, responsavel);
                campoLabel = Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR;
                campoValor = Columns.ORG_CODIGO;
                if (!TextHelper.isNull(estOrgCodigo)) {
                    for (final TransferObject next : entidades) {
                        if (estOrgCodigo.equals(next.getAttribute(Columns.ORG_CODIGO))) {
                            estOrgNome = next.getAttribute(Columns.ORG_NOME).toString() + " - " + next.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                        }
                    }
                }
            } else {
                entidades = consignanteController.lstEstabelecimentos(criterio, responsavel);
                campoLabel = Columns.EST_NOME;
                campoValor = Columns.EST_CODIGO;
                if (!TextHelper.isNull(estOrgCodigo)) {
                    for (final TransferObject next : entidades) {
                        if (estOrgCodigo.equals(next.getAttribute(Columns.EST_CODIGO))) {
                            estOrgNome = next.getAttribute(Columns.EST_NOME).toString();
                        }
                    }
                }
            }
        } catch (final ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Validação para o botão de "Mais opções":
        // 1 - quando tiver 0 não habilita o botão;
        // 2 - quando tiver 1 mostra apenas o botão com a opção;
        // 3 - quando tiver mais de um o botão aparece com as opções.
        int quantidadeDeItensMaisOpcoes = 0;

        if (ParamSist.paramEquals(CodedValues.TPC_VALIDA_OTP_PRIMEIRO_ACESSO_USUARIO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            quantidadeDeItensMaisOpcoes++;
        }
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_RECUPERAR_SENHA_SER, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            quantidadeDeItensMaisOpcoes++;
        }
        if (ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_CADASTRA_SENHA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            quantidadeDeItensMaisOpcoes++;
        }
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CHAT_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            quantidadeDeItensMaisOpcoes++;
        }
        if (!ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_DESABILITADO, AcessoSistema.getAcessoUsuarioSistema())) {
            quantidadeDeItensMaisOpcoes++;
        }

        model.addAttribute("exibeCaptcha", exibeCaptcha);
        model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
        model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
        model.addAttribute("exibeVk", exibeVk);
        model.addAttribute("telaValidacao", telaValidacao);
        model.addAttribute("usuLogin", loginComCfp ? cpfLogin : usuLogin);
        model.addAttribute("usuNome", usuNome);
        model.addAttribute("loginValido", loginValido);
        model.addAttribute("loginDefVisual", loginDefVisual);
        model.addAttribute("estOrgCodigo", estOrgCodigo);
        model.addAttribute("estOrgNome", estOrgNome);
        model.addAttribute("campoLabel", campoLabel);
        model.addAttribute("campoValor", campoValor);
        model.addAttribute("entidades", entidades);
        model.addAttribute("quantidadeDeItensMaisOpcoes", quantidadeDeItensMaisOpcoes);
        model.addAttribute("loginComCfp", loginComCfp);
        model.addAttribute("urlAppGoogleStore", urlAppGoogleStore);
        model.addAttribute("urlAppAppleStore", urlAppAppleStore);
        model.addAttribute("omiteEstOrgLogin", omiteEstOrgLogin);

        // Habilita o chatbot na página de login caso o parâmetro esteja habilitado
        if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_ID_CHATBOT_PAGINA_LOGIN_USU, responsavel)) ||
                !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_ID_CHATBOT_PAGINA_LOGIN_SER, responsavel))) {
            session.setAttribute(ChatbotRestController.CHATBOT_ORIGEM_LOGIN_SERVIDOR, Boolean.TRUE);
        }
    }

    @RequestMapping(value = { "/v3/autenticar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final Object error = session.getAttribute(CodedValues.MSG_ERRO);
        try {
            //recupera se o login veio do fluxo da DESENV-13252
            final Boolean termoUso = (Boolean) session.getAttribute("termo_usu");

            // Invalida a sessão do usuário, caso exista
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute(CodedValues.MSG_ERRO, error);
            // Obtém novamente o responsável
            responsavel = JspHelper.getAcessoSistema(request);

            if(termoUso != null) {
                session.setAttribute("termo_usu", Boolean.TRUE);
            }

        } catch (final IllegalStateException ex) {
            // Trata erro caso a sessão já esteja invalidada
        }

        // Verifica se o portal do servidor esta habilitado
        final boolean possuiPortalServidor = ParamSist.paramEquals(CodedValues.TPC_POSSUI_PORTAL_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        if (!possuiPortalServidor) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informacao.portal.servidor.desabilitado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_MENSAGEM_TELA_LOGIN_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            try {
                String mensagemTelaLoginServidor;
                mensagemTelaLoginServidor = getTextoAutenticacaoServidor(responsavel);
                if (!TextHelper.isNull(mensagemTelaLoginServidor)) {
                    model.addAttribute("mensagemTelaLoginServidor", mensagemTelaLoginServidor);
                }
            } catch (final ViewHelperException e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        SynchronizerToken.saveToken(request);
        response.addCookie(new Cookie("LOGIN", "SERVIDOR"));
        response.addCookie(new Cookie("MOBILE", ""));

        if (ParamSist.paramEquals(CodedValues.TPC_SENHA_EXTERNA, CodedValues.TPC_SIM, responsavel) && MetodoSenhaExternaEnum.OAUTH2.getMetodo().equals(ParamSenhaExternaEnum.METODO.getValor())) {
            if (!TextHelper.isNull(error)) {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            // Se é senha externa e a autenticação é com OAuth2 redireciona para a página de login externa
            return "redirect:/v3/redirecionarOAuth2?acao=entrar";
        }

        return viewRedirect("jsp/autenticarServidor/autenticarServidor", request, session, model, responsavel);
    }

    @RequestMapping(value = { "/v3/autenticar" }, params = { "acao=autenticar" })
    public String autenticar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        final boolean loginComCPF = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_CPF, CodedValues.TPC_SIM, responsavel);

        // Parâmetro para definir se omite escolha de órgão/estabelecimento da tela de login de servidor
        final boolean omiteEstOrgLogin = ParamSist.paramEquals(CodedValues.TPC_OMITIR_ORG_EST_LOGIN_SER, CodedValues.TPC_SIM, responsavel);

        boolean autenticadoViaOAuth2 = false;
        if (ParamSist.paramEquals(CodedValues.TPC_SENHA_EXTERNA, CodedValues.TPC_SIM, responsavel) && MetodoSenhaExternaEnum.OAUTH2.getMetodo().equals(ParamSenhaExternaEnum.METODO.getValor())) {
            autenticadoViaOAuth2 = ((request.getAttribute("OAuth2TokenValido") != null) || (session.getAttribute("autenticadoViaOAuth2") != null));
            session.removeAttribute("autenticadoViaOAuth2");
        }

        // Verifica se o portal do servidor esta habilitado
        final boolean possuiPortalServidor = ParamSist.paramEquals(CodedValues.TPC_POSSUI_PORTAL_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        if (!possuiPortalServidor) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.portal.servidor.desabilitado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        responsavel.setIpUsuario(JspHelper.getRemoteAddr(request));
        responsavel.setPortaLogicaUsuario(JspHelper.getRemotePort(request));
        final boolean alteraLoginServidor = !TextHelper.isNull(session.getAttribute("altera_login_servidor"));
        final String serCodigoLoginOriginal = (session.getAttribute("ser_codigo_login_original") != null) ? session.getAttribute("ser_codigo_login_original").toString() : "";
        session.removeAttribute("altera_login_servidor");
        session.removeAttribute("ser_codigo_login_original");

        // Verifica status de bloqueio de sistema: nenhum usuário servidor pode acessar o sistema quando bloqueado
        Short status = 2;
        try {
            status = sistemaController.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
        } catch (final ConsignanteControllerException e1) {
            LOG.error(e1);
        }
        if (status.equals(CodedValues.STS_INDISP)) {
            session.setAttribute(CodedValues.MSG_ERRO, LoginHelper.getMensagemSistemaIndisponivel());
            request.setAttribute("tipo", "indisponivel");
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // verifica se o login é realizado em duas etapas
        boolean validacaoSeguranca = ParamSist.paramEquals(CodedValues.TPC_VALIDACAO_SEGURANCA_TELA_LOGIN_SER, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        final String telaValidacao = (session.getAttribute("tela_validacao") != null) ? session.getAttribute("tela_validacao").toString() : "";
        final boolean loginInvalido = (session.getAttribute("login_invalido") != null) ? ((Boolean) session.getAttribute("login_invalido")) : false;
        String loginDefVisual = "N";
        // Usuário só é considerado deficiente visual quando existe login em duas etapas
        if (validacaoSeguranca) {
            loginDefVisual = (session.getAttribute("ser_deficiente_visual") != null) ? session.getAttribute("ser_deficiente_visual").toString() : "N";
        }

        // Validação externa é feita validação de chave/contra-chave para acesso sem informação de matrícula/senha
        // Estado do Espirito Santo|https://www.servidor.es.gov.br/scriptsseg/recebe_consig.aspx
        // Estado da Paraiba|http://secadm.no-ip.org/portaldoservidor/valida_chave.php
        // Prefeitura de Camaçari|http://www.camacari.ba.gov.br/contracheque/verificador.php
        final String contraChave = JspHelper.verificaVarQryStr(request, "chave");
        final boolean validacaoExterna = !contraChave.equals("") && (ParamSist.getInstance().getParam(CodedValues.TPC_URL_VALIDACAO_EXTERNA_LOGIN, responsavel) != null);
        String caminhoValidacao = validacaoExterna ? ParamSist.getInstance().getParam(CodedValues.TPC_URL_VALIDACAO_EXTERNA_LOGIN, responsavel).toString() : "";
        boolean loginExterno =  session.getAttribute("loginExterno") != null ? (boolean) session.getAttribute("loginExterno") : false;

        // Se o tipo de login (matricula (é default) ou cpf é informado na requisição possui prioridade sobre TPC_URL_VALIDACAO_EXTERNA_LOGIN
        String tipoLogin = validacaoExterna ? JspHelper.verificaVarQryStr(request, "tipologin") : "matricula";
        if (validacaoExterna && TextHelper.isNull(tipoLogin)) {
            final String parametros = (caminhoValidacao.split("\\?").length == 2) ? caminhoValidacao.split("\\?")[1] : "tipologin=matricula";
            final Map<String, String> mapParametros = new LinkedHashMap<>();
            for (final String parametroValor : parametros.split(" *& *")) {
                final String[] pares = parametroValor.split(" *= *", 2);
                mapParametros.put(pares[0], pares.length == 1 ? "" : pares[1]);
            }
            tipoLogin = (mapParametros.containsKey("tipologin") ? mapParametros.get("tipologin") : "matricula");
        }

        if ((loginComCPF || omiteEstOrgLogin) && !validacaoExterna) {
            tipoLogin = "cpfLocal";
        }

        // Verifica parâmetro que indica a forma do login de usuário servidor
        final boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);
        String estCodigo = null;
        String estIdentificador = null;
        String orgCodigo = null;
        String orgIdentificador = null;

        if (alteraLoginServidor) {
            // na alteração de registro funcional, é necessário informar estabelecimento e órgão
            estCodigo = JspHelper.verificaVarQryStr(request, "codigo_estabelecimento");
            estIdentificador = JspHelper.verificaVarQryStr(request, "estabelecimento");
            orgCodigo = JspHelper.verificaVarQryStr(request, "codigo_orgao");
            orgIdentificador = JspHelper.verificaVarQryStr(request, "orgao");
        } else if (loginComEstOrg) {
            orgCodigo = JspHelper.verificaVarQryStr(request, "codigo_orgao");
            orgIdentificador = JspHelper.verificaVarQryStr(request, "orgao");
        } else {
            estCodigo = JspHelper.verificaVarQryStr(request, "codigo_orgao");
            estIdentificador = JspHelper.verificaVarQryStr(request, "orgao");
        }

        final String mascaraLoginExterno = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
        String matricula = JspHelper.verificaVarQryStr(request, "username");
        final String senhaCriptografada = JspHelper.verificaVarQryStr(request, JspHelper.verificaVarQryStr(request, "cryptedPasswordFieldName"));
        String serCpf = (request.getAttribute("cpf") != null ? request.getAttribute("cpf").toString() : JspHelper.verificaVarQryStr(request, "serCpf"));
        String usuLogin = JspHelper.verificaVarQryStr(request, "usuLogin");

        if (TextHelper.isNull(serCpf) && loginComCPF) {
            serCpf = matricula;
            matricula = "";
        }

        if (!loginComCPF) {
            usuLogin = "";
        }

        final String msgErroLoginInvalido = LoginHelper.getMensagemErroLoginServidor();

        Exception exc = null;
        TransferObject servidor = null;
        TransferObject usuario = null;
        AcessoSistema usuAcesso = null;
        boolean expirada = false;
        List<TransferObject> usuariosLoginCpf = null;

        try {
            // assume que não haverá login em duas etapas quando for login externo
            if (!TextHelper.isNull(mascaraLoginExterno)) {
                validacaoSeguranca = false;
            }

            String senhaAberta = "";

            if (!autenticadoViaOAuth2) {
                if (!validacaoSeguranca || !telaValidacao.equals("1")) {

                    boolean ignorarCaptcha = loginComCPF && (session.getAttribute("senhaFluxoLoginCPF") != null) ? true : false;

                    if (!ignorarCaptcha && !TextHelper.isNull(session.getAttribute("ignoraCaptchaLoginSer"))) {
                        ignorarCaptcha = true;
                        session.removeAttribute("ignoraCaptchaLoginSer");
                    }

                    if (!loginDefVisual.equals("S")) {
                        if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN_SERVIDOR, CodedValues.TPC_SIM, responsavel) && (session.getAttribute(CodedNames.ATTR_SESSION_CENTRALIZADOR) == null) && !validacaoExterna && !alteraLoginServidor && !ignorarCaptcha) {
                            if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                    && !ImageCaptchaServlet.validaCaptcha(session.getId(), request.getParameter("captcha"))) {
                                throw new UsuarioControllerException("mensagem.erro.captcha.invalido", responsavel);
                            }
                            session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                        } else if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN_SER, CodedValues.TPC_SIM, responsavel) && (session.getAttribute(CodedNames.ATTR_SESSION_CENTRALIZADOR) == null) && !validacaoExterna && !alteraLoginServidor && !ignorarCaptcha) {
                            final String remoteAddr = request.getRemoteAddr();

                            if (!isValidCaptcha(request.getParameter("g-recaptcha-response"), remoteAddr, responsavel)) {
                                throw new UsuarioControllerException("mensagem.erro.captcha.invalido", responsavel);
                            }
                        }
                    } else {
                        final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN_SER, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                        if (exigeCaptchaDeficiente) {
                            final String captchaAnswer = request.getParameter("captcha");
                            final String captchaCode   = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                            if ((captchaCode == null) || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                                throw new UsuarioControllerException("mensagem.erro.captcha.invalido", responsavel);
                            }
                            session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                        }
                    }
                    // Se não é login externo, pode ser validação externa.
                    if (TextHelper.isNull(mascaraLoginExterno)) {
                        // Se a validação é feita externamente, gera um token. Caso contrário valida o token vindo da página de login.
                        if (validacaoExterna || alteraLoginServidor) {
                            SynchronizerToken.generateHtmlToken(request);
                        } else if (!SynchronizerToken.isTokenValid(request)) {
                            throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                        }
                    }

                    // Decriptografa a senha informada
                    if (!validacaoExterna && !alteraLoginServidor) {
                        try {
                            final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
                            senhaAberta = RSA.decrypt(senhaCriptografada, keyPair.getPrivate());
                        } catch (final BadPaddingException e) {
                            if (session.getAttribute("servidores") != null) {
                                // Se o servidor já entrou a senha e está escolhendo um do registros então não precisa da senha.
                                senhaAberta = null;
                            } else if (loginComCPF || omiteEstOrgLogin) {
                                senhaAberta = (String) session.getAttribute("senhaFluxoLoginCPF");
                                session.removeAttribute("senhaFluxoLoginCPF");
                            } else {
                                // Corresponde a tentativa de decriptografia com chave errada. A sessão pode ter expirado. Tentar novamente.
                                throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                            }
                        }
                    }
                }

                // quando o login é feito em duas etapas, verifica se o login é inválido e já retorna a mensagem de erro
                if (validacaoSeguranca && loginInvalido) {
                    session.removeAttribute("login_invalido");
                    throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                }

                // Busca as entidades estabelecimento / órgão de acordo com o que foi passado
                if (!TextHelper.isNull(estCodigo)) {
                    final EstabelecimentoTransferObject estabelecimento = consignanteController.findEstabelecimento(estCodigo, responsavel);
                    estIdentificador = estabelecimento.getEstIdentificador();
                } else if (!TextHelper.isNull(estIdentificador)) {
                    final EstabelecimentoTransferObject estabelecimento = consignanteController.findEstabelecimentoByIdn(estIdentificador, responsavel);
                    estCodigo = estabelecimento.getEstCodigo();
                } else if (!TextHelper.isNull(orgCodigo)) {
                    final CustomTransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.ORG_CODIGO, orgCodigo);
                    final List<TransferObject> orgaos = consignanteController.lstOrgaos(criterio, responsavel);
                    if ((orgaos != null) && (orgaos.size() > 0)) {
                        final TransferObject orgao = orgaos.get(0);
                        estCodigo = (String) orgao.getAttribute(Columns.EST_CODIGO);
                        estIdentificador = (String) orgao.getAttribute(Columns.EST_IDENTIFICADOR);
                        orgIdentificador = (String) orgao.getAttribute(Columns.ORG_IDENTIFICADOR);
                    }
                } else if (!TextHelper.isNull(orgIdentificador)) {
                    final CustomTransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.ORG_IDENTIFICADOR, orgIdentificador);
                    final List<TransferObject> orgaos = consignanteController.lstOrgaos(criterio, responsavel);
                    if ((orgaos != null) && (orgaos.size() > 0)) {
                        final TransferObject orgao = orgaos.get(0);
                        estCodigo = (String) orgao.getAttribute(Columns.EST_CODIGO);
                        estIdentificador = (String) orgao.getAttribute(Columns.EST_IDENTIFICADOR);
                        orgCodigo = (String) orgao.getAttribute(Columns.ORG_CODIGO);
                    }
                } else if (!loginComCPF && !omiteEstOrgLogin) {
                    // Se não foi passado nem estabelecimento nem órgão, então assume que o sistema
                    // tenha apenas um estabelecimento ou órgão
                    if (loginComEstOrg) {
                        // Se o login de usuário servidor tem órgão, então assume que o sistema só tem um órgão
                        final List<TransferObject> orgaos = consignanteController.lstOrgaos(null, responsavel);
                        if ((orgaos != null) && (orgaos.size() > 0)) {
                            final TransferObject orgao = orgaos.get(0);
                            estCodigo = (String) orgao.getAttribute(Columns.EST_CODIGO);
                            estIdentificador = (String) orgao.getAttribute(Columns.EST_IDENTIFICADOR);
                            orgCodigo = (String) orgao.getAttribute(Columns.ORG_CODIGO);
                            orgIdentificador = (String) orgao.getAttribute(Columns.ORG_IDENTIFICADOR);
                        }
                    } else {
                        // Se o login de usuário servidor só tem estabelecimento, então assume que o sistema só tem um estabelecimento
                        final List<TransferObject> estabelecimentos = consignanteController.lstEstabelecimentos(null, responsavel);
                        if ((estabelecimentos != null) && (estabelecimentos.size() > 0)) {
                            final TransferObject estabelecimento = estabelecimentos.get(0);
                            estCodigo = (String) estabelecimento.getAttribute(Columns.EST_CODIGO);
                            estIdentificador = (String) estabelecimento.getAttribute(Columns.EST_IDENTIFICADOR);
                        }
                    }
                }
            }

            if (TextHelper.isNull(mascaraLoginExterno)) {
                if (tipoLogin.equalsIgnoreCase("cpf")) {
                    usuariosLoginCpf = usuarioController.lstUsuariosSer(LocaleHelper.formatarCpf(serCpf), null, estIdentificador, orgIdentificador, responsavel);
                    // Verifica se o sistema permite a alteração do login do servidor (registro funcional) sem sair do sistema
                    final boolean permiteAlterarLogin = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_LOGIN_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

                    int numRegistrosAtivos = 0;
                    String matriculaAtiva = null;
                    for (final TransferObject element : usuariosLoginCpf) {
                        usuario = element;
                        matricula = (String) usuario.getAttribute(Columns.RSE_MATRICULA);
                        final String stu_codigo = usuario.getAttribute(Columns.USU_STU_CODIGO) != null ? usuario.getAttribute(Columns.USU_STU_CODIGO).toString() : CodedValues.STU_ATIVO;

                        if (stu_codigo.equals(CodedValues.STU_ATIVO)) {
                            if (permiteAlterarLogin) {
                                // Pega o primeiro ativo, pois permite alteração do login do servidor (registro funcional) sem sair do sistema
                                break;
                            } else if (++numRegistrosAtivos > 1) {
                                throw new UsuarioControllerException("mensagem.erro.multiplos.usuarios", responsavel);
                            } else {
                                matriculaAtiva = matricula;
                            }
                        }
                    }
                    matricula = matriculaAtiva != null ? matriculaAtiva : matricula;
                } else if (tipoLogin.equalsIgnoreCase("cpfLocal") && (!TextHelper.isNull(serCpf) || !TextHelper.isNull(matricula))) {
                    // DESENV-16197 : se for CPF, então tem que garantir que o conteúdo da matrícula não seja CPF.
                    if (loginComCPF && TextHelper.cpfOk(TextHelper.dropSeparator(matricula)) && !alteraLoginServidor) {
                        matricula = "";
                    } else if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "rseMatricula"))) {
                        matricula = JspHelper.verificaVarQryStr(request, "rseMatricula");
                    }
                    final List<TransferObject> usuariosComMesmoCPF = usuarioController.lstUsuariosSerLoginComCpf(usuLogin, matricula, serCpf, estIdentificador, orgIdentificador, false, responsavel);
                    ListIterator<TransferObject> iteratorUsuariosComMesmoCPF = usuariosComMesmoCPF.listIterator();

                    boolean possuiServidorExcluido = false;
                    final Set<String> usuLoginUnicos = new HashSet<>();
                    while (iteratorUsuariosComMesmoCPF.hasNext()) {
                        final TransferObject usuarioComCpf = iteratorUsuariosComMesmoCPF.next();
                        final String tmpUsuLogin = (String) usuarioComCpf.getAttribute(Columns.USU_LOGIN);
                        final String tmpSrsCodigo = (String) usuarioComCpf.getAttribute(Columns.SRS_CODIGO);

                        if (CodedValues.SRS_INATIVOS.contains(tmpSrsCodigo)) {
                            possuiServidorExcluido = true;
                            iteratorUsuariosComMesmoCPF.remove();
                            continue;
                        }

                        if (usuLoginUnicos.contains(tmpUsuLogin)) {
                            iteratorUsuariosComMesmoCPF.remove();
                        }

                        usuLoginUnicos.add(tmpUsuLogin);
                    }

                    if (possuiServidorExcluido && ((usuariosComMesmoCPF == null) || usuariosComMesmoCPF.isEmpty())) {
                        throw new UsuarioControllerException("mensagem.erroLoginServidor.arquivado", responsavel);
                    }

                    iteratorUsuariosComMesmoCPF = usuariosComMesmoCPF.listIterator();

                    // Se tem 2 passo pegamos o primeiro usuario encontrado e retornamos
                    if (validacaoSeguranca && telaValidacao.equals("1") && (usuariosComMesmoCPF.size() > 0)) {
                        final TransferObject usuarioComCpf = iteratorUsuariosComMesmoCPF.next();
                        usuLogin = (String) usuarioComCpf.getAttribute(Columns.USU_LOGIN);
                        matricula = (String) usuarioComCpf.getAttribute(Columns.RSE_MATRICULA);
                        orgCodigo = (String) usuarioComCpf.getAttribute(Columns.ORG_CODIGO);
                        estCodigo = (String) usuarioComCpf.getAttribute(Columns.EST_CODIGO);
                    } else {
                        UsuarioControllerException erroOriginal = null;
                        final List<TransferObject> usuariosComSenhaDandoMatch = new ArrayList<>();
                        while (iteratorUsuariosComMesmoCPF.hasNext()) {
                            final TransferObject usuarioEncontrado = iteratorUsuariosComMesmoCPF.next();

                            final String rseCodigo = (String) usuarioEncontrado.getAttribute(Columns.RSE_CODIGO);
                            final String rseMatriucla = (String) usuarioEncontrado.getAttribute(Columns.RSE_MATRICULA);

                            // Validando se a senha esta ok
                            try {
                                if (!loginExterno && !autenticadoViaOAuth2 && !alteraLoginServidor) {
                                    SenhaHelper.validarSenhaServidor(rseCodigo, senhaAberta, JspHelper.getRemoteAddr(request), rseMatriucla, null, false, true, responsavel);
                                }
                                usuariosComSenhaDandoMatch.add(usuarioEncontrado);
                            } catch (final SenhaExpiradaException e) {
                                // Se retornou senha expirada, significa que a autenticação está correta
                                usuariosComSenhaDandoMatch.add(usuarioEncontrado);
                            } catch (final UsuarioControllerException ex) {
                                LOG.error(ex.getMessage(), ex);
                                erroOriginal = ex;
                            }
                        }

                        // DESENV-17727 Remove as tentativas de acesso. Para não bloquear usuários indevidamente.
                        if(usuariosComSenhaDandoMatch.size() >= 1) {
                        	while (iteratorUsuariosComMesmoCPF.hasPrevious()) {
                        		final TransferObject usuarioEncontrado = iteratorUsuariosComMesmoCPF.previous();
                        		ControleLogin.getInstance().resetTetantivasLogin((String) usuarioEncontrado.getAttribute(Columns.USU_CODIGO));
                        	}
                        }

                        if (usuariosComSenhaDandoMatch.size() == 1) {
                            final TransferObject usuarioComCpf = usuariosComSenhaDandoMatch.get(0);
                            usuLogin = (String) usuarioComCpf.getAttribute(Columns.USU_LOGIN);
                            matricula = (String) usuarioComCpf.getAttribute(Columns.RSE_MATRICULA);
                            orgCodigo = (String) usuarioComCpf.getAttribute(Columns.ORG_CODIGO);
                            orgIdentificador = (String) usuarioComCpf.getAttribute(Columns.ORG_IDENTIFICADOR);
                            estCodigo = (String) usuarioComCpf.getAttribute(Columns.EST_CODIGO);
                            estIdentificador = (String) usuarioComCpf.getAttribute(Columns.EST_IDENTIFICADOR);
                        } else if (usuariosComSenhaDandoMatch.size() > 1) {
                            // Enviando usuario para mais uma tela para ele selecionar qual usuario ele quer fazer login
                            session.setAttribute("tela_validacao", "3");
                            session.setAttribute("senhaFluxoLoginCPF", senhaAberta);
                            session.setAttribute("autenticadoViaOAuth2", request.getAttribute("OAuth2TokenValido"));
                            model.addAttribute("serCpf", serCpf);
                            model.addAttribute("usuarios", usuariosComSenhaDandoMatch);
                            return viewRedirect("jsp/autenticarServidor/autenticarServidorSelecionaUsuario", request, session, model, responsavel);
                        } else if ((usuariosComMesmoCPF.size() == 1) && (erroOriginal != null) && "mensagem.usuarioBloqueado".equals(erroOriginal.getMessageKey())) {
                            // Reporta o erro original caso ele seja de bloqueio de usuário e o CPF possua apenas este usuário
                            throw erroOriginal;
                        }
                    }
                }

                if (ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, responsavel) && !loginComCPF) {
                    try {
                        matricula = String.valueOf(Long.parseLong(matricula));
                    } catch (final NumberFormatException ex) {
                        throw new UsuarioControllerException("mensagem.erro.matricula.somente.numerica", responsavel);
                    }
                }

                if (loginComEstOrg && !loginComCPF && !omiteEstOrgLogin) {
                    usuLogin = estIdentificador + "-" + orgIdentificador + "-" + matricula;
                } else if (!loginComCPF && !omiteEstOrgLogin) {
                    usuLogin = estIdentificador + "-" + matricula;
                }
                LOG.debug("Login do Servidor: " + usuLogin);
                if (!TextHelper.isNull(usuLogin)) {
                    usuario = usuarioController.findTipoUsuarioByLogin(usuLogin, responsavel);
                }

                // verifica se a troca de registro funcional é válida
                if (alteraLoginServidor && (usuario != null) && !usuario.getAttribute(Columns.USE_SER_CODIGO).toString().equals(serCodigoLoginOriginal)) {
                    // registra log de segurança
                    try {
                        final LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.LOGIN, Log.LOG_ERRO_SEGURANCA);
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.log.erro.alteracao.registro.funcional.nao.permitida", responsavel));
                        log.write();
                    } catch (final LogControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                    session.invalidate();
                    session = request.getSession(true);
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                    return iniciar(request, response, session, model);
                }

                // verifica se o login é feito em duas etapas e volta para a tela anterior para solicitar os outros dados
                if (validacaoSeguranca && telaValidacao.equals("1")) {
                    session.setAttribute("usu_login", loginComCPF ? serCpf : matricula);
                    session.setAttribute("usu_cpf", serCpf);
                    session.setAttribute("est_org_codigo", (loginComEstOrg ? orgCodigo : estCodigo));
                    // verifica se o login existe
                    if (usuario != null) {
                        // recupera informações do usuário
                        servidor = servidorController.getRegistroServidorPelaMatricula((String) usuario.getAttribute(Columns.USE_SER_CODIGO), orgCodigo, estCodigo, matricula, usuAcesso);
                        if (servidor == null) {
                            throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                        }
                        session.setAttribute("usu_nome", TextHelper.formataNomeUsuario(servidor.getAttribute(Columns.SER_NOME).toString().toUpperCase(), responsavel));
                        session.setAttribute("ser_deficiente_visual", (usuario.getAttribute(Columns.SER_DEFICIENTE_VISUAL) != null ? usuario.getAttribute(Columns.SER_DEFICIENTE_VISUAL).toString() : "N"));
                    } else {
                        if (!TextHelper.isNull(serCpf) || !TextHelper.isNull(matricula)) {
                            // Se servidor foi arquivado, retorna mensagem de erro específica para arquivamento
                            final List<TransferObject> lstHistoricoSer = servidorController.lstHistoricoServidor(matricula, serCpf, orgCodigo, estCodigo, responsavel);
                            if ((lstHistoricoSer != null) && !lstHistoricoSer.isEmpty()) {
                                throw new UsuarioControllerException("mensagem.erroLoginServidor.arquivado", responsavel);
                            }
                        }

                        // retorna um nome aleatório para dificultar ação de "robôs"
                        final int total = usuarioController.countNomeUsuario(responsavel);
                        // DESENV-19026 : gera o número aleatório usando um seed para garantir que a consulta retornará sempre o mesmo nome
                        // para o login informado (para login incorreto), evitando que o agente malicioso possa descobrir que o login não
                        // existe quando o retorno das tentativas for diferente. ATENÇÃO: não usar um SecureRandom pois este não é deterministico.
                        final int seed = loginComCPF ? serCpf.hashCode() : matricula.hashCode();
                        final Random aleatorio = new Random(seed);
                        final int offset = aleatorio.nextInt(total);
                        final CustomTransferObject usuarioRand = (CustomTransferObject) usuarioController.obtemNomeUsuario(null, null, offset, responsavel);
                        final String usuarioRandNome = usuarioRand.getAttribute(Columns.USU_NOME).toString();
                        session.setAttribute("usu_nome", TextHelper.formataNomeUsuario(usuarioRandNome.toUpperCase(), responsavel));
                        session.setAttribute("ser_deficiente_visual", "N");
                        session.setAttribute("login_invalido", true);
                    }
                    // DESENV-15765: Necessário adicionar na sessão os dados de estabelecimento e orgao para o usuário não ter que escolher novamente
                    // qual usuário que ele fará login se tiver mais de um, pois ele já escolheu no fluxo de trocar usuário.
                    if (alteraLoginServidor) {
                        if (loginComEstOrg) {
                            session.setAttribute("codigo_orgao",orgCodigo);
                            session.setAttribute("orgao",orgIdentificador);
                        } else {
                            session.setAttribute("codigo_orgao",estCodigo);
                            session.setAttribute("orgao",estIdentificador);
                        }
                        session.setAttribute("rse_matricula",JspHelper.verificaVarQryStr(request, "rseMatricula"));
                        session.setAttribute("altera_login_servidor", alteraLoginServidor);
                        session.setAttribute("ser_codigo_login_original", serCodigoLoginOriginal);
                    }
                    // redireciona para a segunda tela de validação de login
                    session.setAttribute("tela_validacao", "2");
                    return viewRedirect("jsp/autenticarServidor/autenticarServidorPasso2", request, session, model, responsavel);
                }

                if (usuario == null) {
                    throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                }

                // Define senha expirada, caso a consulta de busca já tenha retornado esta informação
                expirada = ((usuario.getAttribute("EXPIROU") != null) && usuario.getAttribute("EXPIROU").equals("1"));

                usuAcesso = new AcessoSistema(usuario.getAttribute(Columns.USU_CODIGO).toString(), JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));

                // Busca o registro servidor que não deve estar na situação de excluído
                servidor = servidorController.getRegistroServidorPelaMatricula((String) usuario.getAttribute(Columns.USE_SER_CODIGO), orgCodigo, estCodigo, matricula, usuAcesso);
                if (servidor == null) {
                    throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                }

                if (validacaoExterna) {
                    HttpHelper.desabilitaSSL();

                    if (!caminhoValidacao.contains("?")) {
                        caminhoValidacao += "?";
                    } else {
                        caminhoValidacao += "&";
                    }

                    String userName = matricula;
                    if (tipoLogin.equalsIgnoreCase("cpf")) {
                        userName = serCpf;
                    }

                    if (loginComEstOrg) {
                        caminhoValidacao += "username=" + userName + "&orgao=" + orgIdentificador + "&chave=" + contraChave;
                    } else {
                        caminhoValidacao += "username=" + userName + "&orgao=" + estIdentificador + "&chave=" + contraChave;
                    }

                    // Tratamento para repassar valor recebido na requisição para ponta
                    int inicio = 0;
                    while (caminhoValidacao.indexOf("<", inicio) != -1) {
                        inicio = caminhoValidacao.indexOf("<", inicio) + 1;
                        final int fim = caminhoValidacao.indexOf(">", inicio);
                        final String parametro = caminhoValidacao.substring(inicio, fim);
                        final String valor = JspHelper.verificaVarQryStr(request, parametro);

                        caminhoValidacao = caminhoValidacao.replaceAll("<" + parametro + ">", valor);
                    }

                    if (ParamSist.getBoolParamSist(CodedValues.TPC_URL_VALIDACAO_EXTERNA_GET, responsavel)) {
                        final String urlExterna = ParamSist.getInstance().getParam(CodedValues.TPC_URL_VALIDACAO_EXTERNA_LOGIN, responsavel).toString() + contraChave;
                        if (!validarContraChaveGetUnimed(urlExterna, responsavel)) {
                            throw new UsuarioControllerException("mensagem.sessao.invalida", responsavel);
                        }
                    } else if (!validarContraChave(caminhoValidacao).replaceAll("[ \t]", "").equals("OK")) {
                        throw new UsuarioControllerException("mensagem.sessao.invalida", responsavel);
                    }

                    //registro se o usuário logado foi fruto de um login externo apra troca de matricula sem requisitar senha
                    session.setAttribute("loginExterno", true);
                    loginExterno = true;

                    // DESENV-16005 - Possibilitar o usuário escolher qual usuário acessar o sistema para logins externo
                    if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_URL_VALIDACAO_EXTERNA_LOGIN, responsavel)) && (usuariosLoginCpf != null) && (usuariosLoginCpf.size() > 1)) {
                        session.setAttribute("tela_validacao", "3");
                        session.setAttribute("senhaFluxoLoginCPF", senhaAberta);
                        session.setAttribute("autenticadoViaOAuth2", request.getAttribute("OAuth2TokenValido"));
                        model.addAttribute("serCpf", serCpf);
                        model.addAttribute("usuarios", usuariosLoginCpf);
                        return viewRedirect("jsp/autenticarServidor/autenticarServidorSelecionaUsuario", request, session, model, responsavel);
                    }
                } else if (!alteraLoginServidor) {
                    // Se a validação não for externa e não for login automático, valida a senha do servidor.
                    try {
                        //DESENV-9892: se permite autodesbloqueio para SER, exibe modal perguntando se usuário quer autdesbloquear-se
                        final String stu_codigo = usuario.getAttribute(Columns.USU_STU_CODIGO) != null ? usuario.getAttribute(Columns.USU_STU_CODIGO).toString() : CodedValues.STU_ATIVO;
                        if (stu_codigo.equals(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE)) {
                            boolean redirectAutoDesbloqueio = false;
                            if (!ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_DESABILITADO, AcessoSistema.getAcessoUsuarioSistema())) {
                                redirectAutoDesbloqueio = true;
                            }

                            if (redirectAutoDesbloqueio) {
                                model.addAttribute("redirectAutoDesbloqueio", true);
                                return viewRedirect("jsp/autenticarServidor/autenticarServidor", request, session, model, responsavel);
                            }

                            throw new UsuarioControllerException("mensagem.usuarioBloqueado", responsavel);
                        }

                        if (!loginExterno && !autenticadoViaOAuth2) {
                            SenhaHelper.validarSenhaServidor(servidor.getAttribute(Columns.RSE_CODIGO).toString(), senhaAberta, JspHelper.getRemoteAddr(request), matricula, null, false, true, responsavel);
                        }
                        expirada = false;
                    } catch (final SenhaExpiradaException ex) {
                        expirada = true;
                    }

                    // se a senha está correta, limpa o cache de tentativas para este usuário
                    JspHelper.limpaCacheTentativasLogin(usuario.getAttribute(Columns.USU_CODIGO).toString());
                }
            } else {
                // LOGIN EXTERNO: PRConsig
                try {
                    List<TransferObject> servidores = (List<TransferObject>) session.getAttribute("servidores");
                    final String selecionado = JspHelper.verificaVarQryStr(request, "rseOpt");
                    if ((servidores != null) && !selecionado.equals("") && matricula.equals(session.getAttribute("serLogin"))) {
                        servidor = servidores.get(Integer.parseInt(selecionado));
                    } else {
                        final CustomTransferObject result = SenhaHelper.validarSenhaExternaServidor(matricula, senhaAberta, JspHelper.getRemoteAddr(request), null, true, responsavel);
                        final String senhaOk = (String) result.getAttribute(SenhaExterna.KEY_SENHA);
                        final String rg = (String) result.getAttribute(SenhaExterna.KEY_RG);
                        final String cpf = (String) result.getAttribute(SenhaExterna.KEY_CPF);
                        final String msg = (String) result.getAttribute(SenhaExterna.KEY_ERRO);

                        if (!TextHelper.isNull(msg)) {
                            throw new UsuarioControllerException(msg, responsavel);
                        } else if (senhaOk == null) {
                            throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                        } else {
                            // Lista os servidores cujo o CPF ou o RG sejam iguais ao retornados pelo SenhaExterna.
                            servidores = new ArrayList<>();
                            if (!TextHelper.isNull(cpf)) {
                                servidores.addAll(pesquisarServidorController.pesquisaServidor("CSE", "1", null, null, null, cpf, responsavel, false, false));
                            }
                            if (!TextHelper.isNull(rg)) {
                                TransferObject candidato;
                                for (final TransferObject element : pesquisarServidorController.pesquisaServidor("CSE", "1", null, null, rg, null, responsavel, false, false)) {
                                    candidato = element;
                                    if (rg.equals(candidato.getAttribute(Columns.SER_NRO_IDT)) && !cpf.equals(candidato.getAttribute(Columns.SER_CPF))) {
                                        servidores.add(candidato);
                                    }
                                }
                            }
                            if (servidores.size() == 0) {
                                throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                            } else if (servidores.size() == 1) {
                                servidor = servidores.get(0);
                            } else {
                                session.setAttribute("servidores", servidores);
                                session.setAttribute("serLogin", matricula);
                                session.setAttribute("ignoraCaptchaLoginSer", true);
                                return selecionarServidor(request, response, session, model);
                            }
                        }
                    }

                    final OrgaoTransferObject orgao = consignanteController.findOrgao(servidor.getAttribute(Columns.ORG_CODIGO).toString(), responsavel);
                    final EstabelecimentoTransferObject estabelecimento = consignanteController.findEstabelecimento(orgao.getEstCodigo(), responsavel);
                    estIdentificador = estabelecimento.getEstIdentificador();
                    orgIdentificador = orgao.getOrgIdentificador();
                    matricula = (String) servidor.getAttribute(Columns.RSE_MATRICULA);
                    if (loginComEstOrg) {
                        usuLogin = estIdentificador + "-" + orgIdentificador + "-" + matricula;
                    } else {
                        usuLogin = estIdentificador + "-" + matricula;
                    }

                    usuario = usuarioController.findTipoUsuarioByLogin(usuLogin, responsavel);
                    if (usuario == null) {
                        throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                    }
                    usuAcesso = new AcessoSistema(usuario.getAttribute(Columns.USU_CODIGO).toString(), JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
                } catch (final ServidorControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
                }
            }

            final String stu_codigo = usuario.getAttribute(Columns.USU_STU_CODIGO) != null ? usuario.getAttribute(Columns.USU_STU_CODIGO).toString() : CodedValues.STU_ATIVO;

            // se sistema está configurado para bloquear automaticamente usuário na sua próxima autenticação, faz a verficação de bloqueio.
            boolean bloqueadoPorInatividade = false;
            if(ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_USU_INATIVIDADE_PROXIMA_AUTENTICACAO, responsavel)) {
            	bloqueadoPorInatividade = UsuarioHelper.bloqueioAutomaticoPorInatividade((String) usuario.getAttribute(Columns.USU_CODIGO), responsavel);
            }

            if (stu_codigo.equals(CodedValues.STU_EXCLUIDO)) {
                throw UsuarioControllerException.byMessage(msgErroLoginInvalido);
            } else if (bloqueadoPorInatividade || CodedValues.STU_CODIGOS_INATIVOS.contains(stu_codigo)) {
                model.addAttribute("usuarioBloqueado", "true");
                boolean redirectAutoDesbloqueio = false;
                //DESENV-9892: se permite autodesbloqueio para SER, exibe modal perguntando se usuário quer autdesbloquear-se
                if (stu_codigo.equals(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE)) {
                    if (!ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_DESABILITADO, AcessoSistema.getAcessoUsuarioSistema())) {
                        redirectAutoDesbloqueio = true;
                    }

                    if (redirectAutoDesbloqueio) {
                        model.addAttribute("redirectAutoDesbloqueio", true);
                        return viewRedirect("jsp/autenticarServidor/autenticarServidor", request, session, model, responsavel);
                    }
                }

                throw new UsuarioControllerException("mensagem.usuarioBloqueado", responsavel);
            } else if (stu_codigo.equals(CodedValues.STU_AGUARD_APROVACAO_CADASTRO)) {
                FacesWebServiceClient.verificarCadastro((String) usuario.getAttribute(Columns.USU_CODIGO), (String) servidor.getAttribute(Columns.SER_CPF), responsavel);
            }

            // Verifica se o usuário tem permissão para acessar o sistema a partir de seu IP de origem
            final String usuIpAcesso = (usuario.getAttribute(Columns.USU_IP_ACESSO) != null) ? usuario.getAttribute(Columns.USU_IP_ACESSO).toString() : "";
            if (!usuIpAcesso.equals("") && !JspHelper.validaIp(JspHelper.getRemoteAddr(request), usuIpAcesso)) {
                throw new UsuarioControllerException("mensagem.ipUsuarioInvalido", responsavel);
            }

            // Verifica se o usuário é obrigado a usar o centralizador para fazer login
            if ((usuario.getAttribute(Columns.USU_CENTRALIZADOR) != null) && usuario.getAttribute(Columns.USU_CENTRALIZADOR).toString().equals(CodedValues.TPC_SIM) && (session.getAttribute(CodedNames.ATTR_SESSION_CENTRALIZADOR) == null)) {
                throw new UsuarioControllerException("mensagem.informacao.usuario.deve.acessar.via.centralizador", responsavel);
            }

            //recupera se o login veio do fluxo da DESENV-13252
            final Boolean termoUso = (Boolean) session.getAttribute("termo_usu");
            final String oauthIdToken = (String) session.getAttribute(CodedValues.OAUTH2_ID_TOKEN);

            // Invalida e cria nova sessão para mudar o sessionId evitando ataque
            // de session fixation.
            session.invalidate();
            session = request.getSession(true);

            if (termoUso != null) {
                session.setAttribute("termo_usu", Boolean.TRUE);
            }

            session.setAttribute(CodedValues.OAUTH2_ID_TOKEN, oauthIdToken);
            session.setAttribute("loginExterno", loginExterno);

            // Verifica a expiração da senha.
            session.removeAttribute("AlterarSenha");
            final boolean verificaSeSenhaExpirada = ParamSist.paramEquals(CodedValues.TPC_VALIDA_EXP_SENHA_SER_ACESSO_SIST, CodedValues.TPC_SIM, responsavel);
            if (verificaSeSenhaExpirada && expirada) {
                session.setAttribute("AlterarSenha", "1");
            }

            // Verifica se o usuário precisa aceitar o termo de uso.
            session.removeAttribute("AceitarTermoDeUso");
            final Object paramAceitacaoTermoDeUso = ParamSist.getInstance().getParam(CodedValues.TPC_DATA_TERMO_DE_USO_SER, usuAcesso);
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
                        } else if((session.getAttribute("termo_usu") != null) && (Boolean) session.getAttribute("termo_usu")) {
                            session.setAttribute("AceitarTermoDeUso", "1");
                        }
                    }
                } catch (final java.text.ParseException ex) {
                    // Formato do parâmetro inválido
                    LOG.error(ex.getMessage(), ex);
                }
            }

            // Verifica se o usuário precisa aceitar a política de privacidade.
            session.removeAttribute("AceitarPoliticaPrivacidade");
            final Object paramAceitacaoPoliticaPrivacidade = ParamSist.getInstance().getParam(CodedValues.TPC_DATA_POLITICA_PRIVACIDADE_SER, usuAcesso);
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

            final String tipo = "SER";

            session.removeAttribute("msg");

            // Armazena na sessão o objeto AcessoSistema para este usuário
            // com as informações sobre o usuário (usuCodigo, ip, tipo, entidade)
            usuAcesso.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
            usuAcesso.setNomeEntidade(servidor.getAttribute(Columns.SER_NOME).toString());
            usuAcesso.setCodigoEntidade(!TextHelper.isNull(usuario.getAttribute(Columns.USE_SER_CODIGO)) ? usuario.getAttribute(Columns.USE_SER_CODIGO).toString() : usuario.getAttribute(Columns.SER_CODIGO).toString());
            usuAcesso.setUsuNome(servidor.getAttribute(Columns.SER_NOME).toString());
            usuAcesso.setUsuEmail((String) servidor.getAttribute(Columns.SER_EMAIL));
            usuAcesso.setUsuLogin(usuario.getAttribute(Columns.USU_LOGIN).toString());
            usuAcesso.setDeficienteVisual("S".equals(loginDefVisual));

            // Se exige cadastro de email do servidor no primeiro acesso ao sistema, verifica se é o primeiro acesso do servidor
            final boolean exigeAtualizacaoDadosSerPrimeiroAcesso = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, AcessoSistema.getAcessoUsuarioSistema());
            final boolean exigeReconhecimentoFacialPrimeiroAcesso = ParamSist.getBoolParamSist(CodedValues.TPC_RECONHECIMENTO_FACIAL_ACESSO_SERVIDOR, AcessoSistema.getAcessoUsuarioSistema());

            session.removeAttribute("ExigeEmailOuTelefone");

            if (exigeAtualizacaoDadosSerPrimeiroAcesso) {
                final String serCodigo = usuAcesso.getCodigoEntidade();
                final String tocCodigo = CodedValues.TOC_DIVERGENCIA_CADASTRO_EMAIL_SERVIDOR;

                final String email = (String) servidor.getAttribute(Columns.SER_EMAIL);
                final Timestamp emailValidado = (Timestamp) servidor.getAttribute(Columns.SER_DATA_VALIDACAO_EMAIL);
                final List<TransferObject> listaOcorrenciaEmailIncorretoServidor = servidorController.lstDataOcorrenciaServidor(serCodigo, tocCodigo, responsavel);

                boolean servidorDentroDoPrazoSemValidacaoEmail = false;
                final String valorParamAlteraEmail = (String) servidor.getAttribute(Columns.SER_PERMITE_ALTERAR_EMAIL);
                final boolean permiteAltEmail = valorParamAlteraEmail.equalsIgnoreCase("S") ? true : false;

                //Se o servidor teve uma ocorrencia de email incorreto
                if (!listaOcorrenciaEmailIncorretoServidor.isEmpty() && !TextHelper.isNull(email) && !permiteAltEmail && TextHelper.isNull(emailValidado)) {
                    final Date dataOcorrencia = (Date) listaOcorrenciaEmailIncorretoServidor.get(0).getAttribute(Columns.OCS_DATA);
                    servidorDentroDoPrazoSemValidacaoEmail = servidorController.validaServidorDentroPrazoAcessoSistemaSemValidacaoEmail(usuAcesso.getSerCodigo(), dataOcorrencia, responsavel);
                }

                if (!servidorDentroDoPrazoSemValidacaoEmail) {
                    final boolean primeiroAcesso = TextHelper.isNull(usuario.getAttribute(Columns.USU_DATA_ULT_ACESSO));

                    usuAcesso.setPrimeiroAcesso(primeiroAcesso);
                    if (primeiroAcesso || TextHelper.isNull(emailValidado)) {
                        // Verifica se necessário atualizar dados de email e telefone
                        final String telefone = (String) servidor.getAttribute(Columns.SER_TEL);
                        final String celular = (String) servidor.getAttribute(Columns.SER_CELULAR);

                        if (exigeAtualizacaoDadosSerPrimeiroAcesso
                                && ((ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel) && TextHelper.isNull(email))
                                || (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel) && TextHelper.isNull(emailValidado)))
                                || (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_TELEFONE, responsavel) && TextHelper.isNull(telefone))
                                || (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CELULAR, responsavel) && TextHelper.isNull(celular))
                                ) {
                            session.setAttribute("ExigeEmailOuTelefone", "1");
                        }
                    }
                }
            }

            if(exigeReconhecimentoFacialPrimeiroAcesso) {
                boolean imagemRostoFrontal = false;
                boolean imagemRostoPerfilDireito = false;
                boolean imagemRostoPerfilEsquerdo = false;
                List<String> tarCodigos = List.of(TipoArquivoEnum.ARQUIVO_RECONHECIMENTO_FACIAL_FRONTAL_SERVIDOR.getCodigo(),
                                                  TipoArquivoEnum.ARQUIVO_RECONHECIMENTO_FACIAL_PERFIL_DIREITO_SERVIDOR.getCodigo(),
                                                  TipoArquivoEnum.ARQUIVO_RECONHECIMENTO_FACIAL_PERFIL_ESQUERDO_SERVIDOR.getCodigo()
                                                  );
                String serCodigo = (String) servidor.getAttribute(Columns.SER_CODIGO);
                List<TransferObject> imagensReconhecimentoFacial = arquivoController.listArquivoServidor(serCodigo, tarCodigos, responsavel);
                for(TransferObject imagem : imagensReconhecimentoFacial) {
                  if(imagem.getAttribute(Columns.TAR_CODIGO).equals(tarCodigos.get(0))) {
                      imagemRostoFrontal = true;
                  }else if(imagem.getAttribute(Columns.TAR_CODIGO).equals(tarCodigos.get(1))) {
                      imagemRostoPerfilDireito = true;
                  }else if(imagem.getAttribute(Columns.TAR_CODIGO).equals(tarCodigos.get(2))) {
                      imagemRostoPerfilEsquerdo = true;
                  }
                }
                if(!(imagemRostoFrontal && imagemRostoPerfilDireito && imagemRostoPerfilEsquerdo)) {
                    session.setAttribute("ExigeReconhecimentoFacialPrimeiroAcesso", "1");
                }
            }

            // Se exige a autorização do servidor para desconto parcial ao acessar ao sistema verifica se ele já permitiu
            final boolean exigeAutorizacaoDescontoParcialServidor = !ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_AUTORIZA_DESCONTO_PARCIAL, CodedValues.TPC_NAO, responsavel);

            session.removeAttribute("AutorizaDescontoParcialSer");

            if (exigeAutorizacaoDescontoParcialServidor) {
                final String serCodigo = usuAcesso.getCodigoEntidade();
                final String tocCodigoAutDescontoParcial = CodedValues.TOC_SER_AUTORIZA_DESC_PARCIAL;
                final String tocCodigoNaoAutDescontoParcial = CodedValues.TOC_SER_NAO_AUTORIZA_DESC_PARCIAL;

                final List<TransferObject> listaOcorrenciaSerAutorizaDesc = servidorController.lstDataOcorrenciaServidor(serCodigo, tocCodigoAutDescontoParcial, responsavel);
                final List<TransferObject> listaOcorrenciaSerNaoAutorizaDesc = servidorController.lstDataOcorrenciaServidor(serCodigo, tocCodigoNaoAutDescontoParcial, responsavel);

                if ((listaOcorrenciaSerAutorizaDesc != null) && (listaOcorrenciaSerNaoAutorizaDesc != null) && listaOcorrenciaSerAutorizaDesc.isEmpty() && listaOcorrenciaSerNaoAutorizaDesc.isEmpty()) {
                    session.setAttribute("AutorizaDescontoParcialSer","1");
                }
            }

            usuAcesso.setDadosServidor(servidor.getAttribute(Columns.EST_CODIGO).toString(), servidor.getAttribute(Columns.ORG_CODIGO).toString(), servidor.getAttribute(Columns.RSE_CODIGO).toString(),
                    servidor.getAttribute(Columns.RSE_MATRICULA).toString(),
                    (String) servidor.getAttribute(Columns.SER_CPF),
                    (String) servidor.getAttribute(Columns.SER_EMAIL),
                    servidor.getAttribute(Columns.RSE_PRAZO) != null ? servidor.getAttribute(Columns.RSE_PRAZO).toString() : null,
                    servidor.getAttribute(Columns.SRS_CODIGO).toString());
            session.setAttribute(AcessoSistema.SESSION_ATTR_NAME, usuAcesso);

            // Busca as permissões do usuário
            usuAcesso.setPermissoes(usuarioController.selectFuncoes(usuario.getAttribute(Columns.USU_CODIGO).toString(), (String) usuario.getAttribute(Columns.USE_SER_CODIGO), tipo, usuAcesso));

            // Busca o menu do usuário
            final List<MenuTO> mnuLst = menuController.obterMenu(usuAcesso);
            usuAcesso.setMenu(mnuLst);

            verificarAcessoMenuDropDown(session, usuAcesso, mnuLst);

            // Busca parametro de timeout dependendo do tipo do usuario
            int timeout = CodedValues.TEMPO_DEFAULT_EXPIRACAO_SESSAO;
            final ParamSist paramSist = ParamSist.getInstance();
            if (usuAcesso.isSer()) {
                final Object objTimeout = paramSist.getParam(CodedValues.TPC_TEMPO_EXPIRACAO_SESSAO_SER, AcessoSistema.getAcessoUsuarioSistema());
                try {
                    timeout = objTimeout != null ? Integer.parseInt(objTimeout.toString()) : timeout;
                } catch (final NumberFormatException ex) {
                    //Caso o parametro esta preenchido errado usa o valor default
                    timeout = CodedValues.TEMPO_DEFAULT_EXPIRACAO_SESSAO;
                }
            }

            // Qualquer valor igual ou menor que zero será considerado 20 minutos
            // Tempo máximo de timeout é de 20 minutos
            if ((timeout < 1) || (timeout > CodedValues.TEMPO_MAXIMO_EXPIRACAO_SESSAO)) {
                timeout = CodedValues.TEMPO_DEFAULT_EXPIRACAO_SESSAO;
            }

            //Tempo máximo inativo é em segundos
            session.setMaxInactiveInterval(timeout * 60);

            // Se exige cadastro de e-mail no primeiro acesso e o usuário irá alterar a senha,
            // a data de último acesso ao sistema será atualiza na confirmação do e-mail
            if ((!exigeAtualizacaoDadosSerPrimeiroAcesso || !usuAcesso.isPrimeiroAcesso() || (session.getAttribute("AlterarSenha") == null) || !session.getAttribute("AlterarSenha").equals("1"))) {
                // Seta data de última data de acesso ao sistema
                usuarioController.alteraDataUltimoAcessoSistema(usuAcesso);
            }

            // Valida acesso simultâneo
            sessionManagment.validateNewSession(usuario.getAttribute(Columns.USU_CODIGO).toString(), session.getId());

            // Grava log de login sucesso
            final LogDelegate log = new LogDelegate(usuAcesso, Log.SISTEMA, Log.LOGIN, Log.LOG_LOGIN_SUCESSO);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.user.agent.arg0", responsavel, request.getHeader("user-agent")));
            log.write();

            // Verifica se usuario deve confirmar leitura de alguma mensagem
            session.removeAttribute("mensagem_sem_leitura");
            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.USU_DATA_CAD, usuario.getAttribute(Columns.USU_DATA_CAD));

            final Integer semLeitura = mensagemController.countMensagemUsuarioSemLeitura(criterio, usuAcesso);
            if (semLeitura.intValue() > 0) {
                session.setAttribute("mensagem_sem_leitura", semLeitura);
            }

            session.setAttribute("usu_data_cad", usuario.getAttribute(Columns.USU_DATA_CAD));

            // Verifica se usuario utiliza certificado digital
            session.removeAttribute("valida_certificado_digital");
            if (UsuarioHelper.isUsuarioCertificadoDigital(usuLogin, (String) usuario.getAttribute(Columns.USU_EXIGE_CERTIFICADO), AcessoSistema.ENTIDADE_SER, null, usuAcesso)) {
                session.setAttribute("valida_certificado_digital", "true");
            }

            // Se houver leilão finalizado sem contato e os dados do servidor ainda não tenham sido enviados por e-mail à CSA vencedora, então o servidor deverá informar os dados para continuar
            session.removeAttribute("LeilaoFinalizadoSemContato");
            if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, AcessoSistema.getAcessoUsuarioSistema())) {
                final boolean enviaEmailCsaVencedoraLeilao = ParamSist.getBoolParamSist(CodedValues.TPC_ENVIAR_EMAIL_CONSIGNATARIA_VENCEDORA_DO_LEILAO, responsavel);
                if ((leilaoSolicitacaoController.contarLeilaoFinalizadoSemContato(usuAcesso) > 0) && !enviaEmailCsaVencedoraLeilao) {
                    session.setAttribute("LeilaoFinalizadoSemContato", "1");
                }
            }

            // Recupera a quantidade de registro servidor com o mesmo ser_codigo
            int qtdeRegistroServidor = 0;
            if(ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_LOGIN_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                try {
                    if (!TextHelper.isNull(usuAcesso.getCodigoEntidade())) {
                        qtdeRegistroServidor = servidorController.countRegistroServidorSerCodigo(usuAcesso.getCodigoEntidade(), false, responsavel);
                    }
                } catch (final ServidorControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }
            session.setAttribute("qtdeRegistroServidor", qtdeRegistroServidor);

            // DESENV-15765: Removendo da sessão estas informações.
            if (alteraLoginServidor) {
                session.removeAttribute("codigo_estabelecimento");
                session.removeAttribute("estabelecimento");
                session.removeAttribute("codigo_orgao");
                session.removeAttribute("orgao");
                session.removeAttribute("rse_matricula");
            }

            SynchronizerToken.saveToken(request);
            response.addCookie(new Cookie("LOGIN", "SERVIDOR"));

            //Para validar se não houve tentativa de copiar o id da sessão e logar em outro navegador, previsamos armazenar na sessão o User-Agent
            session.setAttribute("userAgentLogin", request.getHeader("user-agent"));

            if (temCookieAcessoServidorMobileOauth(request)) {
            	final StringBuilder urlMobileOauth = new StringBuilder("../v3/autenticarMobileOAuth?acao=login");
            	urlMobileOauth.append("&id=").append(loginComCPF ? serCpf : matricula);
            	urlMobileOauth.append("&cpf=").append(serCpf);
            	urlMobileOauth.append("&orgCodigo=").append(loginComEstOrg ? orgCodigo : "");
            	urlMobileOauth.append("&estCodigo=").append(!loginComEstOrg ? estCodigo : "");

                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(urlMobileOauth.toString(), request)));
                return "jsp/redirecionador/redirecionar";
            }

            int tpcDias = ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_SER, 0, usuAcesso);

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

            if (!TextHelper.isNull(formularioPesquisaController.verificaFormularioParaResponder(usuAcesso.getUsuCodigo(), responsavel))) {
                return "forward:/v3/formularioResposta?acao=responder&" + SynchronizerToken.generateToken4URL(request);
            }


            // Redireciona para a principal do novo leiaute
            return "redirect:/v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";

        } catch (final Exception ex) {
            exc = ex;
        }

        if (exc.getClass().equals(UsuarioControllerException.class)) {
            if ("mensagem.senha.servidor.consulta.invalida".equals(((UsuarioControllerException) exc).getMessageKey())) {
                session.setAttribute(CodedValues.MSG_ERRO, msgErroLoginInvalido);
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, exc.getMessage());
            }
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            LOG.error(exc.getMessage(), exc);
        }

        if (usuario == null) {
            usuAcesso = JspHelper.getAcessoSistema(request);
        } else {
            usuAcesso = new AcessoSistema(usuario.getAttribute(Columns.USU_CODIGO).toString(), JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
        }
        final LogDelegate log = new LogDelegate(usuAcesso, Log.SISTEMA, Log.LOGIN, Log.LOG_LOGIN_ERRO);
        try {
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.matricula.arg0", responsavel, matricula));
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.erro.arg0", responsavel, session.getAttribute(CodedValues.MSG_ERRO).toString()));
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.user.agent.arg0", responsavel, request.getHeader("user-agent")));
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        //return viewRedirect(telaLogin,  request, session, model, responsavel);
        return iniciar(request, response, session, model);
    }

    @RequestMapping(value = { "/v3/autenticarMobileOAuth" })
    public String iniciarOauth(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException {
        final String iniciar = iniciar(request, response, session, model);

        response.addCookie(new Cookie("MOBILE", "TRUE"));

		return iniciar;
    }

    @RequestMapping(value = { "/v3/autenticarMobileOAuth" }, params = { "acao=login" })
    public String retornaJson(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, AcessoSistema responsavel) throws IOException {
        final UsuarioRestRequest usuarioRequest = new UsuarioRestRequest();
        usuarioRequest.id = request.getParameter("id");
        usuarioRequest.cpf = request.getParameter("cpf");
        usuarioRequest.orgCodigo = request.getParameter("orgCodigo");
        usuarioRequest.estCodigo = request.getParameter("estCodigo");

        final Response resposta = usuService.loginMobileOauth(usuarioRequest, request, responsavel);

        final String jsonObject = new Gson().toJson(resposta.getEntity());
        model.addAttribute("json", jsonObject);

        return "jsp/oauth2/OAuth2Handler";
    }

	@RequestMapping(value = { "/v3/autenticar" }, params = { "acao=iniciarAlteracaoLoginSer" })
    public String inicarAlteracaoLoginServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        List<TransferObject> servidores = (List<TransferObject>) session.getAttribute("servidores");

        // Verifica se o sistema permite a alteração do login do servidor (registro funcional) sem sair do sistema
        final boolean permiteAlterarLogin = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_LOGIN_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        if (!permiteAlterarLogin) {
            // registra log de segurança
            try {
                final LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.LOGIN, Log.LOG_ERRO_SEGURANCA);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.erro.permissao.acesso.alteracao.registro.funcional", responsavel));
                log.write();
            } catch (final com.zetra.econsig.exception.LogControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (servidores == null) {
            try {
                servidores = new ArrayList<>();
                if (!TextHelper.isNull(responsavel.getSerCodigo())) {
                    servidores.addAll(servidorController.lstRegistroServidorSerCodigo(responsavel.getSerCodigo(), false, responsavel));
                    if (servidores.size() > 1) {
                        session.setAttribute("servidores", servidores);
                    } else {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        return viewRedirect("jsp/autenticarServidor/alterarLoginServidor", request, session, model, responsavel);
    }

    @RequestMapping(value = { "/v3/autenticar" }, params = { "acao=alterarLoginSer" })
    public String alterarLoginSer(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String serCodigoSel = JspHelper.verificaVarQryStr(request, "ser_codigo_sel");
        final String usernameSel = JspHelper.verificaVarQryStr(request, "username_sel");
        final String orgIdSel = JspHelper.verificaVarQryStr(request, "org_id_sel");
        final String orgCodigoSel = JspHelper.verificaVarQryStr(request, "org_codigo_sel");
        final String estIdSel = JspHelper.verificaVarQryStr(request, "est_id_sel");
        final String estCodigoSel = JspHelper.verificaVarQryStr(request, "est_codigo_sel");
        final String serCpfSel = JspHelper.verificaVarQryStr(request, "ser_cpf_sel");
        final String rseMatriculaSel = JspHelper.verificaVarQryStr(request, "rse_matricula_sel");
        // verifica se o ser_codigo informado corresponde ao ser_codigo do usuário logado no sistema
        if (!serCodigoSel.equals(responsavel.getSerCodigo()) || TextHelper.isNull(usernameSel) || TextHelper.isNull(orgIdSel) || TextHelper.isNull(orgCodigoSel)) {
            // registra log de segurança
            try {
                final LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.LOGIN, Log.LOG_ERRO_SEGURANCA);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.erro.tentativa.alteracao.registro.funcional", responsavel));
                log.write();
            } catch (final com.zetra.econsig.exception.LogControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            final String telaLogin = LoginHelper.getPaginaLoginServidor();
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(telaLogin, request)));
        } else {
            // indica para outros programas que está sendo realizada uma troca de login do mesmo servidor
            session.setAttribute("altera_login_servidor", "true");
            session.setAttribute("ser_codigo_login_original", responsavel.getSerCodigo());
            final String link = "../v3/autenticar?acao=autenticar&username=" + usernameSel + "&codigo_orgao=" + orgCodigoSel + "&orgao=" + orgIdSel + "&codigo_estabelecimento=" + estCodigoSel + "&estabelecimento=" + estIdSel + "&serCpf=" + serCpfSel +"&rseMatricula="+rseMatriculaSel;
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
        }

        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(value = { "/v3/autenticar" }, params = { "acao=selecionarServidor" })
    public String selecionarServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String loginExterno = (String) session.getAttribute("serLogin");
        final List<TransferObject> servidores = (List<TransferObject>) session.getAttribute("servidores");
        if ((servidores == null) || (loginExterno == null)) {
            // mostra mensagem de erro para voltar para a tela de login.
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/autenticarServidor/selecionarServidor", request, session, model, responsavel);
    }

    private String validarContraChave(String url) throws UsuarioControllerException {
        HttpUriRequest metodo = null;
        InputStream inputStream = null;
        HttpResponse response = null;
        String resultado = null;

        try {
            // Cria o cliente HTTP e o método POST para validação da senha
            final HttpClient client = HttpHelper.getHttpClient(null, null);
            metodo = new HttpGet(url);
            response = client.execute(metodo);

            final int statusCode = response.getStatusLine().getStatusCode();
            // Input Stream para receber o resultado da requisição
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                inputStream = entity.getContent();
                ByteArrayOutputStream out = null;
                if (inputStream != null) {
                    // Grava o resultado em um buffer
                    out = new ByteArrayOutputStream();
                    int c = -1;
                    int count = 0;
                    while (((c = inputStream.read()) != -1) && (count++ < 1024)) {
                        out.write(c);
                    }
                }
                resultado = (out == null) || TextHelper.isNull(out.toString().trim()) ? "Nenhuma resposta no corpo do HTTP foi enviada pelo sistema remoto." : out.toString().trim();
                LOG.debug("URI:" + metodo.getURI() + "\tStatus da resposta: " + statusCode + "   Dados da resposta:|" + URLDecoder.decode(resultado, "iso-8859-1") + "|");
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.sessao.invalida", AcessoSistema.getAcessoUsuarioSistema());
        } finally {
            try {
                if (metodo != null) {
                    metodo.abort();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        return resultado;
    }

    public boolean validarContraChaveGetUnimed(String url, AcessoSistema responsavel) {
        final RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(responsavel);

        final HttpHeaders headers = new HttpHeaders();
        headers.set(ParamSenhaExternaEnum.X_API_KEY.getChave(), ParamSenhaExternaEnum.X_API_KEY.getValor());
        headers.set(ParamSenhaExternaEnum.USER_AGENT.getChave(), ParamSenhaExternaEnum.USER_AGENT.getValor());

        final org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            LOG.debug("Resposta JSON: " + response.getBody());
            return true;
        } else {
            LOG.error("Requisição falhou. Código de resposta: " + response.getStatusCode());
            return false;
        }
    }

    private String getTextoAutenticacaoServidor(AcessoSistema responsavel) throws ViewHelperException {

        String mensagemTelaLoginServidor = null;
        File arqMensagem = null;

        String absolutePath = null;
        final String autenticacaoServidor = "autenticacao_servidor.msg";

        absolutePath = ParamSist.getDiretorioRaizArquivos();
        absolutePath += File.separatorChar + "autenticacao_servidor" + File.separatorChar + autenticacaoServidor;

        arqMensagem = new File(absolutePath);
        if (!arqMensagem.exists()) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.texto.arquivo.autenticacao.servidor.nao.encontrado", responsavel, absolutePath));
            throw new ViewHelperException("mensagem.erro.interno.texto.arquivo.autenticacao.servidor.nao.encontrado", responsavel);
        }

        mensagemTelaLoginServidor = FileHelper.readAll(absolutePath);

        return mensagemTelaLoginServidor;
    }

    private static boolean temCookieAcessoServidorMobileOauth(HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals("MOBILE") && cookie.getValue().equals("TRUE")) {
                    return true;
                }
            }
        }
        return false;
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
}
