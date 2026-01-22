package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.ConsultarDadosCadastraisServidor;

/**
 * <p>Title: ConsultarDadosCadastraisServidorAssembler</p>
 * <p>Description: Assembler para ConsultarDadosCadastraisServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarDadosCadastraisServidorAssembler extends BaseAssembler {

    private ConsultarDadosCadastraisServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConsultarDadosCadastraisServidor consultarDadosCadastraisServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(RSE_MATRICULA, consultarDadosCadastraisServidor.getMatricula());
        parametros.put(EST_IDENTIFICADOR, getValue(consultarDadosCadastraisServidor.getEstabelecimentoCodigo()));
        parametros.put(SER_LOGIN, getValue(consultarDadosCadastraisServidor.getLoginServidor()));
        parametros.put(SENHA, consultarDadosCadastraisServidor.getSenha());

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.servidor.v3.ConsultarDadosCadastraisServidor consultarDadosCadastraisServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(RSE_MATRICULA, consultarDadosCadastraisServidor.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(consultarDadosCadastraisServidor.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarDadosCadastraisServidor.getEstabelecimentoCodigo()));
        parametros.put(SER_LOGIN, getValue(consultarDadosCadastraisServidor.getLoginServidor()));
        parametros.put(SENHA, consultarDadosCadastraisServidor.getSenha());

        return parametros;
    }
}