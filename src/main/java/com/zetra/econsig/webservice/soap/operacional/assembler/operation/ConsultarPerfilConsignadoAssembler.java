package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import com.zetra.econsig.webservice.CamposAPI;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v8.ConsultarPerfilConsignado;

import java.util.EnumMap;
import java.util.Map;

public class ConsultarPerfilConsignadoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarPerfilConsignadoAssembler.class);

    private ConsultarPerfilConsignadoAssembler() {}


    public static Map<CamposAPI, Object> toMap(ConsultarPerfilConsignado consultarPerfilConsignado) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, consultarPerfilConsignado.getUsuario());
        parametros.put(SENHA, consultarPerfilConsignado.getSenha());
        if (consultarPerfilConsignado.getCliente() != null) {
            parametros.put(CLIENTE, consultarPerfilConsignado.getCliente());
        }
        if (consultarPerfilConsignado.getConvenio() != null) {
            parametros.put(CONVENIO, consultarPerfilConsignado.getConvenio());
        }
        if (consultarPerfilConsignado.getCpf() != null) {
            parametros.put(SER_CPF, getValue(consultarPerfilConsignado.getCpf()));
        }
        if (consultarPerfilConsignado.getMatricula() != null) {
            parametros.put(RSE_MATRICULA, consultarPerfilConsignado.getMatricula());
        }
        if (consultarPerfilConsignado.getOrgaoCodigo() != null) {
            parametros.put(ORG_IDENTIFICADOR, getValue(consultarPerfilConsignado.getOrgaoCodigo()));
        }
        if (consultarPerfilConsignado.getEstabelecimentoCodigo() != null) {
            parametros.put(EST_IDENTIFICADOR, getValue(consultarPerfilConsignado.getEstabelecimentoCodigo()));
        }

        return parametros;
    }
}
