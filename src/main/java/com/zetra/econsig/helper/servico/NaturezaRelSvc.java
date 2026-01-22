package com.zetra.econsig.helper.servico;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ServicoDelegate;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;

/**
 * <p>Title: NaturezaRelSvc</p>
 * <p>Description: Singleton repositório das naturezas de relacionamento de servico</p>
 * <p>Copyright: Copyright (c) 2009-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class NaturezaRelSvc {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(NaturezaRelSvc.class);

    private Map<String, Boolean> cache;

    private static class SingletonHelper {
        private static final NaturezaRelSvc instance = new NaturezaRelSvc();
    }

    public static NaturezaRelSvc getInstance() {
        return SingletonHelper.instance;
    }

    private NaturezaRelSvc() {
        if (ExternalCacheHelper.hasExternal()) {
            cache = new ExternalMap<>();

        } else {
            cache = new HashMap<>();
        }
    }

    /**
     * Retorna se uma natureza de relacionamnto de serviço existe
     * @param chave : natureza de relacionamento de serviço a ser verificada
     * @return : se a natureza existe
     */
    public boolean exists(Object chave) {
        // Recarrega os valores apenas se o cache for vazio.
        if (cache.isEmpty()) {
            synchronized (this) {
                if (cache.isEmpty()) {
                    try {
                        // Busca as naturezas de relacionamento serviço
                        final ServicoDelegate svcDelegate = new ServicoDelegate();
                        final List<String> tntCodigos = svcDelegate.lstTipoNaturezasRelSvc();
                        final Map<String, Boolean> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : cache;
                        for (final String element : tntCodigos) {
                            mapForLoad.put(element, Boolean.TRUE);
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
        final Boolean value = cache.get(chave);
        return ((value != null) && value.booleanValue());
    }

    public void reset() {
        if (ExternalCacheHelper.hasExternal()) {
            cache.clear();
        } else {
            cache = new HashMap<>();
        }
   }
}