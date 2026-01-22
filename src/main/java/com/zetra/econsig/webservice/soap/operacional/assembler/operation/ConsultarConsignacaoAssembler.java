package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;

/**
 * <p>Title: ConsultarConsignacaoAssembler</p>
 * <p>Description: Assembler para ConsultarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarConsignacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarConsignacaoAssembler.class);

    private ConsultarConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v1.ConsultarConsignacao consultarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, consultarConsignacao.getUsuario());
        parametros.put(SENHA, consultarConsignacao.getSenha());
        parametros.put(RSE_MATRICULA, consultarConsignacao.getMatricula());
        parametros.put(SER_CPF, getValue(consultarConsignacao.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(consultarConsignacao.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarConsignacao.getEstabelecimentoCodigo()));
        parametros.put(ADE_IDENTIFICADOR, getValue(consultarConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(consultarConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero > 0) && (adeNumero != Long.MAX_VALUE)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(consultarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(consultarConsignacao.getCliente()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.ConsultarConsignacao consultarConsignacao) {
        final com.zetra.econsig.webservice.soap.operacional.v1.ConsultarConsignacao consultarConsignacaoV1 = new com.zetra.econsig.webservice.soap.operacional.v1.ConsultarConsignacao();
        try {
            BeanUtils.copyProperties(consultarConsignacaoV1, consultarConsignacao);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        return toMap(consultarConsignacaoV1);
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v8.ConsultarConsignacao consultarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, consultarConsignacao.getUsuario());
        parametros.put(SENHA, consultarConsignacao.getSenha());
        parametros.put(RSE_MATRICULA, consultarConsignacao.getMatricula());
        parametros.put(SER_CPF, getValue(consultarConsignacao.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(consultarConsignacao.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarConsignacao.getEstabelecimentoCodigo()));

        final List<String> adeIdentificadores = consultarConsignacao.getAdeIdentificador();
        if ((!adeIdentificadores.isEmpty()) && !TextHelper.isNull(adeIdentificadores.get(0))) {
            parametros.put(ADE_IDENTIFICADOR, consultarConsignacao.getAdeIdentificador());
        }

        final List<Long> adeNumeros = consultarConsignacao.getAdeNumero();
        if ((!adeNumeros.isEmpty())) {
            final List<Long> adeNumeroArray = new ArrayList<>();
            for (int i = 0; i < adeNumeros.size(); i++) {
                final Long adeNumero = adeNumeros.get(i);
                if ((adeNumero != null) && (adeNumero > 0) && (adeNumero != Long.MAX_VALUE)) {
                    adeNumeroArray.add(adeNumero);
                }
            }
            parametros.put(ADE_NUMERO, adeNumeroArray);
        }

        parametros.put(CONVENIO, getValue(consultarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(consultarConsignacao.getCliente()));

        return parametros;
    }
}
