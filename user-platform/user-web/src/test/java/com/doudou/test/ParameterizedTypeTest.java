package com.doudou.test;

import org.eclipse.microprofile.config.spi.Converter;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ParameterizedTypeTest {

    public static void main(String[] args) {

        Type classType = IntegerConverter.class.getGenericSuperclass();
        System.out.println("classType is: " + classType);
        if (ParameterizedType.class.isAssignableFrom(classType.getClass())) {
            ParameterizedType parameterizedType = (ParameterizedType) classType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            System.out.println("IntegerConverter 父类范型类型为：" + actualTypeArguments[0].getTypeName());
        }


        Type[] interfaceTypes = IntegerConverter.class.getGenericInterfaces();
        for (Type interfaceType : interfaceTypes) {
            System.out.println("interfaceType is: " + interfaceType);
            if (ParameterizedType.class.isAssignableFrom(interfaceType.getClass())) {
                ParameterizedType parameterizedType = (ParameterizedType) interfaceType;
                if (parameterizedType.getRawType().getTypeName().equals(Converter.class.getName())) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    System.out.println("IntegerConverter 具体范型类型为：" + actualTypeArguments[0].getTypeName());
                }
                System.out.println("###");
            }
        }

        fun(String.class);
    }

    private static void fun(Class<?> type){
        System.out.println("### "+type.getTypeName());
    }

    static class IntegerConverter extends ArrayList<String> implements Converter<Integer>, Comparable<String> {
        @Override
        public Integer convert(String value) throws IllegalArgumentException, NullPointerException {
            return 1;
        }

        @Override
        public int compareTo(String o) {
            return 0;
        }
    }
}
