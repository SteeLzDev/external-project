package com.zetra.econsig.report.jasper.dinamico;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalTextAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalTextAlign;

/**
 * <p>Title: DynamicReportInfo</p>
 * <p>Description: Classe base com métodos para criar relatórios dinâmicos, assim como definir estilos padrões</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class DynamicReportInfo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DynamicReportInfo.class);

    protected Map<String, Object> parameters;
    protected CustomTransferObject criterioQuery;
    protected Relatorio relatorio;

    public DynamicReportInfo(Relatorio relatorio) {
        this.relatorio = relatorio;
    }

    /**
     * parâmetros do relatório (título, subtítulo, nome do arquivo, etc.)
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * transfer object com as colunas que serão exibidas no relatório
     * @param criterioQuery
     */
    public void setCriterios(CustomTransferObject criterioQuery) {
        this.criterioQuery = criterioQuery;
    }

    /**
     * caminho completo para arquivos eConsig
     * @param templateName
     * @param responsavel
     * @return
     */
    protected String getTemplatePath (String templateName, AcessoSistema responsavel) {
        // Recupera o path do sistema de acordo com o parametro
        // e acrescenta o diretório jasper
        final StringBuilder templatePath = new StringBuilder().append(ParamSist.getDiretorioRaizArquivos());
        templatePath.append("/conf/relatorio/").append(templateName);

        return templatePath.toString();
    }

    /**
     * estilo padrão para cabeçalho das colunas
     */
    protected Style getHeaderStyle () {
        final Style cabecalho = new Style();
        cabecalho.setFont(new Font(10, Font._FONT_ARIAL, false));
        cabecalho.setVerticalTextAlign(VerticalTextAlign.TOP);
        cabecalho.setHorizontalTextAlign(HorizontalTextAlign.CENTER);
        cabecalho.setBackgroundColor(Color.GRAY);
        cabecalho.setBorder(Border.THIN());
        cabecalho.setTransparency(Transparency.OPAQUE);

        return cabecalho;
    }

    protected Style getTextColumnStyle() {
        final Style borda = new Style();
        borda.setFont(new Font(10, Font._FONT_ARIAL, false));
        borda.setVerticalTextAlign(VerticalTextAlign.TOP);
        borda.setBorder(Border.THIN());
        borda.setTransparency(Transparency.OPAQUE);
        borda.setBackgroundColor(Color.WHITE);

        return borda;

    }

    protected Style getNumericColumnStyle() {
        final Style bordaNumerica = new Style();
        bordaNumerica.setFont(new Font(10, Font._FONT_ARIAL, false));
        bordaNumerica.setVerticalTextAlign(VerticalTextAlign.TOP);
        bordaNumerica.setBorder(Border.THIN());
        bordaNumerica.setTransparency(Transparency.OPAQUE);
        bordaNumerica.setBackgroundColor(Color.WHITE);

        return bordaNumerica;
    }

    /**
     * coluna padrão de relatórios do eConsig
     * @param title - título da coluna
     * @param width - largura padrão da coluna
     * @param fixedWidth - se a largura deve ser mantida fixa, independente de como o relatório será montado
     * @param queryFieldName - nome da coluna equivalente no SQL
     * @param fieldType - tipo da coluna
     * @param columnStyle - estilo da coluna
     * @return
     */
    protected ColumnBuilder buildColumn(String title, int width, boolean fixedWidth, String queryFieldName, String fieldType, Style columnStyle) {
        return buildColumn(title, width, fixedWidth, queryFieldName, fieldType, columnStyle, null);
    }

    /**
     * Constrói coluna relatórios.
     *
     * @param title - título da coluna
     * @param width - largura padrão da coluna
     * @param fixedWidth - se a largura deve ser mantida fixa, independente de como o relatório será montado
     * @param queryFieldName - nome da coluna equivalente no SQL
     * @param fieldType - tipo da coluna
     * @param columnStyle - estilo da coluna
     * @param pattern - Seta a formatação da coluna caso seja informada
     * @return
     */
    protected ColumnBuilder buildColumn(String title, int width, boolean fixedWidth, String queryFieldName, String fieldType, Style columnStyle, String pattern) {
        final ColumnBuilder columnBuilder = new ColumnBuilder();
        columnBuilder.setTitle(title);
        columnBuilder.setColumnProperty(queryFieldName, fieldType);
        if (!TextHelper.isNull(pattern)) {
            columnBuilder.setPattern(pattern);
        }
        columnBuilder.setWidth(width);
        columnBuilder.setFixedWidth(fixedWidth);
        columnBuilder.setStyle(columnStyle);
        columnBuilder.setHeaderStyle(getHeaderStyle());

        return columnBuilder;
    }

    /**
     * chamada das APIs do Dynamic Jasper para construção efetiva do relatório dinâmico
     * @param parameters
     * @param relatorio
     * @param responsavel
     * @return
     */
    public DynamicReport buildJRXML(Map<String, Object> parameters, AcessoSistema responsavel) {
        DynamicReport reportGenerated = null;

        try {
            final DynamicReportBuilder reportBuilder = reportDesign(responsavel);
            setRowStyle(reportBuilder);

            createModel(responsavel);
            reportBuilder.setTemplateFile(getTemplatePath(relatorio.getModeloDinamico(), responsavel), true, true, true, false);

            // Por padrão se um valor não cabe em uma linha ele é quebrado. Esta tag faz com que se isso acontecer na ultima linha da pagina
            // ao inves da linha ser quebrada (ficando uma parte em cada página) ela aparece diretamente na proxima página.
            reportBuilder.setAllowDetailSplit(false);

            reportGenerated = reportBuilder.build();
            DynamicJasperHelper.generateJRXML(reportGenerated, new ClassicLayoutManager(), parameters, null, getTemplatePath(relatorio.getJasperTemplate().replaceAll(".jasper", ".jrxml"), responsavel));

            /* esses dois parametros são setados automaticamente pelo DynamicJasperHelper no metodo generateJRXML.
             * isso acaba gerando um erro pois o componente de resource bundle que ele seta não é serializavel.
             * Além disso ele não acha o arquivo de recursos pt_br
             */
            parameters.remove("REPORT_LOCALE");
            parameters.remove("REPORT_RESOURCE_BUNDLE");

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return reportGenerated;
    }

    /**
     * seta estilo padrão do eConsig para as linhas do relatório (com cores alternadas entre as linhas)
     * @param reportBuilder
     * @return
     */
    protected DynamicReportBuilder setRowStyle(DynamicReportBuilder reportBuilder) {
        final Color veryLightGrey = new Color(230, 230, 230);
        final Color veryLightBlue = new Color(210, 210, 250);

        final Border border = Border.PEN_2_POINT();
        border.setColor(veryLightBlue);

        final Style oddRowStyle = new Style();
        oddRowStyle.setBorder(border);
        oddRowStyle.setBackgroundColor(veryLightGrey);
        oddRowStyle.setTransparency(Transparency.OPAQUE);

        // responsavel por setar a troca de background a cada linha
        reportBuilder.setOddRowBackgroundStyle(oddRowStyle);
        reportBuilder.setPrintBackgroundOnOddRows(true);
        reportBuilder.setUseFullPageWidth(true);
        reportBuilder.setAllowDetailSplit(false);

        return reportBuilder;
    }

    /**
     * copia o modelo base do relatório dinâmico para a pasta de arquivos do eConsig onde o mesmo
     * será processado
     * @param relatorio
     * @param responsavel
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void createModel(AcessoSistema responsavel) throws IOException, FileNotFoundException {
        final String pathJrxml = ReportManager.PATH_JRXML + relatorio.getModeloDinamico();
        final File layoutReport = new File(getTemplatePath(relatorio.getModeloDinamico(), responsavel));
        final long layoutReportLastModified = layoutReport.lastModified();
        final String lineSeparator = System.lineSeparator();
        if (!layoutReport.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(pathJrxml), "UTF-8"))) {
                try (BufferedWriter bo = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getTemplatePath(relatorio.getModeloDinamico(), responsavel)), "UTF-8"))) {
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        bo.write(line);
                        bo.write(lineSeparator);
                        bo.flush();
                    }
                }
            }
        } else {
            final Resource jrxmlResource = new ClassPathResource(pathJrxml);
            final URL jarURL = jrxmlResource.getURL();

            final URLConnection jarCon = jarURL.openConnection();
            final long jarLastModified = jarCon.getLastModified();

            // se o arquivo jrxml no jar for mais recente que no diretório de arquivos
            // do eConsig, significa que o mesmo foi atualizado.
            if (jarLastModified > layoutReportLastModified) {
                layoutReport.delete();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(pathJrxml), "UTF-8"))) {
                    try (BufferedWriter bo = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getTemplatePath(relatorio.getModeloDinamico(), responsavel)), "UTF-8"))) {
                        String line = null;

                        while ((line = br.readLine()) != null) {
                            bo.write(line);
                            bo.write(lineSeparator);
                            bo.flush();
                        }
                    }
                }
            }
        }
    }

    /**
     * Este método deve ser implementado pelas subClasses. É onde serão incluídas as colunas
     * dinâmicas de cada relatório
     * @return
     */
    protected abstract DynamicReportBuilder reportDesign(AcessoSistema responsavel);
}