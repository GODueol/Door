package com.example.kwoncheolhyeok.core.Util;

import com.example.kwoncheolhyeok.core.PeopleFragment.GridItem;

import java.util.Comparator;
import java.util.TreeSet;

public class IndexedTreeSet<K> extends TreeSet<K> {
    public IndexedTreeSet(Comparator<GridItem> comparable) {
        super((Comparator<? super K>) comparable);
    }

    @Override
    public String toString() {
        StringBuilder rst = new StringBuilder();
        rst.append("***treeset : \n");
        for (K k : this) {
            rst.append("item : ").append(k.toString()).append(",\n");
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
