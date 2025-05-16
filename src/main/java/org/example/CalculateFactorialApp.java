package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.google.common.util.concurrent.RateLimiter;

public class CalculateFactorialApp {
    private final int maxCalculationsForSecond;
    private final String inputFileName = "src/main/resources/input.txt";
    private final String outputFileName = "src/main/resources/output.txt";
    private final int numberOfThreads;

    public CalculateFactorialApp(int numberOfThreads, int maxCalculationsForSecond) {
        this.numberOfThreads = numberOfThreads;
        this.maxCalculationsForSecond = maxCalculationsForSecond;
    }

    public void proccessFactorial() throws InterruptedException, ExecutionException {
        LinkedBlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        NumbersProducer producer = new NumbersProducer(inputQueue, inputFileName);
        Thread readingThread = new Thread(producer);
        readingThread.start();

        Thread.sleep(50);
        Long startTime = System.currentTimeMillis();

        ScheduledExecutorService executorService1 = Executors.newScheduledThreadPool(numberOfThreads);

        processTasks(executorService1, inputQueue, outputQueue, countDownLatch);

        executorService1.shutdown();
        countDownLatch.await();


        SolutionsConsumer consumer = new SolutionsConsumer(outputQueue, outputFileName);
        Thread writingThread = new Thread(consumer);
        writingThread.start();
        Thread.sleep(50);
        Long stopTime = System.currentTimeMillis();
        System.out.println("total time = " + (stopTime - startTime));
    }

    private void processTasks(ExecutorService executorService,
                             LinkedBlockingQueue<String> inputQueue,
                             LinkedBlockingQueue<String> outputQueue,
                             CountDownLatch countDownLatch) throws InterruptedException, ExecutionException {
        RateLimiter rateLimiter = RateLimiter.create(maxCalculationsForSecond);
        List<Future<String>> results = new ArrayList<>();

        int inputQueueSize = inputQueue.size();
        for (int i = 0; i < inputQueueSize; i++) {
            String line = inputQueue.take();
            if (line == null) continue;

            String threadName = "thread" + i;
            rateLimiter.acquire();

            Future<String> future = executorService.submit(() -> {
                return new FactorialThread(threadName, line, outputQueue, countDownLatch).call();
            });

            results.add(future);
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        for (Future<String> future : results) {
            outputQueue.offer(future.get());
        }
    }

}

