package ru.evant.tetris.myref.v3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import static ru.evant.tetris.myref.v3.Const.*;

public class TetrisGame {

    int gameScore = 0;
    static int[][] mine = new int[FIELD_HEIGHT + 1][FIELD_WIDTH];

    JFrame frame;
    Canvas canvasPanel = new Canvas();
    static Figure figure = new Figure();

    static boolean gameOver = false;
    static boolean gameStart = false;

    public static void main(String[] args) {
        new TetrisGame().startGame();
    }

    private void startGame() {
        frame = new JFrame(TITLE_OF_PROGRAM);
        initFrame();
    }

    private void initFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_WIDTH * BLOCK_SIZE + FIELD_DX, FIELD_HEIGHT * BLOCK_SIZE + FIELD_DY);
        frame.setLocation(START_LOCATION, START_LOCATION);
        frame.setResizable(false);

        canvasPanel.setBackground(Color.black);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameStart) {
                    if (e.getKeyCode() == KeyEvent.VK_S) {
                        gameStart = true;
                        gameOver = false;
                    }
                }
                if (!gameOver && gameStart) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) figure.drop();
                    if (e.getKeyCode() == KeyEvent.VK_UP) figure.rotate();
                    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)
                        figure.move(e.getKeyCode());
                }
                canvasPanel.repaint();
            }
        });

        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.setVisible(true);

        Arrays.fill(mine[FIELD_HEIGHT], 1); // дно стакана

        // главый цикл игры
        while (!gameOver) {

            try {
                Thread.sleep(SHOW_DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }

            canvasPanel.repaint();

            if (gameStart) {
                if (figure.isTouchGround()) {
                    figure.leaveOnTheGround();
                    checkFillingLine();
                    figure = new Figure();
                    gameOver = figure.isCrossGround();
                } else {
                    figure.stepDown();
                }
            }
        }
    }

    // заполнение строки и начисление очков
    private void checkFillingLine() {
        int row = FIELD_HEIGHT - 1;
        int countFillRows = 0;
        while (row > 0) {
            int filled = 1;

            for (int col = 0; col < FIELD_WIDTH; col++) {
                filled *= Integer.signum(mine[row][col]);
            }

            if (filled > 0) {
                countFillRows++;
                for (int i = row; i > 0; i--) {
                    System.arraycopy(mine[i - 1], 0, mine[i], 0, FIELD_WIDTH);
                }
            } else {
                row--;
            }
        }

        if (countFillRows > 0) {
            gameScore += SCORES[countFillRows - 1];
            frame.setTitle(TITLE_OF_PROGRAM + " : " + gameScore);
        }
    }
}
