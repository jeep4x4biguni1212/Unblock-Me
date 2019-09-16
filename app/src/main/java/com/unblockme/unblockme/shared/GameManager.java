package com.unblockme.unblockme.shared;


import android.util.Log;

import com.unblockme.unblockme.core.BlockFactory;
import com.unblockme.unblockme.core.Grid;
import com.unblockme.unblockme.core.Move;
import com.unblockme.unblockme.utils.UndoStack;

import java.util.Observable;
import java.util.Observer;

/**
 * Cette classe fera le lien entre les differentes parties du projet
 * Exemple:
 * Incrementation du nombre de mouvement a chaque mouvement d'un bloc
 */
public class GameManager implements Observer {
    private static final GameManager instance = new GameManager();
    private Grid grid;

    private UndoStack moves;

    private int level = 1;

    private GameManager() {
    }

    public static GameManager getInstance() {
        return instance;
    }

    private void createGrid() {
        this.grid = new Grid();
        this.grid.addObserver(this);
        this.moves = new UndoStack();
    }

    private void destroyGrid() {
        this.grid = null;
    }

    public Grid grid() {
        return this.grid;
    }

    /**
     * levelX: initialise la grille du niveau X
     */
    private void level1() {
        if (this.grid != null) this.destroyGrid();
        this.createGrid();
        this.grid.putMarked();
        this.grid.put(BlockFactory.createHorizontalBlock(0, 0, 3));
        this.grid.put(BlockFactory.createHorizontalBlock(4, 3, 2));
        this.grid.put(BlockFactory.createHorizontalBlock(0, 5, 3));
        this.grid.put(BlockFactory.createVerticalBlock(5, 0, 3));
        this.grid.put(BlockFactory.createVerticalBlock(2, 1, 3));
        this.grid.put(BlockFactory.createVerticalBlock(0, 3, 2));
        this.grid.put(BlockFactory.createVerticalBlock(4, 4, 2));
        this.grid.printGrid();
    }

    private void level2() {
        if (this.grid != null) this.destroyGrid();
        this.createGrid();
        this.grid.putMarked();
        this.grid.put(BlockFactory.createHorizontalBlock(0, 3, 2));
        this.grid.put(BlockFactory.createHorizontalBlock(2, 5, 2));
        this.grid.put(BlockFactory.createVerticalBlock(1, 4, 2));
        this.grid.put(BlockFactory.createVerticalBlock(2, 1, 2));
        this.grid.put(BlockFactory.createVerticalBlock(2, 3, 2));
        this.grid.put(BlockFactory.createVerticalBlock(3, 1, 3));
        this.grid.put(BlockFactory.createVerticalBlock(4, 1, 3));
        this.grid.printGrid();
    }

    private void level3() {
        if (this.grid != null) this.destroyGrid();
        this.createGrid();
        this.grid.putMarked();
        this.grid.put(BlockFactory.createHorizontalBlock(1, 0, 2));
        this.grid.put(BlockFactory.createHorizontalBlock(3, 0, 2));
        this.grid.put(BlockFactory.createHorizontalBlock(0, 4, 3));
        this.grid.put(BlockFactory.createVerticalBlock(0, 0, 2));
        this.grid.put(BlockFactory.createVerticalBlock(2, 1, 2));
        this.grid.put(BlockFactory.createVerticalBlock(3, 2, 3));
        this.grid.put(BlockFactory.createVerticalBlock(4, 2, 3));
        this.grid.printGrid();
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        if (level > 3) this.level = 3;
        else if (level < 1) this.level = 1;
        else this.level = level;
        switch (this.level) {
            case 1:
                this.level1();
                break;
            case 2:
                this.level2();
                break;
            case 3:
                this.level3();
                break;
            default:
                break;
        }
    }

    public int getMinimalMoveNumber() {
        int ret = 0;
        switch (this.level) {
            case 1:
                ret = 15;
                break;
            case 2:
                ret = 17;
                break;
            case 3:
                ret = 15;
                break;
        }
        return ret;
    }
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Move) moves.push((Move) arg);
        Log.i("UNDO", arg.toString());
        Log.i("UNDO", "Undo size: " + this.moves.size());
    }

    /**
     * Utilite: avoir le nombre de mouvement que le joueur a effectue
     *
     * @return
     */
    public int undoStackSize() {
        return this.moves.size();
    }

    public void observeStackChange(Observer o) {
        this.moves.addObserver(o);
    }

    public void undoLastMove() {

        if (this.undoStackSize() == 0) return;
        Move pop = this.moves.pop();
        Log.i("ULM", pop.toString());
        this.grid.getMoveBoundaries(pop.getBlockId());
        if (this.grid.move(pop.getBlockId(), pop.getFrom())) {
            this.moves.pop(); // remove the unwanted move from the stack
        }

        Log.i("UNDO", "Undo size: " + this.moves.size());
    }
}
