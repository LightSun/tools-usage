package com.heaven7.study;

import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.MapResultVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.Map;
import com.heaven7.java.visitor.util.Map2Map;
import com.heaven7.java.visitor.util.Predicates;

import java.util.ArrayList;
import java.util.Comparator;
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

        final HashMap<String, String> outMap = new HashMap<>(in.toNormalMap());

        List<PairInfo> infos = VisitServices.from(in).map(new MapResultVisitor<String, String, PairInfo>() {
            @Override
            public PairInfo visit(KeyValuePair<String, String> pair, Object param) {
                List<Item> items = findVars(pair.getValue());
                PairInfo info = new PairInfo(pair, items);
                if (!Predicates.isEmpty(items)) {
                    computeDepth(info, items, 0);
                }
                return info;
            }
        }).getAsList();
        //sort with depth
        VisitServices.from(infos).sortService(new Comparator<PairInfo>() {
            @Override
            public int compare(PairInfo o1, PairInfo o2) {
                return Integer.compare(o1.variableDepth, o2.variableDepth);
            }
        }).fire(new FireVisitor<PairInfo>() {
            @Override
            public Boolean visit(PairInfo info, Object param) {
                getMappedText(info.pair.getKey(), info.pair.getValue(), info.items, mSuccess, outMap);
                return null;
            }
        });
        if(!mSuccess.get()){
            return null;
        }
        return new Map2Map<>(outMap);
    }
    private void getMappedText(String key, String value, List<Item> items, AtomicBoolean resultState, HashMap<String, String> outMap){
        for (Item item : items){
            //if previous is mapped, use directly.
            String s = outMap.get(item.getKeyword());
            if(s == null){
                s = in.get(item.getKeyword());
            }
            if(s == null){
                resultState.compareAndSet(true, false);
                System.err.println("can't resolve variable '" + item.text + "'!");
            }else {
                //s = {$a}/x
                List<Item> childItems = findVars(s);
                if(Predicates.isEmpty(childItems)){
                    value = value.replace(item.text, s);
                    //override
                    outMap.put(key, value);
                }else {
                    getMappedText(item.getKeyword(), s, childItems, resultState, outMap);
                }
            }
        }
    }

    private void computeDepth(PairInfo info, List<Item> items, int current) {
        for (Item item : items){
            String s = in.get(item.getKeyword());
            if(info.variableDepth <= current){
                info.variableDepth ++;
            }
            if(s != null){
                computeDepth(info, findVars(s), current + 1);
            }
        }
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

    private static class Item{

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
    private static class PairInfo{
        final KeyValuePair<String, String> pair;
        final List<Item> items;
        int variableDepth; //variable depth

        public PairInfo(KeyValuePair<String, String> pair, List<Item> items) {
            this.pair = pair;
            this.items = items;
        }
    }
}
