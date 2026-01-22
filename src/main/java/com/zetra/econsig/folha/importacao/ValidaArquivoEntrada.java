package com.zetra.econsig.folha.importacao;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ValidaArquivoEntrada</p>
 * <p>Description: Classe utilitária para validação de arquivos de entrada
 * baseados nos leiautes XML definidos por parâmetros de sistema ou
 * informados nos parâmetros de  execução.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidaArquivoEntrada {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidaArquivoEntrada.class);

    private static final String TIPO_MARGEM       = "MARGEM";
    private static final String TIPO_TRANSFERIDOS = "TRANSFERIDOS";
    private static final String TIPO_RETORNO      = "RETORNO";
    private static final String TIPO_CRITICA      = "CRITICA";

    private static final String CHAVE_ESTATISTICA_MINIMO = "MINIMO";
    private static final String CHAVE_ESTATISTICA_MAXIMO = "MAXIMO";
    private static final String CHAVE_ESTATISTICA_SOMATORIO = "SOMATORIO";

    private static ArrayList<String> listaTiposArquivo;

    static {
        listaTiposArquivo = new ArrayList<>();
        listaTiposArquivo.add(TIPO_MARGEM);
        listaTiposArquivo.add(TIPO_TRANSFERIDOS);
        listaTiposArquivo.add(TIPO_RETORNO);
        listaTiposArquivo.add(TIPO_CRITICA);
    }

    public static void validarEntradaGenerica(String xmlEntrada, String xmlTradutor, String nomeArquivo, boolean imprimeValores) throws ZetraException {
        Tradutor tradutor = criaTradutor(nomeArquivo, xmlEntrada, xmlTradutor);
        validarEntrada(tradutor, imprimeValores);
    }

    public static void validarEntradaPadrao(String tipoArquivo, String nomeArquivo, String tipoEntidade, String codigoEntidade, boolean imprimeValores) throws ZetraException {
        Tradutor tradutor = criaTradutor(nomeArquivo, tipoArquivo, tipoEntidade, codigoEntidade);
        validarEntrada(tradutor, imprimeValores);
    }

    /**
     * Realiza a verificação de correspondência entre o arquivo de dados e o leiaute esperado.
     * @param tradutor Contém os dados necessários para tradução do arquivo de dados.
     * @param imprimeValores Indica se o conteúdo de cada campo deve ser impresso.
     * @throws ZetraException
     */
    private static void validarEntrada(Tradutor tradutor, boolean imprimeValores) throws ZetraException {
        LOG.info("Inicializando validação do arquivo de importação. Por favor, aguarde.");
        try {
            tradutor.iniciaTraducao(true);
            int qtdeLinhas = 0;

            // Armazena estatísticas para cada campo numérico
            Map<String, Map<String, BigDecimal>> estatisticasCamposNumericos = new HashMap<>();

            StringBuilder outputBuffer;
            while (tradutor.traduzProximo()) {
                qtdeLinhas++;

                if (imprimeValores) {
                    Map<String, Object> valoresMap = tradutor.getDados();

                    if (valoresMap != null) {
                        outputBuffer = new StringBuilder("Linha " + qtdeLinhas + ":\n");
                        for (Entry<String, Object> entry : valoresMap.entrySet()) {
                            outputBuffer.append("  Campo " + entry.getKey() + ", valor: " + entry.getValue() + "\n");
                        }
                        LOG.info(outputBuffer.toString());
                    }
                }

                // Inclui os valores da linha lida nas estatísticas dos campos numéricos.
                atualizaEstatisticasCamposNumericos(estatisticasCamposNumericos, tradutor.getValoresNumericos());
            }

            // Imprime as estatísticas dos campos numéricos.
            for (Entry<String, Map<String, BigDecimal>> entry : estatisticasCamposNumericos.entrySet()) {
                outputBuffer = new StringBuilder("\nCampo: " + entry.getKey() + "\n");
                outputBuffer.append("Minimo: " + entry.getValue().get(CHAVE_ESTATISTICA_MINIMO).setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() + "\n");
                outputBuffer.append("Maximo: " + entry.getValue().get(CHAVE_ESTATISTICA_MAXIMO).setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() + "\n");
                outputBuffer.append("Media: " + entry.getValue().get(CHAVE_ESTATISTICA_SOMATORIO).divide(new BigDecimal(qtdeLinhas), 2, java.math.RoundingMode.HALF_UP).toPlainString() + "\n");
                outputBuffer.append("Somatorio: " + entry.getValue().get(CHAVE_ESTATISTICA_SOMATORIO).setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() + "\n");
                LOG.info(outputBuffer.toString());
            }
        } catch (Exception ex) {
            throw new ZetraException(ex);
        } finally {
            try {
                tradutor.encerraTraducao();
            } catch (ParserException pe) {
                throw new ZetraException(pe);
            }
        }

        LOG.info("Arquivo formatado conforme definições.");
    }

    /**
     * Dado novo conjunto de valores, atualiza as estatísticas do processamento corrente.
     * @param estatisticasCamposNumericos Conjunto de estatísticas já computadas
     * @param valoresNumericos Novo grupo de valores numéricos
     */
    private static void atualizaEstatisticasCamposNumericos(Map<String, Map<String, BigDecimal>> estatisticasCamposNumericos, Map<String, BigDecimal> valoresNumericos) {
        if (valoresNumericos != null && valoresNumericos.size() > 0) {
            // Para cada campo do conjunto de campos com valores numéricos
            for (Entry<String, BigDecimal> entry : valoresNumericos.entrySet()) {
                // Se foi fornecido um valor para o campo.
                if (entry.getValue() != null) {
                    // Recupera as estatísticas para o campo atual
                    BigDecimal valorMinimo, valorMaximo, somatorio;
                    Map<String, BigDecimal> estatisticasCampo = estatisticasCamposNumericos.get(entry.getKey());
                    if (estatisticasCampo == null || estatisticasCampo.size() == 0) {
                        estatisticasCampo = new HashMap<>();
                        valorMinimo = null;
                        valorMaximo = null;
                        somatorio = null;
                    } else {
                        valorMinimo = estatisticasCampo.get(CHAVE_ESTATISTICA_MINIMO);
                        valorMaximo = estatisticasCampo.get(CHAVE_ESTATISTICA_MAXIMO);
                        somatorio = estatisticasCampo.get(CHAVE_ESTATISTICA_SOMATORIO);
                    }

                    if (valorMinimo == null || valorMinimo.compareTo(entry.getValue()) > 0) {
                        valorMinimo = entry.getValue();
                    }

                    if (valorMaximo == null || valorMaximo.compareTo(entry.getValue()) < 0) {
                        valorMaximo = entry.getValue();
                    }

                    if (somatorio == null) {
                        somatorio = entry.getValue();
                    } else {
                        somatorio = somatorio.add(entry.getValue());
                    }

                    estatisticasCampo.put(CHAVE_ESTATISTICA_MINIMO, valorMinimo);
                    estatisticasCampo.put(CHAVE_ESTATISTICA_MAXIMO, valorMaximo);
                    estatisticasCampo.put(CHAVE_ESTATISTICA_SOMATORIO, somatorio);

                    // Atualiza as estatísticas para o campo.
                    estatisticasCamposNumericos.put(entry.getKey(), estatisticasCampo);
                }
            }
        }
    }

    /**
     * Cria o tradutor do arquivo de tipo genérico.
     * @param nomeArquivo : Nome do arquivo de entrada
     * @param xmlEntrada  : Nome do XML de configuração do leitor
     * @param xmlTradutor : Nome do XML de configuração do tradutor
     * @return
     * @throws ZetraException
     */
    private static Tradutor criaTradutor(String nomeArquivo, String xmlEntrada, String xmlTradutor) throws ZetraException {
        LeitorArquivoTexto leitor;

        if (!new File(xmlEntrada).exists() || !new File(xmlTradutor).exists()) {
            throw new ZetraException("mensagem.erro.arquivos.xml.configuracao.importacao.ausentes", AcessoSistema.getAcessoUsuarioSistema());
        }

        if (!new File(nomeArquivo).exists()) {
            throw new ZetraException("mensagem.erro.arquivo.entrada.importacao.nao.encontrado", AcessoSistema.getAcessoUsuarioSistema());
        }

        // Configura o leitor de acordo com o arquivo de entrada
        if (nomeArquivo.toLowerCase().endsWith(".zip")) {
            leitor = new LeitorArquivoTextoZip(xmlEntrada, nomeArquivo);
        } else {
            leitor = new LeitorArquivoTexto(xmlEntrada, nomeArquivo);
        }

        // Escritor e tradutor
        Escritor escritor = new EscritorMemoria(new HashMap<>());
        Tradutor tradutor = new Tradutor(xmlTradutor, leitor, escritor);
        return tradutor;
    }

    /**
     * Cria o tradutor do arquivo de tipo definido pelo parâmetro.
     * @param nomeArquivo     : Nome do arquivo de entrada
     * @param tipoArquivo     : Tipo pré-definido do arquivo, não podendo ser do tipo GENERICO.
     * @param tipoEntidade    : CSE/ORG
     * @param codigoEntidade  : Código da entidade, 1 para CSE
     * @return
     * @throws ZetraException
     */
    private static Tradutor criaTradutor(String nomeArquivo, String tipoArquivo, String tipoEntidade, String codigoEntidade) throws ZetraException {
        ParamSist paramSist = ParamSist.getInstance();
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        String nomeEntradaXml = null;
        String nomeTradutorXml = null;
        String path = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "conf" + File.separatorChar;

        if (tipoArquivo.equals(TIPO_MARGEM)) {
            nomeEntradaXml = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_MARGEM, responsavel);
            nomeTradutorXml = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_MARGEM, responsavel);
        } else if (tipoArquivo.equals(TIPO_TRANSFERIDOS)) {
            nomeEntradaXml = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_TRANSF, responsavel);
            nomeTradutorXml = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_TRANSF, responsavel);
        } else if (tipoArquivo.equals(TIPO_RETORNO)) {
            nomeEntradaXml = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_RETORNO, responsavel);
            nomeTradutorXml = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_RETORNO, responsavel);
        } else if (tipoArquivo.equals(TIPO_CRITICA)) {
            nomeEntradaXml = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_CRITICA, responsavel);
            nomeTradutorXml = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_CRITICA, responsavel);
        } else {
            throw new ZetraException("mensagem.erro.tipo.arquivo.invalido", AcessoSistema.getAcessoUsuarioSistema());
        }

        // Verifica arquivo de configuração no diretório de órgão
        if (tipoEntidade.equalsIgnoreCase("ORG") && !TextHelper.isNull(codigoEntidade)) {
            if (new File(path + "cse" + File.separatorChar + codigoEntidade + File.separatorChar + nomeEntradaXml).exists()) {
                nomeEntradaXml =  "cse" + File.separatorChar + codigoEntidade + File.separatorChar + nomeEntradaXml;
            }
            if (new File(path + "cse" + File.separatorChar + codigoEntidade + File.separatorChar + nomeTradutorXml).exists()) {
                nomeTradutorXml = "cse" + File.separatorChar + codigoEntidade + File.separatorChar + nomeTradutorXml;
            }
        } else if (tipoEntidade.equalsIgnoreCase("EST") && !TextHelper.isNull(codigoEntidade)) {
            if (new File(path + "est" + File.separatorChar + codigoEntidade + File.separatorChar + nomeEntradaXml).exists()) {
                nomeEntradaXml =  "est" + File.separatorChar + codigoEntidade + File.separatorChar + nomeEntradaXml;
            }
            if (new File(path + "est" + File.separatorChar + codigoEntidade + File.separatorChar + nomeTradutorXml).exists()) {
                nomeTradutorXml = "est" + File.separatorChar + codigoEntidade + File.separatorChar + nomeTradutorXml;
            }
        }

        return criaTradutor(nomeArquivo, path + nomeEntradaXml, path + nomeTradutorXml);
    }
}
