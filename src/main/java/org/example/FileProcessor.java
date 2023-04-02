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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FileProcessor {
    static final int MAX_FILE_SIZE = 320 * 1024 * 1024;
    static final boolean DEBUG_FLAG = false;
    static Map<String, Map<String, Integer>> duplicates = new HashMap<>(10);
    static List<String> uniqueStrings = new LinkedList<>();
    static List<Integer> counts = new LinkedList<>();
    static Map<String, BigInteger> groupWeights = new HashMap<>();
    static long maxWeight;
    static long minWeight;
    static File file;
    static String fileExtension;
    static CSVParser parser;
    static String group;
    static String type;
    static long weight;
    static final Gson gson = new GsonBuilder().create();
    static Map<String, Integer> groupDuplicates;
    static JsonReader reader;
    static MyObject object;
    static int count;
    static int countDone;
    static long fileSize;

    public static boolean processFile(String path) throws IOException {
        clear();
        file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.err.println("File not found!");
            return false;
        }
        fileSize = file.length();
        System.out.println("File size " + fileSize + " bytes. Using " + ((fileSize < MAX_FILE_SIZE) ? "Map" : "List"));
        fileExtension = getExtension(path); // Получение расширения файла
        printFreeMemory(true);
        switch (fileExtension) {
            case "csv":
                processCsv(file);
                printStatistics();
                return true;
            case "json":
                processJson(file);
                printStatistics();
                return true;
            default:
                System.err.println("Incorrect file format!");
                return false;
        }
    }

    public static String getExtension(String path) {
        fileExtension = "";
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = path.substring(dotIndex + 1);
        }
        return fileExtension;
    }

    private static void clear() {
        duplicates.clear();
        uniqueStrings.clear();
        counts.clear();
        groupWeights.clear();
        maxWeight = Long.MIN_VALUE;
        minWeight = Long.MAX_VALUE;
        count = 0;
        countDone = 0;
        fileSize = 0;
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
        if (fileSize < MAX_FILE_SIZE) {
            if (!duplicates.containsKey(group)) {
                duplicates.put(group, new HashMap<>());
            }
            groupDuplicates = duplicates.get(group);
            if (groupDuplicates.containsKey(objectType)) {
                int count = groupDuplicates.get(objectType);
                groupDuplicates.put(objectType, count + 1);
            } else {
                groupDuplicates.put(objectType, 1);
            }
        } else {
            if (!uniqueStrings.contains(objectType)) {
                uniqueStrings.add(objectType);
                counts.add(1);
            } else {
                int index = uniqueStrings.indexOf(objectType);
                int count = counts.get(index);
                counts.set(index, count + 1);
            }
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
        printFreeMemory(false);
    }

    public static void printFreeMemory(boolean flag) {
        if (DEBUG_FLAG) {
            count++;
            if (count == 100000) {
                countDone += count;
                System.out.print(countDone + ": ");
                count = 0;
                flag = true;
            }
            if (flag) {
                long freeMemory = Runtime.getRuntime().freeMemory();
                System.out.println("Free memory in the heap: " + freeMemory + " bytes");
                System.gc();
                System.gc();
            }
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

    private static void printStatistics() {
        printFreeMemory(true);
        System.out.println("Duplicates:");
        if (fileSize < MAX_FILE_SIZE) {
            for (String group : duplicates.keySet()) {
                for (String objectType : duplicates.get(group).keySet()) {
                    int count = duplicates.get(group).get(objectType);
                    if (count > 1) {
                        System.out.println(objectType + ": " + count);
                    }
                }
            }
        } else {
            for (int i = 0; i < uniqueStrings.size(); i++) {
                Integer count = counts.get(i);
                if (count > 1) {
                    System.out.println(uniqueStrings.get(i) + ": " + count);
                }
            }
        }

        System.out.println("Total weight of objects by groups:");
        for (String group : groupWeights.keySet()) {
            System.out.println(group + ": " + groupWeights.get(group));
        }

        System.out.println("Maximum weight of items: " + maxWeight);
        System.out.println("Minimum weight of items: " + minWeight);

        printFreeMemory(true);
    }

}