package com.zetra.econsig.webservice.soap.folha.assembler;

import static com.zetra.econsig.webservice.CamposAPI.MOVIMENTO_FINANCEIRO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.MovimentoFinanceiro;

/**
 * <p>Title: MovimentoFinanceiroAssembler</p>
 * <p>Description: Assembler para MovimentoFinanceiro.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class MovimentoFinanceiroAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MovimentoFinanceiroAssembler.class);

    private MovimentoFinanceiroAssembler() {
    }

    public static MovimentoFinanceiro toMovimentoFinanceiroV1(Map<CamposAPI, Object> paramResposta) {
        final MovimentoFinanceiro movimentoFinanceiro = new MovimentoFinanceiro();

        final TransferObject movimento = (TransferObject) paramResposta.get(MOVIMENTO_FINANCEIRO);
        final Date periodo = (Date) movimento.getAttribute(Columns.PEX_PERIODO);
        final String periodoFormatado = DateHelper.toPeriodString(periodo);

        movimentoFinanceiro.setPeriodo(periodoFormatado);
        movimentoFinanceiro.setServidor((String) movimento.getAttribute(Columns.SER_NOME));
        movimentoFinanceiro.setCpf((String) movimento.getAttribute(Columns.SER_CPF));
        movimentoFinanceiro.setMatricula((String) movimento.getAttribute(Columns.RSE_MATRICULA));
        movimentoFinanceiro.setEstabelecimentoCodigo((String) movimento.getAttribute(Columns.EST_IDENTIFICADOR));
        movimentoFinanceiro.setEstabelecimento((String) movimento.getAttribute(Columns.EST_NOME));
        movimentoFinanceiro.setOrgaoCodigo((String) movimento.getAttribute(Columns.ORG_IDENTIFICADOR));
        movimentoFinanceiro.setOrgao((String) movimento.getAttribute(Columns.ORG_NOME));
        movimentoFinanceiro.setConsignatariaCodigo((String) movimento.getAttribute(Columns.CSA_IDENTIFICADOR));
        movimentoFinanceiro.setConsignataria((String) movimento.getAttribute(Columns.CSA_NOME));
        movimentoFinanceiro.setServicoCodigo((String) movimento.getAttribute(Columns.SVC_IDENTIFICADOR));
        movimentoFinanceiro.setServico((String) movimento.getAttribute(Columns.SVC_DESCRICAO));
        movimentoFinanceiro.setCodVerba((String) movimento.getAttribute(Columns.CNV_COD_VERBA));
        try {
            movimentoFinanceiro.setDataReserva(toXMLGregorianCalendar((Date) movimento.getAttribute(Columns.ADE_DATA), true));
        } catch (final DatatypeConfigurationException e) {
            LOG.warn(e.getMessage(), e);
        }
        try {
            movimentoFinanceiro.setDataInicial(toXMLGregorianCalendar((Date) movimento.getAttribute(Columns.ADE_ANO_MES_INI), false));
        } catch (final DatatypeConfigurationException e) {
            LOG.warn(e.getMessage(), e);
        }
        try {
            movimentoFinanceiro.setDataFinal(toXMLGregorianCalendar((Date) movimento.getAttribute(Columns.ADE_ANO_MES_FIM), false));
        } catch (final DatatypeConfigurationException e) {
            LOG.warn(e.getMessage(), e);
        }
        movimentoFinanceiro.setValorParcela(movimento.getAttribute(Columns.ADE_VLR) != null ? ((BigDecimal) movimento.getAttribute(Columns.ADE_VLR)).doubleValue() : -1);
        movimentoFinanceiro.setPrazo(movimento.getAttribute(Columns.ADE_PRAZO) != null ? (Integer) movimento.getAttribute(Columns.ADE_PRAZO) : -1);
        movimentoFinanceiro.setPagas(movimento.getAttribute(Columns.ADE_PRD_PAGAS) != null ? (Integer) movimento.getAttribute(Columns.ADE_PRD_PAGAS) : 0);
        movimentoFinanceiro.setAdeNumero(movimento.getAttribute(Columns.ADE_NUMERO) != null ? Long.parseLong(movimento.getAttribute(Columns.ADE_NUMERO).toString()) : null);
        movimentoFinanceiro.setIndice((String) movimento.getAttribute(Columns.ADE_INDICE));
        movimentoFinanceiro.setOperacao(movimento.getAttribute(Columns.ARM_SITUACAO).toString());

        return movimentoFinanceiro;
    }
}