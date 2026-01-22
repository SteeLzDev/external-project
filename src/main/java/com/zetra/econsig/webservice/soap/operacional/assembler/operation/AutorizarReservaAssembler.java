package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.AutorizarReserva;

/**
 * <p>Title: AutorizarReservaAssembler</p>
 * <p>Description: Assembler para AutorizarReserva.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class AutorizarReservaAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutorizarReservaAssembler.class);

    private AutorizarReservaAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(AutorizarReserva autorizarReserva) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, autorizarReserva.getUsuario());
        parametros.put(SENHA, autorizarReserva.getSenha());
        parametros.put(SER_SENHA, autorizarReserva.getSenhaServidor());
        parametros.put(TOKEN, autorizarReserva.getTokenAutServidor());
        parametros.put(ADE_IDENTIFICADOR, getValue(autorizarReserva.getAdeIdentificador()));
        final Long adeNumero = getValue(autorizarReserva.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(autorizarReserva.getConvenio()));
        parametros.put(CLIENTE, getValue(autorizarReserva.getCliente()));
        parametros.put(SER_LOGIN, autorizarReserva.getLoginServidor());
        parametros.put(TMO_OBS, getValue(autorizarReserva.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(autorizarReserva.getCodigoMotivoOperacao()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.AutorizarReserva autorizarReserva) {
       final com.zetra.econsig.webservice.soap.operacional.v1.AutorizarReserva autorizarReservaV1 = new com.zetra.econsig.webservice.soap.operacional.v1.AutorizarReserva();
        try {
            BeanUtils.copyProperties(autorizarReservaV1, autorizarReserva);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        return toMap(autorizarReservaV1);
    }
}