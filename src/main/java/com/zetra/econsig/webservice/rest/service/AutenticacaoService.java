package com.zetra.econsig.webservice.rest.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.service.mensagem.MensagemController;
import org.apache.commons.codec.binary.Base64;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.EnderecoFuncaoTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GoogleAuthenticatorHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.ArquivoUsuario;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ParamSenhaExternaEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.filter.XSSPreventionFilter;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.filter.IpWatchdog;
import com.zetra.econsig.webservice.rest.request.OAuth2UrlResponse;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import com.zetra.econsig.webservice.rest.request.TotpRequest;
import com.zetra.econsig.webservice.rest.request.UsuarioRestRequest;
import com.zetra.econsig.webservice.rest.request.UsuarioRestResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * <p>Title: AutenticacaoService</p>
 * <p>Description: Serviço REST para autenticação de usuários.</p>
 * <p>Copyright: Copyright (c) 2007-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Anderson Assis, Igor Lucas, Marcelo Fortes, Leonel Martins
 */
@Path("/autenticacao")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AutenticacaoService extends RestService {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutenticacaoService.class);


    @Context
    SecurityContext securityContext;

    @POST
    @Path("/login")
    public Response login(UsuarioRestRequest usuario, @Context HttpServletRequest request) {
        // Verifica bloqueio por ip
        final String ip = request.getRemoteAddr();
        final Integer portaLocal = request.getRemotePort();
        int delay = IpWatchdog.verificaIp(ip);
        if (delay>0){
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
        if (usuario == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        final String loginClean = XSSPreventionFilter.stripXSS(usuario.login);
        final String senhaClean = XSSPreventionFilter.stripXSS(usuario.senha);

        if (TextHelper.isNull(loginClean)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.login.usuario", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        if (TextHelper.isNull(senhaClean)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.login.senha", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        final AcessoSistema usuAcesso = new AcessoSistema(CodedValues.USU_CODIGO_SISTEMA, ip, portaLocal);
        usuAcesso.setCanal(CanalEnum.REST);

        Object retorno = null;

        try {
            final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
            CustomTransferObject usuarioCto = usuarioController.findTipoUsuarioByLogin(loginClean, usuAcesso);
            if (usuarioCto == null) {
                // Busca pelo usuLogin assumindo que é e-mail
                final List<TransferObject> usuariosListTO = usuarioController.findUsuarioByEmail(loginClean, usuAcesso);

                // Recupera o usuário caso seja somente um
                if ((usuariosListTO != null) && (usuariosListTO.size() == 1)) {
                    usuarioCto = (CustomTransferObject) usuariosListTO.get(0);
                }
            }

            if ((usuarioCto != null)) {
                final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
                if (!TextHelper.isNull(usuarioCto.getAttribute(Columns.UCA_CSA_CODIGO))) {
                    responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_CSA);
                } else if (!TextHelper.isNull(usuarioCto.getAttribute(Columns.UCO_COR_CODIGO))) {
                    responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_COR);
                } else if (!TextHelper.isNull(usuarioCto.getAttribute(Columns.UCE_CSE_CODIGO))) {
                    responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_CSE);
                } else if (!TextHelper.isNull(usuarioCto.getAttribute(Columns.UOR_ORG_CODIGO))) {
                    responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_ORG);
                } else if (!TextHelper.isNull(usuarioCto.getAttribute(Columns.USE_SER_CODIGO))) {
                    responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
                } else if (!TextHelper.isNull(usuarioCto.getAttribute(Columns.USP_CSE_CODIGO))) {
                    responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SUP);
                }

                final Response responseSist = verificaSistemaDisponivel(responsavel);
                if (responseSist != null) {
                    return responseSist;
                }

                // valida acesso sistema
                validaAcessoSistema(usuarioCto, usuAcesso);
                // autentica usuário
                retorno = autenticarUsuario(senhaClean, usuarioCto, usuAcesso, request);
            } else {
                delay = IpWatchdog.bloqueiaIp(ip);
                if (delay>0){
                    final ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
                }
                gravaLogErro(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", null), loginClean, request);
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", null);
                return Response.status(Response.Status.UNAUTHORIZED).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }
        } catch (final ViewHelperException e) {
            gravaLogErro(e.getMessage(), loginClean, request);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            LOG.error(e.getMessage(), e);
            // Vai entrar nesta exceção para vários casos que não é de senha inválida
            if (e.getMessage().equalsIgnoreCase(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", null))){
                delay = IpWatchdog.bloqueiaIp(ip);
                if (delay>0){
                    LOG.error(e.getMessage(), e);
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
                }
            }
            // vai entrar nessa exceção em casos de usuário ou senha inválidos. Então é código 401.
            return Response.status(Response.Status.UNAUTHORIZED).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        } catch (final ZetraException e) {
            gravaLogErro(e.getMessage(), loginClean, request);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", null);
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
        IpWatchdog.desbloqueiaIp(ip);
        return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    @GET
    @Path("/oath2authenticationurl")
    public Response oathAuthenticationUrl () {
        final String oAuth2UriAuthentication = LoginHelper.getOAuth2UriAuthentication(CodedValues.OAUTH2_ACAO_LOGIN, AcessoSistema.getAcessoUsuarioSistema());
        final String oAuth2UriToken = ParamSenhaExternaEnum.OAUTH2_URI_AUTHENTICATION_2_TOKEN.getValor();

        final OAuth2UrlResponse retorno = new OAuth2UrlResponse();

        retorno.urlAuthentication = oAuth2UriAuthentication;
        retorno.redirectUri = "/v3/autenticarOAuth2";
        retorno.tokenUrl = oAuth2UriToken;

        return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/validarTotp")
    public Response validarTotp(TotpRequest request) throws UsuarioControllerException {
    	final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

    	final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);

    	final UsuarioTransferObject usuario = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);
        final String operacoesTotp = usuario.getUsuChaveValidacaoTotp();

        final String codigoTotp = request.getCodeTotp();
        if ((codigoTotp == null) || codigoTotp.isEmpty()) {

        	final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.totp.nao.informado", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
       }

        try {
            final GoogleAuthenticatorHelper authenticator = new GoogleAuthenticatorHelper();
            final boolean isValido = authenticator.checkCode(operacoesTotp, Long.parseLong(codigoTotp), System.currentTimeMillis());
            if (!isValido) {
            	if(request.getQtdTentativas() >= 3 ) {
            		try {
                        // Gerar log de erro de segurança
                        final com.zetra.econsig.delegate.LogDelegate log = new com.zetra.econsig.delegate.LogDelegate(responsavel, Log.USUARIO, Log.LOGIN, Log.LOG_ERRO_SEGURANCA);
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.erro.tentativa.validacao.totp.excedida", responsavel).toUpperCase());
                        log.write();
                    } catch (final LogControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
            	}
            	final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.totp.invalido", null);
                return Response.status(Response.Status.UNAUTHORIZED).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();

            }

            return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        } catch (final Exception e) {
        	final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", null);
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();

        }
    }


    /**
     * Grava log de erro ao tentar autenticar usuário
     * @param msgErro
     * @param usuLogin
     * @param request
     */
    private void gravaLogErro(String msgErro, String usuLogin, @Context HttpServletRequest request) {
        try {
            // grava log de erro no login
            final UsuarioDelegate usuDelegate = new UsuarioDelegate();
            final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

            // AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            AcessoSistema usuAcesso = JspHelper.getAcessoSistema(request);
            final CustomTransferObject usuario = usuDelegate.findTipoUsuario(usuLogin, responsavel);
            if (usuario != null) {
                usuAcesso = new AcessoSistema(usuario.getAttribute(Columns.USU_CODIGO).toString(), JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
            }
            usuAcesso.setCanal(CanalEnum.REST);
            final LogDelegate log = new LogDelegate (usuAcesso, Log.SISTEMA, Log.LOGIN, Log.LOG_LOGIN_ERRO);
            log.add("LOGIN: " + usuLogin);
            log.add("ERRO: " + msgErro);
            log.add("USER-AGENT: " + request.getHeader("user-agent"));
            log.write();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * verifica configuração necessária do sistema para autenticação
     * @param tipoEntidade
     * @return
     * @throws ZetraException
     */
    private boolean verificaConfiguracaoSistema(String tipoEntidade) throws ZetraException {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        final boolean cpfObrigatorio = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_CPF_OBRIGATORIO_USUARIO, CodedValues.TPC_SIM, responsavel);
        boolean cpfUnico = false;
        if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade) || AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade) || AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
            cpfUnico = ParamSist.getBoolParamSist(CodedValues.TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_CSE, responsavel);
        } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) || AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
            cpfUnico = ParamSist.getBoolParamSist(CodedValues.TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_CSA, responsavel);
        }

        if ((!cpfObrigatorio || !cpfUnico)) {
            // grava log de erro de configuração
            final LogDelegate log = new LogDelegate(responsavel, Log.GERAL, Log.LOGIN, Log.LOG_ERRO);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.cpf.acesso.mobile", responsavel));
            log.write();
            throw new ViewHelperException("mensagem.erro.configuracao.cpf.acesso.mobile", responsavel);
        }
        return true;
    }

    /**
     * valida acesso sistema com dados do usuário informado
     * @param usuarioCto
     * @param responsavel
     * @param request
     * @return
     * @throws ZetraException
     */
    private boolean validaAcessoSistema(CustomTransferObject usuarioCto, AcessoSistema responsavel)  throws ZetraException {
        String tipo = "";

        String cseCodigo = usuarioCto.getAttribute(Columns.UCE_CSE_CODIGO) != null ? usuarioCto.getAttribute(Columns.UCE_CSE_CODIGO).toString() : "";
        final String csaCodigo = usuarioCto.getAttribute(Columns.UCA_CSA_CODIGO) != null ? usuarioCto.getAttribute(Columns.UCA_CSA_CODIGO).toString() : "";
        final String corCodigo = usuarioCto.getAttribute(Columns.UCO_COR_CODIGO) != null ? usuarioCto.getAttribute(Columns.UCO_COR_CODIGO).toString() : "";
        final String orgCodigo = usuarioCto.getAttribute(Columns.UOR_ORG_CODIGO) != null ? usuarioCto.getAttribute(Columns.UOR_ORG_CODIGO).toString() : "";
        final String serCodigo = usuarioCto.getAttribute(Columns.USE_SER_CODIGO) != null ? usuarioCto.getAttribute(Columns.USE_SER_CODIGO).toString() : "";
        final String uspCseCodigo = usuarioCto.getAttribute(Columns.USP_CSE_CODIGO) != null ? usuarioCto.getAttribute(Columns.USP_CSE_CODIGO).toString() : "";

        // Não permite login de usuário servidor
        if (!"".equals(serCodigo)) {
            throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
        }

        // Determina o tipo da entidade do usuário
        if (!"".equals(cseCodigo)) {
            tipo = AcessoSistema.ENTIDADE_CSE;
        } else if (!"".equals(csaCodigo)) {
            tipo = AcessoSistema.ENTIDADE_CSA;
        } else if (!"".equals(corCodigo)) {
            tipo = AcessoSistema.ENTIDADE_COR;
        } else if (!"".equals(orgCodigo)) {
            tipo = AcessoSistema.ENTIDADE_ORG;
        } else if (!"".equals(uspCseCodigo)) {
            tipo = AcessoSistema.ENTIDADE_SUP;
            cseCodigo = uspCseCodigo;
        }

        final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
        // Verifica se o sistema permite o login de usuário correspondente vinculado a uma entidade bloqueada
        final boolean permiteLoginUsuCorEntidadeBloq = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_LOGIN_USU_COR_ENTIDADE_BLOQ, responsavel);
        // Verifica se a consignatária do correspondente não está bloqueada
        if (!permiteLoginUsuCorEntidadeBloq && !TextHelper.isNull(corCodigo)) {
            final CorrespondenteTransferObject cor = csaDelegate.findCorrespondente(corCodigo, responsavel);
            final ConsignatariaTransferObject csa = csaDelegate.findConsignataria(cor.getCsaCodigo(), responsavel);
            if (!csa.getCsaAtivo().equals(CodedValues.STS_ATIVO)) {
                throw new ViewHelperException("mensagem.informacao.consignataria.bloqueada", responsavel);
            }
            if (!cor.getCorAtivo().equals(CodedValues.STS_ATIVO)) {
                throw new ViewHelperException("mensagem.informacao.correspondente.bloqueado", responsavel);
            }
        }
        // verifica configuração necessária para autenticação de usuários pelo App eConsig
        verificaConfiguracaoSistema(tipo);

        return true;
    }

    /**
     * Autentica e retornar dados do usuário autenticado
     * @param senhaClean
     * @param usuario
     * @param responsavel
     * @return
     * @throws ZetraException
     */
    private UsuarioRestResponse autenticarUsuario(String senhaClean, CustomTransferObject usuario, AcessoSistema responsavel, @Context HttpServletRequest request) throws ZetraException {
        if (usuario != null) {
            final String ip = request.getRemoteAddr();
            final Integer portaLocal = request.getRemotePort();
            final TransferObject usuarioAutenticado = UsuarioHelper.autenticarUsuario((String) usuario.getAttribute(Columns.USU_LOGIN), senhaClean, responsavel);
            final UsuarioRestResponse usuResponse = new UsuarioRestResponse();
            final UsuarioDelegate usuDelegate = new UsuarioDelegate();

            final Collection<ArquivoUsuario> lstArqs = usuDelegate.findArquivoUsuario((String) usuario.getAttribute(Columns.USU_CODIGO), TipoArquivoEnum.ARQUIVO_IMAGEM_PERFIL_USUARIO.getCodigo(), AcessoSistema.getAcessoUsuarioSistema());
            if ((lstArqs != null) && !lstArqs.isEmpty()) {
                usuResponse.imagem = Base64.encodeBase64String(lstArqs.iterator().next().getAusConteudo());
            }

            // dados do usuário
            usuResponse.token = usuDelegate.gerarChaveSessaoUsuario((String) usuarioAutenticado.getAttribute(Columns.USU_CODIGO), AcessoSistema.getAcessoUsuarioSistema());
            usuResponse.usuCodigo = (String) usuarioAutenticado.getAttribute(Columns.USU_CODIGO);
            usuResponse.cpf = (String) usuarioAutenticado.getAttribute(Columns.USU_CPF);
            usuResponse.nome = (String) usuarioAutenticado.getAttribute(Columns.USU_NOME);
            usuResponse.telefone = (String) usuarioAutenticado.getAttribute(Columns.USU_TEL);
            usuResponse.dataUltAcesso = usuarioAutenticado.getAttribute(Columns.USU_DATA_ULT_ACESSO).toString();
            usuResponse.email = (String) usuarioAutenticado.getAttribute(Columns.USU_EMAIL);

            responsavel = AcessoSistema.recuperaAcessoSistema((String) usuarioAutenticado.getAttribute(Columns.USU_CODIGO), ip, portaLocal);
            responsavel.setCanal(CanalEnum.REST);

            // dados da entidade do usuário
            usuResponse.tipoEntidade = responsavel.getTipoEntidade();
            usuResponse.codigoEntidade = responsavel.getCodigoEntidade();
            // entidade do usuário
            if (responsavel.isCseSup()) {
                final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                final ConsignanteTransferObject cse = cseDelegate.findConsignante(responsavel.getCodigoEntidade(), responsavel);
                responsavel.setNomeEntidade(cse.getCseNome());
                responsavel.setIdEntidade(cse.getCseIdentificador());
            } else if (responsavel.isCsa()) {
                final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                final ConsignatariaTransferObject csa = csaDelegate.findConsignataria(responsavel.getCodigoEntidade(), responsavel);
                String csaNome = csa.getCsaNomeAbreviado();
                if ((csaNome == null) || csaNome.isBlank()) {
                    csaNome = csa.getCsaNome();
                }
                responsavel.setNomeEntidade(csaNome);
                responsavel.setIdEntidade(csa.getCsaIdentificador());
                responsavel.setNcaCodigo(csa.getCsaNcaNatureza());
            } else if (responsavel.isCor()) {
                final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                final CorrespondenteTransferObject cor = csaDelegate.findCorrespondente(responsavel.getCodigoEntidade(), responsavel);
                final ConsignatariaTransferObject csa = csaDelegate.findConsignataria(cor.getCsaCodigo(), responsavel);
                String csa_nome = csa.getCsaNomeAbreviado();
                if ((csa_nome == null) || csa_nome.isBlank()) {
                    csa_nome = csa.getCsaNome();
                }
                responsavel.setNomeEntidade(cor.getCorNome());
                responsavel.setIdEntidade(cor.getCorIdentificador());
                responsavel.setNomeEntidadePai(csa_nome);
                responsavel.setCodigoEntidadePai(csa.getCsaCodigo());
                responsavel.setNcaCodigo(csa.getCsaNcaNatureza());
            } else if (responsavel.isOrg()) {
                final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                final OrgaoTransferObject org = cseDelegate.findOrgao(responsavel.getCodigoEntidade(), responsavel);
                final EstabelecimentoTransferObject est = cseDelegate.findEstabelecimento(org.getEstCodigo(), responsavel);
                responsavel.setNomeEntidade(org.getOrgNome());
                responsavel.setIdEntidade(org.getOrgIdentificador());
                responsavel.setNomeEntidadePai(est.getEstNome());
                responsavel.setCodigoEntidadePai(est.getEstCodigo());
            } else {
                throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
            }
            // recupera código e nome das entidades após definir entidade do usuário
            usuResponse.nomeEntidade = responsavel.getNomeEntidade();
            usuResponse.codigoEntidadePai = responsavel.getCodigoEntidadePai();
            usuResponse.nomeEntidadePai = responsavel.getNomeEntidadePai();

            final Map<String, EnderecoFuncaoTransferObject> funcoes = usuDelegate.selectFuncoes(responsavel.getUsuCodigo(), responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), responsavel);
            usuResponse.permissoes = new ArrayList<>(funcoes.keySet());

            // DESENV-9262: grava no AcessoSistema do usuário a quantidade máxima de consultas de margem num intervalo específica, se houver.
            responsavel.setQtdConsultasMargem((Integer) usuario.getAttribute(Columns.USU_QTD_CONSULTAS_MARGEM));

            final ConvenioDelegate cnvDelegate = new ConvenioDelegate();
            final List<TransferObject> convenios = cnvDelegate.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), "reservar", responsavel);
            usuResponse.nseCodigos = new HashMap<>();
            if ((convenios != null) && !convenios.isEmpty()) {
                for (final TransferObject cnv : convenios) {
                    usuResponse.nseCodigos.put((String) cnv.getAttribute(Columns.NSE_CODIGO), (String) cnv.getAttribute(Columns.NSE_DESCRICAO));
                }
            }

            // DESENV-23948: Recupera lista de mensagens para o usuário
            final MensagemController mensagemController = ApplicationContextProvider.getApplicationContext().getBean(MensagemController.class);
            final List<TransferObject> mensagens = mensagemController.pesquisaMensagem(responsavel, 0, true);
            usuResponse.mensagens = mensagemController.parseToResponse(mensagens);

            // grava log de sucesso no login
            final LogDelegate log = new LogDelegate(responsavel, Log.SISTEMA, Log.LOGIN, Log.LOG_LOGIN_SUCESSO);
            log.add("USER-AGENT: " + request.getHeader("user-agent"));
            log.write();

            // retorna dados do usuário logado
            return usuResponse;
        } else {
            throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
        }


    }
}
