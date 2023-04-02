package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileProcessorTest {

    private String testFilePathCsv;
    private String testFilePathJson;
    private String nonExistentFilePath;

    @BeforeEach
    void setUp() {
        System.out.println("-------------");
        testFilePathCsv = "src/test/resources/test.csv";
        testFilePathJson = "src/test/resources/test.json";
        nonExistentFilePath = "nonexistent_file.txt";
    }

    @Test
    void testProcessFileCsv_Successful() throws IOException {
        boolean expectedContent = true;
        boolean actualContent = FileProcessor.processFile(testFilePathCsv);
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void testProcessFileJson_Successful() throws IOException {
        boolean expectedContent = true;
        boolean actualContent = FileProcessor.processFile(testFilePathJson);
        assertEquals(expectedContent, actualContent);
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

}
