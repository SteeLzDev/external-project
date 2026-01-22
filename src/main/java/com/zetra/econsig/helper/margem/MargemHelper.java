package com.zetra.econsig.helper.margem;

import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: MargemHelper</p>
 * <p>Description: Singleton repositório das descrições das margens do sistema</p>
 * <p>Copyright: Copyright (c) 2008-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class MargemHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MargemHelper.class);

    private Map<String, MargemTO> cache;

    private static class SingletonHelper {
        private static final MargemHelper instance = new MargemHelper();
    }

    public static MargemHelper getInstance() {
        return SingletonHelper.instance;
    }

    private MargemHelper() {
        if (ExternalCacheHelper.hasExternal()) {
            cache = new ExternalMap<>();

        } else {
            cache = new HashMap<>();
        }
    }

    /**
     * Retorna uma margem cadastrada no sistema
     * @param marCodigo : o código da margem
     * @param responsavel : usuário que realiza a consulta
     * @return : as informações da margem
     */
    public MargemTO getMargem(Short marCodigo, AcessoSistema responsavel) {
        if (cache.isEmpty()) {
            synchronized (this) {
                if (cache.isEmpty()) {
                    try {
                        // Busca a descrição das funções do sistema
                        final MargemController margemController = ApplicationContextProvider.getApplicationContext().getBean(MargemController.class);
                        final Map<String, MargemTO> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : cache;
                        for (final MargemTO margem : margemController.lstMargemRaiz(responsavel)) {
                            mapForLoad.put(margem.getMarCodigo().toString(), margem);
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
        return cache.get(marCodigo != null ? marCodigo.toString() : "");
    }

    /**
     * Retorna a descrição de uma margem
     * @param marCodigo   o código da margem
     * @param responsavel usuário que realiza a consulta
     * @return
     */
    public String getMarDescricao(Short marCodigo, AcessoSistema responsavel) {
        final MargemTO margem = getMargem(marCodigo, responsavel);
        return margem != null ? margem.getMarDescricao() : "";
    }

    /**
     * Limpa da memória as informações das margens.
     */
    public void reset() {
        if (ExternalCacheHelper.hasExternal()) {
            cache.clear();
        } else {
            cache = new HashMap<>();
        }
   }
}