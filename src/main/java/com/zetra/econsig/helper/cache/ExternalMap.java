package com.zetra.econsig.helper.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ExternalMap</p>
 * <p>Description: Wrapper para usar java.util.Map mantido externamente (atualmente usando Redis).</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 */
public class ExternalMap<K, V> implements Map<K, V> {

    private final RedisTemplate<String, V> redisTemplateForCache;

    private final HashOperations<String, K, V> redisCache;

    private final String name;

    private String key;

    private final K hashForNull;

    public ExternalMap() {
        this(ExternalCacheHelper.getCallerName(), null);
    }

    public ExternalMap(String name) {
        this(name, null, true);

    }
    
    public ExternalMap(String name, K hashForNull) {
        this(name, hashForNull, true);
    }

    public ExternalMap(String name, K hashForNull, boolean resetCache) {
        this.name = name;
        this.hashForNull = hashForNull;
        key = ExternalCacheHelper.buildKey(name);
        redisTemplateForCache = ApplicationContextProvider.getApplicationContext().getBean("redisTemplateForCache", RedisTemplate.class);
        if (resetCache) {
            redisTemplateForCache.delete(key);
        }
        redisCache = redisTemplateForCache.opsForHash();
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void clear() {
        redisTemplateForCache.delete(key);
        key = ExternalCacheHelper.buildKey(name);
    }

    @Override
    public boolean containsKey(Object hash)  {
        return redisCache.hasKey(key, hash == null ? hashForNull : hash);
    }

    @Override
    public V get(Object hash) {
        return redisCache.get(key, hash == null ? hashForNull : hash);
    }

    @Override
    public boolean isEmpty() {
        return redisCache.size(key) == 0;
    }

    @Override
    public V put(K hash, V value) {
        redisCache.put(key, hash == null ? hashForNull : hash, value);
        return value;
    }

    @Override
    public V remove(Object hash) {
        final V oldValue = get(hash == null ? hashForNull : hash);
        redisCache.delete(key, hash == null ? hashForNull : hash);
        return oldValue;
    }

    @Override
    public int size() {
        return redisCache.size(key).intValue();
    }

    @Override
    public boolean containsValue(Object value) {
        return redisCache.entries(key).containsValue(value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        redisCache.putAll(key, m);
    }

    @Override
    public Set<K> keySet() {
        return redisCache.keys(key);
    }

    @Override
    public Collection<V> values() {
        return redisCache.values(key);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return redisCache.entries(key).entrySet();
    }
}