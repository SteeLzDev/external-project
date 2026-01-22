package com.zetra.econsig.webservice.soap.operacional.assembler;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.OCP_OBS;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_DATA_DESCONTO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_DATA_REALIZADO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_VLR_PREVISTO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_VLR_REALIZADO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SPD_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v8.ParcelaConsignacao;

/**
 * <p>Title: ParcelaConsignacaoAssembler</p>
 * <p>Description: Classe assembler do objeto parcela consignacao da requisicao soap ListarParcerlas
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class ParcelaConsignacaoAssembler extends BaseAssembler {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ParcelaConsignacaoAssembler.class);

    private ParcelaConsignacaoAssembler() {
        //
    }

    public static ParcelaConsignacao toParcelaConsignacaoV8(Map<CamposAPI, Object> paramResposta) {
        final ParcelaConsignacao parcelaConsignacao = new ParcelaConsignacao();

        parcelaConsignacao.setAdeNumero((Long) paramResposta.get(ADE_NUMERO));
        parcelaConsignacao.setAdeIdentificador((String) paramResposta.get(ADE_IDENTIFICADOR));
        parcelaConsignacao.setServico((String) paramResposta.get(SVC_DESCRICAO));
        parcelaConsignacao.setServicoCodigo((String) paramResposta.get(SVC_IDENTIFICADOR));
        parcelaConsignacao.setCodVerba((String) paramResposta.get(CNV_COD_VERBA));
        parcelaConsignacao.setServidor((String) paramResposta.get(SER_NOME));
        parcelaConsignacao.setCpf((String) paramResposta.get(SER_CPF));
        parcelaConsignacao.setMatricula((String) paramResposta.get(RSE_MATRICULA));
        parcelaConsignacao.setNumeroParcela(((Short) paramResposta.get(PRD_NUMERO)).intValue());
        parcelaConsignacao.setDataDesconto(DateHelper.format((Date) paramResposta.get(PRD_DATA_DESCONTO), "yyyy-MM-dd"));

        try {
            parcelaConsignacao.setDataRealizado(toXMLGregorianCalendar((Date) paramResposta.get(PRD_DATA_REALIZADO), false));
        } catch (final DatatypeConfigurationException e) {
            LOG.warn("ERRO AO RECUPERAR DATA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
        }

        final BigDecimal prdVlrPrevisto = (BigDecimal) paramResposta.get(PRD_VLR_PREVISTO);
        if (!TextHelper.isNull(prdVlrPrevisto)) {
            parcelaConsignacao.setValorPrevisto(prdVlrPrevisto.doubleValue());
        } else {
            parcelaConsignacao.setValorPrevisto(null);
        }

        final BigDecimal prdVlrRealizado = (BigDecimal) paramResposta.get(PRD_VLR_REALIZADO);
        if (!TextHelper.isNull(prdVlrRealizado)) {
            parcelaConsignacao.setValorRealizado(prdVlrRealizado.doubleValue());
        } else {
            parcelaConsignacao.setValorRealizado(null);
        }

        parcelaConsignacao.setStatus((String) paramResposta.get(SPD_DESCRICAO));
        parcelaConsignacao.setObservacao((String) paramResposta.get(OCP_OBS));
        return parcelaConsignacao;
    }
}
