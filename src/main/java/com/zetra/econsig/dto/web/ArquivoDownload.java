package com.zetra.econsig.dto.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ArquivoDownload</p>
 * <p>Description: POJO para listagem de arquivos para download.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoDownload {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ArquivoDownload.class);

    private File arquivo;
    private String nome;
    private String descricao;
    private String tamanho;
    private String data;
    private String entidade;

    public ArquivoDownload(File arquivo, String nome, String tamanho, String data, String entidade) {
        super();
        this.arquivo = arquivo;
        this.nome = nome;
        this.tamanho = tamanho;
        this.data = data;
        this.entidade = entidade;
    }

    public ArquivoDownload(File arquivo, String nome, String tamanho, String data, String entidade, String descricao) {
        super();
        this.arquivo = arquivo;
        this.nome = nome;
        this.descricao = descricao;
        this.tamanho = tamanho;
        this.data = data;
        this.entidade = entidade;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEntidade() {
        return entidade;
    }

    public void setEntidade(String entidade) {
        this.entidade = entidade;
    }

    public String getFormatoImagem() {
        String formato = "";
        String nomeOriginal = getNomeOriginal();

        if (nomeOriginal.toLowerCase().endsWith(".txt")) {
            formato = "text.gif";
        } else if (nomeOriginal.toLowerCase().endsWith(".zip")) {
            formato = "zip.gif";
        } else if (nomeOriginal.toLowerCase().endsWith(".pdf")) {
            formato = "pdf.gif";
        } else {
            formato = "help.gif";
        }

        return formato;
    }

    public String getConversorImagem() {
        String conversor = null;
        String nomeOriginal = getNomeOriginal();

        if (nomeOriginal.toLowerCase().endsWith(".txt")) {
            conversor = "zip.gif";
        } else if (nomeOriginal.toLowerCase().endsWith(".zip")) {
            conversor = "text.gif";
        }

        return conversor;
    }

    public String getConversorTexto() {
        String conversor = null;
        String nomeOriginal = getNomeOriginal();

        if (nomeOriginal.toLowerCase().endsWith(".txt")) {
            conversor = ApplicationResourcesHelper.getMessage("rotulo.acoes.converter.para.zip", AcessoSistema.getAcessoUsuarioSistema());
        } else if (nomeOriginal.toLowerCase().endsWith(".zip")) {
            conversor = ApplicationResourcesHelper.getMessage("rotulo.acoes.converter.para.txt", AcessoSistema.getAcessoUsuarioSistema());
        }

        return conversor;
    }

    public String getNomeOriginal() {
        return getArquivo().getName().replaceAll("\\.crypt", "");
    }

    @Override
    public String toString() {
        return getNomeOriginal();
    }

    public static List<ArquivoDownload> carregarArquivos(List<File> arquivos, String diretorioArquivos, Map<String, TransferObject> orgaos, AcessoSistema responsavel) {
        return carregarArquivos(arquivos, diretorioArquivos, orgaos, 0, Integer.MAX_VALUE, responsavel);
    }

    public static List<ArquivoDownload> carregarArquivos(List<File> arquivos, String diretorioArquivos, Map<String, TransferObject> orgaos, int offset, int size, AcessoSistema responsavel) {
        List<ArquivoDownload> resultado = new ArrayList<ArquivoDownload>();

        if (arquivos != null && !arquivos.isEmpty()) {
            for (int i = offset; i < Math.min(offset + size, arquivos.size()); i++) {
                File arquivo = arquivos.get(i);
                String tam = "";
                if (arquivo.length() > 1024.00) {
                    tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                } else {
                    tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                }
                String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
                String nome = arquivo.getPath().substring(diretorioArquivos.length() + (diretorioArquivos.charAt(diretorioArquivos.length() - 1) == File.separatorChar ? 0 : 1));

                String identificadorEntidade = "";

                if (orgaos != null) {
                    String[] partesNomeArq = nome.split(File.separator);
                    String codigoOrgao = null;
                    if (partesNomeArq[0].equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) && partesNomeArq.length == 3) {
                        // cse/org_codigo/nomeArq.txt
                        codigoOrgao = partesNomeArq[1];
                    } else if (!partesNomeArq[0].equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) &&
                            !partesNomeArq[0].equalsIgnoreCase(AcessoSistema.ENTIDADE_EST) && partesNomeArq.length == 2) {
                        // org_codigo/nomeArq.txt
                        codigoOrgao = partesNomeArq[0];
                    }

                    TransferObject orgao = orgaos.get(codigoOrgao);
                    if (orgao != null) {
                        String orgIdentificador = orgao.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                        String estIdentificador = orgao.getAttribute(Columns.EST_IDENTIFICADOR).toString();

                        if (!TextHelper.isNull(orgIdentificador)) {
                            identificadorEntidade = orgIdentificador + " - " + estIdentificador.toUpperCase();
                        } else if (!TextHelper.isNull(estIdentificador)) {
                            identificadorEntidade = estIdentificador.toUpperCase();
                        }
                    }
                }

                try {
                    nome = java.net.URLEncoder.encode(nome, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                resultado.add(new ArquivoDownload(arquivo, nome, tam, data, identificadorEntidade));
            }
        }

        return resultado;
    }
}
