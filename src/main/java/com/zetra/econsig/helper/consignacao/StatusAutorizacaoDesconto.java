package com.zetra.econsig.helper.consignacao;

import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.helper.cache.ExternalCacheHelper;
import com.zetra.econsig.helper.cache.ExternalMap;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
/**
 * <p>Title: StatusAutorizacaoDesconto</p>
 * <p>Description: Singleton repositório dos status de autorização desconto</p>
 * <p>Copyright: Copyright (c) 2012-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class StatusAutorizacaoDesconto {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(StatusAutorizacaoDesconto.class);

    private Map<String, String> cache;
    private final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

    private static class SingletonHelper {
        private static final StatusAutorizacaoDesconto instance = new StatusAutorizacaoDesconto();
    }

    public static StatusAutorizacaoDesconto getInstance() {
        return SingletonHelper.instance;
    }

    private StatusAutorizacaoDesconto() {
        if (ExternalCacheHelper.hasExternal()) {
            cache = new ExternalMap<>();
        } else {
            cache = new HashMap<>();
        }
    }

    public void reset() {
        cache.clear();
    }

    public boolean hasParam(String chave) {
        return (cache.containsKey(chave) && (cache.get(chave) != null));
    }

    /**
     * Retorna a descrição do status da autorização desconto
     * @param sadCodigo : codigo do status da autorização desconto
     * @return : descrição do status da autorização desconto
     */
    public Object getDescricao(Object sadCodigo) {
        // Recarrega os status da autorização desconto apenas se o cache for vazio.
        if (cache.isEmpty()) {
            synchronized (this) {
                if (cache.isEmpty()) {
                    try {
                        final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
                        cache.putAll(adeDelegate.selectStatusAutorizacao(responsavel));
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        }
        return cache.get(sadCodigo);
    }
}