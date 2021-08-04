//package solver;
package com.kabir.milton;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // write your code here
        Scanner sc = new Scanner(System.in);
        String[] a = sc.nextLine().split(" ");
        String[] b = sc.nextLine().split(" ");
        double[] ar = new double[a.length];
        double[] br = new double[b.length];
        for (int i = 0; i < ar.length; i++) {
            ar[i] = Double.parseDouble(a[i]);
        }
        for (int i = 0; i < br.length; i++) {
            br[i] = Double.parseDouble(b[i]);
        }
        double y = br[2] - ar[2] * br[0] / ar[0];
        y = y / (br[1] - ar[1] * br[0] / ar[0]);
        double x = ar[2] - ar[1] * y;
        x /= ar[0];
        System.out.println(x + " " + y);

    }
}
