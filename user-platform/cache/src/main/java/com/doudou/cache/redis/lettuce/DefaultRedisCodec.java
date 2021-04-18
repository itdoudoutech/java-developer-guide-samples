package com.doudou.cache.redis.lettuce;

import com.doudou.cache.processor.DefaultSerializeProvider;
import com.doudou.cache.processor.SerializeProvider;
import io.lettuce.core.codec.RedisCodec;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class DefaultRedisCodec<K, V> implements RedisCodec<K, V> {

    private final byte[] EMPTY = new byte[0];

    private final SerializeProvider<Serializable, byte[]> serializeProvider = new DefaultSerializeProvider();

    @Override
    public K decodeKey(ByteBuffer bytes) {
        return (K) deCode(bytes);
    }

    @Override
    public V decodeValue(ByteBuffer bytes) {
        return (V) deCode(bytes);
    }

    @Override
    public ByteBuffer encodeKey(K key) {
        return encode(key);
    }

    @Override
    public ByteBuffer encodeValue(V value) {
        return encode(value);
    }


    private Object deCode(ByteBuffer buffer) {
        if (null == buffer) {
            return null;
        }
        int remaining = buffer.remaining();
        if (remaining == 0) {
            return null;
        }
        byte[] bytes = new byte[remaining];
        buffer.get(bytes);
        return serializeProvider.deserialize(bytes);
    }

    private ByteBuffer encode(Object obj) {
        if (obj == null) {
            return ByteBuffer.wrap(EMPTY);
        }
        byte[] bytes = serializeProvider.serialize((Serializable) obj);
        return ByteBuffer.wrap(bytes);
    }

}
