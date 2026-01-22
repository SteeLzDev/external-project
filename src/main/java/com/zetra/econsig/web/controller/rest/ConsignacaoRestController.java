package com.zetra.econsig.web.controller.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: BuscarCepRestController</p>
 * <p>Description: Buscar Cep Rest Controller</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 * */
@RestController
public class ConsignacaoRestController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsignacaoRestController.class);

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @RequestMapping(value = "/v3/verificarDataFim", method = RequestMethod.POST)
    public Response verificarDataFim (@RequestBody String [] adeCodigos, HttpServletRequest request) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            if(ArrayUtils.isEmpty(adeCodigos)) {
                return Response.status(Response.Status.BAD_REQUEST).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }
            Date dataAtual = DateHelper.clearHourTime(new Date());

            List<Long> adesNumeros = compararData(adeCodigos, responsavel, dataAtual);

            return Response.status(Response.Status.OK).entity(adesNumeros.toArray()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();

        } catch (Exception  e) {
            LOG.error(e.getLocalizedMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

    }

    public static class CompararDataFimRequest {
        public String dataInformada;
        public String [] adeCodigos;
    }

    @RequestMapping(value = "/v3/compararComDataFim", method = RequestMethod.POST)
    public Response compararDataFim (@RequestBody CompararDataFimRequest compararDataFimRequest, HttpServletRequest request) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            if(compararDataFimRequest == null || TextHelper.isNull(compararDataFimRequest.dataInformada) || ArrayUtils.isEmpty(compararDataFimRequest.adeCodigos)) {
                return Response.status(Response.Status.BAD_REQUEST).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }

            List<Long> adesNumeros = compararData(compararDataFimRequest.adeCodigos, responsavel, DateHelper.objectToDate(compararDataFimRequest.dataInformada));
            return Response.status(Response.Status.OK).entity(adesNumeros.toArray()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();

        } catch (Exception  e) {
            LOG.error(e.getLocalizedMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

    }

    private List<Long> compararData(String[] adeCodigos, AcessoSistema responsavel, Date dataAtual) throws AutorizacaoControllerException, ParametroControllerException {
        boolean paramPreservarParcelaHabilitado = ParamSist.paramEquals(CodedValues.TPC_PRESERVA_PRD_REJEITADA, CodedValues.TPC_SIM, responsavel);

        List<Long> adesNumeros = new ArrayList<>();
        for (String adeCodigo : adeCodigos) {

            CustomTransferObject aut = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            String svcCodigo = (String) aut.getAttribute(Columns.SVC_CODIGO);
            String csaCodigo = (String)aut.getAttribute(Columns.CSA_CODIGO);

            boolean preservarParcelas = false;
            if (paramPreservarParcelaHabilitado) {
                preservarParcelas = preservarParcelas(svcCodigo, csaCodigo, responsavel);
            }

            if (!preservarParcelas) {

                Date adeAnoMesFim = (Date) aut.getAttribute(Columns.ADE_ANO_MES_FIM);

                if ( adeAnoMesFim != null && adeAnoMesFim.before(dataAtual)) {
                    adesNumeros.add((Long) aut.getAttribute(Columns.ADE_NUMERO));
                }

            }

        }
        return adesNumeros;
    }

    private Boolean preservarParcelas (String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        Boolean preserva = null;

        if (ParamSist.getBoolParamSist(CodedValues.TPC_CSA_ALTERA_PRESERVA_PRD, responsavel)) {

            List<String> tpsCodigo = new ArrayList<>();
            tpsCodigo.add(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL);
            List<TransferObject> params = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigo, false, responsavel);

            if (params != null && params.size() == 1) {
                CustomTransferObject param = (CustomTransferObject) params.get(0);
                if (param != null && param.getAttribute(Columns.PSC_VLR) != null && !param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) {
                    preserva = param.getAttribute(Columns.PSC_VLR).toString().equalsIgnoreCase("S");
                }
            }

        }

        Boolean defaultPreserva = ParamSist.paramEquals(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, CodedValues.TPC_SIM, responsavel);
        preserva = (preserva == null ? defaultPreserva : preserva);

        return preserva;
    }
}
