package org.example;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import com.google.common.util.concurrent.RateLimiter;

public class CalculateFactorialApp {
    int maxCalculationsForSecond = 100;
    String inputFileName = "src/main/resources/input.txt";
    String outputFileName = "src/main/resources/output.txt";
    int numberOfThreads;
    public CalculateFactorialApp(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public void proccessFactorial() throws InterruptedException {
        LinkedBlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        NumbersProducer producer = new NumbersProducer(inputQueue, inputFileName);
        Thread readingThread = new Thread(producer);
        readingThread.start();

        System.out.println("inputQueue = " + inputQueue);

        ScheduledExecutorService executorService1 = Executors.newScheduledThreadPool(numberOfThreads);

        processTasks(executorService1, inputQueue, outputQueue, countDownLatch);

        executorService1.shutdown();
        countDownLatch.await();

        System.out.println("result: " + outputQueue);

        SolutionsConsumer consumer = new SolutionsConsumer(outputQueue, outputFileName);
        Thread writingThread = new Thread(consumer);
        writingThread.start();
    }

    public void processTasks(ScheduledExecutorService executorService,
                             LinkedBlockingQueue<String> inputQueue,
                             LinkedBlockingQueue<String> outputQueue,
                             CountDownLatch countDownLatch) {
        RateLimiter rateLimiter = RateLimiter.create(maxCalculationsForSecond);
        LinkedBlockingQueue<String> tempQueue = new LinkedBlockingQueue<>();

        for (int i = 0; i < numberOfThreads; i++) {
            String line = inputQueue.poll();
            if (line == null) continue;

            String threadName = "thread" + i;
            executorService.submit(() -> {
                rateLimiter.acquire();
                String result = new FactorialThread(threadName, line, outputQueue, countDownLatch).call();
                tempQueue.offer(result);
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!tempQueue.isEmpty()) {
            outputQueue.offer(tempQueue.poll());
        }
    }

}

class FactorialThread implements Callable<String> {
    String threadName;
    String input;
    LinkedBlockingQueue<String> outputQueue;
    CountDownLatch countDownLatch;

    FactorialThread(String threadName, String input, LinkedBlockingQueue<String> outputQueue, CountDownLatch countDownLatch) {
        this.input = input;
        this.outputQueue = outputQueue;
        this.countDownLatch = countDownLatch;
        this.threadName = threadName;
    }

    @Override
    public String call() {
        String currrentResult = calculateFactorial(this.input);
        this.countDownLatch.countDown();
        return currrentResult;
    }

    private String calculateFactorial(String line) {
        System.out.println(System.currentTimeMillis());
        String result;
        try {
            Integer currentInt = Integer.parseInt(line);
            int intResult = 1;
            if (currentInt > 1) {
                for (int i = 1; i <= currentInt; i++) {
                    intResult = intResult * i;
                }
            }
            result = currentInt + " = " + intResult;
        } catch (Exception e) {
            throw new NumberFormatException("invalid input");
        }
        return result;
    }
}

class NumbersProducer implements Runnable{

    BlockingQueue<String> queueFromFile;
    String inputFilePath;

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

class SolutionsConsumer implements Runnable {

    BlockingQueue<String> queueToFile;
    String outputFileName;

    public SolutionsConsumer(BlockingQueue<String> queueToFile, String outputFileName) {
        this.queueToFile = queueToFile;
        this.outputFileName = outputFileName;
    }

    @Override
    public void run() {
        try {
            File outputFile = new File(outputFileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            String line;
            while ((line = queueToFile.poll()) != null) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}