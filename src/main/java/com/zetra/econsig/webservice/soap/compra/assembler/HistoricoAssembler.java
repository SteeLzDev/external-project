package com.zetra.econsig.webservice.soap.compra.assembler;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.Historico;

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

        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, Operacional V1
            BeanUtils.copyProperties(historico, com.zetra.econsig.webservice.soap.operacional.assembler.HistoricoAssembler.toHistoricoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LOG.warn(ex.getMessage(), ex);
        }

        return historico;
    }
}
