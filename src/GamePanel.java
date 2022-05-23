import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 1920;
    static final int SCREEN_HEIGHT = 1080;
    static final int UNIT_SIZE = 70;
    static final int UPDATES_PER_SEC = 60;
    static final float DIAG_CORRECTION = 0.414214f;
    static final double XY_CORRECTION = Math.sqrt((DIAG_CORRECTION * DIAG_CORRECTION) / 2);
    static final Point offset = new Point();
    static int xAim, xFire = -1;
    static int yAim, yFire = -1;
    static boolean shoot = false;
    int x = (SCREEN_WIDTH - UNIT_SIZE) / 2;
    int y = (SCREEN_HEIGHT - UNIT_SIZE) / 2;
    int health = 3;
    boolean running = false;
    Timer timer;
    Random rand;

    GamePanel() {
        rand = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new KeyControls());
        this.addMouseListener(new MouseControls());
        startGame();
    }

    public void startGame() {
        running = true;
        timer = new Timer((int)(1000/UPDATES_PER_SEC), this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {

            // draw spaceship
            g.setColor(Color.gray);
            g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
        }
    }

    public void move() {
        switch (offset.x) {
            case -1: x = x - (int)(0.25 * UNIT_SIZE); break;
            case 1: x = x + (int)(0.25 * UNIT_SIZE); break;
        }
        switch (offset.y) {
            case 1: y = y - (int)(0.25 * UNIT_SIZE); break;
            case -1: y = y + (int)(0.25 * UNIT_SIZE); break;
        }

        if (offset.x == 1 && offset.y == 1) {
            y = y + (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
            x = x - (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
        }
        if (offset.x == -1 && offset.y == -1) {
            y = y - (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
            x = x + (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
        }
        if (offset.x == -1 && offset.y == 1) {
            y = y + (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
            x = x + (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
        }
        if (offset.x == 1 && offset.y == -1) {
            y = y - (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
            x = x - (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
        }
    }

    public void aim() {

        if (shoot) {

        }
    }

    public void asteroids() {

    }

    public void checkCollisions() {

    }

    public void gameOver(Graphics g) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            asteroids();
            aim();
            checkCollisions();
        }
        repaint();
    }

    public class lasers {
        private final int LASER_LENGTH = UNIT_SIZE / 3;

        int x1, x2;
        int y1, y2;

        lasers() {
            if (xFire >= 0 && yFire >= 0) {
                x1 = x + (UNIT_SIZE / 2);
                y1 = y + (UNIT_SIZE / 2);
            }



            shoot = false;
        }
    }
}
