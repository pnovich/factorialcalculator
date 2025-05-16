package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int maxCalculationsForSecond = 100;
        CalculateFactorialApp app = new CalculateFactorialApp(n, maxCalculationsForSecond);
        app.proccessFactorial();
    }
}