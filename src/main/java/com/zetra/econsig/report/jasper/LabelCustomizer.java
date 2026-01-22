package com.zetra.econsig.report.jasper;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartCustomizer;

/**
 * <p> Title: LabelCustomizer</p>
 * <p> Description: Esta classe evita a quebra de label extensos, para usá-la, </p>
 * <p> adicione customizerClass="com.zetra.econsig.report.jasper.LabelCustomizer"> ao chart </p>
 * <p> do elemento gráfico no template do relatório (.jrxml) </p>
 * <p> Copyright: Copyright (c) 2013 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 **/

public class LabelCustomizer implements JRChartCustomizer {

    @Override
    public void customize(JFreeChart chart, JRChart jasperChart) {
        final CategoryPlot plot = (CategoryPlot) chart.getPlot();

        final CategoryAxis axis = plot.getDomainAxis();

        axis.setMaximumCategoryLabelWidthRatio(2f);
    }
}
