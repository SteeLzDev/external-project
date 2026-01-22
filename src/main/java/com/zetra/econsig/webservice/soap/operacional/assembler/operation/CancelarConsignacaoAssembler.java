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

/**
 * <p>Title: CancelarConsignacaoAssembler</p>
 * <p>Description: Assembler para CancelarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CancelarConsignacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CancelarConsignacaoAssembler.class);

    private CancelarConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v1.CancelarConsignacao cancelarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, cancelarConsignacao.getUsuario());
        parametros.put(SENHA, cancelarConsignacao.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(cancelarConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(cancelarConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero > 0) && (adeNumero != Long.MAX_VALUE)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(cancelarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(cancelarConsignacao.getCliente()));
        parametros.put(TMO_OBS, getValue(cancelarConsignacao.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(cancelarConsignacao.getCodigoMotivoOperacao()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.CancelarConsignacao cancelarConsignacao) {
       final com.zetra.econsig.webservice.soap.operacional.v1.CancelarConsignacao cancelarConsignacaoV1 = new com.zetra.econsig.webservice.soap.operacional.v1.CancelarConsignacao();
        try {
            BeanUtils.copyProperties(cancelarConsignacaoV1, cancelarConsignacao);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return toMap(cancelarConsignacaoV1);
    }
}