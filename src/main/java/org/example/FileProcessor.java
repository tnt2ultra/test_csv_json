package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;

public class FileProcessor {
    static final int MAX_COUNT = 1_000_000;
    static Map<String, Map<String, Integer>> duplicates = new HashMap<>(10);
    static List<ByteBuffer> mapsByteBuffer = new LinkedList<>();
    static Map<String, BigInteger> groupWeights = new HashMap<>();
    static long maxWeight;
    static long minWeight;
    static CSVParser parser;
    static String group;
    static String type;
    static long weight;
    static final Gson gson = new GsonBuilder().create();
    static Map<String, Integer> groupDuplicates;
    static JsonReader reader;
    static MyObject object;
    static int countObjects;
    static List<String> listStatistics = new ArrayList<>();

    public static boolean processFile(String path) throws IOException {
        clear();
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.err.println("File not found!");
            return false;
        }
        switch (getExtension(path)) {
            case "csv":
                processCsv(file);
                break;
            case "json":
                processJson(file);
                break;
            default:
                System.err.println("Incorrect file format!");
                return false;
        }
        printStatistics();
        return true;
    }

    public static String getExtension(String path) {
        String fileExtension = "";
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = path.substring(dotIndex + 1);
        }
        return fileExtension;
    }

    private static void clear() {
        duplicates.clear();
        mapsByteBuffer.forEach(ByteBuffer::clear);
        mapsByteBuffer.clear();
        groupWeights.clear();
        maxWeight = Long.MIN_VALUE;
        minWeight = Long.MAX_VALUE;
        countObjects = 0;
        listStatistics.clear();
    }

    private static void processCsv(File file) throws IOException {
        parser = new CSVParser(new FileReader(file), CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build());
        for (CSVRecord record : parser) {
            group = record.get("group");
            type = record.get("type");
            weight = Long.parseLong(record.get("weight"));
            processOneObject(group, type, weight);
        }
        parser.close();
    }

    private static void processOneObject(String group, String type, long weight) {
        // Поиск дубликатов
        String objectType = group + "-" + type;
        groupDuplicates = duplicates.get(group);
        if (groupDuplicates == null) {
            groupDuplicates = new HashMap<>();
            groupDuplicates.put(objectType, 1);
            duplicates.put(group, groupDuplicates);
        } else {
            if (groupDuplicates.containsKey(objectType)) {
                int count = groupDuplicates.get(objectType);
                groupDuplicates.put(objectType, count + 1);
            } else {
                groupDuplicates.put(objectType, 1);
            }
        }
        countObjects++;
        if (countObjects == MAX_COUNT) {
            countObjects = 0;
            ByteBuffer buffer = MapOfMapSerializer.serialize(duplicates);
            mapsByteBuffer.add(buffer);
            duplicates.clear();
        }

        // Суммирование веса объектов по группам
        if (groupWeights.containsKey(group)) {
            BigInteger groupWeight = groupWeights.get(group);
            groupWeights.put(group, groupWeight.add(BigInteger.valueOf(weight)));
        } else {
            groupWeights.put(group, BigInteger.valueOf(weight));
        }

        // Поиск максимального и минимального веса объектов
        if (weight > maxWeight) {
            maxWeight = weight;
        }
        if (weight < minWeight) {
            minWeight = weight;
        }
    }

    public static void processJson(File file) throws IOException {
        reader = new JsonReader(new FileReader(file));
        reader.beginArray();
        while (reader.hasNext()) {
            object = gson.fromJson(reader, MyObject.class);
            group = object.getGroup();
            type = object.getType();
            weight = object.getWeight();
            processOneObject(group, type, weight);
        }
        reader.endArray();
        reader.close();
    }

    public static void printStatistics() {
        fillStatistics();
        listStatistics.forEach(System.out::println);
    }

    public static void fillStatistics() {
        listStatistics.add("Duplicates:");
        ByteBuffer bufferTemp = MapOfMapSerializer.serialize(duplicates);
        mapsByteBuffer.add(bufferTemp);
        duplicates.clear();
        for (ByteBuffer buffer : mapsByteBuffer) {
            Map<String, Map<String, Integer>> sourceMap = MapOfMapSerializer.deserialize(buffer);
            for (Map.Entry<String, Map<String, Integer>> entry : sourceMap.entrySet()) {
                String key = entry.getKey();
                Map<String, Integer> innerMap = entry.getValue();
                Map<String, Integer> newInnerMap = new HashMap<>();
                for (Map.Entry<String, Integer> innerEntry : innerMap.entrySet()) {
                    String innerKey = innerEntry.getKey();
                    Integer value = innerEntry.getValue();
                    if (value > 1) {
                        newInnerMap.put(innerKey, value);
                    }
                }
                if (!newInnerMap.isEmpty()) {
                    MapOfMapSerializer.MergeMap(duplicates, key, newInnerMap);
                }
            }
        }
        for (String group : duplicates.keySet()) {
            for (String objectType : duplicates.get(group).keySet()) {
                int count = duplicates.get(group).get(objectType);
                if (count > 1) {
                    listStatistics.add(objectType + ": " + count);
                }
            }
        }
        listStatistics.add("Total weight of objects by groups:");
        for (String group : groupWeights.keySet()) {
            listStatistics.add(group + ": " + groupWeights.get(group));
        }
        listStatistics.add("Maximum weight of items: " + maxWeight);
        listStatistics.add("Minimum weight of items: " + minWeight);
    }

}