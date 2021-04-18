package com.doudou.cache.redis.jedis;

import com.doudou.cache.AbstractCache;
import com.doudou.cache.ExpirableEntry;
import com.doudou.cache.processor.DefaultSerializeProvider;
import com.doudou.cache.processor.SerializeProvider;
import redis.clients.jedis.Jedis;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.*;
import java.util.Set;

public class JedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

    private final Jedis jedis;

    private final SerializeProvider<Serializable, byte[]> serializeProvider;

    public JedisCache(CacheManager cacheManager, String cacheName,
                      Configuration<K, V> configuration, Jedis jedis) {
        super(cacheManager, cacheName, configuration);
        this.jedis = jedis;
        this.serializeProvider = new DefaultSerializeProvider();
    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = serializeProvider.serialize(key);
        return jedis.exists(keyBytes);
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = serializeProvider.serialize(key);
        return getEntry(keyBytes);
    }

    protected ExpirableEntry<K, V> getEntry(byte[] keyBytes) throws CacheException, ClassCastException {
        byte[] valueBytes = jedis.get(keyBytes);
        K k = (K) serializeProvider.deserialize(keyBytes);
        V v = (V) serializeProvider.deserialize(valueBytes);
        return ExpirableEntry.of(k, v);
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> entry) throws CacheException, ClassCastException {
        byte[] keyBytes = serializeProvider.serialize(entry.getKey());
        byte[] valueBytes = serializeProvider.serialize(entry.getValue());
        jedis.set(keyBytes, valueBytes);
    }

    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = serializeProvider.serialize(key);
        ExpirableEntry<K, V> oldEntry = getEntry(keyBytes);
        jedis.del(keyBytes);
        return oldEntry;
    }

    @Override
    protected void clearEntries() throws CacheException {
        // TODO
    }


    @Override
    protected Set<K> keySet() {
        // TODO
        return null;
    }

    @Override
    protected void doClose() {
        this.jedis.close();
    }
}
