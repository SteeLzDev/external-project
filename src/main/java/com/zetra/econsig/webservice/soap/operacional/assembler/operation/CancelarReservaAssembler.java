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
import com.zetra.econsig.webservice.soap.operacional.v1.CancelarReserva;

/**
 * <p>Title: CancelarReservaAssembler</p>
 * <p>Description: Assembler para CancelarReserva.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CancelarReservaAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CancelarReservaAssembler.class);

    private CancelarReservaAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(CancelarReserva cancelarReserva) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, cancelarReserva.getUsuario());
        parametros.put(SENHA, cancelarReserva.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(cancelarReserva.getAdeIdentificador()));
        final Long adeNumero = getValue(cancelarReserva.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(cancelarReserva.getConvenio()));
        parametros.put(CLIENTE, getValue(cancelarReserva.getCliente()));
        parametros.put(TMO_OBS, getValue(cancelarReserva.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(cancelarReserva.getCodigoMotivoOperacao()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.CancelarReserva cancelarReserva) {
        final com.zetra.econsig.webservice.soap.operacional.v1.CancelarReserva cancelarReservaV1 = new com.zetra.econsig.webservice.soap.operacional.v1.CancelarReserva();
        try {
            BeanUtils.copyProperties(cancelarReservaV1, cancelarReserva);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        return toMap(cancelarReservaV1);
    }
}