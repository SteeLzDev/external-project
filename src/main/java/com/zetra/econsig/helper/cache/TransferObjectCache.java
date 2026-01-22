package com.zetra.econsig.helper.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.zetra.econsig.dto.TransferObject;

/**
 * <p>Title: TransferObjectCache</p>
 * <p>Description: Armazenamento em cache para objetos TransferObject</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TransferObjectCache {

    private Map<Object, TransferObject> cache;

    private static class SingletonHelper {
        private static final TransferObjectCache instance = new TransferObjectCache();
    }

    public static TransferObjectCache getInstance() {
        return SingletonHelper.instance;
    }

    private TransferObjectCache() {
        if (ExternalCacheHelper.hasExternal()) {
            cache = new ExternalMap<>();
        } else {
            cache = new HashMap<>();
        }
    }

    public Optional<TransferObject> getTransferObject(Object chave) {
        return Optional.ofNullable(cache.get(chave));
    }

    public boolean hasParam(Object chave) {
        return (cache.containsKey(chave) && (cache.get(chave) != null));
    }

    public void setTransferObject(String chave, TransferObject valor) {
        cache.put(chave, valor);
    }

    public void setTransferObjectIfAbsent(String chave, TransferObject valor) {
        cache.putIfAbsent(chave, valor);
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

}
