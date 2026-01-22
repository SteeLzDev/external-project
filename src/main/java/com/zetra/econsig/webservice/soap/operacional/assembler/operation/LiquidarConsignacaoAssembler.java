package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.LiquidarConsignacao;

/**
 * <p>Title: AnEmptyAssembler</p>
 * <p>Description: Assembler para AnEmpty.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class LiquidarConsignacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LiquidarConsignacaoAssembler.class);

    private LiquidarConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(LiquidarConsignacao liquidarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, liquidarConsignacao.getUsuario());
        parametros.put(SENHA, liquidarConsignacao.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(liquidarConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(liquidarConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(liquidarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(liquidarConsignacao.getCliente()));
        parametros.put(TMO_OBS, getValue(liquidarConsignacao.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(liquidarConsignacao.getCodigoMotivoOperacao()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v3.LiquidarConsignacao liquidarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, liquidarConsignacao.getUsuario());
        parametros.put(SENHA, liquidarConsignacao.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(liquidarConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(liquidarConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(liquidarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(liquidarConsignacao.getCliente()));
        parametros.put(TMO_OBS, getValue(liquidarConsignacao.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(liquidarConsignacao.getCodigoMotivoOperacao()));

        final String periodo = getValue(liquidarConsignacao.getPeriodo());
        if (!TextHelper.isNull(periodo)) {
            parametros.put(PERIODO, periodo);
        }

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.LiquidarConsignacao liquidarConsignacao) {
       final com.zetra.econsig.webservice.soap.operacional.v3.LiquidarConsignacao liquidarConsignacaoV3 = new com.zetra.econsig.webservice.soap.operacional.v3.LiquidarConsignacao();
        try {
            BeanUtils.copyProperties(liquidarConsignacaoV3, liquidarConsignacao);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return toMap(liquidarConsignacaoV3);
    }
}