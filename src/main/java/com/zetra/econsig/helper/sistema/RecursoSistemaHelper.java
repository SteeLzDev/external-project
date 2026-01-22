package com.zetra.econsig.helper.sistema;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.RecursoSistema;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.web.ApplicationContextProvider;


/**
 * <p>Title: RecursoSistemaHelper</p>
 * <p>Description: Classe utilitária para gestão dos recursos customizados
 * em cada sistema através dos registros em banco de dados.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel
 */
public class RecursoSistemaHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RecursoSistemaHelper.class);

    private final Map<String, byte[]> cacheRecursos;
    private boolean carregado;

    private static class SingletonHelper {
        private static final RecursoSistemaHelper instance = new RecursoSistemaHelper();
    }

    public static RecursoSistemaHelper getInstance() {
        return SingletonHelper.instance;
    }

    private RecursoSistemaHelper() {
        if (ExternalCacheHelper.hasExternal()) {
            cacheRecursos = new ExternalMap<>();
        } else {
            cacheRecursos = new HashMap<>();
        }
    }

    private void carregar() {
        try {
            if (!carregado) {
                synchronized (this) {
                    if (!carregado) {
                        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
                        final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);

                        final Decoder decoder = Base64.getDecoder();
                        final List<RecursoSistema> recursos = sistemaController.lstRecursoSistema(responsavel);
                        final Map<String, byte[]> mapForLoad = ExternalCacheHelper.hasExternal() ? new HashMap<>() : cacheRecursos;
                        for (final RecursoSistema recurso : recursos) {
                            mapForLoad.put(recurso.getResChave(), decoder.decode(recurso.getResConteudo()));
                        }
                        if (ExternalCacheHelper.hasExternal() && cacheRecursos.isEmpty()) {
                            cacheRecursos.putAll(mapForLoad);
                        }

                        carregado = true;
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public static void reset() {
        SingletonHelper.instance.cacheRecursos.clear();
        SingletonHelper.instance.carregado = false;
    }

    public static byte[] getRecurso(String chave) {
        SingletonHelper.instance.carregar();
        return SingletonHelper.instance.cacheRecursos.get(chave);
    }
}
