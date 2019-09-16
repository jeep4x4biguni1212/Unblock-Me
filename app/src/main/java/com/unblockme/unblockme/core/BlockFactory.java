package com.unblockme.unblockme.core;


import com.unblockme.unblockme.utils.Dimension;
import com.unblockme.unblockme.utils.Position;

/**
 * Classe qui gere (facilite) la creation de bloc
 */
public class BlockFactory {
    /**
     * Cree un bloc horizontal de largeur w
     *
     * @param x position suivant X
     * @param y position suivant Y
     * @param w largeur du bloc (suivant X)
     * @return Block
     */
    public static Block createHorizontalBlock(int x, int y, int w) {
        return new Block(new Dimension(1, w), new Position(x, y));
    }

    /**
     * Cree un bloc vertical de longeur l
     *
     * @param x position suivant X
     * @param y position suivant Y
     * @param l longueur du bloc (suivant Y)
     * @return
     */
    public static Block createVerticalBlock(int x, int y, int l) {
        return new Block(new Dimension(l, 1), new Position(x, y));
    }
}
