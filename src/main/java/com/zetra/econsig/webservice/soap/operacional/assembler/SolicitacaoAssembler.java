package com.zetra.econsig.webservice.soap.operacional.assembler;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.DATA_RESERVA;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_NOME;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.Solicitacao;

/**
 * <p>Title: SolicitacaoAssembler</p>
 * <p>Description: Assembler para Solicitacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class SolicitacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitacaoAssembler.class);

    private SolicitacaoAssembler() {
    }

    public static Solicitacao toSolicitacaoV1(Map<CamposAPI, Object> paramResposta) {
        final Solicitacao solicitacao = new Solicitacao();

        solicitacao.setAdeNumero((Long) paramResposta.get(ADE_NUMERO));
        try {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateHelper.parse((String) paramResposta.get(DATA_RESERVA), LocaleHelper.getDateTimePattern()));
            solicitacao.setDataReserva(BaseAssembler.toXMLGregorianCalendar(calendar.getTime(), true));
        } catch (final ParseException | DatatypeConfigurationException e) {
            LOG.warn("ERRO AO RECUPERAR DATA FINAL PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
        }
        solicitacao.setServidor((String) paramResposta.get(SERVIDOR));
        solicitacao.setTelefone((String) paramResposta.get(SER_TEL));
        solicitacao.setCpf((String) paramResposta.get(SER_CPF));
        solicitacao.setMatricula((String) paramResposta.get(RSE_MATRICULA));
        try {
            solicitacao.setValorParcela(Double.parseDouble(NumberHelper.reformat(paramResposta.get(VALOR_PARCELA).toString(), NumberHelper.getLang(), "en", 2, 8)));
            solicitacao.setValorLiberado(Double.parseDouble(NumberHelper.reformat(paramResposta.get(VALOR_LIBERADO).toString(), NumberHelper.getLang(), "en", 2, 8)));
            solicitacao.setTaxaJuros(Double.parseDouble(NumberHelper.reformat(paramResposta.get(TAXA_JUROS).toString(), NumberHelper.getLang(), "en", 2, 8)));
        } catch (final NumberFormatException | ParseException e) {
            LOG.warn("ERRO AO RECUPERAR VALOR DA PARCELA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
        }
        solicitacao.setPrazo(Integer.parseInt(paramResposta.get(PRAZO).toString()));
        solicitacao.setServico((String) paramResposta.get(SERVICO));
        solicitacao.setServicoCodigo((String) paramResposta.get(SVC_IDENTIFICADOR));
        solicitacao.setCodVerba((String) paramResposta.get(CNV_COD_VERBA));
        solicitacao.setOrgaoCodigo((String) paramResposta.get(ORG_IDENTIFICADOR));
        solicitacao.setOrgao((String) paramResposta.get(ORG_NOME));
        solicitacao.setEstabelecimentoCodigo((String) paramResposta.get(EST_IDENTIFICADOR));
        solicitacao.setEstabelecimento((String) paramResposta.get(EST_NOME));

        return solicitacao;
    }
}