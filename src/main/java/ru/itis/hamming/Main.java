package ru.itis.hamming;

import ru.itis.hamming.algorithm.Hamming;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final Hamming hamming = new Hamming();
    private static final Hamming.Prepare hammingPrepare = new Hamming.Prepare();
    private static final Hamming.Decode hammingDecode = new Hamming.Decode();

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        System.out.println("========== Режим работы ===========");
        System.out.println("1: Кодирование \t ----- \t  2: Декодирование");
        int mode = scan.nextInt();

        if (mode == 1) {
            System.out.println("Введите путь до файла с данными: ");
            String path = scan.next();

            String source = hammingPrepare.readFile(path);

            ArrayList<String> symbols = hamming.convertSymbolsToBits(source);
            String hammingCode = hamming.algorithmForAllSymbols(symbols);
            String erroredCode = hamming.setError(hammingCode);

            hamming.writeToFile("./coderResult.txt", erroredCode);
        }
        else if (mode == 2) {
            String source = hammingDecode.readFile("./coderResult.txt");

            String fixedhammingCode = hammingDecode.correctData(source);
            String deleteControlBits = hammingDecode.wrapperRestoreSourceString(fixedhammingCode);
            String restoreInitText = hammingDecode.restoreInitString(deleteControlBits);

            hammingDecode.writeToFile(restoreInitText);
        }
    }
}
