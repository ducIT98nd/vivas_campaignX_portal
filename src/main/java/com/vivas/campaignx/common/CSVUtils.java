package com.vivas.campaignx.common;

import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DOUBLE_QUOTES = '"';
    private static final char DEFAULT_QUOTE_CHAR = DOUBLE_QUOTES;
    private static final String NEW_LINE = "\n";

    private static boolean isMultiLine = false;
    private static String pendingField = "";
    private static String[] pendingFieldLine = new String[]{};

    public static void main(String[] args) throws Exception {

        // loads CSV file from the resource folder.
        parseCsv("");
    }

    public static void parseCsv(String filePath) {
        File file = Paths.get(filePath).toFile();

        List<String[]> result = new ArrayList<>();
        try {
            result = readFile(file, 0);
            int listIndex = 0;
            for (String[] arrays : result) {
                System.out.println("\nString[" + listIndex++ + "] : " + Arrays.toString(arrays));

                int index = 0;
                for (String array : arrays) {
                    System.out.println(index++ + " : " + array);
                }
            }

//            List<String> list = result.stream()
//                    .flatMap(Arrays::stream)
//                    .collect(Collectors.toList());
//            System.out.println(list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int countTotalNumberInFile(String filePath) {
        File file = Paths.get(filePath).toFile();

        List<String[]> result = new ArrayList<>();
        try {
            result = readFile(file, 0);

            List<String> list = result.stream()
                    .flatMap(Arrays::stream)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());

            return list.size();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static List<String[]> readFile(File csvFile, int skipLine)
            throws Exception {

        List<String[]> result = new ArrayList<>();
        int indexLine = 1;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            String line;
            while ((line = br.readLine()) != null) {

                if (indexLine++ <= skipLine) {
                    continue;
                }

                String[] csvLineInArray = parseLine(line);

                if (isMultiLine) {
                    pendingFieldLine = joinArrays(pendingFieldLine, csvLineInArray);
                } else {

                    if (pendingFieldLine != null && pendingFieldLine.length > 0) {
                        // joins all fields and add to list
                        result.add(joinArrays(pendingFieldLine, csvLineInArray));
                        pendingFieldLine = new String[]{};
                    } else {
                        // if dun want to support multiline, only this line is required.
                        result.add(csvLineInArray);
                    }

                }


            }
        }

        return result;
    }

    public static String[] parseLine(String line) throws Exception {
        return parseLine(line, DEFAULT_SEPARATOR);
    }

    public static String[] parseLine(String line, char separator) throws Exception {
        return parse(line, separator, DEFAULT_QUOTE_CHAR).stream().toArray(String[]::new);
    }

    private static List<String> parse(String line, char separator, char quoteChar)
            throws Exception {

        List<String> result = new ArrayList<>();

        boolean inQuotes = false;
        boolean isFieldWithEmbeddedDoubleQuotes = false;

        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {

            if (c == DOUBLE_QUOTES) {               // handle embedded double quotes ""
                if (isFieldWithEmbeddedDoubleQuotes) {

                    if (field.length() > 0) {       // handle for empty field like "",""
                        field.append(DOUBLE_QUOTES);
                        isFieldWithEmbeddedDoubleQuotes = false;
                    }

                } else {
                    isFieldWithEmbeddedDoubleQuotes = true;
                }
            } else {
                isFieldWithEmbeddedDoubleQuotes = false;
            }

            if (isMultiLine) {                      // multiline, add pending from the previous field
                field.append(pendingField).append(NEW_LINE);
                pendingField = "";
                inQuotes = true;
                isMultiLine = false;
            }

            if (c == quoteChar) {
                inQuotes = !inQuotes;
            } else {
                if (c == separator && !inQuotes) {  // if find separator and not in quotes, add field to the list
                    result.add(field.toString());
                    field.setLength(0);             // empty the field and ready for the next
                } else {
                    field.append(c);                // else append the char into a field
                }
            }

        }

        //line done, what to do next?
        if (inQuotes) {
            pendingField = field.toString();        // multiline
            isMultiLine = true;
        } else {
            result.add(field.toString());           // this is the last field
        }

        return result;

    }

    private static String[] joinArrays(String[] array1, String[] array2) {
        return Stream.concat(Arrays.stream(array1), Arrays.stream(array2))
                .toArray(String[]::new);
    }
}
