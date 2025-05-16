package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class SolutionsConsumer implements Runnable {
    private BlockingQueue<String> queueToFile;
    private String outputFileName;

    public SolutionsConsumer(BlockingQueue<String> queueToFile, String outputFileName) {
        this.queueToFile = queueToFile;
        this.outputFileName = outputFileName;
    }

    @Override
    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFileName)))) {
            while (!queueToFile.isEmpty() && !Thread.currentThread().isInterrupted()) {
                String line = queueToFile.take();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

