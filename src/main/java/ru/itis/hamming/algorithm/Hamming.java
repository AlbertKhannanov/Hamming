package ru.itis.hamming.algorithm;

import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Hamming {

    private Integer countControlBits = 0;

    public String setControlBits(String source) {
        StringBuilder result = new StringBuilder();

        while (countControlBits < Math.log(countControlBits + source.length() + 1) / Math.log(2)) {
            countControlBits++;
        }

        int j = 0;
        for (int i = 0; i < source.length() + countControlBits; i++) {
            if (((i + 1) & i) == 0) {
                result.append("0");
            } else {
                result.append(source.charAt(j++));
            }
        }

        return result.toString();
    }

    public String fixControlBits(String sourceWithControlBits) {
        StringBuilder result = new StringBuilder(sourceWithControlBits);

        for (int i = 0; i < countControlBits; i++) {

            int currentSum = 0;
            int fineCounter = 0;
            int skipCounter = 0;
            boolean skipFlag = false;
            for (int j = (1 << i); j <= sourceWithControlBits.length(); j++) {
                if (fineCounter == 1 << i) {
                    fineCounter = 0;
                    skipFlag = true;
                }

                if (skipFlag) {
                    skipCounter++;
                    if (skipCounter == 1 << i) {
                        skipCounter = 0;
                        skipFlag = false;
                    }
                    continue;
                }

                if ((j & (j - 1)) == 0) {
                    fineCounter++;
                    continue;
                }

                currentSum += Integer.parseInt(sourceWithControlBits.substring(j - 1, j));
                fineCounter++;
            }


            result.replace((1 << i) - 1, 1 << i, (currentSum % 2) + "");
        }

        return result.toString();
    }

    public String setError(String hammingCode) {
        StringBuilder result = new StringBuilder(hammingCode);

        Random random = new Random();
        if (random.nextBoolean()) {
            int index = random.nextInt(hammingCode.length() - 1);
            result.replace(index, index + 1, defineSymbol(result.substring(index, index + 1)));
        }

        return result.toString();
    }

    private String defineSymbol(String symbol) {
        return symbol.equals("0") ? "1" : "0";
    }

    public static class Prepare {

        public String readFile(String path) {
            StringBuilder result = new StringBuilder();
            try (FileReader reader = new FileReader(path)) {
                int c;
                while ((c = reader.read()) != -1) {
                    result.append((char) c);
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            return result.toString();
        }
    }

    public static class Decode {

        private int countControlBits = 0;

        public String correctOfBits(String hammingCode) {
            StringBuilder result = new StringBuilder(hammingCode);

            for (int i = 1; i < hammingCode.length(); i++) {
                if (((i - 1) & i) == 0) {
                    countControlBits++;
                }
            }

            int errorSum = 0;
            for (int i = 0; i < countControlBits; i++) {

                int currentSum = 0;
                int fineCounter = 0;
                int skipCounter = 0;
                boolean skipFlag = false;
                for (int j = (1 << i); j <= hammingCode.length(); j++) {
                    if (fineCounter == 1 << i) {
                        fineCounter = 0;
                        skipFlag = true;
                    }

                    if (skipFlag) {
                        skipCounter++;
                        if (skipCounter == 1 << i) {
                            skipCounter = 0;
                            skipFlag = false;
                        }
                        continue;
                    }

                    if ((j & (j - 1)) == 0) {
                        fineCounter++;
                        continue;
                    }

                    currentSum += Integer.parseInt(hammingCode.substring(j - 1, j));
                    fineCounter++;
                }

                if (currentSum % 2 != Integer.parseInt(hammingCode.substring((1 << i) - 1, 1 << i)))
                    errorSum += 1 << i;
            }

            if (errorSum != 0)
                result.replace(errorSum - 1, errorSum, defineSymbol(result.substring(errorSum - 1, errorSum)));

            return result.toString();
        }

        public String restoreSourceString(String hammingCode) {
            StringBuilder result = new StringBuilder(hammingCode);

            int offset = 0;
            for (int i = 0; i < result.length(); i++) {
                if (((i + 1) & i) == 0) {
                    result.replace(i + offset, i + offset + 1, "");
                    offset--;
                }
            }

            return result.toString();
        }

        private String defineSymbol(String symbol) {
            return symbol.equals("0") ? "1" : "0";
        }
    }
}
