package com.unblockme.unblockme.utils;


public class Bound {
    private int high, low;

    public Bound(int high, int low) {
        this.high = high;
        this.low = low;
    }

    public int getHigh() {
        return high;
    }

    public int getLow() {
        return low;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("High:").append(high).append(";\tLow:").append(low).append('\n');
        return sb.toString();
    }
}
