package com.zetra.econsig.helper.relatorio;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: ReportHelper</p>
 * <p>Description: Classes auxiliares para geração de relatórios em PDF e TXT.</p>
 * <p>Copyright: Copyright (c) 2003-2005</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReportHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReportHelper.class);

    private long nro_registros;

    private double totais[];

    private float lineBgColor;

    private String texto_rodape_esquerda, texto_rodape_centro, texto_cabecalho_direita, texto_cabecalho_centro;

    private int numLinhasCabecalho = 0;

    public ReportHelper() {
        texto_rodape_esquerda = null;
        texto_rodape_centro = null;
    }

    public void setTextoRodapeEsquerda(String texto) {
        texto_rodape_esquerda = texto;
    }

    public void setTextoRodapeCentro(String texto) {
        texto_rodape_centro = texto;
    }

    public void setTextoCabecalhoDireita(String texto) {
        texto_cabecalho_direita = texto;
    }

    public void setNumLinhasCab(int numLinhasCab) {
        numLinhasCabecalho = numLinhasCab;
    }

    public void setTextoCabecalhoCentro(String txt) {
        texto_cabecalho_centro = txt;
    }

    /******************************      TXT      ******************************/
    public void db2txt(String fileName, Connection con, String sql, int size, int numColumns) {
        try {
            final PrintWriter out = criaTxtDocument(fileName);

            final Statement stm = con.createStatement();
            final StringBuilder sqlb = new StringBuilder(sql);
            int offset = 0;
            sqlb.append(" LIMIT " + size);
            ResultSet rstNavegacao = stm.executeQuery(sqlb.toString());
            final String valores[] = new String[numColumns];
            while (rstNavegacao.next()) {
                rstNavegacao.beforeFirst();
                try {
                    while (rstNavegacao.next()) {
                        for (int i = 1; i <= numColumns; i++) {
                            valores[i - 1] = rstNavegacao.getString(i) == null ? "" : rstNavegacao.getString(i);
                        }
                        out.println(TextHelper.join(valores, ";"));
                    }
                } catch (final SQLException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                offset += size;
                sqlb.setLength(0);
                sqlb.append(sql + " LIMIT " + offset + ", " + size);
                rstNavegacao.close();
                rstNavegacao = stm.executeQuery(sqlb.toString());
            }
            rstNavegacao.close();
            stm.close();
            out.close();
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void db2txt(String fileName, ResultSet rstNavegacao, int numColumns) {
        try {
            final PrintWriter out = criaTxtDocument(fileName);

            try {
                while (rstNavegacao.next()) {
                    for (int i = 1; i < numColumns; i++) {
                        if (rstNavegacao.getString(i) != null) {
                            out.print(rstNavegacao.getString(i));
                        }
                        out.print(';');
                    }
                    if (rstNavegacao.getString(numColumns) != null) {
                        out.print(rstNavegacao.getString(numColumns));
                    } else {
                        out.println("");
                    }
                }
            } catch (final SQLException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            out.close();
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void list2txt(String fileName, List<TransferObject> lstNavegacao, String fields[]) {
        try {
            final PrintWriter out = criaTxtDocument(fileName);
            dumpList(out, lstNavegacao, fields);
            out.close();
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public PrintWriter criaTxtDocument(String fileName) throws IOException {
        return new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
    }

    public void dumpList(PrintWriter out, List<TransferObject> lstNavegacao, String fields[]) {
        final String valores[] = new String[fields.length];
        TransferObject cto = null;
        for (final TransferObject element : lstNavegacao) {
            cto = element;
            for (int x = 0; x < fields.length; x++) {
                Object val = cto.getAttribute(fields[x]);
                if (val != null) {
                    if (val instanceof java.util.Date) {
                        val = DateHelper.toDateString((java.util.Date) val);

                    } else if ((val instanceof java.lang.Float) ||
                               (val instanceof java.lang.Double) ||
                               (val instanceof java.math.BigDecimal)) {
                        final double valor = ((Number) val).doubleValue();
                        val = NumberHelper.format(valor, NumberHelper.getLang(), true);

                    } else if (val instanceof byte[]) {
                        val = new String((byte[]) val);
                    }
                }
                if (val == null) {
                    val = "";
                }
                valores[x] = val.toString();

            }
            out.println(TextHelper.join(valores, ";"));
        }
    }

    /******************************      PDF      ******************************/

    public void db2pdf(String fileName, Connection con, String sql, int size, String reportTitle, int widths[], int aligns[], String titles[]) {
        final String reportTitles[] = reportTitle.split(System.getProperty("line.separator"));
        final Document document = criaPdfDocument(PageSize.A4.rotate(), 30, 30, 20 + (reportTitles.length * MyPageEvents.HEADER_LINE_SIZE), 30, fileName, reportTitles, widths);
        try {
            document.open();

            final Statement stm = con.createStatement();
            final StringBuilder sqlb = new StringBuilder(sql);
            int offset = 0;
            sqlb.append(" LIMIT " + size);
            ResultSet rstNavegacao = stm.executeQuery(sqlb.toString());
            final PdfPTable tabela = criaTabela(widths, aligns, titles);
            while (rstNavegacao.next()) {
                rstNavegacao.beforeFirst();
                preencheTabela(tabela, rstNavegacao, widths, aligns, titles);
                offset += size;
                sqlb.setLength(0);
                sqlb.append(sql + " LIMIT " + offset + ", " + size);
                rstNavegacao.close();
                rstNavegacao = stm.executeQuery(sqlb.toString());
            }
            if (nro_registros > 0) {
                adicionaResumoTabela(tabela, rstNavegacao, widths, aligns, titles);
            }
            rstNavegacao.close();
            stm.close();
            document.add(tabela);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        if (document != null) {
            document.close();
        }
    }

    public void db2pdf(String fileName, ResultSet rstNavegacao, String reportTitle, int widths[], int aligns[], String titles[]) {
        final String reportTitles[] = reportTitle.split(System.getProperty("line.separator"));
        final Document document = criaPdfDocument(PageSize.A4.rotate(), 30, 30, 20 + (reportTitles.length * MyPageEvents.HEADER_LINE_SIZE), 30, fileName, reportTitles, widths);
        try {
            document.open();
            document.add(montaTabela(rstNavegacao, widths, aligns, titles));
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        if (document != null) {
            document.close();
        }
    }

    private String[] getReportTitles(String reportTitle) {
        final int size = 140;
        final String[] reportTitles = reportTitle.split(System.getProperty("line.separator"));
        for (int i = 0; i < reportTitles.length; i++) {
            if ((reportTitles[i] != null) && (reportTitles[i].length() > size)) {
                reportTitles[i] = reportTitles[i].substring(0, size - 4) + " ...";
            }
        }
        return reportTitles;
    }

    public void list2pdf(String fileName, List<TransferObject> lstNavegacao, String reportTitle, int widths[], int aligns[], String titles[], String fields[]) {
        list2pdf(fileName, lstNavegacao, reportTitle, widths, aligns, titles, fields, true);
    }

    public void list2pdf(String fileName, List<TransferObject> lstNavegacao, String reportTitle, int widths[], int aligns[], String titles[], String fields[], boolean totalizar) {
        final String[] reportTitles = getReportTitles(reportTitle);
        Document document;

        if (numLinhasCabecalho == 0) {
            document = criaPdfDocument(PageSize.A4.rotate(), 30, 30, 20 + (reportTitles.length * MyPageEvents.HEADER_LINE_SIZE), 30, fileName, reportTitles, widths);

        } else {
            document = criaPdfDocument(PageSize.A4.rotate(), 30, 30, 20 + (numLinhasCabecalho * MyPageEvents.HEADER_LINE_SIZE), 30, fileName, reportTitles, widths);
        }
        try {
            document.open();
            document.add(montaTabela(lstNavegacao, widths, aligns, titles, fields, totalizar));
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        if (document != null) {
            document.close();
        }
    }

    public void list2pdf(String fileName, List<TransferObject> lstNavegacao, String reportTitle, int widths[], int aligns[], String titles[], String fields[], String filter) {
        list2pdf(fileName, lstNavegacao, reportTitle, widths, aligns, titles, fields, filter, true);
    }

    public void list2pdf(String fileName, List<TransferObject> lstNavegacao, String reportTitle, int widths[], int aligns[], String titles[], String fields[], String filter, boolean totalizar) {
        if (filter == null) {
            list2pdf(fileName, lstNavegacao, reportTitle, widths, aligns, titles, fields);
        } else {
            final String[] reportTitles = getReportTitles(reportTitle);
            final Document document = criaPdfDocument(PageSize.A4.rotate(), 30, 30, 20 + (reportTitles.length * MyPageEvents.HEADER_LINE_SIZE), 30, fileName, reportTitles, widths);
            try {
                document.open();
                final List<PdfPTable> tabelas = montaTabela(lstNavegacao, widths, aligns, titles, fields, filter, totalizar);

                for (final PdfPTable element : tabelas) {
                    document.add(element);
                    document.newPage();
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
            if (document != null) {
                document.close();
            }
        }
    }

    public Document criaPdfDocument(Rectangle pageSize, float marginLeft, float marginRight, float marginTop, float marginBottom, String fileName, String reportTitles[], int widths[]) {
        try {
            final Document document = new Document(pageSize, marginLeft, marginRight, marginTop, marginBottom);
            final PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            final MyPageEvents events = new MyPageEvents();
            events.setTitulos(reportTitles);
            events.setTextoRodapeEsquerda(texto_rodape_esquerda);
            events.setTextoRodapeCentro(texto_rodape_centro);
            events.setTextoCabecalhoDireita(texto_cabecalho_direita);
            events.setTextoCabecalhoCentro(texto_cabecalho_centro);
            writer.setPageEvent(events);
            nro_registros = 0;
            totais = new double[widths.length];
            return document;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    public PdfPTable criaTabela(int widths[], int aligns[], String titles[]) {
        return criaTabela(null, widths, aligns, titles);
    }

    public PdfPTable criaTabela(String filter, int widths[], int aligns[], String titles[]) {
        final PdfPTable datatable = new PdfPTable(titles.length);
        try {
            datatable.getDefaultCell().setPadding(3);
            datatable.setWidths(widths);
            datatable.setWidthPercentage(100); // percentage

            datatable.getDefaultCell().setBorderWidth(1);
            int hr = 1;
            if (filter != null) {
                datatable.getDefaultCell().setGrayFill(0.6f);
                datatable.getDefaultCell().setColspan(titles.length);
                datatable.addCell(filter);
                datatable.getDefaultCell().setColspan(1);
                hr = 2;
            }

            datatable.getDefaultCell().setGrayFill(0.7f);
            datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            for (int x = 0; x < titles.length; x++) {
                datatable.addCell(titles[x]);
                totais[x] = 0;
            }
            datatable.setHeaderRows(hr); // this is the end of the table header
        } catch (final DocumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return datatable;
    }

    public void preencheTabela(PdfPTable datatable, ResultSet rstNavegacao, int widths[], int aligns[], String titles[]) {
        try {
            final ResultSetMetaData metaData = rstNavegacao.getMetaData();

            datatable.getDefaultCell().setBorderWidth(1);
            boolean hasData = rstNavegacao.next();
            if (!hasData) {
                datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                datatable.getDefaultCell().setGrayFill(0.9f);
                datatable.getDefaultCell().setColspan(titles.length);
                final Phrase phrase = new Phrase(ApplicationResourcesHelper.getMessage("mensagem.erro.nenhum.registro.encontrado", (AcessoSistema) null), FontFactory.getFont(FontFactory.HELVETICA, 9));
                datatable.addCell(phrase);
            } else {
                int i = 1;
                String val;
                while (hasData) {
                    if ((i % 2) == 1) {
                        lineBgColor = 0.9f;
                    } else {
                        lineBgColor = 1.1f;
                    }
                    datatable.getDefaultCell().setGrayFill(lineBgColor);
                    for (int x = 1; x <= titles.length; x++) {
                        val = rstNavegacao.getString(x);
                        switch (metaData.getColumnType(x)) {
                            case Types.CHAR:
                            case Types.VARCHAR:
                            case Types.LONGVARCHAR:
                            case Types.BIT:
                                break;
                            case Types.TINYINT:
                            case Types.SMALLINT:
                            case Types.INTEGER:
                            case Types.BIGINT:
                                if (val != null) {
                                    totais[x - 1] += Double.parseDouble(val);
                                }
                                break;
                            case Types.FLOAT:
                            case Types.DECIMAL:
                            case Types.DOUBLE:
                                if (val != null) {
                                    totais[x - 1] += Double.parseDouble(val);
                                }
                                val = NumberHelper.reformat(val, "en", NumberHelper.getLang(), true);
                                break;
                            case Types.DATE:
                                val = DateHelper.toDateString(rstNavegacao.getDate(x));
                        }
                        if (val == null) {
                            val = "";
                        }
                        datatable.getDefaultCell().setHorizontalAlignment(aligns[x - 1]);
                        final Phrase phrase = new Phrase(val, FontFactory.getFont(FontFactory.HELVETICA, 9));
                        datatable.addCell(phrase);
                    }
                    i++;
                    hasData = rstNavegacao.next();
                }
                nro_registros += i - 1;
            }
        } catch (final ParseException | SQLException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public void preencheTabela(PdfPTable datatable, List<TransferObject> lstNavegacao, int widths[], int aligns[], String titles[], String fields[]) {
        datatable.getDefaultCell().setBorderWidth(1);
        final Iterator<TransferObject> it = lstNavegacao.iterator();
        if (!it.hasNext()) {
            datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            datatable.getDefaultCell().setGrayFill(0.9f);
            datatable.getDefaultCell().setColspan(titles.length);
            final Phrase phrase = new Phrase(ApplicationResourcesHelper.getMessage("mensagem.erro.nenhum.registro.encontrado", (AcessoSistema) null), FontFactory.getFont(FontFactory.HELVETICA, 9));
            datatable.addCell(phrase);
        } else {
            int i = 1;
            Object val = null;
            while (it.hasNext()) {
                if ((i % 2) == 1) {
                    lineBgColor = 0.9f;
                } else {
                    lineBgColor = 1.1f;
                }
                datatable.getDefaultCell().setGrayFill(lineBgColor);
                final TransferObject cto = it.next();
                for (int x = 0; x < fields.length; x++) {
                    val = cto.getAttribute(fields[x]);
                    if (val != null) {
                        if (val instanceof java.util.Date) {
                            val = DateHelper.toDateString((java.util.Date) val);

                        } else if (val instanceof Number) {
                            final double valor = ((Number) val).doubleValue();
                            if (val != null) {
                                totais[x] += valor;
                            }
                            if ((val instanceof java.lang.Float) ||
                                (val instanceof java.lang.Double) ||
                                (val instanceof java.math.BigDecimal)) {
                                val = NumberHelper.format(valor, NumberHelper.getLang(), true);
                            }
                        } else if (val instanceof byte[]) {
                            val = new String((byte[]) val);
                        }
                    }
                    if (val == null) {
                        val = "";
                    }
                    datatable.getDefaultCell().setHorizontalAlignment(aligns[x]);
                    final Phrase phrase = new Phrase(val.toString(), FontFactory.getFont(FontFactory.HELVETICA, 9));
                    datatable.addCell(phrase);
                }
                i++;
            }
            nro_registros += i - 1;
        }
    }

    public List<PdfPTable> montaTabela(List<TransferObject> lstNavegacao, int widths[], int aligns[], String titles[], String fields[], String filter, boolean totalizar) {

        final List<PdfPTable> tables = new ArrayList<>();
        PdfPTable datatable = null;
        Iterator<TransferObject> it = lstNavegacao.iterator();

        if (!it.hasNext()) {
            datatable = criaTabela(widths, aligns, titles);
            datatable.getDefaultCell().setBorderWidth(1);

            datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            datatable.getDefaultCell().setGrayFill(0.9f);
            datatable.getDefaultCell().setColspan(titles.length);
            final Phrase phrase = new Phrase(ApplicationResourcesHelper.getMessage("mensagem.erro.nenhum.registro.encontrado", (AcessoSistema) null), FontFactory.getFont(FontFactory.HELVETICA, 9));
            datatable.addCell(phrase);
            tables.add(datatable);
        } else {
            int i = 1;

            Object val = null;
            String filtroVelho = null;
            String filtroAtual = null;
            CustomTransferObject cto = null;

            // Cria a primeira tabela
            cto = (CustomTransferObject) it.next();
            filtroAtual = cto.getAttribute(filter).toString();
            datatable = criaTabela(filtroAtual, widths, aligns, titles);
            datatable.getDefaultCell().setBorderWidth(1);

            // Contabiliza os resumos das colunas
            final double[] totaisGerais = new double[totais.length];
            Arrays.fill(totaisGerais, 0);

            it = lstNavegacao.iterator();
            while (it.hasNext()) {
                cto = (CustomTransferObject) it.next();
                filtroAtual = cto.getAttribute(filter).toString();
                filtroVelho = (filtroVelho == null) ? filtroAtual : filtroVelho;

                if (!filtroAtual.equals(filtroVelho)) {

                    // Adiciona o resumo da ultima tabela
                    adicionaResumoTabela(datatable, lstNavegacao, widths, aligns, titles, fields, totalizar);
                    // Adiciona a última tabela na Lista
                    tables.add(datatable);

                    datatable = criaTabela(filtroAtual, widths, aligns, titles);
                    datatable.getDefaultCell().setBorderWidth(1);

                    filtroVelho = filtroAtual;
                    nro_registros = 0;
                }

                if ((i % 2) == 1) {
                    lineBgColor = 0.9f;
                } else {
                    lineBgColor = 1.1f;
                }
                datatable.getDefaultCell().setGrayFill(lineBgColor);

                for (int x = 0; x < fields.length; x++) {
                    val = cto.getAttribute(fields[x]);
                    if (val != null) {
                        if (val instanceof java.util.Date) {
                            val = DateHelper.toDateString((java.util.Date) val);

                        } else if (val instanceof Number) {
                            final double valor = ((Number) val).doubleValue();
                            if (val != null) {
                                totais[x] += valor;
                                totaisGerais[x] += valor;
                            }
                            if ((val instanceof java.lang.Float) ||
                                (val instanceof java.lang.Double) ||
                                (val instanceof java.math.BigDecimal)) {
                                val = NumberHelper.format(valor, NumberHelper.getLang(), true);
                            }
                        } else if (val instanceof byte[]) {
                            val = new String((byte[]) val);
                        }
                    }
                    if (val == null) {
                        val = "";
                    }
                    datatable.getDefaultCell().setHorizontalAlignment(aligns[x]);
                    final Phrase phrase = new Phrase(val.toString(), FontFactory.getFont(FontFactory.HELVETICA, 9));
                    datatable.addCell(phrase);
                }
                i++;
                nro_registros++;
            }

            // Adiciona o resumo da ultima tabela
            adicionaResumoTabela(datatable, lstNavegacao, widths, aligns, titles, fields, totalizar);
            // Adiciona a última tabela na Lista
            tables.add(datatable);

            // Adiciona uma tabela com o resumo geral
            datatable = criaTabela("RESUMO", widths, aligns, titles);
            datatable.getDefaultCell().setBorderWidth(1);
            // Pega o número de registros geral
            nro_registros = i - 1;
            // Pega a contabilidade geral
            totais = totaisGerais;
            // Adiciona o resumo geral
            adicionaResumoTabela(datatable, lstNavegacao, widths, aligns, titles, fields, totalizar);
            // Adiciona a tabela com o resumo
            tables.add(datatable);
        }
        return tables;
    }

    public PdfPTable montaTabela(ResultSet rstNavegacao, int widths[], int aligns[], String titles[]) {
        final PdfPTable datatable = criaTabela(widths, aligns, titles);
        preencheTabela(datatable, rstNavegacao, widths, aligns, titles);
        if (nro_registros > 0) {
            adicionaResumoTabela(datatable, rstNavegacao, widths, aligns, titles);
        }
        return datatable;
    }

    public PdfPTable montaTabela(List<TransferObject> lstNavegacao, int widths[], int aligns[], String titles[], String fields[], boolean totalizar) {
        final PdfPTable datatable = criaTabela(widths, aligns, titles);
        preencheTabela(datatable, lstNavegacao, widths, aligns, titles, fields);
        if (nro_registros > 0) {
            adicionaResumoTabela(datatable, lstNavegacao, widths, aligns, titles, fields, totalizar);
        }
        return datatable;
    }

    public void adicionaResumoTabela(PdfPTable datatable, ResultSet rstNavegacao, int widths[], int aligns[], String titles[]) {
        try {
            ResultSetMetaData metaData;
            metaData = rstNavegacao.getMetaData();

            datatable.getDefaultCell().setPadding(3);
            datatable.setWidths(widths);
            datatable.setWidthPercentage(100); // percentage

            datatable.getDefaultCell().setBorderWidth(1);
            lineBgColor = (lineBgColor == 1.1f) ? 0.9f : 1.1f;
            datatable.getDefaultCell().setGrayFill(lineBgColor);
            datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            Phrase phrase = new Phrase(ApplicationResourcesHelper.getMessage("rotulo.numero.registros", (AcessoSistema) null, String.valueOf(nro_registros)), FontFactory.getFont(FontFactory.HELVETICA, 9));
            datatable.addCell(phrase);
            for (int x = 2; x <= titles.length; x++) {
                String val = "";
                switch (metaData.getColumnType(x)) {
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.LONGVARCHAR:
                    case Types.BIT:
                        break;
                    case Types.TINYINT:
                    case Types.SMALLINT:
                    case Types.INTEGER:
                    case Types.BIGINT:
                        val = String.valueOf(Double.valueOf(totais[x - 1]).intValue());
                        break;
                    case Types.FLOAT:
                    case Types.DECIMAL:
                    case Types.DOUBLE:
                        val = NumberHelper.format(totais[x - 1], NumberHelper.getLang(), true);
                        break;
                    case Types.DATE:
                        break;
                }
                if (val == null) {
                    val = "";
                }
                datatable.getDefaultCell().setHorizontalAlignment(aligns[x - 1]);
                phrase = new Phrase(val, FontFactory.getFont(FontFactory.HELVETICA, 9));
                datatable.addCell(phrase);
            }
        } catch (final DocumentException | SQLException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public void adicionaResumoTabela(PdfPTable datatable, List<TransferObject> lstNavegacao, int widths[], int aligns[], String titles[], String fields[], boolean totalizar) {
        try {
            datatable.getDefaultCell().setPadding(3);
            datatable.setWidths(widths);
            datatable.setWidthPercentage(100); // percentage

            datatable.getDefaultCell().setBorderWidth(1);
            lineBgColor = (lineBgColor == 1.1f) ? 0.9f : 1.1f;
            datatable.getDefaultCell().setGrayFill(lineBgColor);
            datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            Phrase phrase = new Phrase(ApplicationResourcesHelper.getMessage("rotulo.numero.registros", (AcessoSistema) null, String.valueOf(nro_registros)), FontFactory.getFont(FontFactory.HELVETICA, 9));
            datatable.addCell(phrase);

            final Iterator<TransferObject> it = lstNavegacao.iterator();
            final TransferObject cto = it.next();
            Object field = null;
            String val = null;
            for (int x = 1; x < titles.length; x++) {
                val = "";
                field = cto.getAttribute(fields[x]);
                if ((field != null) && totalizar) {
                    final Class<?> cl = field.getClass();
                    if (cl.equals(java.lang.Byte.class) || cl.equals(java.lang.Short.class) || cl.equals(java.lang.Integer.class) || cl.equals(java.lang.Long.class)) {
                        val = String.valueOf(Double.valueOf(totais[x]).intValue());
                    } else if (cl.equals(java.lang.Float.class) || cl.equals(java.lang.Double.class) || cl.equals(java.math.BigDecimal.class)) {
                        val = NumberHelper.format(totais[x], NumberHelper.getLang(), true);
                    }
                }
                datatable.getDefaultCell().setHorizontalAlignment(aligns[x]);
                phrase = new Phrase(val.toString(), FontFactory.getFont(FontFactory.HELVETICA, 9));
                datatable.addCell(phrase);
            }
        } catch (final DocumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}

/**
 * Your own page events.
 */

class MyPageEvents extends PdfPageEventHelper {

    public static final int HEADER_FONT_SIZE = 10;

    public static final int HEADER_LINE_SIZE = 16;

    // This is the contentbyte object of the writer
    PdfContentByte cb;

    // we will put the final number of pages in a template
    PdfTemplate template;

    // this is the BaseFont we are going to use for the header / footer
    BaseFont bf = null;

    private String titulos[] = null;

    private String texto_rodape_esquerda, texto_rodape_centro, texto_cabecalho_direita, texto_cabecalho_centro;

    public void setTitulos(String titulos[]) {
        this.titulos = titulos;
    }

    public void setTextoRodapeEsquerda(String texto) {
        texto_rodape_esquerda = texto;
    }

    public void setTextoRodapeCentro(String texto) {
        texto_rodape_centro = texto;
    }

    public void setTextoCabecalhoDireita(String texto) {
        texto_cabecalho_direita = texto;
    }

    public void setTextoCabecalhoCentro(String txt) {
        texto_cabecalho_centro = txt;
    }

    // we override the onOpenDocument method
    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            cb = writer.getDirectContent();
            template = cb.createTemplate(50, 50);
        } catch (final DocumentException | IOException ioe) {
        }
    }

    // we override the onEndPage method
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        final int pageN = writer.getPageNumber();
        final String text = ApplicationResourcesHelper.getMessage("rotulo.rodape.relatorio.pagina", (AcessoSistema) null, String.valueOf(pageN));
        int x;
        final int x_esquerda = 30, x_max = 800, y_rodape = 10, y_cabecalho = 570;

        if (texto_rodape_esquerda == null) {
            texto_rodape_esquerda = "";
        }
        cb.beginText();
        cb.setFontAndSize(bf, HEADER_FONT_SIZE);
        cb.setTextMatrix(x_esquerda, y_rodape);
        cb.showText(texto_rodape_esquerda);
        cb.endText();

        if (texto_rodape_centro == null) {
            texto_rodape_centro = "";
        }
        x = (x_max / 2) + x_esquerda;
        x -= bf.getWidthPoint(texto_rodape_centro, HEADER_FONT_SIZE) / 2;
        cb.beginText();
        cb.setFontAndSize(bf, HEADER_FONT_SIZE);
        cb.setTextMatrix(x, y_rodape);
        cb.showText(texto_rodape_centro);
        cb.endText();

        cb.beginText();
        cb.setFontAndSize(bf, HEADER_FONT_SIZE);
        cb.setTextMatrix(x_max - bf.getWidthPoint(text, HEADER_FONT_SIZE), y_rodape);
        cb.showText(text);
        cb.endText();
        cb.addTemplate(template, x_max, y_rodape);

        //  Insere o texto do cabecalho da esquerda do relatorio
        for (int i = 0; i < titulos.length; i++) {
            cb.beginText();
            cb.setFontAndSize(bf, HEADER_FONT_SIZE);
            cb.setTextMatrix(x_esquerda, y_cabecalho - (i * HEADER_LINE_SIZE));
            cb.showText(titulos[i]);
            cb.endText();
        }

        if (texto_cabecalho_direita == null) {
            texto_cabecalho_direita = "";
        }

        // Insere o texto do cabecalho da direita do relatorio
        cb.beginText();
        cb.setFontAndSize(bf, HEADER_FONT_SIZE);
        cb.setTextMatrix(x_max - bf.getWidthPoint(texto_cabecalho_direita, HEADER_FONT_SIZE), y_cabecalho);
        cb.showText(texto_cabecalho_direita);
        cb.endText();

        if ((texto_cabecalho_centro != null) && !texto_cabecalho_centro.equals("")) {
            // Insere o texto do cabecalho da centro do relatorio
            float x_cab = 0;
            int y_cab = 570;
            int endIndex = texto_cabecalho_centro.indexOf("\n");
            int tam = texto_cabecalho_centro.length();
            String linha = null;
            String restante = texto_cabecalho_centro;

            cb.beginText();
            cb.setFontAndSize(bf, HEADER_FONT_SIZE);

            while (endIndex != -1) {

                linha = restante.substring(0, endIndex);
                restante = restante.substring(endIndex + 1, tam);
                endIndex = restante.indexOf("\n");
                tam = restante.length();

                x_cab = ((x_max - (bf.getWidthPoint(linha, HEADER_FONT_SIZE))) / 2);
                cb.setTextMatrix(x_cab, y_cab);
                cb.showText(linha);
                y_cab = y_cab - 11;
            }
            cb.endText();
        }
    }

    // we override the onCloseDocument method
    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        template.beginText();
        template.setFontAndSize(bf, HEADER_FONT_SIZE);
        template.showText(String.valueOf(writer.getPageNumber() - 1));
        template.endText();
    }
}
