package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PROTOCOLO_SENHA_AUTORIZACAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA_USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.GerarSenhaAutorizacaoServidor;

/**
 * <p>Title: GerarSenhaAutorizacaoServidorAssembler</p>
 * <p>Description: Assembler para GerarSenhaAutorizacaoServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class GerarSenhaAutorizacaoServidorAssembler extends BaseAssembler {

    private GerarSenhaAutorizacaoServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(GerarSenhaAutorizacaoServidor gerarSenhaAutorizacaoServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(SENHA, gerarSenhaAutorizacaoServidor.getSenha());
        parametros.put(RSE_MATRICULA, gerarSenhaAutorizacaoServidor.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(gerarSenhaAutorizacaoServidor.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(gerarSenhaAutorizacaoServidor.getEstabelecimentoCodigo()));
        parametros.put(SER_LOGIN, getValue(gerarSenhaAutorizacaoServidor.getLoginServidor()));
        parametros.put(USUARIO, gerarSenhaAutorizacaoServidor.getUsuario());
        parametros.put(SENHA_USUARIO, gerarSenhaAutorizacaoServidor.getSenhaUsuario());
        parametros.put(PROTOCOLO_SENHA_AUTORIZACAO, gerarSenhaAutorizacaoServidor.getProtocoloSenhaAutorizacao());

        return parametros;
    }
}