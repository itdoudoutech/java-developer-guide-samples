package com.doudou.cache.redis.lettuce;

import com.doudou.cache.AbstractCacheManager;
import io.lettuce.core.RedisClient;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

/**
 * {@link javax.cache.CacheManager} based on Lettuce
 */
public class LettuceCacheManager extends AbstractCacheManager {

    private final RedisClient client;

    public LettuceCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
        client = RedisClient.create("redis://" + uri.getAuthority());
    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        return new LettuceCache(this, cacheName, configuration, client);
    }

    @Override
    protected void doClose() {
        client.shutdown();
    }
}
