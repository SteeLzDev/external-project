package com.zetra.econsig.webservice.soap.operacional.assembler;

import static com.zetra.econsig.webservice.CamposAPI.DATA;
import static com.zetra.econsig.webservice.CamposAPI.DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.RESPONSAVEL;
import static com.zetra.econsig.webservice.CamposAPI.TIPO;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.Historico;

/**
 * <p>Title: HistoricoAssembler</p>
 * <p>Description: Assembler para Historico.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class HistoricoAssembler extends BaseAssembler {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(HistoricoAssembler.class);

    private HistoricoAssembler() {
        //
    }

    public static Historico toHistoricoV1(Map<CamposAPI, Object> paramResposta) {
        final Historico historico = new Historico();

        if (!TextHelper.isNull(paramResposta.get(DATA))) {
            try {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateHelper.parse((String) paramResposta.get(DATA), LocaleHelper.getDateTimePattern()));
                historico.setData(toXMLGregorianCalendar(calendar.getTime(), true));
            } catch (ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DA OCORRENCIA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }
        historico.setResponsavel((String) paramResposta.get(RESPONSAVEL));
        historico.setTipo((String) paramResposta.get(TIPO));
        historico.setDescricao((String) paramResposta.get(DESCRICAO));

        return historico;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v3.Historico toHistoricoV3(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v3.Historico historico = new com.zetra.econsig.webservice.soap.operacional.v3.Historico();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(historico, toHistoricoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return historico;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v4.Historico toHistoricoV4(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v4.Historico historico = new com.zetra.econsig.webservice.soap.operacional.v4.Historico();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(historico, toHistoricoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return historico;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v6.Historico toHistoricoV6(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v6.Historico historico = new com.zetra.econsig.webservice.soap.operacional.v6.Historico();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(historico, toHistoricoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return historico;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v7.Historico toHistoricoV7(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v7.Historico historico = new com.zetra.econsig.webservice.soap.operacional.v7.Historico();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(historico, toHistoricoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return historico;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v8.Historico toHistoricoV8(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v8.Historico historico = new com.zetra.econsig.webservice.soap.operacional.v8.Historico();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(historico, toHistoricoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return historico;
    }
}