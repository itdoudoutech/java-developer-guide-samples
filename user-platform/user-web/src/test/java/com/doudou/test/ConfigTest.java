package com.doudou.test;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class ConfigTest {

    public static void main(String[] args) {
        System.out.println("### read system properties ...\n");
        for (String propertyName : System.getProperties().stringPropertyNames()) {
            System.out.println(propertyName + " : " + System.getProperty(propertyName));
            // System.out.println(propertyName + " > " + System.getProperties().getProperty(propertyName));
        }

        System.out.println("\n### read os properties ...\n");
        System.getenv().forEach((k, v) -> System.out.println(k + " : " + v));
        System.out.println("JAVA_HOME is > " + System.getenv("JAVA_HOME"));

        testLambda();

    }

    private static void testLambda() {
        String value = null;
        String first = Stream.of("true", "1", "YES", "Y", "ON").filter(p -> {
            System.out.println(p);
            return p.equals(value);
        }).findFirst().orElse(null);
        System.out.println(null == first);
    }
}
