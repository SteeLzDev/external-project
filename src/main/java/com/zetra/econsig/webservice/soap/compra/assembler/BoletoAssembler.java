package com.zetra.econsig.webservice.soap.compra.assembler;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.Boleto;

/**
 * <p>Title: BoletoAssembler</p>
 * <p>Description: Assembler para Boleto.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class BoletoAssembler extends BaseAssembler {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BoletoAssembler.class);

    private BoletoAssembler() {
        //
    }

    public static Boleto toBoletoV1(Map<CamposAPI, Object> paramResposta) {
        final Boleto boleto = new Boleto();

        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, Operacional V1
            BeanUtils.copyProperties(boleto, com.zetra.econsig.webservice.soap.operacional.assembler.BoletoAssembler.toBoletoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LOG.warn(ex.getMessage(), ex);
        }

        return boleto;
    }

}
