package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CalculateFactorialApp {
    int numberOfThreads;
    public CalculateFactorialApp(int numberOfThreads) {
       this.numberOfThreads = numberOfThreads;
    }


    public void proccessFactorial() throws InterruptedException {
        LinkedBlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
        inputQueue.offer("1");
        inputQueue.offer("2");
        inputQueue.offer("3");
        inputQueue.offer("4");
        inputQueue.offer("5");
        inputQueue.offer("6");
        inputQueue.offer("7");
        inputQueue.offer("8");
        inputQueue.offer("9");
        inputQueue.offer("10");

        System.out.println("inputQueue = " + inputQueue);

        List<Future<String>> list = new ArrayList<Future<String>>();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            String line = inputQueue.poll();
            Future<String> future = executorService.submit(new FactorialThread("thread" + i, line, outputQueue, countDownLatch));
            list.add(future);
        }

        executorService.shutdown();
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
