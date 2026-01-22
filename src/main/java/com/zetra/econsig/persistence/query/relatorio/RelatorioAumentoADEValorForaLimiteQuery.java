package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioAumentoADEValorForaLimiteQuery</p>
 * <p> Description: Relatório de aumento de valor de ade acima do limite estipulado
 * pelo parâmetro de serviço 135.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioAumentoADEValorForaLimiteQuery extends ReportHQuery {

    public Date periodoIni;
    public Date periodoFim;
    public String csaCodigo;
    public List<String> orgCodigos;
    public List<String> svcCodigo;
    public int filtro;

    public RelatorioAumentoADEValorForaLimiteQuery(int filtro) {
        this.filtro = filtro;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        if (!TextHelper.isNull(criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO))) {
            try {
                periodoIni = DateHelper.parse((String) criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO), "yyyy-MM-dd");
            } catch (ParseException ex) {
                periodoIni = null;
            }
        }
        if (!TextHelper.isNull(criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_FIM))) {
            try {
                periodoFim  = DateHelper.parse((String) criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_FIM), "yyyy-MM-dd HH:mm:ss");
            } catch (ParseException ex) {
                periodoFim = null;
            }
        }
        csaCodigo = (String) criterio.getAttribute(Columns.CNV_CSA_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.CNV_ORG_CODIGO);
        svcCodigo = (List<String>) criterio.getAttribute(Columns.CNV_SVC_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String rotuloIndeterminado = ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", (AcessoSistema) null);

        // OBS: A ordenação dos registros é feita pela classe RelatorioAumentoADEValorForaLimite
        // através da implementação de um Comparator, que obtém os campos pela posição fixa
        // dos mesmos, portanto cuidado ao alterar a lista de campos a serem retornados.

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT DISTINCT ");
        corpoBuilder.append("CAST(ade.adeNumero AS string) AS ade_numero, ");
        corpoBuilder.append("ade.adeData AS ade_data, ");
        corpoBuilder.append("COALESCE(CAST(ade.adePrazo AS string), '").append(rotuloIndeterminado).append("') AS ade_prazo, ");
        corpoBuilder.append("ade.adeVlr AS VLR_NOVO, ");
        corpoBuilder.append("COALESCE(prd.prdVlrPrevisto, ade.adeVlrRef) AS VLR_ANTIGO, ");
        corpoBuilder.append("(ade.adeVlr - COALESCE(prd.prdVlrPrevisto, ade.adeVlrRef)) / COALESCE(prd.prdVlrPrevisto, ade.adeVlrRef) * 100 AS percentual_aumento, ");
        corpoBuilder.append("sad.sadDescricao AS sad_descricao, ");
        corpoBuilder.append("CONCAT(rse.rseMatricula, ' - ', ser.serNome) AS servidor, ");
        corpoBuilder.append("ser.serCpf AS ser_cpf, ");
        corpoBuilder.append("srs.srsDescricao AS srs_descricao, ");
        corpoBuilder.append("csa.csaCodigo AS csa_codigo, ");
        corpoBuilder.append("CONCAT(csa.csaIdentificador, ' - ', csa.csaNome) AS consignataria, ");
        corpoBuilder.append("cnv.cnvCodVerba AS cnv_cod_verba, ");
        corpoBuilder.append("CONCAT(COALESCE(cnv.cnvCodVerba, svc.svcIdentificador), ' - ', svc.svcDescricao) AS verba, ");
        corpoBuilder.append((filtro == 1) ? "to_decimal(pse.pseVlr, 13, 2) AS limite " : "to_decimal(psc.pscVlr, 13, 2) AS limite ");

        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append("INNER JOIN ade.ocorrenciaAutorizacaoSet oca ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.orgao org ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN rse.statusRegistroServidor srs ");
        corpoBuilder.append((filtro == 1) ? "INNER JOIN svc.paramSvcConsignanteSet pse " : "INNER JOIN svc.paramSvcConsignatariaSet psc ");
        corpoBuilder.append("LEFT OUTER JOIN ade.parcelaDescontoSet prd ");

        corpoBuilder.append("WHERE oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("'");
        corpoBuilder.append(" AND oca.ocaData BETWEEN :periodoIni AND :periodoFim ");
        corpoBuilder.append(" AND (prd.prdDataDesconto = (SELECT MAX(hie.hiePeriodo) FROM HistoricoExportacao hie ");
        corpoBuilder.append(" WHERE hie.orgao.orgCodigo = org.orgCodigo AND hie.hieDataFim <= :periodoIni) OR prd.prdDataDesconto IS NULL) ");
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (svcCodigo != null && svcCodigo.size() > 0) {
            corpoBuilder.append(" AND cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (filtro == 1) {
            corpoBuilder.append(" AND pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE).append("'");
            corpoBuilder.append(" AND (ade.adeVlr - COALESCE(prd.prdVlrPrevisto, ade.adeVlrRef)) / COALESCE(prd.prdVlrPrevisto, ade.adeVlrRef) > TO_DECIMAL(COALESCE(NULLIF(TRIM(pse.pseVlr), ''), '0'), 13, 2) / 100.00 ");
            corpoBuilder.append(" AND NULLIF(TRIM(pse.pseVlr), '') IS NOT NULL ");
            corpoBuilder.append(" AND NOT EXISTS(SELECT 1 FROM ParamSvcConsignataria psc WHERE psc.servico.svcCodigo = cnv.servico.svcCodigo ");
            corpoBuilder.append(" AND psc.consignataria.csaCodigo = cnv.consignataria.csaCodigo ");
            corpoBuilder.append(" AND psc.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE).append("'");
            corpoBuilder.append(" AND NULLIF(TRIM(psc.pscVlr), '') IS NOT NULL) ");
        } else {
            corpoBuilder.append(" AND psc.consignataria.csaCodigo = cnv.consignataria.csaCodigo ");
            corpoBuilder.append(" AND psc.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE).append("'");
            corpoBuilder.append(" AND (ade.adeVlr - COALESCE(prd.prdVlrPrevisto, ade.adeVlrRef)) / COALESCE(prd.prdVlrPrevisto, ade.adeVlrRef) > TO_DECIMAL(COALESCE(NULLIF(TRIM(psc.pscVlr), ''), '0'), 13, 2) / 100.00 ");
            corpoBuilder.append(" AND NULLIF(TRIM(psc.pscVlr), '') IS NOT NULL ");
        }

        corpoBuilder.append(" ORDER BY 11, 13, 8");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("periodoIni", periodoIni, query);
        defineValorClausulaNomeada("periodoFim", periodoFim, query);
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (svcCodigo != null && svcCodigo.size() > 0) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.ADE_NUMERO,
                Columns.ADE_DATA,
                Columns.ADE_PRAZO,
                "VLR_NOVO",
                "VLR_ANTIGO",
                "percentual_aumento",
                Columns.SAD_DESCRICAO,
                "servidor",
                Columns.SER_CPF,
                Columns.SRS_DESCRICAO,
                Columns.CSA_CODIGO,
                "consignataria",
                Columns.CNV_COD_VERBA,
                "verba"
        };
    }
}
