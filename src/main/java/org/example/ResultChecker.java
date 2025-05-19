package org.example;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ResultChecker {
//for manual testing to compare input and output files
    public  static void main(String[] args) throws IOException {
        String inputFile = "src/main/resources/input.txt";
        String outputFile = "src/main/resources/output.txt";
        ResultChecker checker = new ResultChecker();
        System.out.println("result: " + checker.check(inputFile, outputFile));
    }

    public String check(String inputFileName, String outputFileName) throws IOException {
        BlockingQueue<String> inputQueue = getQueueFromFile(inputFileName);
        BlockingQueue<String> outputQueue = getQueueFromFile(outputFileName);
        int inputSize = inputQueue.size();
        int outputSize = outputQueue.size();
        if (inputSize != outputSize || inputSize == 0 || outputSize == 0) {
            return "size not equals";
        }
        boolean flag = true;
        int count = 0;
        for (int i = 0; i < inputSize; i++) {
            String inputValue = inputQueue.poll();
            String outputValue = outputQueue.poll();
            System.out.println("Input value: " + inputValue + " Output value: " + outputValue);
            if (inputValue.isEmpty()) {
                if (!outputValue.isEmpty()) {
                    flag = false;
                    count = i;
                    break;
                }

            } else if (!inputValue.split(" ")[0].equals(outputValue.split("")[0])) {
                flag = false;
                count = i;
                break;
            }
        }
        if (!flag) {
            return "inputValue not equals at string " + count;
        }
        return "ok";
    }

    private BlockingQueue<String> getQueueFromFile(String inputFileName) throws IOException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        File file = new File(inputFileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            queue.offer(line);
        }
        br.close();
        return queue;
    };

}
