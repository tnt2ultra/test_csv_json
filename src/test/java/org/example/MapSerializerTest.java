package org.example;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapSerializerTest {

    @Test
    public void testMergeMaps() {
        // Создаем две мапы для объединения
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("key1", 1);
        map1.put("key2", 2);
        map1.put("key3", 3);

        Map<String, Integer> map2 = new HashMap<>();
        map2.put("key2", 5);
        map2.put("key3", 7);
        map2.put("key4", 9);

        // Сериализуем первую мапу
        ByteBuffer buffer1 = MapSerializer.serializeMap(map1);

        // Сериализуем вторую мапу
        ByteBuffer buffer2 = MapSerializer.serializeMap(map2);

        // Объединяем мапы
        Map<String, Integer> result = new HashMap<>();
        MapSerializer.mergeMaps(result, buffer1);
        MapSerializer.mergeMaps(result, buffer2);

        // Проверяем, что значения объединились правильно
        assertEquals(1, result.get("key1"));
        assertEquals(7, result.get("key2"));
        assertEquals(10, result.get("key3"));
        assertEquals(9, result.get("key4"));
    }
}
