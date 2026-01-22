package com.zetra.econsig.webservice.soap.servidor.assembler;

import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_NACIONALIDADE;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CART_PROF;
import static com.zetra.econsig.webservice.CamposAPI.SER_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_COMPL;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_EST_CIVIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_MAE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_PAI;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_PIS;
import static com.zetra.econsig.webservice.CamposAPI.SER_SEXO;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_REGISTRO;

import java.text.ParseException;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.DadosServidor;
import com.zetra.econsig.webservice.soap.servidor.v1.ObjectFactory;

/**
 * <p>Title: DadosServidorAssembler</p>
 * <p>Description: Assembler para DadosServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class DadosServidorAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DadosServidorAssembler.class);

    private DadosServidorAssembler() {
    }

    public static DadosServidor toDadosServidorV1(Map<CamposAPI, Object> paramResposta) {
        final ObjectFactory factory = new ObjectFactory();
        final DadosServidor dadosServidor = new DadosServidor();

        dadosServidor.setServidor((String) paramResposta.get(SERVIDOR));
        dadosServidor.setMatricula((String) paramResposta.get(RSE_MATRICULA));
        dadosServidor.setCpf((String) paramResposta.get(SER_CPF));
        dadosServidor.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
        dadosServidor.setEstabelecimento((String) paramResposta.get(ESTABELECIMENTO));
        dadosServidor.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
        dadosServidor.setOrgao((String) paramResposta.get(ORGAO));
        dadosServidor.setSexo((String) paramResposta.get(SER_SEXO));
        if (!TextHelper.isNull(paramResposta.get(DATA_NASCIMENTO))) {
            try {
                dadosServidor.setDataNascimento(toXMLGregorianCalendar(DateHelper.parse((String) paramResposta.get(DATA_NASCIMENTO), LocaleHelper.getDatePattern()), false));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DE NASCIMENTO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }
        dadosServidor.setEstadoCivil((String) paramResposta.get(SER_EST_CIVIL));
        dadosServidor.setIdentidade((String) paramResposta.get(SER_NRO_IDT));
        dadosServidor.setPai((String) paramResposta.get(SER_NOME_PAI));
        dadosServidor.setMae((String) paramResposta.get(SER_NOME_MAE));
        dadosServidor.setEndereco((String) paramResposta.get(SER_END));
        if (paramResposta.get(SER_NRO) != null) {
            dadosServidor.setNumero((String) paramResposta.get(SER_NRO));
        } else {
            dadosServidor.setNumero("-1");
        }

        dadosServidor.setComplemento((String) paramResposta.get(SER_COMPL));
        dadosServidor.setBairro((String) paramResposta.get(SER_BAIRRO));
        dadosServidor.setCidade((String) paramResposta.get(SER_CIDADE));
        dadosServidor.setUf((String) paramResposta.get(SER_UF));
        dadosServidor.setCep((String) paramResposta.get(SER_CEP));
        dadosServidor.setTelefone((String) paramResposta.get(SER_TEL));
        dadosServidor.setCelular((String) paramResposta.get(SER_CELULAR));
        if (!TextHelper.isNull(paramResposta.get(RSE_DATA_ADMISSAO))) {
            try {
                dadosServidor.setDataAdmissao(toXMLGregorianCalendar(DateHelper.parse((String) paramResposta.get(RSE_DATA_ADMISSAO), LocaleHelper.getDatePattern()), false));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DE ADMISSAO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }
        if (paramResposta.get(RSE_PRAZO) != null) {
            dadosServidor.setPrazoServidor(Integer.valueOf(paramResposta.get(RSE_PRAZO).toString()));
        } else {
            dadosServidor.setPrazoServidor(-1);
        }
        dadosServidor.setNacionalidade((String) paramResposta.get(SER_NACIONALIDADE));
        dadosServidor.setCarteiraTrabalho((String) paramResposta.get(SER_CART_PROF));
        dadosServidor.setPis((String) paramResposta.get(SER_PIS));
        dadosServidor.setCategoria((String) paramResposta.get(RSE_TIPO));
        dadosServidor.setEmail(factory.createDadosServidorEmail((String) paramResposta.get(SER_EMAIL)));

        return dadosServidor;
    }

    public static com.zetra.econsig.webservice.soap.servidor.v3.DadosServidor toDadosServidorV3(Map<CamposAPI, Object> paramResposta) {
        final com.zetra.econsig.webservice.soap.servidor.v3.ObjectFactory factory = new com.zetra.econsig.webservice.soap.servidor.v3.ObjectFactory();
        final com.zetra.econsig.webservice.soap.servidor.v3.DadosServidor dadosServidor = new com.zetra.econsig.webservice.soap.servidor.v3.DadosServidor();

        dadosServidor.setServidor((String) paramResposta.get(SERVIDOR));
        dadosServidor.setMatricula((String) paramResposta.get(RSE_MATRICULA));
        dadosServidor.setCpf((String) paramResposta.get(SER_CPF));
        dadosServidor.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
        dadosServidor.setEstabelecimento((String) paramResposta.get(ESTABELECIMENTO));
        dadosServidor.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
        dadosServidor.setOrgao((String) paramResposta.get(ORGAO));
        dadosServidor.setSexo((String) paramResposta.get(SER_SEXO));
        if (!TextHelper.isNull(paramResposta.get(DATA_NASCIMENTO))) {
            try {
                dadosServidor.setDataNascimento(toXMLGregorianCalendar(DateHelper.parse((String) paramResposta.get(DATA_NASCIMENTO), LocaleHelper.getDatePattern()), false));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DE NASCIMENTO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }
        dadosServidor.setEstadoCivil((String) paramResposta.get(SER_EST_CIVIL));
        dadosServidor.setIdentidade((String) paramResposta.get(SER_NRO_IDT));
        dadosServidor.setPai((String) paramResposta.get(SER_NOME_PAI));
        dadosServidor.setMae((String) paramResposta.get(SER_NOME_MAE));
        dadosServidor.setEndereco((String) paramResposta.get(SER_END));
        if (paramResposta.get(SER_NRO) != null) {
            dadosServidor.setNumero((String) paramResposta.get(SER_NRO));
        } else {
            dadosServidor.setNumero("-1");
        }

        dadosServidor.setComplemento((String) paramResposta.get(SER_COMPL));
        dadosServidor.setBairro((String) paramResposta.get(SER_BAIRRO));
        dadosServidor.setCidade((String) paramResposta.get(SER_CIDADE));
        dadosServidor.setUf((String) paramResposta.get(SER_UF));
        dadosServidor.setCep((String) paramResposta.get(SER_CEP));
        dadosServidor.setTelefone((String) paramResposta.get(SER_TEL));
        dadosServidor.setCelular((String) paramResposta.get(SER_CELULAR));
        if (!TextHelper.isNull(paramResposta.get(RSE_DATA_ADMISSAO))) {
            try {
                dadosServidor.setDataAdmissao(toXMLGregorianCalendar(DateHelper.parse((String) paramResposta.get(RSE_DATA_ADMISSAO), LocaleHelper.getDatePattern()), false));
            } catch (final ParseException | DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA DE ADMISSAO PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }
        if (paramResposta.get(RSE_PRAZO) != null) {
            dadosServidor.setPrazoServidor(Integer.parseInt(paramResposta.get(RSE_PRAZO).toString()));
        } else {
            dadosServidor.setPrazoServidor(-1);
        }
        dadosServidor.setNacionalidade((String) paramResposta.get(SER_NACIONALIDADE));
        dadosServidor.setCarteiraTrabalho((String) paramResposta.get(SER_CART_PROF));
        dadosServidor.setPis((String) paramResposta.get(SER_PIS));
        dadosServidor.setCategoria((String) paramResposta.get(RSE_TIPO));
        dadosServidor.setEmail(factory.createDadosServidorEmail((String) paramResposta.get(SER_EMAIL)));
        dadosServidor.setTipoRegistroServidorCodigo((String) paramResposta.get(TIPO_REGISTRO));

        return dadosServidor;
    }
}