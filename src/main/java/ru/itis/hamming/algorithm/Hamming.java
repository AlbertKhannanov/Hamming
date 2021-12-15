package ru.itis.hamming.algorithm;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Hamming {

    private Integer[][] G = new Integer[][]{
            {1, 1, 0, 1},
            {1, 0, 1, 1},
            {1, 0, 0, 0},
            {0, 1, 1, 1},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    };

    public ArrayList<String> convertSymbolsToBits(String source) {
        ArrayList<String> binaryStrings = new ArrayList<>();

        for (int i = 0; i < source.length(); i++) {
            StringBuilder temp = new StringBuilder();
            for (byte b : String.valueOf(source.charAt(i)).getBytes(StandardCharsets.UTF_8)) {
                temp.append(Integer.toBinaryString(b & 0xFF));
            }
            binaryStrings.add(addZeroBits(temp.toString()));
            temp.setLength(0);
        }

        return binaryStrings;
    }

    public String algorithmForAllSymbols(ArrayList<String> symbols) {
        StringBuilder result = new StringBuilder();

        for (String binSymbol : symbols) {
            for (int i = 0; i < binSymbol.length(); i += 4) {
//                result.append(this.fixControlBits(this.setControlBits(binSymbol.substring(i, i + 4))));
            }
        }

        return result.toString();
    }

    public String getHammingCode(String source) {
        StringBuilder result = new StringBuilder();

        Integer[] currentSequence = new Integer[]{
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

    private int[] matrixToVectorMultiply(Integer[] curSeq) {
        int[] result = new int[7];

        for (int i = 0; i < G.length; i++) {
            for(int j = 0; j < curSeq.length; j++) {
                result[i] += G[i][j] * curSeq[j];
            }
        }

        return result;
    }

    public void writeToFile(String path, String data) {
        try (FileWriter writer = new FileWriter(path, false)) {
            writer.write(data);
            writer.flush();
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
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

        private int countControlBits = 3;

        public String correctData(String data) {
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < data.length(); i += 7) {
                result.append(correctOfBits(data.substring(i, i + 7)));
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

        public String wrapperRestoreSourceString(String hammingCode) {
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < hammingCode.length(); i += 7) {
                result.append(restoreSourceString(correctOfBits(hammingCode.substring(i, i + 7))));
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

        private String correctOfBits(String hammingCode) {
            StringBuilder result = new StringBuilder(hammingCode);

            return result.toString();
        }

        private String restoreSourceString(String hammingCode) {
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

        private byte[] longToByteArray(long num) {
            byte[] longBytes = new byte[4];
            longBytes[0] = (byte) (num >>> 24);
            longBytes[1] = (byte) (num >>> 16);
            longBytes[2] = (byte) (num >>> 8);
            longBytes[3] = (byte) num;
            return longBytes;
        }

        private String defineSymbol(String symbol) {
            return symbol.equals("0") ? "1" : "0";
        }
    }
}
