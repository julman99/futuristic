package com.julman99.futuristic.http;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @autor: julio
 */
public class HttpParamsTest {

    @Test
    public void testInsensitive(){
        HttpParams p = new HttpParams();
        p.put("A", "1");
        p.put("b", "2");
        p.put("c", "3");
        p.put("C", "4");

        //Test for case-insensitive comparisons
        assertEquals("1", p.getFirst("A"));
        assertEquals("1", p.getFirst("a"));
        assertEquals("2", p.getFirst("B"));
        assertEquals("2", p.getFirst("b"));
        assertEquals("3", p.getFirst("c"));
        assertEquals("4", new ArrayList<String>(p.get("c")).get(1));
        assertEquals(4, p.size());

        //Now test that the keys are retreived in their original form
        Set<String> keys = new HashSet<>();
        keys.add("A");
        keys.add("b");
        keys.add("c");
        for(Map.Entry<String, String> entry: p.entries()){
            keys.remove(entry.getKey());
        }
        assertTrue(keys.isEmpty());
    }
}
