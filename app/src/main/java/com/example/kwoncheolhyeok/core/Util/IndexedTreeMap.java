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

    public Map.Entry<K, V> getEntry(int i)
    {
        // check if negetive index provided
        Set<Entry<K, V>> entries = entrySet();
        int j = 0;
        for(Map.Entry<K, V>entry : entries)
            if(j++ == i)return entry;
        return null;

    }

    public Map.Entry<K, V> getEntry(String uuid){
        Set<Entry<K, V>> entries = entrySet();
        for(Map.Entry<K, V>entry : entries)
            if(entry.getValue().equals(uuid))return entry;
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeItem(final V uuid) {
        Set<Entry<K, V>> entries = entrySet();
        entries.removeIf(new Predicate<Entry<K, V>>() {
            @Override
            public boolean test(Entry<K, V> kvEntry) {
                if(kvEntry.getValue().equals(uuid)) return true;
                return false;
            }
        });
    }
}
