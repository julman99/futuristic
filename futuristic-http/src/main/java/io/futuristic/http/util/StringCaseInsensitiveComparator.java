package io.futuristic.http.util;

import java.util.Comparator;

/**
 * @autor: julio
 */
public class StringCaseInsensitiveComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        if(o1 == null && o2 == null){
            return 0;
        } else if (o1 == null){
            return -1;
        } else if (o2 == null){
            return 1;
        } else {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    }
}
