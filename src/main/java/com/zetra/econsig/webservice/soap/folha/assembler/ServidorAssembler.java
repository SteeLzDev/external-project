package com.zetra.econsig.webservice.soap.folha.assembler;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.Servidor;

/**
 * <p>Title: ServidorAssembler</p>
 * <p>Description: Assembler para Servidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ServidorAssembler extends BaseAssembler {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ServidorAssembler.class);

    private ServidorAssembler() {
        //
    }

    public static Servidor toServidorV1(Map<CamposAPI, Object> paramResposta) {
        final Servidor servidor = new Servidor();

        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, Operacional V1
            BeanUtils.copyProperties(servidor, com.zetra.econsig.webservice.soap.operacional.assembler.ServidorAssembler.toServidorV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LOG.warn(ex.getMessage(), ex);
        }

        return servidor;
    }
}