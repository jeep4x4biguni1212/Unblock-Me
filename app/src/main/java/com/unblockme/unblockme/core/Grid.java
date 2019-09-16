package com.unblockme.unblockme.core;


import android.util.ArrayMap;
import android.util.Log;

import com.unblockme.unblockme.utils.Bound;
import com.unblockme.unblockme.utils.Dimension;
import com.unblockme.unblockme.utils.Orientation;
import com.unblockme.unblockme.utils.Position;

import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Cette classe gere toute la logique relie a une grille, tel que le placement des blocs,
 * le deplacement de bloc
 */
public class Grid extends Observable {

    public static final int GRID_SIZE = 6;
    public static final int MARKED_ID = -1;
    private int[][] items = new int[GRID_SIZE][GRID_SIZE];
    private Map<Integer, Block> blocks = new ArrayMap<>();
    private ArrayList<Observer> observers = new ArrayList<>();
    private int cnt = 1;
    private Bound lastBound;

    /**
     * Initialise la matrice de bloc avec des 0 (qui exprime que la case est vide).
     */
    public Grid() {

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++)
                this.items[i][j] = 0;
        }
    }

    @Override
    public synchronized void addObserver(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void notifyObservers(Object arg) {
        for (Observer o :
                this.observers) {
            o.update(this, arg);
        }
    }

    @Override
    public void notifyObservers() {
        this.notifyObservers(null);
    }

    /**
     * Print
     */
    public void printGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < GRID_SIZE; j++)
                str.append(this.items[j][i]).append("\t");
            Log.i("Grid", str.toString());
        }
    }

    /**
     * Insere n bloc dans la grille
     *
     * @param b
     * @return false si la case est occupe, true sinon
     */
    public boolean put(Block b) {
        if ((this.getIdByPosition(b.getPosition()) != 0) || (!this.set(b, cnt)))
            return false;
        this.blocks.put(cnt++, b);
        return true;
    }

    /**
     * Insere un bloc Marque
     *
     * @return false si la case est occupe, true sinon
     */
    public boolean putMarked() {
        Block b = new Block(new Dimension(1, 2), new Position(0, 2));
        if ((this.getIdByPosition(b.getPosition()) != 0) || (!this.set(b, MARKED_ID)))
            return false;
        this.blocks.put(MARKED_ID, b);
        return true;
    }

    /**
     * Attache un bloc a une ou plusieurs cases (en fonction de sa dimension)
     * Concretement: Mets l'ID du bloc en question dans les cases correspondant
     *
     * @param b
     * @param bid
     * @return
     */
    private boolean set(Block b, int bid) {
        for (int i = b.getPosition().getX(); i < b.getDimension().getWidth() + b.getPosition().getX(); i++) {
            for (int j = b.getPosition().getY(); j < b.getDimension().getLength() + b.getPosition().getY(); j++) {
                if (this.items[i][j] != 0) return false;
                this.items[i][j] = bid;
            }
        }
        return true;
    }

    /**
     * Retourne l'ID des blocs present dans la grille
     *
     * @return
     */
    public Set<Integer> getBlockIds() {
        return this.blocks.keySet();
    }


    public int getIdByPosition(Position p) {
        int i;
        try {
            i = this.items[p.getX()][p.getY()];
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            return 0;
        }
        return i;
    }

    public Block getBlockById(int id) {
        return this.blocks.get(id);
    }

    private boolean isEmpty(Position p) {
        return (this.getIdByPosition(p) != 0);
    }

    /**
     * Detache un bloc de la grille; Remets a zero les cases du bloc
     *
     * @param b
     */
    private void unset(Block b) {
        for (int i = b.getPosition().getX(); i < b.getDimension().getWidth() + b.getPosition().getX(); i++) {
            for (int j = b.getPosition().getY(); j < b.getDimension().getLength() + b.getPosition().getY(); j++)
                this.items[i][j] = 0;
        }
    }

    public Bound getMoveBoundaries(int bid) {

        Block b = this.getBlockById(bid);
        Position p = b.getPosition();

        int borne_sup = 0, borne_inf = 0;
        if (b.getOrientation() == Orientation.HORIZONTAL) {
            // Borne sup
            borne_sup = p.getX();
            for (int i = p.getX() + b.getDimension().getWidth(); i < GRID_SIZE; i++) {
                if ((this.items[i][p.getY()] != 0) && (this.items[i][p.getY()] != bid)) {
                    break;
                }
                borne_sup++;
            }
            // Borne inf
            borne_inf = p.getX();
            for (int i = p.getX() - 1; i >= 0; i--) {
                if ((this.items[i][p.getY()] != 0) && (this.items[i][p.getY()] != bid)) {
                    break;
                }
                borne_inf--;
            }
        } else {

            // Borne sup
            borne_sup = p.getY();
            for (int i = p.getY() + b.getDimension().getLength(); i < GRID_SIZE; i++) {
                if ((this.items[p.getX()][i] != 0) && (this.items[p.getX()][i] != bid)) {
                    break;
                }
                borne_sup++;
            }
            // Borne inf
            borne_inf = p.getY();
            for (int i = p.getY() - 1; i >= 0; i--) {
                if ((this.items[p.getX()][i] != 0) && (this.items[p.getX()][i] != bid)) {
                    break;
                }
                borne_inf--;
            }

        }
        this.lastBound = new Bound(borne_sup, borne_inf);
        return this.lastBound;
    }

    public boolean isValidMove(int bid, Position p) {
        Block b = this.blocks.get(bid);

        if ((p.getX() < 0) || (p.getY() < 0)) return false;
        if ((p.getX() >= GRID_SIZE) || (p.getY() >= GRID_SIZE)) return false;

        assert b != null;
        if (b.getOrientation() == Orientation.HORIZONTAL) {
            if (p.getX() > this.lastBound.getHigh()) return false;
            return p.getX() >= this.lastBound.getLow();
        } else {
            if (p.getY() > this.lastBound.getHigh()) return false;
            return p.getY() >= this.lastBound.getLow();
        }
    }

    /**
     * Deplace un bloc dont l'id est bid de sa position vers p
     * Effectue toutes les verifications si le blocs peut etre deplacer ou non
     *
     * @param bid
     * @param p
     * @return true si le bloc a ete deplace; false sinon
     */
    public boolean move(int bid, Position p) {
        Block b = this.blocks.get(bid);
        Position position = b.getPosition();
        if (p.equals(position)) return false;
        if (!this.isValidMove(bid, p)) return false;
        this.unset(b);
        b.setPosition(p);
        this.set(b, bid);

        this.setChanged();
        this.notifyObservers(new Move(bid, position, p));
        this.printGrid();

        return true;
    }

    public boolean isSolved() {
        Block b = this.getBlockById(Grid.MARKED_ID);
        boolean b1 = b.getPosition().getX() == 4;
        return b1;
    }
}
