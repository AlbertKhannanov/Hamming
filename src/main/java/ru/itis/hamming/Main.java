package ru.itis.hamming;

import ru.itis.hamming.algorithm.Hamming;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final Hamming hamming = new Hamming();
    private static final Hamming.Prepare hammingPrepare = new Hamming.Prepare();
    private static final Hamming.Decode hammingDecode = new Hamming.Decode();

    public static void main(String[] args) {

        System.out.println(hamming.getHammingCode("1011"));
    }
}
