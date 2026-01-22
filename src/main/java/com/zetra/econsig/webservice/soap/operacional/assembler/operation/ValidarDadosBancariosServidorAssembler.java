package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v7.ValidarDadosBancariosServidor;

/**
 * <p>Title: ValidarDadosBancariosServidorAssembler</p>
 * <p>Description: Assembler para ValidarDadosBancariosServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ValidarDadosBancariosServidorAssembler extends BaseAssembler {

    private ValidarDadosBancariosServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ValidarDadosBancariosServidor validarDadosBancariosServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, validarDadosBancariosServidor.getUsuario());
        parametros.put(SENHA, validarDadosBancariosServidor.getSenha());
        parametros.put(CONVENIO, getValue(validarDadosBancariosServidor.getConvenio()));
        parametros.put(CLIENTE, getValue(validarDadosBancariosServidor.getCliente()));

        parametros.put(EST_IDENTIFICADOR, getValue(validarDadosBancariosServidor.getEstabelecimentoCodigo()));
        parametros.put(ORG_IDENTIFICADOR, getValue(validarDadosBancariosServidor.getOrgaoCodigo()));
        parametros.put(RSE_MATRICULA, getValue(validarDadosBancariosServidor.getMatricula()));
        parametros.put(SER_CPF, getValue(validarDadosBancariosServidor.getCpf()));

        parametros.put(RSE_BANCO, validarDadosBancariosServidor.getBanco());
        parametros.put(RSE_AGENCIA, validarDadosBancariosServidor.getAgencia());
        parametros.put(RSE_CONTA, validarDadosBancariosServidor.getConta());

        return parametros;
    }
}