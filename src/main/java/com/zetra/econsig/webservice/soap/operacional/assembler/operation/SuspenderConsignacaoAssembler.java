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
import com.zetra.econsig.webservice.soap.operacional.v1.SuspenderConsignacao;

/**
 * <p>Title: SuspenderConsignacaoAssembler</p>
 * <p>Description: Assembler para SuspenderConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class SuspenderConsignacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SuspenderConsignacaoAssembler.class);

    private SuspenderConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(SuspenderConsignacao suspenderConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, suspenderConsignacao.getUsuario());
        parametros.put(SENHA, suspenderConsignacao.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(suspenderConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(suspenderConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(suspenderConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(suspenderConsignacao.getCliente()));
        parametros.put(TMO_OBS, getValue(suspenderConsignacao.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(suspenderConsignacao.getCodigoMotivoOperacao()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.SuspenderConsignacao suspenderConsignacao) {
        final com.zetra.econsig.webservice.soap.operacional.v1.SuspenderConsignacao suspenderConsignacaoV1 = new com.zetra.econsig.webservice.soap.operacional.v1.SuspenderConsignacao();
        try {
            BeanUtils.copyProperties(suspenderConsignacaoV1, suspenderConsignacao);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return toMap(suspenderConsignacaoV1);
    }
}