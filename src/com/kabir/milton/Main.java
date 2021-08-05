//package solver;
package com.kabir.milton;

import java.util.Scanner;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(args));
        String pathIn = "";
        String pathOut = "";
        for (int i = 0; i < args.length; i++) {
            if ("-in".equals(args[i])) {
                pathIn = args[i + 1];
            } else if ("-out".equals(args[i])) {
                pathOut = args[i + 1];
            }
        }
        File fileIn = new File(pathIn);
        if (fileIn.exists()) {
            System.out.println("exists");
        }
        Scanner scanner = new Scanner(fileIn);
        if (!scanner.hasNext()) {
            System.out.println("has no string");
            scanner.close();
            return;
        }
        String s = scanner.nextLine();
        int number = Integer.parseInt(s);
        double[][] matrix = new double[number][number + 1];
        for (int i = 0; i < number; i++) {
            matrix[i] = Arrays.stream(scanner.nextLine().split("\\s"))
                    .mapToDouble(Double::parseDouble)
                    .toArray();
        }
        scanner.close();
        double coef;
        for (int i = 0; i < number; i++) {
            coef = matrix[i][i];
            for (int k = i; k < number + 1; k++) {
                matrix[i][k] = matrix[i][k] / coef;
            }
            if (number > 1) {
                for (int ii = i + 1; ii < number; ii++) {
                    coef = matrix[ii][i];
                    for (int j = i; j < number + 1; j++) {
                        matrix[ii][j] = matrix[ii][j] - matrix[i][j] * coef;
                    }
                }
            }
        }
        if (number > 1) {
            for (int i = number - 1; i >= 0; i--) {
                for (int ii = i - 1; ii >= 0; ii--) {
                    coef = matrix[ii][i];
                    for (int j = i; j < number + 1; j++) {
                        matrix[ii][j] = matrix[ii][j] - matrix[i][j] * coef;
                    }
                }
            }
        }
        File fileOut = new File(pathOut);
        FileWriter writer = new FileWriter(fileOut);
        for (int i = 0; i < number; i++) {
            if (i > 0 && i < number) {
                writer.write("\n");
            }
            System.out.println("" + matrix[i][number]);
            writer.write("" + matrix[i][number]);
        }
        writer.close();
    }
}