package com.zetra.econsig.webservice.legacyxml;

import static com.zetra.econsig.webservice.CamposAPI.ADE_CARENCIA;
import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_SEGURO_PRESTAMISTA;
import static com.zetra.econsig.webservice.CamposAPI.ADE_TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_IOF;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_MENS_VINC;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_TAC;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.COR_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.DATA_DESCONTO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.NOVO_ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.NSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.OCORRENCIA_DATA;
import static com.zetra.econsig.webservice.CamposAPI.OCORRENCIA_OBS;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PARCELA;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_DESCONTO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.xml.XmlHelper;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.config.DocumentoTipo;
import com.zetra.econsig.parser.config.ObjectFactory;
import com.zetra.econsig.parser.config.RegistroTipo;
import com.zetra.econsig.web.filter.XSSPreventionFilter;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.RequisicaoExternaAppController;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;

/**
 * <p>Title: RequisicaoXmlFrontController</p>
 * <p>Description: front controller para requisição externa via XML.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RequisicaoXmlFrontController extends AbstractRequisicaoFrontController{
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RequisicaoXmlFrontController.class);

    // Objeto que representa o documento XML de requisição
    private DocumentoTipo doc;

    // lista de requisições de operações entradas no formulário de XML
    private final List<Map<CamposAPI, Object>> requisicoes = new ArrayList<>();
    private final InputStream entrada;

    public RequisicaoXmlFrontController(InputStream entrada, OutputStream saida, AcessoSistema responsavel) throws RequisicaoFrontControllerException {
            this.responsavel = responsavel;
            this.saida = saida;
            this.entrada = entrada;
    }

    private void createDocument() throws RequisicaoFrontControllerException {
        try {
            doc = XmlHelper.unmarshal(entrada);
        } catch (ParserException ex) {
            RequisicaoFrontControllerException rfex = new RequisicaoFrontControllerException("mensagem.xmlRequisicaoInvalido", responsavel, ex);
            geraRegistroErro(rfex);
        } catch (Exception ex) {
            RequisicaoFrontControllerException rfex = new RequisicaoFrontControllerException("mensagem.erroInternoSistema", responsavel, ex);
            geraRegistroErro(rfex);
        }
    }

    private void geraRegistroErro(RequisicaoFrontControllerException rfex) throws RequisicaoFrontControllerException {
        Map<CamposAPI, Object> param = new EnumMap<>(CamposAPI.class);
        param.put(COD_RETORNO, rfex.getResourcesMessage(ZetraException.MENSAGEM_PROCESSAMENTO_XML));
        param.put(MENSAGEM, rfex.getMessage());
        DocumentoTipo resultado = (DocumentoTipo) geraSaida(param);
        try {
            OutputStream result = new ByteArrayOutputStream();
            XmlHelper.marshal(resultado, result);
            saida = result;
            throw rfex;
        } catch (ParserException jex) {
            LOG.error(jex.getMessage(), jex);
            throw new RequisicaoFrontControllerException("mensagem.erroInternoSistema", responsavel, jex);
        }
    }

    @Override
    protected Object geraSaida(Map<CamposAPI, Object> parametros) throws RequisicaoFrontControllerException {
        RespostaRequisicaoExternaCommand respostaCmnd;
        DocumentoTipo resultado = null;
        try {
            respostaCmnd = RequisicaoExternaAppController.createRespostaRequisicaoExterna(parametros, responsavel);

            List<RegistroRespostaRequisicaoExterna> listResposta = respostaCmnd.geraResposta(parametros);

            ObjectFactory factory = new ObjectFactory();

            resultado = factory.createDocumentoTipo();
            resultado.setID(XmlHelper.VERSAO);

            // Pega a referência para a lista de registros
            // que inicialmente está vazia
            List<RegistroTipo> registros = resultado.getRegistro();

            for (RegistroRespostaRequisicaoExterna resposta: listResposta) {
                RegistroTipo reg = factory.createRegistroTipo();
                reg.setNome(resposta.getNome().toString());
                XmlHelper.adicionaAtributo(reg.getAtributo(), factory, desconverterMap(resposta.getAtributos()), desconverterList(resposta.getKeyOrder()));
                registros.add(reg);
            }

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RequisicaoFrontControllerException(ex);
        }
        return resultado;
    }

    @Override
    public void processa() throws RequisicaoFrontControllerException {
        createDocument();
        preProcessa(converterMap(XmlHelper.parametrosToMap(doc.getParametro())));
        processa(requisicoes);
        geraSaida(requisicoes);
    }

    private static Map<CamposAPI, Object> converterMap(Map<String, Object> parametros) {
        Map<CamposAPI, Object> convertido = new EnumMap<>(CamposAPI.class);
        for (String chave : parametros.keySet()) {
            Object valor = parametros.get(chave);
            CamposAPI campo = switch (chave) {
                case "ADE_IDENTIFICADOR" -> ADE_IDENTIFICADOR;
                case "ADE_INDICE" -> ADE_INDICE;
                case "ADE_NUMERO" -> ADE_NUMERO;
                case "ADE_SEGURO_PRESTAMISTA" -> ADE_SEGURO_PRESTAMISTA;
                case "ADE_TAXA_JUROS" -> ADE_TAXA_JUROS;
                case "ADE_VLR_IOF" -> ADE_VLR_IOF;
                case "ADE_VLR_MENS_VINC" -> ADE_VLR_MENS_VINC;
                case "ADE_VLR_TAC" -> ADE_VLR_TAC;
                case "AGENCIA" -> RSE_AGENCIA;
                case "BANCO" -> RSE_BANCO;
                case "CARENCIA" -> ADE_CARENCIA;
                case "CLIENTE" -> CLIENTE;
                case "COD_VERBA" -> CNV_COD_VERBA;
                case "CONTA" -> RSE_CONTA;
                case "CONVENIO" -> CONVENIO;
                case "CORRESPONDENTE_CODIGO" -> COR_IDENTIFICADOR;
                case "CPF" -> SER_CPF;
                case "DATA_DESCONTO" -> DATA_DESCONTO;
                case "DATA_NASC" -> DATA_NASC;
                case "ESTABELECIMENTO_CODIGO" -> EST_IDENTIFICADOR;
                case "MATRICULA" -> RSE_MATRICULA;
                case "NATUREZA_SERVICO_CODIGO" -> NSE_CODIGO;
                case "NOVO_ADE_IDENTIFICADOR" -> NOVO_ADE_IDENTIFICADOR;
                case "OCORRENCIA_DATA" -> OCORRENCIA_DATA;
                case "OCORRENCIA_OBS" -> OCORRENCIA_OBS;
                case "OPERACAO" -> OPERACAO;
                case "ORGAO_CODIGO" -> ORG_IDENTIFICADOR;
                case "PARCELA" -> PARCELA;
                case "PRAZO" -> PRAZO;
                case "SENHA" -> SENHA;
                case "SER_LOGIN" -> SER_LOGIN;
                case "SER_SENHA" -> SER_SENHA;
                case "SERVICO_CODIGO" -> SERVICO_CODIGO;
                case "SITUACAO_CODIGO" -> SITUACAO_CODIGO;
                case "USUARIO" -> USUARIO;
                case "VALOR_DESCONTO" -> VALOR_DESCONTO;
                case "VALOR_LIBERADO" -> VALOR_LIBERADO;
                case "VALOR_PARCELA" -> VALOR_PARCELA;
                case "TMO_IDENTIFICADOR" -> TMO_IDENTIFICADOR;
                case "TMO_OBS" -> TMO_OBS;
                default -> null;
            };
            if (campo != null) {
                convertido.put(campo, valor);
            } else {
                LOG.warn("Campo nao mapeado XML: " + chave);
            }
        }
        return convertido;
    }

    private static Map<String, Object> desconverterMap(Map<CamposAPI, Object> parametros) {
        Map<String, Object> convertido = new HashMap<>();
        for (CamposAPI chave : parametros.keySet()) {
            Object valor = parametros.get(chave);
            String campo = desconverterCampo(chave);
            convertido.put(campo, valor);
        }
        return convertido;
    }

    private static List<String> desconverterList(List<CamposAPI> campos) {
        List<String> convertido = new ArrayList<>();
        for (CamposAPI campo : campos) {
            convertido.add(desconverterCampo(campo));
        }
        return convertido;
    }

    private static String desconverterCampo(CamposAPI campo) {
        // TODO incluir outras chaves que forem "renomeadas"
        return switch (campo) {
            case EST_IDENTIFICADOR -> "ESTABELECIMENTO_CODIGO";
            case ORG_IDENTIFICADOR -> "ORGAO_CODIGO";
            case CSA_IDENTIFICADOR -> "CONSIGNATARIA_CODIGO";
            case COR_IDENTIFICADOR -> "CORRESPONDENTE_CODIGO";
            case RSE_MATRICULA -> "MATRICULA";
            case RSE_MATRICULA_INST -> "MATRICULA_INST";
            case RSE_TIPO -> "CATEGORIA";
            case RSE_DATA_ADMISSAO -> "DATA_ADMISSAO";
            case RSE_PRAZO -> "PRAZO_SERVIDOR";
            case RSE_DATA_SAIDA -> "DATA_SAIDA";
            case RSE_DATA_RETORNO -> "DATA_RETORNO";
            case RSE_SALARIO -> "SALARIO";
            case RSE_PROVENTOS -> "PROVENTOS";
            case RSE_DESCONTOS_COMP -> "DESCONTOS_COMP";
            case RSE_DESCONTOS_FACU -> "DESCONTOS_FACU";
            case RSE_OUTROS_DESCONTOS -> "OUTROS_DESCONTOS";
            case RSE_BANCO -> "BANCO";
            case RSE_AGENCIA -> "AGENCIA";
            case RSE_CONTA -> "CONTA";
            case SER_CPF -> "CPF";
            case SER_NOME_CONJUGE -> "NOME_CONJUGE";
            case SER_PRIMEIRO_NOME -> "PRIMEIRO_NOME";
            case SER_NOME_MEIO -> "NOME_MEIO";
            case SER_ULTIMO_NOME -> "ULTIMO_NOME";
            case SER_NOME_PAI -> "PAI";
            case SER_NOME_MAE -> "MAE";
            case SER_NRO_IDT -> "IDENTIDADE";
            case SER_EMISSOR_IDT -> "EMISSOR_IDENTIDADE";
            case SER_UF_IDT -> "UF_IDENTIDADE";
            case SER_DATA_IDT -> "DATA_IDENTIDADE";
            case SER_TEL -> "TELEFONE";
            case SER_CELULAR -> "CELULAR";
            case SER_EMAIL -> "EMAIL";
            case SER_SEXO -> "SEXO";
            case SER_NACIONALIDADE -> "NACIONALIDADE";
            case SER_EST_CIVIL -> "ESTADO_CIVIL";
            case SER_CID_NASC -> "CIDADE_NASCIMENTO";
            case SER_QTD_FILHOS -> "QTD_FILHOS";
            case SER_END -> "ENDERECO";
            case SER_NRO -> "NUMERO";
            case SER_BAIRRO -> "BAIRRO";
            case SER_CIDADE -> "CIDADE";
            case SER_COMPL -> "COMPLEMENTO";
            case SER_UF -> "UF";
            case SER_CEP -> "CEP";
            case SER_CART_PROF -> "CARTEIRA_TRABALHO";
            case SER_PIS -> "PIS";
            case ORG_ENDERECO -> "ORGAO_ENDERECO";
            case ORG_NUMERO -> "ORGAO_NUMERO";
            case ORG_COMPLEMENTO -> "ORGAO_COMPLEMENTO";
            case ORG_BAIRRO -> "ORGAO_BAIRRO";
            case ORG_CIDADE -> "ORGAO_CIDADE";
            case ORG_UF -> "ORGAO_UF";
            case ORG_CEP -> "ORGAO_CEP";
            case ORG_TELEFONE -> "ORGAO_TELEFONE";
            case CNV_COD_VERBA -> "COD_VERBA";
            case NSE_CODIGO -> "NATUREZA_SERVICO_CODIGO";
            case ADE_CARENCIA -> "CARENCIA";
            case ADE_PRD_PAGAS -> "PAGAS";
            case MARGEM_1 -> "MARGEM1";
            case MARGEM_2 -> "MARGEM2";
            case MARGEM_3 -> "MARGEM3";

            // Por padrão, assume o texto escrito na chave
            default -> campo.toString();
        };
    }

    /**
     * retorna lista de parâmetros correspondentes a cada registro da requisição XML
     * @param requisicoes
     * @return
     * @throws RequisicaoFrontControllerException
     */
    protected void processa(List<Map<CamposAPI, Object>> requisicoes) throws RequisicaoFrontControllerException {
        for (Map<CamposAPI, Object> paramRequisicao : requisicoes) {
            try {
                RequisicaoExternaCommand opCmnd = RequisicaoExternaAppController.createRequisicaoExternaCommand(paramRequisicao, responsavel);
                opCmnd.autenticaUsuario(paramRequisicao);
                validaPermissao(opCmnd.getResponsavel());
                opCmnd.processa();
            } catch (ZetraException ex) {
                if (ex.getMessageKey() != null) {
                    paramRequisicao.put(COD_RETORNO, ex.getResourcesMessage(ZetraException.MENSAGEM_PROCESSAMENTO_XML));
                }
                paramRequisicao.put(MENSAGEM, ex.getMessage());
            }
        }
    }

    @Override
    protected void preProcessa(Map<CamposAPI, Object> parametros) throws RequisicaoFrontControllerException {
        // Valida os parâmetros para evitar inclusão de XSS
        parametros = XSSPreventionFilter.stripXSS_API(parametros);

        try {
            super.preProcessa(parametros);
        } catch (RequisicaoFrontControllerException ex) {
            DocumentoTipo resultado = (DocumentoTipo) geraSaida(parametros);
            try {
                OutputStream result = new ByteArrayOutputStream();
                XmlHelper.marshal(resultado, result);
                saida = result;
                throw ex;
            } catch (ParserException jex) {
                LOG.error(jex.getMessage(), jex);
                throw new RequisicaoFrontControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }

        if (doc != null) {
            List<RegistroTipo> registros = doc.getRegistro();
            try {
                if (registros != null && registros.size() > 0) {
                    for (RegistroTipo proximo : registros) {
                        if (proximo.getNome() != null && proximo.getNome().equalsIgnoreCase("REQUISICAO")) {
                            Map<CamposAPI, Object> parametrosRegistro = converterMap(XmlHelper.parametrosToMap(proximo.getAtributo()));
                            parametrosRegistro = XSSPreventionFilter.stripXSS_API(parametrosRegistro);
                            Map<CamposAPI, Object> requisicao = new EnumMap<>(CamposAPI.class);
                            requisicao.putAll(parametros);
                            requisicao.putAll(parametrosRegistro);
                            validaOperacao((String) parametros.get(OPERACAO));
                            requisicoes.add(requisicao);
                        }
                    }
                } else {
                    validaOperacao((String) parametros.get(OPERACAO));
                    requisicoes.add(parametros);
                }

                RequisicaoFrontControllerException rfex = null;

                // Verifica a versão do documento
                String id = doc.getID();
                if (id == null || id.equals("")) {
                    rfex = new RequisicaoFrontControllerException("mensagem.xmlVersaoNaoInformada", responsavel, XmlHelper.VERSAO);
                } else if (!id.equals(XmlHelper.VERSAO)) {
                    rfex = new RequisicaoFrontControllerException("mensagem.xmlVersaoInformadaInvalida", responsavel, XmlHelper.VERSAO);
                }

                if (rfex != null) {
                    geraRegistroErro(rfex);
                }
            } catch (RequisicaoFrontControllerException ex) {
                if (ex.getMessageKey() != null) {
                    parametros.put(COD_RETORNO, ex.getResourcesMessage(ZetraException.MENSAGEM_PROCESSAMENTO_XML));
                }
                parametros.put(MENSAGEM, ex.getMessage());
                DocumentoTipo resultado = (DocumentoTipo) geraSaida(parametros);
                try {
                    OutputStream result = new ByteArrayOutputStream();
                    XmlHelper.marshal(resultado, result);
                    saida = result;
                    throw ex;
                } catch (ParserException jex) {
                    LOG.error(jex.getMessage(), jex);
                    throw new RequisicaoFrontControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
            }
        }
    }

    private void geraSaida(List<Map<CamposAPI, Object>> requisicoes) throws RequisicaoFrontControllerException {
        try {
            ObjectFactory factory = new ObjectFactory();
            DocumentoTipo resultado = factory.createDocumentoTipo();
            resultado.setID(XmlHelper.VERSAO);

            for (Map<CamposAPI, Object> parametros : requisicoes) {
                resultado = (DocumentoTipo) this.geraSaida(parametros);
            }

            OutputStream result = new ByteArrayOutputStream();
            XmlHelper.marshal(resultado, result);
            saida = result;
        } catch (ParserException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RequisicaoFrontControllerException(ex);
        }
    }
}
