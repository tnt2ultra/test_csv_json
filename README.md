# test_csv_json

Task description

There are two files with a list of objects. One file is in json format, the other is in csv format. Each object has the following fields:

• “group” – object group (String) (the number of groups in the file is no more than 10)

• “type” – object type (String)

• “number" – object number (long)

• “weight" – the weight of the object (long)

You need to develop a console application. The application must process files in two formats csv and json.  After launching, the application waits for entering the path to the file or the "shutdown command" (entering a sequence of "exit" characters into the console). By the "shutdown command", the application shuts down its work. After entering the path to the file, the application generates summary statistics:

• Duplicate objects (objects with the same group (“group”) and type (“type”)) with the number of their repetitions.

• The total weight (“weight") of objects in each group (“group”).

• Maximum and minimum weights of objects in the file.

After the statistics are displayed on the screen, the application waits for entering the path to the file or the "shutdown command".

In the process of operation, the application should not fall, the output is only on the command to shut down.





Getting test data

• Files with a list of objects are created using the utility test-generator.jar .

• Files are created in UTF-8 encoding.

Launch Command:

java -jar test-generator.jar -file "out.csv" -format csv -count 10 000 000

• -file – output file

• -format – file format (json or csv)

• -count – number of objects



Execution conditions

• Using Java 8

• Gradle build system. 

• It is necessary to do only with the tools included in the Java SE package.

• Using JUnit (for tests), json and csv parsers from third-party libraries is acceptable. 

• The application must work with a heap size of no more than 512 MB (VM parameter –Xmx512m) and the number of objects in the file up to 10000000
