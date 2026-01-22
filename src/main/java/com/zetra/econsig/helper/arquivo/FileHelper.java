package com.zetra.econsig.helper.arquivo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringEscapeUtils;

import com.zetra.econsig.delegate.HistoricoArquivoDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ValidaImportacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.CharsetDetector;
import com.zetra.econsig.helper.texto.ConverteCharset;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.MimeDetector;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.folha.ValidaImportacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

import de.idyl.winzipaes.AesZipFileEncrypter;
import de.idyl.winzipaes.impl.AESEncrypter;
import de.idyl.winzipaes.impl.AESEncrypterBC;
import eu.medsea.mimeutil.MimeType;

/**
 * <p>Title: FileHelper</p>
 * <p>Description: Ajudante para manipulação de arquivos.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FileHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FileHelper.class);

    public static final List<String> OPEN_DOCUMENT_FORMATS = Arrays.asList(new String[]{".odt", ".ott", ".oth", ".odm", ".ods", ".ots", ".odp", ".odg", ".otp"});

    public static final int BUFFER_SIZE = 8192;

    public static void setHeader(String fileName, String text) throws IOException {
        replaceText(fileName, text, 0);
    }

    public static void replaceText(String fileName, String text, int pos) throws IOException {
        final RandomAccessFile randFile = new RandomAccessFile(fileName, "rw");
        randFile.seek(pos);
        randFile.writeBytes(text);
        randFile.close();
    }

    public static int getNumberOfLines(String fileName) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(fileName);
            final byte[] array = new byte[BUFFER_SIZE];
            int lines = 0;
            int count = 0;
            int last = 0;
            int total = 0;
            while ((count = in.read(array)) != -1) {
                for (int i = 0; i < count; i++) {
                    if (array[i] == '\r') {
                        lines++;
                    } else if (array[i] == '\n' && last != '\r') {
                        lines++;
                    }
                    last = array[i];
                    total++;
                }
            }
            return (total == 0) ? 0 : lines + 1;
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return -1;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException ex) {
            }
        }
    }

    public static String readFirstLine(String fileName) {
        BufferedReader in = null;
        String fisrtLine = null;
        try {
            in = new BufferedReader(new FileReader(fileName));
            fisrtLine = in.readLine();
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException ex) {
            }
        }
        return fisrtLine;
    }

    public static String readLastLine(String fileName) {
        try {
            final File file = new File(fileName);
            final RandomAccessFile randFile = new RandomAccessFile(file, "r");
            int i = 1;
            randFile.seek(file.length() - i);
            String lastLine = "";
            final String lineSeparator = System.lineSeparator();
            char c = (char) randFile.read();
            while (lineSeparator.indexOf(c) == -1 && i < randFile.length()) {
                i++;
                lastLine = c + lastLine;
                randFile.seek(randFile.length() - i);
                c = (char) randFile.read();
            }
            if (lineSeparator.indexOf(c) == -1 && randFile.length() == i) {
                lastLine = c + lastLine;
            }
            randFile.close();
            return lastLine;
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static String readAll(String fileName) {
        try {
            final BufferedReader in = new BufferedReader(new FileReader(fileName));
            final StringBuilder resultado = new StringBuilder();
            String linha = null;
            while ((linha = in.readLine()) != null) {
                resultado.append(linha).append(System.lineSeparator());
            }
            in.close();
            return resultado.toString();
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static String readAll(InputStream inputStream, String charset) {
        try {
            final ByteArrayOutputStream result = new ByteArrayOutputStream();
            final byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString(charset);
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static List<String> readAllToList(String fileName) {
        final List<String> retorno = new ArrayList<>();

        try {
            final BufferedReader in = new BufferedReader(new FileReader(fileName));
            new StringBuilder();
            String linha = null;
            while (!TextHelper.isNull((linha = in.readLine()))) {
                retorno.add(linha);
            }
            in.close();

        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return retorno;
    }

    public static void trimTextFile(String fileName) throws IOException {
        final File file = new File(fileName);
        if (file.exists()) {
            final RandomAccessFile randFile = new RandomAccessFile(file, "rw");
            final FileChannel channel = randFile.getChannel();

            while (true) {
                final long fileSize = randFile.length();
                if (fileSize <= 0) {
                    break;
                }

                int i = 1;
                char c = '\u0000';

                String lastLine = "";
                final String lineSeparator = System.lineSeparator();

                do {
                    randFile.seek(file.length() - i);
                    c = (char) randFile.read();

                    if (lineSeparator.indexOf(c) == -1) {
                        lastLine = c + lastLine;
                        i++;
                    }
                } while (lineSeparator.indexOf(c) == -1 && i <= fileSize);

                if (lastLine.trim().equals("")) {
                    channel.truncate(fileSize - i);
                } else {
                    break;
                }
            }
            channel.close();
            randFile.close();
        }
    }

    public static void truncate(String fileName, long cut) throws IOException {
        final File file = new File(fileName);
        final RandomAccessFile randFile = new RandomAccessFile(file, "rw");
        final FileChannel channel = randFile.getChannel();
        channel.truncate(file.length() - cut);
        channel.close();
        randFile.close();
    }

    /**
     * Converte as quebras de linha do arquivo para o padrão do sistema local.
     * Ex: Se a rotina for executada em uma máquina Unix, as quebras de linha ficarão no padrão Unix (LF);
     * se for executada em uma máquina Windows, ficarão no padrão Windows (CR+LF).
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static void convertLineBreaks(String fileName) throws IOException {
        final String tmpFileName = fileName + ".tmp";
        rewriteLineByLine(fileName, tmpFileName);
        final File file = new File(fileName);
        file.delete();
        final File tmpFile = new File(tmpFileName);
        tmpFile.renameTo(file);
    }

    /**
     * Converte charset.
     *
     * @param fileName
     * @return
     * @throws ZetraException
     */
    public static void convertCharset(File fileName) throws ZetraException {
        final ConverteCharset conversor = new ConverteCharset();
        conversor.converte(fileName, Charset.forName("ISO-8859-1"));
    }

    /**
     * Identifica Content-type.
     *
     * @param fileName
     * @return
     * @throws ZetraException
     */
    public static Set<MimeType> detectContentType(File fileName) throws ZetraException {
        final MimeDetector mimeDetector = MimeDetector.MIMEUTIL;
        try {
            return mimeDetector.detect(fileName.toString());
        } catch (final IOException e) {
            throw new ZetraException(e);
        }
    }

    /**
     * Valida extensão com relação ao Content-type.
     * Retorna true se a extensão esta corretamente associada ao content-type do arquivo.
     *
     * @param fileName
     * @return boolean
     * @throws ZetraException
     */
    public static Boolean validaExtensao(String fileName) throws ZetraException {
        final MimeDetector mimeDetector = MimeDetector.MIMEUTIL;
        try {
            return mimeDetector.validaExtensao(fileName);
        } catch (final IOException e) {
            throw new ZetraException(e);
        }
    }

    /**
     * Valida extensão recursivamente com relação ao Content-type e à lista de extensões indicada como permitidas.
     * Retorna true se as extensões estão corretamente associadas ao content-type para todos os arquivos do zip.
     *
     * @param fileNameZip
     * @return boolean
     * @throws ZetraException
     */
    public static Boolean validaExtensaoRecursivamente(String zipFileName, String[] extensoes) throws ZetraException {
        final File zipFile = new File(zipFileName);
        final File outputPath = new File(zipFileName + ".tmp");
        String padrao = null;
        Pattern p = null;
        if (extensoes != null) {
            final String[] extensoes_ = new String[extensoes.length];
            for (int i = 0; i < extensoes.length; i++) {
                extensoes_[i] = extensoes[i].replaceFirst("\\.", "");
            }
            padrao = ".*\\.(" + TextHelper.join(extensoes_, "|") + ")";
            p = Pattern.compile(padrao, Pattern.CASE_INSENSITIVE);
        }

        if (!zipFile.exists()) {
            return false;
        }

        try {
            // Se este método for chamado para arquivos diferente de zip, retorna a validação direta do arquivo.
            if (!isZip(zipFileName)) {
                if (p != null) {
                    final Matcher m = p.matcher(zipFileName);
                    if (!m.matches()) {
                        return false;
                    }
                }
                return validaExtensao(zipFileName);
            }

            // Extrai o conteúdo do arquivo Zip
            if (!outputPath.mkdir()) {
                throw new ZetraException("mensagem.erro.criar.diretorio.extracao.arquivo", (AcessoSistema) null, outputPath.getName());
            }
            final List<String> nomeArquivosConteudo = unZipAll(zipFileName, outputPath.getAbsolutePath());

            for (final String nome : nomeArquivosConteudo) {
                if (p != null) {
                    final Matcher m = p.matcher(nome);
                    if (!m.matches()) {
                        return false;
                    }
                }
                if (!validaExtensao(nome)) {
                    return false;
                }
            }
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erro.descompactacao.arquivo.entrada", (AcessoSistema) null, ex);
        } finally {
            try {
                // Apaga o conteúdo da pasta temporária
                if (outputPath.exists()) {
                    FileHelper.deleteDir(outputPath.getAbsolutePath());
                }
            } catch (final IOException ex) {
                throw new ZetraException("mensagem.erro.apagar.diretorio.temporario.extracao", (AcessoSistema) null, ex, outputPath.toString());
            }
        }
        return true;
    }

    /**
     * Reescreve o arquivo original linha a linha em um novo arquivo.
     *
     * @param fileName
     * @param newFileName
     * @throws IOException
     */
    public static void rewriteLineByLine(String fileName, String newFileName) throws IOException {
        PrintWriter out = null;
        String charsetFile = CharsetDetector.detect(fileName);
        if (charsetFile == null) {
            charsetFile = "ISO-8859-1".intern();
        }

        try {
            // Abro o arquivo de origem e destino usando o mesmo charset
            final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charsetFile));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFileName), charsetFile)));

            String line = null;
            while ((line = in.readLine()) != null) {
                out.println(line);
            }

            in.close();
            out.close();
        } catch (final IOException ex) {
            if (out != null) {
                out.close();
            }
            final File newFile = new File(newFileName);
            if (newFile.exists()) {
                newFile.delete();
            }
            throw ex;
        }
    }

    public static void rename(String fileName, String newFileName) {
        if (fileName != null && newFileName != null && !fileName.equals(newFileName)) {
            final File newFile = new File(newFileName);
            if (newFile.exists()) {
                newFile.delete();
            }
            final File file = new File(fileName);
            file.renameTo(new File(newFileName));
        }
    }

    public static boolean isZip(String fileName) throws FileNotFoundException, IOException {
        boolean zipado = false;

        // Recupera o contentType do arquivo
        final MimeDetector mimeDetector = MimeDetector.MIMEUTIL;
        final Set<MimeType> mime = mimeDetector.detect(fileName);

        if (mime.contains(new MimeType("application/zip")) &&
                !fileName.toLowerCase().endsWith(".xlsx") &&
                !fileName.toLowerCase().endsWith(".xls") &&
                !fileName.toLowerCase().endsWith(".docx") &&
                !fileName.toLowerCase().endsWith(".doc") &&
                (fileName.indexOf(".") != -1 && !OPEN_DOCUMENT_FORMATS.contains(fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase()))) {
            // Caso o mime type seja zip e a extensão do arquivo seja xlsx, docx ou OpenDocument formats; não é um arquivo zip
            zipado = true;
        }

        return zipado;
    }

    public static String unZip(String zipFileName, String outputPath) throws IOException {
        final Charset charset = Charset.forName(System.getProperty("file.encoding"));
        final ZipFile file = new ZipFile(zipFileName, charset);
        final Enumeration<?> entries = file.entries();
        String outFileName = null;
        while (entries.hasMoreElements()) {
            final ZipEntry entry = (ZipEntry) entries.nextElement();

            if (entry.isDirectory()) {
                continue;
            }

            outFileName = outputPath + File.separatorChar + getSafeZipEntryName(entry);

            final BufferedInputStream in = new BufferedInputStream(file.getInputStream(entry), BUFFER_SIZE);
            final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFileName), BUFFER_SIZE);

            int c;
            final String lineSeparator = System.lineSeparator();

            while ((c = in.read()) != -1) {
                if (c != '\r' && c != '\n') {
                    out.write(c);
                } else if (c == '\n') {
                    for (int i = 0; i < lineSeparator.length(); i++) {
                        out.write(lineSeparator.charAt(i));
                    }
                }
            }

            out.close();
            in.close();
        }

        file.close();
        return outFileName;
    }

    public static List<String[]> getZippedTextFileContent(String zipFileName, String delimiter) throws IOException {
        ZipFile zipFile = null;
        final List<String[]> content = new ArrayList<>();
        boolean headerAdded = false;
        try {
            zipFile = new ZipFile(zipFileName);
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                BufferedReader reader = null;
                boolean firstLine = true;
                try {
                    reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        final String[] values = line.split(delimiter);
                        if (values.length > 0) {
                            if (firstLine && !headerAdded) {
                                content.add(values);
                                headerAdded = true;
                            } else if (!firstLine) {
                                content.add(values);
                            }
                            firstLine = false;
                        }
                    }
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
        } finally {
            if (zipFile != null) {
                zipFile.close();
            }
        }

        return content;
    }

    public static byte[] gzipString(String plainContent) throws IOException {
        final ByteArrayOutputStream obj = new ByteArrayOutputStream();
        final GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(plainContent.getBytes("UTF-8"));
        gzip.close();
        return obj.toByteArray();
    }

    public static String gunzipString(byte[] gzipContent) throws IOException {
        final GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(gzipContent));
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        final byte buffer[] = new byte[BUFFER_SIZE];
        int len;

        while ((len = gzip.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        gzip.close();
        return out.toString();
    }

    public static List<String> unZipAll(String zipFileName, String outputPath) throws IOException {
        return unZipAll(zipFileName, outputPath, true);
    }

    /**
     * Descompacta o arquivo de entrada, verificando recursivamente se em seu conteúdo
     * existe arquivos Zip, e caso exista, realiza a descompactação destes também.
     *
     * @param zipFileName         : Nome do arquivo Zip a ser descompactado
     * @param outputPath          : Diretório onde serão gravados os arquivos que compõe o Zip
     * @param useSafeZipEntryName : se false não substitui espaços no nome do arquivo interno ao Zip
     * @return : A lista com o nome dos arquivos Zip
     * @throws IOException
     */
    public static List<String> unZipAll(String zipFileName, String outputPath, boolean useSafeZipEntryName) throws IOException {
        final List<String> result = new ArrayList<>();
        ZipFile zipFile = null;

        try {
            final Charset charset = Charset.forName(System.getProperty("file.encoding"));
            zipFile = new ZipFile(zipFileName, charset);
            final Enumeration<?> zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()) {
                final ZipEntry entry = (ZipEntry) zipEntries.nextElement();
                if (!entry.isDirectory()) {
                    final String outFileName = outputPath + File.separatorChar + getSafeZipEntryName(entry, useSafeZipEntryName);

                    BufferedInputStream in = null;
                    BufferedOutputStream out = null;

                    try {
                        in = new BufferedInputStream(zipFile.getInputStream(entry), BUFFER_SIZE);
                        out = new BufferedOutputStream(new FileOutputStream(outFileName), BUFFER_SIZE);

                        int c;
                        while ((c = in.read()) != -1) {
                            out.write(c);
                        }
                    } finally {
                        if (out != null) {
                            out.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    }

                    if (outFileName.toLowerCase().endsWith(".zip")) {
                        // Se tem uma entrada Zip, então descompacta seu conteúdo e
                        // remove a entrada Zip
                        result.addAll(unZipAll(outFileName, outputPath));
                        delete(outFileName);
                    } else {
                        result.add(outFileName);
                    }
                }
            }
        } finally {
            if (zipFile != null) {
                zipFile.close();
            }
        }

        return result;
    }

    private static String getSafeZipEntryName(ZipEntry entry) {
        return getSafeZipEntryName(entry, true);
    }

    private static String getSafeZipEntryName(ZipEntry entry, boolean replaceSpaces) {
        String entryName = StringEscapeUtils.unescapeJava(entry.getName());

        // Retorna apenas a parte final do nome sem diretórios (caso existam)
        entryName = entryName.substring(entryName.lastIndexOf('/') + 1);
        // Substitui espaços no nome dos arquivos (caso existam)
        if (replaceSpaces) {
            entryName = entryName.replaceAll("\\s", "_");
        }

        return entryName;
    }

    public static String zipAndRemove(String fileName) throws IOException {
        final String zipFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".zip";
        zip(fileName, zipFileName);
        delete(fileName);
        return zipFileName;
    }

    public static void zip(String fileName, String zipFileName) throws IOException {
        final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        final FileInputStream in = new FileInputStream(fileName);

        final String entryName = fileName.substring(fileName.lastIndexOf(File.separatorChar) + 1, fileName.length());
        out.putNextEntry(new ZipEntry(entryName));

        final byte buffer[] = new byte[BUFFER_SIZE];
        int len;

        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        out.closeEntry();
        in.close();

        out.close();
    }

    public static void zip(List<String> files, String zipFileName) throws IOException {
        final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));

        for (final String fileName : files) {
            final FileInputStream in = new FileInputStream(fileName);

            final String entryName = fileName.substring(fileName.lastIndexOf(File.separatorChar) + 1, fileName.length());
            out.putNextEntry(new ZipEntry(entryName));

            final byte buffer[] = new byte[BUFFER_SIZE];
            int len;

            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            out.closeEntry();
            in.close();

        }
        out.close();
    }

    /**
     * Compactar um diretório inteiro
     *
     * @param folder
     * @param zipFile
     * @throws IOException
     */
    public static void zipFolder(String srcFolder, String destZipFile) throws IOException {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;
        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);
        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();
    }

    private static void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws IOException {
        final File folder = new File(srcFile);
        if (folder.isDirectory()) {
            addFolderToZip(path, srcFile, zip);
        } else {
            final byte[] buf = new byte[1024];
            int len;
            try (FileInputStream in = new FileInputStream(srcFile)) {
                zip.putNextEntry(new ZipEntry(path + File.separator + folder.getName()));
                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }
            }
        }
    }

    static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws IOException {
        final File folder = new File(srcFolder);

        final Path dir = FileSystems.getDefault().getPath(srcFolder);
        final DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
        for (final Path streamPath : stream) {
            //for (String fileName : folder.list()) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + File.separator + streamPath.getFileName(), zip);
            } else {
                addFileToZip(path + File.separator + folder.getName(), srcFolder + File.separator + streamPath.getFileName(), zip);
            }
        }
        stream.close();
    }

    public static boolean copyFile(File in, File out) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(in);
            outputStream = new FileOutputStream(out);

            final FileChannel sourceChannel = inputStream.getChannel();
            final FileChannel destinationChannel = outputStream.getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);

            sourceChannel.close();
            destinationChannel.close();
            return true;
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }

    public static void delete(String fileName) throws IOException {
        final File file = new File(fileName);
        if (!file.delete()) {
            throw new IOException(ApplicationResourcesHelper.getMessage("mensagem.erro.remover.arquivo", (AcessoSistema) null, fileName));
        }
    }

    public static void deleteDir(String fileName) throws IOException {
        final File dir = new File(fileName);

        // Apaga o conteúdo do diretório
        deleteDirContent(dir);

        // Depois apaga o próprio diretório
        if (!dir.delete()) {
            throw new IOException(ApplicationResourcesHelper.getMessage("mensagem.erro.remover.diretorio", (AcessoSistema) null, fileName));
        }
    }

    public static void deleteDirContent(File dir) throws IOException {
        if (dir != null && dir.exists() && dir.isDirectory() && dir.canWrite()) {
            final File[] files = dir.listFiles();
            for (final File file : files) {
                if (file.isDirectory()) {
                    // Se for diretório, apaga seu conteúdo
                    deleteDirContent(file);
                    // Depois apaga o diretório vazio
                    if (!file.delete()) {
                        // Se não puder apagar, lança exceção
                        throw new IOException(ApplicationResourcesHelper.getMessage("mensagem.erro.remover.diretorio", (AcessoSistema) null, file.getName()));
                    }
                } else // Se for arquivo, realiza a remoção
                if (!file.delete()) {
                    // Se não puder apagar, lança exceção
                    throw new IOException(ApplicationResourcesHelper.getMessage("mensagem.erro.remover.arquivo", (AcessoSistema) null, file.getName()));
                }
            }
        }
    }

    public static long getDirSize(File dir) {
        long dirSize = 0;
        try {
            final File[] files = dir.listFiles();
            File file;
            for (final File file2 : files) {
                file = file2;
                if (file.isDirectory()) {
                    dirSize += getDirSize(file);
                } else {
                    dirSize += file.length();
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return dirSize;
    }

    public static void saveStreamToFile(InputStream in, String fileName) throws IOException {
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(fileName));
            final byte buffer[] = new byte[8192];
            int len;

            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static void saveByteArrayToFile(byte[] content, String fileName) throws IOException {
        saveStreamToFile(new ByteArrayInputStream(content), fileName);
    }

    public static void saveStringListToFile(List<String> lines, String fileName) throws IOException {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            for (final String line : lines) {
                out.println(line);
            }
        }
    }

    public static long calculateCheckSum(File file) {
        long checksum = 0;
        CheckedInputStream cis = null;
        try {
            // Compute Adler-32 checksum
            cis = new CheckedInputStream(new FileInputStream(file), new Adler32());
            final byte[] tempBuf = new byte[4096];
            while (cis.read(tempBuf) >= 0) {

            }
            checksum = cis.getChecksum().getValue();
        } catch (final IOException e) {
            checksum = 0;
        } finally {
            try {
                if (cis != null) {
                    cis.close();
                }
            } catch (final IOException ex) {
                //
            }
        }
        return checksum;
    }

    /**
     * Lista as partições do sistema de arquivo. Se possuir o arquivo
     * /etc/mtab considera que é um SO Linux e obtém a lista de partições
     * deste arquivo. Se não possuir, lista as raizes do sistema
     * operacional.
     *
     * @return
     */
    public static List<File> getFileSystemPartitions() {
        final List<File> partitions = new ArrayList<>();

        final File mtab = new File("/etc/mtab");
        if (mtab.exists() && mtab.canRead()) {
            try {
                final BufferedReader in = new BufferedReader(new FileReader(mtab));
                String line = null;
                while ((line = in.readLine()) != null) {
                    final String[] values = line.split(" ");
                    if (values.length > 1 && values[0].matches("(^[^,:]*:|)/.*")) {
                        partitions.add(new File(values[1]));
                    }
                }
                in.close();
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            final File[] roots = File.listRoots();
            partitions.addAll(Arrays.asList(roots));
        }

        return partitions;
    }

    /**
     * Recupera os nomes de todos os arquivos presentes em dirName e seus subdiretórios.
     *
     * @param dirName diretório base para listagem
     * @return lista contendo nome (não qualificado) dos arquivos presentes no diretório
     */
    public static List<String> getFilesInDir(String dirName) {
        return getFilesInDir(dirName, null);
    }

    /**
     * Recupera os nomes de todos os arquivos presentes em dirName e seus subdiretórios.
     *
     * @param dirName
     * @param fileFilter filtro de nomes válidos para o arquivo
     * @return
     */
    public static List<String> getFilesInDir(String dirName, FileFilter fileFilter) {
        final File diretorio = new File(dirName);
        final List<String> retornoList = new ArrayList<>();

        if (diretorio.exists()) {
            Object[] fileNameList = null;

            if (fileFilter == null) {
                fileNameList = diretorio.list();
            } else {
                final File[] lstFiles = diretorio.listFiles(fileFilter);
                final List<String> lstNames = new ArrayList<>();
                for (final File file : lstFiles) {
                    lstNames.add(file.getName());
                }

                fileNameList = lstNames.toArray();
            }

            for (final Object element : fileNameList) {
                final File fileSystemObject = new File(dirName + File.separatorChar + element);

                if (!fileSystemObject.isFile()) {
                    final List<String> subFolderList = getFilesInDir(fileSystemObject.getPath(), fileFilter);

                    for (final String fileName : subFolderList) {
                        retornoList.add(element.toString() + File.separatorChar + fileName.toString());
                    }
                } else {
                    retornoList.add(fileSystemObject.getName());
                }
            }
        }

        return retornoList;
    }

    /**
     * Executa a tarefa de remoção dos arquivos antigos do sistema.
     *
     * @param responsavel Responsável pela operação.
     */
    public static void executarLimpezaArquivosAntigos(AcessoSistema responsavel) {
        final String tpcExtensaoArq = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EXTENSAO_ARQUIVO_REMOVIVEL, responsavel);
        final String[] extensaoArq = !TextHelper.isNull(tpcExtensaoArq) ? TextHelper.split(tpcExtensaoArq, ";") : new String[] { ".txt", ".zip", ".ok" };
        final List<String> lstExtensaoArq = Arrays.asList(extensaoArq);

        final Map<String, Short> qtdDiasLimpezaMap = recuperaQtdDiasLimpeza(responsavel);
        if (!qtdDiasLimpezaMap.isEmpty()) {
            for (final String tarCodigo : qtdDiasLimpezaMap.keySet()) {
                final Short qtdDiasLimpeza = qtdDiasLimpezaMap.get(tarCodigo);
                if (qtdDiasLimpeza > 0) {
                    final TipoArquivoEnum tipoArquivoEnum = TipoArquivoEnum.recuperaTipoArquivo(tarCodigo);
                    excluiArquivosAntigos(recuperaDiretorio(tipoArquivoEnum, responsavel), qtdDiasLimpeza, lstExtensaoArq, responsavel);
                }
            }
        }
    }

    /**
     * cria arquivo ZIP com senha de acesso
     *
     * @param inFile   - arquivo a ser comprimido
     * @param outFile  - arquivo de saída
     * @param password - senha de acesso ao arquivo
     * @throws IOException
     */
    public static void zipAndEncrypt(File inFile, File outFile, String password) throws IOException {
        //implementação Bouncy Castle de criptografia AES
        final AESEncrypter encrypter = new AESEncrypterBC();
        final AesZipFileEncrypter enc = new AesZipFileEncrypter(outFile, encrypter);
        try {
            enc.add(inFile, inFile.getName(), password);
        } finally {
            enc.close();
        }
    }

    /**
     * Remove arquivos antigos do sistema de acordo com parâmetros passados.
     *
     * @param diretorio      Diretório a ser pesquisado.
     * @param diasLimpeza    Número de dias para manter os arquivos no diretório.
     * @param lstExtensaoArq Lista com as extensões permitidas para os arquivos removíveis.
     * @param responsavel    Responsável pela operação.
     */
    private static void excluiArquivosAntigos(String diretorio, Short diasLimpeza, List<String> lstExtensaoArq, AcessoSistema responsavel) {
        if (!TextHelper.isNull(diretorio)) {
            final File file = new File(diretorio);
            final String files[] = file.list();
            final Calendar cal = Calendar.getInstance();
            final Date now = cal.getTime();

            if (diretorio != null && files != null && diasLimpeza > 0) {
                for (final String pathname : files) {
                    final String caminhoCompleto = diretorio + pathname;
                    final File arquivo = new File(caminhoCompleto);
                    cal.setTimeInMillis(arquivo.lastModified());
                    final Date date = cal.getTime();

                    if (arquivo.isFile() && DateHelper.dayDiff(now, date) > diasLimpeza && possuiExtensaoArq(pathname, lstExtensaoArq)) {
                        try {
                            arquivo.delete();
                            LOG.debug("Exclusão do arquivo: ['" + arquivo.getAbsolutePath() + "'].");
                            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.ARQUIVO, Log.DELETE_FILE, Log.LOG_INFORMACAO);
                            logDelegate.add(ApplicationResourcesHelper.getMessage("rotulo.log.arquivo", responsavel, arquivo.getAbsolutePath()));
                            logDelegate.write();
                        } catch (final LogControllerException e) {
                            LOG.error("Não foi possível incluir log de exclusão do arquivo: ['" + arquivo.getAbsolutePath() + "'].", e);
                        }
                    } else if (arquivo.isDirectory()) {
                        excluiArquivosAntigos(caminhoCompleto + File.separatorChar, diasLimpeza, lstExtensaoArq, responsavel);
                    }
                }
            }
        }
    }

    /**
     * Recupera diretório em que os arquivos antigos podem ser removidos.
     *
     * @param tipoArquivoEnum Tipo do arquivo do diretório.
     * @param responsavel     Responsável pela operação.
     * @return Diretório a ser pesquisado.
     */
    public static String recuperaDiretorio(TipoArquivoEnum tipoArquivoEnum, AcessoSistema responsavel) {
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();

        switch (tipoArquivoEnum) {
            case ARQUIVO_BLOQUEIO_SERVIDOR:
                return absolutePath + File.separatorChar + "bloqueio_ser" + File.separatorChar;
            case ARQUIVO_CADASTRO_MARGENS:
                return absolutePath + File.separatorChar + "margem" + File.separatorChar;
            case ARQUIVO_CADASTRO_MARGEM_COMPLEMENTAR:
                return absolutePath + File.separatorChar + "margemcomplementar" + File.separatorChar;
            case ARQUIVO_CONCILIACAO:
                return absolutePath + File.separatorChar + "conciliacao" + File.separatorChar;
            case ARQUIVO_CRITICA:
                return absolutePath + File.separatorChar + "critica" + File.separatorChar;
            case ARQUIVO_LOTE:
                return absolutePath + File.separatorChar + "lote" + File.separatorChar;
            case ARQUIVO_MOVIMENTO_FINANCEIRO:
                return absolutePath + File.separatorChar + "movimento" + File.separatorChar;
            case ARQUIVO_REAJUSTE:
                return absolutePath + File.separatorChar + "reajuste" + File.separatorChar;
            case ARQUIVO_RETORNO_ATRASADO:
                return absolutePath + File.separatorChar + "retornoatrasado" + File.separatorChar;
            case ARQUIVO_RETORNO_INTEGRACAO:
                return absolutePath + File.separatorChar + "retorno" + File.separatorChar;
            case ARQUIVO_TRANSFERIDOS:
                return absolutePath + File.separatorChar + "transferidos" + File.separatorChar;
            case ARQUIVO_CONTRACHEQUES:
                return absolutePath + File.separatorChar + "contracheque" + File.separatorChar;
            case ARQUIVO_SENHAS_SERVIDORES:
                return absolutePath + File.separatorChar + "senhaservidores" + File.separatorChar;
            case ARQUIVO_ANEXO_AUTORIZACAO_GENERICO:
            case ARQUIVO_ANEXO_AUTORIZACAO_BOLETO:
            case ARQUIVO_ANEXO_AUTORIZACAO_DSD:
                return absolutePath + File.separatorChar + "anexo" + File.separatorChar;
            case ARQUIVO_ANEXO_CONTRATO:
                return absolutePath + File.separatorChar + "anexos_contrato" + File.separatorChar;
            default:
                return null;
        }
    }

    /**
     * Retorna o TipoArquivoEnum de acordo com o diretório de localização do arquivo
     *
     * @param diretorio
     * @param responsavel
     * @return
     */
    public static TipoArquivoEnum recuperaTipoArquivo(String diretorio, AcessoSistema responsavel) {
        if (diretorio.equals("bloqueio_ser")) {
            return TipoArquivoEnum.ARQUIVO_BLOQUEIO_SERVIDOR;
        } else if (diretorio.equals("margem")) {
            return TipoArquivoEnum.ARQUIVO_CADASTRO_MARGENS;
        } else if (diretorio.equals("margemcomplementar")) {
            return TipoArquivoEnum.ARQUIVO_CADASTRO_MARGEM_COMPLEMENTAR;
        } else if (diretorio.equals("conciliacao")) {
            return TipoArquivoEnum.ARQUIVO_CONCILIACAO;
        } else if (diretorio.equals("critica")) {
            return TipoArquivoEnum.ARQUIVO_CRITICA;
        } else if (diretorio.equals("lote")) {
            return TipoArquivoEnum.ARQUIVO_LOTE;
        } else if (diretorio.equals("movimento")) {
            return TipoArquivoEnum.ARQUIVO_MOVIMENTO_FINANCEIRO;
        } else if (diretorio.equals("reajuste")) {
            return TipoArquivoEnum.ARQUIVO_REAJUSTE;
        } else if (diretorio.equals("retornoatrasado")) {
            return TipoArquivoEnum.ARQUIVO_RETORNO_ATRASADO;
        } else if (diretorio.equals("retorno")) {
            return TipoArquivoEnum.ARQUIVO_RETORNO_INTEGRACAO;
        } else if (diretorio.equals("transferidos")) {
            return TipoArquivoEnum.ARQUIVO_TRANSFERIDOS;
        } else if (diretorio.equals("contracheque")) {
            return TipoArquivoEnum.ARQUIVO_CONTRACHEQUES;
        } else if (diretorio.equals("senhaservidores")) {
            return TipoArquivoEnum.ARQUIVO_SENHAS_SERVIDORES;
        } else if (diretorio.equals("anexo")) {
            return TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_GENERICO;
        }
        return null;
    }

    /**
     * Recupera dias de limpeza para cada tipo de arquivo.
     *
     * @param responsavel Responsável pela operação.
     * @return Map com os dados de código e quantidade de dias de limpeza.
     */
    private static Map<String, Short> recuperaQtdDiasLimpeza(AcessoSistema responsavel) {
        final Map<String, Short> qtdDiasLimpezaMap = new HashMap<>();
        try {
            final HistoricoArquivoDelegate har = new HistoricoArquivoDelegate();
            final List<TransferObject> tiposArquivo = har.lstTiposArquivo(responsavel);
            for (final TransferObject to : tiposArquivo) {
                final String tarCodigo = to.getAttribute(Columns.TAR_CODIGO).toString();
                final Short tarQtdDiasLimpeza = (Short) to.getAttribute(Columns.TAR_QTD_DIAS_LIMPEZA);
                qtdDiasLimpezaMap.put(tarCodigo, tarQtdDiasLimpeza);
            }
        } catch (final Exception ex) {
            LOG.error("Não foi possível recuperar os dados de tipo de arquivo.");
        }
        return qtdDiasLimpezaMap;
    }

    /**
     * Verifica se a extensão do arquivo está dentro da lista permitida.
     *
     * @param pathname       Nome O diretório do arquivo.
     * @param lstExtensaoArq Lista de extensões permitidas.
     * @return Verdadeiro se o arquivo possui a extensão. Falso, cc.
     */
    private static boolean possuiExtensaoArq(String pathname, List<String> lstExtensaoArq) {
        for (final String extensao : lstExtensaoArq) {
            if (pathname.toLowerCase().endsWith(extensao.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static int contaArquivosUpload(String tipoNew, String tipoOld, String path, AcessoSistema responsavel) throws ValidaImportacaoControllerException {

        final ValidaImportacaoController validaImportacaoController = ApplicationContextProvider.getApplicationContext().getBean(ValidaImportacaoController.class);
        final Map<String, String> paramValidacaoArq = validaImportacaoController.lstParamValidacaoArq(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), null, null, responsavel);
        final int diasIdadeMaximaArquivo = (!TextHelper.isNull(paramValidacaoArq.get(tipoNew + "." + "diasIdadeMaximaArquivo"))) ? Integer.valueOf(paramValidacaoArq.get(tipoNew + "." + "diasIdadeMaximaArquivo")) : 0;
        final String padraoNomeArquivo = (!TextHelper.isNull(paramValidacaoArq.get(tipoNew + "." + "padraoNomeArquivoBusca"))) ? paramValidacaoArq.get(tipoNew + "." + "padraoNomeArquivoBusca") : null;
        final String[] extensoes = (!TextHelper.isNull(paramValidacaoArq.get(tipoNew + "." + "extensoes"))) ? paramValidacaoArq.get(tipoNew + "." + "extensoes").split(",") : new String[]{"TXT", "ZIP"};

        final FileFilter filtroExtArq = pathname -> {
            // Verifica a idade máxima do arquivo
            if (diasIdadeMaximaArquivo == 0 || (DateHelper.dayDiff(new Date(pathname.lastModified())) <= diasIdadeMaximaArquivo)) {
                // Se tem a idade máxima correta, verifica o padrão de nome ou as extensões requeridas
                String padrao = "";
                if (!TextHelper.isNull(padraoNomeArquivo)) {
                    padrao = padraoNomeArquivo;
                } else {
                    // Constrói padrão baseado nas extensões permitidas
                    padrao = ".*\\.(" + TextHelper.join(extensoes, "|") + ")";
                }
                // Faz o casamento de padrão ignorando maiúsculas/minúsculas
                final Pattern p = Pattern.compile(padrao, Pattern.CASE_INSENSITIVE);
                final Matcher m = p.matcher(pathname.getName());
                return m.matches();
            }
            return false;
        };

        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final List<String> nameList = FileHelper.getFilesInDir(absolutePath + File.separatorChar + path.replace(tipoOld, tipoNew), filtroExtArq);

        final int totalCopiado = (nameList != null && !nameList.isEmpty()) ? nameList.size() : 1;

        return totalCopiado;
    }

    public static String prepararNomeArquivo(String name) {
        return !TextHelper.isNull(name) ? name.replaceAll("[^a-zA-Z0-9\\._\\-]+", "") : "";
    }

    /**
     * Realiza de contagem de quantos arquivos existe dentro de um Zip
     * @param zipFilePath
     * @return
     * @throws IOException
     */
    public static int contaArquivosZip(String zipFilePath) throws IOException {
        if (isZip(zipFilePath)) {
            final ZipFile zipFile = new ZipFile(zipFilePath);
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            int numRegularFiles = 0;
            while (entries.hasMoreElements()) {
                if (!entries.nextElement().isDirectory()) {
                    ++numRegularFiles;
                }
            }
            zipFile.close();
            return numRegularFiles;
        } else {
            return -1;
        }
    }

    public static String substituirDados(String msgn, CustomTransferObject cse, CustomTransferObject org, CustomTransferObject est) throws ViewHelperException {
        String padrao = "";
        String chave = "";
        String valor = "";

        if (cse != null) {
            for (final String element : cse.getAtributos().keySet()) {
                valor = "";
                chave = element;

                if (chave.indexOf(".") > 0) {
                    padrao = "<@" + chave.substring(chave.indexOf(".") + 1, chave.length()) + ">";
                } else {
                    padrao = "<@" + chave + ">";
                }
                if (padrao.equalsIgnoreCase("<@cse_identificador>")) {
                    valor = cse.getAttribute(chave) != null ? TextHelper.forHtmlContent(cse.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@cse_nome>")) {
                    valor = cse.getAttribute(chave) != null ? TextHelper.forHtmlContent(cse.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@cse_cnpj>")) {
                    valor = cse.getAttribute(chave) != null ? TextHelper.forHtmlContent(cse.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@cse_logradouro>")) {
                    valor = cse.getAttribute(chave) != null ? TextHelper.forHtmlContent(cse.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@cse_nro>")) {
                    valor = cse.getAttribute(chave) != null ? TextHelper.forHtmlContent(cse.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@cse_compl>")) {
                    valor = cse.getAttribute(chave) != null ? TextHelper.forHtmlContent(cse.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@cse_bairro>")) {
                    valor = cse.getAttribute(chave) != null ? TextHelper.forHtmlContent(cse.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@cse_cidade>")) {
                    valor = cse.getAttribute(chave) != null ? TextHelper.forHtmlContent(cse.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@cse_uf>")) {
                    valor = cse.getAttribute(chave) != null ? TextHelper.forHtmlContent(cse.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@cse_cep>")) {
                    valor = cse.getAttribute(chave) != null ? TextHelper.forHtmlContent(cse.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@cse_identificador_interno>")) {
                    valor = cse.getAttribute(chave) != null ? TextHelper.forHtmlContent(cse.getAttribute(chave).toString().trim()) : "";
                }
                msgn = msgn.replaceAll(padrao, Matcher.quoteReplacement(valor));
            }
        }

        if (org != null) {
            for (final String element : org.getAtributos().keySet()) {
                valor = "";
                chave = element;

                if (chave.indexOf(".") > 0) {
                    padrao = "<@" + chave.substring(chave.indexOf(".") + 1, chave.length()) + ">";
                } else {
                    padrao = "<@" + chave + ">";
                }
                if (padrao.equalsIgnoreCase("<@org_codigo>")) {
                    valor = org.getAttribute(chave) != null ? TextHelper.forHtmlContent(org.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@org_identificador>")) {
                    valor = org.getAttribute(chave) != null ? TextHelper.forHtmlContent(org.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@org_nome>")) {
                    valor = org.getAttribute(chave) != null ? TextHelper.forHtmlContent(org.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@org_logradouro>")) {
                    valor = org.getAttribute(chave) != null ? TextHelper.forHtmlContent(org.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@org_nro>")) {
                    valor = org.getAttribute(chave) != null ? TextHelper.forHtmlContent(org.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@org_compl>")) {
                    valor = org.getAttribute(chave) != null ? TextHelper.forHtmlContent(org.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@org_bairro>")) {
                    valor = org.getAttribute(chave) != null ? TextHelper.forHtmlContent(org.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@org_cidade>")) {
                    valor = org.getAttribute(chave) != null ? TextHelper.forHtmlContent(org.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@org_uf>")) {
                    valor = org.getAttribute(chave) != null ? TextHelper.forHtmlContent(org.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@org_cep>")) {
                    valor = org.getAttribute(chave) != null ? TextHelper.forHtmlContent(org.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@org_cnpj>")) {
                    valor = org.getAttribute(chave) != null ? TextHelper.forHtmlContent(org.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@org_nome_abrev>")) {
                    valor = org.getAttribute(chave) != null ? TextHelper.forHtmlContent(org.getAttribute(chave).toString().trim()) : "";
                }
                msgn = msgn.replaceAll(padrao, Matcher.quoteReplacement(valor));
            }
        }

        if (est != null) {
            for (final String element : est.getAtributos().keySet()) {
                valor = "";
                chave = element;

                if (chave.indexOf(".") > 0) {
                    padrao = "<@" + chave.substring(chave.indexOf(".") + 1, chave.length()) + ">";
                } else {
                    padrao = "<@" + chave + ">";
                }
                if (padrao.equalsIgnoreCase("<@est_codigo>")) {
                    valor = est.getAttribute(chave) != null ? TextHelper.forHtmlContent(est.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@est_identificador>")) {
                    valor = est.getAttribute(chave) != null ? TextHelper.forHtmlContent(est.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@est_nome>")) {
                    valor = est.getAttribute(chave) != null ? TextHelper.forHtmlContent(est.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@est_cnpj>")) {
                    valor = est.getAttribute(chave) != null ? TextHelper.forHtmlContent(est.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@est_logradouro>")) {
                    valor = est.getAttribute(chave) != null ? TextHelper.forHtmlContent(est.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@est_nro>")) {
                    valor = est.getAttribute(chave) != null ? TextHelper.forHtmlContent(est.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@est_compl>")) {
                    valor = est.getAttribute(chave) != null ? TextHelper.forHtmlContent(est.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@est_bairro>")) {
                    valor = est.getAttribute(chave) != null ? TextHelper.forHtmlContent(est.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@est_cidade>")) {
                    valor = est.getAttribute(chave) != null ? TextHelper.forHtmlContent(est.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@est_uf>")) {
                    valor = est.getAttribute(chave) != null ? TextHelper.forHtmlContent(est.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@est_cep>")) {
                    valor = est.getAttribute(chave) != null ? TextHelper.forHtmlContent(est.getAttribute(chave).toString().trim()) : "";
                } else if (padrao.equalsIgnoreCase("<@est_nome_abrev>")) {
                    valor = est.getAttribute(chave) != null ? TextHelper.forHtmlContent(est.getAttribute(chave).toString().trim()) : "";
                }
                msgn = msgn.replaceAll(padrao, Matcher.quoteReplacement(valor));
            }
        }

        String limpa = "";
        while (msgn.indexOf("<@") != -1) {
            limpa = msgn.substring(msgn.indexOf("<@"), msgn.indexOf(">", msgn.indexOf("<@")) + 1);
            msgn = msgn.replaceAll(limpa, "");
        }

        return msgn;
    }

    // This pattern allows alphanumeric, underscore, hyphen, and single dots
    // It disallows leading/trailing dots and multiple consecutive dots
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)*$");

    public static boolean isFilenameSafe(String filename) {
        if (filename == null || filename.isEmpty() || filename.length() > 255) {
            return false; // Basic length and null/empty check
        }
        String unescapedFilename = StringEscapeUtils.unescapeJava(filename);
        return SAFE_FILENAME_PATTERN.matcher(unescapedFilename).matches();
    }

    public static boolean isPathSafe(String path) {
        if (path == null || path.isEmpty() || path.length() > 4096) {
            return false; // Basic length and null/empty check
        }
        final String[] pathParts = path.split(File.separator);
        boolean allSafe = true;
        for (int i = 0; i < pathParts.length; i++) {
            allSafe &= (i == 0 && pathParts[i].isBlank()) || isFilenameSafe(pathParts[i]);
        }
        return allSafe;
    }
}
