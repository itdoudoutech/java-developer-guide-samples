package com.doudou.cache.processor;

import javax.cache.CacheException;

/**
 * 序列化 & 反序列化
 *
 * @param <S> 原始数据类型
 * @param <T> 目标数据类型
 */
public interface SerializeProvider<S, T> {

    /**
     * 序列化
     *
     * @param source
     * @return
     */
    T serialize(S source) throws CacheException;

    /**
     * 反序列化
     *
     * @param target
     * @return
     */
    S deserialize(T target) throws CacheException;
}
