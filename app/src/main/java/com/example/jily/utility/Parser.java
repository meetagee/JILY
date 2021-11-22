package com.example.jily.utility;

import java.util.ArrayList;
import java.util.Arrays;

public final class Parser {

    private static final String PATTERN = "[\\[\\]\"\\s]";
    private static final String EMPTY   = "";
    private static final String LEFT    = "[";
    private static final String RIGHT   = "]";
    private static final String QUOTE   = "\"";
    private static final String COMMA   = ",";
    private static final String SPACE   = " ";

    public static ArrayList<String> getAsList(String toBeParsed) {
        String parsed = toBeParsed.replaceAll(PATTERN, EMPTY);
        ArrayList<String> parsedList = new ArrayList<>(Arrays.asList(parsed.split(COMMA)));

        return parsedList;
    }

    public static String setToString(ArrayList<String> toBeParsed) {
        StringBuilder parsed = new StringBuilder(LEFT);
        for (String item : toBeParsed) { parsed.append(QUOTE + item + QUOTE + COMMA + SPACE); }
        parsed.delete(parsed.length() - 2, parsed.length());
        parsed.append(RIGHT);

        return parsed.toString();
    }
}
