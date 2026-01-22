package com.zetra.econsig.webservice.soap.operacional.assembler;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNATARIA;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.RANKING;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.ObjectFactory;
import com.zetra.econsig.webservice.soap.operacional.v1.Simulacao;

/**
 * <p>Title: SimulacaoAssembler</p>
 * <p>Description: Assembler para Simulacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class SimulacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SimulacaoAssembler.class);

    private SimulacaoAssembler() {
    }

    public static Simulacao toSimulacaoV1(Map<CamposAPI, Object> paramResposta) {
        final ObjectFactory factory = new ObjectFactory();
        final Simulacao simulacao = new Simulacao();

        simulacao.setConsignataria((String) paramResposta.get(CONSIGNATARIA));
        simulacao.setConsignatariaCodigo((String) paramResposta.get(CSA_IDENTIFICADOR));
        simulacao.setRanking(Short.parseShort((String) paramResposta.get(RANKING)));
        simulacao.setServico(factory.createSimulacaoServico((String) paramResposta.get(SERVICO)));
        simulacao.setServicoCodigo(factory.createSimulacaoServicoCodigo((String) paramResposta.get(SVC_IDENTIFICADOR)));

        try {
            simulacao.setValorParcela(Double.parseDouble(NumberHelper.reformat(paramResposta.get(VALOR_PARCELA).toString(), NumberHelper.getLang(), "en", 2, 8)));
        } catch (final NumberFormatException | ParseException e) {
            LOG.warn("ERRO AO RECUPERAR VALOR DA PARCELA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
        }
        try {
            simulacao.setValorLiberado(Double.parseDouble(NumberHelper.reformat((String) paramResposta.get(VALOR_LIBERADO), NumberHelper.getLang(), "en", 2, 8)));
        } catch (final NumberFormatException | ParseException e) {
            LOG.warn("ERRO AO RECUPERAR VALOR LIBERADO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
        }
        try {
            simulacao.setTaxaJuros(Double.parseDouble(NumberHelper.reformat((String) paramResposta.get(TAXA_JUROS), NumberHelper.getLang(), "en", 2, 8)));
        } catch (final NumberFormatException | ParseException e) {
            LOG.warn("ERRO AO RECUPERAR TAXAS/CET PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
        }

        return simulacao;
    }


    public static com.zetra.econsig.webservice.soap.operacional.v8.Simulacao toSimulacaoV8(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v8.Simulacao simulacao = new com.zetra.econsig.webservice.soap.operacional.v8.Simulacao();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(simulacao, toSimulacaoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return simulacao;
    }
}