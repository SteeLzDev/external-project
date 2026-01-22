package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.ConfirmarReserva;

/**
 * <p>Title: ConfirmarReservaAssembler</p>
 * <p>Description: Assembler para ConfirmarReserva.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConfirmarReservaAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfirmarReservaAssembler.class);

    private ConfirmarReservaAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConfirmarReserva confirmarReserva) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, confirmarReserva.getUsuario());
        parametros.put(SENHA, confirmarReserva.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(confirmarReserva.getAdeIdentificador()));
        final Long adeNumero = getValue(confirmarReserva.getAdeNumero());
        if ((adeNumero != null) && (adeNumero > 0) && (adeNumero != Long.MAX_VALUE)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(confirmarReserva.getConvenio()));
        parametros.put(CLIENTE, getValue(confirmarReserva.getCliente()));
        parametros.put(TMO_OBS, getValue(confirmarReserva.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(confirmarReserva.getCodigoMotivoOperacao()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.ConfirmarReserva confirmarReserva) {
       final com.zetra.econsig.webservice.soap.operacional.v1.ConfirmarReserva confirmarReservaV1 = new com.zetra.econsig.webservice.soap.operacional.v1.ConfirmarReserva();
        try {
            BeanUtils.copyProperties(confirmarReservaV1, confirmarReserva);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        return toMap(confirmarReservaV1);
    }
}