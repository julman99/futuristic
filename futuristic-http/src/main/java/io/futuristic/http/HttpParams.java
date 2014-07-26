package io.futuristic.http;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultimap;
import io.futuristic.http.util.StringCaseInsensitiveComparator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

/**
 * @autor: julio
 */
public class HttpParams implements Multimap<String, String> {

    private static final Comparator<String> KEY_COMPARATOR = new StringCaseInsensitiveComparator();
    private static final Comparator<String> VALUES_COMPARATOR = Comparator.naturalOrder();

    private Multimap<String, String> params = TreeMultimap.create(KEY_COMPARATOR, VALUES_COMPARATOR);

    public HttpParams(){

    }

    @Override
    public int size() {
        return params.size();
    }

    @Override
    public boolean isEmpty() {
        return params.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return params.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return params.containsValue(value);
    }

    @Override
    public boolean containsEntry(Object key, Object value) {
        return params.containsEntry(key, value);
    }

    @Override
    public boolean put(String key, String value) {
        return params.put(key, value);
    }

    public boolean put(String key, Object value) {
        return params.put(key, value.toString());
    }

    @Override
    public boolean remove(Object key, Object value) {
        return params.remove(key, value);
    }

    @Override
    public boolean putAll(String key, Iterable<? extends String> values) {
        return params.putAll(key, values);
    }

    @Override
    public boolean putAll(Multimap<? extends String, ? extends String> multimap) {
        if(multimap != null){
            return params.putAll(multimap);
        } else {
            return false;
        }

    }

    public boolean putAll(Map<String, String> values){
        boolean modified = false;
        for(Map.Entry<String, String> entry: values.entrySet()){
            modified |= this.put(entry.getKey(), entry.getValue());
        }
        return modified;
    }

    @Override
    public Collection<String> replaceValues(String key, Iterable<? extends String> values) {
        return params.replaceValues(key, values);
    }

    @Override
    public Collection<String> removeAll(Object key) {
        return params.removeAll(key);
    }

    @Override
    public void clear() {
        params.clear();
    }

    @Override
    public Collection<String> get(String key) {
        return params.get(key);
    }

    public String getFirst(String key){
        Collection<String> res = get(key);
        if(res.isEmpty()){
            return null;
        } else {
            return res.iterator().next();
        }
    }

    @Override
    public Set<String> keySet() {
        return params.keySet();
    }

    @Override
    public Multiset<String> keys() {
        return params.keys();
    }

    @Override
    public Collection<String> values() {
        return params.values();
    }

    @Override
    public Collection<Map.Entry<String, String>> entries() {
        return params.entries();
    }

    @Override
    public Map<String, Collection<String>> asMap() {
        return params.asMap();
    }

    public String toUrlEncodedString(){
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for(Map.Entry<String, String> entry: this.entries()){
            String encodedValue = null;
            try {
                if(i > 0){
                    builder.append("&");
                }
                encodedValue = URLEncoder.encode(entry.getValue(), "UTF-8");
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(encodedValue);
                i++;
            } catch (UnsupportedEncodingException e) {
                //nothing because UTF-8 is hardcoded
            }
        }

        return builder.toString();
    }
}
