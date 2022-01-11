package ru.evant.tetris.myref.v3;

import javax.swing.*;
import java.awt.*;

import static ru.evant.tetris.myref.v3.Const.*;

public class Canvas extends JPanel {

    static boolean play = false;

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (!TetrisGame.gameStart) {
            g.setColor(Color.white);
            for (int y = 0; y < GAME_START.length; y++) {
                for (int x = 0; x < GAME_START[y].length; x++) {
                    if (GAME_START[y][x] == 1) g.fill3DRect(x * 11 + 10, y * 11 + 160, 10, 10, true);
                }
            }
        } else {
            TetrisGame.figure.paint(g);
        }

        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_HEIGHT; y++) {
                if (TetrisGame.mine[y][x] > 0) {
                    g.setColor(new Color(TetrisGame.mine[y][x]));
                    g.fill3DRect(x * BLOCK_SIZE + 1, y * BLOCK_SIZE + 1, BLOCK_SIZE - 1, BLOCK_SIZE - 1, true);
                }
            }
        }

        // нарисовать конец игры
            if (TetrisGame.gameOver) {
                g.setColor(Color.white);
                for (int y = 0; y < GAME_OVER_MSG.length; y++) {
                    for (int x = 0; x < GAME_OVER_MSG[y].length; x++) {
                        if (GAME_OVER_MSG[y][x] == 1) g.fill3DRect(x * 11 + 10, y * 11 + 160, 10, 10, true);
                    }
                }
            } else {
                TetrisGame.figure.paint(g);
            }

    }
}
