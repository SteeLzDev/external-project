package com.zetra.econsig.webservice.rest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.FaqControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.faq.FaqController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

/**
 * <p>Title: VisualizarFaqService</p>
 * <p>Description: Serviço REST para listar os faqs de servidores no mobile</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/rest/visualizarfaq")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class VisualizarFaqService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VisualizarFaqService.class);

    @Context
    SecurityContext securityContext;

    @GET
    @Secured
    @Path("/listar")
    public Response listarFaqMobileServidor() throws ZetraException {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        List<Map<String, Object>> retorno = new ArrayList<>();
        FaqController faqController = ApplicationContextProvider.getApplicationContext().getBean(FaqController.class);
        try{
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.FAQ_EXIBE_MOBILE, CodedValues.TPC_SIM);
            criterio.setAttribute(Columns.FAQ_EXIBE_SER, CodedValues.TPC_SIM);
            List<TransferObject> lstFaq = faqController.lstFaq(criterio, -1, -1, responsavel);

            if(lstFaq == null || lstFaq.isEmpty()) {
                return genericError(new ZetraException("mensagem.nenhum.faq.encontrado", responsavel));
            }
            List<String> filter = Arrays.asList("faq_codigo", "usu_codigo","faq_titulo_1", "faq_titulo_2", "faq_texto", "faq_data", "faq_sequencia", "faq_html", "caf_codigo", "caf_descricao");
            Map<String, Object> categoriaOutros = new HashMap<>();
            categoriaOutros.put("desc", "Outros");

            List<TransferObject> faqsCategoria = new ArrayList<>();
            List<TransferObject> faqsCategoriaOutros = new ArrayList<>();
            String categoriaDescricao = "";
            String categoriaCodigo = "";
            for(TransferObject faq : lstFaq) {
                String cafCodigoFaq = (String) faq.getAttribute(Columns.FAQ_CAF_CODIGO);
                String cafDescricaoFaq = (String) faq.getAttribute(Columns.CAF_DESCRICAO);
                faq.setAttribute(Columns.FAQ_DATA, DateHelper.format((Date) faq.getAttribute(Columns.FAQ_DATA), "yyyy-MM-dd HH:mm:ss"));
                if(!TextHelper.isNull(cafCodigoFaq) && !cafCodigoFaq.equals(categoriaCodigo) && !faqsCategoria.isEmpty()) {
                    Map<String, Object> categoria = new HashMap<>();
                    categoria.put("desc", categoriaDescricao);
                    categoria.put("faqs", transformTOs(faqsCategoria, filter));
                    retorno.add(categoria);
                    categoriaDescricao = cafDescricaoFaq;
                    categoriaCodigo = cafCodigoFaq;
                    faqsCategoria = new ArrayList<>();
                    faqsCategoria.add(faq);
                } else {
                    if(TextHelper.isNull(cafCodigoFaq)) {
                        faqsCategoriaOutros.add(faq);
                    } else {
                        faqsCategoria.add(faq);
                        categoriaDescricao = cafDescricaoFaq;
                        categoriaCodigo = cafCodigoFaq;
                    }
                }
            }
            if(!faqsCategoria.isEmpty()) {
                Map<String, Object> categoria = new HashMap<>();
                categoria.put("desc", categoriaDescricao);
                categoria.put("faqs", transformTOs(faqsCategoria, filter));
                retorno.add(categoria);
            }
            categoriaOutros.put("faqs", transformTOs(faqsCategoriaOutros,filter));
            retorno.add(categoriaOutros);
        } catch ( FaqControllerException e) {
            LOG.error(e.getMessage(), e);
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
        return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }
}
