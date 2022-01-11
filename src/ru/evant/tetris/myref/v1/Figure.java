package ru.evant.tetris.myref.v1;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static ru.evant.tetris.myref.v1.Const.*;

public class Figure {

    int[][] mine = TetrisGame.mine;

    private final ArrayList<Block> figure = new ArrayList<>();
    private final int[][] shape = new int[4][4];
    private final int size;
    private final int color;
    private int x = 3, y = 0;

    public Figure() {
        Random random = new Random();
        int type = random.nextInt(SHAPES.length);
        size = SHAPES[type][4][0];
        color = SHAPES[type][4][1];

        if (size == 4) y = -1;

        for (int i = 0; i < size; i++) {
            System.arraycopy(SHAPES[type][i], 0, shape[i], 0, SHAPES[type][i].length);
        }
        createFromShape();
    }

    // создать фигуру
    public void createFromShape() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (shape[y][x] == 1) figure.add(new Block(x + this.x, y + this.y));
            }
        }
    }

    // сбросить
    public void drop() {
        while (!isTouchGround()) stepDown();
    }

    // повернуть
    public void rotate() {
        for (int i = 0; i < size / 2; i++) {
            for (int j = i; j < size - 1 - i; j++) {
                int tmp = shape[size - 1 - j][i];
                shape[size - 1 - j][i] = shape[size - 1 - i][size - 1 - j];
                shape[size - 1 - i][size - 1 - j] = shape[j][size - 1 - i];
                shape[j][size - 1 - i] = shape[i][j];
                shape[i][j] = tmp;
            }
            if (!isWrongPosition()) {
                figure.clear();
                createFromShape();
            }
        }
    }

    private boolean isWrongPosition() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (shape[y][x] == 1) {
                    // соприкосновение с стенами стакана и дном стакана
                    if (y + this.y < 0) return true;
                    if (x + this.x < 0 || x + this.x > FIELD_WIDTH - 1) return true;

                    //соприкосновение с фигурами
                    if (mine[y + this.y][x + this.x] > 0) return true;
                }
            }
        }
        return false;
    }

    // передвинуть
    public void move(int direction) {
        if (!isTouchWall(direction)) {
            int dx = direction - 38;
            for (Block block : figure) {
                block.setX(block.getX() + dx);
            }
            x += dx;
        }
    }

    // касание со стенами
    private boolean isTouchWall(int direction) {
        for (Block block : figure) {
            if (direction == LEFT && (block.getX() == 0 || mine[block.getY()][block.getX() - 1] > 0)) return true;
            if (direction == RIGHT && (block.getX() == FIELD_WIDTH - 1 || mine[block.getY()][block.getX() + 1] > 0))
                return true;
        }
        return false;
    }

    // косание фигурой дна
    public boolean isTouchGround() {
        for (Block block : figure) {
            if (mine[block.getY() + 1][block.getX()] > 0) return true;
        }
        return false;
    }

    // оставить фигуру на дне
    public void leaveOnTheGround() {
        for (Block block : figure) {
            mine[block.getY()][block.getX()] = color;
        }
    }

    // конец игры (пересечение с землей или с фигурами)
    public boolean isCrossGround() {
        for (Block block : figure) {
            if (mine[block.getY()][block.getX()] > 0) return true;
        }
        return false;
    }

    // опустить фигуру на одну линию
    public void stepDown() {
        for (Block block : figure) block.setY(block.getY() + 1);
        y++;
    }

    // нарисовать фигуру
    public void paint(Graphics g) {
        for (Block block : figure) {
            block.paint(g, color);
        }
    }
}
