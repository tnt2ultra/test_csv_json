package org.example;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapOfMapSerializerTest {

    @Test
    void testSerializationDeserialization() {
        // создаем тестовые данные
        Map<String, Map<String, Integer>> originalMap = new HashMap<>();

        Map<String, Integer> innerMap1 = new HashMap<>();
        innerMap1.put("key1", 1);
        innerMap1.put("key2", 2);

        Map<String, Integer> innerMap2 = new HashMap<>();
        innerMap2.put("key3", 3);
        innerMap2.put("key4", 4);

        originalMap.put("map1", innerMap1);
        originalMap.put("map2", innerMap2);

        // сериализуем данные
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MapOfMapSerializer.serialize(buffer, originalMap);

        // десериализуем данные
        Map<String, Map<String, Integer>> deserializedMap = MapOfMapSerializer.deserialize(buffer);

        // сравниваем полученный результат с ожидаемым
        assertEquals(originalMap, deserializedMap);
    }

    @Test
    public void testCalculateBufferSize() {
        Map<String, Map<String, Integer>> map = new HashMap<>(); // 4
        Map<String, Integer> subMap1 = new HashMap<>(); // 4
        subMap1.put("key-1", 1); // 4 + 4 = 8
        subMap1.put("key-2", 2); // 4 + 4 = 8
        Map<String, Integer> subMap2 = new HashMap<>(); // 4
        subMap2.put("key-3", 3); // 4 + 4 = 8
        map.put("subMap1", subMap1); // 7 + 4 = 11
        map.put("subMap2", subMap2); // 7 + 4 = 11
        int expectedSize = 4 + (4 + 7 + 4 + (4 + 5 + 4) * 2) + (4 + 7 + 4 + (4 + 5 + 4));
        assertEquals(expectedSize, MapOfMapSerializer.calculateBufferSize(map));
    }

    @Test
    public void testMergeMaps() {
        // создаем тестовые данные
        Map<String, Map<String, Integer>> mapOfMap = new HashMap<>();
        Map<String, Integer> innerMap1 = new HashMap<>();
        innerMap1.put("key1", 1);
        innerMap1.put("key2", 2);
        mapOfMap.put("map1", innerMap1);
        Map<String, Integer> innerMap2 = new HashMap<>();
        innerMap2.put("key1", 3);
        innerMap2.put("key2", 4);
        mapOfMap.put("map2", innerMap2);

        ByteBuffer buffer = MapOfMapSerializer.serialize(mapOfMap);

        // создаем новые тестовые данные
        Map<String, Map<String, Integer>> newMapOfMap = new HashMap<>();
        Map<String, Integer> newInnerMap1 = new HashMap<>();
        newInnerMap1.put("key2", 2);
        newInnerMap1.put("key3", 3);
        newMapOfMap.put("map1", newInnerMap1);
        Map<String, Integer> newInnerMap2 = new HashMap<>();
        newInnerMap2.put("key2", 4);
        newInnerMap2.put("key4", 5);
        newMapOfMap.put("map2", newInnerMap2);

        // объединяем две мапы
        MapOfMapSerializer.mergeMaps(newMapOfMap, buffer);

        // проверяем, что объединение прошло успешно
        Map<String, Map<String, Integer>> expectedMapOfMap = new HashMap<>();
        Map<String, Integer> expectedInnerMap1 = new HashMap<>();
        expectedInnerMap1.put("key1", 1);
        expectedInnerMap1.put("key2", 4);
        expectedInnerMap1.put("key3", 3);
        expectedMapOfMap.put("map1", expectedInnerMap1);
        Map<String, Integer> expectedInnerMap2 = new HashMap<>();
        expectedInnerMap2.put("key1", 3);
        expectedInnerMap2.put("key2", 8);
        expectedInnerMap2.put("key4", 5);
        expectedMapOfMap.put("map2", expectedInnerMap2);

        assertEquals(expectedMapOfMap, newMapOfMap);
    }

}
