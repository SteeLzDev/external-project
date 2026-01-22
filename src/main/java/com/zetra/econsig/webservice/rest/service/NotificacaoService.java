package com.zetra.econsig.webservice.rest.service;

import java.security.Principal;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.TipoDispositivoEnum;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.DeviceRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

/**
 * <p>Title: NotificacaoService</p>
 * <p>Description: API REST para notificações push a dispostivos de usuários.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/notificacao")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class NotificacaoService extends RestService {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(NotificacaoService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Path("/regdevice")
    @Secured
    public Response regDevice(DeviceRestRequest device) {

        Principal principal = securityContext.getUserPrincipal();
        String usuCodigo = ((AcessoSistema) principal).getUsuCodigo();

        if (device == null) {
            device = new DeviceRestRequest();
        }

        try {
            UsuarioDelegate usuDelegate = new UsuarioDelegate();

            String chaveDispositivoNaBase = usuDelegate.findDeviceToken(usuCodigo, (AcessoSistema) principal);

            device.tdiCodigo = (TextHelper.isNull(device.tdiCodigo)) ? TipoDispositivoEnum.ANDROID.getCodigo() : device.tdiCodigo;

            if (!TextHelper.isNull(device.token) && !device.token.equals(chaveDispositivoNaBase)) {
                usuDelegate.cadastroDeviceToken(usuCodigo, device.tdiCodigo, device.token, (AcessoSistema) principal);
            }
        } catch (UsuarioControllerException e) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

}
