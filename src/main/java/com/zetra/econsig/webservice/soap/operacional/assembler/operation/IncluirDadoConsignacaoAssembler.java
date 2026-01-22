package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.DAD_VALOR;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TDA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.IncluirDadoConsignacao;

/**
 * <p>Title: IncluirDadoConsignacaoAssembler</p>
 * <p>Description: Assembler para IncluirDadoConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class IncluirDadoConsignacaoAssembler extends BaseAssembler {

    private IncluirDadoConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(IncluirDadoConsignacao incluirDadoConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(CLIENTE, getValue(incluirDadoConsignacao.getCliente()));
        parametros.put(CONVENIO, getValue(incluirDadoConsignacao.getConvenio()));
        parametros.put(USUARIO, incluirDadoConsignacao.getUsuario());
        parametros.put(SENHA, incluirDadoConsignacao.getSenha());

        parametros.put(ADE_IDENTIFICADOR, getValue(incluirDadoConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(incluirDadoConsignacao.getAdeNumero());
        if ((adeNumero == null) || (adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }

        parametros.put(TDA_CODIGO, incluirDadoConsignacao.getDadoCodigo());
        parametros.put(DAD_VALOR, getValue(incluirDadoConsignacao.getDadoValor()));

        return parametros;
    }
}
