package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.ALTERA_VLR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CODIGO_AUTORIZACAO;
import static com.zetra.econsig.webservice.CamposAPI.COEFICIENTE;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.NOVO_ADE_IDENTIFICADOR;
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
import com.zetra.econsig.webservice.soap.operacional.v1.ConfirmarSolicitacao;

/**
 * <p>Title: ConfirmarSolicitacaoAssembler</p>
 * <p>Description: Assembler para ConfirmarSolicitacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConfirmarSolicitacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfirmarSolicitacaoAssembler.class);

    private ConfirmarSolicitacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConfirmarSolicitacao confirmarSolicitacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, confirmarSolicitacao.getUsuario());
        parametros.put(SENHA, confirmarSolicitacao.getSenha());
        parametros.put(SER_SENHA, confirmarSolicitacao.getSenhaServidor());
        parametros.put(TOKEN, confirmarSolicitacao.getTokenAutServidor());
        parametros.put(ADE_IDENTIFICADOR, getValue(confirmarSolicitacao.getAdeIdentificador()));
        parametros.put(NOVO_ADE_IDENTIFICADOR, getValue(confirmarSolicitacao.getNovoAdeIdentificador()));

        final Double cftVlr = confirmarSolicitacao.getCoeficiente();
        if ((cftVlr == null) || cftVlr.equals(Double.NaN) || (cftVlr == 0.0)) {
            parametros.put(COEFICIENTE, null);
        } else {
            parametros.put(COEFICIENTE, cftVlr);
        }
        final Long adeNumero = getValue(confirmarSolicitacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(ALTERA_VLR_LIBERADO, confirmarSolicitacao.getAlteraValorLiberado());
        parametros.put(RSE_BANCO, getValue(confirmarSolicitacao.getBanco()));
        parametros.put(RSE_AGENCIA, getValue(confirmarSolicitacao.getAgencia()));
        parametros.put(RSE_CONTA, getValue(confirmarSolicitacao.getConta()));
        parametros.put(CONVENIO, getValue(confirmarSolicitacao.getConvenio()));
        parametros.put(CLIENTE, getValue(confirmarSolicitacao.getCliente()));
        parametros.put(CODIGO_AUTORIZACAO, confirmarSolicitacao.getCodigoAutorizacao());
        parametros.put(SER_LOGIN, confirmarSolicitacao.getLoginServidor());
        parametros.put(TMO_OBS, getValue(confirmarSolicitacao.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(confirmarSolicitacao.getCodigoMotivoOperacao()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.ConfirmarSolicitacao confirmarSolicitacao) {
       final com.zetra.econsig.webservice.soap.operacional.v1.ConfirmarSolicitacao confirmarSolicitacaoV1 = new com.zetra.econsig.webservice.soap.operacional.v1.ConfirmarSolicitacao();
        try {
            BeanUtils.copyProperties(confirmarSolicitacaoV1, confirmarSolicitacao);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        return toMap(confirmarSolicitacaoV1);
    }
}