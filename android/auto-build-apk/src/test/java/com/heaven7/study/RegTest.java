package com.heaven7.study;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest {

    public static void main(String[] args) {
        Pattern pat = Pattern.compile("\\{\\$[^{}]*\\}");
        testRegex(pat, "{$a}");
        testRegex(pat, "{8$a}");
        testRegex(pat, "324{$a}jkjkjk");
        testRegex(pat, "{$a}&{$b}");
    }

    private static void testRegex(Pattern pat, String str) {
        Matcher matcher = pat.matcher(str);

        int end;
        int lastEnd = -1;
        while (matcher.find()) {
            String text = matcher.group();
            int start = matcher.start();
            end = matcher.end();
            if (start > lastEnd) {
                System.out.println(str + ":  find text: text = " + text);
            }
            lastEnd = end;
        }
        System.out.println(str + " >>> " + pat.matcher(str).find());
    }
}

