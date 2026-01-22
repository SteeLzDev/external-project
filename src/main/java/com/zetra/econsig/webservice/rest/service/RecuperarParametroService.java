package com.zetra.econsig.webservice.rest.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.RecuperarParametroRestRequest;
import com.zetra.econsig.webservice.rest.request.RecuperarParametroRestResponse;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

/**
 * <p>Title: RecuperarParametroService</p>
 * <p>Description: Serviço REST para recuperar parâmetros do eConsig.</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/parametros")
public class RecuperarParametroService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RecuperarParametroService.class);

    @Context
    SecurityContext securityContext;

    /**
     * busca a chave vault cadastrada no eConsig para ser utilizada no SalaryPay em integração com a Cielo.
     * @param filtrosBuscaGeolocalizacao
     * @return
     * @throws ZetraException
     */
    @POST
    @Secured
    @Path("/recuperarChaveVault")
    public Response recuperarChaveVault() {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        RecuperarParametroRestResponse parametroRestResponse = new RecuperarParametroRestResponse();

        ParamSist paramSist = ParamSist.getInstance();
        String chaveVault = (String) paramSist.getParam(CodedValues.TPC_CHAVE_VAULT_INTEGRACAO_SALARYPAY_CIELO, responsavel);

        parametroRestResponse.codigoParametro = CodedValues.TPC_CHAVE_VAULT_INTEGRACAO_SALARYPAY_CIELO;
        parametroRestResponse.valorParametro = chaveVault;

        return Response.status(Response.Status.OK).entity(parametroRestResponse).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    /**
     * Busca código e valor dos parâmetros de serviço de consignatária do csa_indentificador_interno recebido para ser utilizado em integração com a MAG.
     * @param token, csaIdentificadorInterno
     * @return
     * @throws ZetraException
     */
    @POST
    @Secured
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/recuperarParametros")
    public Response recuperarParametros(RecuperarParametroRestRequest recuperarParametroRestRequest) {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        try {
            if (!responsavel.isSer()) {
                ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.invalido.recuperar.parametros", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

            if (recuperarParametroRestRequest == null) {
                ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }

            if (TextHelper.isNull(recuperarParametroRestRequest.csaIdentificadorInterno)) {
                ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.csa.identificador.interno.recuperar.parametros", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

            List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO);
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO);
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO);
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO);
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO);
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO);

            ParametroDelegate parDelegate = new ParametroDelegate();
            List<TransferObject> svcCsaParams = parDelegate.selectParamSvcCsa(recuperarParametroRestRequest.csaIdentificadorInterno, tpsCodigos, false, responsavel);

            String cnvCodVerba = null;
            RecuperarParametroRestResponse parametroRestResponse;
            List<RecuperarParametroRestResponse> listParametroRestResponse = new ArrayList<>();
            for (TransferObject param : svcCsaParams) {
                parametroRestResponse = new RecuperarParametroRestResponse();
                parametroRestResponse.codigoParametro = (String) param.getAttribute(Columns.TPS_CODIGO);
                parametroRestResponse.valorParametro = (String) param.getAttribute(Columns.PSC_VLR);

                if (parametroRestResponse.codigoParametro.equals(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO)) {
                	if((Integer.parseInt(parametroRestResponse.valorParametro) != 1) && (Integer.parseInt(parametroRestResponse.valorParametro) != 2)) {
                		parametroRestResponse.valorParametro = "1";
                	}
                }

                if (parametroRestResponse.codigoParametro.equals(CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO)){
                	cnvCodVerba = parametroRestResponse.valorParametro;
                }

                listParametroRestResponse.add(parametroRestResponse);
            }

            RecuperarParametroRestRequest parametroRestRequest = new RecuperarParametroRestRequest();
            parametroRestRequest.csaCodigo = (String) svcCsaParams.get(0).getAttribute(Columns.PSC_CSA_CODIGO);
            parametroRestRequest.svcCodigo = (String) svcCsaParams.get(0).getAttribute(Columns.PSC_SVC_CODIGO);

            ConvenioDelegate convenioDelegate = new ConvenioDelegate();
            List<TransferObject> convenios = convenioDelegate.lstConvenios(cnvCodVerba, parametroRestRequest.csaCodigo, parametroRestRequest.svcCodigo, responsavel.getOrgCodigo(), true, responsavel);
            parametroRestRequest.cnvCodigo = (String) convenios.get(0).getAttribute(Columns.CNV_CODIGO);

            parametroRestRequest.listParametros = listParametroRestResponse;

            return Response.status(Response.Status.OK).entity(parametroRestRequest).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } catch (ParametroControllerException | ConvenioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ex.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }
}