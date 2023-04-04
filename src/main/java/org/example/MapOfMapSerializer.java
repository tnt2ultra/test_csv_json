package org.example;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class MapOfMapSerializer {

    public static void serialize(ByteBuffer buffer, Map<String, Map<String, Integer>> map) {
        buffer.putInt(map.size());
        for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
            writeString(buffer, entry.getKey());
            writeToBuffer(buffer, entry.getValue());
        }
    }

    public static ByteBuffer serialize(Map<String, Map<String, Integer>> map) {
        // Размер int для хранения размера мапы
        int bufferSize = calculateBufferSize(map);

        // Создаем буфер и записываем размер мапы
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.putInt(map.size());

        // Записываем каждую запись из мапы в буфер
        for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
            writeString(buffer, entry.getKey());
            writeToBuffer(buffer, entry.getValue());
        }

        return buffer;
    }

    public static Map<String, Map<String, Integer>> deserialize(ByteBuffer buffer) {
        buffer.flip();
        int mapSize = buffer.getInt();
        Map<String, Map<String, Integer>> map = new HashMap<>();
        for (int i = 0; i < mapSize; i++) {
            String key = readString(buffer);
            Map<String, Integer> value = readMapFromBuffer(buffer);
            map.put(key, value);
        }
        return map;
    }

    private static void writeString(ByteBuffer buffer, String string) {
        byte[] bytes = string.getBytes();
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }

    private static String readString(ByteBuffer buffer) {
        int length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes);
    }

    private static void writeToBuffer(ByteBuffer buffer, Map<String, Integer> map) {
        buffer.putInt(map.size());
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            writeString(buffer, entry.getKey());
            buffer.putInt(entry.getValue());
        }
    }

    private static Map<String, Integer> readMapFromBuffer(ByteBuffer buffer) {
        int mapSize = buffer.getInt();
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < mapSize; i++) {
            String key = readString(buffer);
            int value = buffer.getInt();
            map.put(key, value);
        }
        return map;
    }

    /**
     * Вычисляет размер необходимый для записи Map<String, Map<String, Integer>> в ByteBuffer.
     *
     * @param map Мапа, размер которой нужно вычислить
     * @return Размер, необходимый для записи в ByteBuffer
     */
    public static int calculateBufferSize(Map<String, Map<String, Integer>> map) {
        // Вычисляем размер буфера
        int bufferSize = 4; // Размер int для хранения размера мапы
        for (Map.Entry<String, Map<String, Integer>> mapInner : map.entrySet()) {
            bufferSize += 4; // Размер int для хранения длины ключа
            bufferSize += mapInner.getKey().getBytes().length; // Длина ключа
            bufferSize += 4; // добавляем количество ключей во вложенной мапе
            for (Map.Entry<String, Integer> entry : mapInner.getValue().entrySet()) {
                bufferSize += 4; // Размер int для хранения длины ключа
                bufferSize += entry.getKey().getBytes().length; // Длина ключа
                bufferSize += 4; // Размер int для хранения значения
            }
        }
        return bufferSize;
    }

    public static void mergeMaps(Map<String, Map<String, Integer>> mapOfMap, ByteBuffer buffer) {
        Map<String, Map<String, Integer>> mapTemp = deserialize(buffer);
        mergeMaps(mapOfMap, mapTemp);
    }

    public static void mergeMaps(Map<String, Map<String, Integer>> result, Map<String, Map<String, Integer>> map2) {
        // Обрабатываем ключи второй карты
        for (String key : map2.keySet()) {
            Map<String, Integer> innerMap = map2.get(key);
            MergeMap(result, key, innerMap);
        }
    }

    public static void MergeMap(Map<String, Map<String, Integer>> result, String key, Map<String, Integer> innerMap) {
        // Если ключ уже есть в результирующей карте, то суммируем значения
        if (result.containsKey(key)) {
            Map<String, Integer> existingMap = result.get(key);
            for (String innerKey : innerMap.keySet()) {
//                    int newValue = innerMap.get(innerKey) + existingMap.getOrDefault(innerKey, 0);
//                    existingMap.put(innerKey, newValue);
                existingMap.merge(innerKey, innerMap.get(innerKey), Integer::sum);
            }
        } else {
            // Если ключа нет в результирующей карте, то просто добавляем в нее
            result.put(key, innerMap);
        }
    }


}