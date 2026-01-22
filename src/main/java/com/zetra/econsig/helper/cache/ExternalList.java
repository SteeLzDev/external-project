package com.zetra.econsig.helper.cache;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.RandomAccess;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ExternalSet</p>
 * <p>Description: Wrapper para usar java.util.List mantido externamente (atualmente usando Redis).</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 */
public class ExternalList<V> extends AbstractList<V> implements List<V>, RandomAccess, Cloneable, java.io.Serializable {
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final transient RedisTemplate<String, V> redisTemplateForCache;

    private final transient ListOperations<String, V> redisCache;

    private final String name;

    private String key;

    public ExternalList() {
        this(ExternalCacheHelper.getCallerName());
    }

    public ExternalList(String name) {
        this(name, true);
    }

    public ExternalList(String name, boolean resetCache) {
        this.name = name;
        key = ExternalCacheHelper.buildKey(name);
        redisTemplateForCache = ApplicationContextProvider.getApplicationContext().getBean("redisTemplateForCache", RedisTemplate.class);
        if (resetCache) {
            redisTemplateForCache.delete(key);
        }
        redisCache = redisTemplateForCache.opsForList();
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((key == null) ? 0 : key.hashCode());
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExternalList<V> other = (ExternalList<V>) obj;
        if (!Objects.equals(key, other.key)) {
            return false;
        }
        return Objects.equals(name, other.name);
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
        try {
            return redisCache.indexOf(key, (V) o) != null;
        } catch (final Exception ex) {
            return false;
        }
    }

    @Override
    public Iterator<V> iterator() {
        List<V> list = redisCache.range(key, 0, -1);
        if (list == null) {
            list = new ArrayList<>();
        }
        return Collections.unmodifiableList(list).iterator();
    }

    @Override
    public Object[] toArray() {
        List<V> list = redisCache.range(key, 0, -1);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        List<V> list = redisCache.range(key, 0, -1);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list.toArray(a);
    }

    @Override
    public boolean add(V e) {
        redisCache.rightPush(key, e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        redisCache.remove(key, 0, o);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        List<V> list = redisCache.range(key, 0, -1);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        for (final V i : c) {
            redisCache.rightPush(key, i);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        for (final V m : redisCache.range(key, 0, -1)) {
            if (!c.contains(m)) {
                redisCache.remove(key, 0, m);
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (final Object o : c) {
            redisCache.remove(key, 0, o);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends V> c) {
        final Object[] co = c.toArray();
        for (int i = c.size() - 1; i > 0; i--) {
            add(index, (V)co[i]);
        }
        return true;
    }

    @Override
    public V get(int index) {
        return redisCache.index(key, index);
    }

    @Override
    public V set(int index, V element) {
        final V previous = redisCache.index(key, index);
        redisCache.set(key, index, element);
        return previous;
    }

    @Override
    public void add(int index, V element) {
        final V pivot = redisCache.index(key, index);
        if (element != null) {
            if (pivot != null) {
                redisCache.leftPush(key, pivot, element);
            } else {
                // TODO verficar se está correto
                redisCache.leftPush(key, element);
            }
        }
    }

    @Override
    public V remove(int index) {
        final V element = redisCache.index(key, index);
        if (element != null) {
            redisCache.remove(key, 1, element);
        }
        return element;
    }

    @Override
    public int indexOf(Object o) {
        try {
            return redisCache.indexOf(key, (V) o).intValue();
        } catch (final Exception ex) {
            return -1;
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        try {
            return redisCache.lastIndexOf(key, (V) o).intValue();
        } catch (final Exception ex) {
            return -1;
        }
    }

    @Override
    public ListIterator<V> listIterator() {
        List<V> list = redisCache.range(key, 0, -1);
        if (list == null) {
            list = new ArrayList<>();
        }
        return Collections.unmodifiableList(list).listIterator();
    }

    @Override
    public ListIterator<V> listIterator(int index) {
        List<V> list = redisCache.range(key, index, -1);
        if (list == null) {
            list = new ArrayList<>();
        }
        return Collections.unmodifiableList(list).listIterator(index);
    }

    @Override
    public List<V> subList(int fromIndex, int toIndex) {
        return redisCache.range(key, fromIndex, toIndex);
    }
}