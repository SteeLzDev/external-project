package com.zetra.econsig.webservice.command.saida.v8;

import static com.zetra.econsig.webservice.CamposAPI.CARGO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_ADICIONAIS;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_NOME;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.MARGENS;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.POSTO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.POSTO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
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
import static com.zetra.econsig.webservice.CamposAPI.SRS_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SRS_EXCLUIDO;
import static com.zetra.econsig.webservice.CamposAPI.SRS_FALECIDO;
import static com.zetra.econsig.webservice.CamposAPI.SRS_PENDENTE;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;

/**
 * <p>Title: RespostaDadosConsignacaoCommand</p>
 * <p>Description: classe command que gera uma lista de entidade Servidor em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaServidorCommand extends RespostaRequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RespostaServidorCommand.class);

    public RespostaServidorCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        try {
            CustomTransferObject servidor = (parametros.get(SERVIDOR_V8_0) != null) ? (CustomTransferObject) parametros.get(SERVIDOR_V8_0) : null;

            if (servidor == null) {
                servidor = new CustomTransferObject();
            }

            RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
            reg.setNome(SERVIDOR_V8_0);

            String rseCodigo = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_CODIGO)) ? (String) servidor.getAttribute(Columns.RSE_CODIGO) : (String) parametros.get(RSE_CODIGO);
            Object serNome = !TextHelper.isNull(servidor.getAttribute(Columns.SER_NOME)) ? servidor.getAttribute(Columns.SER_NOME) : parametros.get(SERVIDOR);
            Object serCpf = !TextHelper.isNull(servidor.getAttribute(Columns.SER_CPF)) ? servidor.getAttribute(Columns.SER_CPF) : parametros.get(SER_CPF);
            Object rseMatricula = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_MATRICULA)) ? servidor.getAttribute(Columns.RSE_MATRICULA) : parametros.get(RSE_MATRICULA);
            Object estIdentificador = !TextHelper.isNull(servidor.getAttribute(Columns.EST_IDENTIFICADOR)) ? servidor.getAttribute(Columns.EST_IDENTIFICADOR) : parametros.get(ESTABELECIMENTO);
            Object estNome = !TextHelper.isNull(servidor.getAttribute(Columns.EST_NOME)) ? servidor.getAttribute(Columns.EST_NOME) : parametros.get(EST_NOME);
            Object orgIdentificador = !TextHelper.isNull(servidor.getAttribute(Columns.ORG_IDENTIFICADOR)) ? servidor.getAttribute(Columns.ORG_IDENTIFICADOR) : parametros.get(ORGAO);
            Object orgNome = !TextHelper.isNull(servidor.getAttribute(Columns.ORG_NOME)) ? servidor.getAttribute(Columns.ORG_NOME) : parametros.get(ORG_NOME);

            Object rseTipo = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_TIPO)) ? servidor.getAttribute(Columns.RSE_TIPO) : parametros.get(RSE_TIPO);
            Object rseDataAdmissao = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_DATA_ADMISSAO)) ? servidor.getAttribute(Columns.RSE_DATA_ADMISSAO) : parametros.get(RSE_DATA_ADMISSAO);
            Object rsePrazo = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_PRAZO)) ? servidor.getAttribute(Columns.RSE_PRAZO) : parametros.get(RSE_PRAZO);
            Object rseSalario = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_SALARIO)) ? servidor.getAttribute(Columns.RSE_SALARIO) : parametros.get(RSE_SALARIO);
            Object rseProventos = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_PROVENTOS)) ? servidor.getAttribute(Columns.RSE_PROVENTOS) : parametros.get(RSE_PROVENTOS);

            Object serIdentidade = !TextHelper.isNull(parametros.get(SER_NRO_IDT)) ? parametros.get(SER_NRO_IDT) : servidor.getAttribute(Columns.SER_NRO_IDT);
            Object serUfIdentidade = !TextHelper.isNull(parametros.get(SER_UF_IDT)) ? parametros.get(SER_UF_IDT) : servidor.getAttribute(Columns.SER_UF_IDT);
            Object serEmissorIdentidade = !TextHelper.isNull(parametros.get(SER_EMISSOR_IDT)) ? parametros.get(SER_EMISSOR_IDT) : servidor.getAttribute(Columns.SER_EMISSOR_IDT);
            Object serCidadeNasc = !TextHelper.isNull(parametros.get(SER_CID_NASC)) ? parametros.get(SER_CID_NASC) : servidor.getAttribute(Columns.SER_CID_NASC);
            Object serNacionalidade = !TextHelper.isNull(parametros.get(SER_NACIONALIDADE)) ? parametros.get(SER_NACIONALIDADE) : servidor.getAttribute(Columns.SER_NACIONALIDADE);
            Object serSexo = !TextHelper.isNull(parametros.get(SER_SEXO)) ? parametros.get(SER_SEXO) : servidor.getAttribute(Columns.SER_SEXO);
            Object serEstadoCivil = !TextHelper.isNull(parametros.get(SER_EST_CIVIL)) ? parametros.get(SER_EST_CIVIL) : servidor.getAttribute(Columns.SER_EST_CIVIL);
            Object serEndereco = !TextHelper.isNull(parametros.get(SER_END)) ? parametros.get(SER_END) : servidor.getAttribute(Columns.SER_END);
            Object serNroEndereco = !TextHelper.isNull(parametros.get(SER_NRO)) ? parametros.get(SER_NRO) : servidor.getAttribute(Columns.SER_NRO);
            Object serEnderecoCompl = !TextHelper.isNull(parametros.get(SER_COMPL)) ? parametros.get(SER_COMPL) : servidor.getAttribute(Columns.SER_COMPL);
            Object serBairro = !TextHelper.isNull(parametros.get(SER_BAIRRO)) ? parametros.get(SER_BAIRRO) : servidor.getAttribute(Columns.SER_BAIRRO);
            Object serEnderecoCidade = !TextHelper.isNull(parametros.get(SER_CIDADE)) ? parametros.get(SER_CIDADE) : servidor.getAttribute(Columns.SER_CIDADE);
            Object serEnderecoUF = !TextHelper.isNull(parametros.get(SER_UF)) ? parametros.get(SER_UF) : servidor.getAttribute(Columns.SER_UF);
            Object serCep = !TextHelper.isNull(parametros.get(SER_CEP)) ? parametros.get(SER_CEP) : servidor.getAttribute(Columns.SER_CEP);
            Object serTelefone = !TextHelper.isNull(parametros.get(SER_TEL)) ? parametros.get(SER_TEL) : servidor.getAttribute(Columns.SER_TEL);
            Object serCelular = !TextHelper.isNull(parametros.get(SER_CELULAR)) ? parametros.get(SER_CELULAR) : servidor.getAttribute(Columns.SER_CELULAR);
            Object rseBanco = !TextHelper.isNull(parametros.get(RSE_BANCO)) ? parametros.get(RSE_BANCO) : servidor.getAttribute(Columns.RSE_BANCO_SAL);
            Object rseAgencia = !TextHelper.isNull(parametros.get(RSE_AGENCIA)) ? parametros.get(RSE_AGENCIA) : servidor.getAttribute(Columns.RSE_AGENCIA_SAL);
            Object rseConta = !TextHelper.isNull(parametros.get(RSE_CONTA)) ? parametros.get(RSE_CONTA) : servidor.getAttribute(Columns.RSE_CONTA_SAL);
            Object rseCargoCodigo = !TextHelper.isNull(parametros.get(CARGO_CODIGO)) ? parametros.get(CARGO_CODIGO) : servidor.getAttribute(Columns.CRS_IDENTIFICADOR);
            Object rseCargoDescricao = !TextHelper.isNull(parametros.get(CARGO_DESCRICAO)) ? parametros.get(CARGO_DESCRICAO) : servidor.getAttribute(Columns.CRS_DESCRICAO);
            Object rseHabitacaoCodigo = !TextHelper.isNull(parametros.get(HABITACAO_CODIGO)) ? parametros.get(HABITACAO_CODIGO) : servidor.getAttribute(Columns.THA_CODIGO);
            Object rseHabitacaoDescricao = !TextHelper.isNull(parametros.get(HABITACAO_DESCRICAO)) ? parametros.get(HABITACAO_DESCRICAO) : servidor.getAttribute(Columns.THA_DESCRICAO);
            Object rseEscolaridadeCodigo = !TextHelper.isNull(parametros.get(ESCOLARIDADE_CODIGO)) ? parametros.get(ESCOLARIDADE_CODIGO) : servidor.getAttribute(Columns.NES_CODIGO);
            Object rseEscolaridadeDescricao = !TextHelper.isNull(parametros.get(ESCOLARIDADE_DESCRICAO)) ? parametros.get(ESCOLARIDADE_DESCRICAO) : servidor.getAttribute(Columns.NES_DESCRICAO);
            Object rsePostoCodigo = !TextHelper.isNull(parametros.get(POSTO_CODIGO)) ? parametros.get(POSTO_CODIGO) : servidor.getAttribute(Columns.POS_CODIGO);
            Object rsePostoDescricao = !TextHelper.isNull(parametros.get(POSTO_DESCRICAO)) ? parametros.get(POSTO_DESCRICAO) : servidor.getAttribute(Columns.POS_DESCRICAO);
            Object serQtdFilhos = !TextHelper.isNull(parametros.get(SER_QTD_FILHOS)) ? parametros.get(SER_QTD_FILHOS) : servidor.getAttribute(Columns.SER_QTD_FILHOS);

            Object serNomeMae = !TextHelper.isNull(servidor.getAttribute(Columns.SER_NOME_MAE)) ? servidor.getAttribute(Columns.SER_NOME_MAE) : parametros.get(SER_NOME_MAE);
            Object serNomePai = !TextHelper.isNull(servidor.getAttribute(Columns.SER_NOME_PAI)) ? servidor.getAttribute(Columns.SER_NOME_PAI) : parametros.get(SER_NOME_PAI);
            Object serCartProf = !TextHelper.isNull(servidor.getAttribute(Columns.SER_CART_PROF)) ? servidor.getAttribute(Columns.SER_CART_PROF) : parametros.get(SER_CART_PROF);
            Object serPis = !TextHelper.isNull(servidor.getAttribute(Columns.SER_PIS)) ? servidor.getAttribute(Columns.SER_PIS) : parametros.get(SER_PIS);
            Object serEmail = !TextHelper.isNull(servidor.getAttribute(Columns.SER_EMAIL)) ? servidor.getAttribute(Columns.SER_EMAIL) : parametros.get(SER_EMAIL);
            Object serNomeConjuge = !TextHelper.isNull(servidor.getAttribute(Columns.SER_NOME_CONJUGE)) ? servidor.getAttribute(Columns.SER_NOME_CONJUGE) : parametros.get(SER_NOME_CONJUGE);
            Object serNomeMeio = !TextHelper.isNull(servidor.getAttribute(Columns.SER_NOME_MEIO)) ? servidor.getAttribute(Columns.SER_NOME_MEIO) : parametros.get(SER_NOME_MEIO);
            Object serUltimoNome = !TextHelper.isNull(servidor.getAttribute(Columns.SER_ULTIMO_NOME)) ? servidor.getAttribute(Columns.SER_ULTIMO_NOME) : parametros.get(SER_ULTIMO_NOME);
            Object serPrimeiroNome = !TextHelper.isNull(servidor.getAttribute(Columns.SER_PRIMEIRO_NOME)) ? servidor.getAttribute(Columns.SER_PRIMEIRO_NOME) : parametros.get(SER_PRIMEIRO_NOME);
            Object rseDescontosComp = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_DESCONTOS_COMP)) ? servidor.getAttribute(Columns.RSE_DESCONTOS_COMP) : parametros.get(RSE_DESCONTOS_COMP);
            Object rseDescontosFacu = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_DESCONTOS_FACU)) ? servidor.getAttribute(Columns.RSE_DESCONTOS_FACU) : parametros.get(RSE_DESCONTOS_FACU);
            Object rseOutrosDescontos = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_OUTROS_DESCONTOS)) ? servidor.getAttribute(Columns.RSE_OUTROS_DESCONTOS) : parametros.get(RSE_OUTROS_DESCONTOS);
            Object rseMatriculaInst = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_MATRICULA_INST)) ? servidor.getAttribute(Columns.RSE_MATRICULA_INST) : parametros.get(RSE_MATRICULA_INST);
            Object rseDataRetorno = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_DATA_RETORNO)) ? servidor.getAttribute(Columns.RSE_DATA_RETORNO) : parametros.get(RSE_DATA_RETORNO);

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
                ParametroDelegate parDelegate = new ParametroDelegate();
                if (!parDelegate.hasValidacaoDataNasc(responsavel)) {
                    Object serDataNasc = !TextHelper.isNull(servidor.getAttribute(Columns.SER_DATA_NASC)) ? servidor.getAttribute(Columns.SER_DATA_NASC) : parametros.get(DATA_NASCIMENTO);
                    reg.addAtributo(SER_DATA_NASC, serDataNasc);
                }
            } catch (ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            String srsCodigo = !TextHelper.isNull(servidor.getAttribute(Columns.SRS_CODIGO)) ? (String) servidor.getAttribute(Columns.SRS_CODIGO) : (String) parametros.get(SRS_CODIGO);
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
            String serDataIdtn = !TextHelper.isNull(parametros.get(SER_DATA_IDT)) ? parametros.get(SER_DATA_IDT).toString() : null;
            if (serDataIdtn != null) {
                serDataIdtn = DateHelper.reformat(serDataIdtn, LocaleHelper.FORMATO_DATA_INGLES, LocaleHelper.getDatePattern());
            }
            reg.addAtributo(SER_DATA_IDT, serDataIdtn);

            reg.addAtributo(SER_UF_IDT, serUfIdentidade);
            reg.addAtributo(SER_EMISSOR_IDT, serEmissorIdentidade);
            reg.addAtributo(SER_CID_NASC, serCidadeNasc);
            reg.addAtributo(SER_NACIONALIDADE, serNacionalidade);
            reg.addAtributo(SER_SEXO, serSexo);
            reg.addAtributo(SER_EST_CIVIL, serEstadoCivil);
            reg.addAtributo(SER_END, serEndereco);
            reg.addAtributo(SER_NRO, serNroEndereco);
            reg.addAtributo(SER_COMPL, serEnderecoCompl);
            reg.addAtributo(SER_BAIRRO, serBairro);
            reg.addAtributo(SER_CIDADE, serEnderecoCidade);
            reg.addAtributo(SER_UF, serEnderecoUF);
            reg.addAtributo(SER_CEP, serCep);
            reg.addAtributo(SER_TEL, serTelefone);
            reg.addAtributo(SER_CELULAR, serCelular);

            BigDecimal salario = (parametros.get(RSE_SALARIO) != null) ? (BigDecimal) parametros.get(RSE_SALARIO) : (BigDecimal) servidor.getAttribute(Columns.RSE_SALARIO);
            if (salario != null) {
                reg.addAtributo(RSE_SALARIO, salario);
            }

            String dataSaida = !TextHelper.isNull(parametros.get(RSE_DATA_SAIDA)) ? parametros.get(RSE_DATA_SAIDA).toString() :
                (servidor.getAttribute(Columns.RSE_DATA_SAIDA) != null ? servidor.getAttribute(Columns.RSE_DATA_SAIDA).toString() : null);
            if (dataSaida != null) {
                dataSaida = DateHelper.reformat(dataSaida, LocaleHelper.FORMATO_DATA_INGLES, LocaleHelper.getDatePattern());
                reg.addAtributo(RSE_DATA_SAIDA, dataSaida);
            }

            reg.addAtributo(RSE_BANCO, rseBanco);
            reg.addAtributo(RSE_AGENCIA, rseAgencia);
            reg.addAtributo(RSE_CONTA, rseConta);
            reg.addAtributo(CARGO_CODIGO, rseCargoCodigo);
            reg.addAtributo(CARGO_DESCRICAO, rseCargoDescricao);
            reg.addAtributo(HABITACAO_CODIGO, rseHabitacaoCodigo);
            reg.addAtributo(HABITACAO_DESCRICAO, rseHabitacaoDescricao);
            reg.addAtributo(ESCOLARIDADE_CODIGO, rseEscolaridadeCodigo);
            reg.addAtributo(ESCOLARIDADE_DESCRICAO, rseEscolaridadeDescricao);
            reg.addAtributo(POSTO_CODIGO, rsePostoCodigo);
            reg.addAtributo(POSTO_DESCRICAO, rsePostoDescricao);
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

        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return respostas;
    }

}
