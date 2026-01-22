package com.zetra.econsig.parser;

import java.io.File;
import java.io.IOException;

import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.parser.febraban.Analisador;

/**
 * <p>Title: LeitorArquivoFebraban</p>
 * <p>Description: Leitor de arquivos do padrão Febraban. Aceita como arquivos
 * de entrada tanto arquivos TXT como arquivos ZIP</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor e Leonel
 * $Author$
 * $Revision$
 * $Date$
 */
public class LeitorArquivoFebraban extends LeitorArquivoTexto {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LeitorArquivoFebraban.class);

    private final boolean validarArquivo;
    private final String nomeArquivoOriginal;

    // Guardam as linhas de Header e Footer lidas
    private String linhaHeaderArquivo;
    private String linhaFooterArquivo;
    private String linhaHeaderLoteAtual;
    private String linhaFooterLoteAtual;

    public LeitorArquivoFebraban(String nomeArqConfig, String nomeArqEntrada) {
        this(nomeArqConfig, nomeArqEntrada, true);
    }

    public LeitorArquivoFebraban(String nomeArqConfig, String nomeArqEntrada, boolean validarArquivo) {
        super(nomeArqConfig, nomeArqEntrada);
        nomeArquivoOriginal = nomeArqEntrada;
        this.validarArquivo = validarArquivo;
    }

    @Override
    public void iniciaLeitura() throws ParserException {
        // Se for um arquivo compactado, então descompacta o arquivo antes de começar
        if (nomeArquivo.toLowerCase().endsWith(".zip")|| nomeArquivo.toLowerCase().endsWith(".zip.prc")) {
            try {
                String path = nomeArquivo.substring(0, nomeArquivo.lastIndexOf(File.separatorChar));
                nomeArquivo = FileHelper.unZip(nomeArquivo, path);
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ParserException("mensagem.erro.leitor.arquivo.falha.descompactacao.arquivo", (AcessoSistema) null, ex);
            }
        }

        // Remove os vazios no final do arquivo
        try {
            FileHelper.trimTextFile(nomeArquivo);
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParserException("mensagem.erro.leitor.arquivo.falha.processamento", (AcessoSistema) null, ex, nomeArquivo);
        }

        if (validarArquivo) {
            // Executar Rotina de validação do padrão febraban
            Analisador analisador = new Analisador();
            String nomeArquivoCritica = analisador.executar(nomeArquivo);

            if (nomeArquivoCritica == null) {
                throw new ParserException("mensagem.erro.leitor.arquivo.falha.processamento", (AcessoSistema) null, nomeArquivo);
            }

            // Se o arquivo de entrada é um arquivo compactado, então remove
            // o arquivo descompactado, pois o arquivo a ser utilizado agora
            // será o resultado do Analisador
            if (nomeArquivoOriginal.toLowerCase().endsWith(".zip")) {
                File temporario = new File(nomeArquivo);
                temporario.delete();
            }

            // Muda o nome do arquivo de entrada para obter o arquivo já criticado
            nomeArquivo = nomeArquivoCritica;
        }

        // Invoca a rotina do leitor de arquivo texto
        super.iniciaLeitura();
    }

    @Override
    public void encerraLeitura() throws ParserException {
        // Fecha o stream de leitura do arquivo
        super.encerraLeitura();

        if (validarArquivo) {
            // Remove o arquivo gerado pelo Analisador
            File temporarioAnalisador = new File(nomeArquivo);
            temporarioAnalisador.delete();
        } else if (nomeArquivoOriginal.toLowerCase().endsWith(".zip")) {
            // Remove o arquivo descompactado, caso o original seja um ZIP
            File temporario = new File(nomeArquivo);
            temporario.delete();
        }
    }

    @Override
    protected void leArquivo() {
        // Lê a proxima linha do arquivo de entrada
        super.leArquivo();

        if (linha != null) {
            // Verifica de acordo com o tipo de registro, se alguma ação deve ser realizada
            switch (linha.charAt(Analisador.POSICAO_TIPO_REGISTRO)) {
                case '0': // HEADER DE ARQUIVO

                    /**@todo TROCAR CÓDIGO ABAIXO, POR ESCRITOR DE LOTE, PARA TRADUZIR O ARQUIVO DE ENTRADA PARA PADRÃO ESPECÍFICO POR CSA */

                    ////////////////////////////////////////////////////////////////////////////////////////////////
                    // Altera os campos específicos do BB(PR) para integração
                    // Código Remessa / Retorno
                    linha = linha.substring(0, 142) + "1" + linha.substring(143);
                    // Para Uso Reservado do Banco
                    linha = linha.substring(0, 171)
                               + (linha.substring(171, 177).equals("CDC241") ? "CDC240" : linha.substring(171, 177))
                               + linha.substring(177);
                    ////////////////////////////////////////////////////////////////////////////////////////////////

                    // Guarda a linha de header
                    linhaHeaderArquivo = linha;

                    // Interpreta os valores do header e adiciona os dados no cache de valores do header
                    lerDadosHeaderArquivo();

                    // Lê a proxima linha e termina
                    leArquivo();
                    break;

                case '1': // HEADER DE LOTE

                    // Guarda a linha de header
                    linhaHeaderLoteAtual = linha;

                    // Interpreta os valores do header e adiciona os dados no cache de valores do header
                    lerDadosHeaderLote();

                    // Lê a proxima linha e termina
                    leArquivo();
                    break;

                case '5': // TRAILER DE LOTE

                    // Guarda a linha de footer
                    linhaFooterLoteAtual = linha;

                    // Lê a próxima linha e termina
                    leArquivo();
                    break;

                case '9': // TRAILER DE ARQUIVO

                    // Guarda a linha de footer
                    linhaFooterArquivo = linha;

                    // Lê a próxima linha e termina
                    leArquivo();
                    break;
            }
        }
    }

    private void lerDadosHeaderArquivo() {
        // HEADER DE ARQUIVO
    }

    private void lerDadosHeaderLote() {
        try {
            // HEADER DE LOTE
            valoresHeader.putAll(formataEntrada(doc.getHeader().getAtributo(), linha));
        } catch (ParserException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    // GET'S E SET'S

    public String getLinhaFooterArquivo() {
        return linhaFooterArquivo;
    }

    public String getLinhaFooterLoteAtual() {
        return linhaFooterLoteAtual;
    }

    public String getLinhaHeaderArquivo() {
        return linhaHeaderArquivo;
    }

    public String getLinhaHeaderLoteAtual() {
        return linhaHeaderLoteAtual;
    }

    public void setLinhaHeaderLoteAtual(String linhaHeaderLoteAtual) {
        this.linhaHeaderLoteAtual = linhaHeaderLoteAtual;
    }

    public void setLinhaHeaderArquivo(String linhaHeaderArquivo) {
        this.linhaHeaderArquivo = linhaHeaderArquivo;
    }

    public void setLinhaFooterLoteAtual(String linhaFooterLoteAtual) {
        this.linhaFooterLoteAtual = linhaFooterLoteAtual;
    }

    public void setLinhaFooterArquivo(String linhaFooterArquivo) {
        this.linhaFooterArquivo = linhaFooterArquivo;
    }

    // Métodos presentes na classe pai

    @Override
    public String getLinhaFooter() {
        return linhaFooterArquivo;
    }

    @Override
    public String getLinhaHeader() {
        return linhaHeaderArquivo;
    }
}
