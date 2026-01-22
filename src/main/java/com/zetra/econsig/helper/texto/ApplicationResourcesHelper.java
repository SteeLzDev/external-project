package com.zetra.econsig.helper.texto;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ApplicationResourcesHelper</p>
 * <p>Description: Singleton repositório das mensagens do ApplicationResources</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class ApplicationResourcesHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ApplicationResourcesHelper.class);

    private Map<String, String> recursos;

    private final Map<String, String> propriedades;

    // Determina se o Helper está sendo usado como biblioteca,
    // cenário em que ele não irá carregar os recursos do banco
    private boolean biblioteca = false;

    // Determina se ocorreu erro na carga das mensagens, evitando
    // loop e erro de StackOverflow ao executar a rotina fora do contexto
    // do servidor de aplicação
    private boolean erroCargaTabela = false;

    private static Markdown4jProcessorExtended markdownProcessor;

    private static final String PADRAO_INICIO = "${";

    private static final String PADRAO_TERMINO = "}";

    private static final String PADRAO_OPERACAO_LOWER = "lower(";

    private static final String PADRAO_OPERACAO_UPPER = "upper(";

    private static final String PADRAO_TERMINO_OPERACAO = ")";

    private static final String TEXTO_SISTEMA_VAZIO = "</>";

    private static final String PADRAO_OPERACAO_MARKDOWN = "markdown(";

    private static class SingletonHelper {
        private static final ApplicationResourcesHelper instance = new ApplicationResourcesHelper();
    }

    public static ApplicationResourcesHelper getInstance() {
        return SingletonHelper.instance;
    }

    static {
        markdownProcessor = new Markdown4jProcessorExtended(false);
    }

    private ApplicationResourcesHelper() {
        if (ExternalCacheHelper.hasExternal()) {
            final String prefix = getClass().getSimpleName();
            recursos = new ExternalMap<>(prefix + "-recursos");
        } else {
            recursos = new HashMap<>();
        }

        propriedades = carregarArquivoPropriedades();
        replaceSubKeys(propriedades, null, null);

    }

    private Map<String, String> carregarArquivoPropriedades() {
        final Map<String, String> propriedadesArq = new HashMap<>();

        try {
            final Properties file = new Properties();
            if (ApplicationResourcesHelper.class.getClassLoader().getResourceAsStream("ApplicationResources-client.properties") != null) {
                biblioteca = true;
                file.load(ApplicationResourcesHelper.class.getClassLoader().getResourceAsStream("ApplicationResources-client.properties"));
            } else {
                file.load(ApplicationResourcesHelper.class.getClassLoader().getResourceAsStream("ApplicationResources.properties"));
            }

            for (final Entry<Object, Object> entrada : file.entrySet()) {
                propriedadesArq.put(entrada.getKey().toString().trim(), entrada.getValue().toString().trim());
            }
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return propriedadesArq;
    }

    private void carregarRecursosBanco() {
        try {
            if (!biblioteca && !erroCargaTabela && recursos.isEmpty()) {
                synchronized (this) {
                    if (recursos.isEmpty()) {
                        final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);

                        // Carrega os textos do application resources no banco de dados
                        sistemaController.carregarTextoSistema(carregarArquivoPropriedades(), AcessoSistema.getAcessoUsuarioSistema());

                        final Map<String, String> recursosForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : recursos;

                        // Recarrega os textos à partir do banco
                        final List<TransferObject> lstTextos = sistemaController.lstTextoSistema(AcessoSistema.getAcessoUsuarioSistema());

                        for (final TransferObject texto : lstTextos) {
                            final String chave = texto.getAttribute(Columns.TEX_CHAVE).toString().trim();
                            final String valor = texto.getAttribute(Columns.TEX_TEXTO).toString().trim();
                            if (!chave.equals("release.date") && !chave.equals("release.tag")) {
                                recursosForLoad.put(chave, valor);
                            }
                        }

                        replaceSubKeys(recursosForLoad, propriedades, null);

                        for (final Map.Entry<String, String> recursosEntry : recursosForLoad.entrySet()) {
                            final String chave = recursosEntry.getKey();
                            final String valor = recursosEntry.getValue();
                            if (chave.matches(".*[^\\p{ASCII}].*")) {
                                LOG.error("Chave inválida: " + chave + " - " + valor);
                            }
                            // Se for mensagens do mobile, não processa
                            if (!chave.startsWith("mobile.")) {
                                final String valorSemParametros = valor.replaceAll("\\{[0-9]\\}", "");
                                if ((valorSemParametros.indexOf('{') != -1) || (valorSemParametros.indexOf('}') != -1)) {
                                    LOG.error("Chave inválida: " + chave + " - " + valor);
                                }
                            }
                        }
                        if (ExternalCacheHelper.hasExternal() && recursos.isEmpty()) {
                            recursos.putAll(recursosForLoad);
                        }
                    }
                }
            }
        } catch (final ConsignanteControllerException ex) {
            // Esta linha deve vir antes do LOG.error, porque senão não resolve
            // a questão do loop de StackOverflow
            erroCargaTabela = true;
            LOG.error(ex.getMessage(), ex);
        }
    }

    private static void replaceSubKeys(Map<String, String> recursos, Map<String, String> propriedades, String chaveEspecifica) {
        Set<String> chaves = null;
        if (!TextHelper.isNull(chaveEspecifica)) {
            chaves = new HashSet<>();
            chaves.add(chaveEspecifica);
        } else {
            chaves = recursos.keySet();
        }

        for (final String chave : chaves) {
            boolean aplicarMarcacaoMarkdown = false;
            String valor = recursos.get(chave);

            if (valor.contains(PADRAO_OPERACAO_MARKDOWN)) {
                valor = removeMarcacaoMarkdown(valor);
                aplicarMarcacaoMarkdown = true;
            }

            if ((valor != null) && (valor.indexOf(PADRAO_INICIO) >= 0)) {
                int inicio = 0;
                int fim = 0;
                while (((inicio = valor.indexOf(PADRAO_INICIO, inicio)) >= 0) && ((fim = valor.indexOf(PADRAO_TERMINO, inicio)) >= 0)) {

                    String variavelChave = valor.substring(inicio + PADRAO_INICIO.length(), fim);
                    String variavelValor = null;
                    if ((fim = variavelChave.indexOf(PADRAO_TERMINO_OPERACAO, 0)) >= 0) {
                        if ((inicio = variavelChave.indexOf(PADRAO_OPERACAO_LOWER)) >= 0) {
                            variavelChave = variavelChave.substring(inicio + PADRAO_OPERACAO_LOWER.length(), fim);
                            if (recursos.get(variavelChave) != null) {
                                variavelValor = recursos.get(variavelChave);
                                if (variavelValor.indexOf(PADRAO_INICIO) >= 0) {
                                    replaceSubKeys(recursos, propriedades, variavelChave);
                                    variavelValor = recursos.get(variavelChave).toLowerCase();
                                } else {
                                    variavelValor = variavelValor.toLowerCase();
                                }
                            } else if (recursos.get(chave) != null) {
                                variavelValor = recursos.get(chave);
                                if (variavelValor.indexOf(PADRAO_INICIO) >= 0) {
                                    variavelValor = propriedades.get(variavelChave) != null ? propriedades.get(variavelChave) : "";
                                    variavelValor = !TextHelper.isNull(variavelValor) ? variavelValor.toLowerCase() : "";
	                            } else {
	                                LOG.error(variavelChave);
	                                throw new RuntimeException(variavelChave);
	                            }
                            }
                        } else if ((inicio = variavelChave.indexOf(PADRAO_OPERACAO_UPPER)) >= 0) {
                            variavelChave = variavelChave.substring(inicio + PADRAO_OPERACAO_UPPER.length(), fim);
                            if (recursos.get(variavelChave) != null) {
                                variavelValor = recursos.get(variavelChave);
                                if (variavelValor.indexOf(PADRAO_INICIO) >= 0) {
                                    replaceSubKeys(recursos, propriedades, variavelChave);
                                    variavelValor = recursos.get(variavelChave).toUpperCase();
                                } else {
                                    variavelValor = recursos.get(variavelChave).toUpperCase();
                                }
                            } else if (recursos.get(chave) != null) {
                                variavelValor = recursos.get(chave);
                                if (variavelValor.indexOf(PADRAO_INICIO) >= 0) {
                                    variavelValor = propriedades.get(variavelChave) != null ? propriedades.get(variavelChave) : "";
                                    variavelValor = !TextHelper.isNull(variavelValor) ? variavelValor.toUpperCase() : "";
                                }
                            } else {
                                LOG.error(variavelChave);
                                throw new RuntimeException(variavelChave);
                            }
                        }
                    } else {
                        variavelValor = recursos.get(variavelChave);
                        if (!TextHelper.isNull(variavelValor) && (variavelValor.indexOf(PADRAO_INICIO) >= 0)) {
                            replaceSubKeys(recursos, propriedades, variavelChave);
                            variavelValor = recursos.get(variavelChave);
                        }
                    }

                    variavelValor = (variavelValor != null) ? variavelValor : "";
                    if (TextHelper.isNull(variavelValor) && (propriedades != null)) {
                        variavelValor = propriedades.get(variavelChave) != null ? propriedades.get(variavelChave) : "";
                    }
                    if (TextHelper.isNull(variavelValor)) {
                        LOG.error("Sub-chave não localizada: " + variavelChave + " - " + chave);
                    }

                    valor = valor.replace(PADRAO_INICIO + PADRAO_OPERACAO_LOWER + variavelChave + PADRAO_TERMINO_OPERACAO + PADRAO_TERMINO, variavelValor);
                    valor = valor.replace(PADRAO_INICIO + PADRAO_OPERACAO_UPPER + variavelChave + PADRAO_TERMINO_OPERACAO + PADRAO_TERMINO, variavelValor);
                    valor = valor.replace(PADRAO_INICIO + variavelChave + PADRAO_TERMINO, variavelValor);
                    inicio = inicio + variavelValor.length();
                }

                recursos.put(chave, valor);
            }

            // DESENV-15882: Necessário tratar o markdown fora do loop anterior para que se existir alguma marcação dentro da textoSistema
            // Ela não seja afeta ou o markdown não seja executado.
            try {
                if (aplicarMarcacaoMarkdown) {
                    valor = markdownProcessor.process(valor);
                    recursos.put(chave, valor);
                }
            } catch (final IOException e) {
                throw new RuntimeException(getMessage("mensagem.erro.interpretar.texto.sistema", AcessoSistema.getAcessoUsuarioSistema()));
            }
        }
    }

    public void reset() {
        if (ExternalCacheHelper.hasExternal()) {
            final String prefix = getClass().getSimpleName();
            recursos = new ExternalMap<>(prefix + "-recursos");
        } else {
            recursos = new HashMap<>();
        }
    }

    /**
     * Retorna uma mensagem do ApplicationResources
     * @param chave : a chave da mensagem no ApplicationResources
     * @param responsavel : Usuário autenticado no sistema
     * @param args : Argumentos de substituição da mensagem
     * @return : a mensagem formatada
     */
    public static String getMessage(String chave, AcessoSistema responsavel, String... args) {
        SingletonHelper.instance.carregarRecursosBanco();


        // Busca mensagem personalizada por papel
        if ((responsavel != null) && (responsavel.getUsuCodigo() != null) && !responsavel.isSistema() && (responsavel.getTipoEntidade() != null)) {
            final String chavePapel = responsavel.getTipoEntidade().toLowerCase() + "." + chave;
            if (SingletonHelper.instance.recursos.containsKey(chavePapel)) {
                return getMessage(chavePapel, responsavel, args);
            }
        }

        // Busca mensagem padrão
        String mensagem = SingletonHelper.instance.recursos.get(chave);

        // Por compatibilidade, caso a mensagem não exista, busca do properties, pois
        // algumas chaves são gravadas lá, como "release.date" e "release.tag"
        if (TextHelper.isNull(mensagem)) {
            mensagem = (SingletonHelper.instance.propriedades.get(chave));
        }

        if (!SingletonHelper.instance.recursos.containsKey(chave) && !SingletonHelper.instance.propriedades.containsKey(chave)) {
            // Se a chave não foi encontrada, e é chave de LOTE ou HOST-HOST (identificadas pelos sufixos)
            // remove o sufixo e localiza a mensagem padrão a ser utilizada, e gera log de WARN.
            if (chave.endsWith(ZetraException.MENSAGEM_LOTE) || chave.endsWith(ZetraException.MENSAGEM_LOTE_FEBRABAN) || chave.endsWith(ZetraException.MENSAGEM_PROCESSAMENTO_XML)) {
                LOG.warn("Chave não encontrada: " + chave);
                final String novaChave = chave.replaceAll(ZetraException.MENSAGEM_LOTE, "").replaceAll(ZetraException.MENSAGEM_LOTE_FEBRABAN, "").replaceAll(ZetraException.MENSAGEM_PROCESSAMENTO_XML, "");
                return getMessage(novaChave, responsavel, args);
            }
            throw new RuntimeException(getMessage("mensagem.erro.chave.nao.encontrada", responsavel, chave));
        }

        // Formata a mensagem, caso tenha operadores de substituição {0}, {1} ...
        if (isNotEmpty(args)) {
            mensagem = new java.text.MessageFormat(escape(mensagem)).format(args);
        }

        if (TEXTO_SISTEMA_VAZIO.equals(mensagem)) {
            return "";
        }

       return mensagem;
    }

    /**
     *
     * @param texto
     * @param responsavel
     * @return
     */
    public static String interpolate(String texto, AcessoSistema responsavel) {
        final String chave = UUID.randomUUID().toString();// "__TEMP__interpolate__";
//        synchronized (chave) {
            SingletonHelper.instance.recursos.put(chave, texto);
            replaceSubKeys(SingletonHelper.instance.recursos, SingletonHelper.instance.propriedades, chave);
            return SingletonHelper.instance.recursos.remove(chave);
//        }
    }

    /**
     * Faz escape de aspas simples evitando erro na aplicação de padrões de substituição
     * @param mensagem
     * @return
     */
    protected static String escape(String mensagem) {
        if ((mensagem == null) || (mensagem.indexOf('\'') < 0)) {
            return mensagem;
        }
        return mensagem.replace("\'", "\'\'");
    }

    /**
     * Verifica se o array de parâmetros contém algum parâmetro
     * não nulo.
     * @param args
     * @return
     */
    private static boolean isNotEmpty(String[] args) {
        if ((args != null) && (args.length > 0)) {
            for (final String arg : args) {
                // A verificação (args[i] != null) é necessária para evitar que os operadores
                // sejam substituídos por NULL, deixando estes intactos, para serem tratados
                // por códigos javascript.
                if (arg != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Remove a marcação de markdown para edição no texto sistema
     * @param input
     * @return
     */
    public static String removeMarcacaoMarkdown(String input) {
       if (input.contains(PADRAO_OPERACAO_MARKDOWN)) {
           final String textoSistema = input.replace(PADRAO_INICIO+PADRAO_OPERACAO_MARKDOWN, "");
           return textoSistema.substring(0, textoSistema.length()-2);
       }
       return input;
    }

    /**
     * Adiciona a marcação de markdown para edição no texto sistema, neste caso coloca o texto inteiro.
     * @param input
     * @return
     */
    public static String addMarcacaoMarkdown(String input) {
       if (!input.contains(PADRAO_OPERACAO_MARKDOWN)) {
           return PADRAO_INICIO+PADRAO_OPERACAO_MARKDOWN+input+PADRAO_TERMINO_OPERACAO+PADRAO_TERMINO;
       }
       return input;
    }
}
