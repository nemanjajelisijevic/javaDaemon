package com.daemonize.daemondevapp;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ExampleUnitTest {


    private SortAlgorithm sortAlgorithm;

    private int[] array = new int[] {65, 33, 45, 80, 105, 33, 66, 2, 84, 68, 22, 107, -11, 212, 41, 615, 92, 86, 99, 6, 9, 87};
    private int[] nearlySortedArray = new int[] {-11, 2, 6, 9, 22, 33, 33, 41, 45, 65, 66, 68, 80, 84, 86, 87, 99, 92, 105, 107, 212, 615};


    private int[] currentArray;


    @Before
    public void setUp() {
        currentArray = array;
    }

    @Test
    public void quickSort() {
        sortAlgorithm = new QuickSort();
        sort();
    }

    @Test
    public void insertionSort() {
        sortAlgorithm = new InsertionSort();
        sort();
    }

    @Test
    public void mergeSort() {
        sortAlgorithm = new MergeSort();
        sort();
    }

    private void sort() {

        int[] array = Arrays.copyOf(this.currentArray, this.currentArray.length);
        sortAlgorithm.sortMeasured(array);

        for (int i = 0; i < array.length; ++i) {
            System.out.println(Integer.toString(array[i]));
            if ( i > 0)
                assertTrue(array[i -1] <= array[i]);
        }
    }

}