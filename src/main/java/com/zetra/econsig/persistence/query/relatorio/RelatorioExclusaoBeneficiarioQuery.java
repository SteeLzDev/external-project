package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioExclusaoBeneficiarioQuery</p>
 * <p>Description: Query usada para gerar relatório de exclusão de beneficiários</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class RelatorioExclusaoBeneficiarioQuery extends ReportHQuery {
    public String dataIni;

    public String dataFim;

    public String operadora;

    public String beneficio;

    public String motivoOperacao;

    public String formatoRelatorio;

    @Override
    public void setCriterios(TransferObject criterio) {
        dataIni = (String) criterio.getAttribute("dataIni");
        dataFim = (String) criterio.getAttribute("dataFim");
        operadora = (String) criterio.getAttribute("operadora");
        beneficio = (String) criterio.getAttribute("beneficio");
        motivoOperacao = (String) criterio.getAttribute("motivoOperacao");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT csa.csaNome as csa_nome, ben.benDescricao as ben_descricao, rse.rseMatricula as rse_matricula, ");
        corpoBuilder.append(" ser.serNome as ser_nome, ocb.ocbData as ocb_data, ");
        corpoBuilder.append(" bfc.bfcNome as bfc_nome, ");
        corpoBuilder.append(" cbe.cbeNumero as cbe_numero ");
        corpoBuilder.append(" from OcorrenciaCttBeneficio ocb ");
        corpoBuilder.append(" inner join ocb.contratoBeneficio cbe ");
        corpoBuilder.append(" inner join cbe.beneficio ben ");
        corpoBuilder.append(" inner join cbe.autDescontoSet ade ");
        corpoBuilder.append(" inner join ade.registroServidor rse ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" inner join ade.tipoLancamento tla ");
        corpoBuilder.append(" inner join ben.consignataria csa ");
        corpoBuilder.append(" inner join cbe.beneficiario bfc ");

        corpoBuilder.append(" WHERE ocb.ocbData between :dataIni and :dataFim ");
        corpoBuilder.append(" AND tla.tlaCodigoPai IS NULL AND ocb.tipoOcorrencia.tocCodigo = :tipoOcorrencia ");
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo = :sadCodigo ");

        if(motivoOperacao != null && !motivoOperacao.isEmpty()) {
            corpoBuilder.append(" AND "
                    + "( EXISTS(SELECT 1 from AutDesconto adeNovo "
                        + "inner join adeNovo.ocorrenciaAutorizacaoSet ocaNovo "
                        + "inner join ocaNovo.tipoOcorrencia tocNovo "
                        + "where ade.adeCodigo = adeNovo.adeCodigo "
                        + "AND ocaNovo.tipoMotivoOperacao.tmoCodigo = :tmoCodigoAutorizacao "
                        + "AND tocNovo.tocCodigo = :tipoOcorrenciaOca)"
                    + " OR "
                    + "( EXISTS(SELECT 1 from ContratoBeneficio cbeNovo "
                        + "inner join cbeNovo.ocorrenciaCttBeneficioSet ocoNovo "
                        + "WHERE cbeNovo.cbeCodigo = cbe.cbeCodigo "
                        + "AND ocoNovo.tipoMotivoOperacao.tmoCodigo = :tmoCodigoContrato )))");
        }

        if (operadora != null && !operadora.isEmpty()) {
            corpoBuilder.append("AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", operadora));
        }

        if (beneficio != null && !beneficio.isEmpty()) {
            corpoBuilder.append("AND ben.benCodigo ").append(criaClausulaNomeada("benCodigo", beneficio));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        defineValorClausulaNomeada("tipoOcorrencia", CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO, query);
        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_LIQUIDADA, query);

        if (!TextHelper.isNull(operadora)) {
            defineValorClausulaNomeada("csaCodigo", operadora, query);
        }

        if (!TextHelper.isNull(beneficio)) {
            defineValorClausulaNomeada("benCodigo", beneficio, query);
        }

        if (!TextHelper.isNull(motivoOperacao)) {
            defineValorClausulaNomeada("tmoCodigoAutorizacao", motivoOperacao, query);
            defineValorClausulaNomeada("tipoOcorrenciaOca", CodedValues.TOC_TARIF_LIQUIDACAO, query);
            defineValorClausulaNomeada("tmoCodigoContrato", motivoOperacao, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_NOME,
                Columns.BEN_DESCRICAO,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.OCB_DATA,
                Columns.BFC_NOME,
                Columns.CBE_NUMERO
        };
    }

}
