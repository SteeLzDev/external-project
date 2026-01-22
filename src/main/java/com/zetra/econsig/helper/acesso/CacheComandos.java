package com.zetra.econsig.helper.acesso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: CacheComandos</p>
 * <p>Description: Enum para implementação de singleton de cache dos comandos pendentes do centralizador</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum CacheComandos {
    INSTANCE;

    CacheComandos() {
        cache = new HashMap<>();
        chave = Long.valueOf("0");
    }

    private final Map<Long, Long> cache;
    private Long chave;

    /**
     * Valida no cache a informação do comando recebido previamente e pendente de execução.
     * Remove a entrada do cache para evitar o replay do mesmo pacote de confirmação.
     * @param chave  Chave de recuperação de referência de comando
     * @return  Um timestamp
     */
    public Boolean hasComando(Long chaveValidar) {
        cleanComandos();
        synchronized (cache) {
            Boolean resposta = cache.containsKey(chaveValidar);
            if (resposta) {
                cache.remove(chaveValidar);
            }
            return resposta;
        }
    }

    /**
     * Atribui uma referência a um comando pendente de confirmação e retorna a chave de consulta ao cache.
     * @param valor  Um timestamp
     * @return  Uma chave
     */
    public Long setComando(Long valor) {
        cleanComandos();
        synchronized (cache) {
            chave = (chave == Long.MAX_VALUE) ? chave = Long.valueOf("0") : ++chave;
        }
        synchronized (cache) {
            cache.put(chave, valor);
        }
        return chave;
    }

    /**
     * Remove referência a comandos expirados.
     * @param chave
     */
    public void cleanComandos() {
        synchronized (cache) {
            List<Long> chaves = new ArrayList<>(cache.keySet());
            for (Long chave: chaves) {
                if (System.currentTimeMillis() - cache.get(chave)  > CodedValues.TIMEOUT_COMUNICACAO) {
                    cache.remove(chave);
                }
            }
        }
    }
}