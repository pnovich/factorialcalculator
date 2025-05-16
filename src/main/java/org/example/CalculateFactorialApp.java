package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class CalculateFactorialApp {
    int maxCalculationsForSecond = 100;
    String inputFileName = "src/main/resources/input.txt";
    String outputFileName = "src/main/resources/output.txt";
    int numberOfThreads;
    public CalculateFactorialApp(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

//    public static void main(String[] args) throws InterruptedException {
//        int n = 10;
//        CalculateFactorialApp app = new CalculateFactorialApp(n);
//        app.proccessFactorial();
//    }


    public void proccessFactorial() throws InterruptedException {
        LinkedBlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
//        inputQueue.offer("1");
//        inputQueue.offer("2");
//        inputQueue.offer("3");
//        inputQueue.offer("4");
//        inputQueue.offer("5");
//        inputQueue.offer("6");
//        inputQueue.offer("7");
//        inputQueue.offer("8");
//        inputQueue.offer("9");
//        inputQueue.offer("10");

        NumbersProducer producer = new NumbersProducer(inputQueue, inputFileName);
        Thread readingThread = new Thread(producer);
        readingThread.start();

        System.out.println("inputQueue = " + inputQueue);

        List<Future<String>> list = new ArrayList<Future<String>>();

//        ExecutorService executorService =
//                Executors.newFixedThreadPool(numberOfThreads);
//        for (int i = 0; i < numberOfThreads; i++) {
//            String line = inputQueue.poll();
//            Future<String> future = executorService.submit(new FactorialThread("thread" + i, line, outputQueue, countDownLatch));
//            list.add(future);
//        }

        ScheduledExecutorService executorService1 = Executors.newScheduledThreadPool(numberOfThreads);
        long delay = getCorrectTimeout(numberOfThreads, maxCalculationsForSecond);
        for (int i = 0; i < numberOfThreads; i++) {
            String line = inputQueue.poll();
            Future<String> future = executorService1.schedule(
                    new FactorialThread("thread" + i, line, outputQueue, countDownLatch),
                    delay,
                    TimeUnit.MILLISECONDS
            );
//                    executorService.submit(new FactorialThread("thread" + i, lineCounter, line, outputQueue, countDownLatch, timeCounter));
            list.add(future);
        }


//        executorService.shutdown();
        executorService1.shutdown();
        countDownLatch.await();
        for(Future<String> fut : list){
            try {
                String currentOutput = fut.get();
                outputQueue.offer(currentOutput);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("result: " + outputQueue);
        SolutionsConsumer consumer = new SolutionsConsumer(outputQueue, outputFileName);
        Thread writingThread = new Thread(consumer);
        writingThread.start();

    }

    public static int getCorrectTimeout(int threadsNumber, int maxCalculationsForSecond) {
        int result = maxCalculationsForSecond / threadsNumber;
        return result;
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
//            InputStreamReader stream = new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(inputFilePath)));
            File inputFile = new File(inputFilePath);
//            BufferedReader reader =
//                    new BufferedReader(stream);
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = reader.readLine()) != null) {
                queueFromFile.offer(line);
            }
            reader.close();
//            stream.close();

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