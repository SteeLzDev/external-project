package com.zetra.econsig.webservice.soap.servidor.assembler;

import static com.zetra.econsig.webservice.CamposAPI.DATA_CARGA;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO;

import java.text.ParseException;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.Contracheque;

/**
 * <p>Title: ContrachequeAssembler</p>
 * <p>Description: Assembler para Contracheque.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ContrachequeAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ContrachequeAssembler.class);

    private ContrachequeAssembler() {
    }

    public static Contracheque toContrachequeV1( Map<CamposAPI, Object> paramResposta) {
        final Contracheque contracheque = new Contracheque();

        if (!TextHelper.isNull(paramResposta.get(PERIODO))) {
            try {
                contracheque.setPeriodo(toXMLGregorianCalendar(DateHelper.parsePeriodString((String) paramResposta.get(PERIODO)), true));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR O PERIODO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        if (!TextHelper.isNull(paramResposta.get(DATA_CARGA))) {
            try {
                contracheque.setDataCarga(toXMLGregorianCalendar(DateHelper.parse((String) paramResposta.get(DATA_CARGA), LocaleHelper.getDateTimePattern()), true));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DA CARGA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        contracheque.setTexto((String) paramResposta.get(TEXTO));

        return contracheque;
    }
}