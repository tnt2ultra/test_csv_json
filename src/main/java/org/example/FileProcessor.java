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
import java.util.HashMap;
import java.util.Map;

public class FileProcessor {
    private final Map<String, Map<String, Integer>> duplicates = new HashMap<>();
    private final Map<String, Long> groupWeights = new HashMap<>();
    private long maxWeight = Long.MIN_VALUE;
    private long minWeight = Long.MAX_VALUE;

    public boolean processFile(String path) throws IOException {
        clear();
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.err.println("Файл не найден!");
            return false;
        }
        String fileExtension = getExtension(path); // Получение расширения файла
        switch (fileExtension) {
            case "csv":
                processCsv(file);
                return true;
            case "json":
                processJson(file);
                return true;
            default:
                System.err.println("Некорректный формат файла!");
                return false;
        }
    }

    public static String getExtension(String path) {
        String fileExtension = "";
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = path.substring(dotIndex + 1);
        }
        System.out.println("file extension " + fileExtension);
        return fileExtension;
    }

    private void clear() {
        duplicates.clear();
        groupWeights.clear();
        maxWeight = Long.MIN_VALUE;
        minWeight = Long.MAX_VALUE;
    }

    private void processCsv(File file) throws IOException {
        CSVParser parser = new CSVParser(new FileReader(file), CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build());
        for (CSVRecord record : parser) {
            String group = record.get("group");
            String type = record.get("type");
            long weight = Long.parseLong(record.get("weight"));
            processOneObject(group, type, weight);
        }
        parser.close();
        printStatistics(); // Вывод результатов обработки файла
    }

    private void processOneObject(String group, String type, long weight) {
        // Поиск дубликатов
        if (!duplicates.containsKey(group)) {
            duplicates.put(group, new HashMap<>());
        }
        Map<String, Integer> groupDuplicates = duplicates.get(group);
        String objectType = group + "-" + type;
        if (groupDuplicates.containsKey(objectType)) {
            int count = groupDuplicates.get(objectType);
            groupDuplicates.put(objectType, count + 1);
        } else {
            groupDuplicates.put(objectType, 1);
        }

        // Суммирование веса объектов по группам
        if (groupWeights.containsKey(group)) {
            long groupWeight = groupWeights.get(group);
            groupWeights.put(group, groupWeight + weight);
        } else {
            groupWeights.put(group, weight);
        }

        // Поиск максимального и минимального веса объектов
        if (weight > maxWeight) {
            maxWeight = weight;
        }
        if (weight < minWeight) {
            minWeight = weight;
        }
    }

    public void processJson(File file) throws IOException {
        Gson gson = new GsonBuilder().create();
        JsonReader reader = new JsonReader(new FileReader(file));
        reader.beginArray();
        while (reader.hasNext()) {
            MyObject object = gson.fromJson(reader, MyObject.class);
            String group = object.getGroup();
            String type = object.getType();
            long weight = object.getWeight();
            processOneObject(group, type, weight);
        }
        reader.endArray();
        reader.close();
        printStatistics(); // Вывод результатов обработки файла
    }

    private void printStatistics() {
        System.out.println("Дубликаты:");
        for (String group : duplicates.keySet()) {
            for (String objectType : duplicates.get(group).keySet()) {
                int count = duplicates.get(group).get(objectType);
                if (count > 1) {
                    System.out.println(objectType + " " + count);
                }
            }
        }

        System.out.println("Суммарный вес объектов по группам:");
        for (String group : groupWeights.keySet()) {
            System.out.println(group + " " + groupWeights.get(group));
        }

        System.out.println("Максимальный вес объектов: " + maxWeight);
        System.out.println("Минимальный вес объектов: " + minWeight);
    }

}