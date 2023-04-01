package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        System.out.println("main started");
        try {
            FileProcessor fileProcessor = new FileProcessor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input;
            do {
                System.out.print("Введите путь к файлу или команду 'exit': ");
                input = reader.readLine();
                if (!input.equals("exit")) {
                    try {
                        fileProcessor.processFile(input);
                    } catch (IOException e) {
                        System.err.println("Ошибка при обработке файла: " + e.getMessage());
                    }
                }
            } while (!input.equals("exit"));
        } catch (Exception e) {
            System.err.println("Ошибка при чтении ввода: " + e.getMessage());
        }
    }
}