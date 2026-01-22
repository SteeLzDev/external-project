package com.zetra.econsig.web.controller.sistema;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rede.HttpHelper;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.AcessoRecurso;
import com.zetra.econsig.persistence.entity.Funcao;
import com.zetra.econsig.persistence.entity.ItemMenu;
import com.zetra.econsig.persistence.entity.OperacaoNaoConfirmada;
import com.zetra.econsig.persistence.entity.Papel;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AutorizarOperacoesFilaWebController</p>
 * <p>Description: Controller para autorização de operação sensíveis em fila.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 28658 $
 * $Date: 2020-01-17 13:08:04 -0300 (sex, 17 jan 2020) $
 */
@Controller
@RequestMapping(value = { "/v3/autorizarOperacoesFila" })
public class AutorizarOperacoesFilaWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutorizarOperacoesFilaWebController.class);

    @Value("${server.servlet.session.cookie.name}")
    private String sessionCookieName;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired(required = false)
    private SessionRepository<org.springframework.session.Session> springSessionRepository;

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=listarOpAutorizacao" })
    public String listarOperacoesFilaAutorizacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            model.addAttribute("lstFilaOperacoes", sistemaController.listarFilaOperacao(responsavel));
            return viewRedirect("jsp/autorizarOperacao/listarOperacoesFilaAutorizacao", request, session, model, responsavel);
        } catch (final ConsignanteControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=resolverOperacoes" })
    public String resolverOperacoes(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String[] oncADescartar = request.getParameterValues("chkDescartar");
        final String obsDescarte = request.getParameter("obsUsuarioDescarte");

        final String[] oncConfirmar = request.getParameterValues("chkConfirmar");

        String sessionId = session.getId();
        for (final Cookie cookie : request.getCookies()) {
            if (sessionCookieName.equalsIgnoreCase(cookie.getName())) {
                sessionId = cookie.getValue();
                break;
            }
        }

        // DESENV-20032 : Se o parâmetro de sistema diz que a URL do sistema começa com https e a URL de acesso não,
        // então substitui o http por https pois provavelmente o Apache na frente do sistema está mascarando a URL
        String urlSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);
        String requestUrl = request.getRequestURL().toString();
        if (urlSistema != null && urlSistema.startsWith("https://") && !requestUrl.startsWith("https://")) {
            requestUrl = requestUrl.replaceFirst("^[a-z]+://", "https://");
        }

        final Map<HttpHelper.SessionKeysEnum, String> sessionConfig = new HashMap<>();
        sessionConfig.put(HttpHelper.SessionKeysEnum.CONTEXT_PATH, session.getServletContext().getContextPath());
        sessionConfig.put(HttpHelper.SessionKeysEnum.SESSION_ID, sessionId);
        sessionConfig.put(HttpHelper.SessionKeysEnum.SESSION_COOKIE_NAME, sessionCookieName);
        sessionConfig.put(HttpHelper.SessionKeysEnum.REQUEST_URL, requestUrl.substring(0, requestUrl.indexOf("/v3")));
        sessionConfig.put(HttpHelper.SessionKeysEnum.REQUEST_USER_AGENT, request.getHeader("user-agent"));

        if (oncADescartar != null) {
            for (final String opDescarte: oncADescartar) {
                try {
                    sistemaController.descartarOpFilaAutorizacao(opDescarte, obsDescarte, sessionConfig, responsavel);
                } catch (final ConsignanteControllerException e) {
                    session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                }
            }
        }

        // DESENV-19802 : Obtém a interface do spring para gerenciamento de sessão, pois a sessão só é atualizada no Redis
        // quando a reposta é enviada ao usuário, e neste cenário, é feito uma requisição para confirmar a operação antes
        // do envio da resposta ao usuário. Portanto fazer "session.setAttribute" é inócuo.
        final org.springframework.session.Session springSession = (springSessionRepository == null) ? null : springSessionRepository.findById(session.getId());

        boolean temErro = false;
        if (oncConfirmar != null) {
            try {
                for (final String oncCodigo: oncConfirmar) {
                    try {
                        // Atualiza o token na sessão para um novo
                        SynchronizerToken.saveToken(request);
                        // Repassa o token gerado para ser enviado como parâmetro de requisição
                        sessionConfig.put(HttpHelper.SessionKeysEnum.REQUEST_TOKEN, SynchronizerToken.generateToken4URL(request));
                        // Se a sessão é gerenciada pelo spring, salva o token na sessão do spring
                        if (springSession != null) {
                            springSession.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                        }

                        final OperacaoNaoConfirmada oncConfirmacao = sistemaController.findOperacaoNaoConfirmada(oncCodigo, responsavel);

                        final String usuCodigo = oncConfirmacao.getUsuario().getUsuCodigo();
                        final AcessoSistema respExecutor = AcessoSistema.recuperaAcessoSistema(usuCodigo, oncConfirmacao.getOncIpAcesso(), responsavel.getPortaLogicaUsuario());
                        final AcessoRecurso recursoBanco = sistemaController.findAcessoRecurso(oncConfirmacao.getAcessoRecurso().getAcrCodigo(), responsavel);
                        final Funcao funcao = usuarioController.findFuncao(recursoBanco.getFuncao().getFunCodigo(), responsavel);
                        final CustomTransferObject filtro = new CustomTransferObject();
                        filtro.setAttribute(Columns.USU_CODIGO, usuCodigo);
                        final List<TransferObject> lstUsuarios = usuarioController.listUsuarios(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), filtro, -1, -1, responsavel);
                        final TransferObject usuExecutor = lstUsuarios.get(0);
                        respExecutor.setPermissoes(usuarioController.selectFuncoes(usuCodigo, (String) usuExecutor.getAttribute("CODIGO_ENTIDADE"), (String) usuExecutor.getAttribute("TIPO"), responsavel));
                        respExecutor.setPermissaoUnidadesEdt(usuarioController.unidadesPermissaoEdtUsuario(usuCodigo, responsavel));

                        final ItemMenu itmMenu = recursoBanco.getItemMenu();
                        final Papel papel = recursoBanco.getPapel();
                        final Funcao funObject = recursoBanco.getFuncao();
                        final String funExigeTmo = funcao.getFunExigeTmo();
                        final String funRestritaNca = funcao.getFunRestritaNca();
                        final String funPermBloq = funcao.getFunPermiteBloqueio();

                        final AcessoRecursoHelper.AcessoRecurso recursoAcessado = new AcessoRecursoHelper.AcessoRecurso(recursoBanco.getAcrCodigo(), recursoBanco.getAcrRecurso(), recursoBanco.getAcrParametro(),
                                recursoBanco.getAcrOperacao(), recursoBanco.getAcrAtivo() == 1,
                                "S".equals(recursoBanco.getAcrSessao()), "S".equals(recursoBanco.getAcrBloqueio()),
                                "S".equals(recursoBanco.getAcrFimFluxo()), recursoBanco.getAcrMetodoHttp(),
                                itmMenu != null ? itmMenu.getItmCodigo() : null, papel != null ? papel.getPapCodigo() : null,
                                        funObject != null ? funObject.getFunCodigo() : null, funcao.getFunDescricao(), !TextHelper.isNull(funPermBloq) && "S".equals(funPermBloq)
                                                , !TextHelper.isNull(funExigeTmo) && "S".equals(funExigeTmo), !TextHelper.isNull(funRestritaNca) && "S".equals(funRestritaNca), funcao.getFunExigeSegundaSenhaSup(), funcao.getFunExigeSegundaSenhaCse(),
                                                funcao.getFunExigeSegundaSenhaOrg(), funcao.getFunExigeSegundaSenhaCsa(), funcao.getFunExigeSegundaSenhaCor(), funcao.getFunExigeSegundaSenhaSer(), false, null);
                        respExecutor.setRecursoAcessado(recursoAcessado);
                        if (springSession != null) {
                            springSession.setAttribute(AcessoSistema.SESSION_ATTR_NAME, respExecutor);
                            springSession.setAttribute(AcessoSistema.OPERACAO_FILA_AUTORIZADA, Boolean.TRUE);
                            springSession.setAttribute("_recurso_acessado_", recursoAcessado);
                        } else {
                            session.setAttribute(AcessoSistema.SESSION_ATTR_NAME, respExecutor);
                            session.setAttribute(AcessoSistema.OPERACAO_FILA_AUTORIZADA, Boolean.TRUE);
                            session.setAttribute("_recurso_acessado_", recursoAcessado);
                        }
                        final JsonReader jsonReader = Json.createReader(new StringReader(oncConfirmacao.getOncParametros().trim()));
                        final JsonObject paramsRequisicao = jsonReader.readObject();
                        final JsonArray queryCaptchaArray = paramsRequisicao.getJsonObject(HttpHelper.REQUEST_QUERY_STRING_JSON) != null ? paramsRequisicao.getJsonObject(HttpHelper.REQUEST_QUERY_STRING_JSON).getJsonArray(JspHelper.CAPTCHA_FIELD) : null;
                        if (queryCaptchaArray != null) {
                            final JsonString captchaValue = queryCaptchaArray.getJsonString(1);

                            if (springSession != null) {
                                springSession.setAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY, captchaValue.toString().replace("\"", "").replace("[", "").replace("]", ""));
                            } else {
                                session.setAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY, captchaValue.toString().replace("\"", "").replace("[", "").replace("]", ""));
                            }
                        } else {
                            final JsonArray multiPartCaptcha = paramsRequisicao.getJsonObject(HttpHelper.MULTI_PARTPARAM_REQUEST_PARAMS) != null ? paramsRequisicao.getJsonObject(HttpHelper.MULTI_PARTPARAM_REQUEST_PARAMS).getJsonArray(JspHelper.CAPTCHA_FIELD) : null;

                            if (multiPartCaptcha != null) {
                                final JsonString captchaValue = multiPartCaptcha.getJsonString(1);

                                if (springSession != null) {
                                    springSession.setAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY, captchaValue.toString().replace("\"", "").replace("[", "").replace("]", ""));
                                } else {
                                    session.setAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY, captchaValue.toString().replace("\"", "").replace("[", "").replace("]", ""));
                                }
                            }
                        }

                        // DESENV-19802 :força a atualização da sessão no repositório do Redis
                        if (springSession != null) {
                            springSessionRepository.save(springSession);
                        }

                        sistemaController.confirmarOperacaoFila(oncCodigo, sessionConfig, responsavel);

                    } catch (final ZetraException e) {
                        LOG.error(e.getMessage(), e);
                        session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                        temErro = true;
                    }
                }
            } finally {
                if (springSession != null) {
                    springSession.setAttribute(AcessoSistema.SESSION_ATTR_NAME, responsavel);
                    springSession.removeAttribute(AcessoSistema.OPERACAO_FILA_AUTORIZADA);
                    springSessionRepository.save(springSession);

                } else {
                    session.setAttribute(AcessoSistema.SESSION_ATTR_NAME, responsavel);
                    session.removeAttribute(AcessoSistema.OPERACAO_FILA_AUTORIZADA);
                }
            }
        }

        if (!temErro) {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.info.fila.op.resolvidas.sucesso", responsavel));
        }

        // Atualiza o token na sessão para um novo
        SynchronizerToken.saveToken(request);

        return listarOperacoesFilaAutorizacao(request, response, session, model);
    }
}
