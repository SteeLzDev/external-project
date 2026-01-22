package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarConvenio;

/**
 * <p>Title: ConsultarConvenioAssembler</p>
 * <p>Description: Assembler para ConsultarConvenio.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarConvenioAssembler extends BaseAssembler {

    private ConsultarConvenioAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConsultarConvenio consultarConvenio) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(ORG_IDENTIFICADOR, consultarConvenio.getCodigoOrgao());
        parametros.put(CSA_IDENTIFICADOR, consultarConvenio.getCodigoConsignataria());
        parametros.put(SVC_IDENTIFICADOR, consultarConvenio.getCodigoServico());
        parametros.put(EST_IDENTIFICADOR, getValue(consultarConvenio.getCodigoEstabelecimento()));
        parametros.put(CNV_COD_VERBA, consultarConvenio.getVerbaConvenio());
        parametros.put(USUARIO, consultarConvenio.getUsuario());
        parametros.put(SENHA, consultarConvenio.getSenha());

        return parametros;
    }
}