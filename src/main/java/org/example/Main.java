package org.example;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int maxCalculationsForSecond = 100;
        CalculateFactorialApp app = new CalculateFactorialApp(n, maxCalculationsForSecond);
        app.proccessFactorial();
    }
}