package com.zetra.econsig.webservice.rest.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Priority;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.cache.TransferObjectCache;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.entity.UsuarioChaveSessao;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webclient.sso.SSOClient;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AuthenticationFilter.class);

    private SSOClient ssoClient;

    @Context
    private HttpServletRequest sr;


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the HTTP Authorization header from the request
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();
        UsuarioChaveSessao chave = null;

        // Verifica se o sistema está disponível (Não bloqueado)
        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        Short status = null;
        try {
            status = cseDelegate.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, null);
        } catch (ConsignanteControllerException e1) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", null);
            requestContext.abortWith(
                    Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).build());
            return;
        }

        if (!status.equals(CodedValues.STS_ATIVO)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("rotulo.sistema.indisponivel", null);
            requestContext.abortWith(
                    Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).build());
            return;
        }

        UsuarioDelegate usuDelegate = null;
        TransferObject usuRequesting = null;
        try {
            boolean isSsoToken = (!TextHelper.isNull(requestContext.getHeaderString("sso_token")) && requestContext.getHeaderString("sso_token").equals("true")) ;
            // Validate the token
            if (!isSsoToken) {
                chave = validateToken(token);
            } else {
                usuDelegate = new UsuarioDelegate();
                String username = requestContext.getHeaderString(SSOClient.USERNAME_PARAM_NAME);
                String clientId = requestContext.getHeaderString(SSOClient.CLIENT_ID_PARAM_NAME);

                if (TextHelper.isNull(username) || TextHelper.isNull(token)) {
                    ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", AcessoSistema.getAcessoUsuarioSistema());
                    requestContext.abortWith(
                            Response.status(Response.Status.UNAUTHORIZED).entity(responseError).build());
                    return;
                }

                TransferObject dadosUsuCache = TransferObjectCache.getInstance().getTransferObject(username).orElse(null);

                List<TransferObject> usuList = dadosUsuCache == null ? usuDelegate.findUsuarioByEmail(username, AcessoSistema.getAcessoUsuarioSistema()) : List.of(dadosUsuCache);
                if (usuList == null || usuList.isEmpty() || usuList.size() > 1) {
                    ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", AcessoSistema.getAcessoUsuarioSistema());
                    requestContext.abortWith(
                            Response.status(Response.Status.UNAUTHORIZED).entity(responseError).build());
                    return;
                }

                usuRequesting = usuList.get(0);

                //DESENV-22675: validação de IP/DNS de acesso
                String ipAValidar = JspHelper.getRemoteAddr(sr);
                UsuarioHelper.verificarIpDDNSAcesso(UsuarioHelper.obterTipoEntidade(usuRequesting), (String) usuRequesting.getAttribute("COD_ENTIDADE"), !TextHelper.isNull(ipAValidar) ? ipAValidar : sr.getRemoteHost(),
                   (String) usuRequesting.getAttribute(Columns.USU_IP_ACESSO), (String) usuRequesting.getAttribute(Columns.USU_DDNS_ACESSO),
                   (String) usuRequesting.getAttribute(Columns.USU_CODIGO), AcessoSistema.getAcessoUsuarioSistema());

                //DESENV-20923: dados de usuário suporte colocados em cache para evitar busca anterior, que tem custo computacional
                //              é feito só para dados de suporte. analisar no futuro para servidores, pois pode ocorrer estouro de memória pra estes.
                if (dadosUsuCache == null && !TextHelper.isNull(usuRequesting.getAttribute(Columns.USP_CSE_CODIGO))) {
                    TransferObjectCache.getInstance().setTransferObjectIfAbsent(username, usuRequesting);
                }

                if (ssoClient == null) {
                    ssoClient = ApplicationContextProvider.getApplicationContext().getBean(SSOClient.class);
                }

                //DESENV-20829 - se este header estiver com valor true, tenta buscar token SSO em cache
                String searchTokenInCache = requestContext.getHeaderString("sso_token_cached");
                if (!ssoClient.isTokenValido(username, clientId, token,
                        (!TextHelper.isNull(searchTokenInCache) && "true".equals(searchTokenInCache)))) {
                    ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.tokenInvalido", AcessoSistema.getAcessoUsuarioSistema());
                    requestContext.abortWith(
                            Response.status(Response.Status.UNAUTHORIZED).entity(responseError).build());
                    return;
                }

                chave = new UsuarioChaveSessao();
                chave.setUsuario(new Usuario((String) usuList.get(0).getAttribute(Columns.USU_CODIGO)));
            }
        } catch (ViewHelperException vex) {
            writeSecurityLog(usuRequesting, vex);

            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = vex.getMessage();
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN).entity(responseError).build());
            return;
        } catch (Exception e) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).entity(responseError).build());
            return;
        }

        setUserPrincipalContext(requestContext, chave, cseDelegate, usuDelegate);

    }

    private void writeSecurityLog(TransferObject usuRequesting, ViewHelperException vex) {
        if (usuRequesting != null) {
            final AcessoSistema acessoSistema = new AcessoSistema((String) usuRequesting.getAttribute(Columns.USU_CODIGO), sr.getRemoteAddr(), sr.getRemotePort());

            LogDelegate log = new LogDelegate(acessoSistema, Log.LOG_ERRO_SEGURANCA, Log.USUARIO, Log.LOG_ERRO);
            log.setUsuario((String) usuRequesting.getAttribute(Columns.USU_CODIGO));
            try {
                log.add(vex.getMessage());
                log.write();
            } catch (LogControllerException e) {
                LOG.warn("ERRO AO ESCREVER LOG DE VALIDAÇÃO DE SEGURANÇA NA BASE DE DADOS", e);
            }
        }
    }

    private void setUserPrincipalContext(ContainerRequestContext requestContext, UsuarioChaveSessao chave, ConsignanteDelegate cseDelegate, UsuarioDelegate usuDelegate) {
        String usuCodigo = chave.getUsuario().getUsuCodigo();        
        final AcessoSistema acessoSistema = new AcessoSistema(usuCodigo, JspHelper.getRemoteAddr(sr), sr.getRemotePort());
        //seta o canal de acesso
        acessoSistema.setCanal(CanalEnum.REST);

        try {
            if (usuDelegate == null) {
                usuDelegate = new UsuarioDelegate();
            }

            TransferObject usuTipoTO = usuDelegate.obtemUsuarioTipo(usuCodigo, null, AcessoSistema.getAcessoUsuarioSistema());
            String tipoEntidade = usuTipoTO.getAttribute("TIPO").toString();

            switch (tipoEntidade) {
                case AcessoSistema.ENTIDADE_SER: {
                    ServidorDelegate serDelegate = new ServidorDelegate();
                    TransferObject ctoUsuario = serDelegate.buscaUsuarioServidor(acessoSistema.getUsuCodigo(), acessoSistema);
                    String serCodigo = (String) ctoUsuario.getAttribute(Columns.SER_CODIGO);
                    String orgCodigo = (String) ctoUsuario.getAttribute(Columns.ORG_CODIGO);
                    String estCodigo = (String) ctoUsuario.getAttribute(Columns.EST_CODIGO);
                    String rseCodigo = (String) ctoUsuario.getAttribute(Columns.RSE_CODIGO);
                    String srsCodigo = (String) ctoUsuario.getAttribute(Columns.SRS_CODIGO);
                    String serEmail = (String) ctoUsuario.getAttribute(Columns.SER_EMAIL);
                    String rseMatricula = (String) ctoUsuario.getAttribute(Columns.RSE_MATRICULA);
                    String rsePrazo = (ctoUsuario.getAttribute(Columns.RSE_PRAZO) != null ? String.valueOf(ctoUsuario.getAttribute(Columns.RSE_PRAZO)) : "");
                    String usuLogin = (String) ctoUsuario.getAttribute(Columns.USU_LOGIN);
                    String serCpf = (String) ctoUsuario.getAttribute(Columns.SER_CPF);
                    String serNome = (String) ctoUsuario.getAttribute(Columns.SER_NOME);
                    acessoSistema.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
                    acessoSistema.setCodigoEntidade(serCodigo);
                    acessoSistema.setDadosServidor(estCodigo, orgCodigo, rseCodigo, rseMatricula, serCpf, serEmail, rsePrazo, srsCodigo);
                    acessoSistema.setUsuLogin(usuLogin);
                    acessoSistema.setUsuNome(serNome);
                    acessoSistema.setPermissoes(usuDelegate.selectFuncoes(acessoSistema.getUsuCodigo(), acessoSistema.getCodigoEntidade(), acessoSistema.getTipoEntidade(), acessoSistema));
                    break;
                }
                case AcessoSistema.ENTIDADE_COR: {
                    ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                    CorrespondenteTransferObject cor = csaDelegate.findCorrespondente(usuTipoTO.getAttribute("CODIGO").toString(), acessoSistema);
                    ConsignatariaTransferObject csa = csaDelegate.findConsignataria(cor.getCsaCodigo(), acessoSistema);
                    String csa_nome = csa.getCsaNomeAbreviado();
                    if (csa_nome == null || csa_nome.isBlank()) {
                        csa_nome = csa.getCsaNome();
                    }
                    String codigoEntidade = usuTipoTO.getAttribute("CODIGO").toString();
                    acessoSistema.setNomeEntidade(cor.getCorNome());
                    acessoSistema.setIdEntidade(cor.getCorIdentificador());
                    acessoSistema.setNomeEntidadePai(csa_nome);
                    acessoSistema.setCodigoEntidadePai(csa.getCsaCodigo());
                    acessoSistema.setTipoEntidade(tipoEntidade);
                    acessoSistema.setCodigoEntidade(codigoEntidade);
                    acessoSistema.setUsuLogin(usuTipoTO.getAttribute(Columns.USU_LOGIN).toString());
                    acessoSistema.setUsuNome(usuTipoTO.getAttribute(Columns.USU_NOME).toString());
                    acessoSistema.setUsuEmail(!TextHelper.isNull(usuTipoTO.getAttribute(Columns.USU_EMAIL)) ? usuTipoTO.getAttribute(Columns.USU_EMAIL).toString() : null);
                    acessoSistema.setUsuCpf(!TextHelper.isNull(usuTipoTO.getAttribute(Columns.USU_CPF)) ? usuTipoTO.getAttribute(Columns.USU_CPF).toString() : null);
                    acessoSistema.setPermissoes(usuDelegate.selectFuncoes(acessoSistema.getUsuCodigo(), acessoSistema.getCodigoEntidade(), acessoSistema.getTipoEntidade(), acessoSistema));
                    break;
                }
                case AcessoSistema.ENTIDADE_ORG: {
                    OrgaoTransferObject org = cseDelegate.findOrgao(usuTipoTO.getAttribute("CODIGO").toString(), acessoSistema);
                    EstabelecimentoTransferObject est = cseDelegate.findEstabelecimento(org.getEstCodigo(), acessoSistema);
                    String codigoEntidade = usuTipoTO.getAttribute("CODIGO").toString();
                    acessoSistema.setNomeEntidade(org.getOrgNome());
                    acessoSistema.setIdEntidade(org.getOrgIdentificador());
                    acessoSistema.setNomeEntidadePai(est.getEstNome());
                    acessoSistema.setCodigoEntidadePai(est.getEstCodigo());
                    acessoSistema.setTipoEntidade(tipoEntidade);
                    acessoSistema.setCodigoEntidade(codigoEntidade);
                    acessoSistema.setUsuLogin(usuTipoTO.getAttribute(Columns.USU_LOGIN).toString());
                    acessoSistema.setUsuNome(usuTipoTO.getAttribute(Columns.USU_NOME).toString());
                    acessoSistema.setUsuEmail(!TextHelper.isNull(usuTipoTO.getAttribute(Columns.USU_EMAIL)) ? usuTipoTO.getAttribute(Columns.USU_EMAIL).toString() : null);
                    acessoSistema.setUsuCpf(!TextHelper.isNull(usuTipoTO.getAttribute(Columns.USU_CPF)) ? usuTipoTO.getAttribute(Columns.USU_CPF).toString() : null);
                    acessoSistema.setPermissoes(usuDelegate.selectFuncoes(acessoSistema.getUsuCodigo(), acessoSistema.getCodigoEntidade(), acessoSistema.getTipoEntidade(), acessoSistema));
                    break;
                }
                default: {
                    String codigoEntidade = usuTipoTO.getAttribute("CODIGO").toString();
                    acessoSistema.setTipoEntidade(tipoEntidade);
                    acessoSistema.setCodigoEntidade(codigoEntidade);
                    acessoSistema.setNomeEntidade(usuTipoTO.getAttribute("ENTIDADE").toString());
                    acessoSistema.setUsuLogin(usuTipoTO.getAttribute(Columns.USU_LOGIN).toString());
                    acessoSistema.setUsuNome(usuTipoTO.getAttribute(Columns.USU_NOME).toString());
                    acessoSistema.setUsuEmail(!TextHelper.isNull(usuTipoTO.getAttribute(Columns.USU_EMAIL)) ? usuTipoTO.getAttribute(Columns.USU_EMAIL).toString() : null);
                    acessoSistema.setUsuCpf(!TextHelper.isNull(usuTipoTO.getAttribute(Columns.USU_CPF)) ? usuTipoTO.getAttribute(Columns.USU_CPF).toString() : null);
                    acessoSistema.setPermissoes(usuDelegate.selectFuncoes(acessoSistema.getUsuCodigo(), acessoSistema.getCodigoEntidade(), acessoSistema.getTipoEntidade(), acessoSistema));
                    break;
                }
            }
            acessoSistema.setQtdConsultasMargem((Integer) usuTipoTO.getAttribute(Columns.USU_QTD_CONSULTAS_MARGEM));

        } catch (ConsignatariaControllerException | ConsignanteControllerException | ServidorControllerException | UsuarioControllerException e) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            requestContext.abortWith(Response.status(Response.Status.CONFLICT).entity(responseError).build());
            return;
        }

        requestContext.setSecurityContext(new SecurityContext() {

            @Override
            public Principal getUserPrincipal() {
                return acessoSistema;
            }

            @Override
            public boolean isUserInRole(String role) {
                return true;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public String getAuthenticationScheme() {
                return null;
            }
        });
    }

    /**
     * Recupera o token e verifica se este já está expirado ou não. Se estiver expirado, apaga o token.
     * Qualquer exceção é para negar acesso.
     * @param token
     * @return
     * @throws UsuarioControllerException
     */
    private UsuarioChaveSessao validateToken(String token) throws UsuarioControllerException{
        return new UsuarioDelegate().validateToken(token);
    }
}