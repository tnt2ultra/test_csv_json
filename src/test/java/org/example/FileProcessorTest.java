package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileProcessorTest {

    private String testFilePathCsv;
    private String testFilePathJson;
    private String nonExistentFilePath;
    private String testFilePathCsv_10_000_000;
    private String testFilePathJson_10_000_000;

    @BeforeEach
    void setUp() {
        System.out.println("-------------");
        testFilePathCsv = "src/test/resources/test.csv";
        testFilePathJson = "src/test/resources/test.json";
        nonExistentFilePath = "nonexistent_file.txt";
        testFilePathCsv_10_000_000 = "src/test/resources/data.csv";
        testFilePathJson_10_000_000 = "src/test/resources/data.json";
    }

    @Test
    void testProcessFileCsv_Successful() throws IOException {
        boolean expectedContent = true;
        boolean actualContent = FileProcessor.processFile(testFilePathCsv);
        assertEquals(expectedContent, actualContent);
        assertEquals("[Duplicates:, group3-type1: 3, group3-type2: 3, group2-type2: 2, group1-type1: 2, " +
                "Total weight of objects by groups:, group3: 330, group2: 110, group1: 40, " +
                "Maximum weight of items: 60, Minimum weight of items: 10]", FileProcessor.listStatistics.toString());
    }

    @Test
    void testProcessFileJson_Successful() throws IOException {
        boolean expectedContent = true;
        boolean actualContent = FileProcessor.processFile(testFilePathJson);
        assertEquals(expectedContent, actualContent);
        assertEquals("[Duplicates:, group2-type3: 3, group1-type2: 2, Total weight of objects by groups:, " +
                        "group2: 90, group1: 50, Maximum weight of items: 30, Minimum weight of items: 10]",
                FileProcessor.listStatistics.toString());
    }

    @Test
    void testProcessFile_NonExistentFile() throws IOException {
        boolean expectedContent = false;
        boolean actualContent = FileProcessor.processFile(nonExistentFilePath);
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void testGetExtension() {
        String testFilePath = "src/test/resources/test_output_file.csv";
        String expectedExtension = FileProcessor.getExtension(testFilePath);
        assertEquals("csv", expectedExtension);

        testFilePath = "src/test/resources/test_output_file.json";
        expectedExtension = FileProcessor.getExtension(testFilePath);
        assertEquals("json", expectedExtension);
    }

    @Test
    void testCsv() throws IOException {
        File file = new File(testFilePathCsv_10_000_000);
        if(!file.exists()) {
            CreateCsv_10_000_000(testFilePathCsv_10_000_000);
        }
        boolean expectedContent = true;
        boolean actualContent = FileProcessor.processFile(testFilePathCsv_10_000_000);
        assertEquals(expectedContent, actualContent);
        assertEquals("[Duplicates:, A-B: 10000000, Total weight of objects by groups:, " +
                "A: 90000000000000000000000000, Maximum weight of items: 9000000000000000000, " +
                "Minimum weight of items: 9000000000000000000]", FileProcessor.listStatistics.toString());
    }

    public static void CreateCsv_10_000_000(String pathName) {
        String[] headers = {"group", "type", "number", "weight"};
        String[] data = {"A", "B", "1", "9000000000000000000"};

        try {
            File file = new File(pathName);
            FileWriter csvWriter = new FileWriter(file);

            // Записываем заголовки в csv-файл
            for (int i = 0; i < headers.length; i++) {
                csvWriter.append(headers[i]);
                if (i != headers.length - 1) {
                    csvWriter.append(",");
                }
            }
            csvWriter.append("\r\n");

            // Записываем данные в csv-файл
            for (int j = 0; j < 10_000_000; j++) {
                for (int i = 0; i < data.length; i++) {
                    csvWriter.append(data[i]);
                    if (i != data.length - 1) {
                        csvWriter.append(",");
                    }
                }
                csvWriter.append("\r\n");
            }

            csvWriter.flush();
            csvWriter.close();

            System.out.println("The CSV file was successfully created and filled with data.");

        } catch (IOException e) {
            System.out.println("Error when creating a CSV file: " + e.getMessage());
        }
    }

    @Test
    void testJson() throws IOException {
        File file = new File(testFilePathJson_10_000_000);
        if(!file.exists()) {
            CreateJson_10_000_000(testFilePathJson_10_000_000);
        }
        boolean expectedContent = true;
        boolean actualContent = FileProcessor.processFile(testFilePathJson_10_000_000);
        assertEquals(expectedContent, actualContent);
        assertEquals("[Duplicates:, A-B: 10000000, Total weight of objects by groups:, " +
                "A: 90000000000000000000000000, Maximum weight of items: 9000000000000000000, " +
                "Minimum weight of items: 9000000000000000000]", FileProcessor.listStatistics.toString());
    }

    public static void CreateJson_10_000_000(String pathName) {
        String[] headers = {"group", "type", "number", "weight"};
        String[] data = {"A", "B", "1", "9000000000000000000"};

        try {
            File file = new File(pathName);
            FileWriter jsonWriter = new FileWriter(file);

            // Записываем данные в json-файл
            jsonWriter.append("[\r\n");
            for (int j = 0; j < 10_000_000; j++) {
                jsonWriter.append("{");
                for (int i = 0; i < data.length; i++) {
                    jsonWriter.append("\"");
                    jsonWriter.append(headers[i]);
                    jsonWriter.append("\":\"");
                    jsonWriter.append(data[i]);
                    jsonWriter.append("\"");
                    if (i != data.length - 1) {
                        jsonWriter.append(",");
                    }
                }
                jsonWriter.append("}");
                if(j != 10_000_000 - 1) {
                    jsonWriter.append(",");
                }
                jsonWriter.append("\r\n");
            }
            jsonWriter.append("]");

            jsonWriter.flush();
            jsonWriter.close();

            System.out.println("The json file was successfully created and filled with data.");

        } catch (IOException e) {
            System.out.println("Error when creating a json file: " + e.getMessage());
        }
    }

}
