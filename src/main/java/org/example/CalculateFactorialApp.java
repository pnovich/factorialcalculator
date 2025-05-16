package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CalculateFactorialApp {
    int numberOfThreads;
    static int numberOfStringBeforeDelay = 4;
    volatile FactorialCounter lineCounter;
    volatile TimeCounter timeCounter = new TimeCounter(0);
    public CalculateFactorialApp(int numberOfThreads) {
       this.numberOfThreads = numberOfThreads;
    }


    public void proccessFactorial() throws InterruptedException {
        LinkedBlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
        lineCounter = new FactorialCounter(0);
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

        ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1);
        TimeChecker timeChecker = new TimeChecker(timeCounter);
        scheduledExecutor.scheduleAtFixedRate(timeChecker, 0,2, TimeUnit.MILLISECONDS);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < inputQueue.size(); i++) {
            String line = inputQueue.poll();
            Future<String> future = executorService.submit(new FactorialThread("thread" + i, lineCounter, line, outputQueue, countDownLatch, timeCounter));
            list.add(future);
        }

//        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(numberOfThreads);
//        for (int i = 0; i < numberOfThreads; i++) {
//            String line = inputQueue.poll();
//            Future<String> future = executorService.schedule(
//                    new FactorialThread("thread" + i, lineCounter, line, outputQueue, countDownLatch, timeCounter),
//                    2,
//                    TimeUnit.MILLISECONDS
//            );
////                    executorService.submit(new FactorialThread("thread" + i, lineCounter, line, outputQueue, countDownLatch, timeCounter));
//            list.add(future);
//        }


        executorService.shutdown();
        scheduledExecutor.shutdown();
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
    FactorialCounter factorialCounter;
    FactorialCounter internalCounter;
    TimeCounter timeCounter;

    FactorialThread(String threadName, FactorialCounter factorialCounter,  String input, LinkedBlockingQueue<String> outputQueue,
                    CountDownLatch countDownLatch, TimeCounter timeCounter) {
        this.input = input;
        this.outputQueue = outputQueue;
        this.countDownLatch = countDownLatch;
        this.threadName = threadName;
        this.factorialCounter = factorialCounter;
        this.timeCounter = timeCounter;
        this.internalCounter = new FactorialCounter(0);
    }

    @Override
    public String call() {

        String currrentResult = calculateFactorial(this.input);
        this.countDownLatch.countDown();
        return currrentResult;
    }

    private String calculateFactorial(String line) {
        System.out.println("timeCounter inside calculation= " + timeCounter.getTimeCounter());
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

        synchronized (factorialCounter) {
            if (factorialCounter.getCounter() >= CalculateFactorialApp.numberOfStringBeforeDelay) {
                try {
                    System.out.println("delay, counter = " + factorialCounter.getCounter());
                    Thread.sleep(1000);
                    factorialCounter.setCounter(0);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            factorialCounter.setCounter(factorialCounter.getCounter() + 1);
            internalCounter.setCounter(internalCounter.getCounter() + 1);
            System.out.println("factorialCounter = " + factorialCounter.getCounter());
            System.out.println("internalCounter = " + internalCounter.getCounter());

        }
        return result;
    }
}

class FactorialCounter {
    Integer factorialCounter;

    public FactorialCounter(Integer factorialCounter) {
        this.factorialCounter = factorialCounter;
    }

    public Integer getCounter() {
        return factorialCounter;
    }

    public void setCounter(Integer factorialCounter) {
        this.factorialCounter = factorialCounter;
    }
}

class TimeCounter {
    Integer timeCounter;

    public TimeCounter(Integer timeCounter) {
        this.timeCounter = timeCounter;
    }

    public Integer getTimeCounter() {
        return timeCounter;
    }

    public void setTimeCounter(Integer timeCounter) {
        this.timeCounter = timeCounter;
    }
}

class TimeChecker implements Runnable {
    TimeCounter timeCounter;

    public TimeChecker(TimeCounter timeCounter) {
        this.timeCounter = timeCounter;
    }

    @Override
    public void run() {
    timeCounter.setTimeCounter(timeCounter.getTimeCounter() + 1);
        System.out.println("timeCounter = " + timeCounter.timeCounter);
    }
}
