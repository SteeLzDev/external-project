package com.zetra.econsig.webservice.rest.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.sistema.TextoSistemaController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.MobileMessageRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import com.zetra.econsig.webservice.rest.request.TextoSistemaRestRequest;

/**
 * <p>Title: TextoSistemaService</p>
 * <p>Description: Serviço REST para internalização do Mobile.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/textoSistema")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class TextoSistemaService extends RestService {

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/buscaNovasChaves")
    /*
     * As chave mobile tem o sufixo 'mobile.' na tb_texto_sistema, mas como não
     * é assim no prório mobile o sufixo é colocado e retirado automaticamente.
     * Sendo assim basta passar a chave ignorando o prefixo e tratar o retorno da mesma forma
    */
    public Response buscaNovasChaves(MobileMessageRestRequest mobileTextoSistema, @Context HttpServletRequest request) throws ParseException {
        try {

            if (TextHelper.isNull(mobileTextoSistema.dataUltimaAlteracao)) {
                ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

            AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            TextoSistemaController controller = ApplicationContextProvider.getApplicationContext().getBean(TextoSistemaController.class);
            Date texDataAlteracao;

            try {
                texDataAlteracao = DateHelper. parse(mobileTextoSistema.dataUltimaAlteracao, "yyyy-MM-dd HH:mm:ss");
            } catch (ParseException e) {
                texDataAlteracao = DateHelper.parse("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
            }

            List<TransferObject> mensagens = controller.lstMobileTextoSistema(null, texDataAlteracao, responsavel);

            //se nenhuma msg mudou é mantida a última data de atualização
            String dataUltimaAlteracao = mensagens.isEmpty() ? mobileTextoSistema.dataUltimaAlteracao :  DateHelper.format(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss");

            List<Map<String, String>> msgAlteradas = new ArrayList<>();
            Map<String, Object> retorno = new HashMap<>();
            retorno.put("dataUltimaAlteracao", dataUltimaAlteracao);

            mensagens.forEach(o -> {
                Map<String, String> mensagem = new HashMap<>();
                mensagem.put("texChave", (String) o.getAttribute(Columns.TEX_CHAVE));
                mensagem.put("texTexto", (String) o.getAttribute(Columns.TEX_TEXTO));
                mensagem.put("texDataAlteracao", DateHelper.format((java.util.Date) o.getAttribute(Columns.TEX_DATA_ALTERACAO), "yyyy-MM-dd HH:mm:ss"));
                msgAlteradas.add(mensagem);
            });

            retorno.put("mensagens", msgAlteradas);

            return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();

        } catch (ConsignanteControllerException e) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }

    @POST
    @Secured
    @Path("/insereNovasChaves")
    /*
     * As chave mobile tem o sufixo 'mobile.' na tb_texto_sistema, mas como não
     * é assim no prório mobile o sufixo é colocado e retirado automaticamente.
     * Sendo assim basta passar a chave ignorando o prefixo e tratar o retorno da mesma forma
    */
    public Response insereNovasChaves(MobileMessageRestRequest mobileTextoSistema, @Context HttpServletRequest request) throws ParseException {
        try {

            if (mobileTextoSistema.mensagensFormatadas.isEmpty()) {
                ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

            AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            TextoSistemaController controller = ApplicationContextProvider.getApplicationContext().getBean(TextoSistemaController.class);

            Date texDataAlteracao = Calendar.getInstance().getTime();
            String formatedTexDataAlteracao = DateHelper.format(texDataAlteracao, "yyyy-MM-dd HH:mm:ss");

            List<TextoSistemaRestRequest> mensagensOut =  controller.updateMobileTextoSistema(mobileTextoSistema.mensagensFormatadas, texDataAlteracao, responsavel)
                                                                    .stream().map(o -> {return new TextoSistemaRestRequest(o.getTexChave(), o.getTexTexto(), formatedTexDataAlteracao);})
                                                                    .collect(Collectors.toList());


            Map<String, Object> retorno = new HashMap<>();
            retorno.put("dataUltimaAlteracao", formatedTexDataAlteracao);
            retorno.put("mensagens", mensagensOut);

            return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();

        } catch (ConsignanteControllerException e) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }

}
