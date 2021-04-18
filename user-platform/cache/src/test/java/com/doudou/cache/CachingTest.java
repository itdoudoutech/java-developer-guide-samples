/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doudou.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import com.doudou.cache.event.TestCacheEntryListener;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.net.URI;

import static com.doudou.cache.configuration.ConfigurationUtils.cacheEntryListenerConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * {@link Caching} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class CachingTest {

    private final static String IN_MEMORY_CACHE_URI = "in-memory://localhost/";
    private final static String REDIS_JEDIS_CACHE_URI = "redis-jedis://127.0.0.1:6379/";
    private final static String REDIS_LETTUCE_CACHE_URI = "redis-lettuce://127.0.0.1:6379/";

    @Test
    public void testSampleInMemory() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create(IN_MEMORY_CACHE_URI), null);
        // configure the cache
        MutableConfiguration<String, Integer> config =
                new MutableConfiguration<String, Integer>()
                        .setManagementEnabled(true)
                        .setTypes(String.class, Integer.class);

        // create the cache
        Cache<String, Integer> cache = cacheManager.createCache("simpleCache", config);

        // add listener
        cache.registerCacheEntryListener(cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));

        // cache operations
        String key = "key";
        Integer value1 = 1;
        cache.put(key, value1);

        // update
        value1 = 2;
        cache.put(key, value1);

        Integer value2 = cache.get(key);
        assertEquals(value1, value2);
        cache.remove(key);
        assertNull(cache.get(key));
    }

    @Test
    public void testSampleRedisByJedis() {
        baseTestRedis(REDIS_JEDIS_CACHE_URI);
    }

    @Test
    public void testSampleRedisByLettuce() {
        baseTestRedis(REDIS_LETTUCE_CACHE_URI);
    }

    private void baseTestRedis(String redis_lettuce_cache_uri) {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create(redis_lettuce_cache_uri), null);
        // configure the cache
        MutableConfiguration<String, Integer> config =
                new MutableConfiguration<String, Integer>()
                        .setTypes(String.class, Integer.class);

        // create the cache
        Cache<String, Integer> cache = cacheManager.createCache("redisCache", config);

        // add listener
        cache.registerCacheEntryListener(cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));

        // cache operations
        String key = "redis-key";
        Integer value1 = 1;
        cache.put(key, value1);
        assertEquals(value1, cache.get(key));

        // update
        value1 = 2;
        cache.put(key, value1);

        Integer value2 = cache.get(key);
        assertEquals(value1, value2);

        cache.remove(key);
        assertNull(cache.get(key));
    }

    @Test
    public void testJedis() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        try (Jedis jedis = pool.getResource()) {
            /// ... do stuff here ... for example
            jedis.set("foo", "123");
            String foobar = jedis.get("foo");
            System.out.println(foobar);
        }
/// ... when closing your application:
        pool.close();
    }

    @Test
    public void testLettuce() {
        // client
        RedisClient client = RedisClient.create("redis://127.0.0.1:6379");
        // connect
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> sync = connection.sync();
        String value = sync.set("name", "admin");
        System.out.println(value);
        System.out.println(sync.get("name"));
        connection.close();
        client.shutdown();

        String admin = JSON.toJSONString("admin");
        System.out.println(admin);
        JSONObject object = JSON.parseObject(admin);
        System.out.println(object.toString());
    }
}
