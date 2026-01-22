package com.zetra.econsig.helper.parametro;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ParamSist</p>
 * <p>Description: Singleton repositório dos parametrôs de Sistema</p>
 * <p>Copyright: Copyright (c) 2003-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class ParamSist {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ParamSist.class);

    private Map<String, String> cache;

    private static class SingletonHelper {
        private static final ParamSist instance = new ParamSist();
    }

    public static ParamSist getInstance() {
        return SingletonHelper.instance;
    }

    private ParamSist() {
        if (ExternalCacheHelper.hasExternal()) {
            cache = new ExternalMap<>();
        } else {
            cache = new HashMap<>();
        }
    }

    public boolean hasParam(Object chave) {
        return (cache.containsKey(chave) && (cache.get(chave) != null));
    }

    /**
     * Retorna um parâmetro do sistema
     * @param chave : o nome do parâmetro
     * @param responsavel : usuário que realiza a consulta ao parametro
     * @return : o valor do parâmetro
     */
    public Object getParam(Object chave, AcessoSistema responsavel) {
        // Recarrega os parâmetros apenas se o cache for vazio.
        // Para recarregar, deve-se entrar na tela de edição de parâmetros
        // do sistema e salvar a nova configuração.
        if (cache.isEmpty()) {
            synchronized (this) {
                if (cache.isEmpty()) {
                    try {
                        // Traz Parâmetros da Base de Dados
                        final ParametroDelegate delegate = new ParametroDelegate();
                        final List<TransferObject> params = delegate.selectParamSistCse(null, null, null, null, responsavel);
                        final Map<String, String> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : cache;
                        for (final TransferObject param : params) {
                            mapForLoad.put((String) param.getAttribute(Columns.TPC_CODIGO), (String) param.getAttribute(Columns.PSI_VLR));
                        }
                        if (ExternalCacheHelper.hasExternal() && cache.isEmpty()) {
                            cache.putAll(mapForLoad);
                        }
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        }

        return cache.get(chave);
    }

    public Object getParamOrDefault(String chave, Object defaultValue, AcessoSistema responsavel) {
        final Object paramValue = getParam(chave, responsavel);
        return TextHelper.isNull(paramValue) ? defaultValue : paramValue;
    }

    public void setParam(String chave, String valor) {
        cache.put(chave, valor);
    }

    public void dropParam(Object chave) {
        cache.remove(chave);
    }

    public void reset() {
        if (ExternalCacheHelper.hasExternal()) {
            cache.clear();
        } else {
            cache = new HashMap<>();
        }
    }

    public static boolean getBoolParamSist(Object chave, AcessoSistema responsavel) {
        final Object param = getInstance().getParam(chave, responsavel);
        return ((param != null) && param.equals(CodedValues.TPC_SIM));
    }

    public static int getIntParamSist(Object chave, int defaultValue, AcessoSistema responsavel) {
        final Object param = getInstance().getParam(chave, responsavel);
        if (TextHelper.isNum(param)) {
            try {
                return Integer.parseInt(param.toString());
            } catch (final NumberFormatException ex) {
                LOG.error("Valor incorreto para o parâmetro de sistema \"" + chave + "\": \"" + param + "\"");
            }
        }
        return defaultValue;
    }

    public static BigDecimal getFloatParamSist(Object chave, BigDecimal defaultValue, AcessoSistema responsavel) {
        final Object param = getInstance().getParam(chave, responsavel);
        if (TextHelper.isDecimalNum(param)) {
            try {
                return new BigDecimal(param.toString());
            } catch (final NumberFormatException ex) {
                LOG.error("Valor incorreto para o parâmetro de sistema \"" + chave + "\": \"" + param + "\"");
            }
        }
        return defaultValue;
    }

    /**
     * Verifica se o parâmetro do sistema é igual ao valor passado. Retornará verdadeiro se:
     * 1. Tanto o valor a ser comparado como o valor do parâmetro forem nulos;
     * 2. Tanto o valor a ser comparado como o valor do parâmetro não forem nulos e forem iguais.
     *
     * @param chave Identificador do parâmetro a ser verificado.
     * @param valor Valor a ser comparado.
     * @param responsavel Usuário
     * @return Se o parâmetro é igual ao valor passado.
     */
    public static boolean paramEquals(Object chave, Object valor, AcessoSistema responsavel) {
        final Object param = SingletonHelper.instance.getParam(chave, responsavel);
        return ((valor == null) && (param == null)) || ((valor != null) && (param != null) && param.equals(valor));
    }

    private static final String PARAM_DIR_RAIZ_ARQUIVOS_VALIDADO = "__PARAM_DIR_RAIZ_ARQUIVOS_VALIDADO__";

    public static String getDiretorioRaizArquivos() {
        final Object paramValidado = getInstance().getParam(PARAM_DIR_RAIZ_ARQUIVOS_VALIDADO, AcessoSistema.getAcessoUsuarioSistema());
        if (paramValidado != null) {
            return paramValidado.toString();
        }

        final Object param = getInstance().getParam(CodedValues.TPC_DIR_RAIZ_ARQUIVOS, AcessoSistema.getAcessoUsuarioSistema());
        try {
            if (param != null) {
                final String pathParam = param.toString();
                if (FileHelper.isPathSafe(pathParam)) {
                    final File path = new File(pathParam);
                    if (path.exists() && path.canRead() && path.getCanonicalPath().startsWith(pathParam)) {
                        // getInstance().setParam(PARAM_DIR_RAIZ_ARQUIVOS_VALIDADO, pathParam);
                        return pathParam;
                    }
                }
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        LOG.warn("Valor invalido parametro de sistema 5. Assumindo valor default.");
        return "/home/eConsig/arquivos";
    }
}