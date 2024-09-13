package dbms;

import java.util.Arrays;

public class Printer {
    public static void printArr(String[] arr) {
        Arrays.stream(arr).forEach(System.out::println);
    }
}
