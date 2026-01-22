package com.zetra.econsig.webservice.rest.service;

import java.util.HashMap;
import java.util.Map;

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

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.criptografia.JCrypt;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.CodigoAutorizacaoRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

@Path("/reservar")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class CodigoAutorizacaoService  extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CodigoAutorizacaoService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/enviarCodigoAutorizacao")
    public Response enviarCodigoAutorizacao(CodigoAutorizacaoRestRequest dados, @Context HttpServletRequest request) throws ViewHelperException, ServidorControllerException {

        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if (dados == null || TextHelper.isNull(dados.rseCodigo)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        HashMap<String, String> result = new HashMap<>();
        String celularDestinatario = null;
        // Busca dados do servidor
        ServidorDelegate serDelegate = new ServidorDelegate();
        ServidorTransferObject servidor = serDelegate.findServidorByRseCodigo(dados.rseCodigo, responsavel);

        if (servidor != null) {
            // Formata o telefone para o padrão do país
            celularDestinatario = !TextHelper.isNull(servidor.getSerCelular()) ? LocaleHelper.formataCelular(servidor.getSerCelular()) : null;
        }

        if (!TextHelper.isNull(celularDestinatario)) {
            // Envia o SMS.
            try {

                UsuarioDelegate usuDelegate = new UsuarioDelegate();
                usuDelegate.enviarCodigoAutorizacaoSms(dados.rseCodigo, responsavel);

                result.put("mensagem", "Enviado");

            } catch (ZetraException e) {
                LOG.debug("Erro ao enviar o SMS: " + e.getMessage());
                result.put("mensagem", ApplicationResourcesHelper.getMessage("mensagem.erro.enviar.sms", responsavel));
                LOG.error(e.getMessage(), e);
                return Response.status(Response.Status.CONFLICT).entity(result).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }
        } else {
            result.put("mensagem", ApplicationResourcesHelper.getMessage("mensagem.erro.necessita.telefone.cadastrado", responsavel));
        }

        return Response.status(Response.Status.OK).entity(result).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/validarCodigoAutorizacao")
    public Response validarCodigoAutorizacao(CodigoAutorizacaoRestRequest dados, @Context HttpServletRequest request, AcessoSistema responsavel) {

        responsavel = responsavel == null ? (AcessoSistema) securityContext.getUserPrincipal() : responsavel;

        if (dados == null || TextHelper.isNull(dados.codAutorizacao)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        Map<String, String> result = new HashMap<>();

        TransferObject usuario;
        ServidorDelegate serDelegate = new ServidorDelegate();

        try {
            usuario = serDelegate.buscaUsuarioServidor(responsavel.getUsuCodigo(), responsavel);
        } catch (ServidorControllerException e) {
            LOG.debug("Erro ao localizar o servidor: " + e.getMessage());
            result.put("mensagem", ApplicationResourcesHelper.getMessage("mensagem.erro.localizar.servidor", responsavel));
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(result).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        ParamSist paramSist = ParamSist.getInstance();
        String timeoutOtpString = (String) paramSist.getParam(CodedValues.TPC_TEMPO_EXPIRACAO_OTP, AcessoSistema.getAcessoUsuarioSistema());
        Integer timeoutOtp = TextHelper.isNull(timeoutOtpString) ? null : Integer.valueOf(timeoutOtpString);
        // default 20 minutos
        if (timeoutOtp == null) {
            timeoutOtp = 20;
        }

        // Valida se o otp expirou
        java.util.Date otpDataCadastro = usuario.getAttribute(Columns.USU_OTP_DATA_CADASTRO) != null ? (java.util.Date) usuario.getAttribute(Columns.USU_OTP_DATA_CADASTRO) : null;
        java.util.Date dataAtual = new java.util.Date();

        // Verifica se otp é inválido ou se passou o limite do tempo em milisegundos
        if (TextHelper.isNull(usuario.getAttribute(Columns.USU_OTP_CODIGO)) || !JCrypt.verificaSenha(dados.codAutorizacao, usuario.getAttribute(Columns.USU_OTP_CODIGO).toString())) {
            result.put("mensagem", ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.autorizacao.invalido", responsavel));

            return Response.status(Response.Status.CONFLICT).entity(result).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();

        } else if (otpDataCadastro == null || ((dataAtual.getTime() - otpDataCadastro.getTime()) > timeoutOtp * (6 * Math.pow(10, 4)))) {
            result.put("mensagem", ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.autorizacao.expirado", responsavel));
            return Response.status(Response.Status.CONFLICT).entity(result).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        return Response.status(Response.Status.OK).entity(result).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }
}
