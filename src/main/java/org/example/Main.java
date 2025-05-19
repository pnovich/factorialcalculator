package org.example;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int n = readNumberOfThreads();
        int maxCalculationsForSecond = 100;
        CalculateFactorialApp app = new CalculateFactorialApp(n, maxCalculationsForSecond);
        app.proccessFactorial();
    }

    private static Integer readNumberOfThreads() {
        Integer n = null;
        while (n == null) {
            System.out.println("Enter the number of threads you want to run :");
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            n = validateInput(input);
        }
        return n;
    }

    private static Integer validateInput(String input) {
        if (!input.matches("\\d+") || Integer.parseInt(input) < 1) {
            System.out.println("input should be integer positive number");
            return null;
        }
        return Integer.parseInt(input);
    }

}