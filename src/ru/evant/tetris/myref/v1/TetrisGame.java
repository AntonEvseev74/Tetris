package ru.evant.tetris.myref.v1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import static ru.evant.tetris.myref.v1.Const.*;

public class TetrisGame {

    int gameScore = 0;
    static int[][] mine = new int[FIELD_HEIGHT + 1][FIELD_WIDTH];

    JFrame frame;
    Canvas canvasPanel = new Canvas();
    Figure figure = new Figure();

    boolean gameOver = false;

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
                if (!gameOver) {
                    if (e.getKeyCode() == DOWN) figure.drop();
                    if (e.getKeyCode() == UP) figure.rotate();
                    if (e.getKeyCode() == LEFT || e.getKeyCode() == RIGHT) figure.move(e.getKeyCode());
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


    public class Canvas extends JPanel {

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            for (int x = 0; x < FIELD_WIDTH; x++) {
                for (int y = 0; y < FIELD_HEIGHT; y++) {
                    if (mine[y][x] > 0) {
                        g.setColor(new Color(mine[y][x]));
                        g.fill3DRect(x * BLOCK_SIZE + 1, y * BLOCK_SIZE + 1, BLOCK_SIZE - 1, BLOCK_SIZE - 1, true);
                    }
                }
            }

            // нарисовать конец игры
            if (gameOver) {
                g.setColor(Color.white);
                for (int y = 0; y < GAME_OVER_MSG.length; y++) {
                    for (int x = 0; x < GAME_OVER_MSG[y].length; x++) {
                        if (GAME_OVER_MSG[y][x] == 1) g.fill3DRect(x * 11 + 10, y * 11 + 160, 10, 10, true);
                    }
                }
            } else {
                figure.paint(g);
            }
        }
    }

}
