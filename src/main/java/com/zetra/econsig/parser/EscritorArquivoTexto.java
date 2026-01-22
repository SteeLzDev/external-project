package com.zetra.econsig.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.config.AtributoTipo;
import com.zetra.econsig.parser.config.HeaderTipo;

/**
 * <p>Title: EscritorArquivoTexto</p>
 * <p>Description: Implementação de um escritor para arquivo texto.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EscritorArquivoTexto extends ArquivoTexto implements Escritor {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EscritorArquivoTexto.class);

    protected PrintWriter out;

    private String filtro;
    private String filtroValor;

    private final Map<String, Object> valoresHeader;
    private final Map<String, Object> valoresFooter;
    private final StringBuilder cache;

    private int qtdTotal;
    private int qtdParcial;

    private final Map<String, Object> headerAdicional;
    private final Map<String, Object> footerAdicional;

    private int countHeader;

    private final List<HeaderTipo> headersTotal;
    private final List<HeaderTipo> footersTotal;

    private final Map<String, HeaderTipo> headersLote;
    private final Map<String, HeaderTipo> footersLote;

    private final Map<String, String> filtroValorHeaders;
    private final Map<String, String> filtroValorFooters;

    private final Map<String, Map<String, Object>> valoresHeadersLote;
    private final Map<String, Map<String, Object>> valoresFootersLote;
    private final Map<HeaderTipo, Map<String, Object>> valoresHeadersTotal;
    private final Map<HeaderTipo, Map<String, Object>> valoresFootersTotal;

    public EscritorArquivoTexto(String nomearqconfig, String nomearqsaida) {
        super(nomearqconfig, nomearqsaida);

        valoresHeader = new HashMap<>();
        valoresFooter = new HashMap<>();
        cache = new StringBuilder();

        filtro = null;
        filtroValor = null;

        headersTotal = new ArrayList<>();
        footersTotal = new ArrayList<>();

        headersLote = new HashMap<>();
        footersLote = new HashMap<>();

        filtroValorHeaders = new HashMap<>();
        filtroValorFooters = new HashMap<>();

        qtdTotal = 0;
        qtdParcial = 0;

        headerAdicional = new HashMap<>();
        footerAdicional = new HashMap<>();

        valoresHeadersLote = new HashMap<>();
        valoresFootersLote = new HashMap<>();
        valoresHeadersTotal = new HashMap<>();
        valoresFootersTotal = new HashMap<>();

        countHeader = 1;
    }

    @Override
    public void escreve(Map<String, Object> informacao) throws ParserException {
        if (informacao.get("__HEADER__") != null) {
            if (doc.getHeader() != null) {
                // Header Adicional, sempre do tipo TOTAL (Header de Arquivo)
                headerAdicional.putAll(informacao);
                int tipoHeaderAdicional = (delimitador != null) ? DELIMITADO_TOTAL : POSICIONAL_TOTAL;
                out.println(geraHeader(doc.getHeader(), headerAdicional, tipoHeaderAdicional, false));
            }
            return;
        }
        if (informacao.get("__FOOTER__") != null) {
            if (doc.getFooter() != null) {
                // Footer Adicional, sempre do tipo TOTAL (Footer de Arquivo)
                footerAdicional.putAll(informacao);
            }
            return;
        }
        List<AtributoTipo> lista = doc.getAtributo();
        Iterator<AtributoTipo> it = lista.iterator();
        String saida[] = new String[lista.size()];

        while (it.hasNext()) {
            StringBuilder temp;
            AtributoTipo atr = it.next();
            Object valor = informacao.get(atr.getNome());
            if (valor == null) {
                // Verifica se campo pode ser nulo
                if (atr.getDefault() != null) {
                    temp = new StringBuilder(atr.getDefault());
                } else {
                    // Campo não pode ser nulo
                    throw new ParserException("mensagem.erro.campo.nulo",(AcessoSistema) null, atr.getNome());
                }
            } else {
                temp = new StringBuilder(valor.toString());
            }

            // Trunca no tamanho definido no arquivo de configuração.
            if (atr.getTamanho() > 0 && atr.getTamanho() < temp.length()) {
                temp.delete(atr.getTamanho(), temp.length());
            }
            // Arquivo com posições predefinidas
            if (delimitador == null) {
                char compl = atr.getComplemento() != null ? atr.getComplemento().charAt(0) : ' ';
                if (atr.getAlinhamento().equalsIgnoreCase("Direita")) {
                    temp.reverse();
                } while (temp.length() < atr.getTamanho()) {
                    temp.append(compl);
                }
                if (atr.getAlinhamento().equalsIgnoreCase("Direita")) {
                    temp.reverse();
                }

                // Arquivo com valores em sequencia
            } else {
                temp.append(delimitador);
            }

            saida[atr.getIndice()] = temp.toString();
        }

        // Agrupa os valores
        StringBuilder temp = new StringBuilder(saida[0]);
        for (int i = 1; i < saida.length; i++) {
            temp.append(saida[i]);
        }

        // Remove o ultimo delimitador, caso não seja nulo
        if (delimitador != null) {
            temp.delete(temp.length() - delimitador.length(), temp.length());
        }

        /* -------------------- Escreve a saida --------------------------------- */
        // Se tem footer ou header, e o agrupamento é por lote
        if ((temHeader || temFooter) && filtro != null) {
            String aux = (String) informacao.get(filtro);
            // Se o filtro foi alterado
            if (aux != null && filtroValor != null && !filtroValor.equals(aux)) {
                if (temHeader && (tipoHeader == DELIMITADO_LOTE || tipoHeader == POSICIONAL_LOTE)) {
                    // Imprime o header por lote
                    countHeader++;
                    out.println(geraHeader(doc.getHeader(), valoresHeader, tipoHeader, false));
                    valoresHeader.clear();
                }
                // Imprime a saida
                out.print(cache);
                cache.setLength(0);

                if (temFooter && (tipoFooter == DELIMITADO_LOTE || tipoFooter == POSICIONAL_LOTE)) {
                    // Imprime o footer por lote
                    out.println(geraHeader(doc.getFooter(), valoresFooter, tipoFooter, false));
                    valoresFooter.clear();
                }

                qtdParcial = 0;
            }

            filtroValor = aux;
            cache.append(temp).append(System.getProperty("line.separator"));

            qtdTotal++;
            qtdParcial++;

            if (temHeader) {
                setaValoresHeader(doc.getHeader(), valoresHeader, informacao, tipoHeader);
            }
            if (temFooter) {
                setaValoresHeader(doc.getFooter(), valoresFooter, informacao, tipoFooter);
            }
        } else if (temHeader || temFooter) {
            qtdTotal++;
            qtdParcial++;

            if (temHeader) {
                setaValoresHeader(doc.getHeader(), valoresHeader, informacao, tipoHeader);
            }
            if (temFooter) {
                setaValoresHeader(doc.getFooter(), valoresFooter, informacao, tipoFooter);
            }

            out.println(temp);
        } else if (!headersLote.isEmpty() || !footersLote.isEmpty() || !headersTotal.isEmpty() || !footersTotal.isEmpty()) {
            if (!headersLote.isEmpty() || !footersLote.isEmpty()) {
                // ArrayList com todos os filtros.
                List<String> filtros = new ArrayList<>(headersLote.keySet());

                // Insere cada filtro de footer no array de filtros,
                // caso ainda não tenha sido inserido.
                Iterator<String> itFiltroFooter = footersLote.keySet().iterator();
                while (itFiltroFooter.hasNext()) {
                    String filtroFooter = itFiltroFooter.next();
                    if (headersLote.get(filtroFooter) == null) {
                        filtros.add(filtroFooter);
                    }
                }

                // Para cada filtro
                Iterator<String> itFiltro = filtros.iterator();
                while (itFiltro.hasNext()) {
                    String filtroAtual = itFiltro.next();
                    String aux = (String) informacao.get(filtroAtual);
                    HeaderTipo header = headersLote.get(filtroAtual);
                    HeaderTipo footer = footersLote.get(filtroAtual);

                    // Se o valor atual do campo utilizado como filtro eh diferente do valor
                    // valor anterior do mesmo campo, então o cabeçalho deve ser impresso.
                    if (aux != null && filtroValorHeaders.get(filtroAtual) != null && !filtroValorHeaders.get(filtroAtual).equals(aux)) {
                        if (header != null) {
                            // Inclui o texto do cabeçalho
                            countHeader++;
                            out.println(geraHeader(header, valoresHeadersLote.get(filtroAtual), header.getTipo(), false));
                            valoresHeadersLote.get(filtroAtual).clear();
                        }
                        // Escreve a saída
                        out.print(cache);
                        cache.setLength(0);

                        qtdParcial = 0;
                    }

                    // Da mesma forma, testa se algum footer deve ser impresso.
                    if (aux != null && filtroValorFooters.get(filtroAtual) != null && !filtroValorFooters.get(filtroAtual).equals(aux)) {
                        out.print(cache);
                        cache.setLength(0);
                        if (footer != null) {
                            out.println(geraHeader(footer, valoresFootersLote.get(filtroAtual), footer.getTipo(), false));
                            valoresFootersLote.get(filtroAtual).clear();
                        }
                        qtdParcial = 0;
                    }

                    // Guarda o valor atual utilizado como filtro para o header e o footer.
                    if (headersLote.keySet().contains(filtroAtual)) {
                        filtroValorHeaders.put(filtroAtual, aux);
                    }
                    if (footersLote.keySet().contains(filtroAtual)) {
                        filtroValorFooters.put(filtroAtual, aux);
                    }
                }

                qtdTotal++;
                qtdParcial++;

                // Adiciona a linha atual ao cache.
                cache.append(temp).append(System.getProperty("line.separator"));

                // Atribui valores aos headers e footers de lote.
                String filtroAtual;
                HeaderTipo header, footer;
                Iterator<String> itFiltroHeader = headersLote.keySet().iterator();
                while (itFiltroHeader.hasNext()) {
                    filtroAtual = itFiltroHeader.next();
                    header = headersLote.get(filtroAtual);
                    setaValoresHeader(header, valoresHeadersLote.get(filtroAtual), informacao, header.getTipo());
                }
                itFiltroFooter = footersLote.keySet().iterator();
                while (itFiltroFooter.hasNext()) {
                    filtroAtual = itFiltroFooter.next();
                    footer = footersLote.get(filtroAtual);
                    setaValoresHeader(footer, valoresFootersLote.get(filtroAtual), informacao, footer.getTipo());
                }
            }

            // Atribui valores para os headers e footers totais.
            if (!headersTotal.isEmpty() || !footersTotal.isEmpty()) {
                HeaderTipo header, footer;
                Iterator<HeaderTipo> itHeaderTotal = headersTotal.iterator();
                while (itHeaderTotal.hasNext()) {
                    header = itHeaderTotal.next();
                    valoresHeadersTotal.put(header, new HashMap<>());

                    setaValoresHeader(header, valoresHeadersTotal.get(header), informacao, header.getTipo());
                }

                Iterator<HeaderTipo> itFooterTotal = footersTotal.iterator();
                while (itFooterTotal.hasNext()) {
                    footer = itFooterTotal.next();
                    valoresFootersTotal.put(footer, new HashMap<>());

                    setaValoresHeader(footer, valoresFootersTotal.get(footer), informacao, footer.getTipo());
                }
            }
        } else {
            out.println(temp);
        }

        /* -------------------- Fim do Escreve a saida ---------------------------- */
    }

    private String geraHeader(HeaderTipo headerObj, Map<String, Object> cacheHeader, int tipo, boolean vazio) throws ParserException {
        List<AtributoTipo> attrs = headerObj.getAtributo();
        String header[] = new String[attrs.size()];
        Iterator<AtributoTipo> it = attrs.iterator();
        AtributoTipo attr = null;
        StringBuilder temp = new StringBuilder();

        while (it.hasNext()) {
            attr = it.next();

            if (cacheHeader.get(attr.getNome()) != null) {
                temp.append(cacheHeader.get(attr.getNome()));
            } else {
                // Verifica se campo pode ser nulo
                if (attr.getDefault() != null) {
                    temp.append(attr.getDefault());
                } else if (!vazio) {
                    // Campo não pode ser nulo
                    throw new ParserException("mensagem.erro.campo.nulo", (AcessoSistema) null, attr.getNome());
                }
            }

            if (tipo == POSICIONAL_TOTAL || tipo == POSICIONAL_LOTE) {
                char compl = attr.getComplemento() != null ? attr.getComplemento().charAt(0) : ' ';
                if (attr.getAlinhamento().equalsIgnoreCase("Direita")) {
                    temp.reverse();
                } while (temp.length() < attr.getTamanho()) {
                    temp.append(compl);
                }
                if (attr.getAlinhamento().equalsIgnoreCase("Direita")) {
                    temp.reverse();
                }
            } else {
                temp.append(delimitador);
            }

            header[attr.getIndice()] = temp.toString();
            temp.setLength(0);
        }

        // Agrupa os valores
        for (String element : header) {
            temp.append(element);
        }

        // Remove o ultimo delimitador, caso não seja nulo
        if (tipo == DELIMITADO_TOTAL || tipo == DELIMITADO_LOTE) {
            temp.delete(temp.length() - delimitador.length(), temp.length());
        }

        // Imprime o header
        return (temp.toString());
    }

    private void setaValoresHeader(HeaderTipo headerObj, Map<String, Object> cacheHeader, Map<String, Object> informacao, int tipo) {
        List<AtributoTipo> attrs = headerObj.getAtributo();
        Iterator<AtributoTipo> it = attrs.iterator();
        AtributoTipo attr = null;

        int qtd = (tipo == DELIMITADO_TOTAL || tipo == POSICIONAL_TOTAL) ? qtdTotal : qtdParcial;

        String nome = null, operacao = null;
        Object valor1 = null, valor2 = null;

        while (it.hasNext()) {
            attr = it.next();

            nome = attr.getNome();
            operacao = attr.getOperacao();

            valor1 = informacao.get(nome);
            valor2 = cacheHeader.get(nome);

            if (operacao == null) {
                operacao = "I";
            }
            switch (operacao.charAt(0)) {
                case 'S': /* SOMA */
                    valor1 = valor1 != null ? new BigDecimal(valor1.toString()) : null;
                    valor2 = valor2 != null ? new BigDecimal(valor2.toString()) : null;
                    if (valor1 != null && valor2 != null) {
                        valor1 = ((BigDecimal) valor1).add((BigDecimal) valor2);
                        cacheHeader.put(nome, valor1);
                    } else if (valor1 != null) {
                        cacheHeader.put(nome, valor1);
                    }
                    break;
                case 'C': /* CONTAGEM */
                    cacheHeader.put(nome, Integer.valueOf(qtd));
                    break;
                case 'M': /* MÉDIA */
                    valor1 = valor1 != null ? new BigDecimal(valor1.toString()) : new BigDecimal("0");
                    valor2 = valor2 != null ? new BigDecimal(valor2.toString()) : new BigDecimal("0");

                    valor2 = ((BigDecimal) valor2).multiply(new BigDecimal(String.valueOf(qtd - 1)));
                    valor1 = ((BigDecimal) valor2).add((BigDecimal) valor1).divide(new BigDecimal(String.valueOf(qtd)), java.math.RoundingMode.DOWN);
                    cacheHeader.put(nome, valor1);
                    break;
                case '>': /* MÁXIMO */
                    valor1 = valor1 != null ? new BigDecimal(valor1.toString()) : new BigDecimal(Double.MIN_VALUE);
                    valor2 = valor2 != null ? new BigDecimal(valor2.toString()) : new BigDecimal(Double.MIN_VALUE);
                    if (((BigDecimal) valor1).compareTo((BigDecimal) valor2) > 0) {
                        cacheHeader.put(nome, valor1);
                    }
                    break;
                case '<': /* MÍNIMO */
                    valor1 = valor1 != null ? new BigDecimal(valor1.toString()) : new BigDecimal(Double.MAX_VALUE);
                    valor2 = valor2 != null ? new BigDecimal(valor2.toString()) : new BigDecimal(Double.MAX_VALUE);
                    if (((BigDecimal) valor1).compareTo((BigDecimal) valor2) < 0) {
                        cacheHeader.put(nome, valor1);
                    }
                    break;
                case 'E': /* ENUMERAÇÃO DE HEADERS */
                    cacheHeader.put(nome, valor1 != null ? valor1 : Integer.valueOf(countHeader));
                    break;
                case 'P': /* VALOR PREENCHIDO */
                    if (!TextHelper.isNull(valor1)) {
                        cacheHeader.put(nome, valor1);
                    }
                    break;
                default:
                    cacheHeader.put(nome, valor1);
                    break;
            }
        }
    }

    @Override
    public void iniciaEscrita() throws ParserException {
        try {
            HeaderTipo header;
            HeaderTipo footer;
            int tipoHeaderAtual;
            int tipoFooterAtual;
            String filtroAtual;

            if (tipoHeader == DELIMITADO_LOTE || tipoHeader == POSICIONAL_LOTE) {
                filtro = doc.getHeader().getFiltro();
                if (filtro == null) {
                    throw new ParserException("mensagem.erro.escritor.arquivo.filtro.nulo.header.separado.lotes", (AcessoSistema) null);
                }
            }
            if (filtro == null && (tipoFooter == DELIMITADO_LOTE || tipoFooter == POSICIONAL_LOTE)) {
                filtro = doc.getFooter().getFiltro();
                if (filtro == null) {
                    throw new ParserException("mensagem.erro.escritor.arquivo.filtro.nulo.footer.separado.lotes", (AcessoSistema) null);
                }
            }

            Iterator<HeaderTipo> itHeader = headers.iterator();
            int countHeader = 0;
            while (itHeader.hasNext()) {
                header = itHeader.next();
                tipoHeaderAtual = header.getTipo();
                filtroAtual = null;

                if (tipoHeaderAtual == DELIMITADO_LOTE || tipoHeaderAtual == POSICIONAL_LOTE) {
                    // O sistema não está preparado para trabalhar com mais de um header por lotes
                    if (++countHeader > 1) {
                        throw new ParserException("mensagem.erro.escritor.arquivo.maximo.um.header.separado.lotes", (AcessoSistema) null);
                    }

                    filtroAtual = header.getFiltro();
                    if (filtroAtual == null) {
                        throw new ParserException("mensagem.erro.escritor.arquivo.filtro.nulo.header.separado.lotes", (AcessoSistema) null);
                    } else if (headersLote.get(filtroAtual) != null) {
                        throw new ParserException("mensagem.erro.escritor.arquivo.filtro.repetido.para.header.separado.lotes", (AcessoSistema) null);
                    } else {
                        headersLote.put(filtroAtual, header);
                        valoresHeadersLote.put(filtroAtual, new HashMap<>());
                    }
                } else {
                    headersTotal.add(header);
                }
            }

            Iterator<HeaderTipo> itFooter = footers.iterator();
            int countFooter = 0;
            while (itFooter.hasNext()) {
                footer = itFooter.next();
                tipoFooterAtual = footer.getTipo();
                filtroAtual = null;

                if (tipoFooterAtual == DELIMITADO_LOTE || tipoFooterAtual == POSICIONAL_LOTE) {
                    // A implementação não está preparada para trabalhar com mais de um footer por lotes
                    if (++countFooter > 1) {
                        throw new ParserException("mensagem.erro.escritor.arquivo.maximo.um.footer.separado.lotes", (AcessoSistema) null);
                    }

                    filtroAtual = footer.getFiltro();

                    // A implementação não está preparada para trabalhar com um header e um
                    // footer com filtros diferentes.
                    if (headersLote.get(filtroAtual) == null && !headersLote.isEmpty()) {
                        throw new ParserException("mensagem.erro.escritor.arquivo.filtro.footer.separado.lotes.diferente.seu.header", (AcessoSistema) null);
                    }

                    if (filtroAtual == null) {
                        throw new ParserException("mensagem.erro.escritor.arquivo.filtro.nulo.footer.separado.lotes", (AcessoSistema) null);
                    } else if (footersLote.get(filtroAtual) != null) {
                        throw new ParserException("mensagem.erro.escritor.arquivo.filtro.repetido.para.footer.separado.lotes", (AcessoSistema) null);
                    } else {
                        footersLote.put(filtroAtual, footer);
                        valoresFootersLote.put(filtroAtual, new HashMap<>());
                    }
                } else {
                    footersTotal.add(footer);
                }
            }

            out = new PrintWriter(new BufferedWriter(new FileWriter(nomeArquivo)));

            // Imprime espaço para posterior inclusão do header.
            if (tipoHeader == POSICIONAL_TOTAL || tipoHeader == DELIMITADO_TOTAL) {
                out.println(geraHeader(doc.getHeader(), valoresHeader, tipoHeader, true));
            }

            // Imprime espaço para posterior inclusão dos headers totais.
            Iterator<HeaderTipo> itHeaderTotal = headersTotal.iterator();

            while (itHeaderTotal.hasNext()) {
                header = itHeaderTotal.next();
                out.println(geraHeader(header, valoresHeader, header.getTipo(), true));
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParserException("mensagem.erro.criacao.arquivo.saida", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void encerraEscrita() throws ParserException {
        try {
            if (qtdTotal == 0) {
                out.close();
                // Remove a linha em branco impressa para o posicionamento do header
                if (tipoHeader == POSICIONAL_TOTAL || tipoHeader == DELIMITADO_TOTAL) {
                    File arq = new File(nomeArquivo);
                    FileHelper.truncate(nomeArquivo, arq.length());
                }
            } else {
                String strFooter = "";
                // Se tem a declaração de footer e o tipo é um dos tipos válidos
                if (temFooter && (tipoFooter == DELIMITADO_TOTAL || tipoFooter == POSICIONAL_TOTAL ||
                        tipoFooter == DELIMITADO_LOTE || tipoFooter == POSICIONAL_LOTE)) {
                    // Se não tem header, ou o header é de algum tipo total
                    if ((!temHeader || tipoHeader == DELIMITADO_TOTAL || tipoHeader == POSICIONAL_TOTAL) &&
                            cache.length() > 0) {
                        strFooter += cache.toString();
                    }

                    // Gera o footer
                    strFooter += geraHeader(doc.getFooter(), valoresFooter, tipoFooter, false)
                            + System.getProperty("line.separator");
                }

                switch (tipoHeader) {
                    case DELIMITADO_TOTAL:
                    case POSICIONAL_TOTAL:
                        out.print(strFooter);
                        out.close();
                        FileHelper.setHeader(nomeArquivo, geraHeader(doc.getHeader(), valoresHeader, tipoHeader, false));
                        break;

                    case DELIMITADO_LOTE:
                    case POSICIONAL_LOTE:
                        out.println(geraHeader(doc.getHeader(), valoresHeader, tipoHeader, false));
                        out.print(cache);
                        out.print(strFooter);

                        if (footerAdicional.size() > 0) {
                            // Footer Adicional, sempre do tipo TOTAL (Footer de Arquivo)
                            int tipoFooterAdicional = (delimitador != null) ? DELIMITADO_TOTAL : POSICIONAL_TOTAL;
                            setaValoresHeader(doc.getFooter(), valoresFooter, footerAdicional, tipoFooterAdicional);
                            out.print(geraHeader(doc.getFooter(), valoresFooter, tipoFooterAdicional, false));
                        }

                        out.close();
                        break;

                    default:
                        if (headersLote.size() > 0 || footersLote.size() > 0) {
                            // Se há headers de lote, imprime-os
                            if (headersLote.size() > 0) {
                                Iterator<String> itFiltroHeaderLote = headersLote.keySet().iterator();

                                while (itFiltroHeaderLote.hasNext()) {
                                    String filtro = itFiltroHeaderLote.next();
                                    HeaderTipo header = headersLote.get(filtro);

                                    out.println(geraHeader(header, valoresHeadersLote.get(filtro), header.getTipo(), false));
                                }
                            }
                            if (cache.length() > 0) {
                                out.print(cache);
                            }
                            // Se há footers de lote, imprime-os
                            if (footersLote.size() > 0) {
                                Iterator<String> itFiltroFooterLote = footersLote.keySet().iterator();

                                while (itFiltroFooterLote.hasNext()) {
                                    String filtro = itFiltroFooterLote.next();
                                    HeaderTipo footer = footersLote.get(filtro);

                                    out.println(geraHeader(footer, valoresFootersLote.get(filtro), footer.getTipo(), false));
                                }
                            }
                        }

                        // Imprime footers totais.
                        Iterator<HeaderTipo> itFooterTotal = footersTotal.iterator();
                        HeaderTipo footer;
                        while (itFooterTotal.hasNext()) {
                            footer = itFooterTotal.next();
                            out.println(geraHeader(footer, valoresFootersTotal.get(footer), footer.getTipo(), true));
                        }

                        out.print(strFooter);

                        if (footerAdicional.size() > 0) {
                            // Footer Adicional, sempre do tipo TOTAL (Footer de Arquivo)
                            int tipoFooterAdicional = (delimitador != null) ? DELIMITADO_TOTAL : POSICIONAL_TOTAL;
                            setaValoresHeader(doc.getFooter(), valoresFooter, footerAdicional, tipoFooterAdicional);
                            out.print(geraHeader(doc.getFooter(), valoresFooter, tipoFooterAdicional, false));
                        }

                        out.close();
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParserException("mensagem.erro.criacao.arquivo.saida", (AcessoSistema) null, ex);
        }
    }
}