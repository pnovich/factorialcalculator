package org.example;

import java.io.*;
import java.util.concurrent.BlockingQueue;

public class NumbersProducer implements Runnable {

    private BlockingQueue<String> queueFromFile;
    private String inputFilePath;

    public NumbersProducer(BlockingQueue<String> queueFromFile, String inputFilePath) {
        this.queueFromFile = queueFromFile;
        this.inputFilePath = inputFilePath;
    }

    @Override
    public void run() {

        try {
            File inputFile = new File(inputFilePath);
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = reader.readLine()) != null) {
                queueFromFile.offer(line);
            }
            reader.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

