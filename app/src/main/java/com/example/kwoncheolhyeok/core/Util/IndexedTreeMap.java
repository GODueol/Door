package com.example.kwoncheolhyeok.core.Util;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.kwoncheolhyeok.core.PeopleFragment.ImageAdapter;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

public class IndexedTreeMap<K,V> extends TreeMap<K, V> {
    public IndexedTreeMap(Comparator<ImageAdapter.Item> comparable) {
        super((Comparator<? super K>) comparable);
    }

    public Map.Entry<K, V> getEntry(String uuid){
        Set<Entry<K, V>> entries = entrySet();
        for(Map.Entry<K, V>entry : entries)
            if(entry.getValue().equals(uuid))return entry;
        return null;
    }

}
