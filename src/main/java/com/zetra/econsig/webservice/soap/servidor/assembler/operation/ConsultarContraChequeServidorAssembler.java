package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.ConsultarContraChequeServidor;

/**
 * <p>Title: ConsultarContraChequeServidorAssembler</p>
 * <p>Description: Assembler para ConsultarContraChequeServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarContraChequeServidorAssembler extends BaseAssembler {

    private ConsultarContraChequeServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConsultarContraChequeServidor consultarContraChequeServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(SENHA, consultarContraChequeServidor.getSenha());
        parametros.put(RSE_MATRICULA, consultarContraChequeServidor.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(consultarContraChequeServidor.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarContraChequeServidor.getEstabelecimentoCodigo()));
        parametros.put(SER_LOGIN, getValue(consultarContraChequeServidor.getLoginServidor()));
        parametros.put(PERIODO, getValueAsDate(consultarContraChequeServidor.getPeriodo()));

        return parametros;
    }
}