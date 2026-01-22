package com.zetra.econsig.webservice.rest.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BannerPublicidadeControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.service.banner.BannerPublicidadeController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.BannerPublicidadeRestRequest;
import com.zetra.econsig.webservice.rest.request.BannerPublicidadeRestResponse;

/**
 * <p>Title: BannerPublicidadeService</p>
 * <p>Description: Serviço REST para consulta de banners publidade mobile.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Path("/banner")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class BannerPublicidadeService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BannerPublicidadeService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/getBanner")
    public Response getBanner(List<BannerPublicidadeRestRequest> listBanners) {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONSULTAR_BANNER_PROPAGANDA), null);
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }

            List<BannerPublicidadeRestResponse> listResponse = new ArrayList<BannerPublicidadeRestResponse>();
            BannerPublicidadeController bannerPublicidadeController = ApplicationContextProvider.getApplicationContext().getBean(BannerPublicidadeController.class);
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.BPU_EXIBE_MOBILE, CodedValues.TPC_SIM);

            List<TransferObject> bannersPublicidadeMobile = bannerPublicidadeController.listarBannerPublicidade(criterio, -1, -1, responsavel);

            if (listBanners.isEmpty()) {
                for(TransferObject banner: bannersPublicidadeMobile) {
                    listResponse.add(BannerPublicidadeRestResponse(banner));
                }
            } else {
                // Separar banners atualizados
                for(BannerPublicidadeRestRequest banner : listBanners) {
                    Optional<TransferObject> itemTO = bannersPublicidadeMobile.stream().parallel().filter(item ->
                    item.getAttribute(Columns.BPU_CODIGO).toString().equalsIgnoreCase(banner.bpuCodigo)
                    && !DateHelper.toDateTimeString((Date) item.getAttribute(Columns.BPU_DATA)).equalsIgnoreCase(banner.bpuData)
                    ).findAny();

                    if (itemTO.isPresent()) {
                        listResponse.add(BannerPublicidadeRestResponse(itemTO.get()));
                    }
                }
                // Separar novos banners
                for(TransferObject bannerTO : bannersPublicidadeMobile) {
                    Optional<BannerPublicidadeRestRequest> itemRestResponse = listBanners.stream().parallel().filter(item -> item.bpuCodigo.equalsIgnoreCase(bannerTO.getAttribute(Columns.BPU_CODIGO).toString())).findAny();

                    if (!itemRestResponse.isPresent()) {
                        listResponse.add(BannerPublicidadeRestResponse(bannerTO));
                    }
                }

                // Separar excluídos
                for(BannerPublicidadeRestRequest banner : listBanners) {
                    Optional<TransferObject> itemRestResponse = bannersPublicidadeMobile.stream().parallel().filter(item -> item.getAttribute(Columns.BPU_CODIGO).toString().equalsIgnoreCase(banner.bpuCodigo)).findAny();

                    if (!itemRestResponse.isPresent()) {
                        BannerPublicidadeRestResponse removedBanner = new BannerPublicidadeRestResponse();
                        removedBanner.bpuCodigo = banner.bpuCodigo;
                        listResponse.add(removedBanner);
                    }
                }
            }

            return Response.status(Response.Status.OK).entity(listResponse).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();

        } catch (BannerPublicidadeControllerException ex){
            LOG.error(ex.getMessage(), ex);
            return genericError(ex);
        }
    }

    public BannerPublicidadeRestResponse BannerPublicidadeRestResponse(TransferObject bannerTO) {
        BannerPublicidadeRestResponse bannerResponse = new BannerPublicidadeRestResponse();

        bannerResponse.bpuCodigo = bannerTO.getAttribute(Columns.BPU_CODIGO).toString();
        bannerResponse.bpuData = DateHelper.toDateTimeString((Date) bannerTO.getAttribute(Columns.BPU_DATA));
        bannerResponse.bpuUrlSaida = (String) bannerTO.getAttribute(Columns.BPU_URL_SAIDA);
        bannerResponse.bpuOrdem = (Short) bannerTO.getAttribute(Columns.BPU_ORDEM);
        bannerResponse.bpuDescricao = (String) bannerTO.getAttribute(Columns.TAR_DESCRICAO);
        bannerResponse.nseCodigo = (String) bannerTO.getAttribute(Columns.BPU_NSE_CODIGO);
        bannerResponse.arqConteudo = (String) bannerTO.getAttribute(Columns.ARQ_CONTEUDO);

        return bannerResponse;
    }
}
