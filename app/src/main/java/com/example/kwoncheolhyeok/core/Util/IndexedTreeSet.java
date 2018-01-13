package com.example.kwoncheolhyeok.core.Util;

import com.example.kwoncheolhyeok.core.PeopleFragment.ImageAdapter;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class IndexedTreeSet<K,V> extends TreeSet<K> {
    public IndexedTreeSet(Comparator<ImageAdapter.Item> comparable) {
        super((Comparator<? super K>) comparable);
    }

    @Override
    public String toString() {
        String rst = "";

        Iterator it  = iterator();
        while ( it.hasNext() ) {
            K k = (K)it.next();
            rst += "item hash : " +  k.hashCode() + ",\n";
        }

        return rst;
    }

    public K get(String uuid){
        Iterator it  = iterator();
        while ( it.hasNext() ) {
            K k = (K)it.next();
            if(k.hashCode() == uuid.hashCode()){
                return k;
            }
        }
        return null;
    }

}
