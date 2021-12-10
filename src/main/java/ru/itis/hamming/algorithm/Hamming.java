package ru.itis.hamming.algorithm;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
            result.append(this.fixControlBits(this.setControlBits(binSymbol)));
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

        private int countControlBits = 6;

        public String correctData(String data) {
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < data.length(); i += 32 + 6) {
                result.append(correctOfBits(data.substring(i, i + 32 + 6)));
            }

            return result.toString();
        }


        public String restoreInitString(String bytes) {
            StringBuilder result = new StringBuilder();


            for (int i = 0; i < bytes.length(); i += 32) {
                int code = Integer.parseInt(bytes.substring(i, i + 32), 2);
                if (code != 10) {
                    result.append(new String(intToByteArray(code), StandardCharsets.UTF_8).replaceAll("\\u0000", ""));
                } else {
                    result.append("\n");
                }
            }

            return result.toString();
        }

        public String wrapperRestoreSourceString(String hammingCode) {
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < hammingCode.length(); i += 32 + 6) {
                result.append(restoreSourceString(correctOfBits(hammingCode.substring(i, i + 32 + 6))));
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

        public void writeToFile(String restoreInitText) {
            try(FileWriter writer = new FileWriter("./resultDecode.txt", false)) {
                writer.write(restoreInitText);
                writer.flush();
            }
            catch(IOException ex){

                System.out.println(ex.getMessage());
            }
        }

        private String correctOfBits(String hammingCode) {
            StringBuilder result = new StringBuilder(hammingCode);

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

        private byte[] intToByteArray(int num) {
            byte[] intBytes = new byte[4];
            intBytes[0] = (byte) (num >>> 24);
            intBytes[1] = (byte) (num >>> 16);
            intBytes[2] = (byte) (num >>> 8);
            intBytes[3] = (byte) num;
            return intBytes;
        }

        private String defineSymbol(String symbol) {
            return symbol.equals("0") ? "1" : "0";
        }
    }
}
