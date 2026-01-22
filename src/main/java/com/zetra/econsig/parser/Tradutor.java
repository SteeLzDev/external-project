package com.zetra.econsig.parser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.criptografia.JCrypt;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.servidor.GeradorCpfServidor;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.xml.XmlHelper;
import com.zetra.econsig.parser.config.DocumentoTipo;
import com.zetra.econsig.parser.config.MapeamentoTipo;

/**
 * <p>Title: Tradutor</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Tradutor implements ITradutor {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Tradutor.class);

    private static final String KEY_VALOR_SAIDA = "VALOR_SAIDA";
    private static final String KEY_VALOR_NUMERICO = "VALOR_NUMERICO";

    private final Leitor leitor;
    private final Escritor escritor;
    private DocumentoTipo doc;
    private final Map<String, Map<String, String>> mapas;
    private final Map<String, Object> cacheTradutor;
    private List<MapeamentoTipo> mapList;
    private Map<String, Object> dados;

    private boolean gerarValoresNumericos;
    private Map<String, BigDecimal> valoresNumericos;

    public Tradutor(String nomearqconfig, Leitor l, Escritor e) {
        try {
            doc = XmlHelper.unmarshal(nomearqconfig);
        } catch (ParserException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        leitor = l;
        escritor = e;

        mapas = new HashMap<>();
        cacheTradutor = new HashMap<>();
        mapList = null;
    }

    public Map<String, Object> getDados() {
        return dados;
    }

    public Map<String, BigDecimal> getValoresNumericos() {
        return gerarValoresNumericos ? valoresNumericos : null;
    }

    @Override
    public void iniciaTraducao() throws ParserException {
        this.iniciaTraducao(false);
    }

    /**
     * Inicia o processo de tradução
     * @param gerarValoresNumericos Se os campos numéricos devem ser gravados separadamente.
     * @throws ParserException
     */
    public void iniciaTraducao(boolean gerarValoresNumericos) throws ParserException {
        this.gerarValoresNumericos = gerarValoresNumericos;

        leitor.iniciaLeitura();
        escritor.iniciaEscrita();

        mapList = doc.getMapeamento();
    }

    @Override
    public boolean traduzProximo() throws ParserException {
        Map<String, Object> entrada = null;
        int mapListSize = mapList.size();
        Iterator<MapeamentoTipo> it;

        try {
            if ((entrada = leitor.le()) != null) {
                it = mapList.iterator();
                dados = new HashMap<>(mapListSize);

                if (gerarValoresNumericos) {
                    // O mapa terá no máximo o tamanho de mapListSize.
                    valoresNumericos = new HashMap<>(mapListSize);
                }

                String valorSaida = null;
                BigDecimal valorNumerico = null;

                while (it.hasNext()) {
                    MapeamentoTipo map = it.next();
                    valorSaida = entrada.get(map.getEntrada()) != null ? entrada.get(map.getEntrada()).toString() : null;
                    if (valorSaida == null) {
                        valorSaida = (dados.get(map.getEntrada()) != null) ? dados.get(map.getEntrada()).toString() : null;
                    }

                    try {
                        Map<String, Object> valorFormatado = format(valorSaida, map, mapas, cacheTradutor, entrada, dados, gerarValoresNumericos);
                        valorSaida = (String) valorFormatado.get(KEY_VALOR_SAIDA);
                        if (gerarValoresNumericos) {
                            valorNumerico = (BigDecimal) valorFormatado.get(KEY_VALOR_NUMERICO);
                        }
                    } catch (ParserException ex) {
                        if (leitor.getClass().equals(LeitorArquivoTexto.class)) {
                            if (((LeitorArquivoTexto) leitor).temHeader && ((LeitorArquivoTexto) leitor).linhaCorrente == 0) {
                                ex = new ParserException("mensagem.erro.tradutor.linha.cabecalho.entrada.invalida", (AcessoSistema) null);
                            } else {
                                ex = new ParserException("mensagem.informacao.tradutor.numero.linha.corrente", (AcessoSistema) null, ex.getMessage(), String.valueOf(((LeitorArquivoTexto) leitor).linhaCorrente));
                            }
                        }
                        throw ex;
                    }
                    dados.put(map.getSaida(), valorSaida);

                    if (gerarValoresNumericos && valorNumerico != null) {
                        valoresNumericos.put(map.getSaida(), valorNumerico);
                    }
                }

                escritor.escreve(dados);
                return true;
            }

            return false;

        } catch (Exception ex) {
            if (entrada != null) {
                LOG.error("Erro(Tradutor.traduzProximo): posição atual: " + entrada.entrySet().toString());
            }
            throw new ParserException(ex);
        }
    }

    @Override
    public void traduz() throws ParserException {
        iniciaTraducao();
        while (traduzProximo()) {

        }
        encerraTraducao();
    }

    @Override
    public void encerraTraducao() throws ParserException {
        escritor.encerraEscrita();
        leitor.encerraLeitura();
    }

    private static Map<String, Object> format(String entrada, MapeamentoTipo map, Map<String, Map<String, String>> cacheMapas, Map<String, Object> cacheTradutor,
            Map<String, Object> cacheEntrada, Map<String, Object> cacheSaida, boolean gerarValorNumerico) throws ParserException {

        Map<String, String> mapa;
        String valorSaida = (entrada != null) ? entrada.toString() : null;
        BigDecimal valorNumerico = null;
        String tipo = map.getTipo();

        if (cacheMapas == null) {
            cacheMapas = new HashMap<>();
        }
        if (cacheEntrada == null) {
            cacheEntrada = new HashMap<>();
        }
        if (cacheSaida == null) {
            cacheSaida = new HashMap<>();
        }

        if ((valorSaida != null && !valorSaida.equals(""))) {
            // Faz a tradução do campo de acordo com os formatos de entrada e saida
            try {
                if ((tipo.equalsIgnoreCase("Data") || tipo.equalsIgnoreCase("Numerico") || tipo.equalsIgnoreCase("Mapa")) &&
                        (map.getFormatoEntrada() != null) && (map.getFormatoSaida() != null) && (!map.getFormatoEntrada().equals(map.getFormatoSaida()))) {
                    // Formato de entrada e saída obrigatórios para os tipos: Data, Numerico e Mapa
                    if (tipo.equalsIgnoreCase("Data")) {
                        //DateHelper.validaData(valorSaida, map.getFormatoEntrada());
                        valorSaida = DateHelper.reformat(valorSaida, map.getFormatoEntrada(), map.getFormatoSaida());
                    } else if (tipo.equalsIgnoreCase("Numerico")) {
                        if (TextHelper.isNotNumeric(valorSaida)) {
                            throw new ParserException("mensagem.erro.tradutor.valor.saida.invalido.para.campo", (AcessoSistema) null, valorSaida, map.getEntrada());
                        }

                        if (map.getFormatoEntrada().equalsIgnoreCase("pt")) {
                            valorSaida = TextHelper.dropSeparator(valorSaida, '.');
                        } else if (map.getFormatoEntrada().equalsIgnoreCase("en")) {
                            valorSaida = TextHelper.dropSeparator(valorSaida, ',');
                        }

                        if ((map.getFormatoEntrada().equalsIgnoreCase("pt")) && (valorSaida.indexOf(',') == -1 || valorSaida.indexOf(',') != valorSaida.lastIndexOf(',') || valorSaida.indexOf('.') != -1)) {
                            throw new ParserException("mensagem.erro.tradutor.valor.formato.incorreto.use.pt", (AcessoSistema) null, valorSaida);
                        } else if ((map.getFormatoEntrada().equals("en")) && (valorSaida.indexOf('.') == -1 || valorSaida.indexOf('.') != valorSaida.lastIndexOf('.') || valorSaida.indexOf(',') != -1)) {
                            throw new ParserException("mensagem.erro.tradutor.valor.formato.incorreto.use.en", (AcessoSistema) null, valorSaida);
                        }

                        if (gerarValorNumerico) {
                            try {
                                valorNumerico = new BigDecimal(NumberHelper.parse(valorSaida, map.getFormatoEntrada()));
                            } catch (ParseException pe) {
                                valorNumerico = null;
                            } catch (NumberFormatException nfe) {
                                valorNumerico = null;
                            }
                        }

                        valorSaida = NumberHelper.reformat(valorSaida, map.getFormatoEntrada(), map.getFormatoSaida());
                    } else if (tipo.equalsIgnoreCase("Mapa")) {
                        if (cacheMapas.get(map.getSaida()) == null) {
                            String mapaEntrada = map.getFormatoEntrada(), mapaSaida = map.getFormatoSaida();
                            String valoresEntrada[] = TextHelper.split(mapaEntrada, map.getDelimitadorMapa() != null ? map.getDelimitadorMapa() : ";"),
                            valoresSaida[] = TextHelper.split(mapaSaida, map.getDelimitadorMapa() != null ? map.getDelimitadorMapa() : ";");
                            mapa = new HashMap<>(valoresEntrada.length);
                            for (int i = 0; i < valoresEntrada.length; i++) {
                                mapa.put(valoresEntrada[i], valoresSaida[i]);
                            }
                            cacheMapas.put(map.getSaida(), mapa);
                        }
                        mapa = cacheMapas.get(map.getSaida());
                        valorSaida = mapa.get(valorSaida) != null ? mapa.get(valorSaida) : mapa.get("Default");
                        if (cacheSaida.containsKey(valorSaida)) {
                            valorSaida = cacheSaida.get(valorSaida) != null ? cacheSaida.get(valorSaida).toString() : null;
                        } else if (cacheEntrada.containsKey(valorSaida)) {
                            valorSaida = cacheEntrada.get(valorSaida) != null ? cacheEntrada.get(valorSaida).toString() : null;
                        }
                    }

                } else if (tipo.equalsIgnoreCase("Numerico") && (map.getFormatoEntrada() != null)) {
                    if((map.getFormatoEntrada().equalsIgnoreCase("pt")) || (map.getFormatoEntrada().equals("en"))) {
                        try {
                            double valor = NumberHelper.parse(valorSaida, map.getFormatoEntrada());

                            if (gerarValorNumerico) {
                                try {
                                    valorNumerico = new BigDecimal(valor);
                                } catch (NumberFormatException nfe) {
                                    valorNumerico = null;
                                }
                            }
                        } catch (ParseException pe) {
                            throw new ParserException("mensagem.erro.tradutor.valor.saida.invalido.para.campo", (AcessoSistema) null, valorSaida, map.getEntrada());
                        }
                    }

                    if (TextHelper.isNotNumeric(valorSaida)) {
                        throw new ParserException("mensagem.erro.tradutor.valor.saida.invalido.para.campo", (AcessoSistema) null, valorSaida, map.getEntrada());
                    }

                    if ((map.getFormatoEntrada().equalsIgnoreCase("pt")) && (valorSaida.indexOf(',') == -1 || valorSaida.indexOf(',') != valorSaida.lastIndexOf(',') || valorSaida.indexOf('.') != -1)) {
                        throw new ParserException("mensagem.erro.tradutor.valor.formato.incorreto.use.pt", (AcessoSistema) null, valorSaida);
                    } else if ((map.getFormatoEntrada().equals("en")) && (valorSaida.indexOf('.') == -1 || valorSaida.indexOf('.') != valorSaida.lastIndexOf('.') || valorSaida.indexOf(',') != -1)) {
                        throw new ParserException("mensagem.erro.tradutor.valor.formato.incorreto.use.en", (AcessoSistema) null, valorSaida);
                    }
                } else if (tipo.equalsIgnoreCase("Numerico") && (map.getFormatoSaida() != null)) {
                    if(TextHelper.isNotNumeric(valorSaida)) {
                        throw new ParserException("mensagem.erro.tradutor.valor.saida.invalido.para.campo", (AcessoSistema) null, valorSaida, map.getEntrada());
                    }

                    if (map.getFormatoSaida().equalsIgnoreCase("pt") && valorSaida.indexOf(',') == -1) {
                        if(valorSaida.indexOf('.') > 0) {
                            valorSaida = valorSaida.replace('.',',');
                        } else {
                           valorSaida = valorSaida.substring(0,valorSaida.length() - 2) + "," + valorSaida.substring(valorSaida.length() - 2);
                        }
                    } else if (map.getFormatoSaida().equalsIgnoreCase("en") && (valorSaida.indexOf('.') == -1)) {
                        if(valorSaida.indexOf(',') > 0) {
                            valorSaida = valorSaida.replace(',','.');
                        } else {
                            valorSaida = valorSaida.substring(0,valorSaida.length() - 2) + "." + valorSaida.substring(valorSaida.length() - 2);
                        }
                    }

                    Double valor = Double.valueOf(valorSaida).doubleValue();
                    valorSaida = NumberHelper.format(valor, map.getFormatoSaida());

                    if (gerarValorNumerico) {
                        try {
                            valorNumerico = new BigDecimal(valor);
                        } catch (NumberFormatException nfe) {
                            valorNumerico = null;
                        }
                    }

                } else if (tipo.equalsIgnoreCase("Numerico") && (map.getFormatoSaida() == null) && (map.getFormatoEntrada() == null)) {
                    if(TextHelper.isNotNumeric(valorSaida)) {
                        throw new ParserException("mensagem.erro.tradutor.valor.saida.invalido.para.campo", (AcessoSistema) null, valorSaida, map.getEntrada());
                    }

                    if (gerarValorNumerico) {
                        try {
                            valorNumerico = new BigDecimal(valorSaida);
                        } catch (NumberFormatException nfe) {
                            valorNumerico = null;
                        }
                    }

                } else if (tipo.equalsIgnoreCase("Texto") && map.getFormatoSaida() != null) {
                    // Formato de saída opcional para o tipo: Texto
                    valorSaida = TextHelper.format(valorSaida, map.getFormatoSaida());
                } else if (tipo.equalsIgnoreCase("Uppercase")) {
                    // Coloca a String em Caixa Alta
                    valorSaida = TextHelper.removeAccent(valorSaida.toUpperCase());
                } else if (tipo.equalsIgnoreCase("CPF")) {
                    // Verifica se o cpf é valido
                    valorSaida = valorSaida.trim();
                    if (TextHelper.dropSeparator(valorSaida).length() != 11) {
                        throw new ParserException("mensagem.erro.tradutor.cpf.formato.incorreto", (AcessoSistema) null, valorSaida);
                    } else if (!TextHelper.cpfOk(TextHelper.dropSeparator(valorSaida))) {
                        throw new ParserException("mensagem.erro.cpf.numero.invalido", (AcessoSistema) null, valorSaida);
                    }
                    // Formata o CPF
                    if (map.getFormatoSaida() != null) {
                        valorSaida = TextHelper.format(valorSaida, map.getFormatoSaida());
                    } else {
                        valorSaida = TextHelper.format(valorSaida, "###.###.###-##");
                    }
                } else if (tipo.equalsIgnoreCase("Split") &&
                        map.getFormatoEntrada() != null && map.getFormatoSaida() != null) {
                    String[] valores = TextHelper.split(valorSaida, map.getFormatoEntrada());
                    valorSaida = valores[Integer.parseInt(map.getFormatoSaida().toString())];
                } else if (tipo.equalsIgnoreCase("Divtexto") &&
                        map.getFormatoEntrada() != null && map.getFormatoSaida() != null) {

                    String Posicao = null;
                    int tam = 0;
                    try {
                        Posicao = map.getFormatoEntrada().split(";")[0];
                        tam = Integer.parseInt(map.getFormatoEntrada().split(";")[1]);
                    } catch (NumberFormatException ex) {
                        throw new ParserException("mensagem.erro.valor.formato.entrada.invalido", (AcessoSistema) null);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ParserException("mensagem.erro.numero.parametros.incorreto", (AcessoSistema) null);
                    }

                    String saida[] = null;
                    if (Posicao.equalsIgnoreCase("Direita")) {
                        // se direita
                        saida = new String[] {
                                (valorSaida.substring(0, valorSaida.length() - tam)),
                                (valorSaida.substring(valorSaida.length() - tam))};
                    } else if (Posicao.equalsIgnoreCase("Esquerda")) {
                        // se esquerda
                        saida = new String[] {
                                (valorSaida.substring(0, tam)),
                                (valorSaida.substring(tam))};
                    }
                    int retorno = 0;
                    try {
                        retorno = Integer.parseInt(map.getFormatoSaida());
                    } catch (NumberFormatException ex) {
                    }
                    if (retorno < saida.length) {
                        valorSaida = saida[retorno];
                    }
                } else if (tipo.equalsIgnoreCase("Substring")) {
                    // Formato de Entrada: posição inicial - Formato de Saída: posição final
                	// Quando um dos valores é negativo, isto quer dizer que será relativo ao final da string.
                    int ini = (map.getFormatoEntrada() != null && !map.getFormatoEntrada().toString().equals("")) ? Integer.parseInt(map.getFormatoEntrada().toString()) : 0;
                    int fim = (map.getFormatoSaida() != null && !map.getFormatoSaida().toString().equals("")) ? Integer.parseInt(map.getFormatoSaida().toString()) : valorSaida.length();

                    // casos negativos
                    if (fim < 0) {
                        fim = valorSaida.length() + fim;
                    }
                    if (ini < 0) {
                        ini = valorSaida.length() + ini;
                    }
                    // Neste ponto nenhum dos valores pode ser menor que zero
                    if (ini < 0) {
                        ini = 0;
                    }
                    if (fim < 0) {
                        fim = 0;
                    }
                    // Verifica se o final está fora do permitido
                    if (fim > valorSaida.length()) {
                        fim = valorSaida.length();
                    }

                    if (ini <= fim) {
                        valorSaida = valorSaida.substring(ini, fim);
                    }
                } else if (tipo.equalsIgnoreCase("Concat") && map.getFormatoEntrada() != null) {
                    // Formato de Entrada: elementos a serem concatenados

                    String elementos[] = TextHelper.split(map.getFormatoEntrada(), map.getDelimitadorMapa() != null ? map.getDelimitadorMapa() : ";");
                    StringBuilder sbTemp = new StringBuilder();
                    String temp = null;

                    for (String elemento : elementos) {
                        temp = elemento;
                        if (cacheSaida.containsKey(temp)) {
                            temp = cacheSaida.get(temp) != null ? cacheSaida.get(temp).toString() : null;
                        } else if (cacheEntrada.containsKey(temp)) {
                            temp = cacheEntrada.get(temp) != null ? cacheEntrada.get(temp).toString() : null;
                        }
                        sbTemp.append(temp);
                    }
                    valorSaida = sbTemp.toString();
                } else if (tipo.equalsIgnoreCase("Replace") && map.getFormatoEntrada() != null && map.getFormatoSaida() != null) {
                    valorSaida = valorSaida.replaceAll(map.getFormatoEntrada(), map.getFormatoSaida());
                } else if (tipo.equalsIgnoreCase("Inteiro")) {
                    // Números inteiros
                    try {
                        valorSaida = String.valueOf(Long.parseLong(valorSaida.trim()));
                    } catch (NumberFormatException ex) {
                        throw new ParserException("mensagem.erro.tradutor.valor.formato.incorreto.campo.numerico", (AcessoSistema) null, valorSaida);
                    }

                    if (gerarValorNumerico) {
                        try {
                            valorNumerico = new BigDecimal(valorSaida);
                        } catch (NumberFormatException nfe) {
                            valorNumerico = null;
                        }
                    }
                } else if (tipo.equalsIgnoreCase("Senha")) {
                    // Encriptação de senhas
                    valorSaida = JCrypt.crypt(valorSaida);
                } else if (tipo.equalsIgnoreCase("DateAdd") && map.getFormatoEntrada() != null &&
                        !map.getFormatoEntrada().equals("") && map.getFormatoSaida() != null &&
                        !map.getFormatoSaida().equals("")) {
                    /**
                     * FormatoEntrada - > formato em que a data esta, sendo que este formato
                     * será o formato retornado - por exemplo : yyyy-MM-dd
                     * FormatoSaida -> composto por duas partes sendo elas: tipo de adição: DIA, MES e ANO.
                     * e intervalo a ser alterado valor inteiro, pode ser negativo ou positivo.
                     */

                    String elementos[] = TextHelper.split(map.getFormatoSaida(), map.getDelimitadorMapa() != null ? map.getDelimitadorMapa() : ";");
                    String formatoEntrada = (map.getFormatoEntrada() != null) ? map.getFormatoEntrada() : LocaleHelper.getDatePattern();

                    if (elementos.length != 2) {
                        throw new ParserException("mensagem.erro.tradutor.parametros.inconsistentes.alteracao.data", (AcessoSistema) null);
                    }

                    // Verifica se o elemento a ser adicionado à data é um campo de tradução
                    String temp = elementos[1];
                    if (!TextHelper.isNum(temp)) {
                        if (cacheSaida.containsKey(temp)) {
                            temp = cacheSaida.get(temp) != null ? cacheSaida.get(temp).toString() : null;
                        } else if (cacheEntrada.containsKey(temp)) {
                            temp = cacheEntrada.get(temp) != null ? cacheEntrada.get(temp).toString() : null;
                        }
                    }

                    int intervalo = 0;
                    try {
                        intervalo = Integer.parseInt(temp);
                    } catch (Exception ex) {
                        throw new ParserException("mensagem.erro.tradutor.valor.formato.campo.incorreto.campo.inteiro", (AcessoSistema) null, temp, map.getSaida());
                    }

                    try {
                        valorSaida = DateHelper.format(DateHelper.dateAdd(DateHelper.parse(valorSaida, formatoEntrada), elementos[0], intervalo), formatoEntrada);
                    } catch (ParseException e) {
                        LOG.error("Erro de parser: " + e.getMessage());
                        throw new ParserException(e);
                    }
                }
            } catch (ParseException ex) {
                LOG.warn("AVISO: possível problema na formatação do campo " + map.getEntrada() + ", valor: " + entrada + ". Valor padrão para o campo atribuído.");
                if (map.getDefault() == null) {
                    throw new ParserException("mensagem.erro.tradutor.campo.formato.incorreto.para.entrada", (AcessoSistema) null, map.getEntrada(), valorSaida);
                } else {
                    valorSaida = map.getDefault();
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ParserException("mensagem.erro.tradutor.campo.formato.incorreto.para.entrada", (AcessoSistema) null, map.getEntrada(), valorSaida);
            }

        } else if(tipo.equalsIgnoreCase("Soma") && map.getFormatoEntrada() != null) {
            // Formato de Entrada: elementos a serem somados

            String elementos[] = TextHelper.split(map.getFormatoEntrada(), map.getDelimitadorMapa() != null ? map.getDelimitadorMapa() : ";");
            String temp = null;
            BigDecimal vlrTemp = new BigDecimal("0");

            for (String elemento : elementos) {
                temp = elemento;
                if (cacheSaida.containsKey(temp)) {
                    temp = cacheSaida.get(temp) != null ? cacheSaida.get(temp).toString() : null;
                } else if (cacheEntrada.containsKey(temp)) {
                    temp = cacheEntrada.get(temp) != null ? cacheEntrada.get(temp).toString() : null;
                }
                try {
                    if (temp != null) {
                        vlrTemp = vlrTemp.add(new BigDecimal(temp));
                    }
                } catch (Exception e) {
                    throw new ParserException("mensagem.erro.tradutor.valor.formato.incorreto.para.campo.numerico", (AcessoSistema) null, temp, valorSaida);
                }
            }
            valorSaida = vlrTemp.toString();

            if (gerarValorNumerico) {
                valorNumerico = vlrTemp;
            }
        } else if(tipo.equalsIgnoreCase("Multiplicar") && map.getFormatoEntrada() != null) {
            // Formato de Entrada: elementos a serem multiplicados

            String elementos[] = TextHelper.split(map.getFormatoEntrada(), map.getDelimitadorMapa() != null ? map.getDelimitadorMapa() : ";");
            String temp = null;
            BigDecimal vlrTemp = new BigDecimal("1");

            for (String elemento : elementos) {
                temp = elemento;
                if (cacheSaida.containsKey(temp)) {
                    temp = cacheSaida.get(temp) != null ? cacheSaida.get(temp).toString() : null;
                } else if (cacheEntrada.containsKey(temp)) {
                    temp = cacheEntrada.get(temp) != null ? cacheEntrada.get(temp).toString() : null;
                }

                try {
                    if (temp != null) {
                        vlrTemp = vlrTemp.multiply(new BigDecimal(temp));
                    }
                } catch (Exception e) {
                    throw new ParserException("mensagem.erro.tradutor.valor.formato.incorreto.para.campo.numerico", (AcessoSistema) null, temp, valorSaida);
                }
                valorSaida = vlrTemp.toString();
            }

            if (gerarValorNumerico) {
                valorNumerico = vlrTemp;
            }
        } else if(tipo.equalsIgnoreCase("Dividir") && map.getFormatoEntrada() != null) {
            // Formato de Entrada: elementos a serem divididos

            String elementos[] = TextHelper.split(map.getFormatoEntrada(), map.getDelimitadorMapa() != null ? map.getDelimitadorMapa() : ";");
            String temp = null;
            BigDecimal vlrTemp = null;

            for (String elemento : elementos) {
                temp = elemento;
                if (cacheSaida.containsKey(temp)) {
                    temp = cacheSaida.get(temp) != null ? cacheSaida.get(temp).toString() : null;
                } else if (cacheEntrada.containsKey(temp)) {
                    temp = cacheEntrada.get(temp) != null ? cacheEntrada.get(temp).toString() : null;
                }

                try {
                    if (temp != null) {
                        if (vlrTemp == null) {
                            vlrTemp = new BigDecimal(temp);
                        } else {
                            vlrTemp = vlrTemp.divide(new BigDecimal(temp), 2, java.math.RoundingMode.HALF_UP);
                        }
                    }
                } catch (Exception e) {
                    throw new ParserException("mensagem.erro.tradutor.valor.formato.incorreto.para.campo.numerico", (AcessoSistema) null, temp, valorSaida);
                }
                valorSaida = vlrTemp.toString();
            }

            if (gerarValorNumerico) {
                valorNumerico = vlrTemp;
            }
        } else if (tipo.equalsIgnoreCase("Data") && map.getDefault() != null &&
                map.getDefault().equalsIgnoreCase("DataAtual")) {
            Date hoje = DateHelper.getSystemDatetime();
            String formatoSaida = (map.getFormatoSaida() != null) ? map.getFormatoSaida() : LocaleHelper.getDatePattern();
            valorSaida = DateHelper.format(hoje, formatoSaida);

        } else if (tipo.equalsIgnoreCase("Data") && map.getDefault() != null &&
                map.getDefault().equalsIgnoreCase("PeriodoAtual")) {

            try {
                java.sql.Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(null, AcessoSistema.getAcessoUsuarioSistema());
                String formatoSaida = (map.getFormatoSaida() != null) ? map.getFormatoSaida() : LocaleHelper.getDatePattern();
                valorSaida = DateHelper.format(periodoAtual, formatoSaida);
            } catch (Exception ex) {
                LOG.warn("AVISO: possível problema na formatação do campo " + map.getEntrada() + ", valor: " + entrada + ". Valor padrão para o campo atribuído.");
                valorSaida = map.getDefault();
            }
        } else if (tipo.equalsIgnoreCase("Data") && map.getDefault() != null &&
                map.getDefault().equalsIgnoreCase("PeriodoAnterior")) {

            try {
                java.sql.Date periodoAnterior = PeriodoHelper.getInstance().getPeriodoAnterior(null, AcessoSistema.getAcessoUsuarioSistema());
                String formatoSaida = (map.getFormatoSaida() != null) ? map.getFormatoSaida() : LocaleHelper.getDatePattern();
                valorSaida = DateHelper.format(periodoAnterior, formatoSaida);
            } catch (Exception ex) {
                LOG.warn("AVISO: possível problema na formatação do campo " + map.getEntrada() + ", valor: " + entrada + ". Valor padrão para o campo atribuído.");
                valorSaida = map.getDefault();
            }
        } else if ((tipo.equalsIgnoreCase("DataDiff") || tipo.equalsIgnoreCase("DataTempoDiff")) && map.getFormatoEntrada() != null) {
            // Formato de Entrada: datas para realização do calculo
            // Formato de Saida: Forma de Calculo

            String elementos[] = TextHelper.split(map.getFormatoEntrada(), map.getDelimitadorMapa() != null ? map.getDelimitadorMapa() : ";");
            if (elementos != null && elementos.length > 0) {
                String dataIni = null;
                String dataFim = null;

                if (cacheSaida.containsKey(elementos[0])) {
                    dataIni = cacheSaida.get(elementos[0]) != null ? cacheSaida.get(elementos[0]).toString() : null;
                } else if (cacheEntrada.containsKey(elementos[0])) {
                    dataIni = cacheEntrada.get(elementos[0]) != null ? cacheEntrada.get(elementos[0]).toString() : null;
                }

                if (elementos.length > 1) {
                    if (cacheSaida.containsKey(elementos[1])) {
                        dataFim = cacheSaida.get(elementos[1]) != null ? cacheSaida.get(elementos[1]).toString() : null;
                    } else if (cacheEntrada.containsKey(elementos[1])) {
                        dataFim = cacheEntrada.get(elementos[1]) != null ? cacheEntrada.get(elementos[1]).toString() : null;
                    }
                } else {
                    dataFim = DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM-dd");
                }

                if (dataIni != null && dataFim != null) {
                    String calculo = map.getFormatoSaida() != null ? map.getFormatoSaida() : "PRAZO";
                    if (tipo.equalsIgnoreCase("DataDiff")) {
                        valorSaida = String.valueOf(DateHelper.dateDiff(dataIni, dataFim, "yyyy-MM-dd", null, calculo));
                    } else  {//"DataTempoDiff"
                        valorSaida = String.valueOf(DateHelper.dateDiff(dataIni, dataFim, "yyyy-MM-dd HH:mm:ss", null, calculo));
                    }
                } else {
                    valorSaida = map.getDefault();
                }

            } else {
                valorSaida = map.getDefault();
            }

        } else if (tipo.equalsIgnoreCase("IF")) {
            if (TextHelper.isNull(map.getFormatoEntrada()) || TextHelper.isNull(map.getFormatoSaida())) {
                throw new ParserException("mensagem.erro.tradutor.especificar.formatos.entrada.saida.operacao.if", (AcessoSistema) null);
            }

            /**
             * Entrada="ENTRADA"
             * SAIDA="SAIDA"
             * Tipo="IF"
             * FormatoEntrada="N1;OPERADOR;N2"
             * FormatoSaida="R1;R2"
             *
             * Pseudo-Código:
             *
             * if  (N1 OPERADOR N2) {
             *     SAIDA=R1
             * } else {
             *     SAIDA=R2
             * }
             *
             * Observações:
             * N1 e N2 têm que ser numéricos (se não, lança Exceção)
             * Valores permitidos para o OPERADOR => "IGUAL", "DIFERENTE", "MENOR", "MAIOR", "MENOR_IGUAL", "MAIOR_IGUAL" (se não, lança Exceção)
             * O valor de entrada deverá ser nulo (senão não executa operação)
             * Será procurado nos caches os valores N1, N2, R1 e R2 e se estes existirem, se usará o valor do cache.
             */

            try {
                String[] operadores = map.getFormatoEntrada().split(map.getDelimitadorMapa() != null ? map.getDelimitadorMapa() : ";");
                String[] resultados = map.getFormatoSaida().split(map.getDelimitadorMapa() != null ? map.getDelimitadorMapa() : ";");

                double operadorEsq = Double.parseDouble(getCacheObject(operadores[0], cacheEntrada, cacheSaida).toString());
                double operadorDir = Double.parseDouble(getCacheObject(operadores[2], cacheEntrada, cacheSaida).toString());
                String operadorMeio = operadores[1];
                boolean resultadoOperador = false;

                if (operadorMeio.equalsIgnoreCase("IGUAL")) {
                    resultadoOperador = operadorEsq == operadorDir;
                } else if (operadorMeio.equalsIgnoreCase("MENOR")) {
                    resultadoOperador = operadorEsq < operadorDir;
                } else if (operadorMeio.equalsIgnoreCase("MAIOR")) {
                    resultadoOperador = operadorEsq > operadorDir;
                } else if (operadorMeio.equalsIgnoreCase("MENOR_IGUAL")) {
                    resultadoOperador = operadorEsq <= operadorDir;
                } else if (operadorMeio.equalsIgnoreCase("MAIOR_IGUAL")) {
                    resultadoOperador = operadorEsq >= operadorDir;
                } else if (operadorMeio.equalsIgnoreCase("DIFERENTE")) {
                    resultadoOperador = operadorEsq != operadorDir;
                } else {
                    throw new ParserException("mensagem.erro.tradutor.operador.invalido", (AcessoSistema) null, operadorMeio);
                }

                Object valorResultado = null;
                if (resultadoOperador) {
                    valorResultado = getCacheObject(resultados[0], cacheEntrada, cacheSaida);
                } else {
                    valorResultado = getCacheObject(resultados[1], cacheEntrada, cacheSaida);
                }

                if (valorResultado != null) {
                    valorSaida = valorResultado.toString();
                } else {
                    valorSaida = map.getDefault();
                }

            } catch (NumberFormatException ex) {
                throw new ParserException("mensagem.erro.tradutor.operadores.devem.ser.numericos", (AcessoSistema) null);
            } catch (IndexOutOfBoundsException ex) {
                throw new ParserException("mensagem.erro.tradutor.parametros.invalidos", (AcessoSistema) null);
            }

        } else if (tipo.equalsIgnoreCase("Contador")) {
            /**
             * Tipo de formatação utilizado para contar registros ou lotes.
             * FormatoEntrada: Filtros de Incremento
             * FormatoSaida:   Filtros de Reinicialização
             * Exemplos:
             * Saida:C1 Tipo:Contador Entrada:       Saida:CSA;20 | Incrementa sempre e reinicia quando o filtro mudar ou o limite for alcançado
             * Saida:C2 Tipo:Contador Entrada:CSA;20 Saida:       | Incrementa quando o filtro mudar ou o limite for alcançado e nunca reinicia
             * Saida:C3 Tipo:Contador Entrada:20     Saida:CSA    | Incrementa quando o limite for alcançado e reinicia quando o filtro mudar
             * Saida:C4 Tipo:Contador Entrada:CSA    Saida:20     | Incrementa quando o filtro mudar e reinicia quando o limite for alcançado (Não serve pra nada)
             * Saida:C5 Tipo:Contador Entrada:       Saida:       | Incrementa sempre e nunca reincia
             * Saida:C6 Tipo:Contador Entrada:CSA    Saida:       | Incrementa quando o filtro mudar e nunca reinicia
             * Saida:C7 Tipo:Contador Entrada:       Saida:CSA    | Incrementa sempre e reinicia quando o filtro mudar
             * Saida:C8 Tipo:Contador Entrada:       Saida:20     | Incrementa sempre e reinicia quando o limite for alcançado
             * Saida:C9 Tipo:Contador Entrada:20     Saida:       | Incrementa quando o limite for alcançado e nunca reinicia
             */

            // Filtros para definirem quando o contador deve ser incrementado (Opcional, se igual a vazio sempre incrementa)
            String formatoEntrada = map.getFormatoEntrada();
            // Filtros para definirem quando o contador deve ser reiniciado (Opcional, se igual a vazio nunca reinicia)
            String formatoSaida = map.getFormatoSaida();

            // Obtém o contador de controle atual
            String chaveContador = map.getSaida() + "_contador";
            Integer contadorControle = (Integer) cacheTradutor.get(chaveContador);
            if (contadorControle == null) {
                // Se não existe o contador de controle, cria um com o valor Um
                contadorControle = Integer.valueOf(1);
            } else {
                // Se existe, então incrementa o contador
                contadorControle = Integer.valueOf(contadorControle.intValue() + 1);
            }
            cacheTradutor.put(chaveContador, contadorControle);


            // Obtém a variável de controle atual de incremento
            String chaveControleIncremento = map.getSaida() + "_controle_incremento";
            String controleIncremento = (String) cacheTradutor.get(chaveControleIncremento);

            // Determina a nova variável de controle de incremento
            int limiteIncremento = Integer.MAX_VALUE;
            String novoControleIncremento = "";
            if (!TextHelper.isNull(formatoEntrada)) {
                String[] filtros = formatoEntrada.split(";");
                for (String filtro : filtros) {
                    String valorFiltro = "";
                    if (cacheSaida.containsKey(filtro)) {
                        valorFiltro = cacheSaida.get(filtro) != null ? cacheSaida.get(filtro).toString() : "";
                    } else if (cacheEntrada.containsKey(filtro)) {
                        valorFiltro = cacheEntrada.get(filtro) != null ? cacheEntrada.get(filtro).toString() : "";
                    } else {
                        try {
                            // Se o filtro não está nos Maps e é um inteiro, verifica o limite
                            // de registros de acordo com o contador atual
                            limiteIncremento = Integer.parseInt(filtro);
                        } catch (NumberFormatException ex) {
                            // O filtro não existe nos Maps e nem é um limite numérico
                        }
                    }
                    novoControleIncremento += valorFiltro + ";";
                }
            }
            if (controleIncremento == null) {
                controleIncremento = novoControleIncremento;
            }
            cacheTradutor.put(chaveControleIncremento, novoControleIncremento);

            // Obtém a variável de controle atual de reinicialização
            String chaveControleReinicializacao = map.getSaida() + "_controle_reinicializacao";
            String controleReinicializacao = (String) cacheTradutor.get(chaveControleReinicializacao);

            // Determina a nova variável de controle de reinicialização
            int limiteReinicializacao = Integer.MAX_VALUE;
            String novoControleReinicializacao = "";
            if (!TextHelper.isNull(formatoSaida)) {
                String[] filtros = formatoSaida.split(";");
                for (String filtro : filtros) {
                    String valorFiltro = "";
                    if (cacheSaida.containsKey(filtro)) {
                        valorFiltro = cacheSaida.get(filtro) != null ? cacheSaida.get(filtro).toString() : "";
                    } else if (cacheEntrada.containsKey(filtro)) {
                        valorFiltro = cacheEntrada.get(filtro) != null ? cacheEntrada.get(filtro).toString() : "";
                    } else {
                        try {
                            // Se o filtro não está nos Maps e é um inteiro, verifica o limite
                            // de registros de acordo com o contador atual
                            limiteReinicializacao = Integer.parseInt(filtro);
                        } catch (NumberFormatException ex) {
                            // O filtro não existe nos Maps e nem é um limite numérico
                        }
                    }
                    novoControleReinicializacao += valorFiltro + ";";
                }
            }
            if (controleReinicializacao == null) {
                controleReinicializacao = novoControleReinicializacao;
            }
            cacheTradutor.put(chaveControleReinicializacao, novoControleReinicializacao);


            // Obtém o valor atual do contador
            String vlrContadorAtual = cacheTradutor.get(map.getSaida()) != null ? cacheTradutor.get(map.getSaida()).toString() : "0";

            if (TextHelper.isNull(formatoEntrada)
                    || !controleIncremento.equals(novoControleIncremento)
                    || (contadorControle.intValue() > limiteIncremento)
                    || vlrContadorAtual.equals("0")) {
                // Se não tem filtro de incremento, então sempre incrementa.
                // Ou se tem filtro de incremento, mas as chaves mudaram ou
                // o limite foi alcançado, então incrementa o contador
                vlrContadorAtual = String.valueOf(Integer.parseInt(vlrContadorAtual) + 1);
            }

            if (!TextHelper.isNull(formatoSaida)) {
                // Tem filtro de reinicialização, então reinicia o contador impresso
                // caso o limite de reinicialização seja alcançado ou a chave mude
                if (!controleReinicializacao.equals(novoControleReinicializacao)
                        || (contadorControle.intValue() > limiteReinicializacao)) {
                    vlrContadorAtual = "1";
                }
            }

            // Reinica o contador interno caso os limites sejam alcançados
            // (tanto o de incremento quanto o de reinicialização) ou as
            // chaves mudarem (ambas novamente)
            if (!controleReinicializacao.equals(novoControleReinicializacao)
                    || !controleIncremento.equals(novoControleIncremento)
                    || (contadorControle.intValue() > limiteReinicializacao)
                    || (contadorControle.intValue() > limiteIncremento)) {
                cacheTradutor.put(chaveContador, Integer.valueOf(1));
            }

            cacheTradutor.put(map.getSaida(), vlrContadorAtual);


            // Retorna o valor encontrado
            valorSaida = vlrContadorAtual;

        } else if (tipo.equalsIgnoreCase("CPF Sequencial")) {
            // DESENV-7639 Preenchimento do CPF com valor sequencial no processamento do arquivo de Margem
            valorSaida = GeradorCpfServidor.getInstance().getNext();
        } else {
            valorSaida = map.getDefault();
        }

        Map<String, Object> saida = new HashMap<>(gerarValorNumerico ? 2 : 1);
        saida.put(KEY_VALOR_SAIDA, valorSaida);
        if (gerarValorNumerico && (tipo.equalsIgnoreCase("Numerico") || tipo.equalsIgnoreCase("Inteiro") ||
                tipo.equalsIgnoreCase("Soma") || tipo.equalsIgnoreCase("Multiplicar") || tipo.equalsIgnoreCase("Dividir"))) {
            saida.put(KEY_VALOR_NUMERICO, valorNumerico);
        }

        return saida;
    }

    private static Object getCacheObject(String key, Map<String, Object> cacheEntrada, Map<String, Object> cacheSaida) {
        Object value = key;
        if (cacheSaida.containsKey(value)) {
            value = cacheSaida.get(value) != null ? cacheSaida.get(value).toString() : null;
        } else if (cacheEntrada.containsKey(value)) {
            value = cacheEntrada.get(value) != null ? cacheEntrada.get(value).toString() : null;
        }
        return value;
    }
}