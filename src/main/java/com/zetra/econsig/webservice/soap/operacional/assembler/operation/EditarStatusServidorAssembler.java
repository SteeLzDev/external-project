package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_SAIDA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ULT_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PEDIDO_DEMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PROVENTOS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_PRIMEIRO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SER_ULTIMO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO_SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v7.EditarStatusServidor;
import com.zetra.econsig.webservice.soap.operacional.v7.SituacaoServidor;

/**
 * <p>Title: EditarStatusServidorAssembler</p>
 * <p>Description: Assembler para EditarStatusServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class EditarStatusServidorAssembler extends BaseAssembler {

    private EditarStatusServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(EditarStatusServidor editarStatusServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, editarStatusServidor.getUsuario());
        parametros.put(SENHA, editarStatusServidor.getSenha());
        parametros.put(CONVENIO, getValue(editarStatusServidor.getConvenio()));
        parametros.put(CLIENTE, getValue(editarStatusServidor.getCliente()));

        parametros.put(SER_CPF, getValue(editarStatusServidor.getCpf()));
        parametros.put(SER_DATA_NASC, getValueAsDate(editarStatusServidor.getDataNascimento()));
        parametros.put(SER_PRIMEIRO_NOME, getValue(editarStatusServidor.getPrimeiroNome()));
        parametros.put(SER_ULTIMO_NOME, getValue(editarStatusServidor.getUltimoNome()));

        parametros.put(EST_IDENTIFICADOR, getValue(editarStatusServidor.getEstabelecimentoCodigo()));
        parametros.put(ORG_IDENTIFICADOR, getValue(editarStatusServidor.getOrgaoCodigo()));
        parametros.put(RSE_MATRICULA, getValue(editarStatusServidor.getMatricula()));
        final SituacaoServidor situacao = getValue(editarStatusServidor.getSituacao());
        if (situacao != null) {
            if (situacao.getAtivo() == null) {
                situacao.setAtivo(false);
            }
            if (situacao.getBloqueado() == null) {
                situacao.setBloqueado(false);
            }
            if (situacao.getExcluido() == null) {
                situacao.setExcluido(false);
            }
            if (situacao.getFalecido() == null) {
                situacao.setFalecido(false);
            }
            if (situacao.getPendente() == null) {
                situacao.setPendente(false);
            }
        }
        parametros.put(SITUACAO_SERVIDOR, situacao);

        parametros.put(RSE_DATA_SAIDA, getValueAsDate(editarStatusServidor.getDataSaida()));
        parametros.put(RSE_DATA_ULT_SALARIO, getValueAsDate(editarStatusServidor.getDataUltimoSalario()));
        parametros.put(RSE_DATA_RETORNO, getValueAsDate(editarStatusServidor.getDataRetorno()));
        parametros.put(RSE_PEDIDO_DEMISSAO, getValue(editarStatusServidor.getPedidoDemissao()));
        parametros.put(RSE_SALARIO, getValue(editarStatusServidor.getSalario()));
        parametros.put(RSE_PROVENTOS, getValue(editarStatusServidor.getProventos()));

        parametros.put(TMO_OBS, getValue(editarStatusServidor.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(editarStatusServidor.getCodigoMotivoOperacao()));

        return parametros;
    }
}