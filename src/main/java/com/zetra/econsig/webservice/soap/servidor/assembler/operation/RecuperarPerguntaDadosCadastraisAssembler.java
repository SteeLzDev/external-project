package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.GRUPO_PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.RecuperarPerguntaDadosCadastrais;

/**
 * <p>Title: RecuperarPerguntaDadosCadastraisAssembler</p>
 * <p>Description: Assembler para RecuperarPerguntaDadosCadastrais.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class RecuperarPerguntaDadosCadastraisAssembler extends BaseAssembler {

    private RecuperarPerguntaDadosCadastraisAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(RecuperarPerguntaDadosCadastrais recuperarPerguntaDadosCadastrais) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(SENHA, recuperarPerguntaDadosCadastrais.getSenha());
        parametros.put(RSE_MATRICULA, recuperarPerguntaDadosCadastrais.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(recuperarPerguntaDadosCadastrais.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(recuperarPerguntaDadosCadastrais.getEstabelecimentoCodigo()));
        parametros.put(SER_LOGIN, getValue(recuperarPerguntaDadosCadastrais.getLoginServidor()));
        parametros.put(GRUPO_PERGUNTA, recuperarPerguntaDadosCadastrais.getGrupoPergunta());
        parametros.put(USUARIO, getValue(recuperarPerguntaDadosCadastrais.getUsuario()));

        return parametros;
    }
}
