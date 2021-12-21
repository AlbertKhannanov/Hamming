package ru.itis.hamming.algorithm;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Hamming {

    private final Integer[][] generatorMatrix = new Integer[][]{
            {1, 1, 0, 1},
            {1, 0, 1, 1},
            {1, 0, 0, 0},
            {0, 1, 1, 1},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    };

    public String getHammingCodeForSequence(String sourceData) {
        StringBuilder result = new StringBuilder();

        ArrayList<String> splitByBits = convertSymbolsToBits(sourceData);

        for (String symbolBits : splitByBits) {
            for (int i = 0; i < symbolBits.length(); i += 4) {
                result.append(getHammingCode(symbolBits.substring(i, i + 4)));
            }
        }

        return result.toString();
    }

    public void writeToFile(String path, String data) {
        try (FileWriter writer = new FileWriter(path, false)) {
            writer.write(data);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private ArrayList<String> convertSymbolsToBits(String source) {
        ArrayList<String> binaryStrings = new ArrayList<>();

        for (int i = 0; i < source.length();) {
            int utf8Code = source.codePointAt(i);
            StringBuilder temp = new StringBuilder();
            for (byte b : source.substring(i, i + Character.charCount(utf8Code)).getBytes(StandardCharsets.UTF_8)) {
                temp.append(Integer.toBinaryString(b & 0xFF));
            }
            binaryStrings.add(addZeroBits(temp.toString()));
            temp.setLength(0);

            i += Character.charCount(utf8Code);
        }

        return binaryStrings;
    }

    private String getHammingCode(String source) {
        StringBuilder result = new StringBuilder();

        int[] currentSequence = new int[]{
                Integer.parseInt(source.substring(0, 1)),
                Integer.parseInt(source.substring(1, 2)),
                Integer.parseInt(source.substring(2, 3)),
                Integer.parseInt(source.substring(3, 4))
        };

        int[] tempResult = matrixToVectorMultiply(currentSequence);

        for (int i = 0; i < tempResult.length; i++) {
            tempResult[i] = tempResult[i] % 2;
            result.append(tempResult[i]);
        }

        return result.toString();
    }

    private int[] matrixToVectorMultiply(int[] curSeq) {
        int[] result = new int[7];

        for (int i = 0; i < generatorMatrix.length; i++) {
            for (int j = 0; j < curSeq.length; j++) {
                result[i] += generatorMatrix[i][j] * curSeq[j];
            }
        }

        return result;
    }

    private String addZeroBits(String current) {
        StringBuilder stringBuilder = new StringBuilder(current);

        while (stringBuilder.length() < 32) {
            stringBuilder.insert(0, "0");
        }

        return stringBuilder.toString();
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

        private final int[][] parityCheckMatrix = new int[][]{
                {1, 0, 1, 0, 1, 0, 1},
                {0, 1, 1, 0, 0, 1, 1},
                {0, 0, 0, 1, 1, 1, 1}
        };

        private final int[][] restoreMatrix = new int[][]{
                {0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 1}
        };

        public String readHammingCodeAndRestoreSourceData(String hammingCodeSequence) {
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < hammingCodeSequence.length(); i += 7) {
                result.append(
                        restoreSourceData(restoreHammingCode(hammingCodeSequence.substring(i, i + 7)))
                );
            }

            return result.toString();
        }

        public String restoreInitString(String bytes) {
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < bytes.length(); i += 32) {
                long code = Long.parseLong(bytes.substring(i, i + 32), 2);
                if (code != 10) {
                    result.append(new String(longToByteArray(code), StandardCharsets.UTF_8).replaceAll("\\u0000", ""));
                } else {
                    result.append("\n");
                }
            }

            return result.toString();
        }

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

        public void writeToFile(String path, String restoreInitText) {
            try (FileWriter writer = new FileWriter(path, false)) {
                writer.write(restoreInitText);
                writer.flush();
            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
        }

        private String restoreHammingCode(String encoded) {
            StringBuilder result = new StringBuilder();

            int[] encodedVector = new int[]{
                    Integer.parseInt(encoded.substring(0, 1)),
                    Integer.parseInt(encoded.substring(1, 2)),
                    Integer.parseInt(encoded.substring(2, 3)),
                    Integer.parseInt(encoded.substring(3, 4)),
                    Integer.parseInt(encoded.substring(4, 5)),
                    Integer.parseInt(encoded.substring(5, 6)),
                    Integer.parseInt(encoded.substring(6, 7)),
            };

            int[] temp = matrixToVectorMultiply(encodedVector, parityCheckMatrix);

            int errIndex = 0;
            for (int i = 0; i < temp.length; i++) {
                if (temp[i] % 2 == 1) {
                    errIndex += 1 << i; // возводим в степень двойки
                }
            }

            if (errIndex != 0)
                encodedVector[errIndex - 1] = encodedVector[errIndex - 1] == 1 ? 0 : 1;

            for (int i = 0; i < encodedVector.length; i++) {
                result.append(encodedVector[i]);
            }

            return result.toString();
        }

        private String restoreSourceData(String hammingCode) {
            StringBuilder result = new StringBuilder();

            int[] hammingCodeVector = new int[]{
                    Integer.parseInt(hammingCode.substring(0, 1)),
                    Integer.parseInt(hammingCode.substring(1, 2)),
                    Integer.parseInt(hammingCode.substring(2, 3)),
                    Integer.parseInt(hammingCode.substring(3, 4)),
                    Integer.parseInt(hammingCode.substring(4, 5)),
                    Integer.parseInt(hammingCode.substring(5, 6)),
                    Integer.parseInt(hammingCode.substring(6, 7)),
            };

            int[] tempResult = matrixToVectorMultiply(hammingCodeVector, restoreMatrix);

            for (int i = 0; i < tempResult.length; i++) {
                result.append(tempResult[i]);
            }

            return result.toString();
        }

        private int[] matrixToVectorMultiply(int[] vector, int[][] matrix) {
            int[] result = new int[matrix.length];

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < vector.length; j++) {
                    result[i] += matrix[i][j] * vector[j];
                }
            }

            return result;
        }

        private byte[] longToByteArray(long num) {
            byte[] longBytes = new byte[4];
            longBytes[0] = (byte) (num >>> 24);
            longBytes[1] = (byte) (num >>> 16);
            longBytes[2] = (byte) (num >>> 8);
            longBytes[3] = (byte) num;
            return longBytes;
        }
    }
}
