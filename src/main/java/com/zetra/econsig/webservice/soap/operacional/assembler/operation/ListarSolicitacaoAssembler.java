package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.ListarSolicitacao;

/**
 * <p>Title: ListarSolicitacaoAssembler</p>
 * <p>Description: Assembler para ListarSolicitacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ListarSolicitacaoAssembler extends BaseAssembler {

    private ListarSolicitacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ListarSolicitacao listarSolicitacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, listarSolicitacao.getUsuario());
        parametros.put(SENHA, listarSolicitacao.getSenha());
        parametros.put(RSE_MATRICULA, listarSolicitacao.getMatricula());
        parametros.put(SER_CPF, getValue(listarSolicitacao.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(listarSolicitacao.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(listarSolicitacao.getEstabelecimentoCodigo()));
        parametros.put(ADE_IDENTIFICADOR, getValue(listarSolicitacao.getAdeIdentificador()));
        final Long adeNumero = getValue(listarSolicitacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(listarSolicitacao.getConvenio()));
        parametros.put(CLIENTE, getValue(listarSolicitacao.getCliente()));

        return parametros;
    }
}