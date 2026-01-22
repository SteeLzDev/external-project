package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMAIL;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.VerificarEmailServidor;

/**
 * <p>Title: VerificarEmailServidorAssembler</p>
 * <p>Description: Assembler para VerificarEmailServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class VerificarEmailServidorAssembler extends BaseAssembler {

    private VerificarEmailServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(VerificarEmailServidor verificarEmailServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(EST_IDENTIFICADOR, getValue(verificarEmailServidor.getEstabelecimentoCodigo()));
        parametros.put(ORG_IDENTIFICADOR, getValue(verificarEmailServidor.getOrgaoCodigo()));
        parametros.put(RSE_MATRICULA, verificarEmailServidor.getMatricula());
        parametros.put(SER_EMAIL, verificarEmailServidor.getEmail());

        return parametros;
    }
}