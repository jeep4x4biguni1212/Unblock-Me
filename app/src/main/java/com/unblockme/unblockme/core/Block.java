package com.unblockme.unblockme.core;


import com.unblockme.unblockme.utils.Dimension;
import com.unblockme.unblockme.utils.Orientation;
import com.unblockme.unblockme.utils.Position;

/**
 * Classe representant un bloc avec ses propriete:
 *      Dimension
 *      Position
 *      Orientation
 */
public class Block {
    private Dimension dim;
    private Position pos;

    public Block(Dimension dim, Position pos) {
        this.dim = dim;
        this.pos = pos;
    }

    public Dimension getDimension() {
        return dim;
    }

    public Position getPosition() {
        return pos;
    }

    public void setPosition(Position pos) {
        this.pos = pos;
    }

    public Orientation getOrientation() {
        return (this.dim.getLength() < this.dim.getWidth()) ?
                Orientation.HORIZONTAL : Orientation.VERTICAL;
    }
}
