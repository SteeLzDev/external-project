package com.zetra.econsig.webservice.soap.operacional.assembler;

import static com.zetra.econsig.webservice.CamposAPI.DATA_DESCONTO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_REALIZADO;
import static com.zetra.econsig.webservice.CamposAPI.OBSERVACAO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.SPD_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SPD_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.VLR_PREVISTO;
import static com.zetra.econsig.webservice.CamposAPI.VLR_REALIZADO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v3.Parcela;

/**
 * <p>Title: ParcelaAssembler</p>
 * <p>Description: Assembler para Parcela.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ParcelaAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ParcelaAssembler.class);

    private ParcelaAssembler() {
    }

    public static Parcela toParcelaV3(Map<CamposAPI, Object> paramResposta) {
        final Parcela parcela = new Parcela();

        parcela.setDataDesconto(DateHelper.toPeriodString((Date) paramResposta.get(DATA_DESCONTO)));
        try {
            parcela.setDataRealizado(toXMLGregorianCalendar((Date) paramResposta.get(DATA_REALIZADO), false));
        } catch (final DatatypeConfigurationException e) {
            LOG.error(e.getMessage(), e);
        }
        parcela.setNumero(paramResposta.get(PRD_NUMERO) != null ? ((Short) paramResposta.get(PRD_NUMERO)).intValue() : 0);
        parcela.setStatus((String) paramResposta.get(SPD_CODIGO) + " - " + (String) paramResposta.get(SPD_DESCRICAO));
        parcela.setValorPrevisto(((BigDecimal) paramResposta.get(VLR_PREVISTO)).doubleValue());
        if (!TextHelper.isNull(paramResposta.get(VLR_REALIZADO))) {
        	parcela.setValorRealizado(((BigDecimal) paramResposta.get(VLR_REALIZADO)).doubleValue());
        }
        parcela.setObservacao((String) paramResposta.get(OBSERVACAO));

        return parcela;
    }
}