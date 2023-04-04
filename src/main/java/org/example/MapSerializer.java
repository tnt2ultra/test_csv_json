package org.example;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class MapSerializer {

    public static ByteBuffer serializeMap(Map<String, Integer> map) {
        // Вычисляем размер буфера
        int bufferSize = 4; // Размер int для хранения размера мапы
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            bufferSize += 4; // Размер int для хранения длины ключа
            bufferSize += entry.getKey().getBytes().length; // Длина ключа
            bufferSize += 4; // Размер int для хранения значения
        }

        // Создаем буфер и записываем размер мапы
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.putInt(map.size());

        // Записываем каждую запись из мапы в буфер
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            byte[] keyBytes = entry.getKey().getBytes();
            int value = entry.getValue();

            // Записываем длину ключа и сам ключ
            buffer.putInt(keyBytes.length);
            buffer.put(keyBytes);

            // Записываем значение
            buffer.putInt(value);
        }

        // Возвращаем буфер
        buffer.flip();
        return buffer;
    }

    public static Map<String, Integer> deserializeMap(ByteBuffer buffer) {
        // Получаем размер мапы из буфера
        int mapSize = buffer.getInt();

        // Создаем новую мапу и заполняем ее данными из буфера
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < mapSize; i++) {
            int keyLength = buffer.getInt();
            byte[] keyBytes = new byte[keyLength];
            buffer.get(keyBytes);
            String key = new String(keyBytes);

            int value = buffer.getInt();

            map.put(key, value);
        }

        // Возвращаем мапу
        return map;
    }

    public static void mergeMaps(Map<String, Integer> map1, ByteBuffer buffer) {
        Map<String, Integer> map2 = deserializeMap(buffer);
        for (Map.Entry<String, Integer> entry : map2.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            map1.merge(key, value, Integer::sum);
        }
    }

}
