package ru.evant.tetris.learncode;

/* GeekBrains. Сергей Ирюпин */

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TetrisGame {

    public final static String TITLE_OF_PROGRAM = "Tetris";
    public final static int BLOCK_SIZE = 25; // размер блока
    public final static int ARC_RADIUS = 6;

    public final static int FIELD_WIDTH = 10;
    public final static int FIELD_HEIGHT = 18;
    public final static int START_LOCATION = 180;
    public final static int FIELD_DX = 17; // размер отступа (ширина правой и левой рамки окна)
    public final static int FIELD_DY = 40; // размер отступа (высота верхней панели с названием программы)

    public final static int LEFT = 37;
    public final static int UP = 38;
    public final static int RIGHT = 39;
    public final static int DOWN = 40;

    public final static int SHOW_DELAY = 350; // задуржка анимации

    public final static int[][][] SHAPES = {
            {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {4, 0x00f0f0}}, // I
            {{0, 0, 0, 0}, {0, 1, 1, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}, {4, 0xf0f000}}, // O
            {{1, 0, 0, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0x0000f0}}, // J
            {{0, 0, 1, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xf0a0f0}}, // L
            {{0, 1, 1, 0}, {1, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0x00f000}}, // S
            {{1, 1, 1, 0}, {0, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xa000f0}}, // T
            {{1, 1, 0, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xf00000}}  // Z
    };

    public final static int[] SCORES = {100, 300, 700, 1500};

    public final static int[][] GAME_OVER_MSG = {
            {0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0},
            {1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1},
            {1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1},
            {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0},
            {0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0},
            {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0},
            {1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0},
            {1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0},
            {0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0},
    };

    int gameScore = 0;
    int[][] mine = new int[FIELD_HEIGHT + 1][FIELD_WIDTH];

    JFrame frame;
    Canvas canvasPanel = new Canvas();
    Figure figure = new Figure();
    Random random = new Random();

    boolean gameOver = false;


    public static void main(String[] args) {
        new TetrisGame().go();
    }

    private void go() {
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

//}


    class Figure {

        private Random random = new Random();
        private ArrayList<Block> figure = new ArrayList<>();
        private int[][] shape = new int[4][4];
        private int type, size, color;
        private int x = 3, y = 0;

        public Figure() {
            type = random.nextInt(SHAPES.length);
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


    class Block {
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
