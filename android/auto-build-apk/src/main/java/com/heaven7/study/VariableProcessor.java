package com.heaven7.study;

import com.heaven7.java.visitor.MapFireVisitor;
import com.heaven7.java.visitor.MapPredicateVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.Map;
import com.heaven7.java.visitor.util.Map2Map;
import com.heaven7.java.visitor.util.Predicates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//a={$b}
/*public*/ final class VariableProcessor {

    private static final boolean DEBUG = true;
    private static final Pattern PAT_VAR = Pattern.compile("\\{\\$[^{}]*\\}");
    private final Map<String, String> in;

    public VariableProcessor(Map<String, String> in) {
        this.in = in;
    }

    /** null means failed */
    public Map<String, String> process(){
        final AtomicBoolean mSuccess = new AtomicBoolean(true);
        HashMap<String, String> outMap = new HashMap<>(in.toNormalMap());
        VisitServices.from(in).fire(new MapFireVisitor<String, String>() {
            @Override
            public Boolean visit(KeyValuePair<String, String> pair, Object param) {
                String value = pair.getValue();
                List<Item> items = findVars(value);
                if(!Predicates.isEmpty(items)){
                    for (Item item : items){
                        String s = in.get(item.getKeyword());
                        if(s == null){
                            mSuccess.compareAndSet(true, false);
                            System.err.println("can't resolve variable '" + item.text + "'!");
                        }else {
                            value = value.replace(item.text, s);
                        }
                    }
                    //override
                    outMap.put(pair.getKey(), value);
                }
                return null;
            }
        });
        if(!mSuccess.get()){
            return null;
        }
        return new Map2Map<>(outMap);
    }

    private static List<Item> findVars(String value) {
        final List<Item> items = new ArrayList<>();
        Matcher matcher = PAT_VAR.matcher(value);
        int end;
        int lastEnd = -1;
        while (matcher.find()){
            String text = matcher.group();
            int start = matcher.start();
            end = matcher.end();
            if (start > lastEnd) {
                items.add(new Item(start, end, text));
                if(DEBUG){
                    System.out.println(value + ":  find text: text = " + text);
                }
            }
            lastEnd = end;
        }
        return items;
    }

    public static class Item{

        public final int start;
        public final int end;
        public final String text;

        public Item(int start, int end, String text) {
            this.start = start;
            this.end = end;
            this.text = text;
        }

        public String getKeyword(){
            //{$xxx} -> xxx
            return text.substring(2, text.length()-1);
        }
    }

}
