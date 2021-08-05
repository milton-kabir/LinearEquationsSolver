//package solver;
package com.kabir.milton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class Row {
    private final ComplexNumber[] elements;

    public Row(ComplexNumber[] elements) {
        this.elements = elements;
    }

    public Row(Row row) {
        elements = new ComplexNumber[row.length()];
        for (int i = 0; i < row.length(); i++) {
            elements[i] = row.getElement(i);
        }
    }

    public ComplexNumber getElement(int index) {
        return elements[index];
    }

    public void setElement(int position, ComplexNumber element) {
        elements[position] = element;
    }

    public void multiply(ComplexNumber multiplier) {
        for (int i = 0; i < elements.length; i++) {
            elements[i] = elements[i].multiply(multiplier);
        }
    }

    public void divide(ComplexNumber number) {
        for (int i = 0; i < elements.length; i++) {
            elements[i] = elements[i].divide(number);
        }
    }

    public void addition(Row addend) {
        if (addend.length() == elements.length) {
            for (int i = 0; i < elements.length; i++) {
                elements[i] = elements[i].add(addend.getElement(i));
            }
        }
    }

    public boolean hasNoSolution() {
        int countZero = 0;
        if (!elements[elements.length - 1].equal(new ComplexNumber(0, 0))) {
            for (int i = 0; i < elements.length - 1; i++) {
                if (elements[i].equal(new ComplexNumber(0, 0))) {
                    countZero++;
                }
            }
            return countZero == elements.length - 1;
        }
        return false;
    }

    public boolean isAllZero() {
        for (ComplexNumber element : elements) {
            if (!element.equal(new ComplexNumber(0, 0))) {
                return false;
            }
        }
        return true;
    }

    public int length() {
        return elements.length;
    }
}

class Matrix {
    private final Row[] rows;
    private final int numberOfVariables;
    private String solution;

    public String getSolution() {
        return solution;
    }

    public Matrix(int numberOfVariables, int numberOfEquations) {
        this.numberOfVariables = numberOfVariables;
        rows = new Row[numberOfEquations];
        solution = "";
    }

    public void add(Row row, int index) {
        if (index >= 0 && index < rows.length) {
            rows[index] = row;
        }
    }

    public void swapRow(int row1, int row2) {
        Row aux = rows[row1];
        rows[row1] = rows[row2];
        rows[row2] = aux;
    }

    public void swapColumns(int col1, int col2) {
        ComplexNumber aux;
        for (Row row : rows) {
            aux = row.getElement(col1);
            row.setElement(col1, row.getElement(col2));
            row.setElement(col2, aux);
        }
    }

    public void showResolution() {
        for (int row = 0; row < rows.length; row++) {
            if (rows[row].getElement(row).equal(new ComplexNumber(0, 0))) {
                boolean leadingEntryZero = true;
                for (int i = row + 1; i < rows.length; i++) {
                    if (!rows[i].getElement(row).equal(new ComplexNumber(0, 0))) {
                        swapRow(row, i);
                        leadingEntryZero = false;
                        System.out.println("Rows manipulation:");
                        System.out.printf("R%d <-> R%d\n", row + 1, i + 1);
                        break;
                    }
                }
                if (leadingEntryZero) {
                    for (int i = row; i < rows[row].length() - 1; i++) {
                        if (!rows[row].getElement(i).equal(new ComplexNumber(0, 0))) {
                            swapColumns(row, i);
                            leadingEntryZero = false;
                            System.out.println("Cols manipulation:");
                            System.out.printf("C%d <-> C%d\n", row + 1, i + 1);
                            break;
                        }
                    }
                }
                if (leadingEntryZero) {
                    for (int i = row + 1; i < rows.length; i++) {
                        for (int j = +1; j < rows[row].length() - 1; j++) {
                            if (!rows[i].getElement(j).equal(new ComplexNumber(0, 0))) {
                                swapRow(row, i);
                                System.out.println("Rows manipulation:");
                                System.out.printf("R%d <-> R%d\n", row + 1, i + 1);
                                swapColumns(row, j);
                                System.out.println("Cols manipulation:");
                                System.out.printf("C%d <-> C%d\n", row + 1, j + 1);
                                leadingEntryZero = false;
                                break;
                            }
                        }
                        if (!leadingEntryZero) {
                            break;
                        }
                    }
                }
            }

            if (!rows[row].getElement(row).equal(new ComplexNumber(0, 0))) {
                ComplexNumber actualNumber = rows[row].getElement(row);
                if (!actualNumber.equal(new ComplexNumber(1, 0))) {
                    rows[row].divide(actualNumber);
                    System.out.println("R" + (row + 1) + " / " + actualNumber + " -> R" + (row + 1));
                }
                ComplexNumber leadingEntry;
                for (int rowCol = row + 1; rowCol < rows.length; rowCol++) {
                    leadingEntry = rows[rowCol].getElement(row);
                    if (!leadingEntry.equal(new ComplexNumber(0, 0))) {
                        leadingEntry = leadingEntry.multiply(-1);
                        Row aux = new Row(rows[row]);
                        aux.multiply(leadingEntry);
                        rows[rowCol].addition(aux);
                        System.out.println(leadingEntry + " * R" + (row + 1) + " + R" + (rowCol + 1) + " -> R" + (rowCol + 1));
                    }
                }
            }
        }

        for (Row row : rows) {
            if (row.hasNoSolution()) {
                solution = "No solutions";
                System.out.println(solution);
                return;
            }
        }
        int countZeroRows = 0;
        for (Row row : rows) {
            if (row.isAllZero()) {
                countZeroRows++;
            }
        }
        int significantEquations = rows.length - countZeroRows;
        if (significantEquations < numberOfVariables) {
            solution = "Infinitely many solutions";
            System.out.println("Infinitely many solutions");
            return;
        }
        for (int rowCol = rows.length - 1; rowCol > 0; rowCol--) {
            ComplexNumber element;
            for (int row = rowCol - 1; row >= 0; row--) {
                element = rows[row].getElement(rowCol);
                if (!element.equal(new ComplexNumber(0, 0))) {
                    element = element.multiply(-1);
                    Row aux = new Row(rows[rowCol]);
                    aux.multiply(element);
                    rows[row].addition(aux);
                    System.out.println(element + " * R" + (rowCol + 1) + " + R" + (row + 1) + " -> R" + (row + 1));
                }
            }
        }
        StringBuilder strB = new StringBuilder();
        for (int i = 0; i < numberOfVariables - 1; i++) {
            strB.append(rows[i].getElement(rows[i].length() - 1)).append("\n");
        }
        strB.append(rows[numberOfVariables - 1].getElement(rows[numberOfVariables - 1].length() - 1));
        solution = strB.toString();
    }
}

class ComplexNumber {

    private final double real;
    private final double imaginary;

    public ComplexNumber(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public ComplexNumber(String str) {
        String real;
        String imaginary;
        int length = str.length();
        int index;

        if (str.lastIndexOf('+') != -1) {
            index = str.indexOf('+');
            real = str.substring(0, index);
            if (index + 1 == length - 1) {
                imaginary = "1";
            } else {
                imaginary = str.substring(index + 1, length - 1);
            }
        } else if (str.lastIndexOf('-') != -1 && str.lastIndexOf('-') != 0) {
            index = str.lastIndexOf('-');
            real = str.substring(0, index);
            if (index + 1 == length - 1) {
                imaginary = "-1";
            } else {
                imaginary = str.substring(index, length - 1);
            }

        } else if (str.lastIndexOf('i') != -1) {
            if ("i".equals(str)) {
                imaginary = "1";

            } else if ("-i".equals(str)) {
                imaginary = "-1";
            } else {
                imaginary = str.substring(0, length - 1);
            }
            real = "0";
        } else {
            real = str.substring(0, length);
            imaginary = "0";
        }
        this.real = Double.parseDouble(real);
        this.imaginary = Double.parseDouble(imaginary);
    }

    public ComplexNumber add(ComplexNumber num) {
        return new ComplexNumber(this.real + num.real, this.imaginary + num.imaginary);
    }

    public ComplexNumber multiply(double num) {
        return new ComplexNumber(num * real, num * imaginary);
    }

    public ComplexNumber multiply(ComplexNumber num) {
        double re = this.real * num.getReal() - (this.imaginary * num.getImaginary());
        double im = this.getReal() * num.getImaginary() + this.imaginary * num.getReal();
        return new ComplexNumber(re, im);
    }

    public ComplexNumber divide(ComplexNumber that) {
        return this.multiply(that.reciprocal());
    }

    public ComplexNumber reciprocal() {
        double scale = real * real + imaginary * imaginary;
        return new ComplexNumber(real / scale, -imaginary / scale);
    }

    public boolean equal(ComplexNumber that) {
        return this.real == that.real && this.imaginary == that.imaginary;
    }

    public double getReal() {
        return real;
    }

    public double getImaginary() {
        return imaginary;
    }

    @Override
    public String toString() {
        if (real == 0) {
            if (imaginary == 0) {
                return "0";
            }
            return imaginary + "i";
        }
        if (imaginary == 0) return real + "";
        if (imaginary == 1) return "i";

        if (imaginary < 0) return (real + "-" + (-imaginary) + "i");
        return real + "+" + imaginary + "i";
    }
}

public class Main {
    public static void main(String[] args) {
        if (args.length == 4 && "-in".equals(args[0]) && "-out".equals(args[2])) {
            Matrix matrix = readFile(args[1]);
            if (matrix != null) {
                matrix.showResolution();
                System.out.println("The solution is: \n" + matrix.getSolution());
                writeFile(matrix.getSolution(), args[3]);
                System.out.println("Saved to file " + args[3]);
            }
        }
    }

    private static Row getLinearEquation(String str) {
        String[] itemsInLine = str.split("\\s+");
        ComplexNumber[] equation = new ComplexNumber[itemsInLine.length];
        for (int i = 0; i < equation.length; i++) {
            equation[i] = new ComplexNumber(itemsInLine[i]);
        }
        return new Row(equation);
    }

    private static Matrix readFile(String pathToFile) {
        File file = new File(pathToFile);

        try (Scanner scanner = new Scanner(file)) {

            int numberOfVariables = scanner.nextInt();
            int numberOfEquations = Integer.parseInt(scanner.nextLine().trim());
            Matrix matrix = new Matrix(numberOfVariables, numberOfEquations);
            int count = 0;
            while (scanner.hasNext()) {
                Row row = getLinearEquation(scanner.nextLine());
                matrix.add(row, count++);
            }
            return matrix;
        } catch (FileNotFoundException e) {
            System.out.println("No file found: " + pathToFile);
        }
        return null;
    }

    private static void writeFile(String solution, String pathToFile) {
        File file = new File(pathToFile);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(solution);
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
    }
}