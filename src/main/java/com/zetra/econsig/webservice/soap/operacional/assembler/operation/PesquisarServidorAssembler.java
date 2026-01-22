package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.NOME;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_SOBRENOME;
import static com.zetra.econsig.webservice.CamposAPI.STATUS;
import static com.zetra.econsig.webservice.CamposAPI.TEM_CONTRATO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;

/**
 * <p>Title: PesquisarServidorAssembler</p>
 * <p>Description: Assembler para PesquisarServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class PesquisarServidorAssembler extends BaseAssembler {

    private PesquisarServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v4.PesquisarServidor pesquisarServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(NOME, getValue(pesquisarServidor.getPrimeiroNome()));
        parametros.put(SER_SOBRENOME, getValue(pesquisarServidor.getUltimoNome()));
        parametros.put(RSE_MATRICULA, getValue(pesquisarServidor.getMatricula()));
        parametros.put(SER_CPF, getValue(pesquisarServidor.getCpf()));
        parametros.put(SER_DATA_NASC, getValueAsDate(pesquisarServidor.getDataNascimento()));
        parametros.put(EST_IDENTIFICADOR, getValue(pesquisarServidor.getEstIdentificador()));
        parametros.put(ORG_IDENTIFICADOR, getValue(pesquisarServidor.getOrgIdentificador()));
        parametros.put(USUARIO, pesquisarServidor.getUsuario());
        parametros.put(SENHA, pesquisarServidor.getSenha());
        parametros.put(CONVENIO, getValue(pesquisarServidor.getConvenio()));
        parametros.put(CLIENTE, getValue(pesquisarServidor.getCliente()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v8.PesquisarServidor pesquisarServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(NOME, getValue(pesquisarServidor.getPrimeiroNome()));
        parametros.put(SER_SOBRENOME, getValue(pesquisarServidor.getUltimoNome()));
        parametros.put(RSE_MATRICULA, getValue(pesquisarServidor.getMatricula()));
        parametros.put(SER_CPF, getValue(pesquisarServidor.getCpf()));
        parametros.put(SER_DATA_NASC, getValueAsDate(pesquisarServidor.getDataNascimento()));
        parametros.put(EST_IDENTIFICADOR, getValue(pesquisarServidor.getEstIdentificador()));
        parametros.put(ORG_IDENTIFICADOR, getValue(pesquisarServidor.getOrgIdentificador()));
        parametros.put(USUARIO, pesquisarServidor.getUsuario());
        parametros.put(SENHA, pesquisarServidor.getSenha());
        parametros.put(CONVENIO, getValue(pesquisarServidor.getConvenio()));
        parametros.put(CLIENTE, getValue(pesquisarServidor.getCliente()));
        parametros.put(RSE_TIPO, getValue(pesquisarServidor.getCategoria()));

        final com.zetra.econsig.webservice.soap.operacional.v8.SituacaoServidor statusServidor = getValue(pesquisarServidor.getStatus());
        if (statusServidor != null) {
            final List<String> rseSrsCodigo = new ArrayList<>();
            if (Boolean.TRUE.equals(statusServidor.getAtivo())) {
                rseSrsCodigo.add(CodedValues.SRS_ATIVO);
            }
            if (Boolean.TRUE.equals(statusServidor.getPendente())) {
                rseSrsCodigo.add(CodedValues.SRS_PENDENTE);
            }
            if (Boolean.TRUE.equals(statusServidor.getBloqueado())) {
                rseSrsCodigo.addAll(CodedValues.SRS_BLOQUEADOS);
            }
            if (Boolean.TRUE.equals(statusServidor.getExcluido())) {
                rseSrsCodigo.add(CodedValues.SRS_EXCLUIDO);
            }
            if (Boolean.TRUE.equals(statusServidor.getFalecido())) {
                rseSrsCodigo.add(CodedValues.SRS_FALECIDO);
            }

            parametros.put(STATUS, rseSrsCodigo);
        }

        if (getValue(pesquisarServidor.getTemContrato()) != null) {
            parametros.put(TEM_CONTRATO, pesquisarServidor.getTemContrato().getValue());
        }

        return parametros;
    }
}
