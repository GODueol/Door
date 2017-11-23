package com.example.kwoncheolhyeok.core.Util;

import com.example.kwoncheolhyeok.core.PeopleFragment.ImageAdapter;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class IndexedTreeMap<K,V> extends TreeMap<K, V> {
    public IndexedTreeMap(Comparator<ImageAdapter.Item> comparable) {
        super((Comparator<? super K>) comparable);
    }

    public Map.Entry<K, V> getEntry(int i)
    {
        // check if negetive index provided
        Set<Entry<K,V>> entries = entrySet();
        int j = 0;
        for(Map.Entry<K, V>entry : entries)
            if(j++ == i)return entry;
        return null;

    }
}
