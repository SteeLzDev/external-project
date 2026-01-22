package com.zetra.econsig.parser.febraban;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.febraban.exception.ArquivoInvalidoException;
import com.zetra.econsig.parser.febraban.exception.FebrabanException;
import com.zetra.econsig.parser.febraban.exception.LoteInvalidoException;
import com.zetra.econsig.parser.febraban.exception.RegistroInvalidoException;

/**
 * <p>Title: Analisador</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas e Leonel Martins
 * $Author$
 * $Revision$
 * $Date$
 */
public class Analisador {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Analisador.class);

    /******************************* CONSTANTES **********************************/

    // Código com as ocorrências de processamento
    public static final String OCORRENCIA_SUCESSO                         = "";
    public static final String OCORRENCIA_CONTROLE_INVALIDO               = "AA";
    public static final String OCORRENCIA_TIPO_OPERACAO_INVALIDO          = "AB";
    public static final String OCORRENCIA_TIPO_SERVICO_INVALIDO           = "AC";
    public static final String OCORRENCIA_NUM_SEQ_REGISTRO_LOTE_INVALIDO  = "AH";
    public static final String OCORRENCIA_CODIGO_SEGMENTO_INVALIDO        = "AI";
    public static final String OCORRENCIA_TIPO_MOVIMENTO_INVALIDO         = "AJ";
    public static final String OCORRENCIA_DATA_LANCAMENTO_INVALIDA        = "AP";
    public static final String OCORRENCIA_VALOR_PARCELA_INVALIDA          = "BL";
    public static final String OCORRENCIA_LOTE_SERVICO_FORA_SEQUENCIA     = "HG";
    public static final String OCORRENCIA_LOTE_SERVICO_INVALIDO           = "HH";
    public static final String OCORRENCIA_TIPO_REGISTRO_INVALIDO          = "HJ";
    public static final String OCORRENCIA_CODIGO_REMESSA_RETORNO_INVALIDO = "HK";
    public static final String OCORRENCIA_VERSAO_LAYOUT_INVALIDA          = "HL";
    public static final String OCORRENCIA_MUTUARIO_NAO_IDENTIFICADO       = "HM";
    public static final String OCORRENCIA_QTD_PARCELAS_INVALIDA           = "HV";
    public static final String OCORRENCIA_TOTAIS_LOTE_COM_DIFERENCA       = "TA";
    public static final String OCORRENCIA_INVALIDO_PADRAO                 = "YC";

    // Indica o tamanho de caracteres da linha do padrão febraban
    public static final int TAMANHO_LINHA_CNAB = 240;

    // Posição na linha que indica o tipo de registro no padrão febraban
    public static final int POSICAO_TIPO_REGISTRO = 7;

    // Posição na linha que indica o começo do campos das ocorrencias
    public static final int POSICAO_INICIO_OCORRENCIAS = 230;

    // Indica o tamanho de caracteres do campo de ocorrencias
    public static final int TAMANHO_CAMPO_OCORRENCIAS = 10;

    // Complemento do campo de ocorrencias
    public static final String COMPLEMENTO_DEFAULT_CAMPO_OCORRENCIAS = " ";

    // Código para o layout do serviço
    public static final String VERSAO_LAYOUT_LOTE = "011";

    // Código para o segmento aceito
    private static final String CODIGO_SEGMENTO = "H";

    // Tipo de operação para empréstimo
    private static final String TIPO_OPERACAO_EMPRESTIMO = "2";

    // Código do campo indicador de remessa/retorno
    private static final String TIPO_CODIGO_REMESSA = "2";

    // Identificador do lote de serviço para header e trailer de arquivo
    private static final String CODIGO_LOTE_SERVICO_HEADER_ARQUIVO = "0000";
    private static final String CODIGO_LOTE_SERVICO_TRAILER_ARQUIVO = "9999";

    // Código para validar o tipo de registro do header de arquivo, de modo que
    // o primeiro tipo de registro deve ser 0 (zero)
    private static final char TIPO_REGISTRO_INICIAL = 'X';

    // Tipos de registros permitidos no padrão febraban
    private static final char TIPO_REGISTRO_HEADER_ARQUIVO = '0';
    private static final char TIPO_REGISTRO_HEADER_LOTE = '1';
    private static final char TIPO_REGISTRO_DETALHE = '3';
    private static final char TIPO_REGISTRO_TRAILER_LOTE = '5';
    private static final char TIPO_REGISTRO_TRAILER_ARQUIVO = '9';

    // Tipos de Validação para os campos do padrão febraban
    private static final Integer OPCIONAL = Integer.valueOf(0);
    private static final Integer NUMERICO_OBRIGATORIO = Integer.valueOf(1);
    private static final Integer NUMERICO = Integer.valueOf(2);
    private static final Integer OBRIGATORIO = Integer.valueOf(3);
    private static final Integer DATA = Integer.valueOf(4);

    // Descrição dos campos para cada tipo de registro do arquivo que devem ser validados
    // 0 - Nome campo, 1 - Inicio e fim do campo, 2 - Tipo de Validação,
    // 3 - Ocorrência em caso de erro
    private static final Object[][] CAMPOS_HEADER_ARQUIVO = {
            {"LOTE_SERVICO",    new int[] {3, 7},     NUMERICO_OBRIGATORIO, OCORRENCIA_LOTE_SERVICO_INVALIDO},
            {"TIPO_REGISTRO",   new int[] {7, 8},     NUMERICO_OBRIGATORIO, OCORRENCIA_TIPO_REGISTRO_INVALIDO},
            {"REMESSA/RETORNO", new int[] {142, 143}, NUMERICO_OBRIGATORIO, OCORRENCIA_CODIGO_REMESSA_RETORNO_INVALIDO},
            {"LEIAUTE_ARQUIVO", new int[] {163, 166}, OPCIONAL, OCORRENCIA_VERSAO_LAYOUT_INVALIDA}
    };

    private static final Object[][] CAMPOS_TRAILER_ARQUIVO = {
            {"LOTE_SERVICO",  new int[] {3, 7},   NUMERICO_OBRIGATORIO, OCORRENCIA_LOTE_SERVICO_INVALIDO},
            {"TIPO_REGISTRO", new int[] {7, 8},   NUMERICO_OBRIGATORIO, OCORRENCIA_TIPO_REGISTRO_INVALIDO},
            {"QTD_LOTES",     new int[] {17, 23}, NUMERICO_OBRIGATORIO, OCORRENCIA_TOTAIS_LOTE_COM_DIFERENCA},
            {"QTD_REGISTROS", new int[] {23, 29}, NUMERICO_OBRIGATORIO, OCORRENCIA_INVALIDO_PADRAO}
    };

    private static final Object[][] CAMPOS_HEADER_LOTE = {
            {"CODIGO_AVERBACAO", new int[] {3, 7},   NUMERICO_OBRIGATORIO, OCORRENCIA_INVALIDO_PADRAO},
            {"TIPO_REGISTRO",    new int[] {7, 8},   NUMERICO_OBRIGATORIO, OCORRENCIA_TIPO_REGISTRO_INVALIDO},
            {"TIPO_SERVICO",     new int[] {9, 11},  NUMERICO_OBRIGATORIO, OCORRENCIA_TIPO_SERVICO_INVALIDO},
            {"LEIAUTE_SERVICO",  new int[] {11, 14}, NUMERICO_OBRIGATORIO, OCORRENCIA_VERSAO_LAYOUT_INVALIDA},
            {"LOTE_SERVICO",     new int[] {20, 24}, NUMERICO_OBRIGATORIO, OCORRENCIA_LOTE_SERVICO_INVALIDO},
            {"NUM_SEQ_LOTE",     new int[] {24, 31}, NUMERICO_OBRIGATORIO, OCORRENCIA_INVALIDO_PADRAO},
            {"CODIGO_CONVENIO",  new int[] {52, 71}, OBRIGATORIO, OCORRENCIA_INVALIDO_PADRAO}
    };

    private static final Object[][] CAMPOS_TRAILER_LOTE = {
            {"LOTE_SERVICO",  new int[] {3, 7},   NUMERICO_OBRIGATORIO, OCORRENCIA_LOTE_SERVICO_INVALIDO},
            {"TIPO_REGISTRO", new int[] {7, 8},   NUMERICO_OBRIGATORIO, OCORRENCIA_TIPO_REGISTRO_INVALIDO},
            {"NUM_SEQ_LOTE",  new int[] {8, 15},  NUMERICO_OBRIGATORIO, OCORRENCIA_INVALIDO_PADRAO},
            {"QTD_REGISTROS", new int[] {15, 21}, NUMERICO_OBRIGATORIO, OCORRENCIA_TOTAIS_LOTE_COM_DIFERENCA},
            // DESENV-21099 TS9 - Retirar validação de quantidade de parcelas trailer de lote
            //{"QTD_PARCELAS",  new int[] {21, 26}, NUMERICO_OBRIGATORIO, OCORRENCIA_QTD_PARCELAS_INVALIDA},
            {"SOMA_PARCELAS", new int[] {26, 41}, NUMERICO_OBRIGATORIO, OCORRENCIA_VALOR_PARCELA_INVALIDA}
    };

    private static final Object[][] CAMPOS_DETALHE = {
            {"LOTE_SERVICO",      new int[] {3, 7},     NUMERICO_OBRIGATORIO, OCORRENCIA_LOTE_SERVICO_INVALIDO},
            {"TIPO_REGISTRO",     new int[] {7, 8},     NUMERICO_OBRIGATORIO, OCORRENCIA_TIPO_REGISTRO_INVALIDO},
            {"NUM_SEQ_REGISTRO",  new int[] {8, 13},    NUMERICO_OBRIGATORIO, OCORRENCIA_NUM_SEQ_REGISTRO_LOTE_INVALIDO},
            {"SEGMENTO",          new int[] {13, 14},   OBRIGATORIO, OCORRENCIA_CODIGO_SEGMENTO_INVALIDO},
            {"TIPO_MOVIMENTO",    new int[] {14, 15},   NUMERICO_OBRIGATORIO, OCORRENCIA_TIPO_MOVIMENTO_INVALIDO},
            {"UNIDADE/ORGAO",     new int[] {45, 51},   OPCIONAL, OCORRENCIA_INVALIDO_PADRAO},
            {"CPF_MUTUARIO",      new int[] {51, 62},   NUMERICO, OCORRENCIA_INVALIDO_PADRAO},
            {"MATRICULA",         new int[] {62, 74},   OPCIONAL, OCORRENCIA_MUTUARIO_NAO_IDENTIFICADO},
            {"STATUS_MUTUARIO",   new int[] {74, 75},   NUMERICO, OCORRENCIA_INVALIDO_PADRAO},
            {"TIPO_OPERACAO",     new int[] {96, 97},   OBRIGATORIO, OCORRENCIA_TIPO_OPERACAO_INVALIDO},
            {"QTD_PARCELAS",      new int[] {107, 109}, NUMERICO, OCORRENCIA_QTD_PARCELAS_INVALIDA},
            {"DATA_INI",          new int[] {109, 117}, DATA, OCORRENCIA_DATA_LANCAMENTO_INVALIDA},
            {"DATA_FIM",          new int[] {117, 125}, DATA, OCORRENCIA_DATA_LANCAMENTO_INVALIDA},
            {"VLR_LIBERADO",      new int[] {125, 134}, NUMERICO, OCORRENCIA_INVALIDO_PADRAO},
            {"VLR_OPERACAO",      new int[] {134, 143}, NUMERICO, OCORRENCIA_INVALIDO_PADRAO},
            {"VLR_PARCELA",       new int[] {143, 152}, NUMERICO, OCORRENCIA_INVALIDO_PADRAO},
            {"VLR_SALDO_DEVEDOR", new int[] {152, 161}, NUMERICO, OCORRENCIA_INVALIDO_PADRAO},
            {"QTD_PARCELAS_EXT",  new int[] {221, 227}, NUMERICO, OCORRENCIA_QTD_PARCELAS_INVALIDA}
    };

    /******************************* VARIÁVEIS PARA A VALIDAÇÃO **********************************/

    private int contadorLote;
    private int contadorRegistroLote;
    private int contadorRegistroArquivo;
    private int contadorParcelas;
    private int contadorLinhas;
    private long somatorioParcelas;
    private char tipoRegistroAnterior;
    private final Map<String, Set<String>> cacheOcorrencias;

    /******************************* CONSTRUTOR DA CLASSE **********************************/

    public Analisador() {
        contadorLote = 0;
        contadorRegistroLote = 0;
        contadorRegistroArquivo = 0;
        contadorParcelas = 0;
        contadorLinhas = 0;
        somatorioParcelas = 0;
        tipoRegistroAnterior = TIPO_REGISTRO_INICIAL;
        cacheOcorrencias = new HashMap<>();
    }

    /******************************* MÉTODO PRINCIPAL **********************************/

    /**
     * Método principal para a execução da validação do arquivo de lote
     * padrão FEBRABAN CNAB 240. Executa duas passadas no arquivo, a primeira
     * é feita a validação e a inclusão de ocorrencias a nivel de registro.
     * Na segunda passada, as ocorrências em nivel de lote e arquivo são
     * gravadas nas linhas corretas.
     * @param nomeArquivo String
     * @return String
     * @throws ParserException
     */
    @SuppressWarnings("resource") // DESENV-11498 : falso positivo
    public String executar(String nomeArquivo) throws ParserException {
        String nomeArquivoCritica = nomeArquivo.substring(0, nomeArquivo.length() - 4) + "-critica.txt";
        String nomeArquivoTmp = nomeArquivo.substring(0, nomeArquivo.length() - 4) + "-tmp.txt";

        BufferedReader in = null;
        PrintWriter out = null;

        String linha = null;
        String ocorrencias = null;

        Map<String, String> dados = null;

        // Primeira passada: valida os dados dos registros gerando as informações de
        // erro. Apenas as ocorrências nos registros de detalhe são escritas no
        // arquivo de critica. As ocorrências de lote e de arquivo são armazenadas
        // no cache para serem utilizadas na segunda passada.
        try {
            in = new BufferedReader(new FileReader(nomeArquivo));
            out = new PrintWriter(new BufferedWriter(new FileWriter(nomeArquivoTmp)));

            while ((linha = in.readLine()) != null) {
                contadorLinhas++;

                if (linha.length() < TAMANHO_LINHA_CNAB) {
                    throw new ParserException("mensagem.erro.linha.arquivo.entrada.formato.incorreto", (AcessoSistema)null, String.valueOf(contadorLinhas));
                }

                try {
                    // Extrair os campos de acordo com os indices fixos do padrão febraban
                    dados = processarLinha(linha);
                    // Valida os campos presentes na linha de dados
                    ocorrencias = validarLinha(linha, dados);
                } catch (FebrabanException ex) {
                    ocorrencias = ex.getMessage();
                }

                if (linha.charAt(POSICAO_TIPO_REGISTRO) != TIPO_REGISTRO_DETALHE) {
                    // Imprime a linha original no arquivo de crítica,
                    // retirando as ocorrências originais
                    out.println(linha.substring(0, POSICAO_INICIO_OCORRENCIAS) + TextHelper.formataMensagem("", COMPLEMENTO_DEFAULT_CAMPO_OCORRENCIAS, TAMANHO_CAMPO_OCORRENCIAS, true));
                } else {
                    // Se for registro de detalhe, seta a ocorrencia na posição pre-definida
                    out.println(linha.substring(0, POSICAO_INICIO_OCORRENCIAS) + TextHelper.formataMensagem(ocorrencias, COMPLEMENTO_DEFAULT_CAMPO_OCORRENCIAS, TAMANHO_CAMPO_OCORRENCIAS, true));
                }

                // Seta o tipo de registro processado e termina a iteração
                tipoRegistroAnterior = linha.charAt(POSICAO_TIPO_REGISTRO);
            }

        } catch (FileNotFoundException ex) {
            LOG.error("O arquivo '" + nomeArquivo + "' não foi encontrado.", ex);
            return null;
        } catch (IOException ex) {
            LOG.error("Erro ao processar o arquivo '" + nomeArquivo + "'.", ex);
            return null;
        } catch (ParserException ex) {
            LOG.error("Erro ao processar o arquivo '" + nomeArquivo + "'.", ex);
            // Fecha o fluxo de escrita e remove o arquivo temporário criado
            out.close();
            (new File(nomeArquivoTmp)).delete();
            throw ex;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        // Segunda passada: as ocorrências a nivel de arquivo e lote são gravadas
        // nos registros específicos de acordo com o cache de ocorrencias. Se não
        // existir nenhuma ocorrência deste nivel, então a segunda passada não é
        // realizada e o arquivo de critica final será o arquivo temporário da
        // primeira passada.
        try {
            if (!cacheOcorrencias.isEmpty()) {
                in = new BufferedReader(new FileReader(nomeArquivoTmp));
                out = new PrintWriter(new BufferedWriter(new FileWriter(nomeArquivoCritica)));
                while ((linha = in.readLine()) != null) {
                    if (linha.charAt(POSICAO_TIPO_REGISTRO) == TIPO_REGISTRO_HEADER_LOTE ||
                            linha.charAt(POSICAO_TIPO_REGISTRO) == TIPO_REGISTRO_TRAILER_LOTE) {

                        try {
                            // Extrair os campos de acordo com os indices fixos do padrão febraban
                            dados = processarLinha(linha);
                        } catch (FebrabanException ex) {
                        }

                        // Gera a ocorrencia para o lote
                        ocorrencias = montarOcorrencia(dados.get("LOTE_SERVICO").toString());

                        // Concatena os dados do registro com a ocorrencia gerada
                        out.println(linha.substring(0, POSICAO_INICIO_OCORRENCIAS) + TextHelper.formataMensagem(ocorrencias, COMPLEMENTO_DEFAULT_CAMPO_OCORRENCIAS, TAMANHO_CAMPO_OCORRENCIAS, true));

                    } else {
                        // Caso não seja nem header de lote ou trailer de lote,
                        // imprime a linha original
                        out.println(linha);
                    }
                }
                // Fecha o fluxo de leitura do arquivo temporário e
                // remove o arquivo.
                in.close();
                (new File(nomeArquivoTmp)).delete();
            } else {
                // Se não tem ocorrencias a nivel de arquivo ou lote, então
                // renomeia o arquivo temporário para ser o arquivo de crítica
                File arqCritica = new File(nomeArquivoCritica);
                File arqTemp = new File(nomeArquivoTmp);
                arqCritica.delete();
                arqTemp.renameTo(new File(nomeArquivoCritica));
            }
        } catch (FileNotFoundException ex) {
            LOG.error("O arquivo '" + nomeArquivoTmp + "' não foi encontrado.", ex);
            return null;
        } catch (IOException ex) {
            LOG.error("Erro ao processar o arquivo '" + nomeArquivoTmp + "'.", ex);
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        return nomeArquivoCritica;
    }

    /******************************* MÉTODOS GERENCIAMENTO OCORRENCIAS *********************************/

    /**
     * Monta o campo de ocorrencia para um determinado lote.
     * Adiciona as ocorrencias de arquivo antes das ocorrencias
     * especificas do lote.
     * @param codigoLote String
     * @return String
     */
    private String montarOcorrencia(String codigoLote) {
        Set<String> ocorrenciasArquivo = cacheOcorrencias.get(CODIGO_LOTE_SERVICO_HEADER_ARQUIVO);
        Set<String> ocorrenciasLote = cacheOcorrencias.get(codigoLote);
        StringBuilder ocorrencia = new StringBuilder();

        // Adiciona as ocorrencias de arquivo
        if (ocorrenciasArquivo != null) {
            Iterator<String> it = ocorrenciasArquivo.iterator();
            while (it.hasNext()) {
                ocorrencia.append(it.next());
            }
        }

        // Adiciona as ocorrencias de lote
        if (ocorrenciasLote != null) {
            Iterator<String> it = ocorrenciasLote.iterator();
            while (it.hasNext()) {
                ocorrencia.append(it.next());
            }
        }

        return ocorrencia.toString();
    }

    /**
     * Inclui uma nova ocorrencia para o lote especificado.
     * As ocorrencias são inseridas em um conjunto, assim, no final teremos
     * apenas os códigos distindos. Um LinkedHashSet mantém a ordem de
     * inserção dos elementos.
     * @param codigoLote String
     * @param msg String
     */
    private void incluirNovaOcorrencia(String codigoLote, String msg) {
        // Obtém as ocorrencias já cadastradas para o lote informado
        Set<String> ocorrencias = cacheOcorrencias.get(codigoLote);

        if (ocorrencias == null) {
            ocorrencias = new LinkedHashSet<>();
            cacheOcorrencias.put(codigoLote, ocorrencias);

            // Adiciona a ocorrencia de Arquivo/Lote invalido
            // Será a primeira ocorrencia a ser exibida, pois foi a
            // primeira a ser inserida
            if (codigoLote.equals(CODIGO_LOTE_SERVICO_HEADER_ARQUIVO)) {
                ocorrencias.add(ArquivoInvalidoException.OCORRENCIA_ARQUIVO_INVALIDO);
            } else {
                ocorrencias.add(LoteInvalidoException.OCORRENCIA_LOTE_INVALIDO);
            }
        }

        // Adiciona a ocorrencia encontrada
        ocorrencias.add(msg);
    }

    /**
     * Retorna a mensagem de ocorrencia para um campo, definido nas
     * constantes de validação
     * @param nomeCampo String
     * @param campos Object[][]
     * @return String
     */
    private String obterOcorrenciaCampo(String nomeCampo, Object[][] campos) {
        for (Object[] campo : campos) {
            if (campo[0].equals(nomeCampo)) {
                return campo[3].toString();
            }
        }
        return OCORRENCIA_SUCESSO;
    }

    /******************************* MÉTODOS PROCESSAMENTO DA LINHA *********************************/

    /**
     * Extrai os campos da linha de dados
     * @param linha String
     * @return Map
     * @throws ArquivoInvalidoException
     */
    private Map<String, String> processarLinha(String linha) throws ArquivoInvalidoException {
        switch (linha.charAt(POSICAO_TIPO_REGISTRO)) {
            case TIPO_REGISTRO_HEADER_ARQUIVO:
                return extrairCampos(linha, CAMPOS_HEADER_ARQUIVO);
            case TIPO_REGISTRO_HEADER_LOTE:
                return extrairCampos(linha, CAMPOS_HEADER_LOTE);
            case TIPO_REGISTRO_DETALHE:
                return extrairCampos(linha, CAMPOS_DETALHE);
            case TIPO_REGISTRO_TRAILER_LOTE:
                return extrairCampos(linha, CAMPOS_TRAILER_LOTE);
            case TIPO_REGISTRO_TRAILER_ARQUIVO:
                return extrairCampos(linha, CAMPOS_TRAILER_ARQUIVO);
            default:
                // Gera ocorrencia de erro a nivel de arquivo
                throw new ArquivoInvalidoException(OCORRENCIA_TIPO_REGISTRO_INVALIDO);
        }
    }

    /**
     * De acordo com a estrutura dos dados, quebra a linha de entrada nos
     * campos especificados.
     * @param linha String
     * @param campos Object[][]
     * @return Map
     */
    private Map<String, String> extrairCampos(String linha, Object[][] campos) {
        Map<String, String> dados = new HashMap<>();
        for (Object[] campo : campos) {
            dados.put(campo[0].toString(), linha.substring(((int[]) campo[1])[0], ((int[]) campo[1])[1]));
        }
        return dados;
    }

    /******************************* MÉTODOS DE VALIDAÇÃO DA LINHA *********************************/

    /**
     * Executa a validação dos dados da linha processada.
     * Retorna uma string com os códigos das ocorrencias de erro.
     * @param linha String
     * @param dados Map
     * @return String
     */
    private String validarLinha(String linha, Map<String, String> dados) {
        try {
            char tipoRegistroAtual = linha.charAt(POSICAO_TIPO_REGISTRO);
            validarTipoRegistroAtual(tipoRegistroAtual);

            switch (tipoRegistroAtual) {
                case TIPO_REGISTRO_HEADER_ARQUIVO:
                    validarDadosHeaderArquivo(dados);
                    break;
                case TIPO_REGISTRO_HEADER_LOTE:
                    validarDadosHeaderLote(dados);
                    break;
                case TIPO_REGISTRO_DETALHE:
                    validarDadosDetalhe(dados);
                    break;
                case TIPO_REGISTRO_TRAILER_LOTE:
                    validarDadosTrailerLote(dados);
                    break;
                case TIPO_REGISTRO_TRAILER_ARQUIVO:
                    validarDadosTrailerArquivo(dados);
                    break;
                default:

                    // Gera ocorrencia de erro a nivel de arquivo
                    throw new ArquivoInvalidoException(OCORRENCIA_TIPO_REGISTRO_INVALIDO);
            }
        } catch (ArquivoInvalidoException ex) {
            incluirNovaOcorrencia(CODIGO_LOTE_SERVICO_HEADER_ARQUIVO, ex.getMessage());
            return ex.getMessage();
        } catch (LoteInvalidoException ex) {
            incluirNovaOcorrencia(dados.get("LOTE_SERVICO").toString(), ex.getMessage());
            return ex.getMessage();
        } catch (FebrabanException ex) {
            return ex.getMessage();
        }
        return OCORRENCIA_SUCESSO;
    }

    /**
     * Executa uma validação genérica nos campos de dados. Verifica se os tipos
     * estão nos formatos corretos e se os campos obrigatórios estão presentes.
     * @param dados Map
     * @param campos Object[][]
     * @throws FebrabanException
     */
    private void validarDados(Map<String, String> dados, Object[][] campos) throws FebrabanException {
        for (Object[] campo : campos) {

            if (campo[2].equals(NUMERICO_OBRIGATORIO)) {
                if (TextHelper.isNull(dados.get(campo[0]))) {
                    // Gera ocorrencia a nivel de arquivo
                    throw new FebrabanException(campo[3].toString()); //"CAMPO OBRIGATORIO NULO"
                } else if (!TextHelper.isNum(dados.get(campo[0]))) {
                    // Gera ocorrencia a nivel de arquivo
                    throw new FebrabanException(campo[3].toString()); //"FORMATO NUMERICO INCORRETO"
                }

            } else if (campo[2].equals(NUMERICO)) {
                if (!TextHelper.isNull(dados.get(campo[0])) &&
                        !TextHelper.isNum(dados.get(campo[0]))) {
                    // Gera ocorrencia a nivel de arquivo
                    throw new FebrabanException(campo[3].toString()); //"FORMATO NUMERICO INCORRETO"
                }

            } else if (campo[2].equals(OBRIGATORIO)) {
                if (TextHelper.isNull(dados.get(campo[0]))) {
                    // Gera ocorrencia a nivel de arquivo
                    throw new FebrabanException(campo[3].toString()); //"CAMPO OBRIGATORIO NULO"
                }

            } else if (campo[2].equals(DATA)) {
                if (!TextHelper.isNull(dados.get(campo[0]))) {
                    try {
                        DateHelper.parse(dados.get(campo[0]).toString(), "ddMMyyyy");
                    } catch (ParseException ex) {
                        // Gera ocorrencia a nivel de arquivo
                        throw new FebrabanException(campo[3].toString()); //"FORMATO DATA INCORRETO"
                    }
                }
            }
        }
    }

    /**
     * Valida o contador de lotes em cada linha informada
     * @param dados Map
     * @param descricaoCampo String
     * @throws ArquivoInvalidoException
     */
    private void validarNumeroSequencialLote(Map<String, String> dados, String descricaoCampo) throws ArquivoInvalidoException {
        // Verifica se o LOTE_SERVICO está com o valor correto
        if (!TextHelper.isNull(dados.get(descricaoCampo)) &&
                Integer.parseInt(dados.get(descricaoCampo).toString().trim()) != contadorLote) {
            throw new ArquivoInvalidoException(OCORRENCIA_LOTE_SERVICO_FORA_SEQUENCIA);
        }
    }

    /**
     * Valida o Tipo de registro atual de acordo com o anterior
     * @param tipoRegistroAtual char
     * @throws ArquivoInvalidoException
     */
    private void validarTipoRegistroAtual(char tipoRegistroAtual) throws ArquivoInvalidoException {
        switch (tipoRegistroAtual) {
            case TIPO_REGISTRO_HEADER_ARQUIVO:
                if (tipoRegistroAnterior != TIPO_REGISTRO_INICIAL) {
                    throw new ArquivoInvalidoException(OCORRENCIA_TIPO_REGISTRO_INVALIDO);
                }
                break;
            case TIPO_REGISTRO_HEADER_LOTE:
                if (tipoRegistroAnterior != TIPO_REGISTRO_HEADER_ARQUIVO && tipoRegistroAnterior != TIPO_REGISTRO_TRAILER_LOTE) {
                    throw new ArquivoInvalidoException(OCORRENCIA_TIPO_REGISTRO_INVALIDO);
                }
                break;
            case TIPO_REGISTRO_DETALHE:
                if (tipoRegistroAnterior != TIPO_REGISTRO_HEADER_LOTE && tipoRegistroAnterior != TIPO_REGISTRO_DETALHE) {
                    throw new ArquivoInvalidoException(OCORRENCIA_TIPO_REGISTRO_INVALIDO);
                }
                break;
            case TIPO_REGISTRO_TRAILER_LOTE:
                if (tipoRegistroAnterior != TIPO_REGISTRO_HEADER_LOTE && tipoRegistroAnterior != TIPO_REGISTRO_DETALHE) {
                    throw new ArquivoInvalidoException(OCORRENCIA_TIPO_REGISTRO_INVALIDO);
                }
                break;
            case TIPO_REGISTRO_TRAILER_ARQUIVO:
                if (tipoRegistroAnterior != TIPO_REGISTRO_HEADER_ARQUIVO && tipoRegistroAnterior != TIPO_REGISTRO_TRAILER_LOTE) {
                    throw new ArquivoInvalidoException(OCORRENCIA_TIPO_REGISTRO_INVALIDO);
                }
                break;
            default:

                // Gera ocorrencia de erro a nivel de arquivo
                throw new ArquivoInvalidoException(OCORRENCIA_TIPO_REGISTRO_INVALIDO);
        }
    }

    /**
     * Valida os campos do header de arquivo
     * @param dados Map
     * @throws ArquivoInvalidoException
     * @throws LoteInvalidoException
     */
    private void validarDadosHeaderArquivo(Map<String, String> dados) throws ArquivoInvalidoException {
        // Valida os formatos dos campos e os campos obrigatórios
        try {
            validarDados(dados, CAMPOS_HEADER_ARQUIVO);
        } catch (FebrabanException ex) {
            throw new ArquivoInvalidoException(ex.getMessage());
        }

        // Valida o contador sequencial de lotes
        validarNumeroSequencialLote(dados, "LOTE_SERVICO");

        // Verifica se o TIPO_OPERACAO está com o valor correto no campo de detalhe
        if (!TextHelper.isNull(dados.get("REMESSA/RETORNO")) && !dados.get("REMESSA/RETORNO").equals(TIPO_CODIGO_REMESSA)) {
            throw new ArquivoInvalidoException(obterOcorrenciaCampo("REMESSA/RETORNO", CAMPOS_HEADER_ARQUIVO));
        }
    }

    /**
     * Valida os campos do trailer de arquivo
     * @param dados Map
     * @throws ArquivoInvalidoException
     */
    private void validarDadosTrailerArquivo(Map<String, String> dados) throws ArquivoInvalidoException {
        // Valida os formatos dos campos e os campos obrigatórios
        try {
            validarDados(dados, CAMPOS_TRAILER_ARQUIVO);
        } catch (FebrabanException ex) {
            throw new ArquivoInvalidoException(ex.getMessage());
        }

        // Verifica se o LOTE_SERVICO está com o valor correto
        if (!TextHelper.isNull(dados.get("LOTE_SERVICO")) &&
                !dados.get("LOTE_SERVICO").equals(CODIGO_LOTE_SERVICO_TRAILER_ARQUIVO)) {
            throw new ArquivoInvalidoException(obterOcorrenciaCampo("LOTE_SERVICO", CAMPOS_TRAILER_ARQUIVO));
        }

        // Verifica se o QTD_LOTES está com o valor correto no trailer de arquivo
        if (!TextHelper.isNull(dados.get("QTD_LOTES")) &&
                Integer.parseInt(dados.get("QTD_LOTES").toString().trim()) != contadorLote) {
            throw new ArquivoInvalidoException(obterOcorrenciaCampo("QTD_LOTES", CAMPOS_TRAILER_ARQUIVO));
        }

        // Verifica se o QTD_REGISTROS está com o valor correto no trailer de arquivo
        // this.contadorRegistroArquivo guarda apenas os registros de detalhe, por isso temos que somar a
        // quantidade de header e trailers de arquivo, mais os header e trailer de lote
        if (!TextHelper.isNull(dados.get("QTD_REGISTROS")) &&
                Integer.parseInt(dados.get("QTD_REGISTROS").toString().trim()) != (contadorRegistroArquivo + (contadorLote * 2) + 2)) {
            throw new ArquivoInvalidoException(obterOcorrenciaCampo("QTD_REGISTROS", CAMPOS_TRAILER_ARQUIVO));
        }
    }

    /**
     * Valida os campos dos headers de lote
     * @param dados Map
     * @throws ArquivoInvalidoException
     * @throws LoteInvalidoException
     */
    private void validarDadosHeaderLote(Map<String, String> dados) throws ArquivoInvalidoException, LoteInvalidoException {
        // Inicializa o contador de registros do lote
        contadorRegistroLote = 0;
        somatorioParcelas = 0;
        contadorParcelas = 0;

        // Incrementa o contador de lotes
        contadorLote++;

        // Valida os formatos dos campos e os campos obrigatórios
        try {
            validarDados(dados, CAMPOS_HEADER_LOTE);
        } catch (FebrabanException ex) {
            throw new LoteInvalidoException(ex.getMessage());
        }

        // Verifica se o TIPO_SERVICO está com o valor correto no trailer de lote
        if (!TextHelper.isNull(dados.get("TIPO_SERVICO"))) {
            int tipoServico = Integer.parseInt(dados.get("TIPO_SERVICO").toString().trim());
            if (tipoServico < 8 || tipoServico > 12) {
                /*
                 '08' = Consulta/Informação Margem
                 '09' = Averbação da Consignação/Retenção
                 '10' = Pagamento Dividendos
                 '11' = Manutenção da Consignação
                 '12' = Consignação de Parcelas
                 */
                throw new LoteInvalidoException(obterOcorrenciaCampo("TIPO_SERVICO", CAMPOS_HEADER_LOTE));
            }
        }

        // Verifica se o LEIAUTE_SERVICO está com o valor correto no header de lote
        /*
               if (!TextHelper.isNull(dados.get("LEIAUTE_SERVICO")) && !dados.get("LEIAUTE_SERVICO").equals(VERSAO_LAYOUT_LOTE)) {
            throw new LoteInvalidoException(obterOcorrenciaCampo("LEIAUTE_SERVICO", CAMPOS_HEADER_LOTE));
               }
         */

        // Valida o contador sequencial de lotes
        validarNumeroSequencialLote(dados, "LOTE_SERVICO");
    }

    /**
     * Valida os campos dos trailers de lote
     * @param dados Map
     * @throws ArquivoInvalidoException
     * @throws LoteInvalidoException
     */
    private void validarDadosTrailerLote(Map<String, String> dados) throws ArquivoInvalidoException, LoteInvalidoException {
        // Valida os formatos dos campos e os campos obrigatórios
        try {
            validarDados(dados, CAMPOS_TRAILER_LOTE);
        } catch (FebrabanException ex) {
            throw new LoteInvalidoException(ex.getMessage());
        }

        // Valida o contador sequencial de lotes
        validarNumeroSequencialLote(dados, "LOTE_SERVICO");

        // Verifica se o QTD_REGISTROS está com o valor correto no trailer de lote
        // this.contadorRegistroLote guarda apenas os registro de detalhe, por isso fazemos +2
        if (!TextHelper.isNull(dados.get("QTD_REGISTROS")) &&
                Integer.parseInt(dados.get("QTD_REGISTROS").toString().trim()) != (contadorRegistroLote + 2)) {
            throw new LoteInvalidoException(obterOcorrenciaCampo("QTD_REGISTROS", CAMPOS_TRAILER_LOTE));
        }

        // Verifica se o QTD_PARCELAS está com o valor correto no trailer de lote
        if (!TextHelper.isNull(dados.get("QTD_PARCELAS")) &&
                Integer.parseInt(dados.get("QTD_PARCELAS").toString().trim()) != contadorParcelas) {
            throw new LoteInvalidoException(obterOcorrenciaCampo("QTD_PARCELAS", CAMPOS_TRAILER_LOTE));
        }

        // Verifica se o SOMA_PARCELAS está com o valor correto no trailer de lote
        if (!TextHelper.isNull(dados.get("SOMA_PARCELAS")) &&
                Long.parseLong(dados.get("SOMA_PARCELAS").toString().trim()) != somatorioParcelas) {
            throw new LoteInvalidoException(obterOcorrenciaCampo("SOMA_PARCELAS", CAMPOS_TRAILER_LOTE));
        }
    }

    /**
     * Valida os campos dos registros de detalhe
     * @param dados Map
     * @throws ArquivoInvalidoException
     * @throws RegistroInvalidoException
     */
    private void validarDadosDetalhe(Map<String, String> dados) throws RegistroInvalidoException, ArquivoInvalidoException {
        // Incrementa o contador de registros do lote
        contadorRegistroLote++;

        // Incrementa o contador de registros do arquivo
        contadorRegistroArquivo++;

        // Atualiza o contador de parcelas
        boolean campoQtdParcelasPreenchido = false;
        if (TextHelper.isNum(dados.get("QTD_PARCELAS"))) {
            try {
                int qtdParcelas = Integer.parseInt(dados.get("QTD_PARCELAS").toString().trim());
                if (qtdParcelas > 0) {
                    contadorParcelas += qtdParcelas;
                    campoQtdParcelasPreenchido = true;
                }
            } catch (NumberFormatException ex) {
                // deixa para o exception ser lançada na validaçao do campo abaixo
            }
        }
        if (!campoQtdParcelasPreenchido && TextHelper.isNum(dados.get("QTD_PARCELAS_EXT"))) {
            try {
                contadorParcelas += Integer.parseInt(dados.get("QTD_PARCELAS_EXT").toString().trim());
            } catch (NumberFormatException ex) {
                // deixa para o exception ser lançada na validaçao do campo abaixo
            }
        }

        // Atualiza o somatório de parcelas
        if (!TextHelper.isNull(dados.get("VLR_PARCELA"))) {
            try {
                somatorioParcelas += Long.parseLong(dados.get("VLR_PARCELA").toString().trim());
            } catch (NumberFormatException ex) {
                // deixa para o exception ser lançada na validaçao do campo abaixo
            }
        }

        // Valida os formatos dos campos e os campos obrigatórios
        try {
            validarDados(dados, CAMPOS_DETALHE);
        } catch (FebrabanException ex) {
            throw new RegistroInvalidoException(ex.getMessage());
        }

        // Valida o contador sequencial de lotes
        validarNumeroSequencialLote(dados, "LOTE_SERVICO");

        // Verifica se o NUM_SEQ_REGISTRO está com o valor correto no campo de detalhe
        if (!TextHelper.isNull(dados.get("NUM_SEQ_REGISTRO")) &&
                Integer.parseInt(dados.get("NUM_SEQ_REGISTRO").toString().trim()) != contadorRegistroLote) {
            throw new RegistroInvalidoException(obterOcorrenciaCampo("NUM_SEQ_REGISTRO", CAMPOS_DETALHE));
        }

        // Verifica se o SEGMENTO está com o valor correto no campo de detalhe
        if (!TextHelper.isNull(dados.get("SEGMENTO")) && !dados.get("SEGMENTO").equals(CODIGO_SEGMENTO)) {
            throw new RegistroInvalidoException(obterOcorrenciaCampo("SEGMENTO", CAMPOS_DETALHE));
        }

        // Verifica se o TIPO_MOVIMENTO está com o valor correto no campo de detalhe
        if (!TextHelper.isNull(dados.get("TIPO_MOVIMENTO"))) {
            int tipoMovimento = Integer.parseInt(dados.get("TIPO_MOVIMENTO").toString().trim());
            if (tipoMovimento != 0 && tipoMovimento != 1 && tipoMovimento != 3 &&
                    tipoMovimento != 5 && tipoMovimento != 7 && tipoMovimento != 9) {
                /* '0' INCLUSÃO    |   '1' CONSULTA    |  '3' ESTORNO
                   '5' ALTERAÇÃO   |   '7' LIQUIDAÇÃO  |  '9' EXCLUSÃO */
                throw new RegistroInvalidoException(obterOcorrenciaCampo("TIPO_MOVIMENTO", CAMPOS_DETALHE));
            }
        }

        // Verifica se o STATUS_MUTUARIO está com o valor correto no campo de detalhe
        if (!TextHelper.isNull(dados.get("STATUS_MUTUARIO")) && !dados.get("STATUS_MUTUARIO").toString().equals("0")) {
            int statusMutuario = Integer.parseInt(dados.get("STATUS_MUTUARIO").toString().trim());
            if (statusMutuario != 1 && statusMutuario != 2 && statusMutuario != 3) {
                /* '1' ATIVO | '2' INATVO | '3' PENSIONISTA */
                throw new RegistroInvalidoException(obterOcorrenciaCampo("STATUS_MUTUARIO", CAMPOS_DETALHE));
            }
        }

        // Verifica se o TIPO_OPERACAO está com o valor correto no campo de detalhe
        if (!TextHelper.isNull(dados.get("TIPO_OPERACAO")) && !dados.get("TIPO_OPERACAO").equals(TIPO_OPERACAO_EMPRESTIMO)) {
            throw new RegistroInvalidoException(obterOcorrenciaCampo("TIPO_OPERACAO", CAMPOS_DETALHE));
        }
    }

    /******************************* ROTINAS AUXILIARES ******************************************/

    /**
     * Completa as linhas de um arquivo CNAB240 para possuirem 240 posições.
     * Completa as linhas com espaços em branco.
     * @param nomeArquivo String
     * @throws ParserException
     */
    public void completarTamanhoLinhaCnab(String nomeArquivo) throws ParserException {
        String nomeArquivoSaida = nomeArquivo.substring(0, nomeArquivo.length() - 4) + "-novo.txt";

        BufferedReader in = null;
        PrintWriter out = null;

        String linha = null;

        try {
            in = new BufferedReader(new FileReader(nomeArquivo));
            out = new PrintWriter(new BufferedWriter(new FileWriter(nomeArquivoSaida)));

            while ((linha = in.readLine()) != null) {
                while (linha.length() < TAMANHO_LINHA_CNAB) {
                    linha += " ";
                }
                out.println(linha);
            }
        } catch (FileNotFoundException ex) {
            LOG.error("O arquivo '" + nomeArquivo + "' não foi encontrado.", ex);
        } catch (IOException ex) {
            LOG.error("Erro ao processar o arquivo '" + nomeArquivo + "'.", ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }
}
