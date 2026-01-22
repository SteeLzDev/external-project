package com.zetra.econsig.helper.texto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

import eu.medsea.mimeutil.MimeType;

public class ConverteCharset {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConverteCharset.class);

    /**
     * Converte charset de uma lista de arquivos para charset indicado, se necessário.
     * Salva o arquivo original com extensão .ARQ
     * @param arquivos : lista de arquivos a serem convertidos
     * @param charset : charset de destino
     * @param mintreshold : parâmetro para ajustar confidencialidade do detector (defaul: 0.20)
     * @return
     * @throws IOException
     * @throws ZetraException
     */

    public void converte(List<File> arquivos, Charset charset) throws ZetraException {
        final File[] arrayArquivos = arquivos.toArray(new File[arquivos.size()]);
        this.converte(arrayArquivos, charset);
    }

    public String converte(String string, String charset) throws IOException {
        final String charsetAtual = CharsetDetector.detectString(string);

        final CharsetDecoder charsetDecoder = Charset.forName(charsetAtual).newDecoder();
        charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        final CharsetEncoder charsetEncoder = Charset.forName(charset).newEncoder();
        charsetEncoder.onUnmappableCharacter(CodingErrorAction.REPLACE);

        final ByteBuffer byteBuffer = charsetEncoder.encode(CharBuffer.wrap(string));
        final CharBuffer charBuffer = charsetDecoder.decode(byteBuffer);

        return charBuffer.toString();
    }

    public void converte(File[] arquivos, Charset charset) throws ZetraException {
        try {
            for (final File arquivo : arquivos) {
                String nomeArquivo = null;
                nomeArquivo = arquivo.getAbsolutePath();

                if (arquivo.length() == 0) {
                    break;
                }

                final MimeDetector mimeDetector = MimeDetector.MIMEUTIL;
                final Set<MimeType> mime = mimeDetector.detect(nomeArquivo);
                if (mime == null || mime.size() != 1 || (!mime.toArray()[0].toString().contains("text/plain") && !mime.toArray()[0].toString().contains("application/zip"))) {
                    LOG.info("Content-Type: " + Arrays.toString(mime.toArray()) + ", abortando conversão de charset.");
                    break;
                }

                if (mime.toArray()[0].toString().contains("text/plain")) {
                    converte(arquivo, charset);
                } else {
                    converteConteudoZip(arquivo, charset);
                }
            }
        } catch (final Exception e) {
            throw new ZetraException(e);
        }

    }

    private void converteConteudoZip(File zipFile, Charset charset) throws ZetraException {
        try {
            final File tmpDir = new File(zipFile.getCanonicalFile() + ".tmp");

            // Valido se o arquivo de entrada existe.
            if (!zipFile.exists()) {
                throw new ZetraException("mensagem.erro.arquivo.zip.nao.existe", (AcessoSistema) null);
            }

            // Se este método for chamado para arquivos diferente de zip retorna falha.
            if (!FileHelper.isZip(zipFile.getCanonicalPath())){
                throw new ZetraException("mensagem.erro.arquivo.nao.zip", (AcessoSistema) null);
            }

            try {
                //FileHelper.unZipAll cria um diretório temporário com extensão .zip
                if (tmpDir.exists()) {
                    FileHelper.deleteDir(tmpDir.getCanonicalPath());
                }

                if (!tmpDir.mkdir()) {
                    throw new ZetraException("mensagem.erro.criar.diretorio.extracao.arquivo", (AcessoSistema) null, tmpDir.getName());
                }

                List<String> nomeArquivosConteudo = null;
                try {
                    nomeArquivosConteudo = FileHelper.unZipAll(zipFile.getCanonicalPath(), tmpDir.getAbsolutePath());
                } catch (final Exception e){
                    throw new ZetraException("mensagem.erro.conteudo.arquivo.invalido", (AcessoSistema) null);
                }

                for (final String nome : nomeArquivosConteudo) {
                    converte(new File(nome), charset);
                }
                zipFile.delete();
                FileHelper.zip(nomeArquivosConteudo, zipFile.getCanonicalPath());
            } finally {
                if (tmpDir.exists()) {
                    FileHelper.deleteDir(tmpDir.getCanonicalPath());
                }
            }

        } catch (final IOException e) {
            throw new ZetraException(e);
        }
    }

    public void converte(File arquivo, Charset charset) throws ZetraException {

        BufferedReader input = null;
        BufferedWriter output = null;

        try {
            String nomeArquivo = null;
            nomeArquivo = arquivo.getAbsolutePath();

            if (arquivo.length() == 0) {
                return;
            }

            final MimeDetector mimeDetector = MimeDetector.MIMEUTIL;
            final Set<MimeType> mime = mimeDetector.detect(nomeArquivo);
            if (mime == null || (!mime.contains(new MimeType("text/plain")) && !mime.contains(new MimeType("application/zip")))) {
                LOG.info("Content-Type: " + Arrays.toString(mime.toArray()) + ", abortando conversão de charset.");
                return;
            }

            if (mime.contains(new MimeType("application/zip")) && !nomeArquivo.toLowerCase().endsWith(".xlsx") && !nomeArquivo.toLowerCase().endsWith(".docx")) {
                LOG.info("Arquivo zip.");
                converteConteudoZip(arquivo, charset);
                return;
            }

            final String charsetFile = CharsetDetector.detect(nomeArquivo);
            if (charsetFile == null) {
                LOG.info("Charset desconhecido ou arquivo binário.");
                return;
            }

            if (charset.contains(Charset.forName(charsetFile))){
                LOG.info("O charset detectado "+charsetFile+" está contido no "+charset+", nenhuma conversão é necessária");
                return;
            }

            LOG.info("Detectado encoding = " + charsetFile + " para o arquivo " + nomeArquivo + ", convertendo para " + charset.displayName());

            FileHelper.rename(nomeArquivo, nomeArquivo + ".ARQCHARSET");
            final File novoArquivo = new File(nomeArquivo);

            input = new BufferedReader(new InputStreamReader(new FileInputStream(nomeArquivo + ".ARQCHARSET"), charsetFile));
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(novoArquivo),charset.name()));

            String line = null;
            // O objetivo de usar encode/decode é remover os caracteres não mapeáveis.
            // Isto substitui por ? os caracteres 128 até 159 do WINDOWS-1252, caso existam
            // ou qualquer outro não mapeável
            final CharsetDecoder charsetDecoder = Charset.forName(charset.name()).newDecoder();
            charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            final CharsetEncoder charsetEncoder = Charset.forName(charset.name()).newEncoder();
            charsetEncoder.onUnmappableCharacter(CodingErrorAction.REPLACE);

            try {
                // BOM marker will only appear on the very beginning
                input.mark(4);
                if ('\ufeff' != input.read()) {
                   input.reset(); // not the BOM marker
                }

                while ((line = input.readLine()) != null ) {
                // As strings são armazenadas no JAVA como UTF16, o processo consiste em converter para
                // o charset de destino, descartando caracteres não mapeáveis e voltar para o UTF16, antes de salvar
                final ByteBuffer byteBuffer = charsetEncoder.encode(CharBuffer.wrap(line));
                final CharBuffer charBuffer = charsetDecoder.decode(byteBuffer);

                // O descarte de caracteres inválidos insere nulls na linha, o processo abaixo os remove
                final ArrayList<Character> listChar = new ArrayList<>();
                    for (int i=0; i<charBuffer.length(); i++) {
                            if (charBuffer.charAt(i) != 0){
                                listChar.add(charBuffer.charAt(i));
                            } else {
                                    continue;
                            }
                    }

                    String result = "";
                    final Character[] arrayChar = listChar.toArray(new Character[listChar.size()]);
                    for (final Character c: arrayChar) {
                            result += c;
                    }

                    output.write(result);
                    output.newLine();
                }
            } finally {
                input.close();
                output.close();
            }
        } catch (final Exception e) {
            throw new ZetraException(e);
        }
    }
}
