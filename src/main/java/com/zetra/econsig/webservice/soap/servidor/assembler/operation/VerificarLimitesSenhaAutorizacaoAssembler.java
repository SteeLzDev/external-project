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
import com.zetra.econsig.webservice.soap.servidor.v1.VerificarLimitesSenhaAutorizacao;

/**
 * <p>Title: VerificarLimitesSenhaAutorizacaoAssembler</p>
 * <p>Description: Assembler para VerificarLimitesSenhaAutorizacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class VerificarLimitesSenhaAutorizacaoAssembler extends BaseAssembler {

    private VerificarLimitesSenhaAutorizacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(VerificarLimitesSenhaAutorizacao verificarLimitesSenhaAutorizacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(SENHA, verificarLimitesSenhaAutorizacao.getSenha());
        parametros.put(RSE_MATRICULA, verificarLimitesSenhaAutorizacao.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(verificarLimitesSenhaAutorizacao.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(verificarLimitesSenhaAutorizacao.getEstabelecimentoCodigo()));
        parametros.put(SER_LOGIN, getValue(verificarLimitesSenhaAutorizacao.getLoginServidor()));
        parametros.put(USUARIO, verificarLimitesSenhaAutorizacao.getUsuario());
        parametros.put(SENHA_USUARIO, verificarLimitesSenhaAutorizacao.getSenhaUsuario());
        parametros.put(PROTOCOLO_SENHA_AUTORIZACAO, verificarLimitesSenhaAutorizacao.getProtocoloSenhaAutorizacao());

        return parametros;
    }
}