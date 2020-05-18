package com.heaven7.study;

import com.heaven7.java.visitor.MapResultVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.Map;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*public*/ class Utils {

    public static Map<String, String> load(String path){
        FileInputStream in = null;
        try {
            in = new FileInputStream(path);
            Properties prop = new Properties();
            prop.load(new InputStreamReader(in));
            return VisitServices.from(prop).map2Map(new MapResultVisitor<Object, Object, String>() {
                @Override
                public String visit(KeyValuePair<Object, Object> t, Object param) {
                    return t.getKey().toString().trim();
                }
            }, new MapResultVisitor<Object, Object, String>() {
                @Override
                public String visit(KeyValuePair<Object, Object> t, Object param) {
                    return t.getValue().toString().trim();
                }
            }).get();
        } catch (Exception e) {
            System.err.println("can't load properties from path = " + path);
            return null;
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

  /*  public static void main(String[] args) {
        Pattern pat = Pattern.compile("\\{\\$[^{}]*\\}");
        testRegex(pat, "{$a}");
        testRegex(pat, "{8$a}");
        testRegex(pat, "324{$a}jkjkjk");
        testRegex(pat, "{$a}&{$b}");
    }
    private static void testRegex(Pattern pat, String str){
        Matcher matcher = pat.matcher(str);

        int end;
        int lastEnd = -1;
        while (matcher.find()){
            String text = matcher.group();
            int start = matcher.start();
            end = matcher.end();
            if (start > lastEnd) {
                System.out.println(str + ":  find text: text = " + text);
            }
            lastEnd = end;
        }
        System.out.println(str + " >>> " + pat.matcher(str).find());
    }*/
}
