package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileProcessorTest {

    private FileProcessor fileProcessor;
    private String testFilePathCsv;
    private String testFilePathJson;
    private String nonExistentFilePath;

    @BeforeEach
    void setUp() {
        fileProcessor = new FileProcessor();
        testFilePathCsv = "src/test/resources/test.csv";
        testFilePathJson = "src/test/resources/test.json";
        nonExistentFilePath = "nonexistent_file.txt";
    }

    @Test
    void testProcessFile_Successful() throws IOException {
        boolean expectedContent = true;
        boolean actualContent = fileProcessor.processFile(testFilePathCsv);
        assertEquals(expectedContent, actualContent);
        actualContent = fileProcessor.processFile(testFilePathJson);
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void testProcessFile_NonExistentFile() throws IOException {
        boolean expectedContent = false;
        boolean actualContent = fileProcessor.processFile(nonExistentFilePath);
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void testGetExtension() {
        String testFilePath = "src/test/resources/test_output_file.txt";
        String expectedExtension = FileProcessor.getExtension(testFilePath);
        assertEquals("txt", expectedExtension);
    }

}
