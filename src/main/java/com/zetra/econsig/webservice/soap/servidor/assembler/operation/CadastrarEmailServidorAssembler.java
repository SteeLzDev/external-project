package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EXIGE_GRUPO_PERGUNTAS;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PROTOCOLO_CADASTRO_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA_USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.CadastrarEmailServidor;

/**
 * <p>Title: CadastrarEmailServidorAssembler</p>
 * <p>Description: Assembler para CadastrarEmailServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CadastrarEmailServidorAssembler extends BaseAssembler {

    private CadastrarEmailServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(CadastrarEmailServidor cadastrarEmailServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(EST_IDENTIFICADOR, getValue(cadastrarEmailServidor.getEstabelecimentoCodigo()));
        parametros.put(ORG_IDENTIFICADOR, getValue(cadastrarEmailServidor.getOrgaoCodigo()));
        parametros.put(RSE_MATRICULA, cadastrarEmailServidor.getMatricula());
        parametros.put(SER_EMAIL, cadastrarEmailServidor.getEmail());
        parametros.put(USUARIO, cadastrarEmailServidor.getUsuario());
        parametros.put(SENHA_USUARIO, cadastrarEmailServidor.getSenhaUsuario());
        parametros.put(PROTOCOLO_CADASTRO_EMAIL, cadastrarEmailServidor.getProtocoloCadastroEmail());
        parametros.put(RSE_BANCO, getValue(cadastrarEmailServidor.getBanco()));
        parametros.put(RSE_AGENCIA, getValue(cadastrarEmailServidor.getAgencia()));
        parametros.put(RSE_CONTA, getValue(cadastrarEmailServidor.getConta()));
        parametros.put(EXIGE_GRUPO_PERGUNTAS, getValue(cadastrarEmailServidor.getExigeGrupoPerguntas()));

        return parametros;
    }
}