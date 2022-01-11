package ru.evant.tetris.myref.v1;

import java.awt.*;

import static ru.evant.tetris.myref.v1.Const.ARC_RADIUS;
import static ru.evant.tetris.myref.v1.Const.BLOCK_SIZE;

public class Block {
    private int x, y;

    public Block(int x, int y) {
        setX(x);
        setY(y);
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void paint(Graphics g, int color) {
        g.setColor(new Color(color));
        g.drawRoundRect(x * BLOCK_SIZE + 1, y * BLOCK_SIZE + 1,
                BLOCK_SIZE - 2, BLOCK_SIZE - 2,
                ARC_RADIUS, ARC_RADIUS);
    }
}
