//package solver;
 package com.kabir.milton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Scanner;

class Row {

    private final double[] elements;
    private final int size;

    public Row(int size) {
        this.size = size;
        elements = new double[size];
    }

    public void setVal(int idx, double val) {
        elements[idx] = val;
    }

    public int getSize() {
        return elements.length;
    }

    public void divideRowByElement(int idx) {
        double d = elements[idx];
        for (int i = idx; i < size; i++) {
            elements[i] /= d;
        }
    }

    public void subtractRowMultipliedByElement(Row row, int idx) {
        double d = elements[idx];
        for (int i = idx; i < size; i++) {
            elements[i] -= row.elements[i] * d;
        }
    }

    public double getElement(int idx) {
        return elements[idx];
    }

    public double getLastElement() {
        return elements[size - 1];
    }

    public void swap(int i, int j) {
        if (i == j) return;
        double t = elements[i];
        elements[i] = elements[j];
        elements[j] = t;
    }

    public boolean areTheFirstElementsZero() {
        for (int i = 0; i < size - 1; i++) {
            if (elements[i] != 0) {
                return false;
            }
        }
        return true;
    }

}

class Matrix {

    private final Row[] rows;
    private final int numRows;
    private final Deque<Integer> columnsSwapStack;

    public Matrix(int numRows, int rowSize) {
        this.numRows = numRows;
        rows = new Row[numRows];
        for (int i = 0; i < numRows; i++) {
            rows[i] = new Row(rowSize);
        }
        columnsSwapStack = new ArrayDeque<>();
    }

    public Row getRow(int idx) {
        return rows[idx];
    }

    public boolean divideRowByElement(int rowIdx, int elementIdx) {
        if (findNonZeroCoefficient(rowIdx, elementIdx)) {
            rows[rowIdx].divideRowByElement(elementIdx);
            return true;
        }
        return false;
    }

    public void subtractRowFromRowsBelow(int rowIdx) {
        Row row = rows[rowIdx];
        for (int i = rowIdx + 1; i < numRows; i++) {
            rows[i].subtractRowMultipliedByElement(row, rowIdx);
        }
    }

    public void subtractRowFromRowsAbove(int rowIdx) {
        Row row = rows[rowIdx];
        for (int i = 0; i < rowIdx; i++) {
            rows[i].subtractRowMultipliedByElement(row, rowIdx);
        }
    }

    public Deque<Integer> getColumnsSwapStack() {
        return columnsSwapStack;
    }

    private boolean findNonZeroCoefficient(int rowIdx, int elementIdx) {
        return findNonZeroCoefficient(rowIdx, elementIdx, rowIdx, elementIdx);
    }

    private boolean findNonZeroCoefficient(int rowIdx, int columnIdx, int initialRowIdx, int initialColumnIdx) {
        if (rowIdx >= numRows || columnIdx >= rows[rowIdx].getSize() - 1) {
            return false;
        }

        for (int i = rowIdx; i < numRows; i++) {
            if (rows[i].getElement(columnIdx) != 0) {
                swapRows(i, initialRowIdx);
                swapColumns(columnIdx, initialColumnIdx);
                return true;
            }
        }

        int rowSize = rows[rowIdx].getSize() - 1;
        for (int i = columnIdx + 1; i < rowSize; i++) {
            if (rows[rowIdx].getElement(i) != 0) {
                swapRows(rowIdx, initialRowIdx);
                swapColumns(i, initialColumnIdx);
                return true;
            }
        }

        return findNonZeroCoefficient(rowIdx + 1, columnIdx + 1, initialRowIdx, initialColumnIdx);
    }

    private void swapRows(int i, int j) {
        if (i == j) return;
        Row temp = rows[i];
        rows[i] = rows[j];
        rows[j] = temp;
    }

    private void swapColumns(int i, int j) {
        if (i == j) return;
        columnsSwapStack.push(i);
        columnsSwapStack.push(j);
        for (Row row : rows) {
            row.swap(i, j);
        }
    }
}

class LinearEquation {

    private final Matrix matrix;
    private final int numEquations;
    private final int numVars;

    LinearEquation(int numVars, int numEquations) {
        this.numVars = numVars;
        this.numEquations = numEquations;
        matrix = new Matrix(numEquations, numVars + 1);
    }

    public void setCoefficients(double[] coeffs) {
        int rowSize = numVars + 1;
        if (coeffs.length != numEquations * rowSize) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < numEquations; i++) {
            Row row = matrix.getRow(i);
            for (int j = 0; j < rowSize; j++) {
                row.setVal(j, coeffs[i * rowSize + j]);
            }
        }
    }

    public double[] solve() {
        makeEchelonMatrix();
        if (containsContradiction()) {
            return null;
        }
        if (getSignificantEquations() < numVars) {
            return new double[]{Double.POSITIVE_INFINITY};
        }
        backSolving();
        return getSolution();
    }

    private void makeEchelonMatrix() {
        for (int i = 0; i < numEquations; i++) {
            if (!matrix.divideRowByElement(i, i)) {
                return;
            }
            matrix.subtractRowFromRowsBelow(i);
        }
    }

    private boolean containsContradiction() {
        for (int i = 0; i < numEquations; i++) {
            Row row = matrix.getRow(i);
            if (row.areTheFirstElementsZero() && row.getLastElement() != 0) {
                return true;
            }
        }
        return false;
    }

    private int getSignificantEquations() {
        int significantEquations = 0;
        for (int i = 0; i < numEquations; i++) {
            if (!matrix.getRow(i).areTheFirstElementsZero()) {
                significantEquations++;
            }
        }
        return significantEquations;
    }

    private void backSolving() {
        for (int i = numEquations - 1; i > 0; i--) {
            matrix.subtractRowFromRowsAbove(i);
        }
    }

    private double[] getSolution() {
        double[] solution = new double[numEquations];
        for (int i = 0; i < numEquations; i++) {
            solution[i] = matrix.getRow(i).getLastElement();
        }
        Deque<Integer> swapStack = matrix.getColumnsSwapStack();
        while (!swapStack.isEmpty()) {
            swap(solution, swapStack.pop(), swapStack.pop());
        }
        return Arrays.copyOf(solution, numVars);
    }

    public void swap(double[] arr, int i, int j) {
        if (i == j) return;
        double t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

}

public class Main {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Error");
        }

        String inFile = null;
        String outFile = null;
        for (int i = 0; i < 4; i += 2) {
            switch (args[i]) {
                case "-in":
                    inFile = args[i + 1];
                    break;
                case "-out":
                    outFile = args[i + 1];
                    break;
            }
        }

        LinearEquation equation = readEquation(inFile);
        double[] solution = equation.solve();
        writeSolution(outFile, solution);
    }

    private static LinearEquation readEquation(String file) {
        try (BufferedReader br = Files.newBufferedReader(Path.of(file))) {
            Scanner sc = new Scanner(br);
            int numVars = sc.nextInt();
            int numEquations = sc.nextInt();
            LinearEquation equation = new LinearEquation(numVars, numEquations);
            double[] coeffs = new double[numEquations * (numVars + 1)];
            for (int i = 0; i < coeffs.length; i++) {
                coeffs[i] = sc.nextDouble();
            }
            equation.setCoefficients(coeffs);
            return equation;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void writeSolution(String file, double[] solution) {
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(file))) {
            if (solution == null) {
                bw.write("No solutions");
            } else if (solution[0] == Double.POSITIVE_INFINITY) {
                bw.write("Infinitely many solutions");
            } else {
                for (double d : solution) {
                    bw.write(Double.toString(d));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}