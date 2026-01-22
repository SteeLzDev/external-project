package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarRegras;

public class ConsultarRegrasAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarRegrasAssembler.class);

    private ConsultarRegrasAssembler() {}


    public static Map<CamposAPI, Object> toMap(ConsultarRegras consultarRegras) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, consultarRegras.getUsuario());
        parametros.put(SENHA, consultarRegras.getSenha());
        if (consultarRegras.getCliente() != null) {
            parametros.put(CLIENTE, consultarRegras.getCliente());
        }
        if (consultarRegras.getConvenio() != null) {
            parametros.put(CONVENIO, consultarRegras.getConvenio());
        }

        return parametros;
    }
}
