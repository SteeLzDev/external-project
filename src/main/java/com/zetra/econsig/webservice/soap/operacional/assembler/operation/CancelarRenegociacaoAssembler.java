package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.LOGIN_EXTERNO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.CancelarRenegociacao;

/**
 * <p>Title: CancelarRenegociacaoAssembler</p>
 * <p>Description: Assembler para CancelarRenegociacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CancelarRenegociacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CancelarRenegociacaoAssembler.class);

    private CancelarRenegociacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(CancelarRenegociacao cancelarRenegociacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, cancelarRenegociacao.getUsuario());
        parametros.put(SENHA, cancelarRenegociacao.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(cancelarRenegociacao.getAdeIdentificador()));
        final Long adeNumero = getValue(cancelarRenegociacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero > 0) && (adeNumero != Long.MAX_VALUE)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(cancelarRenegociacao.getConvenio()));
        parametros.put(CLIENTE, getValue(cancelarRenegociacao.getCliente()));
        parametros.put(TMO_OBS, getValue(cancelarRenegociacao.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(cancelarRenegociacao.getCodigoMotivoOperacao()));
        parametros.put(SER_SENHA, getValue(cancelarRenegociacao.getSenhaServidor()));
        parametros.put(LOGIN_EXTERNO, cancelarRenegociacao.getLoginExternoServidor());

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.CancelarRenegociacao cancelarRenegociacao) {
        final com.zetra.econsig.webservice.soap.operacional.v1.CancelarRenegociacao cancelarRenegociacaoV1 = new com.zetra.econsig.webservice.soap.operacional.v1.CancelarRenegociacao();
        try {
            BeanUtils.copyProperties(cancelarRenegociacaoV1, cancelarRenegociacao);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        return toMap(cancelarRenegociacaoV1);
    }
}