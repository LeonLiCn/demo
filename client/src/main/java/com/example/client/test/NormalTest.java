package com.example.client.test;

import java.util.LinkedHashMap;
import java.util.Map;

public class NormalTest {

    public static void main(String[] args) {

        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("ccc", 10);
        map.put("zzz", 2);
        map.put("bbb", 5);
        map.put("aaa", 9);
        map.put("ttt", 7);

        map = sortByValue(map);
        map.keySet().forEach(System.out::println);
        System.out.println(Runtime.getRuntime().availableProcessors());

        ClassLoader loader = NormalTest.class.getClassLoader();
        while (loader != null) {
            System.out.println(loader.toString());
            loader = loader.getParent();
        }

    }

    private static  <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }



}
