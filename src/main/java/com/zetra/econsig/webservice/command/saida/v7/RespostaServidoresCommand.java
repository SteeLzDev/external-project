package com.zetra.econsig.webservice.command.saida.v7;

import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_ADICIONAIS;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.MARGENS;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_SAIDA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PROVENTOS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDORES;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V7_0;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_CID_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_COMPL;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMISSOR_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_EST_CIVIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_NACIONALIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_QTD_FILHOS;
import static com.zetra.econsig.webservice.CamposAPI.SER_SEXO;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO_SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SRS_ATIVO;
import static com.zetra.econsig.webservice.CamposAPI.SRS_BLOQUEADO;
import static com.zetra.econsig.webservice.CamposAPI.SRS_EXCLUIDO;
import static com.zetra.econsig.webservice.CamposAPI.SRS_FALECIDO;
import static com.zetra.econsig.webservice.CamposAPI.SRS_PENDENTE;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;

/**
 * <p>Title: RespostaDadosConsignacaoCommand</p>
 * <p>Description: classe command que gera uma lista de entidade Servidor em resposta à requisição externa ao eConsig versão 7.0.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaServidoresCommand extends RespostaRequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RespostaServidoresCommand.class);

    public RespostaServidoresCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        ParametroDelegate parDelegate = new ParametroDelegate();
        ServidorDelegate serDelegate = new ServidorDelegate();
        String operacao = parametros.get(OPERACAO).toString();

        // Adiciona vários registros com os servidores encontrados na pesquisa,
        // para que o usuário possa escolher um
        List<TransferObject> servidores = (List<TransferObject>) parametros.get(SERVIDORES);
        for (TransferObject servidor : servidores) {
            String rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);
            String srsCodigo = (String) servidor.getAttribute(Columns.SRS_CODIGO);

            Object estIdentificador = servidor.getAttribute(Columns.EST_IDENTIFICADOR);
            Object estNome = servidor.getAttribute(Columns.EST_NOME);
            Object orgIdentificador = servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
            Object orgNome = servidor.getAttribute(Columns.ORG_NOME);

            Object rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA);
            Object rseTipo = servidor.getAttribute(Columns.RSE_TIPO);
            Object rseDataAdmissao = servidor.getAttribute(Columns.RSE_DATA_ADMISSAO);
            Object rsePrazo = servidor.getAttribute(Columns.RSE_PRAZO);
            Object rseSalario = servidor.getAttribute(Columns.RSE_SALARIO);
            Object rseProventos = servidor.getAttribute(Columns.RSE_PROVENTOS);
            Object rseDataSaida = servidor.getAttribute(Columns.RSE_DATA_SAIDA);
            Object rseBanco = servidor.getAttribute(Columns.RSE_BANCO_SAL);
            Object rseAgencia = servidor.getAttribute(Columns.RSE_AGENCIA_SAL);
            Object rseConta = servidor.getAttribute(Columns.RSE_CONTA_SAL);
            Object rseCargoCodigo = servidor.getAttribute(Columns.CRS_IDENTIFICADOR);
            Object rseCargoDescricao = servidor.getAttribute(Columns.CRS_DESCRICAO);
            Object rseHabitacaoCodigo = servidor.getAttribute(Columns.THA_IDENTIFICADOR);
            Object rseHabitacaoDescricao = servidor.getAttribute(Columns.THA_DESCRICAO);
            Object rseEscolaridadeCodigo = servidor.getAttribute(Columns.NES_IDENTIFICADOR);
            Object rseEscolaridadeDescricao = servidor.getAttribute(Columns.NES_DESCRICAO);

            Object serNome = servidor.getAttribute(Columns.SER_NOME);
            Object serCpf = servidor.getAttribute(Columns.SER_CPF);
            Object serDataNasc = servidor.getAttribute(Columns.SER_DATA_NASC);
            Object serIdentidade = servidor.getAttribute(Columns.SER_NRO_IDT);
            Object serUfIdentidade = servidor.getAttribute(Columns.SER_UF_IDT);
            Object serEmissorIdentidade = servidor.getAttribute(Columns.SER_EMISSOR_IDT);
            Object serDataIdentidade = servidor.getAttribute(Columns.SER_DATA_IDT);
            Object serCidadeNasc = servidor.getAttribute(Columns.SER_CID_NASC);
            Object serNacionalidade = servidor.getAttribute(Columns.SER_NACIONALIDADE);
            Object serSexo = servidor.getAttribute(Columns.SER_SEXO);
            Object serEstadoCivil = servidor.getAttribute(Columns.SER_EST_CIVIL);
            Object serEndereco = servidor.getAttribute(Columns.SER_END);
            Object serNroEndereco = servidor.getAttribute(Columns.SER_NRO);
            Object serEnderecoCompl = servidor.getAttribute(Columns.SER_COMPL);
            Object serBairro = servidor.getAttribute(Columns.SER_BAIRRO);
            Object serEnderecoCidade = servidor.getAttribute(Columns.SER_CIDADE);
            Object serEnderecoUF = servidor.getAttribute(Columns.SER_UF);
            Object serCep = servidor.getAttribute(Columns.SER_CEP);
            Object serTelefone = servidor.getAttribute(Columns.SER_TEL);
            Object serCelular = servidor.getAttribute(Columns.SER_CELULAR);
            Object serQtdFilhos = servidor.getAttribute(Columns.SER_QTD_FILHOS);

            RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
            reg.setNome(SERVIDOR_V7_0);

            reg.addAtributo(SERVIDOR, serNome);
            reg.addAtributo(SER_CPF, serCpf);
            reg.addAtributo(RSE_MATRICULA, rseMatricula);
            reg.addAtributo(EST_IDENTIFICADOR, estIdentificador);
            reg.addAtributo(ESTABELECIMENTO, estNome);
            reg.addAtributo(ORG_IDENTIFICADOR, orgIdentificador);
            reg.addAtributo(ORGAO, orgNome);
            reg.addAtributo(RSE_TIPO, rseTipo);
            reg.addAtributo(RSE_DATA_ADMISSAO, rseDataAdmissao);
            reg.addAtributo(RSE_PRAZO, rsePrazo);
            reg.addAtributo(RSE_SALARIO, rseSalario);
            reg.addAtributo(RSE_PROVENTOS, rseProventos);
            reg.addAtributo(RSE_SALARIO, rseSalario);
            reg.addAtributo(RSE_DATA_SAIDA, rseDataSaida);

            try {
                if (!parDelegate.hasValidacaoDataNasc(responsavel)) {
                    reg.addAtributo(SER_DATA_NASC, serDataNasc);
                }
            } catch (ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            RegistroRespostaRequisicaoExterna resSrs = new RegistroRespostaRequisicaoExterna();

            if (srsCodigo.equals(CodedValues.SRS_ATIVO)) {
                resSrs.addAtributo(SRS_ATIVO, Boolean.TRUE);
            } else if (CodedValues.SRS_BLOQUEADOS.contains(srsCodigo)) {
                resSrs.addAtributo(SRS_BLOQUEADO, Boolean.TRUE);
            } else if (srsCodigo.equals(CodedValues.SRS_EXCLUIDO)) {
                resSrs.addAtributo(SRS_EXCLUIDO, Boolean.TRUE);
            } else if (srsCodigo.equals(CodedValues.SRS_FALECIDO)) {
                resSrs.addAtributo(SRS_FALECIDO, Boolean.TRUE);
            } else if (srsCodigo.equals(CodedValues.SRS_PENDENTE)) {
                resSrs.addAtributo(SRS_PENDENTE, Boolean.TRUE);
            }

            reg.addAtributo(SITUACAO_SERVIDOR, resSrs);

            reg.addAtributo(SER_NRO_IDT, serIdentidade);
            reg.addAtributo(SER_DATA_IDT, serDataIdentidade);
            reg.addAtributo(SER_UF_IDT, serUfIdentidade);
            reg.addAtributo(SER_EMISSOR_IDT, serEmissorIdentidade);
            reg.addAtributo(SER_CID_NASC, serCidadeNasc);
            reg.addAtributo(SER_NACIONALIDADE, serNacionalidade);
            reg.addAtributo(SER_SEXO, serSexo);

            // Pega a descrição do codigo de estado civil
            if (!TextHelper.isNull(serEstadoCivil)) {
                String serEstCivil = serDelegate.getEstCivil((String)serEstadoCivil, responsavel);
                reg.addAtributo(SER_EST_CIVIL, serEstCivil);
            }

            reg.addAtributo(SER_END, serEndereco);
            reg.addAtributo(SER_NRO, serNroEndereco);
            reg.addAtributo(SER_COMPL, serEnderecoCompl);
            reg.addAtributo(SER_BAIRRO, serBairro);
            reg.addAtributo(SER_CIDADE, serEnderecoCidade);
            reg.addAtributo(SER_UF, serEnderecoUF);
            reg.addAtributo(SER_CEP, serCep);
            reg.addAtributo(SER_TEL, serTelefone);
            reg.addAtributo(SER_CELULAR, serCelular);

            // No retorno de múltiplos servidores encontrados da operação validar dados bancários, os dados bancários não devem ser retornados.
            if (!operacao.equalsIgnoreCase(CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER)) {
                reg.addAtributo(RSE_BANCO, rseBanco);
                reg.addAtributo(RSE_AGENCIA, rseAgencia);
                reg.addAtributo(RSE_CONTA, rseConta);
            }

            reg.addAtributo(CARGO_CODIGO, rseCargoCodigo);
            reg.addAtributo(CARGO_DESCRICAO, rseCargoDescricao);
            reg.addAtributo(HABITACAO_CODIGO, rseHabitacaoCodigo);
            reg.addAtributo(HABITACAO_DESCRICAO, rseHabitacaoDescricao);
            reg.addAtributo(ESCOLARIDADE_CODIGO, rseEscolaridadeCodigo);
            reg.addAtributo(ESCOLARIDADE_DESCRICAO, rseEscolaridadeDescricao);
            reg.addAtributo(SER_QTD_FILHOS, serQtdFilhos);

            Map<String, List<MargemTO>> margens = (Map<String, List<MargemTO>>) parametros.get(MARGENS);
            List<MargemTO> lstMargens = null;
            if (margens != null) {
                lstMargens = margens.get(rseCodigo);

            }
            reg.addAtributo(MARGENS, lstMargens);

            Map<String, List<TransferObject>> mapDadosAdicionaisServidor = (Map<String, List<TransferObject>>) parametros.get(DADOS_ADICIONAIS);
            if (mapDadosAdicionaisServidor != null && !mapDadosAdicionaisServidor.isEmpty()) {
                reg.addAtributo(DADOS_ADICIONAIS, mapDadosAdicionaisServidor.get(rseCodigo));
            }

            respostas.add(reg);
        }

        return respostas;
    }
}
