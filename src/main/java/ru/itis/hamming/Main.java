package ru.itis.hamming;

import ru.itis.hamming.algorithm.Hamming;

public class Main {

    private static final Hamming hamming = new Hamming();
    private static final Hamming.Prepare hammingPrepare = new Hamming.Prepare();
    private static final Hamming.Decode hammingDecode = new Hamming.Decode();

    public static void main(String[] args) {

        String source = hammingPrepare.readFile("D:\\Another\\Univercity\\Тесты\\tic-hamming\\src\\main\\test.txt");

        String hammingCode = hamming.fixControlBits(hamming.setControlBits(source));
        String hammingCodeWithError = hamming.setError(hammingCode);
        String correctDecodedHammingCode = hammingDecode.correctOfBits(hammingCode);
        String restoredSource = hammingDecode.restoreSourceString(correctDecodedHammingCode);

        System.out.println("Исходная строка \t\t\t\t\t" + source);
        System.out.println("Код Хэмминга для исходной строки \t" + hammingCode);
        System.out.println("Код Хэмминга с возможной ошибкой \t" + hammingCodeWithError);
        System.out.println("Исправленный код Хэммина \t\t\t" + correctDecodedHammingCode);
        System.out.println("Восстановленная строка \t\t\t\t" + restoredSource);


//        StringBuilder stringBuilder = new StringBuilder("qwer");
//        stringBuilder.delete(0,1);
//        System.out.println(stringBuilder.toString());
    }
}
