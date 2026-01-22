package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.GRUPO_PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.RESPOSTA_PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.VerificarRespostaDadosCadastrais;

/**
 * <p>Title: VerificarRespostaDadosCadastraisAssembler</p>
 * <p>Description: Assembler para VerificarRespostaDadosCadastrais.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class VerificarRespostaDadosCadastraisAssembler extends BaseAssembler {

    private VerificarRespostaDadosCadastraisAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(VerificarRespostaDadosCadastrais verificarRespostaDadosCadastrais) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(SENHA, verificarRespostaDadosCadastrais.getSenha());
        parametros.put(RSE_MATRICULA, verificarRespostaDadosCadastrais.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(verificarRespostaDadosCadastrais.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(verificarRespostaDadosCadastrais.getEstabelecimentoCodigo()));
        parametros.put(SER_LOGIN, getValue(verificarRespostaDadosCadastrais.getLoginServidor()));
        parametros.put(GRUPO_PERGUNTA, verificarRespostaDadosCadastrais.getGrupoPergunta());
        parametros.put(PERGUNTA, verificarRespostaDadosCadastrais.getPergunta());
        parametros.put(RESPOSTA_PERGUNTA, verificarRespostaDadosCadastrais.getResposta());
        parametros.put(USUARIO, getValue(verificarRespostaDadosCadastrais.getUsuario()));

        return parametros;
    }
}