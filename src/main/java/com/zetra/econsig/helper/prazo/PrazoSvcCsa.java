package com.zetra.econsig.helper.prazo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalSet;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

public class PrazoSvcCsa {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PrazoSvcCsa.class);

    private static final String EMPTY_CACHE = "__EMPTY_CACHE__";
    private static final String FORMATO_CHAVE_CACHE_PRAZOS = "%s:%s:%s";

    private static final Set<String> cache;

    static {
        if (ExternalCacheHelper.hasExternal()) {
            cache = new ExternalSet<>();
        } else {
            cache = new HashSet<>();
        }
    }

    // DESENV-17859 : necessário para poder fazer o deserialize do redis
    public PrazoSvcCsa() {
    }

    private static synchronized void load(AcessoSistema responsavel) {
        if (cache.isEmpty()) {
            try {
                final SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
                final List<TransferObject> listaPrazos = simulacaoController.lstPrazoSvcCsa(responsavel);
                if (listaPrazos == null || listaPrazos.isEmpty()) {
                    // Se não há prazos ativos, cadastra uma chave para evitar que fique em loop
                    // tentando carregar um cache que não existe
                    cache.add(EMPTY_CACHE);
                } else {
                    for (TransferObject prazo : listaPrazos) {
                        String svcCodigo = prazo.getAttribute(Columns.PRZ_SVC_CODIGO).toString();
                        String csaCodigo = prazo.getAttribute(Columns.PZC_CSA_CODIGO).toString();
                        String przVlr = prazo.getAttribute(Columns.PRZ_VLR).toString();
                        cache.add(getCacheKey(svcCodigo, csaCodigo, przVlr));
                    }
                }
            } catch (SimulacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private static String getCacheKey(String svcCodigo, String csaCodigo, String przVlr) {
        return String.format(FORMATO_CHAVE_CACHE_PRAZOS, svcCodigo, csaCodigo, przVlr);
    }

    public static boolean temPrazo(String svcCodigo, String csaCodigo, String przVlr, AcessoSistema responsavel) {
        if (cache.isEmpty()) {
            load(responsavel);
        }
        return cache.contains(getCacheKey(svcCodigo, csaCodigo, przVlr));
    }

    public static synchronized void reset() {
        cache.clear();
    }
}
