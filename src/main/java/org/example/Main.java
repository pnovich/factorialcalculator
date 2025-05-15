package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        CalculateFactorialApp app = new CalculateFactorialApp(n);
        app.proccessFactorial();
    }
}