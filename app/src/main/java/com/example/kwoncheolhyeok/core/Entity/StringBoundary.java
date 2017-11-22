package com.example.kwoncheolhyeok.core.Entity;

import java.io.Serializable;

/**
 * Created by gimbyeongjin on 2017. 10. 6..
 */

public class StringBoundary implements Serializable {
    String max;
    String min;

    public StringBoundary() {
    }

    public StringBoundary(String max, String min) {
        this.max = max;

        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }
}