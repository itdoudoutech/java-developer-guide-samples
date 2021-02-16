package com.doudou.jvm.classloader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class CustomClassLoader extends ClassLoader {

    public static void main(String[] args) throws Exception {
        CustomClassLoader loader = new CustomClassLoader();
        Class<?> clazz = loader.loadClass("Hello.xlass");

        Object obj = clazz.newInstance();
        Object result = clazz.getDeclaredMethod("hello").invoke(obj);

        System.out.println(result);

        // 默认父加载器 可以从 ClassLoader 的无参构造器得到答案
        System.out.println(CustomClassLoader.getSystemClassLoader());
        System.out.println(loader.getParent());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = loadClassFromFile(name);
        return defineClass("Hello", bytes, 0, bytes.length);
    }

    private static byte[] loadClassFromFile(String name) {
        try {
            InputStream in = ClassLoader.getSystemResourceAsStream(name);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int nextValue = 0;
            while ((nextValue = in.read()) != -1) {
                byteStream.write(255 - nextValue);
            }
            return byteStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
