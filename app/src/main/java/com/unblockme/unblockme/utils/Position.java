package com.unblockme.unblockme.utils;

public class Position {
    private int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        Position p = (Position) obj;
        return (p.getX() == this.getX()) && (p.getY() == this.getY());
    }
}
