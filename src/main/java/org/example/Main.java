package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.example.FileProcessor.printFreeMemory;

public class Main {
    public static void main(String[] args) {
        try {
            printFreeMemory(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input;
            do {
                System.out.print("Enter the path to the file or the command 'exit': ");
                input = reader.readLine();
                if (!input.equals("exit")) {
                    try {
                        FileProcessor.processFile(input);
                    } catch (IOException e) {
                        System.err.println("Error processing the file: " + e.getMessage());
                    }
                }
            } while (!input.equals("exit"));
        } catch (Exception e) {
            System.err.println("Error while reading: " + e.getMessage());
        }
    }
}