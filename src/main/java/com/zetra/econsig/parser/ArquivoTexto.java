package com.zetra.econsig.parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.zetra.econsig.helper.xml.XmlHelper;
import com.zetra.econsig.parser.config.DocumentoTipo;
import com.zetra.econsig.parser.config.HeaderTipo;
import com.zetra.econsig.parser.config.ParametroTipo;

/**
 * <p>Title: ArquivoTexto</p>
 * <p>Description: Definição de um ArquivoTexto.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoTexto {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ArquivoTexto.class);

    protected String nomeArquivo;
    protected DocumentoTipo doc;

    // Parametros disponíveis no XML de configuração
    protected String delimitador; // Delimitador de campos
    protected String ordem; // Ordenação dos dados de arquivo de saida
    protected int limite; // Limite de linhas de arquivo de entrada
    protected int larguraMax; // Numero de caracteres Máximos permitidos para a linha de entrada
    protected int larguraMin; // Numero de caracteres Mínimos permitidos para a linha de entrada
    protected String comentario; // Marcador de comentário das linhas de entrada
    protected String comentario_regex; // Marcador de comentário das linhas de entrada
    protected String sufixo; // Sufixo do arquivo de saída
    protected int linhas_por_registro; //Quantas linhas representam um registro de entrada

    protected boolean temHeader;
    protected boolean temFooter;
    protected int tipoHeader;
    protected int tipoFooter;

    protected List<HeaderTipo> headers;
    protected List<HeaderTipo> footers;

    // Tipos de Header e Footer
    public static final int DELIMITADO_TOTAL = 0;
    public static final int POSICIONAL_TOTAL = 1;
    public static final int DELIMITADO_LOTE = 2;
    public static final int POSICIONAL_LOTE = 3;

    public ArquivoTexto(String nomearq) {
        limite = -1;
        larguraMax = -1;
        larguraMin = -1;
        ordem = null;
        comentario = null;
        comentario_regex = null;
        delimitador = null;
        linhas_por_registro = 1;

        nomeArquivo = nomearq;
        temHeader = false;
        temFooter = false;

        headers = new ArrayList<>();
        footers = new ArrayList<>();
        sufixo = "formatado";
    }

    public ArquivoTexto(String nomearqconfig, String nomearq) {
        this(nomearq);

        try {
            doc = XmlHelper.unmarshal(nomearqconfig);
        } catch (ParserException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        tipoHeader = -1;
        if (doc.getHeader() != null) {
            temHeader = true;
            tipoHeader = doc.getHeader().getTipo();
        } else if (doc.getHeaders() != null) {
            headers = doc.getHeaders().getHeader();
        }

        tipoFooter = -1;
        if (doc.getFooter() != null) {
            temFooter = true;
            tipoFooter = doc.getFooter().getTipo();
        } else if (doc.getFooters() != null) {
            footers = doc.getFooters().getFooter();
        }

        if (doc.getParametro() != null) {
            int limite_noturno = -1;

            // Faz o tratamento dos parametros, caso exista algum
            Iterator<ParametroTipo> it = doc.getParametro().iterator();
            while (it.hasNext()) {
                ParametroTipo param = it.next();
                if (param.getNome().equalsIgnoreCase("delimitador")) {
                    delimitador = param.getValor();
                } else if (param.getNome().equalsIgnoreCase("ordem")) {
                    ordem = param.getValor();
                } else if (param.getNome().equalsIgnoreCase("limite")) {
                    limite = Integer.parseInt(param.getValor());
                } else if (param.getNome().equalsIgnoreCase("limite_noturno")) {
                    limite_noturno = Integer.parseInt(param.getValor());
                } else if (param.getNome().equalsIgnoreCase("larguramax")) {
                    larguraMax = Integer.parseInt(param.getValor());
                } else if (param.getNome().equalsIgnoreCase("larguramin")) {
                    larguraMin = Integer.parseInt(param.getValor());
                } else if (param.getNome().equalsIgnoreCase("comentario")) {
                    comentario = param.getValor();
                } else if (param.getNome().equalsIgnoreCase("comentario_regex")) {
                    comentario_regex = param.getValor();
                } else if (param.getNome().equalsIgnoreCase("sufixo")) {
                    sufixo = param.getValor();
                } else if (param.getNome().equalsIgnoreCase("linhas_por_registro")) {
                    try {
                        linhas_por_registro = Integer.parseInt(param.getValor());
                    } catch(NumberFormatException ex) {
                        //Caso não seja um numero no parametro considera como 1
                        linhas_por_registro = 1;
                    }
                }
            }
            if (limite_noturno != -1 &&
                    (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                    Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 19 ||
                    (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance().get(Calendar.MINUTE) <= 7 * 60 + 30))) {
                limite = limite_noturno;
            }
        }
        nomeArquivo = nomeArquivo.replaceAll("<sufixo>", sufixo);
    }

    public void setConfig(DocumentoTipo doc) {
        this.doc = doc;
    }

    public DocumentoTipo getConfig() {
        return doc;
    }

    public String getOrdem() {
        return ordem;
    }

    public String getDelimitador() {
        return delimitador;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }
}