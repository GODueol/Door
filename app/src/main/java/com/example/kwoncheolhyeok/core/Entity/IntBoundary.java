package com.example.kwoncheolhyeok.core.Entity;

/**
 * Created by gimbyeongjin on 2017. 10. 6..
 */

public class IntBoundary {
    int max;
    int min;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public IntBoundary() {
    }

    public IntBoundary(int max, int min) {
        this.max = max;
        this.min = min;
    }
}