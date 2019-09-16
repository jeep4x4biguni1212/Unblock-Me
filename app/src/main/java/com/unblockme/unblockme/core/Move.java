package com.unblockme.unblockme.core;

import com.unblockme.unblockme.utils.Position;

public class Move {
    private Position from;
    private Position to;
    private int blockId;

    public Move(int block_id, Position from, Position to) {
        this.from = from;
        this.to = to;
        this.blockId = block_id;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public int getBlockId() {
        return blockId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(this.blockId)
                .append(";\tFROM").append(this.from)
                .append(";\tTO").append(this.to);
        return sb.toString();
    }
}
