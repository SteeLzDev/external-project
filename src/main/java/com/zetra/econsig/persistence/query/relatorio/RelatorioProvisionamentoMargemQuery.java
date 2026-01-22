package com.zetra.econsig.persistence.query.relatorio;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioProvisionamentoMargemQuery</p>
 * <p>Description: Consulta do relatório provisionamento de margem.</p>
 * <p>Copyright: Copyright (c) 2002-2014/p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioProvisionamentoMargemQuery extends ReportHNativeQuery {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioProvisionamentoMargemQuery.class);

    private String dataIni;
    private String dataFim;
    private List<String> orgCodigos;
    private List<String> servicos;
    private String csaCodigo;
    private String corCodigo;

    private boolean adeNuncaExistiuLancamento;
    private boolean adeAptasPortabilidade;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        dataIni = (String) criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO);
        dataFim = (String) criterio.getAttribute(ReportManager.PARAM_NAME_PERIODO_FIM);

        servicos = (List<String>) criterio.getAttribute(Columns.SVC_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        corCodigo = (String) criterio.getAttribute(Columns.COR_CODIGO);
        adeNuncaExistiuLancamento = (boolean) criterio.getAttribute("adeNuncaExistiuLancamento");
        adeAptasPortabilidade = (boolean) criterio.getAttribute("adeAptasPortabilidade");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder queryBuffer = new StringBuilder();
        queryBuffer.append("SELECT");
        if (responsavel.isCseSupOrg()) {
            queryBuffer.append(" csa.csa_nome as CAMPO_GRUPO,");
        } else if (responsavel.isCsaCor()) {
            queryBuffer.append(" COALESCE(cor.cor_nome, '"+ApplicationResourcesHelper.getMessage("rotulo.campo.nenhum.simples", (AcessoSistema) null)+"') as CAMPO_GRUPO,");
        } else {
            queryBuffer.append(" '' as CAMPO_GRUPO,");
        }
        queryBuffer.append(" rse.rse_codigo AS RSE_CODIGO, ade.ade_codigo AS ADE_CODIGO,");
        queryBuffer.append(" rse.rse_matricula AS RSE_MATRICULA, ser.ser_nome AS SER_NOME, ser.ser_cpf AS SER_CPF,");
        queryBuffer.append(" srs.srs_descricao AS SRS_DESCRICAO,");
        queryBuffer.append(" rse.rse_tipo AS RSE_TIPO, org.org_nome as ORG_NOME, svc.svc_descricao AS SVC_DESCRICAO,");
        queryBuffer.append(" ade.ade_data AS ADE_DATA, ade.ade_vlr AS VLR_RESERVA,");
        queryBuffer.append(" COALESCE(SUM(ade_lanc.ade_vlr), 0) AS VLR_LANCAMENTOS");

        queryBuffer.append(" FROM tb_aut_desconto ade");
        queryBuffer.append(" INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo)");
        queryBuffer.append(" INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo)");
        queryBuffer.append(" INNER JOIN tb_status_registro_servidor srs ON (srs.srs_codigo = rse.srs_codigo)");
        queryBuffer.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo)");
        queryBuffer.append(" INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo)");
        queryBuffer.append(" INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo)");
        queryBuffer.append(" INNER JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo)");
        queryBuffer.append(" INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo)");
        queryBuffer.append(" INNER JOIN tb_relacionamento_servico rel_svc ON (rel_svc.svc_codigo_origem = svc.svc_codigo AND rel_svc.tnt_codigo = '" + CodedValues.TNT_CARTAO + "')");
        queryBuffer.append(" INNER JOIN tb_servico svc_lanc ON (rel_svc.svc_codigo_destino = svc_lanc.svc_codigo)");
        queryBuffer.append(" INNER JOIN tb_convenio cnv_lanc ON (cnv_lanc.svc_codigo = svc_lanc.svc_codigo AND cnv_lanc.org_codigo = org.org_codigo AND cnv_lanc.csa_codigo = csa.csa_codigo)");
        queryBuffer.append(" INNER JOIN tb_verba_convenio vco_lanc ON (vco_lanc.cnv_codigo = cnv_lanc.cnv_codigo)");

        queryBuffer.append(" LEFT OUTER JOIN tb_aut_desconto ade_lanc ON (ade_lanc.ade_codigo <> ade.ade_codigo");
        queryBuffer.append(" AND vco_lanc.vco_codigo = ade_lanc.vco_codigo");
        queryBuffer.append(" AND ade_lanc.rse_codigo = rse.rse_codigo");
        queryBuffer.append(" AND ade_lanc.sad_codigo = '" + CodedValues.SAD_DEFERIDA + "')");

        queryBuffer.append(" LEFT OUTER JOIN tb_correspondente cor ON (ade.cor_codigo = cor.cor_codigo)");
        queryBuffer.append(" LEFT OUTER JOIN tb_correspondente cor_lanc ON (ade_lanc.cor_codigo = cor_lanc.cor_codigo)");
        if (adeAptasPortabilidade) {
            queryBuffer.append(" LEFT OUTER JOIN tb_param_svc_consignante pse332 ON (svc.svc_codigo = pse332.svc_codigo AND pse332.tps_codigo = '" + CodedValues.TPS_SOMENTE_CONTRATOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE + "') ");
            queryBuffer.append(" LEFT OUTER JOIN tb_param_svc_consignante pse331 ON (svc.svc_codigo = pse331.svc_codigo AND pse332.tps_codigo = '" + CodedValues.TPS_QUANTIDADE_PERIODOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE + "') ");
            queryBuffer.append(" LEFT OUTER JOIN ( ");
            queryBuffer.append(" SELECT ade.ade_codigo, MAX(adeDestino.ade_ano_mes_ini_ref) as ade_ano_mes_ini_ref ");
            queryBuffer.append(" FROM tb_aut_desconto ade ");
            queryBuffer.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            queryBuffer.append(" INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            queryBuffer.append(" INNER JOIN tb_relacionamento_autorizacao radOrigem ON (radOrigem.ADE_CODIGO_ORIGEM = ade.ade_codigo and radOrigem.TNT_CODIGO = '" + CodedValues.TNT_CARTAO + "') ");
            queryBuffer.append(" INNER JOIN tb_aut_desconto adeDestino ON (radOrigem.ADE_CODIGO_DESTINO = adeDestino.ade_codigo) ");
            queryBuffer.append(" LEFT OUTER JOIN tb_correspondente cor ON (ade.cor_codigo = cor.cor_codigo)");
            queryBuffer.append(" WHERE 1=1 ");
            if (!TextHelper.isNull(csaCodigo)) {
                queryBuffer.append(" AND cnv.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            }
            if (!TextHelper.isNull(corCodigo)) {
                queryBuffer.append(" AND cor.cor_codigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
            }
            queryBuffer.append(" group by ade.ade_codigo ");
            queryBuffer.append(" ) X ON (X.ade_codigo = ade.ade_codigo) ");
        }
        queryBuffer.append(" WHERE ade.sad_codigo = '" + CodedValues.SAD_DEFERIDA + "'");

        if (!TextHelper.isNull(dataIni) && !TextHelper.isNull(dataFim)) {
            queryBuffer.append(" AND ade.ade_data BETWEEN :dataIni AND :dataFim ");
        }
        if ((servicos != null) && !servicos.isEmpty()) {
            queryBuffer.append(" AND svc.svc_codigo ").append(criaClausulaNomeada("svcCodigos", servicos));
        }
        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            queryBuffer.append(" AND org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            queryBuffer.append(" AND csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(corCodigo)) {
            queryBuffer.append(" AND cor.cor_codigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
            queryBuffer.append(" AND (ade_lanc.ade_codigo IS NULL OR cor_lanc.cor_codigo = cor.cor_codigo)");
        }

        if (adeNuncaExistiuLancamento) {
            queryBuffer.append(" AND NOT EXISTS (SELECT 1 FROM tb_relacionamento_autorizacao tra where tra.ade_codigo_origem = ade.ade_codigo and tra.tnt_codigo = '" + CodedValues.TNT_CARTAO + "') ");
        }

        if (adeAptasPortabilidade) {
            queryBuffer.append(" AND (CASE WHEN pse332.PSE_VLR='1' THEN NOT EXISTS (SELECT 1 FROM tb_relacionamento_autorizacao tra where tra.ade_codigo_origem = ade.ade_codigo and tnt_codigo = '" + CodedValues.TNT_CARTAO + "') ELSE 1=1 END) ");
            queryBuffer.append(" AND (CASE WHEN X.ade_ano_mes_ini_ref IS NULL THEN 1000 ELSE month_diff(X.ade_ano_mes_ini_ref, :periodo) END >  ");
            queryBuffer.append("     CASE WHEN pse331.pse_vlr IS NOT NULL AND pse331.pse_vlr != '' AND pse331.pse_vlr > 0 THEN pse331.pse_vlr ELSE 0 END) ");
        }

        queryBuffer.append(" GROUP BY");
        String campoAgrupamento = null;
        if (responsavel.isCseSupOrg()) {
            campoAgrupamento = " csa.csa_nome";
        } else if (responsavel.isCsaCor()) {
            campoAgrupamento = " cor.cor_nome";
        }
        queryBuffer.append(campoAgrupamento != null ? campoAgrupamento + "," : "");
        queryBuffer.append(" rse.rse_codigo, ade.ade_codigo, rse.rse_matricula, ser.ser_nome, ser.ser_cpf,");
        queryBuffer.append(" srs.srs_descricao, rse.rse_tipo, org.org_nome, svc.svc_descricao, ade.ade_data, ade.ade_vlr");

        queryBuffer.append(" ORDER BY");
        queryBuffer.append((campoAgrupamento != null ? campoAgrupamento + "," : "") + " ser.ser_nome, ade.ade_data");

        final Query<Object[]> queryInst = instanciarQuery(session, queryBuffer.toString());

        if (!TextHelper.isNull(dataIni) && !TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), queryInst);
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), queryInst);
        }
        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, queryInst);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, queryInst);
        }
        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, queryInst);
        }
        if ((servicos != null) && !servicos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", servicos, queryInst);
        }
        if (adeAptasPortabilidade) {
            Date periodo = null;
            try {
                periodo = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);
                defineValorClausulaNomeada("periodo", periodo, queryInst);
            } catch (final PeriodoException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new HQueryException(ex);
            }
        }

        return queryInst;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "CAMPO_GRUPO",
                Columns.RSE_CODIGO,
                Columns.ADE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.SRS_DESCRICAO,
                Columns.RSE_TIPO,
                Columns.ORG_NOME,
                Columns.SVC_DESCRICAO,
                Columns.ADE_DATA,
                "VLR_RESERVA",
                "VLR_LANCAMENTOS"
        };
    }
}
