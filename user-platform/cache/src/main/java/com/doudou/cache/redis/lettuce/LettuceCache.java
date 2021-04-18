package com.doudou.cache.redis.lettuce;

import com.doudou.cache.AbstractCache;
import com.doudou.cache.ExpirableEntry;
import com.doudou.cache.processor.DefaultSerializeProvider;
import com.doudou.cache.processor.SerializeProvider;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.RedisCodec;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.Serializable;
import java.util.Set;

public class LettuceCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

    private final RedisClient redisClient;

    private final StatefulRedisConnection<K, V> connection;

    private final RedisCommands<K, V> commands;

    public LettuceCache(CacheManager cacheManager, String cacheName,
                        Configuration<K, V> configuration, RedisClient redisClient) {
        super(cacheManager, cacheName, configuration);
        this.redisClient = redisClient;
        RedisCodec<K, V> redisCodec = new DefaultRedisCodec<>();
        this.connection = redisClient.connect(redisCodec);
        this.commands = connection.sync();
    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        return commands.exists(key) > 0;
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        V value = commands.get(key);
        return ExpirableEntry.of(key, value);
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> entry) throws CacheException, ClassCastException {
        commands.set(entry.getKey(), entry.getValue());
    }

    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException {
        ExpirableEntry<K, V> entry = getEntry(key);
        commands.del(key);
        return entry;
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
        connection.close();
        redisClient.shutdown();
    }
}
