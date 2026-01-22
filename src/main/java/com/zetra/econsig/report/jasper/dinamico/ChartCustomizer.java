package com.zetra.econsig.report.jasper.dinamico;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartCustomizer;

/**
 * <p>Title: ChartCustomizer</p>
 * <p>Description: ChartCustomizer.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Rodrigo Barbosa, Igor Lucas, Leonel Martinss
 */
public class ChartCustomizer implements JRChartCustomizer {

    protected static final RectangleEdge DEFAULT_LEGEND_POSITION = RectangleEdge.BOTTOM;

    @Override
    public void customize(JFreeChart chart, JRChart jasperChart) {
        final Plot plot = chart.getPlot();
        if (plot instanceof final PiePlot piePlot) {
            piePlot.setLabelGenerator(null);
        }
        if (plot instanceof final CategoryPlot categoryPlot) {
           final CategoryAxis xAxis = categoryPlot.getDomainAxis();
           xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        }

        /* Chart Properties */
        chart.setBackgroundPaint(Color.white);
        chart.setBorderPaint(new Color(192, 192, 192));
        chart.setBorderVisible(true);
        chart.setBorderStroke(new BasicStroke(0.001f));
        chart.setPadding(new RectangleInsets(0, 1, 0, 1)); // top,left,bottom,right

        /* Legend Properties */
        try {

            final LegendTitle legend = chart.getLegend();

            legend.setPosition(DEFAULT_LEGEND_POSITION);
            legend.setMargin(10, 10, 10, 10);
            legend.setBounds(new Rectangle(new Dimension(200, 80)));

            plot.setInsets(new RectangleInsets(0.0, 0.0, 1.0, 1.0));

        } catch (final NullPointerException e) {
            e.printStackTrace();
        }
    }
}