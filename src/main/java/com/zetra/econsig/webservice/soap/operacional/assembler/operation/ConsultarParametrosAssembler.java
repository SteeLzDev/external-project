package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_SISTEMA;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;

/**
 * <p>Title: ConsultarParametros</p>
 * <p>Description: Assembler para ConsultarParametros.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarParametrosAssembler extends BaseAssembler {

    private ConsultarParametrosAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v1.ConsultarParametros consultarParametros) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, consultarParametros.getUsuario());
        parametros.put(SENHA, consultarParametros.getSenha());
        parametros.put(CNV_COD_VERBA, getValue(consultarParametros.getCodVerba()));
        parametros.put(CLIENTE, getValue(consultarParametros.getCliente()));
        parametros.put(CONVENIO, getValue(consultarParametros.getConvenio()));
        parametros.put(SERVICO_CODIGO, getValue(consultarParametros.getServicoCodigo()));
        parametros.put(ORG_IDENTIFICADOR, consultarParametros.getOrgaoCodigo());
        parametros.put(EST_IDENTIFICADOR, consultarParametros.getEstabelecimentoCodigo());

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v2.ConsultarParametros consultarParametros) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, consultarParametros.getUsuario());
        parametros.put(SENHA, consultarParametros.getSenha());
        parametros.put(CNV_COD_VERBA, getValue(consultarParametros.getCodVerba()));
        parametros.put(CLIENTE, getValue(consultarParametros.getCliente()));
        parametros.put(CONVENIO, getValue(consultarParametros.getConvenio()));
        parametros.put(SERVICO_CODIGO, getValue(consultarParametros.getServicoCodigo()));
        parametros.put(ORG_IDENTIFICADOR, consultarParametros.getOrgaoCodigo());
        parametros.put(EST_IDENTIFICADOR, consultarParametros.getEstabelecimentoCodigo());

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v8.ConsultarParametros consultarParametros) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, consultarParametros.getUsuario());
        parametros.put(SENHA, consultarParametros.getSenha());
        parametros.put(CNV_COD_VERBA, getValue(consultarParametros.getCodVerba()));
        parametros.put(CLIENTE, getValue(consultarParametros.getCliente()));
        parametros.put(CONVENIO, getValue(consultarParametros.getConvenio()));
        parametros.put(SERVICO_CODIGO, getValue(consultarParametros.getServicoCodigo()));
        parametros.put(ORG_IDENTIFICADOR, consultarParametros.getOrgaoCodigo());
        parametros.put(EST_IDENTIFICADOR, consultarParametros.getEstabelecimentoCodigo());
        parametros.put(DADOS_SISTEMA, consultarParametros.getDadosSistema());

        return parametros;
    }
}
