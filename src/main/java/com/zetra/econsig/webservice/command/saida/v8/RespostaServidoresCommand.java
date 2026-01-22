package com.zetra.econsig.webservice.command.saida.v8;

import static com.zetra.econsig.webservice.CamposAPI.CARGO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_DESCRICAO;
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
import static com.zetra.econsig.webservice.CamposAPI.POSTO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.POSTO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_SAIDA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DESCONTOS_COMP;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DESCONTOS_FACU;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA_INST;
import static com.zetra.econsig.webservice.CamposAPI.RSE_OUTROS_DESCONTOS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PROVENTOS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDORES;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V8_0;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CART_PROF;
import static com.zetra.econsig.webservice.CamposAPI.SER_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_CID_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_COMPL;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMISSOR_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_EST_CIVIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_NACIONALIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_CONJUGE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_MAE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_MEIO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_PAI;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_PIS;
import static com.zetra.econsig.webservice.CamposAPI.SER_PRIMEIRO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SER_QTD_FILHOS;
import static com.zetra.econsig.webservice.CamposAPI.SER_SEXO;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_ULTIMO_NOME;
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
 * <p>Description: classe command que gera uma lista de entidade Servidor em resposta à requisição externa ao eConsig versão 8.0.</p>
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
        final List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        final ParametroDelegate parDelegate = new ParametroDelegate();
        final ServidorDelegate serDelegate = new ServidorDelegate();
        final String operacao = parametros.get(OPERACAO).toString();

        // Adiciona vários registros com os servidores encontrados na pesquisa,
        // para que o usuário possa escolher um
        final List<TransferObject> servidores = (List<TransferObject>) parametros.get(SERVIDORES);
        for (final TransferObject servidor : servidores) {
            final String rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);
            final String serCodigo = (String) servidor.getAttribute(Columns.SER_CODIGO);
            final String srsCodigo = (String) servidor.getAttribute(Columns.SRS_CODIGO);

            final Object estIdentificador = servidor.getAttribute(Columns.EST_IDENTIFICADOR);
            final Object estNome = servidor.getAttribute(Columns.EST_NOME);
            final Object orgIdentificador = servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
            final Object orgNome = servidor.getAttribute(Columns.ORG_NOME);

            final Object rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA);
            final Object rseTipo = servidor.getAttribute(Columns.RSE_TIPO);
            final Object rseDataAdmissao = servidor.getAttribute(Columns.RSE_DATA_ADMISSAO);
            final Object rsePrazo = servidor.getAttribute(Columns.RSE_PRAZO);
            final Object rseSalario = servidor.getAttribute(Columns.RSE_SALARIO);
            final Object rseProventos = servidor.getAttribute(Columns.RSE_PROVENTOS);
            final Object rseDataSaida = servidor.getAttribute(Columns.RSE_DATA_SAIDA);
            final Object rseBanco = servidor.getAttribute(Columns.RSE_BANCO_SAL);
            final Object rseAgencia = servidor.getAttribute(Columns.RSE_AGENCIA_SAL);
            final Object rseConta = servidor.getAttribute(Columns.RSE_CONTA_SAL);
            final Object rseCargoCodigo = servidor.getAttribute(Columns.CRS_IDENTIFICADOR);
            final Object rseCargoDescricao = servidor.getAttribute(Columns.CRS_DESCRICAO);
            final Object rseHabitacaoCodigo = servidor.getAttribute(Columns.THA_IDENTIFICADOR);
            final Object rseHabitacaoDescricao = servidor.getAttribute(Columns.THA_DESCRICAO);
            final Object rseEscolaridadeCodigo = servidor.getAttribute(Columns.NES_IDENTIFICADOR);
            final Object rseEscolaridadeDescricao = servidor.getAttribute(Columns.NES_DESCRICAO);
            final Object rsePostoCodigo = servidor.getAttribute(Columns.POS_CODIGO);
            final Object rsePostoDescricao = servidor.getAttribute(Columns.POS_DESCRICAO);

            final Object serNome = servidor.getAttribute(Columns.SER_NOME);
            final Object serCpf = servidor.getAttribute(Columns.SER_CPF);
            final Object serDataNasc = servidor.getAttribute(Columns.SER_DATA_NASC);
            final Object serIdentidade = servidor.getAttribute(Columns.SER_NRO_IDT);
            final Object serUfIdentidade = servidor.getAttribute(Columns.SER_UF_IDT);
            final Object serEmissorIdentidade = servidor.getAttribute(Columns.SER_EMISSOR_IDT);
            final Object serDataIdentidade = servidor.getAttribute(Columns.SER_DATA_IDT);
            final Object serCidadeNasc = servidor.getAttribute(Columns.SER_CID_NASC);
            final Object serNacionalidade = servidor.getAttribute(Columns.SER_NACIONALIDADE);
            final Object serSexo = servidor.getAttribute(Columns.SER_SEXO);
            final Object serEstadoCivil = servidor.getAttribute(Columns.SER_EST_CIVIL);
            final Object serEndereco = servidor.getAttribute(Columns.SER_END);
            final Object serNroEndereco = servidor.getAttribute(Columns.SER_NRO);
            final Object serEnderecoCompl = servidor.getAttribute(Columns.SER_COMPL);
            final Object serBairro = servidor.getAttribute(Columns.SER_BAIRRO);
            final Object serEnderecoCidade = servidor.getAttribute(Columns.SER_CIDADE);
            final Object serEnderecoUF = servidor.getAttribute(Columns.SER_UF);
            final Object serCep = servidor.getAttribute(Columns.SER_CEP);
            final Object serTelefone = servidor.getAttribute(Columns.SER_TEL);
            final Object serCelular = servidor.getAttribute(Columns.SER_CELULAR);
            final Object serQtdFilhos = servidor.getAttribute(Columns.SER_QTD_FILHOS);

            final Object serNomeMae = servidor.getAttribute(Columns.SER_NOME_MAE);
            final Object serNomePai = servidor.getAttribute(Columns.SER_NOME_PAI);
            final Object serCartProf = servidor.getAttribute(Columns.SER_CART_PROF);
            final Object serPis = servidor.getAttribute(Columns.SER_PIS);
            final Object serEmail = servidor.getAttribute(Columns.SER_EMAIL);
            final Object serNomeConjuge = servidor.getAttribute(Columns.SER_NOME_CONJUGE);
            final Object serNomeMeio = servidor.getAttribute(Columns.SER_NOME_MEIO);
            final Object serUltimoNome = servidor.getAttribute(Columns.SER_ULTIMO_NOME);
            final Object serPrimeiroNome = servidor.getAttribute(Columns.SER_PRIMEIRO_NOME);
            final Object rseDescontosComp = servidor.getAttribute(Columns.RSE_DESCONTOS_COMP);
            final Object rseDescontosFacu = servidor.getAttribute(Columns.RSE_DESCONTOS_FACU);
            final Object rseOutrosDescontos = servidor.getAttribute(Columns.RSE_OUTROS_DESCONTOS);
            final Object rseMatriculaInst = servidor.getAttribute(Columns.RSE_MATRICULA_INST);
            final Object rseDataRetorno = servidor.getAttribute(Columns.RSE_DATA_RETORNO);

            final RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
            reg.setNome(SERVIDOR_V8_0);

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
            reg.addAtributo(SER_NOME_MAE, serNomeMae);
            reg.addAtributo(SER_NOME_PAI, serNomePai);
            reg.addAtributo(SER_CART_PROF, serCartProf);
            reg.addAtributo(SER_PIS, serPis);
            reg.addAtributo(SER_EMAIL, serEmail);
            reg.addAtributo(SER_NOME_CONJUGE, serNomeConjuge);
            reg.addAtributo(SER_NOME_MEIO, serNomeMeio);
            reg.addAtributo(SER_ULTIMO_NOME, serUltimoNome);
            reg.addAtributo(SER_PRIMEIRO_NOME, serPrimeiroNome);
            reg.addAtributo(RSE_DESCONTOS_COMP, rseDescontosComp);
            reg.addAtributo(RSE_DESCONTOS_FACU, rseDescontosFacu);
            reg.addAtributo(RSE_OUTROS_DESCONTOS, rseOutrosDescontos);
            reg.addAtributo(RSE_MATRICULA_INST, rseMatriculaInst);
            reg.addAtributo(RSE_DATA_RETORNO, rseDataRetorno);

            try {
                if (!parDelegate.hasValidacaoDataNasc(responsavel)) {
                    reg.addAtributo(SER_DATA_NASC, serDataNasc);
                }
            } catch (final ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            final RegistroRespostaRequisicaoExterna resSrs = new RegistroRespostaRequisicaoExterna();

            if (CodedValues.SRS_ATIVO.equals(srsCodigo)) {
                resSrs.addAtributo(SRS_ATIVO, Boolean.TRUE);
            } else if (CodedValues.SRS_BLOQUEADOS.contains(srsCodigo)) {
                resSrs.addAtributo(SRS_BLOQUEADO, Boolean.TRUE);
            } else if (CodedValues.SRS_EXCLUIDO.equals(srsCodigo)) {
                resSrs.addAtributo(SRS_EXCLUIDO, Boolean.TRUE);
            } else if (CodedValues.SRS_FALECIDO.equals(srsCodigo)) {
                resSrs.addAtributo(SRS_FALECIDO, Boolean.TRUE);
            } else if (CodedValues.SRS_PENDENTE.equals(srsCodigo)) {
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
                final String serEstCivil = serDelegate.getEstCivil((String)serEstadoCivil, responsavel);
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
            if (!CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER.equalsIgnoreCase(operacao) && !CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER_V8_0.equalsIgnoreCase(operacao)) {
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
            reg.addAtributo(POSTO_CODIGO, rsePostoCodigo);
            reg.addAtributo(POSTO_DESCRICAO, rsePostoDescricao);
            reg.addAtributo(SER_QTD_FILHOS, serQtdFilhos);

            final Map<String, List<MargemTO>> margens = (Map<String, List<MargemTO>>) parametros.get(MARGENS);
            List<MargemTO> lstMargens = null;
            if (margens != null) {
                lstMargens = margens.get(rseCodigo);

            }
            reg.addAtributo(MARGENS, lstMargens);

            final Map<String, List<TransferObject>> mapDadosAdicionaisServidor = (Map<String, List<TransferObject>>) parametros.get(DADOS_ADICIONAIS);
            if ((mapDadosAdicionaisServidor != null) && !mapDadosAdicionaisServidor.isEmpty()) {
                if (mapDadosAdicionaisServidor.get(rseCodigo) != null) {
                    reg.addAtributo(DADOS_ADICIONAIS, mapDadosAdicionaisServidor.get(rseCodigo));
                } else if (!TextHelper.isNull(serCodigo) && (mapDadosAdicionaisServidor.get(serCodigo) != null)) {
                    reg.addAtributo(DADOS_ADICIONAIS, mapDadosAdicionaisServidor.get(serCodigo));
                }
            }

            respostas.add(reg);
        }

        return respostas;
    }
}
