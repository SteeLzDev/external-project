package com.zetra.econsig.webservice.soap.operacional.assembler;

import static com.zetra.econsig.webservice.CamposAPI.CARGO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.POSTO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.POSTO_DESCRICAO;
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamCsa;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.Servidor;
import com.zetra.econsig.webservice.soap.operacional.v6.ObjectFactory;
import com.zetra.econsig.webservice.soap.operacional.v6.SituacaoServidor;
import com.zetra.econsig.webservice.soap.operacional.v7.DadoAdicional;
import com.zetra.econsig.webservice.soap.operacional.v7.Margem;


/**
 * <p>Title: ServidorAssembler</p>
 * <p>Description: Assembler para Servidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ServidorAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ServidorAssembler.class);

    private static final Map<String, Map<CamposAPI, String>> restrictions = new HashMap<>();

    static {
        final Map<CamposAPI, String> consultarMargem = restrictions.computeIfAbsent(CodedValues.OP_CONSULTAR_MARGEM_V8_0, k -> new EnumMap<>(CamposAPI.class));
        consultarMargem.put(EST_IDENTIFICADOR, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_EST_ESTABELECIMENTO_CODIGO);
        consultarMargem.put(ESTABELECIMENTO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_EST_ESTABELECIMENTO);
        consultarMargem.put(ORG_IDENTIFICADOR, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_ORG_ORGAO_CODIGO);
        consultarMargem.put(ORGAO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_ORG_ORGAO);
        consultarMargem.put(RSE_TIPO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_TIPO);
        consultarMargem.put(SERVIDOR, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_NOME);
        consultarMargem.put(SER_CPF, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_CPF);
        consultarMargem.put(RSE_MATRICULA, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_MATRICULA);
        consultarMargem.put(SER_DATA_NASC, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_MATRICULA);
        consultarMargem.put(RSE_DATA_ADMISSAO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_DATA_ADMISSAO);
        consultarMargem.put(RSE_PRAZO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_PRAZO_SERVIDOR);
        consultarMargem.put(SITUACAO_SERVIDOR, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SRS_SITUACAO_SERVIDOR);
        consultarMargem.put(RSE_SALARIO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_SALARIO_LIQUIDO);
        consultarMargem.put(RSE_PROVENTOS, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_SALARIO_BRUTO);
        consultarMargem.put(SER_NRO_IDT, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_IDENTIDADE);
        consultarMargem.put(SER_DATA_IDT, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_DATA_IDENTIDADE);
        consultarMargem.put(SER_UF_IDT, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_UF_IDENTIDADE);
        consultarMargem.put(SER_EMISSOR_IDT, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_EMISSOR_IDENTIDADE);
        consultarMargem.put(SER_CID_NASC, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_CIDADE_NASCIMENTO);
        consultarMargem.put(SER_NACIONALIDADE, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_NACIONALIDADE);
        consultarMargem.put(SER_SEXO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_SEXO);
        consultarMargem.put(SER_EST_CIVIL, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_ESTADO_CIVIL);
        consultarMargem.put(SER_END, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_ENDERECO);
        consultarMargem.put(SER_NRO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_NUMERO);
        consultarMargem.put(SER_COMPL, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_COMPLEMENTO);
        consultarMargem.put(SER_BAIRRO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_BAIRRO);
        consultarMargem.put(SER_CIDADE, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_CIDADE);
        consultarMargem.put(SER_UF, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_UF);
        consultarMargem.put(SER_CEP, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_CEP);
        consultarMargem.put(SER_TEL, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_TELEFONE);
        consultarMargem.put(SER_CELULAR, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_CELULAR);
        consultarMargem.put(RSE_SALARIO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_SALARIO);
        consultarMargem.put(RSE_DATA_SAIDA, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_DATA_SAIDA);
        consultarMargem.put(RSE_BANCO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_BANCO);
        consultarMargem.put(RSE_AGENCIA, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_AGENCIA);
        consultarMargem.put(RSE_CONTA, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_CONTA);
        consultarMargem.put(CARGO_CODIGO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_CRS_CARGO_CODIGO);
        consultarMargem.put(CARGO_DESCRICAO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_CRS_CARGO_DESCRICAO);
        consultarMargem.put(HABITACAO_CODIGO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_THA_HABITACAO_CODIGO);
        consultarMargem.put(HABITACAO_DESCRICAO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_THA_HABITACAO_DESCRICAO);
        consultarMargem.put(ESCOLARIDADE_CODIGO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_NES_ESCOLARIDADE_CODIGO);
        consultarMargem.put(ESCOLARIDADE_DESCRICAO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_NES_ESCOLARIDADE_DESCRICAO);
        consultarMargem.put(POSTO_CODIGO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_POS_POSTO_CODIGO);
        consultarMargem.put(POSTO_DESCRICAO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_POS_POSTO_DESCRICAO);
        consultarMargem.put(SER_NOME_MAE, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_NOME_MAE);
        consultarMargem.put(SER_NOME_PAI, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_NOME_PAI);
        consultarMargem.put(SER_CART_PROF, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_CART_PROF);
        consultarMargem.put(SER_PIS, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_PIS);
        consultarMargem.put(SER_EMAIL, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_EMAIL);
        consultarMargem.put(SER_NOME_CONJUGE, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_NOME_CONJUGE);
        consultarMargem.put(SER_NOME_MEIO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_NOME_MEIO);
        consultarMargem.put(SER_ULTIMO_NOME, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_ULTIMO_NOME);
        consultarMargem.put(SER_PRIMEIRO_NOME, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_PRIMEIRO_NOME);
        consultarMargem.put(RSE_DESCONTOS_COMP, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_DESCONTOS_COMP);
        consultarMargem.put(RSE_DESCONTOS_FACU, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_DESCONTOS_FACU);
        consultarMargem.put(RSE_OUTROS_DESCONTOS, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_OUTROS_DESCONTOS);
        consultarMargem.put(RSE_MATRICULA_INST, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_MATRICULA_INST);
        consultarMargem.put(RSE_DATA_RETORNO, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_RSE_DATA_RETORNO);
        consultarMargem.put(SER_QTD_FILHOS, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_SER_QUANTIDADE_FILHOS);
        consultarMargem.put(MARGENS, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_MARGEM);
        consultarMargem.put(DADOS_ADICIONAIS, FieldKeysConstants.SOAP_CONSULTAR_MARGEM_DADOS_ADICIONAIS);

        final Map<CamposAPI, String> pesquisarServidor = restrictions.computeIfAbsent(CodedValues.OP_PESQUISAR_SERVIDOR_V8_0, k -> new EnumMap<>(CamposAPI.class));
        pesquisarServidor.put(EST_IDENTIFICADOR, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_EST_ESTABELECIMENTO_CODIGO);
        pesquisarServidor.put(ESTABELECIMENTO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_EST_ESTABELECIMENTO);
        pesquisarServidor.put(ORG_IDENTIFICADOR, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_ORG_ORGAO_CODIGO);
        pesquisarServidor.put(ORGAO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_ORG_ORGAO);
        pesquisarServidor.put(RSE_TIPO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_TIPO);
        pesquisarServidor.put(SERVIDOR, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_NOME);
        pesquisarServidor.put(SER_CPF, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_CPF);
        pesquisarServidor.put(RSE_MATRICULA, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_MATRICULA);
        pesquisarServidor.put(SER_DATA_NASC, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_MATRICULA);
        pesquisarServidor.put(RSE_DATA_ADMISSAO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_DATA_ADMISSAO);
        pesquisarServidor.put(RSE_PRAZO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_PRAZO_SERVIDOR);
        pesquisarServidor.put(SITUACAO_SERVIDOR, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SRS_SITUACAO_SERVIDOR);
        pesquisarServidor.put(RSE_SALARIO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_SALARIO_LIQUIDO);
        pesquisarServidor.put(RSE_PROVENTOS, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_SALARIO_BRUTO);
        pesquisarServidor.put(SER_NRO_IDT, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_IDENTIDADE);
        pesquisarServidor.put(SER_DATA_IDT, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_DATA_IDENTIDADE);
        pesquisarServidor.put(SER_UF_IDT, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_UF_IDENTIDADE);
        pesquisarServidor.put(SER_EMISSOR_IDT, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_EMISSOR_IDENTIDADE);
        pesquisarServidor.put(SER_CID_NASC, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_CIDADE_NASCIMENTO);
        pesquisarServidor.put(SER_NACIONALIDADE, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_NACIONALIDADE);
        pesquisarServidor.put(SER_SEXO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_SEXO);
        pesquisarServidor.put(SER_EST_CIVIL, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_ESTADO_CIVIL);
        pesquisarServidor.put(SER_END, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_ENDERECO);
        pesquisarServidor.put(SER_NRO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_NUMERO);
        pesquisarServidor.put(SER_COMPL, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_COMPLEMENTO);
        pesquisarServidor.put(SER_BAIRRO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_BAIRRO);
        pesquisarServidor.put(SER_CIDADE, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_CIDADE);
        pesquisarServidor.put(SER_UF, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_UF);
        pesquisarServidor.put(SER_CEP, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_CEP);
        pesquisarServidor.put(SER_TEL, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_TELEFONE);
        pesquisarServidor.put(SER_CELULAR, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_CELULAR);
        pesquisarServidor.put(RSE_SALARIO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_SALARIO);
        pesquisarServidor.put(RSE_DATA_SAIDA, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_DATA_SAIDA);
        pesquisarServidor.put(RSE_BANCO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_BANCO);
        pesquisarServidor.put(RSE_AGENCIA, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_AGENCIA);
        pesquisarServidor.put(RSE_CONTA, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_CONTA);
        pesquisarServidor.put(POSTO_CODIGO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_POS_POSTO_CODIGO);
        pesquisarServidor.put(POSTO_DESCRICAO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_POS_POSTO_DESCRICAO);
        pesquisarServidor.put(CARGO_CODIGO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_CRS_CARGO_CODIGO);
        pesquisarServidor.put(CARGO_DESCRICAO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_CRS_CARGO_DESCRICAO);
        pesquisarServidor.put(HABITACAO_CODIGO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_THA_HABITACAO_CODIGO);
        pesquisarServidor.put(HABITACAO_DESCRICAO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_THA_HABITACAO_DESCRICAO);
        pesquisarServidor.put(ESCOLARIDADE_CODIGO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_NES_ESCOLARIDADE_CODIGO);
        pesquisarServidor.put(ESCOLARIDADE_DESCRICAO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_NES_ESCOLARIDADE_DESCRICAO);
        pesquisarServidor.put(SER_NOME_MAE, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_NOME_MAE);
        pesquisarServidor.put(SER_NOME_PAI, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_NOME_PAI);
        pesquisarServidor.put(SER_CART_PROF, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_CART_PROF);
        pesquisarServidor.put(SER_PIS, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_PIS);
        pesquisarServidor.put(SER_EMAIL, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_EMAIL);
        pesquisarServidor.put(SER_NOME_CONJUGE, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_NOME_CONJUGE);
        pesquisarServidor.put(SER_NOME_MEIO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_NOME_MEIO);
        pesquisarServidor.put(SER_ULTIMO_NOME, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_ULTIMO_NOME);
        pesquisarServidor.put(SER_PRIMEIRO_NOME, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_PRIMEIRO_NOME);
        pesquisarServidor.put(RSE_DESCONTOS_COMP, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_DESCONTOS_COMP);
        pesquisarServidor.put(RSE_DESCONTOS_FACU, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_DESCONTOS_FACU);
        pesquisarServidor.put(RSE_OUTROS_DESCONTOS, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_OUTROS_DESCONTOS);
        pesquisarServidor.put(RSE_MATRICULA_INST, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_MATRICULA_INST);
        pesquisarServidor.put(RSE_DATA_RETORNO, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_RSE_DATA_RETORNO);
        pesquisarServidor.put(SER_QTD_FILHOS, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_SER_QUANTIDADE_FILHOS);
        pesquisarServidor.put(DADOS_ADICIONAIS, FieldKeysConstants.SOAP_PESQUISAR_SERVIDOR_DADOS_ADICIONAIS);
    }

    private ServidorAssembler() {
        //
    }

    public static Servidor toServidorV1(Map<CamposAPI, Object> paramResposta) {
        final Servidor servidor = new Servidor();

        servidor.setServidor((String) paramResposta.get(SERVIDOR));
        servidor.setCpf((String) paramResposta.get(SER_CPF));
        servidor.setMatricula((String) paramResposta.get(RSE_MATRICULA));
        servidor.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
        servidor.setEstabelecimento((String) paramResposta.get(ESTABELECIMENTO));
        servidor.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
        servidor.setOrgao((String) paramResposta.get(ORGAO));

        return servidor;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v3.Servidor toServidorV3(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v3.Servidor servidor = new com.zetra.econsig.webservice.soap.operacional.v3.Servidor();

        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(servidor, toServidorV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return servidor;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v4.Servidor toServidorV4(Map<CamposAPI, Object> paramResposta, AcessoSistema responsavel) {
        final com.zetra.econsig.webservice.soap.operacional.v4.ObjectFactory factory = new com.zetra.econsig.webservice.soap.operacional.v4.ObjectFactory();
        final com.zetra.econsig.webservice.soap.operacional.v4.Servidor servidor = new com.zetra.econsig.webservice.soap.operacional.v4.Servidor();

        servidor.setServidor((String) paramResposta.get(SERVIDOR));
        servidor.setCpf((String) paramResposta.get(SER_CPF));
        servidor.setMatricula((String) paramResposta.get(RSE_MATRICULA));
        servidor.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
        servidor.setEstabelecimento((String) paramResposta.get(ESTABELECIMENTO));
        servidor.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
        servidor.setOrgao((String) paramResposta.get(ORGAO));
        try {
            servidor.setDataNascimento(factory.createServidorDataNascimento(toXMLGregorianCalendar((Date) paramResposta.get(SER_DATA_NASC), false)));
        } catch (final DatatypeConfigurationException ex) {
            LOG.warn("Erro de formatação da data de nascimento do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
        }
        servidor.setCategoria(factory.createServidorCategoria((String) paramResposta.get(RSE_TIPO)));
        try {
            servidor.setDataAdmissao(factory.createServidorDataAdmissao(toXMLGregorianCalendar((Date) paramResposta.get(RSE_DATA_ADMISSAO), false)));
        } catch (final DatatypeConfigurationException ex) {
            LOG.warn("Erro de formatação da data de admissão do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
        }

        // DESENV-9901: Se param de CSA habilitado, retorna prazoServidor = 0, se rsePrazo = NULL; prazoServidor = -1 se rseprazo <=0;
        //              e prazoServidor = rsePrazo se rsePrazo > 0
        final ParametroDelegate paramDelegate = new ParametroDelegate();
        try {
            String formataRsePrazo = null;
            if ((responsavel != null) && responsavel.isCsaCor()) {
                formataRsePrazo = paramDelegate.getParamCsa(responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.getCodigoEntidadePai(), CodedValues.TPA_ALTERA_RSE_PRAZO_SOAP, responsavel);
            }

            if (!TextHelper.isNull(formataRsePrazo) && CodedValues.TPA_SIM.equals(formataRsePrazo)) {
                if (TextHelper.isNull(paramResposta.get(RSE_PRAZO))) {
                    servidor.setPrazoServidor(factory.createServidorPrazoServidor(0));
                } else {
                    final int rsePrazoAux = (Integer) paramResposta.get(RSE_PRAZO);

                    if (rsePrazoAux <= 0) {
                        servidor.setPrazoServidor(factory.createServidorPrazoServidor(-1));
                    } else {
                        servidor.setPrazoServidor(factory.createServidorPrazoServidor((Integer) paramResposta.get(RSE_PRAZO)));
                    }
                }
            } else if (paramResposta.get(RSE_PRAZO) != null) {
                servidor.setPrazoServidor(factory.createServidorPrazoServidor((Integer) paramResposta.get(RSE_PRAZO)));
            } else {
                servidor.setPrazoServidor(factory.createServidorPrazoServidor(-1));
            }
        } catch (final ParametroControllerException e1) {
            LOG.warn("ERRO AO RECUPERAR PARÂMETRO DE CONSIGNATÁRIA " + CodedValues.TPA_ALTERA_RSE_PRAZO_SOAP);
        }
        if (paramResposta.get(RSE_SALARIO) != null) {
            if (paramResposta.get(RSE_SALARIO) instanceof final Double value) {
                servidor.setSalarioLiquido(factory.createServidorSalarioLiquido(value));
            } else if (paramResposta.get(RSE_SALARIO) instanceof final BigDecimal value) {
                servidor.setSalarioLiquido(factory.createServidorSalarioLiquido(value.doubleValue()));
            }
        }
        if (paramResposta.get(RSE_PROVENTOS) != null) {
            if (paramResposta.get(RSE_PROVENTOS) instanceof final Double value) {
                servidor.setSalarioBruto(factory.createServidorSalarioBruto(value));
            } else if (paramResposta.get(RSE_PROVENTOS) instanceof final BigDecimal value) {
                servidor.setSalarioBruto(factory.createServidorSalarioBruto(value.doubleValue()));
            }
        }

        final RegistroRespostaRequisicaoExterna resSrsCodigo = (RegistroRespostaRequisicaoExterna) paramResposta.get(SITUACAO_SERVIDOR);
        final Map<CamposAPI, Object> srsMap = resSrsCodigo.getAtributos();
        final com.zetra.econsig.webservice.soap.operacional.v4.SituacaoServidor situacaoSrs = new com.zetra.econsig.webservice.soap.operacional.v4.SituacaoServidor();
        situacaoSrs.setAtivo(false);
        situacaoSrs.setBloqueado(false);
        situacaoSrs.setExcluido(false);
        situacaoSrs.setFalecido(false);
        situacaoSrs.setPendente(false);

        if (srsMap.containsKey(SRS_ATIVO)) {
            situacaoSrs.setAtivo(true);
        } else if (srsMap.containsKey(SRS_BLOQUEADO)) {
            situacaoSrs.setBloqueado(true);
        } else if (srsMap.containsKey(SRS_EXCLUIDO)) {
            situacaoSrs.setExcluido(true);
        } else if (srsMap.containsKey(SRS_FALECIDO)) {
            situacaoSrs.setFalecido(true);
        } else if (srsMap.containsKey(SRS_PENDENTE)) {
            situacaoSrs.setPendente(true);
        }

        servidor.setSituacaoServidor(factory.createServidorSituacaoServidor(situacaoSrs));

        return servidor;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v6.Servidor toServidorV6(Map<CamposAPI, Object> paramResposta, AcessoSistema responsavel) {
        final ObjectFactory factory = new ObjectFactory();
        final com.zetra.econsig.webservice.soap.operacional.v6.Servidor servidor = new com.zetra.econsig.webservice.soap.operacional.v6.Servidor();
        final com.zetra.econsig.webservice.soap.operacional.v4.Servidor servidorV4 = ServidorAssembler.toServidorV4(paramResposta, responsavel);

        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V4
            BeanUtils.copyProperties(servidor, servidorV4);
            final SituacaoServidor situacaoServidor = new SituacaoServidor();
            BeanUtils.copyProperties(situacaoServidor, servidorV4.getSituacaoServidor().getValue());
            // recria o elemento xml com a versão do model pois foi sobrescrita com versão anterior na cópia da versão anterior
            servidor.setSituacaoServidor(factory.createCadastrarServidorSituacao(situacaoServidor));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }

        return servidor;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v7.Servidor toServidorV7(Map<CamposAPI, Object> paramResposta, AcessoSistema responsavel) {
        final com.zetra.econsig.webservice.soap.operacional.v7.ObjectFactory factory = new com.zetra.econsig.webservice.soap.operacional.v7.ObjectFactory();
        final com.zetra.econsig.webservice.soap.operacional.v7.Servidor servidor = new com.zetra.econsig.webservice.soap.operacional.v7.Servidor();

        servidor.setServidor((String) paramResposta.get(SERVIDOR));
        servidor.setCpf((String) paramResposta.get(SER_CPF));
        servidor.setMatricula((String) paramResposta.get(RSE_MATRICULA));
        servidor.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
        servidor.setEstabelecimento((String) paramResposta.get(ESTABELECIMENTO));
        servidor.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
        servidor.setOrgao((String) paramResposta.get(ORGAO));
        servidor.setCategoria(factory.createServidorCategoria((String) paramResposta.get(RSE_TIPO)));

        final Object dataNasc = paramResposta.get(SER_DATA_NASC);
        final Object dataAdmissao = paramResposta.get(RSE_DATA_ADMISSAO);
        try {
            if (!TextHelper.isNull(dataNasc)) {
                if (dataNasc instanceof String) {
                    servidor.setDataNascimento(factory.createServidorDataNascimento(toXMLGregorianCalendar(DateHelper.parse(dataNasc.toString(), LocaleHelper.getDatePattern()), false)));
                } else if (dataNasc instanceof final Date value) {
                    servidor.setDataNascimento(factory.createServidorDataNascimento(toXMLGregorianCalendar(value, false)));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Erro de formatação da data de nascimento do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
        }
        try {
            if (!TextHelper.isNull(dataAdmissao)) {
                if (dataAdmissao instanceof String) {
                    servidor.setDataAdmissao(factory.createServidorDataAdmissao(toXMLGregorianCalendar(DateHelper.parse(dataAdmissao.toString(), LocaleHelper.getDatePattern()), false)));
                } else if (dataAdmissao instanceof final Date value) {
                    servidor.setDataAdmissao(factory.createServidorDataAdmissao(toXMLGregorianCalendar(value, false)));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Erro de formatação da data de admissão do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
        }

        if (paramResposta.get(RSE_PRAZO) != null) {
            servidor.setPrazoServidor(factory.createServidorPrazoServidor((Integer) paramResposta.get(RSE_PRAZO)));
        } else {
            servidor.setPrazoServidor(factory.createServidorPrazoServidor(-1));
        }
        if (paramResposta.get(RSE_SALARIO) != null) {
            if (paramResposta.get(RSE_SALARIO) instanceof final Double value) {
                servidor.setSalarioLiquido(factory.createServidorSalarioLiquido(value));
            } else if (paramResposta.get(RSE_SALARIO) instanceof final BigDecimal value) {
                servidor.setSalarioLiquido(factory.createServidorSalarioLiquido(value.doubleValue()));
            }
        }
        if (paramResposta.get(RSE_PROVENTOS) != null) {
            if (paramResposta.get(RSE_PROVENTOS) instanceof final Double value) {
                servidor.setSalarioBruto(factory.createServidorSalarioBruto(value));
            } else if (paramResposta.get(RSE_PROVENTOS) instanceof final BigDecimal value) {
                servidor.setSalarioBruto(factory.createServidorSalarioBruto(value.doubleValue()));
            }
        }

        servidor.setIdentidade(factory.createServidorIdentidade((String) paramResposta.get(SER_NRO_IDT)));
        final Object dataIdentidade = paramResposta.get(SER_DATA_IDT);
        try {
            if (!TextHelper.isNull(dataIdentidade)) {
                if (dataIdentidade instanceof String) {
                    servidor.setDataIdentidade(factory.createServidorDataIdentidade(toXMLGregorianCalendar(DateHelper.parse(dataIdentidade.toString(), LocaleHelper.getDatePattern()), false)));
                } else if (dataIdentidade instanceof final Date value) {
                    servidor.setDataIdentidade(factory.createServidorDataIdentidade(toXMLGregorianCalendar(value, false)));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Erro de formatação da data da identidade do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
        }
        servidor.setUfIdentidade(factory.createServidorUfIdentidade((String) paramResposta.get(SER_UF_IDT)));
        servidor.setEmissorIdentidade(factory.createServidorEmissorIdentidade((String) paramResposta.get(SER_EMISSOR_IDT)));
        servidor.setCidadeNascimento(factory.createServidorCidadeNascimento((String) paramResposta.get(SER_CID_NASC)));
        servidor.setNacionalidade(factory.createServidorNacionalidade((String) paramResposta.get(SER_NACIONALIDADE)));
        servidor.setSexo(factory.createServidorSexo((String) paramResposta.get(SER_SEXO)));
        servidor.setEstadoCivil(factory.createServidorEstadoCivil((String) paramResposta.get(SER_EST_CIVIL)));
        servidor.setEndereco(factory.createServidorEndereco((String) paramResposta.get(SER_END)));
        servidor.setNumero(factory.createServidorNumero((String) paramResposta.get(SER_NRO)));
        servidor.setComplemento(factory.createServidorComplemento((String) paramResposta.get(SER_COMPL)));
        servidor.setCidade(factory.createServidorCidade((String) paramResposta.get(SER_CIDADE)));
        servidor.setUf(factory.createServidorUf((String) paramResposta.get(SER_UF)));
        servidor.setBairro(factory.createServidorBairro((String) paramResposta.get(SER_BAIRRO)));
        servidor.setCep(factory.createServidorCep((String) paramResposta.get(SER_CEP)));
        servidor.setTelefone(factory.createServidorTelefone((String) paramResposta.get(SER_TEL)));
        servidor.setCelular(factory.createServidorCelular((String) paramResposta.get(SER_CELULAR)));
        if (!TextHelper.isNull(paramResposta.get(RSE_SALARIO)) && !(paramResposta.get(RSE_SALARIO) instanceof BigDecimal)) {
            try {
                servidor.setSalario(factory.createServidorSalario(NumberHelper.parse((paramResposta.get(RSE_SALARIO)).toString(), "en")));
            } catch (final NumberFormatException | ParseException e) {
                LOG.warn("ERRO AO RECUPERAR RSE_SALARIO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        } else if (!TextHelper.isNull(paramResposta.get(RSE_SALARIO))) {
            servidor.setSalario(factory.createServidorSalario(((BigDecimal) paramResposta.get(RSE_SALARIO)).doubleValue()));
        } else {
            servidor.setSalario(factory.createServidorSalario(Double.NaN));
        }
        final Object dataSaida = paramResposta.get(RSE_DATA_SAIDA);
        try {
            if (!TextHelper.isNull(dataSaida)) {
                if (dataSaida instanceof final String value) {
                    servidor.setDataSaida(factory.createServidorDataSaida(toXMLGregorianCalendar(DateHelper.parse(value, LocaleHelper.getDatePattern()), false)));
                } else if (dataSaida instanceof final Date value) {
                    servidor.setDataSaida(factory.createServidorDataSaida(toXMLGregorianCalendar(value, false)));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Erro de formatação da data de saida do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
        }
        servidor.setBanco(factory.createServidorBanco((String) paramResposta.get(RSE_BANCO)));
        servidor.setAgencia(factory.createServidorAgencia((String) paramResposta.get(RSE_AGENCIA)));
        servidor.setConta(factory.createServidorConta((String) paramResposta.get(RSE_CONTA)));
        servidor.setCargoCodigo(factory.createServidorCargoCodigo((String) paramResposta.get(CARGO_CODIGO)));
        servidor.setCargoDescricao(factory.createServidorCargoDescricao((String) paramResposta.get(CARGO_DESCRICAO)));

        servidor.setHabitacaoCodigo(factory.createServidorHabitacaoCodigo((String) paramResposta.get(HABITACAO_CODIGO)));
        servidor.setHabitacaoDescricao(factory.createServidorHabitacaoDescricao((String) paramResposta.get(HABITACAO_DESCRICAO)));
        servidor.setEscolaridadeCodigo(factory.createServidorEscolaridadeCodigo((String) paramResposta.get(ESCOLARIDADE_CODIGO)));
        servidor.setEscolaridadeDescricao(factory.createServidorEscolaridadeDescricao((String) paramResposta.get(ESCOLARIDADE_DESCRICAO)));
        if (!TextHelper.isNull(paramResposta.get(SER_QTD_FILHOS))) {
            servidor.setQtdFilhos(factory.createServidorQtdFilhos(Integer.valueOf(paramResposta.get(SER_QTD_FILHOS).toString())));
        }

        final RegistroRespostaRequisicaoExterna resSrsCodigo = (RegistroRespostaRequisicaoExterna) paramResposta.get(SITUACAO_SERVIDOR);
        final Map<CamposAPI, Object> srsMap = resSrsCodigo.getAtributos();
        final com.zetra.econsig.webservice.soap.operacional.v7.SituacaoServidor situacaoSrs = new com.zetra.econsig.webservice.soap.operacional.v7.SituacaoServidor();
        situacaoSrs.setAtivo(false);
        situacaoSrs.setBloqueado(false);
        situacaoSrs.setExcluido(false);
        situacaoSrs.setFalecido(false);
        situacaoSrs.setPendente(false);

        if (srsMap.containsKey(SRS_ATIVO)) {
            situacaoSrs.setAtivo(true);
        } else if (srsMap.containsKey(SRS_BLOQUEADO)) {
            situacaoSrs.setBloqueado(true);
        } else if (srsMap.containsKey(SRS_EXCLUIDO)) {
            situacaoSrs.setExcluido(true);
        } else if (srsMap.containsKey(SRS_FALECIDO)) {
            situacaoSrs.setFalecido(true);
        } else if (srsMap.containsKey(SRS_PENDENTE)) {
            situacaoSrs.setPendente(true);
        }

        servidor.setSituacaoServidor(factory.createServidorSituacaoServidor(situacaoSrs));

        final List<MargemTO> margens = (List<MargemTO>) paramResposta.get(MARGENS);
        if ((margens != null) && !margens.isEmpty()) {
            for (final MargemTO margemTO : margens) {
                final Margem margem = new Margem();
                margem.setCodigo(margemTO.getMarCodigo().toString());
                margem.setDescricao(margemTO.getMarDescricao());
                if (!TextHelper.isNull(margemTO.getMrsMargem()) && retornarVlrFolhaVlrUsadoMargem(responsavel)) {
                    margem.setValorFolha(factory.createMargemValorFolha(margemTO.getMrsMargem().doubleValue()));
                } else {
                    margem.setValorFolha(factory.createMargemValorFolha(Double.NaN));
                }
                if (!TextHelper.isNull(margemTO.getMrsMargemUsada()) && retornarVlrFolhaVlrUsadoMargem(responsavel)) {
                    margem.setValorUsado(factory.createMargemValorUsado(margemTO.getMrsMargemUsada().doubleValue()));
                } else {
                    margem.setValorUsado(factory.createMargemValorUsado(Double.NaN));
                }
                if (!TextHelper.isNull(margemTO.getMrsMargemRest())) {
                    margem.setValorDisponivel(factory.createMargemValorDisponivel(margemTO.getMrsMargemRest().doubleValue()));
                } else {
                    margem.setValorDisponivel(factory.createMargemValorDisponivel(Double.NaN));
                }
                if (!TextHelper.isNull(margemTO.getMargemLimite())) {
                    margem.setValorLimite(factory.createMargemValorLimite(margemTO.getMargemLimite().doubleValue()));
                } else {
                    margem.setValorLimite(factory.createMargemValorLimite(Double.NaN));
                }
                if (!TextHelper.isNull(margemTO.getObservacao())) {
                    margem.setMensagem(factory.createMargemMensagem(margemTO.getObservacao()));
                } else {
                    margem.setMensagem(factory.createMargemMensagem(""));
                }

                servidor.getMargens().add(margem);
            }
        }

        final List<TransferObject> listaDadosAdicionaisServidor = (List<TransferObject>) paramResposta.get(DADOS_ADICIONAIS);
        if ((listaDadosAdicionaisServidor != null) && !listaDadosAdicionaisServidor.isEmpty()) {
            for (final TransferObject dadoAdicionalTO : listaDadosAdicionaisServidor) {
                if ((Log.SERVIDOR.equals(dadoAdicionalTO.getAttribute(Columns.TDA_TEN_CODIGO)))) {
                    final String codigo = (String) dadoAdicionalTO.getAttribute(Columns.TDA_CODIGO);
                    final String descricao = (String) dadoAdicionalTO.getAttribute(Columns.TDA_DESCRICAO);
                    final String valor = (String) dadoAdicionalTO.getAttribute(Columns.DAS_VALOR);

                    final DadoAdicional dadoAdicional = new DadoAdicional();
                    dadoAdicional.setCodigo(codigo);
                    dadoAdicional.setDescricao(descricao);
                    dadoAdicional.setValor(valor);
                    servidor.getDados().add(dadoAdicional);
                }
            }
        }

        return servidor;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v8.Servidor toServidorV8(Map<CamposAPI, Object> paramResposta, AcessoSistema responsavel) {
        final com.zetra.econsig.webservice.soap.operacional.v8.Servidor servidor = new com.zetra.econsig.webservice.soap.operacional.v8.Servidor();

        final String operacao = paramResposta.get(OPERACAO).toString();

        if (CodedValues.OP_PESQUISAR_SERVIDOR_V8_0.equalsIgnoreCase(operacao) ||
            CodedValues.OP_CONSULTAR_MARGEM_V8_0.equalsIgnoreCase(operacao)) {
            setValuesV8(operacao, servidor, paramResposta, responsavel);
        } else {
            final com.zetra.econsig.webservice.soap.operacional.v8.ObjectFactory factory = new com.zetra.econsig.webservice.soap.operacional.v8.ObjectFactory();

            servidor.setServidor((String) paramResposta.get(SERVIDOR));
            servidor.setCpf((String) paramResposta.get(SER_CPF));
            servidor.setMatricula((String) paramResposta.get(RSE_MATRICULA));
            servidor.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
            servidor.setEstabelecimento((String) paramResposta.get(ESTABELECIMENTO));
            servidor.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
            servidor.setOrgao((String) paramResposta.get(ORGAO));
            servidor.setCategoria(factory.createServidorCategoria((String) paramResposta.get(RSE_TIPO)));

            final Object dataNasc = paramResposta.get(SER_DATA_NASC);
            final Object dataAdmissao = paramResposta.get(RSE_DATA_ADMISSAO);
            try {
                if (!TextHelper.isNull(dataNasc)) {
                    if (dataNasc instanceof String) {
                        servidor.setDataNascimento(factory.createServidorDataNascimento(toXMLGregorianCalendar(DateHelper.parse(dataNasc.toString(), LocaleHelper.getDatePattern()), false)));
                    } else if (dataNasc instanceof final Date value) {
                        servidor.setDataNascimento(factory.createServidorDataNascimento(toXMLGregorianCalendar(value, false)));
                    }
                }
            } catch (final Exception ex) {
                LOG.warn("Erro de formatação da data de nascimento do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
            }
            try {
                if (!TextHelper.isNull(dataAdmissao)) {
                    if (dataAdmissao instanceof String) {
                        servidor.setDataAdmissao(factory.createServidorDataAdmissao(toXMLGregorianCalendar(DateHelper.parse(dataAdmissao.toString(), LocaleHelper.getDatePattern()), false)));
                    } else if (dataAdmissao instanceof final Date value) {
                        servidor.setDataAdmissao(factory.createServidorDataAdmissao(toXMLGregorianCalendar(value, false)));
                    }
                }
            } catch (final Exception ex) {
                LOG.warn("Erro de formatação da data de admissão do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
            }

            if (paramResposta.get(RSE_PRAZO) != null) {
                servidor.setPrazoServidor(factory.createServidorPrazoServidor((Integer) paramResposta.get(RSE_PRAZO)));
            } else {
                servidor.setPrazoServidor(factory.createServidorPrazoServidor(-1));
            }
            if (paramResposta.get(RSE_SALARIO) != null) {
                if (paramResposta.get(RSE_SALARIO) instanceof final Double value) {
                    servidor.setSalarioLiquido(factory.createServidorSalarioLiquido(value));
                } else if (paramResposta.get(RSE_SALARIO) instanceof final BigDecimal value) {
                    servidor.setSalarioLiquido(factory.createServidorSalarioLiquido(value.doubleValue()));
                }
            }
            if (paramResposta.get(RSE_PROVENTOS) != null) {
                if (paramResposta.get(RSE_PROVENTOS) instanceof final Double value) {
                    servidor.setSalarioBruto(factory.createServidorSalarioBruto(value));
                } else if (paramResposta.get(RSE_PROVENTOS) instanceof final BigDecimal value) {
                    servidor.setSalarioBruto(factory.createServidorSalarioBruto(value.doubleValue()));
                }
            }

            servidor.setIdentidade(factory.createServidorIdentidade((String) paramResposta.get(SER_NRO_IDT)));
            final Object dataIdentidade = paramResposta.get(SER_DATA_IDT);
            try {
                if (!TextHelper.isNull(dataIdentidade)) {
                    if (dataIdentidade instanceof String) {
                        servidor.setDataIdentidade(factory.createServidorDataIdentidade(toXMLGregorianCalendar(DateHelper.parse(dataIdentidade.toString(), LocaleHelper.getDatePattern()), false)));
                    } else if (dataIdentidade instanceof final Date value) {
                        servidor.setDataIdentidade(factory.createServidorDataIdentidade(toXMLGregorianCalendar(value, false)));
                    }
                }
            } catch (final Exception ex) {
                LOG.warn("Erro de formatação da data da identidade do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
            }
            servidor.setUfIdentidade(factory.createServidorUfIdentidade((String) paramResposta.get(SER_UF_IDT)));
            servidor.setEmissorIdentidade(factory.createServidorEmissorIdentidade((String) paramResposta.get(SER_EMISSOR_IDT)));
            servidor.setCidadeNascimento(factory.createServidorCidadeNascimento((String) paramResposta.get(SER_CID_NASC)));
            servidor.setNacionalidade(factory.createServidorNacionalidade((String) paramResposta.get(SER_NACIONALIDADE)));
            servidor.setSexo(factory.createServidorSexo((String) paramResposta.get(SER_SEXO)));
            servidor.setEstadoCivil(factory.createServidorEstadoCivil((String) paramResposta.get(SER_EST_CIVIL)));
            servidor.setEndereco(factory.createServidorEndereco((String) paramResposta.get(SER_END)));
            servidor.setNumero(factory.createServidorNumero((String) paramResposta.get(SER_NRO)));
            servidor.setComplemento(factory.createServidorComplemento((String) paramResposta.get(SER_COMPL)));
            servidor.setCidade(factory.createServidorCidade((String) paramResposta.get(SER_CIDADE)));
            servidor.setUf(factory.createServidorUf((String) paramResposta.get(SER_UF)));
            servidor.setBairro(factory.createServidorBairro((String) paramResposta.get(SER_BAIRRO)));
            servidor.setCep(factory.createServidorCep((String) paramResposta.get(SER_CEP)));
            servidor.setTelefone(factory.createServidorTelefone((String) paramResposta.get(SER_TEL)));
            servidor.setCelular(factory.createServidorCelular((String) paramResposta.get(SER_CELULAR)));
            if (!TextHelper.isNull(paramResposta.get(RSE_SALARIO)) && !(paramResposta.get(RSE_SALARIO) instanceof BigDecimal)) {
                try {
                    servidor.setSalario(factory.createServidorSalario(NumberHelper.parse((paramResposta.get(RSE_SALARIO)).toString(), "en")));
                } catch (final NumberFormatException | ParseException e) {
                    LOG.warn("ERRO AO RECUPERAR RSE_SALARIO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
                }
            } else if (!TextHelper.isNull(paramResposta.get(RSE_SALARIO))) {
                servidor.setSalario(factory.createServidorSalario(((BigDecimal) paramResposta.get(RSE_SALARIO)).doubleValue()));
            } else {
                servidor.setSalario(factory.createServidorSalario(Double.NaN));
            }
            final Object dataSaida = paramResposta.get(RSE_DATA_SAIDA);
            try {
                if (!TextHelper.isNull(dataSaida)) {
                    if (dataSaida instanceof final String value) {
                        servidor.setDataSaida(factory.createServidorDataSaida(toXMLGregorianCalendar(DateHelper.parse(value, LocaleHelper.getDatePattern()), false)));
                    } else if (dataSaida instanceof final Date value) {
                        servidor.setDataSaida(factory.createServidorDataSaida(toXMLGregorianCalendar(value, false)));
                    }
                }
            } catch (final Exception ex) {
                LOG.warn("Erro de formatação da data de saida do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
            }
            servidor.setBanco(factory.createServidorBanco((String) paramResposta.get(RSE_BANCO)));
            servidor.setAgencia(factory.createServidorAgencia((String) paramResposta.get(RSE_AGENCIA)));
            servidor.setConta(factory.createServidorConta((String) paramResposta.get(RSE_CONTA)));
            servidor.setCargoCodigo(factory.createServidorCargoCodigo((String) paramResposta.get(CARGO_CODIGO)));
            servidor.setCargoDescricao(factory.createServidorCargoDescricao((String) paramResposta.get(CARGO_DESCRICAO)));

            servidor.setHabitacaoCodigo(factory.createServidorHabitacaoCodigo((String) paramResposta.get(HABITACAO_CODIGO)));
            servidor.setHabitacaoDescricao(factory.createServidorHabitacaoDescricao((String) paramResposta.get(HABITACAO_DESCRICAO)));
            servidor.setPostoCodigo(factory.createServidorPostoCodigo((String) paramResposta.get(POSTO_CODIGO)));
            servidor.setPostoDescricao(factory.createServidorPostoDescricao((String) paramResposta.get(POSTO_DESCRICAO)));
            servidor.setEscolaridadeCodigo(factory.createServidorEscolaridadeCodigo((String) paramResposta.get(ESCOLARIDADE_CODIGO)));
            servidor.setEscolaridadeDescricao(factory.createServidorEscolaridadeDescricao((String) paramResposta.get(ESCOLARIDADE_DESCRICAO)));
            if (!TextHelper.isNull(paramResposta.get(SER_QTD_FILHOS))) {
                servidor.setQtdFilhos(factory.createServidorQtdFilhos(Integer.valueOf(paramResposta.get(SER_QTD_FILHOS).toString())));
            }

            final RegistroRespostaRequisicaoExterna resSrsCodigo = (RegistroRespostaRequisicaoExterna) paramResposta.get(SITUACAO_SERVIDOR);
            final Map<CamposAPI, Object> srsMap = resSrsCodigo.getAtributos();
            final com.zetra.econsig.webservice.soap.operacional.v8.SituacaoServidor situacaoSrs = new com.zetra.econsig.webservice.soap.operacional.v8.SituacaoServidor();
            situacaoSrs.setAtivo(false);
            situacaoSrs.setBloqueado(false);
            situacaoSrs.setExcluido(false);
            situacaoSrs.setFalecido(false);
            situacaoSrs.setPendente(false);

            if (srsMap.containsKey(SRS_ATIVO)) {
                situacaoSrs.setAtivo(true);
            } else if (srsMap.containsKey(SRS_BLOQUEADO)) {
                situacaoSrs.setBloqueado(true);
            } else if (srsMap.containsKey(SRS_EXCLUIDO)) {
                situacaoSrs.setExcluido(true);
            } else if (srsMap.containsKey(SRS_FALECIDO)) {
                situacaoSrs.setFalecido(true);
            } else if (srsMap.containsKey(SRS_PENDENTE)) {
                situacaoSrs.setPendente(true);
            }

            servidor.setSituacaoServidor(factory.createServidorSituacaoServidor(situacaoSrs));

            final List<MargemTO> margens = (List<MargemTO>) paramResposta.get(MARGENS);
            if ((margens != null) && !margens.isEmpty()) {
                for (final MargemTO margemTO : margens) {
                    final com.zetra.econsig.webservice.soap.operacional.v8.Margem margem = new com.zetra.econsig.webservice.soap.operacional.v8.Margem();
                    margem.setCodigo(margemTO.getMarCodigo().toString());
                    margem.setDescricao(margemTO.getMarDescricao());
                    if (!TextHelper.isNull(margemTO.getMrsMargem()) && retornarVlrFolhaVlrUsadoMargem(responsavel)) {
                        margem.setValorFolha(factory.createMargemValorFolha(margemTO.getMrsMargem().doubleValue()));
                    } else {
                        margem.setValorFolha(factory.createMargemValorFolha(Double.NaN));
                    }
                    if (!TextHelper.isNull(margemTO.getMrsMargemUsada()) && retornarVlrFolhaVlrUsadoMargem(responsavel)) {
                        margem.setValorUsado(factory.createMargemValorUsado(margemTO.getMrsMargemUsada().doubleValue()));
                    } else {
                        margem.setValorUsado(factory.createMargemValorUsado(Double.NaN));
                    }
                    if (!TextHelper.isNull(margemTO.getMrsMargemRest())) {
                        margem.setValorDisponivel(factory.createMargemValorDisponivel(margemTO.getMrsMargemRest().doubleValue()));
                    } else {
                        margem.setValorDisponivel(factory.createMargemValorDisponivel(Double.NaN));
                    }
                    if (!TextHelper.isNull(margemTO.getMargemLimite())) {
                        margem.setValorLimite(factory.createMargemValorLimite(margemTO.getMargemLimite().doubleValue()));
                    } else {
                        margem.setValorLimite(factory.createMargemValorLimite(Double.NaN));
                    }
                    if (!TextHelper.isNull(margemTO.getObservacao())) {
                        margem.setMensagem(factory.createMargemMensagem(margemTO.getObservacao()));
                    } else {
                        margem.setMensagem(factory.createMargemMensagem(""));
                    }

                    servidor.getMargens().add(margem);
                }
            }

            final List<TransferObject> listaDadosAdicionaisServidor = (List<TransferObject>) paramResposta.get(DADOS_ADICIONAIS);
            if ((listaDadosAdicionaisServidor != null) && !listaDadosAdicionaisServidor.isEmpty()) {
                for (final TransferObject dadoAdicionalTO : listaDadosAdicionaisServidor) {
                    if ((Log.SERVIDOR.equals(dadoAdicionalTO.getAttribute(Columns.TDA_TEN_CODIGO)))) {
                        final String codigo = (String) dadoAdicionalTO.getAttribute(Columns.TDA_CODIGO);
                        final String descricao = (String) dadoAdicionalTO.getAttribute(Columns.TDA_DESCRICAO);
                        final String valor = (String) dadoAdicionalTO.getAttribute(Columns.DAS_VALOR);

                        final com.zetra.econsig.webservice.soap.operacional.v8.DadoAdicional dadoAdicional = new com.zetra.econsig.webservice.soap.operacional.v8.DadoAdicional();
                        dadoAdicional.setCodigo(codigo);
                        dadoAdicional.setDescricao(descricao);
                        dadoAdicional.setValor(valor);
                        servidor.getDados().add(dadoAdicional);
                    }
                }
            }
        }

        return servidor;
    }

    private static void setValuesV8(String operacao, com.zetra.econsig.webservice.soap.operacional.v8.Servidor servidor, Map<CamposAPI, Object> paramResposta, AcessoSistema responsavel) {
        final com.zetra.econsig.webservice.soap.operacional.v8.ObjectFactory factory = new com.zetra.econsig.webservice.soap.operacional.v8.ObjectFactory();
        try {
            final Map<CamposAPI, String> fieldKeys = restrictions.get(operacao);
            if (ShowFieldHelper.showField(fieldKeys.get(EST_IDENTIFICADOR), responsavel)) {
                servidor.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(ESTABELECIMENTO), responsavel)) {
                servidor.setEstabelecimento((String) paramResposta.get(ESTABELECIMENTO));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(ORG_IDENTIFICADOR), responsavel)) {
                servidor.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(ORGAO), responsavel)) {
                servidor.setOrgao((String) paramResposta.get(ORGAO));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_TIPO), responsavel)) {
                servidor.setCategoria(factory.createServidorCategoria((String) paramResposta.get(RSE_TIPO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SERVIDOR), responsavel)) {
                servidor.setServidor((String) paramResposta.get(SERVIDOR));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_CPF), responsavel)) {
                servidor.setCpf((String) paramResposta.get(SER_CPF));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_MATRICULA), responsavel)) {
                servidor.setMatricula((String) paramResposta.get(RSE_MATRICULA));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_DATA_NASC), responsavel)) {
                final Object dataNasc = paramResposta.get(SER_DATA_NASC);
                try {
                    if (!TextHelper.isNull(dataNasc)) {
                        if (dataNasc instanceof String) {
                            servidor.setDataNascimento(factory.createServidorDataNascimento(toXMLGregorianCalendar(DateHelper.parse(dataNasc.toString(), LocaleHelper.getDatePattern()), false)));
                        } else if (dataNasc instanceof final Date value) {
                            servidor.setDataNascimento(factory.createServidorDataNascimento(toXMLGregorianCalendar(value, false)));
                        }
                    }
                } catch (final Exception ex) {
                    LOG.warn("Erro de formatação da data de nascimento do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
                }
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_DATA_ADMISSAO), responsavel)) {
                final Object dataAdmissao = paramResposta.get(RSE_DATA_ADMISSAO);
                try {
                    if (!TextHelper.isNull(dataAdmissao)) {
                        if (dataAdmissao instanceof String) {
                            servidor.setDataAdmissao(factory.createServidorDataAdmissao(toXMLGregorianCalendar(DateHelper.parse(dataAdmissao.toString(), LocaleHelper.getDatePattern()), false)));
                        } else if (dataAdmissao instanceof final Date value) {
                            servidor.setDataAdmissao(factory.createServidorDataAdmissao(toXMLGregorianCalendar(value, false)));
                        }
                    }
                } catch (final Exception ex) {
                    LOG.warn("Erro de formatação da data de admissão do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
                }

            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_PRAZO), responsavel)) {
                if (paramResposta.get(RSE_PRAZO) != null) {
                    servidor.setPrazoServidor(factory.createServidorPrazoServidor((Integer) paramResposta.get(RSE_PRAZO)));
                } else {
                    servidor.setPrazoServidor(factory.createServidorPrazoServidor(-1));
                }
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SITUACAO_SERVIDOR), responsavel)) {
                final RegistroRespostaRequisicaoExterna resSrsCodigo = (RegistroRespostaRequisicaoExterna) paramResposta.get(SITUACAO_SERVIDOR);
                final Map<CamposAPI, Object> srsMap = resSrsCodigo.getAtributos();
                final com.zetra.econsig.webservice.soap.operacional.v8.SituacaoServidor situacaoSrs = new com.zetra.econsig.webservice.soap.operacional.v8.SituacaoServidor();
                situacaoSrs.setAtivo(false);
                situacaoSrs.setBloqueado(false);
                situacaoSrs.setExcluido(false);
                situacaoSrs.setFalecido(false);
                situacaoSrs.setPendente(false);

                if (srsMap.containsKey(SRS_ATIVO)) {
                    situacaoSrs.setAtivo(true);
                } else if (srsMap.containsKey(SRS_BLOQUEADO)) {
                    situacaoSrs.setBloqueado(true);
                } else if (srsMap.containsKey(SRS_EXCLUIDO)) {
                    situacaoSrs.setExcluido(true);
                } else if (srsMap.containsKey(SRS_FALECIDO)) {
                    situacaoSrs.setFalecido(true);
                } else if (srsMap.containsKey(SRS_PENDENTE)) {
                    situacaoSrs.setPendente(true);
                }

                servidor.setSituacaoServidor(factory.createServidorSituacaoServidor(situacaoSrs));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_SALARIO), responsavel) &&
                (paramResposta.get(RSE_SALARIO) != null)) {
                if (paramResposta.get(RSE_SALARIO) instanceof final Double value) {
                    servidor.setSalarioLiquido(factory.createServidorSalarioLiquido(value));
                } else if (paramResposta.get(RSE_SALARIO) instanceof final BigDecimal value) {
                    servidor.setSalarioLiquido(factory.createServidorSalarioLiquido(value.doubleValue()));
                }
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_PROVENTOS), responsavel) && (paramResposta.get(RSE_PROVENTOS) != null)) {
                if (paramResposta.get(RSE_PROVENTOS) instanceof final Double value) {
                    servidor.setSalarioBruto(factory.createServidorSalarioBruto(value));
                } else if (paramResposta.get(RSE_PROVENTOS) instanceof final BigDecimal value) {
                    servidor.setSalarioBruto(factory.createServidorSalarioBruto(value.doubleValue()));
                }
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_NRO_IDT), responsavel)) {
                servidor.setIdentidade(factory.createServidorIdentidade((String) paramResposta.get(SER_NRO_IDT)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_DATA_IDT), responsavel)) {
                final Object dataIdentidade = paramResposta.get(SER_DATA_IDT);
                try {
                    if (!TextHelper.isNull(dataIdentidade)) {
                        if (dataIdentidade instanceof String) {
                            servidor.setDataIdentidade(factory.createServidorDataIdentidade(toXMLGregorianCalendar(DateHelper.parse(dataIdentidade.toString(), LocaleHelper.getDatePattern()), false)));
                        } else if (dataIdentidade instanceof final Date value) {
                            servidor.setDataIdentidade(factory.createServidorDataIdentidade(toXMLGregorianCalendar(value, false)));
                        }
                    }
                } catch (final Exception ex) {
                    LOG.warn("Erro de formatação da data da identidade do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
                }
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_UF_IDT), responsavel)) {
                servidor.setUfIdentidade(factory.createServidorUfIdentidade((String) paramResposta.get(SER_UF_IDT)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_EMISSOR_IDT), responsavel)) {
                servidor.setEmissorIdentidade(factory.createServidorEmissorIdentidade((String) paramResposta.get(SER_EMISSOR_IDT)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_CID_NASC), responsavel)) {
                servidor.setCidadeNascimento(factory.createServidorCidadeNascimento((String) paramResposta.get(SER_CID_NASC)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_NACIONALIDADE), responsavel)) {
                servidor.setNacionalidade(factory.createServidorNacionalidade((String) paramResposta.get(SER_NACIONALIDADE)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_SEXO), responsavel)) {
                servidor.setSexo(factory.createServidorSexo((String) paramResposta.get(SER_SEXO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_EST_CIVIL), responsavel)) {
                servidor.setEstadoCivil(factory.createServidorEstadoCivil((String) paramResposta.get(SER_EST_CIVIL)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_END), responsavel)) {
                servidor.setEndereco(factory.createServidorEndereco((String) paramResposta.get(SER_END)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_NRO), responsavel)) {
                servidor.setNumero(factory.createServidorNumero((String) paramResposta.get(SER_NRO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_COMPL), responsavel)) {
                servidor.setComplemento(factory.createServidorComplemento((String) paramResposta.get(SER_COMPL)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_BAIRRO), responsavel)) {
                servidor.setBairro(factory.createServidorBairro((String) paramResposta.get(SER_BAIRRO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_CIDADE), responsavel)) {
                servidor.setCidade(factory.createServidorCidade((String) paramResposta.get(SER_CIDADE)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_UF), responsavel)) {
                servidor.setUf(factory.createServidorUf((String) paramResposta.get(SER_UF)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_CEP), responsavel)) {
                servidor.setCep(factory.createServidorCep((String) paramResposta.get(SER_CEP)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_TEL), responsavel)) {
                servidor.setTelefone(factory.createServidorTelefone((String) paramResposta.get(SER_TEL)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_CELULAR), responsavel)) {
                servidor.setCelular(factory.createServidorCelular((String) paramResposta.get(SER_CELULAR)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_SALARIO), responsavel)) {
                if (!TextHelper.isNull(paramResposta.get(RSE_SALARIO)) && !(paramResposta.get(RSE_SALARIO) instanceof BigDecimal)) {
                    try {
                        servidor.setSalario(factory.createServidorSalario(NumberHelper.parse((paramResposta.get(RSE_SALARIO)).toString(), "en")));
                    } catch (final NumberFormatException | ParseException e) {
                        LOG.warn("ERRO AO RECUPERAR RSE_SALARIO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
                    }
                } else if (!TextHelper.isNull(paramResposta.get(RSE_SALARIO))) {
                    servidor.setSalario(factory.createServidorSalario(((BigDecimal) paramResposta.get(RSE_SALARIO)).doubleValue()));
                } else {
                    servidor.setSalario(factory.createServidorSalario(Double.NaN));
                }
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_DATA_SAIDA), responsavel)) {
                final Object dataSaida = paramResposta.get(RSE_DATA_SAIDA);
                try {
                    if (!TextHelper.isNull(dataSaida)) {
                        if (dataSaida instanceof final String value) {
                            servidor.setDataSaida(factory.createServidorDataSaida(toXMLGregorianCalendar(DateHelper.parse(value, LocaleHelper.getDatePattern()), false)));
                        } else if (dataSaida instanceof final Date value) {
                            servidor.setDataSaida(factory.createServidorDataSaida(toXMLGregorianCalendar(value, false)));
                        }
                    }
                } catch (final Exception ex) {
                    LOG.warn("Erro de formatação da data de saida do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
                }
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_BANCO), responsavel)) {
                servidor.setBanco(factory.createServidorBanco((String) paramResposta.get(RSE_BANCO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_AGENCIA), responsavel)) {
                servidor.setAgencia(factory.createServidorAgencia((String) paramResposta.get(RSE_AGENCIA)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_CONTA), responsavel)) {
                servidor.setConta(factory.createServidorConta((String) paramResposta.get(RSE_CONTA)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(CARGO_CODIGO), responsavel)) {
                servidor.setCargoCodigo(factory.createServidorCargoCodigo((String) paramResposta.get(CARGO_CODIGO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(CARGO_DESCRICAO), responsavel)) {
                servidor.setCargoDescricao(factory.createServidorCargoDescricao((String) paramResposta.get(CARGO_DESCRICAO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(POSTO_CODIGO), responsavel)) {
                servidor.setPostoCodigo(factory.createServidorPostoCodigo((String) paramResposta.get(POSTO_CODIGO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(POSTO_DESCRICAO), responsavel)) {
                servidor.setPostoDescricao(factory.createServidorPostoDescricao((String) paramResposta.get(POSTO_DESCRICAO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(HABITACAO_CODIGO), responsavel)) {
                servidor.setHabitacaoCodigo(factory.createServidorHabitacaoCodigo((String) paramResposta.get(HABITACAO_CODIGO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(HABITACAO_DESCRICAO), responsavel)) {
                servidor.setHabitacaoDescricao(factory.createServidorHabitacaoDescricao((String) paramResposta.get(HABITACAO_DESCRICAO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(ESCOLARIDADE_CODIGO), responsavel)) {
                servidor.setEscolaridadeCodigo(factory.createServidorEscolaridadeCodigo((String) paramResposta.get(ESCOLARIDADE_CODIGO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(ESCOLARIDADE_DESCRICAO), responsavel)) {
                servidor.setEscolaridadeDescricao(factory.createServidorEscolaridadeDescricao((String) paramResposta.get(ESCOLARIDADE_DESCRICAO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_NOME_MAE), responsavel)) {
                servidor.setNomeMae(factory.createServidorNomeMae((String) paramResposta.get(SER_NOME_MAE)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_NOME_PAI), responsavel)) {
                servidor.setNomePai(factory.createServidorNomePai((String) paramResposta.get(SER_NOME_PAI)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_CART_PROF), responsavel)) {
                servidor.setCartProf(factory.createServidorCartProf((String) paramResposta.get(SER_CART_PROF)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_PIS), responsavel)) {
                servidor.setPis(factory.createServidorPis((String) paramResposta.get(SER_PIS)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_EMAIL), responsavel)) {
                servidor.setEmail(factory.createServidorEmail((String) paramResposta.get(SER_EMAIL)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_NOME_CONJUGE), responsavel)) {
                servidor.setNomeConjuge(factory.createServidorNomeConjuge((String) paramResposta.get(SER_NOME_CONJUGE)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_NOME_MEIO), responsavel)) {
                servidor.setNomeMeio(factory.createServidorNomeMeio((String) paramResposta.get(SER_NOME_MEIO)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_ULTIMO_NOME), responsavel)) {
                servidor.setUltimoNome(factory.createServidorUltimoNome((String) paramResposta.get(SER_ULTIMO_NOME)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_PRIMEIRO_NOME), responsavel)) {
                servidor.setPrimeiroNome(factory.createServidorPrimeiroNome((String) paramResposta.get(SER_PRIMEIRO_NOME)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_DESCONTOS_COMP), responsavel) &&
                (paramResposta.get(RSE_DESCONTOS_COMP) != null)) {
                if (paramResposta.get(RSE_DESCONTOS_COMP) instanceof final Double value) {
                    servidor.setDescontosComp(factory.createServidorDescontosComp(value));
                } else if (paramResposta.get(RSE_DESCONTOS_COMP) instanceof final BigDecimal value) {
                    servidor.setDescontosComp(factory.createServidorDescontosComp(value.doubleValue()));
                }
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_DESCONTOS_FACU), responsavel) &&
                (paramResposta.get(RSE_DESCONTOS_FACU) != null)) {
                if (paramResposta.get(RSE_DESCONTOS_FACU) instanceof final Double value) {
                    servidor.setDescontosFacu(factory.createServidorDescontosFacu(value));
                } else if (paramResposta.get(RSE_DESCONTOS_FACU) instanceof final BigDecimal value) {
                    servidor.setDescontosFacu(factory.createServidorDescontosFacu(value.doubleValue()));
                }
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_OUTROS_DESCONTOS), responsavel) &&
                (paramResposta.get(RSE_OUTROS_DESCONTOS) != null)) {
                if (paramResposta.get(RSE_OUTROS_DESCONTOS) instanceof final Double value) {
                    servidor.setOutrosDescontos(factory.createServidorOutrosDescontos(value));
                } else if (paramResposta.get(RSE_OUTROS_DESCONTOS) instanceof final BigDecimal value) {
                    servidor.setOutrosDescontos(factory.createServidorOutrosDescontos(value.doubleValue()));
                }
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_MATRICULA_INST), responsavel)) {
                servidor.setMatriculaInst(factory.createServidorMatriculaInst((String) paramResposta.get(RSE_MATRICULA_INST)));
            }
            if (ShowFieldHelper.showField(fieldKeys.get(RSE_DATA_RETORNO), responsavel)) {
                final Object dataRetorno = paramResposta.get(RSE_DATA_RETORNO);
                try {
                    if (!TextHelper.isNull(dataRetorno)) {
                        if (dataRetorno instanceof String) {
                            servidor.setDataRetorno(factory.createServidorDataRetorno(toXMLGregorianCalendar(DateHelper.parse(dataRetorno.toString(), LocaleHelper.getDatePattern()), false)));
                        } else if (dataRetorno instanceof final Date value) {
                            servidor.setDataRetorno(factory.createServidorDataRetorno(toXMLGregorianCalendar(value, false)));
                        }
                    }
                } catch (final Exception ex) {
                    LOG.warn("Erro de formatação da data de nascimento do " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", null));
                }
            }
            if (ShowFieldHelper.showField(fieldKeys.get(SER_QTD_FILHOS), responsavel) &&
                !TextHelper.isNull(paramResposta.get(SER_QTD_FILHOS))) {
                servidor.setQtdFilhos(factory.createServidorQtdFilhos(Integer.valueOf(paramResposta.get(SER_QTD_FILHOS).toString())));
            }

            if ((fieldKeys.get(MARGENS) != null) && ShowFieldHelper.showField(fieldKeys.get(MARGENS), responsavel)) {
                final List<MargemTO> margens = (List<MargemTO>) paramResposta.get(MARGENS);
                if ((margens != null) && !margens.isEmpty()) {
                    for (final MargemTO margemTO : margens) {
                        final com.zetra.econsig.webservice.soap.operacional.v8.Margem margem = new com.zetra.econsig.webservice.soap.operacional.v8.Margem();
                        margem.setCodigo(margemTO.getMarCodigo().toString());
                        margem.setDescricao(margemTO.getMarDescricao());
                        if (!TextHelper.isNull(margemTO.getMrsMargem()) && retornarVlrFolhaVlrUsadoMargem(responsavel)) {
                            margem.setValorFolha(factory.createMargemValorFolha(margemTO.getMrsMargem().doubleValue()));
                        } else {
                            margem.setValorFolha(factory.createMargemValorFolha(Double.NaN));
                        }
                        if (!TextHelper.isNull(margemTO.getMrsMargemUsada()) && retornarVlrFolhaVlrUsadoMargem(responsavel)) {
                            margem.setValorUsado(factory.createMargemValorUsado(margemTO.getMrsMargemUsada().doubleValue()));
                        } else {
                            margem.setValorUsado(factory.createMargemValorUsado(Double.NaN));
                        }
                        if (!TextHelper.isNull(margemTO.getMrsMargemRest())) {
                            margem.setValorDisponivel(factory.createMargemValorDisponivel(margemTO.getMrsMargemRest().doubleValue()));
                        } else {
                            margem.setValorDisponivel(factory.createMargemValorDisponivel(Double.NaN));
                        }
                        if (!TextHelper.isNull(margemTO.getMargemLimite())) {
                            margem.setValorLimite(factory.createMargemValorLimite(margemTO.getMargemLimite().doubleValue()));
                        } else {
                            margem.setValorLimite(factory.createMargemValorLimite(Double.NaN));
                        }
                        if (!TextHelper.isNull(margemTO.getObservacao())) {
                            margem.setMensagem(factory.createMargemMensagem(margemTO.getObservacao()));
                        } else {
                            margem.setMensagem(factory.createMargemMensagem(""));
                        }

                        servidor.getMargens().add(margem);
                    }
                }
            }

            if (ShowFieldHelper.showField(fieldKeys.get(DADOS_ADICIONAIS), responsavel)) {
                final List<TransferObject> listaDadosAdicionaisServidor = (List<TransferObject>) paramResposta.get(DADOS_ADICIONAIS);
                if ((listaDadosAdicionaisServidor != null) && !listaDadosAdicionaisServidor.isEmpty()) {
                    for (final TransferObject dadoAdicionalTO : listaDadosAdicionaisServidor) {
                        if ((Log.SERVIDOR.equals(dadoAdicionalTO.getAttribute(Columns.TDA_TEN_CODIGO)))) {
                            final String codigo = (String) dadoAdicionalTO.getAttribute(Columns.TDA_CODIGO);
                            final String descricao = (String) dadoAdicionalTO.getAttribute(Columns.TDA_DESCRICAO);
                            final String valor = (String) dadoAdicionalTO.getAttribute(Columns.DAS_VALOR);

                            final com.zetra.econsig.webservice.soap.operacional.v8.DadoAdicional dadoAdicional = new com.zetra.econsig.webservice.soap.operacional.v8.DadoAdicional();
                            dadoAdicional.setCodigo(codigo);
                            dadoAdicional.setDescricao(descricao);
                            dadoAdicional.setValor(valor);
                            servidor.getDados().add(dadoAdicional);
                        }
                    }

                }
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private static boolean retornarVlrFolhaVlrUsadoMargem(AcessoSistema responsavel) {
        if (responsavel != null) {
            if (responsavel.isCseSupOrg()) {
                return true;
            } else if (responsavel.isCsaCor()) {
                final String param = ParamCsa.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_RETORNA_VLR_FOLHA_E_VLR_USADO_CONSULTA_MARGEM_SOAP_WEB, responsavel);
                return CodedValues.TPA_SIM.equals(param);
            }
        }
        return false;
    }
}
