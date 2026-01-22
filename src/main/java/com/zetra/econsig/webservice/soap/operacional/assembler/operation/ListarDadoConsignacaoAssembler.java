package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TDA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.ListarDadoConsignacao;

/**
 * <p>Title: ListarDadoConsignacao</p>
 * <p>Description: Assembler para ListarDadoConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ListarDadoConsignacaoAssembler extends BaseAssembler {

    private ListarDadoConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ListarDadoConsignacao listarDadoConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(CLIENTE, getValue(listarDadoConsignacao.getCliente()));
        parametros.put(CONVENIO, getValue(listarDadoConsignacao.getConvenio()));
        parametros.put(USUARIO, listarDadoConsignacao.getUsuario());
        parametros.put(SENHA, listarDadoConsignacao.getSenha());

        parametros.put(ADE_IDENTIFICADOR, getValue(listarDadoConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(listarDadoConsignacao.getAdeNumero());
        if ((adeNumero == null) || (adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }

        parametros.put(TDA_CODIGO, getValue(listarDadoConsignacao.getDadoCodigo()));

        return parametros;
    }
}
