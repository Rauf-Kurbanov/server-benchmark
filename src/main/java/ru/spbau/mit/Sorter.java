package ru.spbau.mit;

import java.util.Random;

public interface Sorter {

    static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            final int x = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > x) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = x;
        }
    }

    static int[] generateArr(int size) {
        final Random rand = new Random();
        return rand.ints(0, size * 2).limit(size).toArray();
    }
}
