package com.zetra.econsig.webservice.rest.service;

import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.beneficios.ProvedorBeneficioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.filter.XSSPreventionFilter;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

/**
 * <p>Title: GeolocalizacaoService</p>
 * <p>Description: Serviço REST para operações sobre geolocalização.</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/geolocalizacao")
public class GeolocalizacaoService extends RestService {

    @Context
    SecurityContext securityContext;


    /**
     * busca dados de estabelecimentos parceiros dentro de um raio de geolocalização que ofereçam serviços de natureza SALARYPAY.
     * @param filtrosBuscaGeolocalizacao
     * @return
     * @throws ZetraException
     */
    @GET
    @Secured
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/salarypay/parceiros")
    public Response buscaParceirosSalaryPayGeoLocalizacao(@QueryParam("latitude") Float latitude, @QueryParam("longitude") Float longitude, @QueryParam("filtro") String filtro) throws ZetraException {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        // Verifica latitude e longitude foram preenchidos.
        if (latitude == null || longitude == null) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.dados.geolocalizacao.devem.ser.informados", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        Object paramSistRaioBusca = ParamSist.getInstance().getParam(CodedValues.TPC_RAIO_METROS_BUSCA_END_CONSIGNATARIAS, responsavel);
        Float raioBusca = null;

        if (paramSistRaioBusca == null) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.alerta.busca.entidade.geolocalizacao.inativa", null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } else {
            raioBusca = Float.parseFloat(paramSistRaioBusca.toString());

            if (raioBusca.compareTo(0.0f) <= 0) {
                ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.alerta.busca.entidade.geolocalizacao.inativa", null);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
        }

        //TODO: trocar esta instanciação quando refatorar classe para Spring RestController
        ProvedorBeneficioController provedorBeneficioController = ApplicationContextProvider.getApplicationContext().getBean(ProvedorBeneficioController.class);

        List<TransferObject> parceiros = provedorBeneficioController.listarProvedorBeneficioEmPerimetro(latitude, longitude, raioBusca, Arrays.asList(CodedValues.NSE_SALARYPAY), XSSPreventionFilter.stripXSS(filtro), responsavel);

        for (TransferObject parceiro : parceiros) {
            parceiro.setAttribute("nome", (!TextHelper.isNull(parceiro.getAttribute(Columns.CSA_NOME)) ? parceiro.getAttribute(Columns.CSA_NOME) : parceiro.getAttribute(Columns.COR_NOME)));
            parceiro.setAttribute("latitude", (!TextHelper.isNull(parceiro.getAttribute(Columns.ENC_LATITUDE)) ? parceiro.getAttribute(Columns.ENC_LATITUDE) : parceiro.getAttribute(Columns.ECR_LATITUDE)));
            parceiro.setAttribute("longitude", (!TextHelper.isNull(parceiro.getAttribute(Columns.ENC_LONGITUDE)) ? parceiro.getAttribute(Columns.ENC_LONGITUDE) : parceiro.getAttribute(Columns.ECR_LONGITUDE)));
            parceiro.setAttribute("logradouro", (!TextHelper.isNull(parceiro.getAttribute(Columns.ENC_LOGRADOURO)) ? parceiro.getAttribute(Columns.ENC_LOGRADOURO) : parceiro.getAttribute(Columns.ECR_LOGRADOURO)));
            parceiro.setAttribute("numero", (!TextHelper.isNull(parceiro.getAttribute(Columns.ENC_NUMERO)) ? parceiro.getAttribute(Columns.ENC_NUMERO) : parceiro.getAttribute(Columns.ECR_NUMERO)));
            parceiro.setAttribute("complemento", (!TextHelper.isNull(parceiro.getAttribute(Columns.ENC_COMPLEMENTO)) ? parceiro.getAttribute(Columns.ENC_COMPLEMENTO) : parceiro.getAttribute(Columns.ECR_COMPLEMENTO)));
            parceiro.setAttribute("bairro", (!TextHelper.isNull(parceiro.getAttribute(Columns.ENC_BAIRRO)) ? parceiro.getAttribute(Columns.ENC_BAIRRO) : parceiro.getAttribute(Columns.ECR_BAIRRO)));
            parceiro.setAttribute("municipio", (!TextHelper.isNull(parceiro.getAttribute(Columns.ENC_MUNICIPIO)) ? parceiro.getAttribute(Columns.ENC_MUNICIPIO) : parceiro.getAttribute(Columns.ECR_MUNICIPIO)));
            parceiro.setAttribute("uf", (!TextHelper.isNull(parceiro.getAttribute(Columns.ENC_UF)) ? parceiro.getAttribute(Columns.ENC_UF) : parceiro.getAttribute(Columns.ECR_UF)));
            parceiro.setAttribute("cep", (!TextHelper.isNull(parceiro.getAttribute(Columns.ENC_CEP)) ? parceiro.getAttribute(Columns.ENC_CEP) : parceiro.getAttribute(Columns.ECR_CEP)));
            parceiro.setAttribute("distancia", parceiro.getAttribute("DISTANCIA"));
        }

        List<String> filter = Arrays.asList("ben_descricao", "nome", "latitude", "longitude", "logradouro", "numero", "complemento", "bairro", "municipio", "uf", "cep", "distancia");
        return Response.status(Response.Status.OK).entity(transformTOs(parceiros, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }
}
