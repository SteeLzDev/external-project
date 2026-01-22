package com.zetra.econsig.webservice.soap.operacional.assembler;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNATARIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.DATA_FINAL;
import static com.zetra.econsig.webservice.CamposAPI.DATA_INICIAL;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_RESERVA;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_NACIONALIDADE;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CEP;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.ORG_COMPLEMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_ENDERECO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_TELEFONE;
import static com.zetra.econsig.webservice.CamposAPI.ORG_UF;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_PRD_PAGAS;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RANKING;
import static com.zetra.econsig.webservice.CamposAPI.RESPONSAVEL;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_SAIDA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SAD_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_CID_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_COMPL;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMISSOR_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_EST_CIVIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_MAE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_PAI;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_QTD_FILHOS;
import static com.zetra.econsig.webservice.CamposAPI.SER_SEXO;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.Boleto;
import com.zetra.econsig.webservice.soap.operacional.v1.ObjectFactory;


/**
 * <p>Title: BoletoAssembler</p>
 * <p>Description: Assembler para Boleto.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class BoletoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BoletoAssembler.class);

    private BoletoAssembler() {
    }

    public static Boleto toBoletoV1(Map<CamposAPI, Object> paramResposta) {
        final ObjectFactory factory = new ObjectFactory();
        final Boleto boleto = new Boleto();

        boleto.setServidor((String) paramResposta.get(SERVIDOR));
        boleto.setCpf((String) paramResposta.get(SER_CPF));
        boleto.setSexo((String) paramResposta.get(SER_SEXO));

        if (!TextHelper.isNull(paramResposta.get(DATA_NASCIMENTO))) {
            try {
                boleto.setDataNascimento(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse((String) paramResposta.get(DATA_NASCIMENTO), LocaleHelper.getDatePattern()), false));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DE NASCIMENTO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }
        boleto.setEstadoCivil((String) paramResposta.get(SER_EST_CIVIL));
        boleto.setIdentidade((String) paramResposta.get(SER_NRO_IDT));
        boleto.setPai((String) paramResposta.get(SER_NOME_PAI));
        boleto.setMae((String) paramResposta.get(SER_NOME_MAE));
        boleto.setEndereco((String) paramResposta.get(SER_END));
        if (paramResposta.get(SER_NRO) != null) {
            boleto.setNumero((String) paramResposta.get(SER_NRO));
        } else {
            boleto.setNumero("-1");
        }
        boleto.setComplemento((String) paramResposta.get(SER_COMPL));
        boleto.setBairro((String) paramResposta.get(SER_BAIRRO));
        boleto.setCidade((String) paramResposta.get(SER_CIDADE));
        boleto.setUf((String) paramResposta.get(SER_UF));
        boleto.setCep((String) paramResposta.get(SER_CEP));
        boleto.setTelefone((String) paramResposta.get(SER_TEL));
        boleto.setMatricula((String) paramResposta.get(RSE_MATRICULA));

        if (!TextHelper.isNull(paramResposta.get(RSE_DATA_ADMISSAO))) {
            try {
                boleto.setDataAdmissao(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse((String) paramResposta.get(RSE_DATA_ADMISSAO), LocaleHelper.getDatePattern()), false));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DE ADMISSAO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }
        if (paramResposta.get(RSE_PRAZO) != null) {
            boleto.setPrazoServidor(Integer.valueOf(paramResposta.get(RSE_PRAZO).toString()));
        } else {
            boleto.setPrazoServidor(-1);
        }
        boleto.setCategoria((String) paramResposta.get(RSE_TIPO));
        boleto.setEstabelecimento((String) paramResposta.get(ESTABELECIMENTO));
        boleto.setOrgao((String) paramResposta.get(ORGAO));
        boleto.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
        boleto.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
        boleto.setOrgaoEndereco((String) paramResposta.get(ORG_ENDERECO));
        if (paramResposta.get(ORG_NUMERO) != null) {
            boleto.setOrgaoNumero((Integer) paramResposta.get(ORG_NUMERO));
        } else {
            boleto.setOrgaoNumero(-1);
        }
        boleto.setOrgaoComplemento((String) paramResposta.get(ORG_COMPLEMENTO));
        boleto.setOrgaoBairro((String) paramResposta.get(ORG_BAIRRO));
        boleto.setOrgaoCidade((String) paramResposta.get(ORG_CIDADE));
        boleto.setOrgaoUf((String) paramResposta.get(ORG_UF));
        boleto.setOrgaoCep((String) paramResposta.get(ORG_CEP));
        boleto.setOrgaoTelefone((String) paramResposta.get(ORG_TELEFONE));

        if (!TextHelper.isNull(paramResposta.get(VALOR_LIBERADO)) && !(paramResposta.get(VALOR_LIBERADO) instanceof BigDecimal)) {
            try {
                boleto.setValorLiberado(Double.valueOf(NumberHelper.reformat((String) paramResposta.get(VALOR_LIBERADO), NumberHelper.getLang(), "en", 2, 8)));
            } catch (final NumberFormatException | ParseException e) {
                LOG.warn("ERRO AO RECUPERAR VALOR LIBERADO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        } else if (paramResposta.get(VALOR_LIBERADO) != null) {
            boleto.setValorLiberado(((BigDecimal) paramResposta.get(VALOR_LIBERADO)).doubleValue());
        } else {
            boleto.setValorLiberado(-1d);
        }

        if (!TextHelper.isNull(paramResposta.get(TAXA_JUROS)) && !(paramResposta.get(TAXA_JUROS) instanceof BigDecimal)) {
            try {
                boleto.setTaxaJuros(factory.createBoletoTaxaJuros(Double.valueOf(NumberHelper.reformat((String) paramResposta.get(TAXA_JUROS), NumberHelper.getLang(), "en", 2, 8))));
            } catch (final NumberFormatException | ParseException e) {
                LOG.warn("ERRO AO RECUPERAR A TAXA DE JUROS PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        } else if (paramResposta.get(TAXA_JUROS) != null) {
            boleto.setTaxaJuros(factory.createBoletoTaxaJuros(((BigDecimal) paramResposta.get(TAXA_JUROS)).doubleValue()));
        }

        boleto.setConsignataria((String) paramResposta.get(CONSIGNATARIA));
        boleto.setConsignatariaCodigo(factory.createBoletoConsignatariaCodigo((String) paramResposta.get(CSA_IDENTIFICADOR)));
        boleto.setCodVerba((String) paramResposta.get(CNV_COD_VERBA));
        if (paramResposta.get(RANKING) != null) {
            boleto.setRanking((Short) paramResposta.get(RANKING));
        } else {
            boleto.setRanking(Short.valueOf("-1"));
        }
        boleto.setServico((String) paramResposta.get(SERVICO));

        if (!TextHelper.isNull(paramResposta.get(DATA_RESERVA))) {
            try {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateHelper.parse((String) paramResposta.get(DATA_RESERVA), LocaleHelper.getDateTimePattern()));
                boleto.setDataReserva(BaseAssembler.toXMLGregorianCalendar(calendar.getTime(), true));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DE RESERVA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        if (!TextHelper.isNull(paramResposta.get(DATA_INICIAL))) {
            try {
                boleto.setDataInicial(BaseAssembler.toXMLGregorianCalendar(DateHelper.parsePeriodString((String) paramResposta.get(DATA_INICIAL)), false));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA INICIAL PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        if (!TextHelper.isNull(paramResposta.get(DATA_FINAL))) {
            try {
                boleto.setDataFinal(BaseAssembler.toXMLGregorianCalendar(DateHelper.parsePeriodString((String) paramResposta.get(DATA_FINAL)), false));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA FINAL PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        if (!TextHelper.isNull(paramResposta.get(VALOR_PARCELA)) && !(paramResposta.get(VALOR_PARCELA) instanceof BigDecimal)) {
            try {
                boleto.setValorParcela(Double.parseDouble(NumberHelper.reformat(paramResposta.get(VALOR_PARCELA).toString(), NumberHelper.getLang(), "en", 2, 8)));
            } catch (final NumberFormatException | ParseException e) {
                LOG.warn("ERRO AO RECUPERAR VALOR DA PARCELA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        } else {
            boleto.setValorParcela(((BigDecimal) paramResposta.get(VALOR_PARCELA)).doubleValue());
        }
        boleto.setAdeNumero((Long) paramResposta.get(ADE_NUMERO));
        boleto.setAdeIdentificador((String) paramResposta.get(ADE_IDENTIFICADOR));
        boleto.setPrazo(Integer.parseInt(paramResposta.get(PRAZO).toString()));
        if (paramResposta.get(ADE_PRD_PAGAS) != null) {
            boleto.setPagas(Integer.parseInt(paramResposta.get(ADE_PRD_PAGAS).toString()));
        }
        boleto.setSituacao((String) paramResposta.get(SITUACAO));
        boleto.setServicoCodigo((String) paramResposta.get(SVC_IDENTIFICADOR));
        boleto.setStatusCodigo((String) paramResposta.get(SAD_CODIGO));
        boleto.setResponsavel((String) paramResposta.get(RESPONSAVEL));
        boleto.setIndice((String) paramResposta.get(ADE_INDICE));

        return boleto;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v3.Boleto toBoletoV3(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v3.Boleto boleto = new com.zetra.econsig.webservice.soap.operacional.v3.Boleto();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(boleto, toBoletoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return boleto;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v4.Boleto toBoletoV4(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.operacional.v4.Boleto boleto = new com.zetra.econsig.webservice.soap.operacional.v4.Boleto();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V1
            BeanUtils.copyProperties(boleto, toBoletoV1(paramResposta));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return boleto;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v6.Boleto toBoletoV6(Map<CamposAPI, Object> paramResposta, AcessoSistema responsavel) {
        final com.zetra.econsig.webservice.soap.operacional.v6.ObjectFactory factory = new com.zetra.econsig.webservice.soap.operacional.v6.ObjectFactory();
        final com.zetra.econsig.webservice.soap.operacional.v6.Boleto boleto = new com.zetra.econsig.webservice.soap.operacional.v6.Boleto();

        boleto.setServidor((String) paramResposta.get(SERVIDOR));
        boleto.setCpf((String) paramResposta.get(SER_CPF));
        boleto.setSexo((String) paramResposta.get(SER_SEXO));

        if (!TextHelper.isNull(paramResposta.get(DATA_NASCIMENTO))) {
            try {
                boleto.setDataNascimento(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse((String) paramResposta.get(DATA_NASCIMENTO), LocaleHelper.getDatePattern()), false));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DE NASCIMENTO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }
        boleto.setEstadoCivil((String) paramResposta.get(SER_EST_CIVIL));
        boleto.setIdentidade((String) paramResposta.get(SER_NRO_IDT));
        boleto.setPai((String) paramResposta.get(SER_NOME_PAI));
        boleto.setMae((String) paramResposta.get(SER_NOME_MAE));
        boleto.setEndereco((String) paramResposta.get(SER_END));
        if (paramResposta.get(SER_NRO) != null) {
            boleto.setNumero((String) paramResposta.get(SER_NRO));
        } else {
            boleto.setNumero("-1");
        }
        boleto.setComplemento((String) paramResposta.get(SER_COMPL));
        boleto.setBairro((String) paramResposta.get(SER_BAIRRO));
        boleto.setCidade((String) paramResposta.get(SER_CIDADE));
        boleto.setUf((String) paramResposta.get(SER_UF));
        boleto.setCep((String) paramResposta.get(SER_CEP));
        boleto.setTelefone((String) paramResposta.get(SER_TEL));
        boleto.setMatricula((String) paramResposta.get(RSE_MATRICULA));

        if (!TextHelper.isNull(paramResposta.get(RSE_DATA_ADMISSAO))) {
            try {
                boleto.setDataAdmissao(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse((String) paramResposta.get(RSE_DATA_ADMISSAO), LocaleHelper.getDatePattern()), false));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DE ADMISSAO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        //DESENV-9901: Se param de CSA habilitado, retorna prazoServidor = 0, se rsePrazo = NULL; prazoServidor = -1 se rseprazo <=0;
        //             e prazoServidor = rsePrazo se rsePrazo > 0
        final ParametroDelegate paramDelegate = new ParametroDelegate();
        try {
            String formataRsePrazo = null;
            if ((responsavel != null) && responsavel.isCsaCor()) {
                formataRsePrazo = paramDelegate.getParamCsa(responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.getCodigoEntidadePai(), CodedValues.TPA_ALTERA_RSE_PRAZO_SOAP, responsavel);
            }

            if (!TextHelper.isNull(formataRsePrazo) && CodedValues.TPA_SIM.equals(formataRsePrazo)) {
                if (TextHelper.isNull(paramResposta.get(RSE_PRAZO))) {
                    boleto.setPrazoServidor(0);
                } else {
                    final int rsePrazoAux = (Integer) paramResposta.get(RSE_PRAZO);

                    if (rsePrazoAux <= 0) {
                        boleto.setPrazoServidor(-1);
                    } else {
                        boleto.setPrazoServidor((Integer) paramResposta.get(RSE_PRAZO));
                    }
                }
            } else if (paramResposta.get(RSE_PRAZO) != null) {
                boleto.setPrazoServidor(Integer.parseInt(paramResposta.get(RSE_PRAZO).toString()));
            } else {
                boleto.setPrazoServidor(-1);
            }
        } catch (final ParametroControllerException e1) {
            LOG.warn("ERRO AO RECUPERAR PARÂMETRO DE CONSIGNATÁRIA " + CodedValues.TPA_ALTERA_RSE_PRAZO_SOAP);
        }
        boleto.setCategoria((String) paramResposta.get(RSE_TIPO));
        boleto.setEstabelecimento((String) paramResposta.get(ESTABELECIMENTO));
        boleto.setOrgao((String) paramResposta.get(ORGAO));
        boleto.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
        boleto.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
        boleto.setOrgaoEndereco((String) paramResposta.get(ORG_ENDERECO));
        if (paramResposta.get(ORG_NUMERO) != null) {
            boleto.setOrgaoNumero((Integer) paramResposta.get(ORG_NUMERO));
        } else {
            boleto.setOrgaoNumero(-1);
        }
        boleto.setOrgaoComplemento((String) paramResposta.get(ORG_COMPLEMENTO));
        boleto.setOrgaoBairro((String) paramResposta.get(ORG_BAIRRO));
        boleto.setOrgaoCidade((String) paramResposta.get(ORG_CIDADE));
        boleto.setOrgaoUf((String) paramResposta.get(ORG_UF));
        boleto.setOrgaoCep((String) paramResposta.get(ORG_CEP));
        boleto.setOrgaoTelefone((String) paramResposta.get(ORG_TELEFONE));

        if (!TextHelper.isNull(paramResposta.get(VALOR_LIBERADO)) && !(paramResposta.get(VALOR_LIBERADO) instanceof BigDecimal)) {
            try {
                boleto.setValorLiberado(Double.parseDouble(NumberHelper.reformat((String) paramResposta.get(VALOR_LIBERADO), NumberHelper.getLang(), "en", 2, 8)));
            } catch (final NumberFormatException | ParseException e) {
                LOG.warn("ERRO AO RECUPERAR VALOR LIBERADO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        } else if (paramResposta.get(VALOR_LIBERADO) != null) {
            boleto.setValorLiberado(((BigDecimal) paramResposta.get(VALOR_LIBERADO)).doubleValue());
        } else {
            boleto.setValorLiberado(-1d);
        }

        if (!TextHelper.isNull(paramResposta.get(TAXA_JUROS)) && !(paramResposta.get(TAXA_JUROS) instanceof BigDecimal)) {
            try {
                boleto.setTaxaJuros(factory.createBoletoTaxaJuros(Double.parseDouble(NumberHelper.reformat((String) paramResposta.get(TAXA_JUROS), NumberHelper.getLang(), "en", 2, 8))));
            } catch (final NumberFormatException | ParseException e) {
                LOG.warn("ERRO AO RECUPERAR A TAXA DE JUROS PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        } else if (paramResposta.get(TAXA_JUROS) != null) {
            boleto.setTaxaJuros(factory.createBoletoTaxaJuros(((BigDecimal) paramResposta.get(TAXA_JUROS)).doubleValue()));
        }

        boleto.setConsignataria((String) paramResposta.get(CONSIGNATARIA));
        boleto.setConsignatariaCodigo(factory.createBoletoConsignatariaCodigo((String) paramResposta.get(CSA_IDENTIFICADOR)));
        boleto.setCodVerba((String) paramResposta.get(CNV_COD_VERBA));
        if (paramResposta.get(RANKING) != null) {
            boleto.setRanking((Short) paramResposta.get(RANKING));
        } else {
            boleto.setRanking(Short.parseShort("-1"));
        }
        boleto.setServico((String) paramResposta.get(SERVICO));

        if (!TextHelper.isNull(paramResposta.get(DATA_RESERVA))) {
            try {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateHelper.parse((String) paramResposta.get(DATA_RESERVA), LocaleHelper.getDateTimePattern()));
                boleto.setDataReserva(BaseAssembler.toXMLGregorianCalendar(calendar.getTime(), true));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DE RESERVA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        if (!TextHelper.isNull(paramResposta.get(DATA_INICIAL))) {
            try {
                boleto.setDataInicial(BaseAssembler.toXMLGregorianCalendar(DateHelper.parsePeriodString((String) paramResposta.get(DATA_INICIAL)), false));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA INICIAL PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        if (!TextHelper.isNull(paramResposta.get(DATA_FINAL))) {
            try {
                boleto.setDataFinal(BaseAssembler.toXMLGregorianCalendar(DateHelper.parsePeriodString((String) paramResposta.get(DATA_FINAL)), false));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA FINAL PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        if (!TextHelper.isNull(paramResposta.get(VALOR_PARCELA)) && !(paramResposta.get(VALOR_PARCELA) instanceof BigDecimal)) {
            try {
                boleto.setValorParcela(Double.parseDouble(NumberHelper.reformat(paramResposta.get(VALOR_PARCELA).toString(), NumberHelper.getLang(), "en", 2, 8)));
            } catch (final NumberFormatException | ParseException e) {
                LOG.warn("ERRO AO RECUPERAR VALOR DA PARCELA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        } else {
            boleto.setValorParcela(((BigDecimal) paramResposta.get(VALOR_PARCELA)).doubleValue());
        }
        boleto.setAdeNumero((Long) paramResposta.get(ADE_NUMERO));
        boleto.setAdeIdentificador((String) paramResposta.get(ADE_IDENTIFICADOR));
        boleto.setPrazo(Integer.parseInt(paramResposta.get(PRAZO).toString()));
        if (paramResposta.get(ADE_PRD_PAGAS) != null) {
            boleto.setPagas(Integer.parseInt(paramResposta.get(ADE_PRD_PAGAS).toString()));
        }
        boleto.setSituacao((String) paramResposta.get(SITUACAO));
        boleto.setServicoCodigo((String) paramResposta.get(SVC_IDENTIFICADOR));
        boleto.setStatusCodigo((String) paramResposta.get(SAD_CODIGO));
        boleto.setResponsavel((String) paramResposta.get(RESPONSAVEL));
        boleto.setIndice((String) paramResposta.get(ADE_INDICE));

        if (!TextHelper.isNull(paramResposta.get(SER_DATA_IDT))) {
            try {
                boleto.setDataIdentidade(factory.createBoletoDataIdentidade(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse((String) paramResposta.get(SER_DATA_IDT), LocaleHelper.getDatePattern()), false)));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA INICIAL PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }
        boleto.setIdentidade((String) paramResposta.get(SER_NRO_IDT));
        boleto.setUfIdentidade(factory.createBoletoUfIdentidade((String) paramResposta.get(SER_UF_IDT)));
        boleto.setEmissorIdentidade(factory.createBoletoEmissorIdentidade((String) paramResposta.get(SER_EMISSOR_IDT)));
        boleto.setCidadeNascimento(factory.createBoletoCidadeNascimento((String) paramResposta.get(SER_CID_NASC)));
        boleto.setNacionalidade(factory.createBoletoNacionalidade((String) paramResposta.get(SER_NACIONALIDADE)));
        boleto.setSexo((String) paramResposta.get(SER_SEXO));
        boleto.setEstadoCivil((String) paramResposta.get(SER_EST_CIVIL));
        boleto.setCelular(factory.createBoletoCelular((String) paramResposta.get(SER_CELULAR)));

        if (!TextHelper.isNull(paramResposta.get(RSE_SALARIO)) && !(paramResposta.get(RSE_SALARIO) instanceof BigDecimal)) {
            try {
                boleto.setSalario(factory.createBoletoSalario(Double.parseDouble(NumberHelper.reformat(paramResposta.get(RSE_SALARIO).toString(), NumberHelper.getLang(), "en", 2, 8))));
            } catch (final NumberFormatException | ParseException e) {
                LOG.warn("ERRO AO RECUPERAR VALOR DO RSE_SALARIO DO SERVIDOR PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        } else if (!TextHelper.isNull(paramResposta.get(RSE_SALARIO))) {
            boleto.setSalario(factory.createBoletoSalario(((BigDecimal) paramResposta.get(RSE_SALARIO)).doubleValue()));
        } else {
            boleto.setSalario(factory.createBoletoSalario(Double.NaN));
        }
        if (!TextHelper.isNull(paramResposta.get(RSE_DATA_SAIDA))) {
            try {
                boleto.setDataSaida(factory.createBoletoDataSaida(BaseAssembler.toXMLGregorianCalendar(DateHelper.parse((String) paramResposta.get(RSE_DATA_SAIDA), LocaleHelper.getDatePattern()), false)));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA SAIDA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }
        boleto.setBanco(factory.createBoletoBanco((String) paramResposta.get(RSE_BANCO)));
        boleto.setAgencia(factory.createBoletoAgencia((String) paramResposta.get(RSE_AGENCIA)));
        boleto.setConta(factory.createBoletoConta((String) paramResposta.get(RSE_CONTA)));
        boleto.setCargoCodigo(factory.createBoletoCargoCodigo((String) paramResposta.get(CARGO_CODIGO)));
        boleto.setCargoDescricao(factory.createBoletoCargoDescricao((String) paramResposta.get(CARGO_DESCRICAO)));
        boleto.setHabitacaoCodigo(factory.createBoletoHabitacaoCodigo((String) paramResposta.get(HABITACAO_CODIGO)));
        boleto.setHabitacaoDescricao(factory.createBoletoHabitacaoDescricao((String) paramResposta.get(HABITACAO_DESCRICAO)));
        boleto.setEscolaridadeCodigo(factory.createBoletoEscolaridadeCodigo((String) paramResposta.get(ESCOLARIDADE_CODIGO)));
        boleto.setEscolaridadeDescricao(factory.createBoletoEscolaridadeDescricao((String) paramResposta.get(ESCOLARIDADE_DESCRICAO)));
        if (!TextHelper.isNull(paramResposta.get(SER_QTD_FILHOS))) {
            boleto.setQtdFilhos(factory.createBoletoQtdFilhos(Integer.parseInt(paramResposta.get(SER_QTD_FILHOS).toString())));
        }

        return boleto;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v7.Boleto toBoletoV7(Map<CamposAPI, Object> paramResposta, AcessoSistema responsavel) {
        final com.zetra.econsig.webservice.soap.operacional.v7.Boleto boleto = new com.zetra.econsig.webservice.soap.operacional.v7.Boleto();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V6
            BeanUtils.copyProperties(boleto, toBoletoV6(paramResposta, responsavel));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return boleto;
    }

    public static com.zetra.econsig.webservice.soap.operacional.v8.Boleto toBoletoV8(Map<CamposAPI, Object> paramResposta, AcessoSistema responsavel) {
        final com.zetra.econsig.webservice.soap.operacional.v8.Boleto boleto = new com.zetra.econsig.webservice.soap.operacional.v8.Boleto();
        try {
            // copyProperties(to, from) : copias os valores do model em que a versão atual se baseia, no caso, V6
            BeanUtils.copyProperties(boleto, toBoletoV6(paramResposta, responsavel));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return boleto;
    }
}