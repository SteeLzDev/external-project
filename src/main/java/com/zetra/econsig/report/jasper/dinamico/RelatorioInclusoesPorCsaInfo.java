package com.zetra.econsig.report.jasper.dinamico;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.report.config.Relatorio;

import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalTextAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalTextAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

/**
 * <p>Title: RelatorioInclusoesPorCsaInfo</p>
 * <p>Description: Definição de colunas do Relatório Inclusões por Consignatária</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioInclusoesPorCsaInfo extends DynamicReportInfo {

    public RelatorioInclusoesPorCsaInfo(Relatorio relatorio) {
        super(relatorio);
    }

    @Override
    protected DynamicReportBuilder reportDesign(AcessoSistema responsavel) {
        DynamicReportBuilder reportBuilder = new DynamicReportBuilder();
        reportBuilder.setWhenNoDataAllSectionNoDetail();

        LinkedList<String> campos = (LinkedList<String>) criterioQuery.getAttribute("campos");
        int countMes = (Integer) criterioQuery.getAttribute("countMes");
        int countEst = (Integer) criterioQuery.getAttribute("countEst");
        int countPeriodos = (Integer) criterioQuery.getAttribute("countPeriodos");
        LinkedList<String> alias = (LinkedList<String>) criterioQuery.getAttribute("alias");
        String tituloId = ApplicationResourcesHelper.getMessage("rotulo.relatorio.inclusoes.por.csa.identificador", responsavel);
        String tituloDescricao = ApplicationResourcesHelper.getMessage("rotulo.relatorio.inclusoes.por.csa.nome", responsavel);
        String tituloQtde = ApplicationResourcesHelper.getMessage("rotulo.relatorio.inclusoes.por.csa.qtde.inclusoes", responsavel);
        String tituloVlr = ApplicationResourcesHelper.getMessage("rotulo.relatorio.inclusoes.por.csa.valor.inclusoes", responsavel);
        String tituloTotalGeral = ApplicationResourcesHelper.getMessage("rotulo.relatorio.inclusoes.por.csa.total.geral", responsavel);
        String formato = (String) criterioQuery.getAttribute("formato");
        String tituloColuna = "";
        String tituloColunaQtde = "";
        String tituloColunaVlr = "";
        int tamanhoColuna = 150;

        Style borda = new Style("borda");

        Style bordaNumerica = new Style("bordaNumerica");

        Style bordaCentro = new Style();
        bordaCentro.setFont(new Font(10, Font._FONT_ARIAL, false));
        bordaCentro.setVerticalTextAlign(VerticalTextAlign.TOP);
        bordaCentro.setHorizontalTextAlign(HorizontalTextAlign.CENTER);
        bordaCentro.setBorder(Border.THIN());
        bordaCentro.setTransparency(Transparency.OPAQUE);
        bordaCentro.setBackgroundColor(Color.WHITE);

        // coluna csa identificador
        ColumnBuilder fixedColumnBuilder = buildColumn(tituloId, tamanhoColuna, false, "CSA_IDENTIFICADOR", String.class.getName(), borda);
        fixedColumnBuilder.setFixedWidth(true);
        AbstractColumn columnCsaIdentificador = fixedColumnBuilder.build();
        reportBuilder.addColumn(columnCsaIdentificador);

        // coluna csa nome
        fixedColumnBuilder = buildColumn(tituloDescricao, tamanhoColuna, false, "CSA_NOME", String.class.getName(), borda);
        fixedColumnBuilder.setFixedWidth(true);
        AbstractColumn columnCsaNome = fixedColumnBuilder.build();
        reportBuilder.addColumn(columnCsaNome);

        for (int i = 1; i <= (countMes); i++) {
            ArrayList<Object> lstColumnsQtde = new ArrayList<>();
            ArrayList<Object> lstColumnsVlr = new ArrayList<>();
            for (int j = 1; j <= countPeriodos; j++) {
                for (int k = 1; k <= countEst; k++) {
                    String chave_qtde = "TOTAL_QTDE_Q_" + i + "_" + k + "_" + j;
                    String chave_vlr = "TOTAL_VALOR_Q_" + i + "_" + k + "_" + j;
                    if (alias.contains(chave_qtde) && alias.contains(chave_vlr)) {
                        if (formato.equals("TEXT")) {
                            tituloColuna = campos.remove();
                            tituloColunaQtde = tituloColuna + " " + tituloQtde;
                            tituloColunaVlr = tituloColuna + " " + tituloVlr;
                        } else {
                            tituloColunaQtde = tituloQtde;
                            tituloColunaVlr = tituloVlr;
                        }

                        ColumnBuilder columnBuilder = null;
                        AbstractColumn coluna = null;
                        // qtde contratos por estabelecimento
                        columnBuilder = buildColumn(tituloColunaQtde, tamanhoColuna, false, chave_qtde, BigDecimal.class.getName(), bordaCentro);
                        columnBuilder.setFixedWidth(true);
                        coluna = columnBuilder.build();
                        lstColumnsQtde.add(coluna);
                        reportBuilder.addColumn(coluna);
                        // total qtde contratos por estabelecimento
                        reportBuilder.addGlobalFooterVariable(coluna, DJCalculation.SUM, getHeaderStyle());

                        // valor capital devido por estabelecimento
                        columnBuilder = buildColumn(tituloColunaVlr, tamanhoColuna, false, chave_vlr, BigDecimal.class.getName(), bordaNumerica, "#,##0.00");
                        columnBuilder.setFixedWidth(true);
                        coluna = columnBuilder.build();
                        lstColumnsVlr.add(coluna);
                        reportBuilder.addColumn(coluna);
                        // total capital devido por estabelecimento
                        reportBuilder.addGlobalFooterVariable(coluna, DJCalculation.SUM, getHeaderStyle());
                    }
                }
                // totais por período
                String chave_qtde = "TOTAL_QTDE_Q_" + i + "_" + j;
                String chave_vlr = "TOTAL_VALOR_Q_" + i + "_" + j;
                if (alias.contains(chave_qtde) && alias.contains(chave_vlr)) {
                    if (formato.equals("TEXT")) {
                        tituloColuna = campos.remove();
                        tituloColunaQtde = tituloColuna + " " + tituloQtde;
                        tituloColunaVlr = tituloColuna + " " + tituloVlr;
                    } else {
                        tituloColunaQtde = tituloQtde;
                        tituloColunaVlr = tituloVlr;
                    }
                    ColumnBuilder columnBuilder = null;
                    AbstractColumn coluna = null;
                    // qtde contratos por período
                    columnBuilder = buildColumn(tituloColunaQtde, tamanhoColuna, false, chave_qtde, BigDecimal.class.getName(), bordaCentro);
                    columnBuilder.setFixedWidth(true);
                    coluna = columnBuilder.build();
                    lstColumnsQtde.add(coluna);
                    reportBuilder.addColumn(coluna);
                    // total qtde contratos por período
                    reportBuilder.addGlobalFooterVariable(coluna, DJCalculation.SUM, getHeaderStyle());

                    // valor capital devido por período
                    columnBuilder = buildColumn(tituloColunaVlr, tamanhoColuna, false, chave_vlr, BigDecimal.class.getName(), bordaNumerica, "#,##0.00");
                    columnBuilder.setFixedWidth(true);
                    coluna = columnBuilder.build();
                    lstColumnsVlr.add(coluna);
                    reportBuilder.addColumn(coluna);
                    // total capital devido por período
                    reportBuilder.addGlobalFooterVariable(coluna, DJCalculation.SUM, getHeaderStyle());
                }
            }
        }
        // total geral
        reportBuilder.setGrandTotalLegend(tituloTotalGeral);
        reportBuilder.setGrandTotalLegendStyle(getHeaderStyle());
        // titulo das colunas com período e estabelecimentos
        if (!formato.equals("TEXT")) {
            for (int j = 2; j <= countPeriodos * (countEst + 1) * 2; j+=2) {
                reportBuilder.setColspan(j, 2, campos.remove().toString(), getHeaderStyle());
            }
            reportBuilder.setHeaderHeight(100);
            reportBuilder.setHeaderVariablesHeight(100);
        }

        Page page = new Page();
        page.setWidth(((countPeriodos * (countEst + 1) * 2) + 2 + 1000) * 2);
        page.setOrientationPortrait(false);
        reportBuilder.setPageSizeAndOrientation(page);

        return reportBuilder;
    }
}
