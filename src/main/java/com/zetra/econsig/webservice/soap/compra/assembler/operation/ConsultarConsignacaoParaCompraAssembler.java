package com.zetra.econsig.webservice.soap.compra.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.ConsultarConsignacaoParaCompra;

/**
 * <p>Title: ConsultarConsignacaoParaCompraAssembler</p>
 * <p>Description: Assembler para ConsultarConsignacaoParaCompra.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarConsignacaoParaCompraAssembler extends BaseAssembler {

    private ConsultarConsignacaoParaCompraAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConsultarConsignacaoParaCompra consultarConsignacaoParaCompra) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, consultarConsignacaoParaCompra.getUsuario());
        parametros.put(SENHA, consultarConsignacaoParaCompra.getSenha());
        parametros.put(RSE_MATRICULA, consultarConsignacaoParaCompra.getMatricula());
        parametros.put(SER_CPF, getValue(consultarConsignacaoParaCompra.getCpf()));
        parametros.put(ADE_IDENTIFICADOR, getValue(consultarConsignacaoParaCompra.getAdeIdentificador()));
        final Long adeNumero = getValue(consultarConsignacaoParaCompra.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(consultarConsignacaoParaCompra.getConvenio()));
        parametros.put(CLIENTE, getValue(consultarConsignacaoParaCompra.getCliente()));
        parametros.put(SERVICO_CODIGO, consultarConsignacaoParaCompra.getServicoCodigo());
        parametros.put(RSE_BANCO, getValue(consultarConsignacaoParaCompra.getBanco()));
        parametros.put(RSE_AGENCIA, getValue(consultarConsignacaoParaCompra.getAgencia()));
        parametros.put(RSE_CONTA, getValue(consultarConsignacaoParaCompra.getConta()));
        parametros.put(SER_SENHA, getValue(consultarConsignacaoParaCompra.getSenhaServidor()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarConsignacaoParaCompra.getEstabelecimentoCodigo()));
        parametros.put(ORG_IDENTIFICADOR, getValue(consultarConsignacaoParaCompra.getOrgaoCodigo()));

        return parametros;
    }
}