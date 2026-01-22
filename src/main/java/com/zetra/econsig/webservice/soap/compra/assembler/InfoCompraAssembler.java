package com.zetra.econsig.webservice.soap.compra.assembler;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_NOME;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.RAD_DATA;
import static com.zetra.econsig.webservice.CamposAPI.RAD_DATA_APR_SALDO;
import static com.zetra.econsig.webservice.CamposAPI.RAD_DATA_INF_SALDO;
import static com.zetra.econsig.webservice.CamposAPI.RAD_DATA_PGT_SALDO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SDV_VALOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME;
import static com.zetra.econsig.webservice.CamposAPI.STC_DESCRICAO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.InfoCompra;
import com.zetra.econsig.webservice.soap.compra.v1.ObjectFactory;

/**
 * <p>Title: AnEmptyAssembler</p>
 * <p>Description: Assembler para AnEmpty.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class InfoCompraAssembler extends BaseAssembler {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(InfoCompraAssembler.class);


    private InfoCompraAssembler() {
        //
    }

    public static InfoCompra toInfoCompraV1(Map<CamposAPI, Object> paramResposta) {
        final ObjectFactory factory = new ObjectFactory();
        final InfoCompra infoCompra = new InfoCompra();

        infoCompra.setAdeNumero(factory.createInfoCompraAdeNumero((Long) paramResposta.get(ADE_NUMERO)));
        infoCompra.setCodigoConsignataria(factory.createInfoCompraCodigoConsignataria((String) paramResposta.get(CSA_IDENTIFICADOR)));
        infoCompra.setNomeConsignataria(factory.createInfoCompraNomeConsignataria((String) paramResposta.get(CSA_NOME)));
        infoCompra.setNomeServidor(factory.createInfoCompraNomeServidor((String) paramResposta.get(SER_NOME)));
        infoCompra.setCpf(factory.createInfoCompraCpf((String) paramResposta.get(SER_CPF)));
        infoCompra.setMatricula(factory.createInfoCompraMatricula((String) paramResposta.get(RSE_MATRICULA)));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime((Timestamp) paramResposta.get(RAD_DATA));
        try {
            infoCompra.setDataCompra(factory.createInfoCompraDataCompra(toXMLGregorianCalendar(calendar.getTime(), true)));
        } catch (final DatatypeConfigurationException e) {
            LOG.warn("ERRO AO RECUPERAR DATA DA COMPRA DA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
        }

        if (paramResposta.get(RAD_DATA_INF_SALDO) != null) {
            calendar = Calendar.getInstance();
            calendar.setTime((Timestamp) paramResposta.get(RAD_DATA_INF_SALDO));
            try {
                infoCompra.setDataInfoSaldoDevedor(factory.createInfoCompraDataInfoSaldoDevedor(toXMLGregorianCalendar(calendar.getTime(), true)));
            } catch (final DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR VALOR DA DATA INFO SALDO DEVEDOR DA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        if (!TextHelper.isNull(paramResposta.get(SDV_VALOR))) {
            try {
                infoCompra.setValorSaldoDevedor(factory.createInfoCompraValorSaldoDevedor(((BigDecimal) paramResposta.get(SDV_VALOR)).doubleValue()));
            } catch (final NumberFormatException e) {
                LOG.warn("ERRO AO RECUPERAR VALOR DO SALDO DEVEDOR PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        if (paramResposta.get(RAD_DATA_APR_SALDO) != null) {
            calendar = Calendar.getInstance();
            calendar.setTime((Timestamp) paramResposta.get(RAD_DATA_APR_SALDO));
            try {
                infoCompra.setDataAprovacaoSaldoDevedor(factory.createInfoCompraDataAprovacaoSaldoDevedor(toXMLGregorianCalendar(calendar.getTime(), true)));
            } catch (final DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA APROVACAO SALDO DEVEDOR DA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        if (paramResposta.get(RAD_DATA_PGT_SALDO) != null) {
            calendar = Calendar.getInstance();
            calendar.setTime((Timestamp) paramResposta.get(RAD_DATA_PGT_SALDO));
            try {
                infoCompra.setDataPagamentoSaldoDevedor(factory.createInfoCompraDataPagamentoSaldoDevedor(toXMLGregorianCalendar(calendar.getTime(), true)));
            } catch (final DatatypeConfigurationException e) {
                LOG.warn("ERRO AO RECUPERAR DATA PAGAMENTO SALDO DEVEDOR DA PARA OPERACAO " + paramResposta.get(OPERACAO) + " VIA SOAP.");
            }
        }

        infoCompra.setSituacao(factory.createInfoCompraSituacao((String) paramResposta.get(STC_DESCRICAO)));

        return infoCompra;
    }
}