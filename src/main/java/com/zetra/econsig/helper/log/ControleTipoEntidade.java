package com.zetra.econsig.helper.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.TipoEntidade;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ControleTipoEntidade</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
public class ControleTipoEntidade {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ControleTipoEntidade.class);

    private Map<String, Map<String, String>> cache;

    private static class SingletonHelper {
        private static final ControleTipoEntidade instance = new ControleTipoEntidade();
    }

    public static ControleTipoEntidade getInstance() {
        return SingletonHelper.instance;
    }

    private ControleTipoEntidade() {
        reset();
    }

    public void reset() {
        if (ExternalCacheHelper.hasExternal()) {
            cache = new ExternalMap<>();
        } else {
            cache = new HashMap<>();
        }

        try {
            final LogDelegate logDelegate = new LogDelegate();
            final List<TipoEntidade> tiposEntidade = logDelegate.getTiposEntidade(AcessoSistema.getAcessoUsuarioSistema());
            if (tiposEntidade != null) {
                final Map<String, Map<String, String>> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : cache;
                for (final TipoEntidade tipoEntidade : tiposEntidade) {
                    final Map<String, String> map = new HashMap<>();
                    if (!TextHelper.isNull(tipoEntidade.getTenCampoEnt00())) {
                        map.put(tipoEntidade.getTenCampoEnt00(), Columns.LOG_COD_ENTIDADE_00);
                    }
                    if (!TextHelper.isNull(tipoEntidade.getTenCampoEnt01())) {
                        map.put(tipoEntidade.getTenCampoEnt01(), Columns.LOG_COD_ENTIDADE_01);
                    }
                    if (!TextHelper.isNull(tipoEntidade.getTenCampoEnt02())) {
                        map.put(tipoEntidade.getTenCampoEnt02(), Columns.LOG_COD_ENTIDADE_02);
                    }
                    if (!TextHelper.isNull(tipoEntidade.getTenCampoEnt03())) {
                        map.put(tipoEntidade.getTenCampoEnt03(), Columns.LOG_COD_ENTIDADE_03);
                    }
                    if (!TextHelper.isNull(tipoEntidade.getTenCampoEnt04())) {
                        map.put(tipoEntidade.getTenCampoEnt04(), Columns.LOG_COD_ENTIDADE_04);
                    }
                    if (!TextHelper.isNull(tipoEntidade.getTenCampoEnt05())) {
                        map.put(tipoEntidade.getTenCampoEnt05(), Columns.LOG_COD_ENTIDADE_05);
                    }
                    if (!TextHelper.isNull(tipoEntidade.getTenCampoEnt06())) {
                        map.put(tipoEntidade.getTenCampoEnt06(), Columns.LOG_COD_ENTIDADE_06);
                    }
                    if (!TextHelper.isNull(tipoEntidade.getTenCampoEnt07())) {
                        map.put(tipoEntidade.getTenCampoEnt07(), Columns.LOG_COD_ENTIDADE_07);
                    }
                    if (!TextHelper.isNull(tipoEntidade.getTenCampoEnt08())) {
                        map.put(tipoEntidade.getTenCampoEnt08(), Columns.LOG_COD_ENTIDADE_08);
                    }
                    if (!TextHelper.isNull(tipoEntidade.getTenCampoEnt09())) {
                        map.put(tipoEntidade.getTenCampoEnt09(), Columns.LOG_COD_ENTIDADE_09);
                    }
                    if (!TextHelper.isNull(tipoEntidade.getTenCampoEnt10())) {
                        map.put(tipoEntidade.getTenCampoEnt10(), Columns.LOG_COD_ENTIDADE_10);
                    }
                    mapForLoad.put(tipoEntidade.getTenCodigo(), map);
                }
                if (ExternalCacheHelper.hasExternal() && cache.isEmpty()) {
                    cache.putAll(mapForLoad);
                }
            }

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public synchronized String recuperaColuna(String tipoEntidade, String campo) {
        if (TextHelper.isNull(tipoEntidade)) {
            return null;
        }

        final Map<String, String> chaves = cache.get(tipoEntidade);
        final String codigo = chaves != null ? chaves.get(campo.toUpperCase()) : "";

        if (TextHelper.isNull(codigo)) {
            LOG.warn("Não existe campo mapeado no LOG para tipo de entidade '" + tipoEntidade + "' e campo '" + campo + "'.");
        }
        return codigo;
    }

    public Map<String, Map<String, String>> lstTipoEntidadePorEntidade () {
        final Map<String, Map<String, String>> retorno = new HashMap<>();

        for (final Map.Entry<String, Map<String, String>> cacheEntry : cache.entrySet()) {
            final String tenCodigo = cacheEntry.getKey();
            for (final Map.Entry<String, String> valoresEntry : cacheEntry.getValue().entrySet()) {
                final Map<String, String> x = retorno.computeIfAbsent(valoresEntry.getKey(), k -> new HashMap<>());
                x.put(tenCodigo, valoresEntry.getValue());
            }
        }

        return retorno;
    }
}
