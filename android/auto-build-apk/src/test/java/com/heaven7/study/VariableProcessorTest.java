package com.heaven7.study;


import com.heaven7.java.visitor.util.Map;
import com.heaven7.java.visitor.util.Map2Map;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class VariableProcessorTest {

    @Test
    public void test1(){
        HashMap<String, String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "{$a}/x");
        map.put("c", "{$b}/y");
        map.put("d", "{$c}/z");
        Map<String, String> result = new VariableProcessor(new Map2Map<>(map)).process();
        Assert.assertTrue(result.get("a").equals("1"));
        Assert.assertTrue(result.get("b").equals("1/x"));
        Assert.assertTrue(result.get("c").equals("1/x/y"));
        Assert.assertTrue(result.get("d").equals("1/x/y/z"));
    }

}
