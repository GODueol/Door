package com.example.kwoncheolhyeok.core.Util;

import com.example.kwoncheolhyeok.core.PeopleFragment.ImageAdapter;

import java.util.Comparator;
import java.util.TreeSet;

public class IndexedTreeSet<K> extends TreeSet<K> {
    public IndexedTreeSet(Comparator<ImageAdapter.Item> comparable) {
        super((Comparator<? super K>) comparable);
    }

    @Override
    public String toString() {
        StringBuilder rst = new StringBuilder();

        for (K k : this) {
            rst.append("item hash : ").append(k.hashCode()).append(",\n");
        }

        return rst.toString();
    }

    public K get(String uuid){
        for (K k : this) {
            if (k.hashCode() == uuid.hashCode()) {
                return k;
            }
        }
        return null;
    }

}
