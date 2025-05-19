package org.example;

import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class FactorialThread implements Callable<String> {
    private String threadName;
    private String input;
    private LinkedBlockingQueue<String> outputQueue;
    private CountDownLatch countDownLatch;
    private Integer lineNumber;

    FactorialThread(String threadName, String input, Integer lineNumber, LinkedBlockingQueue<String> outputQueue, CountDownLatch countDownLatch) {
        this.input = input;
        this.outputQueue = outputQueue;
        this.countDownLatch = countDownLatch;
        this.threadName = threadName;
        this.lineNumber = lineNumber;
    }

    @Override
    public String call() {
        String currrentResult = calculateFactorial(this.input, this.lineNumber);
        this.countDownLatch.countDown();
        return currrentResult;
    }


    private String calculateFactorial(String line, Integer lineNumber) {
        if (!line.matches("\\d+")) {
            System.out.println(lineNumber + " -> " + line);
            return line;
        }

        int currentInt = Integer.parseInt(line);
        BigInteger bigResult = BigInteger.ONE;

        if (currentInt > 1) {
            for (int i = 2; i <= currentInt; i++) {
                bigResult = bigResult.multiply(BigInteger.valueOf(i));
            }
        }

        String result = currentInt + " = " + bigResult;
        System.out.println(lineNumber + " -> " + result);
        return result;
    }
}

