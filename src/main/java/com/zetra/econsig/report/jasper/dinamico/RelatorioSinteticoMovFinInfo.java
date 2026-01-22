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
 * <p>Title: RelatorioSinteticoMovFinInfo</p>
 * <p>Description: Definição de colunas do Relatório Sintético de Mov. Financeira</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoMovFinInfo extends DynamicReportInfo{
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioSinteticoMovFinInfo.class);

    public RelatorioSinteticoMovFinInfo(Relatorio relatorio) {
        super(relatorio);
    }

    @Override
    protected DynamicReportBuilder reportDesign(AcessoSistema responsavel) {
        DynamicReportBuilder reportBuilder = new DynamicReportBuilder();
        reportBuilder.setWhenNoDataAllSectionNoDetail();

        try {
            List<String> camposRelatorio = (List<String>) criterioQuery.getAttribute("CAMPOS");
            List<String> campos = new ArrayList<>();

            CamposRelatorioSinteticoEnum camposEnum = null;
            for (String key : camposRelatorio) {
                camposEnum = CamposRelatorioSinteticoEnum.recuperaCampo(key);
                if (camposEnum != null) {
                    campos.add(camposEnum.getCampo());
                }
            }

            boolean tarifacao = criterioQuery.getAttribute("tarifacao") != null;
            boolean exibeMargem = criterioQuery.getAttribute("MAR_CODIGOS") != null;

            Style borda = new Style("borda");
            Style bordaNumerica = new Style("bordaNumerica");

            if (campos != null) {
                if (campos.contains(Columns.CSA_NOME_ABREV)) {
                    //coluna nome abreviado
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.consignataria.nome.abreviado", responsavel), 50, false, "csa_nome_abrev", String.class.getName(), borda);

                    AbstractColumn columnNomAbrev = columnBuilder.build();

                    reportBuilder.addColumn(columnNomAbrev);
                    columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel), 90, false, "csa_nome", String.class.getName(), borda);
                    AbstractColumn columnCsa = columnBuilder.build();
                    reportBuilder.addColumn(columnCsa);
                }

                if (campos.contains(Columns.COR_NOME)) {
                    //coluna correspondente
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel), 70, false, "cor_nome", String.class.getName(), borda);

                    AbstractColumn columnCor = columnBuilder.build();

                    reportBuilder.addColumn(columnCor);
                }

                if (campos.contains(Columns.EST_NOME)) {
                    //coluna estabelecimento
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular", responsavel), 150, false, "est_nome", String.class.getName(), borda);

                    AbstractColumn columnEst = columnBuilder.build();

                    reportBuilder.addColumn(columnEst);
                }

                if (campos.contains(Columns.ORG_NOME)) {
                    //coluna órgão
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel), 90, false, "org_nome", String.class.getName(), borda);

                    AbstractColumn columnOrgao = columnBuilder.build();

                    reportBuilder.addColumn(columnOrgao);
                }

                if (campos.contains(Columns.CNV_COD_VERBA)) {
                    //coluna código de verba
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.convenio.codigo.verba", responsavel), 50, false, "cnv_cod_verba", String.class.getName(), borda);

                    AbstractColumn columnServico = columnBuilder.build();

                    reportBuilder.addColumn(columnServico);
                }

                if (campos.contains(Columns.SVC_DESCRICAO)) {
                    //coluna serviço
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel), 90, false, "svc_descricao", String.class.getName(), borda);

                    AbstractColumn columnServico = columnBuilder.build();

                    reportBuilder.addColumn(columnServico);
                    if(exibeMargem) {
                        //coluna margem só exibe se serviço estiver sendo usado.
                        ColumnBuilder columnBuilderMargem = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.margem.singular", responsavel), 90, false, "mar_descricao", String.class.getName(), borda);

                        AbstractColumn columnMargem = columnBuilderMargem.build();

                        reportBuilder.addColumn(columnMargem);
                    }
                }

                if (campos.contains(Columns.SAD_DESCRICAO)) {
                    //coluna situação
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.consignacao.status", responsavel), 100, false, "sad_descricao", String.class.getName(), borda);

                    AbstractColumn columnSituacao = columnBuilder.build();

                    reportBuilder.addColumn(columnSituacao);
                }

                if (campos.contains(Columns.ADE_DATA)) {
                    //coluna data inicial
                    ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inicial", responsavel), 70, false, "ade_ano_mes_ini", String.class.getName(), borda);

                    AbstractColumn columnAdeData = columnBuilder.build();

                    reportBuilder.addColumn(columnAdeData);
                }
            }

            // coluna status
            ColumnBuilder fixedColumnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.status.parcela", responsavel), 100, false, "STATUS", String.class.getName(), borda);

            AbstractColumn columnStatus = fixedColumnBuilder.build();

            reportBuilder.addColumn(columnStatus);

            // coluna Num parcelas
            fixedColumnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.nro.parcelas", responsavel), 50, true, "NUM_PARCELAS", Long.class.getName(), bordaNumerica);
            AbstractColumn columnNumParcelas = fixedColumnBuilder.build();
            reportBuilder.addColumn(columnNumParcelas);

            // coluna total VLR ADE
            fixedColumnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.total.vlr.ade", responsavel), 75, true, "TOTAL_PREVISTO", BigDecimal.class.getName(), bordaNumerica, "#,##0.00");
            AbstractColumn columnPrevisto = fixedColumnBuilder.build();
            reportBuilder.addColumn(columnPrevisto);

            // coluna total prestações
            fixedColumnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.total.prd", responsavel), 75, true, "TOTAL_PRESTACOES", BigDecimal.class.getName(), bordaNumerica, "#,##0.00");
            AbstractColumn columnTotal = fixedColumnBuilder.build();
            reportBuilder.addColumn(columnTotal);

            if(tarifacao) {
                fixedColumnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.tarifacao.valor.tarifado", responsavel), 75, true, "VALOR_TARIFADO", BigDecimal.class.getName(), bordaNumerica, "#,##0.00");
                AbstractColumn columnTarifacao = fixedColumnBuilder.build();
                reportBuilder.addColumn(columnTarifacao);
            }

            if (campos.contains("MEDIA_QTD_PARCELAS")) {
                // coluna Media Qtd Parcelas
                ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.qtd.media.parcelas", responsavel), 50, false, "MEDIA_QTD_PARCELAS", BigDecimal.class.getName(), bordaNumerica, "#,##0.00");
                AbstractColumn columnMediaQntParcelas = columnBuilder.build();
                reportBuilder.addColumn(columnMediaQntParcelas);
            }

            if (campos.contains("VALOR_MEDIO_PARCELAS")) {
                // coluna Valor medio parcelas
                ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.valor.media.parcelas", responsavel), 50, false, "VALOR_MEDIO_PARCELAS", BigDecimal.class.getName(), bordaNumerica, "#,##0.00");
                AbstractColumn columnMediaQntParcelas = columnBuilder.build();
                reportBuilder.addColumn(columnMediaQntParcelas);
            }

            if (campos.contains("MEDIA_QNTD_PARCELAS_PAGAS")) {
                // coluna Media Qtd Parcelas Pagas
                ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.qtd.media.parcelas.pagas", responsavel), 50, false, "MEDIA_QNTD_PARCELAS_PAGAS", BigDecimal.class.getName(), bordaNumerica, "#,##0.00");
                AbstractColumn columnMediaQntParcelasPagas = columnBuilder.build();
                reportBuilder.addColumn(columnMediaQntParcelasPagas);
            }

            if (campos.contains("CAPITAL_DEVIDO")) {
                // coluna Capital Devido
                ColumnBuilder columnBuilder = buildColumn(ApplicationResourcesHelper.getMessage("rotulo.relatorio.capital.devido", responsavel), 50, false, "CAPITAL_DEVIDO", BigDecimal.class.getName(), bordaNumerica, "#,##0.00");
                AbstractColumn ColumnsCapitalDevido = columnBuilder.build();
                reportBuilder.addColumn(ColumnsCapitalDevido);
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return reportBuilder;
    }

}
