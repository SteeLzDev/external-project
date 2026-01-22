package com.zetra.econsig.helper.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ExternalSet</p>
 * <p>Description: Wrapper para usar java.util.Set mantido externamente (atualmente usando Redis).</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 */
public class ExternalSet<V> implements Set<V> {
    private final transient RedisTemplate<String, V> redisTemplateForCache;

    private final SetOperations<String, V> redisCache;

    private final String name;
    private String key;

    public ExternalSet() {
        this(ExternalCacheHelper.getCallerName());
    }

    public ExternalSet(String name) {
        this(name, true);
    }

    public ExternalSet(String name, boolean resetCache) {
        this.name = name;
        key = ExternalCacheHelper.buildKey(name);
        redisTemplateForCache = ApplicationContextProvider.getApplicationContext().getBean("redisTemplateForCache", RedisTemplate.class);
        if (resetCache) {
            redisTemplateForCache.delete(key);
        }
        redisCache = redisTemplateForCache.opsForSet();
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
    public int size() {
        final Long size = redisCache.size(key);
        return (size == null) ? 0 : size.intValue();
    }

    @Override
    public boolean isEmpty() {
        return redisCache.size(key) == 0;
    }

    @Override
    public boolean contains(Object o) {
        return redisCache.isMember(key, o);
    }

    @Override
    public Iterator<V> iterator() {
        final Set<V> members = redisCache.members(key);
        return members == null ? null : members.iterator();
    }

    @Override
    public Object[] toArray() {
        Set<V> members = redisCache.members(key);
        if (members == null) {
            members = new HashSet<>();
        }
        return members.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Set<V> members = redisCache.members(key);
        if (members == null) {
            members = new HashSet<>();
        }
        return members.toArray(a);
    }

    @Override
    public boolean add(V e) {
        redisCache.add(key, e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        redisCache.remove(key, o);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        //TODO implementar
        redisCache.isMember(key, c.toArray());
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        for (final V i : c) {
            redisCache.add(key, i);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        for (final V m : redisCache.members(key)) {
            if (!c.contains(m)) {
                redisCache.remove(key, m);
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (final Object o : c) {
            redisCache.remove(key, o);
        }
        return true;
    }
}