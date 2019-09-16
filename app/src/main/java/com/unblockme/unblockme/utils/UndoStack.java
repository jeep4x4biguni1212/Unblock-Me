package com.unblockme.unblockme.utils;

import com.unblockme.unblockme.core.Move;

import java.util.Observable;
import java.util.Stack;

public class UndoStack extends Observable {
    private Stack<Move> stack = new Stack<>();

    public void push(Move m) {
        this.stack.push(m);
        this.setChanged();
        this.notifyObservers();
    }

    public Move pop() {
        Move pop = this.stack.pop();
        this.setChanged();
        this.notifyObservers();
        return pop;
    }

    public int size() {
        return this.stack.size();
    }
}
