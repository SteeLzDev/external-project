package com.zetra.econsig.report.jasper.dinamico;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.values.CamposRelatorioSinteticoEnum;
import com.zetra.econsig.values.Columns;

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;


/**
 * <p>Title: RelatorioSinteticoDecisaoJudicialInfo</p>
 * <p>Description: Definição de colunas do Relatório Sintético de Decisões Judiciais</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoDecisaoJudicialInfo extends DynamicReportInfo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioSinteticoDecisaoJudicialInfo.class);

    public RelatorioSinteticoDecisaoJudicialInfo(Relatorio relatorio) {
        super(relatorio);
    }

    @Override
    protected DynamicReportBuilder reportDesign(AcessoSistema responsavel) {
        DynamicReportBuilder reportBuilder = new DynamicReportBuilder();
        reportBuilder.setWhenNoDataAllSectionNoDetail();

        try {
            List<String> camposRelatorio = (List<String>) criterioQuery.getAttribute("CAMPOS_RELATORIO");
            List<String> campos = new ArrayList<>();

            CamposRelatorioSinteticoEnum camposEnum = null;
            for (String key : camposRelatorio) {
                camposEnum = CamposRelatorioSinteticoEnum.recuperaCampo(key);
                if (camposEnum != null) {
                    campos.add(camposEnum.getCampo());
                }
            }

            Style borda = new Style("borda");
            Style bordaNumerica = new Style("bordaNumerica");

            if (campos != null) {
                if (campos.contains(Columns.CSA_NOME_ABREV)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.consignataria.nome.abreviado", responsavel), 100, false, "csa_nome_abrev", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains(Columns.CSA_NOME)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel), 100, false, "csa_nome", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains(Columns.COR_NOME)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel), 100, false, "cor_nome", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains(Columns.EST_NOME)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular", responsavel), 100, false, "est_nome", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains(Columns.ORG_NOME)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel), 100, false, "org_nome", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains(Columns.SVC_DESCRICAO)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel), 70, false, "svc_descricao", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains(Columns.SAD_DESCRICAO)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.consignacao.status", responsavel), 40, false, "sad_descricao", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains(Columns.DJU_DATA)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.decisao.judicial.inclusao", responsavel), 40, false, "dju_data", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains(Columns.TJU_DESCRICAO)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.decisao.judicial.tipo.justica", responsavel), 40, false, "tju_descricao", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains(Columns.CID_NOME)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.decisao.judicial.comarca", responsavel), 40, false, "cid_nome", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains(Columns.ADE_DATA)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.periodo.inclusao", responsavel), 55, false, "ade_data", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains(Columns.ADE_ANO_MES_INI)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inicial", responsavel), 55, false, "ade_ano_mes_ini", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains(Columns.ADE_ANO_MES_FIM)) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.final", responsavel), 55, false, "ade_ano_mes_fim", String.class.getName(), borda);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains("CONTRATOS")) {
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.qtd.contratos", responsavel), 50, false, "contratos", Long.class.getName(), bordaNumerica);
                    AbstractColumn columnCor = columnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains("PRESTACAO")) {
                    ColumnBuilder fixedColumnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.total.prd.geral", responsavel), 60, true, "prestacao", BigDecimal.class.getName(), bordaNumerica, "#,##0.00");
                    AbstractColumn columnCor = fixedColumnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains("VALOR")) {
                    ColumnBuilder fixedColumnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.total.prd.mensal", responsavel), 60, true, "valor", BigDecimal.class.getName(), bordaNumerica, "#,##0.00");
                    AbstractColumn columnCor = fixedColumnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
                if (campos.contains("CAPITAL_DEVIDO")) {
                    ColumnBuilder fixedColumnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.consignacao.capital.devido", responsavel), 60, true, "capital_devido", BigDecimal.class.getName(), bordaNumerica, "#,##0.00");
                    AbstractColumn columnCor = fixedColumnBuilder.build();
                    reportBuilder.addColumn(columnCor);
                }
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return reportBuilder;
    }

}
