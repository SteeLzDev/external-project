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
import com.zetra.econsig.webservice.soap.operacional.v1.ReativarConsignacao;

/**
 * <p>Title: ReativarConsignacaoAssembler</p>
 * <p>Description: Assembler para ReativarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ReativarConsignacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReativarConsignacaoAssembler.class);

    private ReativarConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ReativarConsignacao reativarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, reativarConsignacao.getUsuario());
        parametros.put(SENHA, reativarConsignacao.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(reativarConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(reativarConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(reativarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(reativarConsignacao.getCliente()));
        parametros.put(TMO_OBS, getValue(reativarConsignacao.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(reativarConsignacao.getCodigoMotivoOperacao()));
        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.ReativarConsignacao reativarConsignacao) {
        final com.zetra.econsig.webservice.soap.operacional.v1.ReativarConsignacao reativarConsignacaoV1 = new com.zetra.econsig.webservice.soap.operacional.v1.ReativarConsignacao();
        try {
            BeanUtils.copyProperties(reativarConsignacaoV1, reativarConsignacao);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return toMap(reativarConsignacaoV1);
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v8.ReativarConsignacao reativarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, reativarConsignacao.getUsuario());
        parametros.put(SENHA, reativarConsignacao.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(reativarConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(reativarConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(reativarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(reativarConsignacao.getCliente()));
        parametros.put(TMO_OBS, getValue(reativarConsignacao.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(reativarConsignacao.getCodigoMotivoOperacao()));
        parametros.put(SER_LOGIN, getValue(reativarConsignacao.getLoginServidor()));
        parametros.put(SER_SENHA, getValue(reativarConsignacao.getSenhaServidor()));
        parametros.put(TOKEN, getValue(reativarConsignacao.getTokenAutServidor()));
        return parametros;
    }
}