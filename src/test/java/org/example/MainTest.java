package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Test
    public void testMainMethod() {
        String input = "path/to/test/file.txt\nexit\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Main.main(new String[]{});
        String expectedOutput = "Enter the path to the file or the command 'exit': "
                // here goes expected output for the given input
                + "Enter the path to the file or the command 'exit': ";
        assertEquals(expectedOutput, outContent.toString());
    }

}
