package com.zetra.econsig.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.config.AtributoTipo;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: LeitorArquivoTextoSimpletl</p>
 * <p>Description: Implementação do Leitor para ArquivoTexto.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LeitorArquivoTexto extends ArquivoTexto implements Leitor {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LeitorArquivoTexto.class);

    protected BufferedReader in;
    protected String linha;
    protected String linhaFooter;
    protected String linhaHeader;
    protected Map<String, Object> valoresHeader;
    protected int numLinhas;
    protected int linhaCorrente;

    public LeitorArquivoTexto(String nomearqentrada) {
        super(nomearqentrada);
    }

    public LeitorArquivoTexto(String nomearqconfig, String nomearqentrada) {
        super(nomearqconfig, nomearqentrada);
    }

    public LeitorArquivoTexto(String nomearqconfig, String nomearqentrada, boolean ignoraHeaderFooter) {
        super(nomearqconfig, nomearqentrada);

        if (ignoraHeaderFooter) {
            temHeader = false;
            temFooter = false;
            tipoHeader = -1;
            tipoFooter = -1;
            headers = new ArrayList<>();
            footers = new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> le() throws ParserException {

        leArquivo();

        if (comentario != null) {
            // Se no xml foi especificado delimitador de comentario
            while (linha != null && linha.startsWith(comentario)) {
                leArquivo();
            }
        }

        if (comentario_regex != null) {
            // Se no xml foi especificado delimitador de comentario_regex
            while (linha != null && linha.matches(comentario_regex)) {
                leArquivo();
            }
        }

        Map<String, Object> retorno = null;

        if ((linha != null) && (linha.length() > 0) &&
                (numLinhas >= linhaCorrente)) {

            if (larguraMin != -1 && linha.length() < larguraMin) {
                throw new ParserException("mensagem.erro.leitor.arquivo.numero.caracteres.minimo.permitido.leiaute", (AcessoSistema) null, String.valueOf(larguraMin), String.valueOf(linhaCorrente));
            }

            if (larguraMax != -1 && linha.length() > larguraMax) {
                throw new ParserException("mensagem.erro.leitor.arquivo.numero.caracteres.maximo.permitido.leiaute", (AcessoSistema) null, String.valueOf(larguraMax), String.valueOf(linhaCorrente));
            }

            retorno = formataEntrada(doc.getAtributo(), linha);
            retorno.putAll(valoresHeader);
        }

        return retorno;
    }

    protected Map<String, Object> formataEntrada(List<AtributoTipo> attrConfig, String linhaEntrada) throws ParserException {
        Map<String, Object> retorno = new HashMap<>();
        Iterator<AtributoTipo> it = attrConfig.iterator();

        String valores[] = null;
        int inicio = 0;
        int fim = 0;
        while (it.hasNext()) {
            AtributoTipo atr = it.next();

            // Arquivo texto de entrada com valores separador por um delimitador
            if (delimitador != null) {
                valores = linhaEntrada.split(delimitador);
                // Arquivo texto de entrada com valores em posições definidas
            } else {
                inicio = atr.getInicio();
                fim = inicio + atr.getTamanho();
                if (fim > linhaEntrada.length()) {
                    fim = linhaEntrada.length();
                }
            }
            StringBuilder temp = new StringBuilder();
            try {
                if (delimitador != null) {
                    if (atr.getIndice() < valores.length) {
                        temp.append(valores[atr.getIndice()]);
                    }
                } else {
                    temp.append(linhaEntrada.substring(inicio, fim));
                }
            } catch (StringIndexOutOfBoundsException ex) {
                throw new ParserException("mensagem.erro.leitor.arquivo.formato.incorreto.linha.numero", (AcessoSistema) null, String.valueOf(linhaCorrente));
            }

            if (temp.length() > 0) {
                String alin = atr.getAlinhamento();
                if (alin != null) {
                    String[] complementos = (atr.getComplemento() != null && atr.getComplemento().length() > 1) ?
                            (atr.getComplemento().split(";")) :
                            (atr.getComplemento() != null ? new String[] {
                            atr.getComplemento()}
                            : new String[] {
                            " "});

                    for (String complemento : complementos) {
                    	char compl;
                    	if(complemento != null && complemento.length() > 0){
                    		compl = complemento.charAt(0);
                    	} else {
                    		compl = ' ';
                    	}
                        if ((alin.equalsIgnoreCase("direita")) || (alin.equalsIgnoreCase("centro"))) {
                            while ((temp.length() > 0) && (temp.charAt(0) == compl)) {
                                temp.deleteCharAt(0);
                            }
                        }
                        if ((alin.equalsIgnoreCase("esquerda")) || (alin.equalsIgnoreCase("centro"))) {
                            while ((temp.length() > 0) && (temp.charAt(temp.length() - 1) == compl)) {
                                temp.deleteCharAt(temp.length() - 1);
                            }
                        }
                    }
                }
            }

            if ((temp == null) || (temp.length() == 0)) {
                if (atr.getDefault() != null) {
                    retorno.put(atr.getNome(), atr.getDefault());
                } else if (atr.getValor() != null) {
                    retorno.put(atr.getNome(), atr.getValor());
                } else {
                    retorno.put(atr.getNome(), null);
                }
            } else {
                retorno.put(atr.getNome(), temp.toString());
            }
        }
        return retorno;
    }

    protected void leArquivo() {
        try {
            linha = "";
            int i = 0;
            do {
                if (linhaCorrente >= numLinhas && i > 0) {
                    LOG.error("Erro(LeitorArquivoTexto.leArquivo) Quantidade de linhas inválidas - linha corrente: " + linhaCorrente + " linha: " + linha);
                    linha = null;
                    return;
                }

                if (!TextHelper.isNull(linha)){
                    linha += (TextHelper.isNull(delimitador) ? " " : delimitador);
                }

                String linhaAtual = in.readLine();
                if (linhaAtual != null) {
                    linha += linhaAtual;
                } else {
                    linha = null;
                }

                linhaCorrente++;
                i++;
            } while (i < linhas_por_registro && !TextHelper.isNull(linha));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            linha = null;
        }
    }

    public String getLinha() {
        return linha;
    }

    public String getLinhaFooter() {
        return linhaFooter;
    }

    public String getLinhaHeader() {
        return linhaHeader;
    }

    public int getNumeroLinha() {
        return linhaCorrente;
    }

    @Override
    public void iniciaLeitura() throws ParserException {
        LOG.debug("COMENTARIO: " + comentario);
        LOG.debug("LARGURAMAX: " + larguraMax);
        LOG.debug("LARGURAMIN: " + larguraMin);
        LOG.debug("LIMITE: " + limite);

        // Inicializa alguns valores
        valoresHeader = new HashMap<>();
        numLinhas = Integer.MAX_VALUE;
        linhaCorrente = 0;

        try {
            // Remove os vazios no final do arquivo
            FileHelper.trimTextFile(nomeArquivo);

            // Valida o tamanho do arquivo, caso o parametro limite seja especificado no xml
            if (limite != -1) {
                numLinhas = FileHelper.getNumberOfLines(nomeArquivo);
                if (numLinhas > limite) {
                    throw new ParserException("mensagem.erro.leitor.arquivo.numero.maximo.linhas", (AcessoSistema) null, String.valueOf(limite));
                }
            }

            // Se for multiplas linhas por registro e ter header ou footer da erro
            if ((temFooter || temHeader) && linhas_por_registro > 1) {
                throw new ParserException("mensagem.erro.leitor.arquivo.multiplos.registros.header.footer", (AcessoSistema) null);
            }

            if (temFooter) {
                if (tipoFooter == POSICIONAL_TOTAL || tipoFooter == DELIMITADO_TOTAL) {
                    linhaFooter = FileHelper.readLastLine(nomeArquivo);
                    // Pega o número de linhas do arquivo e subtrai uma, que é o rodapé, para que ele não seja lido
                    numLinhas = FileHelper.getNumberOfLines(nomeArquivo) - 1;

                    if (linhaFooter.endsWith("\r") || linhaFooter.endsWith("\n\r")) {
                        numLinhas -= 1;
                    }

                    if (doc.getFooter().getAtributo().size() == 0) {
                        // Calcula quantidade de registros para validação com o rodapé
                        int qtdRegistros = numLinhas;
                        // Subtrai o header da quantidade de registros para validação
                        if (temHeader) {
                            qtdRegistros--;
                        }

                        // Transforma para inteiro a quantidade de registros presente na última linha do arquivo
                        int valorRodape = Integer.parseInt(linhaFooter);

                        // Se o número de registros for diferente do especificado, gera uma exeção
                        if (qtdRegistros != valorRodape) {
                            throw new ParserException("mensagem.erro.leitor.arquivo.numero.registros.especificados.inconsistente.conteudo", (AcessoSistema) null);
                        }
                    }
                }
            }

            // Abre o FileInputStream
            in = new BufferedReader(new FileReader(nomeArquivo));
            if (temHeader) {
                if (tipoHeader == POSICIONAL_TOTAL || tipoHeader == DELIMITADO_TOTAL) {
                    // Lê e Guarda as informações do Header em um hash para passar ao Tradutor
                    leArquivo();
                    linhaHeader = linha;
                    valoresHeader.putAll(formataEntrada(doc.getHeader().getAtributo(), linhaHeader));
                } else if (tipoHeader == POSICIONAL_LOTE && doc.getID().equalsIgnoreCase(CodedValues.CODIGO_ID_FEBRABAN)) {
                    // Arquivo padrão FEBRABAN CNAB240-081
                } else {
                    throw new ParserException("mensagem.erro.leitor.arquivo.configuracao.header.entrada.incorreta", (AcessoSistema) null);
                }
            }

        } catch (FileNotFoundException ex) {
            throw new ParserException("mensagem.erro.arquivo.invalido.inexistente", (AcessoSistema) null, ex);
        } catch (IOException ex) {
            throw new ParserException("mensagem.erro.leitor.arquivo.falha.processamento", (AcessoSistema) null, ex, String.valueOf(nomeArquivo));
        }
    }

    @Override
    public void encerraLeitura() throws ParserException {
        try {
            in.close();
        } catch (IOException ex) {
            throw new ParserException("mensagem.erro.leitor.arquivo.falha.processamento", (AcessoSistema) null, ex, String.valueOf(nomeArquivo));
        }
    }
}
