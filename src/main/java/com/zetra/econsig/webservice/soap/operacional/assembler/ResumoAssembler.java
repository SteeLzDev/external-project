package com.zetra.econsig.webservice.soap.operacional.assembler;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_NOME;
import static com.zetra.econsig.webservice.CamposAPI.DATA_RESERVA;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_PRD_PAGAS;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RESPONSAVEL;
import static com.zetra.econsig.webservice.CamposAPI.SAD_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.ObjectFactory;
import com.zetra.econsig.webservice.soap.operacional.v1.Resumo;

/**
 * <p>Title: ResumoAssembler</p>
 * <p>Description: Assembler para Resumo.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ResumoAssembler extends BaseAssembler {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ResumoAssembler.class);

    private ResumoAssembler() {
        //
    }

    public static Resumo toResumoV1(Map<CamposAPI, Object> paramResposta) {
        final ObjectFactory factory = new ObjectFactory();
        final Resumo resumo = new Resumo();

        resumo.setAdeIdentificador((String) paramResposta.get(ADE_IDENTIFICADOR));
        resumo.setAdeNumero((Long) paramResposta.get(ADE_NUMERO));
        resumo.setIndice((String) paramResposta.get(ADE_INDICE));
        resumo.setResponsavel((String) paramResposta.get(RESPONSAVEL));
        resumo.setServico((String) paramResposta.get(SERVICO));
        resumo.setCodVerba((String) paramResposta.get(CNV_COD_VERBA));
        if (!TextHelper.isNull(paramResposta.get(DATA_RESERVA))) {
            try {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateHelper.parse((String) paramResposta.get(DATA_RESERVA), LocaleHelper.getDateTimePattern()));
                resumo.setDataReserva(toXMLGregorianCalendar(calendar.getTime(), true));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DE RESERVA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }
        try {
            resumo.setValorParcela(Double.parseDouble(NumberHelper.reformat(paramResposta.get(VALOR_PARCELA).toString(), NumberHelper.getLang(), "en", 2, 8)));
        } catch (final NumberFormatException | ParseException e) {
            LOG.warn("ERRO AO RECUPERAR VALOR DA PARCELA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
        }
        resumo.setPrazo(Integer.parseInt(paramResposta.get(PRAZO).toString()));
        resumo.setPagas((paramResposta.get(ADE_PRD_PAGAS) != null) ? Integer.valueOf(paramResposta.get(ADE_PRD_PAGAS).toString()) : 0);
        resumo.setSituacao((String) paramResposta.get(SITUACAO));
        resumo.setServicoCodigo((String) paramResposta.get(SVC_IDENTIFICADOR));
        resumo.setStatusCodigo((String) paramResposta.get(SAD_CODIGO));
        resumo.setConsignataria((String) paramResposta.get(CSA_NOME));
        resumo.setConsignatariaCodigo(factory.createResumoConsignatariaCodigo((String) paramResposta.get(CSA_IDENTIFICADOR)));

        return resumo;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v3.Resumo toResumoV3(Map<CamposAPI, Object> paramResposta) {
       final com.zetra.econsig.webservice.soap.operacional.v3.Resumo resumo = new com.zetra.econsig.webservice.soap.operacional.v3.Resumo();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(resumo, ResumoAssembler.toResumoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return resumo;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v4.Resumo toResumoV4(Map<CamposAPI, Object> paramResposta) {
       final com.zetra.econsig.webservice.soap.operacional.v4.Resumo resumo = new com.zetra.econsig.webservice.soap.operacional.v4.Resumo();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(resumo, ResumoAssembler.toResumoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return resumo;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v6.Resumo toResumoV6(Map<CamposAPI, Object> paramResposta) {
       final com.zetra.econsig.webservice.soap.operacional.v6.Resumo resumo = new com.zetra.econsig.webservice.soap.operacional.v6.Resumo();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(resumo, ResumoAssembler.toResumoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return resumo;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v7.Resumo toResumoV7(Map<CamposAPI, Object> paramResposta) {
       final com.zetra.econsig.webservice.soap.operacional.v7.Resumo resumo = new com.zetra.econsig.webservice.soap.operacional.v7.Resumo();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(resumo, ResumoAssembler.toResumoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return resumo;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v8.Resumo toResumoV8(Map<CamposAPI, Object> paramResposta) {
       final com.zetra.econsig.webservice.soap.operacional.v8.Resumo resumo = new com.zetra.econsig.webservice.soap.operacional.v8.Resumo();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(resumo, ResumoAssembler.toResumoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return resumo;
    }
}