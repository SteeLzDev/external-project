package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.DESCRICAO_ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.Anexo;

/**
 * <p>Title: IncluirAnexoConsignacaoAssembler</p>
 * <p>Description: Assembler para IncluirAnexoConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class IncluirAnexoConsignacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(IncluirAnexoConsignacaoAssembler.class);

    private IncluirAnexoConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v1.IncluirAnexoConsignacao incluirAnexoConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, incluirAnexoConsignacao.getUsuario());
        parametros.put(SENHA, incluirAnexoConsignacao.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(incluirAnexoConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(incluirAnexoConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(incluirAnexoConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(incluirAnexoConsignacao.getCliente()));
        parametros.put(ANEXO, incluirAnexoConsignacao.getAnexo());
        parametros.put(DESCRICAO_ANEXO, getValue(incluirAnexoConsignacao.getDescricaoAnexo()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v7.IncluirAnexoConsignacao incluirAnexoConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, incluirAnexoConsignacao.getUsuario());
        parametros.put(SENHA, incluirAnexoConsignacao.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(incluirAnexoConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(incluirAnexoConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(incluirAnexoConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(incluirAnexoConsignacao.getCliente()));
        if (incluirAnexoConsignacao.getAnexo() != null) {
            try {
                final Anexo anexo = new Anexo();
                BeanUtils.copyProperties(anexo, incluirAnexoConsignacao.getAnexo());
                parametros.put(ANEXO, anexo);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        parametros.put(DESCRICAO_ANEXO, getValue(incluirAnexoConsignacao.getDescricaoAnexo()));
        parametros.put(PERIODO, getValue(incluirAnexoConsignacao.getPeriodo()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v8.IncluirAnexoConsignacao incluirAnexoConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, incluirAnexoConsignacao.getUsuario());
        parametros.put(SENHA, incluirAnexoConsignacao.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(incluirAnexoConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(incluirAnexoConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(incluirAnexoConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(incluirAnexoConsignacao.getCliente()));
        if (incluirAnexoConsignacao.getAnexo() != null) {
            try {
                final Anexo anexo = new Anexo();
                BeanUtils.copyProperties(anexo, incluirAnexoConsignacao.getAnexo());
                parametros.put(ANEXO, anexo);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        parametros.put(DESCRICAO_ANEXO, getValue(incluirAnexoConsignacao.getDescricaoAnexo()));
        parametros.put(PERIODO, getValue(incluirAnexoConsignacao.getPeriodo()));
        parametros.put(TIPO_ARQUIVO, getValue(incluirAnexoConsignacao.getTipoArquivo()));

        return parametros;
    }
}
