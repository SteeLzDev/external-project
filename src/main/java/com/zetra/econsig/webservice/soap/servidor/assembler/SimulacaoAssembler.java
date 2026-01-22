package com.zetra.econsig.webservice.soap.servidor.assembler;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.Simulacao;

/**
 * <p>Title: SimulacaoAssembler</p>
 * <p>Description: Assembler para Simulacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class SimulacaoAssembler extends BaseAssembler {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SimulacaoAssembler.class);

    private SimulacaoAssembler() {
        //
    }

    public static Simulacao toSimulacaoV1(Map<CamposAPI, Object> paramResposta) {
        final Simulacao simulacao = new Simulacao();

        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, Operacional V1
            BeanUtils.copyProperties(simulacao, com.zetra.econsig.webservice.soap.operacional.assembler.SimulacaoAssembler.toSimulacaoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LOG.warn(ex.getMessage(), ex);
        }

        return simulacao;
    }

    public static com.zetra.econsig.webservice.soap.servidor.v2.Simulacao toSimulacaoV2(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.servidor.v2.Simulacao simulacao = new com.zetra.econsig.webservice.soap.servidor.v2.Simulacao();

        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, Operacional V1
            BeanUtils.copyProperties(simulacao, com.zetra.econsig.webservice.soap.operacional.assembler.SimulacaoAssembler.toSimulacaoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LOG.warn(ex.getMessage(), ex);
        }

        return simulacao;
    }
}