package org.example;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CalculateFactorialAppTest {

    @Test
    void testProcessTasksWithFileIOAndRateLimiter() throws InterruptedException, IOException, ExecutionException {
        int numberOfThreads = 5;
        int maxCalculationsForSecond = 3;

        String inputFileName = "src/main/resources/input.txt";
        String outputFileName = "src/main/resources/output.txt";

        File inFile = new File(inputFileName);
        if (!inFile.exists()) {
            inFile.createNewFile();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(inputFileName));
        writer.write("5\n6\n7\n8\n9\n10");
        writer.close();

        long startTime = System.currentTimeMillis();

        CalculateFactorialApp app = new CalculateFactorialApp(numberOfThreads, maxCalculationsForSecond);
        app.proccessFactorial();

        long endTime = System.currentTimeMillis();

        File outFile = new File(outputFileName);
        if (!outFile.exists()) {
            outFile.createNewFile();
        }
        BufferedReader reader = new BufferedReader(new FileReader(outputFileName));
        int lineCount = 0;
        while (reader.readLine() != null) {
            lineCount++;
        }
        reader.close();

        assertEquals(6, lineCount);

        long expectedTime = (lineCount * 1000 / maxCalculationsForSecond) - 50;
        System.out.println("expectedTime: " + expectedTime);
        System.out.println("actualTime: " + (endTime - startTime));

        assertTrue(endTime - startTime >= expectedTime - 300, "RateLimiter should work");
    }
}
