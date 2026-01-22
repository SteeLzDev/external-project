package com.zetra.econsig.report.jasper.dinamico;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartCustomizer;

public class InadimplenciaCustomizer implements JRChartCustomizer {

    @Override
    public void customize(JFreeChart chart, JRChart jasperChart) {
        final Plot plot = chart.getPlot();

        if (plot instanceof final CategoryPlot categoryPlot) {
           final ValueAxis yAxis = categoryPlot.getRangeAxis();
           yAxis.setUpperMargin(0.2);
           yAxis.setVisible(false);
        }
    }
}