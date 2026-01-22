package com.zetra.econsig.helper.limiteoperacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.RegraLimiteOperacaoControllerException;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalList;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.limiteoperacao.RegraLimiteOperacaoController;
import com.zetra.econsig.web.ApplicationContextProvider;

public class RegraLimiteOperacaoCache {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraLimiteOperacaoCache.class);

    private List<TransferObject> regras;

    private static class SingletonHelper {
        private static final RegraLimiteOperacaoCache instance = new RegraLimiteOperacaoCache();
    }

    public static RegraLimiteOperacaoCache getInstance() {
        return SingletonHelper.instance;
    }

    private RegraLimiteOperacaoCache() {
        if (ExternalCacheHelper.hasExternal()) {
            regras = new ExternalList<>();
        } else {
            regras = new ArrayList<>();
        }
    }

    private void carregarRecursosBanco() {
        try {
            if (regras.isEmpty()) {
                final RegraLimiteOperacaoController controller = ApplicationContextProvider.getApplicationContext().getBean(RegraLimiteOperacaoController.class);
                regras.addAll(Collections.unmodifiableList(controller.lstRegraLimiteOperacao(AcessoSistema.getAcessoUsuarioSistema())));
            }
        } catch (final RegraLimiteOperacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public static void reset() {
        SingletonHelper.instance.regras.clear();
    }

    public static List<TransferObject> getRegras() {
        SingletonHelper.instance.carregarRecursosBanco();
        return SingletonHelper.instance.regras;
    }
}
