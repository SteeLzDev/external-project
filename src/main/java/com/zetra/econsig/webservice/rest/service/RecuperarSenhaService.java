package com.zetra.econsig.webservice.rest.service;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.filter.XSSPreventionFilter;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.filter.IpWatchdog;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import com.zetra.econsig.webservice.rest.request.UsuarioRestRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * <p>Title: RecuperarSenha</p>
 * <p>Description: Serviço REST para recuperar senha do usuário.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/recuperarSenha")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class RecuperarSenhaService extends RestService {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RecuperarSenhaService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Path("/recuperar")
    public Response recuperarSenha(UsuarioRestRequest usuario, @Context HttpServletRequest request) {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        // Verifica bloqueio por ip
        String ip = request.getRemoteAddr();
        int delay = IpWatchdog.verificaIp(ip);
        if (delay>0){
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
        if (usuario == null) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
        Response responseSist = verificaSistemaDisponivel();
        if (responseSist != null) {
            return responseSist;
        }

        String idClean = XSSPreventionFilter.stripXSS(usuario.id);
        String loginClean = XSSPreventionFilter.stripXSS(usuario.login);
        String cpfClean = XSSPreventionFilter.stripXSS(usuario.cpf);
        String senhaClean = XSSPreventionFilter.stripXSS(usuario.senha);
        String otpClean = XSSPreventionFilter.stripXSS(usuario.otp);
        String orgCodigoClean = XSSPreventionFilter.stripXSS(usuario.orgCodigo);

        if (TextHelper.isNull(loginClean)) {
            if (TextHelper.isNull(idClean) && TextHelper.isNull(cpfClean)) {
                boolean omiteCpf = ParamSist.getBoolParamSist(CodedValues.TPC_OMITE_CPF_SERVIDOR, responsavel);
                ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = omiteCpf ? ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.email", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cpf", responsavel);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }
        } else if (TextHelper.isNull(cpfClean)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.cpf", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        AcessoSistema respValidacao = AcessoSistema.getAcessoUsuarioSistema();
        if (TextHelper.isNull(loginClean)) {
            respValidacao.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
        }
        try {
            List<String> orgCodigos = null;
            if (!TextHelper.isNull(orgCodigoClean)) {
                orgCodigos = new ArrayList<>();
                orgCodigos.add(orgCodigoClean);
            }

            UsuarioDelegate usuDelegate = new UsuarioDelegate();
            usuDelegate.recuperarSenha(!TextHelper.isNull(idClean) ? idClean : cpfClean, loginClean, orgCodigos, otpClean, senhaClean, TextHelper.isNull(loginClean), respValidacao);

            IpWatchdog.desbloqueiaIp(ip);
            return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();

        } catch (UsuarioControllerException e) {
            if (e.getMessageKey().equals("mensagem.erro.servidor.nao.encontrado")) {
                delay = IpWatchdog.bloqueiaIp(ip);
                if (delay>0) {
                    ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
                    LOG.error(e.getMessage(), e);
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
                }
            } else if(e.getMessageKey().equals("mensagem.erro.email.ser.nao.cadastrado") ||
                      e.getMessageKey().equals("mensagem.erro.celular.ser.nao.cadastrado") ||
                      e.getMessageKey().equals("mensagem.erro.tel.email.ser.nao.cadastrado")) {

                //A ideia é não logar no caso desses erros para limpar o console
                //LOG.error(e.getMessage(), e);
            } else {
                LOG.error(e.getMessage(), e);
            }

            return genericError(e);
        }
    }

    @POST
    @Path("/podeMandarOtp")
    public Response podeMandarOtp(UsuarioRestRequest usuario, @Context HttpServletRequest request) {
        // Verifica bloqueio por ip
        String ip = request.getRemoteAddr();
        int delay = IpWatchdog.verificaIp(ip);
        if (delay>0){
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
        if (usuario == null) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
        Response responseSist = verificaSistemaDisponivel();
        if (responseSist != null) {
            return responseSist;
        }
        String idClean = XSSPreventionFilter.stripXSS(usuario.id);
        try {
            AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
            ServidorDelegate serDelegate = new ServidorDelegate();
            List<TransferObject> lstServidor = serDelegate.pesquisaServidor(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), null, null, null, idClean, 0, JspHelper.LIMITE, responsavel, false, null, false, null);
            Object paramRecuperacaoSenha = ParamSist.getInstance().getParam(CodedValues.TPC_METODO_ENVIO_OTP_RECUPERACAO_SENHA, responsavel);
            String strParamRecuperaSenha = paramRecuperacaoSenha != null ? paramRecuperacaoSenha.toString() : CodedValues.ENVIA_OTP_DESABILITADO;
            ResponseRestRequest response = new ResponseRestRequest();

            if (lstServidor == null || lstServidor.isEmpty()) {
                response.mensagem = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.otp.erro.servidor", null);
            } else if (TextHelper.isNull(paramRecuperacaoSenha) || paramRecuperacaoSenha.equals(CodedValues.ENVIA_OTP_DESABILITADO)) {
                response.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.recuperacao.nao.disponivel", null);
            } else if (!strParamRecuperaSenha.equals(CodedValues.ENVIA_OTP_SMS) && !strParamRecuperaSenha.equals(CodedValues.ENVIA_OTP_EMAIL) && !strParamRecuperaSenha.equals(CodedValues.ENVIA_OTP_SMS_OU_EMAIL)) {
                response.mensagem = ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", null);
            } else {
                TransferObject servidor = lstServidor.get(0);
                if (paramRecuperacaoSenha.equals(CodedValues.ENVIA_OTP_SMS) && TextHelper.isNull(servidor.getAttribute(Columns.SER_CELULAR))) {
                    response.mensagem = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.otp.erro.celular.invalido", null);
                } else if (paramRecuperacaoSenha.equals(CodedValues.ENVIA_OTP_EMAIL) && TextHelper.isNull(servidor.getAttribute(Columns.SER_EMAIL))) {
                    response.mensagem = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.otp.erro.email.invalido", null);
                } else if (paramRecuperacaoSenha.equals(CodedValues.ENVIA_OTP_SMS_OU_EMAIL) && (TextHelper.isNull(servidor.getAttribute(Columns.SER_EMAIL)) || TextHelper.isNull(servidor.getAttribute(Columns.SER_CELULAR)))) {
                    response.mensagem = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.otp.erro.email.celular.invalido", null);
                }
            }
            IpWatchdog.desbloqueiaIp(ip);
            return Response.status(Response.Status.OK).entity(response).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();

        } catch (ServidorControllerException e) {
            if (e.getMessageKey().equals("mensagem.erro.servidor.nao.encontrado")) {
                delay = IpWatchdog.bloqueiaIp(ip);
                if (delay>0) {
                    ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
                    LOG.error(e.getMessage(), e);

                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
                }
            }
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    }
}
