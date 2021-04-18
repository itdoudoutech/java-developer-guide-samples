package com.doudou.cache.processor;


import javax.cache.CacheException;
import java.io.*;

public class DefaultSerializeProvider implements SerializeProvider<Serializable, byte[]> {

    @Override
    public byte[] serialize(Serializable source) throws CacheException {
        byte[] bytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            // Key -> byte[]
            objectOutputStream.writeObject(source);
            objectOutputStream.flush();
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new CacheException(e);
        }
        return bytes;
    }

    @Override
    public Serializable deserialize(byte[] target) throws CacheException {
        if (target == null) {
            return null;
        }
        Serializable value;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(target);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            // byte[] -> Value
            value = (Serializable) objectInputStream.readObject();
        } catch (Exception e) {
            throw new CacheException(e);
        }
        return value;
    }
}
