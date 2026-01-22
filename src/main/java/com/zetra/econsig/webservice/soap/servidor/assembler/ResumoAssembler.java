package com.zetra.econsig.webservice.soap.servidor.assembler;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.Resumo;

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
        final Resumo resumo = new Resumo();

        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, Operacional V1
            BeanUtils.copyProperties(resumo, com.zetra.econsig.webservice.soap.operacional.assembler.ResumoAssembler.toResumoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LOG.warn(ex.getMessage(), ex);
        }

        return resumo;
    }
}