package com.zetra.econsig.helper.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.consignacao.BoletoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: PDFHelper</p>
 * <p>Description: Auxiliar para arquivos PDF.</p>
 * <p>Copyright: Copyright (c) 2010-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Rodrigo Viana, Junio Goncalves, Fagner Luiz, Igor Lucas,
 *         Marcos Nolasco, Leonel Martins
 */
public final class PDFHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PDFHelper.class);

    // Sugerido pelo SonarLint
    private PDFHelper() {
    }

    /**
     * Concatena uma lista de PDFs em um único arquivo.
     * @param nomesArquivosOrigem
     * @param nomeArquivoDestino
     * @param responsavel
     */
    public static void concatenarPDFs(List<String> nomesArquivosOrigem, String nomeArquivoDestino, AcessoSistema responsavel) throws ZetraException {
        Document document = new Document();
        document.setPageSize(PageSize.A4.rotate());

        OutputStream file = null;
        PdfCopyFields pdfCopyFields = null;
        try {
            file = new FileOutputStream(new File(nomeArquivoDestino + ".tmp"));
            pdfCopyFields = new PdfCopyFields(file);

            document.open();
            for (final String nomeArquivo : nomesArquivosOrigem) {
                PdfReader pdfReader = new PdfReader(new FileInputStream(nomeArquivo));
                pdfCopyFields.addDocument(pdfReader);
                pdfReader.close();
            }
            file.flush();
            pdfCopyFields.close();
            document.close();
            file.close();

            // mark as closed
            file = null;
            pdfCopyFields = null;
            document = null;

            final PdfReader pdfReader = new PdfReader(nomeArquivoDestino + ".tmp");
            final PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(nomeArquivoDestino));
            final BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            final int pages = pdfReader.getNumberOfPages();
            for (int i = 1; i <= pages; i++) {
                final PdfContentByte pageContentByte = pdfStamper.getOverContent(i);
                pageContentByte.beginText();
                pageContentByte.setFontAndSize(baseFont, 9);
                pageContentByte.showTextAligned(PdfContentByte.ALIGN_CENTER, ApplicationResourcesHelper.getMessage("rotulo.pagina.de.total.paginas", responsavel, String.valueOf(i), String.valueOf(pages)), 720, 28, 0);
                pageContentByte.endText();
            }
            pdfStamper.close();

            // Remove o arquivo temporário utilizado
            FileHelper.delete(nomeArquivoDestino + ".tmp");

        } catch (final Exception e) {
            throw new ZetraException("mensagem.erro.concatenar.arquivos", responsavel, e);
        } finally {
            if (pdfCopyFields != null) {
                pdfCopyFields.close();
            }
            if (document != null) {
                document.close();
            }
            if (file != null) {
                try {
                    file.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ZetraException("mensagem.erro.interno.sistema.motivo.arg0", responsavel, ex.getMessage());
                }
            }
        }
    }

    /**
     * Gera um PDF único contendo todos os arquivos anexos de uma ade
     * @param adeCodigo
     * @param nomesArquivosOrigem - lista de arquivos (caminho absoluto) a serem incluídos no PDF de saída
     * @param nomeArquivoDestino - nome do PDF de saída
     * @param responsavel
     * @return - caminho absoluto ao nome do arquivo PDF de saída
     * @throws ZetraException
     */
    public static String gerarPDFAssinaturaDigital(String adeCodigo, List<String> nomesArquivosOrigem, String nomeArquivoDestino, AcessoSistema responsavel) throws ZetraException {
        final String hoje = DateHelper.format(DateHelper.getSystemDate(), "yyyyMMdd");
        final String dirRaiz = ParamSist.getDiretorioRaizArquivos();
        final String arquivoAAssinar = dirRaiz + File.separatorChar + "anexo" +
                File.separatorChar + "aAssinar" + File.separatorChar + adeCodigo + File.separatorChar + hoje + File.separatorChar +
                nomeArquivoDestino;

        final File aAssinarDir = new File(dirRaiz + File.separatorChar + "anexo" + File.separatorChar + "aAssinar");
        if (!aAssinarDir.exists() && !aAssinarDir.mkdirs()) {
            throw new ViewHelperException("mensagem.erro.criacao.diretorio", responsavel, aAssinarDir.getAbsolutePath());
        }

        final File adeCodigoDir = new File(dirRaiz + File.separatorChar + "anexo" + File.separatorChar + "aAssinar" + File.separatorChar + adeCodigo);
        if (!adeCodigoDir.exists() && !adeCodigoDir.mkdirs()) {
            throw new ViewHelperException("mensagem.erro.criacao.diretorio", responsavel, adeCodigoDir.getAbsolutePath());
        }

        final File hojeDir = new File(dirRaiz + File.separatorChar + "anexo" + File.separatorChar + "aAssinar" + File.separatorChar + adeCodigo + File.separatorChar + hoje);
        if (!hojeDir.exists() && !hojeDir.mkdirs()) {
            throw new ViewHelperException("mensagem.erro.criacao.diretorio", responsavel, hojeDir.getAbsolutePath());
        }

        final Document document = new Document();
        document.setPageSize(PageSize.A4.rotate());

        OutputStream file = null;
        PdfWriter writer = null;
        try {
            file = new FileOutputStream(new File(arquivoAAssinar));

            writer = PdfWriter.getInstance(document, file);
            final Paragraph paragrafo = new Paragraph(" ");
            document.open();
            document.add(paragrafo);

            final String boleto = BoletoHelper.gerarTextoBoleto(adeCodigo, false, AcessoSistema.getAcessoUsuarioSistema());
            final String absolutePath = ParamSist.getDiretorioRaizArquivos() +
                    File.separatorChar + "boleto" + File.separatorChar + "imgs" + File.separatorChar;
            String newBoleto = null;
            if (boleto.contains("../img/view.jsp?nome=img/")) {
                newBoleto = boleto.replace("../img/view.jsp?nome=img/", absolutePath);
            } else {
                newBoleto =  boleto;
            }
            addHTMLToPDF(document, newBoleto);

            for (final String fileName: nomesArquivosOrigem) {
                if (fileName.endsWith(".pdf")) {
                    addPDFToCurrentPDF(document, fileName, writer, false, responsavel);
                } else {
                    addImageToPDF(document, fileName);
                }
            }

            return arquivoAAssinar;

        } catch (final FileNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.arquivo.nao.encontrado", responsavel);
        } catch (final DocumentException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erro.interno.sistema.motivo.arg0", responsavel, ex.getMessage());
        } finally {
            document.close();

            if (writer != null) {
                writer.flush();
                writer.close();
            }
            if (file != null) {
                try {
                    file.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ZetraException("mensagem.erro.interno.sistema.motivo.arg0", responsavel, ex.getMessage());
                }
            }
        }

    }

    public static void addHTMLToPDF(Document documentoDestino, String html) throws IOException {
        try (final HTMLWorker htmlWorker = new HTMLWorker(documentoDestino)) {
            htmlWorker.parse(new StringReader(html));
        }
    }

    public static void addImageToPDF(Document documentoDestino, String imgFileName) throws MalformedURLException, IOException, DocumentException {
        final Image img = Image.getInstance(imgFileName);
        documentoDestino.newPage();
        documentoDestino.add(img);
    }

    private static void addPDFToCurrentPDF(Document documentoDestino, String nomePdf, PdfWriter writer, boolean paginar, AcessoSistema responsavel) throws FileNotFoundException, IOException, DocumentException {
        final InputStream pdf = new FileInputStream(nomePdf);
        final PdfReader pdfReader = new PdfReader(pdf);
        final BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        final PdfContentByte contentByte = writer.getDirectContent();
        final int qtdeTotalPaginas = pdfReader.getNumberOfPages();

        // Cria uma nova página no arquivo de destino para cada página de origem
        for (int currentPageNumber = 1; currentPageNumber <= qtdeTotalPaginas; currentPageNumber++) {
            documentoDestino.newPage();

            // faz um wrapper das páginas para imagem, pois esta classe possui melhor API para manipular escalas do PDF
            final PdfImportedPage imPage = writer.getImportedPage(pdfReader, currentPageNumber);
            final Image page = Image.getInstance(imPage);
            page.setAbsolutePosition(200, 0);
            // escala 70% foi o que melhor enquadrou uma página "retrato" no formato "paisagem"
            page.scalePercent(0.70f * 100, 0.70f * 100);

            documentoDestino.add(page);

            // Code for pagination.
            if (paginar) {
                contentByte.beginText();
                contentByte.setFontAndSize(baseFont, 9);
                contentByte.showTextAligned(PdfContentByte.ALIGN_CENTER, ApplicationResourcesHelper.getMessage("rotulo.pagina.de.total.paginas", responsavel, String.valueOf(currentPageNumber), String.valueOf(qtdeTotalPaginas)), 720, 28, 0);
                contentByte.endText();
            }
        }

        pdfReader.close();
        pdf.close();
    }

    public static String convertPDFToBase64(String pdfPath) throws IOException {
        File file = new File(pdfPath);
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[(int) file.length()];
        fis.read(byteArray);
        fis.close();
        return "data:application/pdf;base64," + Base64.getEncoder().encodeToString(byteArray);
    }
}
