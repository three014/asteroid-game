import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 1920;
    static final int SCREEN_HEIGHT = 1080;
    static final int UNIT_SIZE = 70;
    static final int UPDATES_PER_SEC = 60;
    static final float DIAG_CORRECTION = 0.414214f; // ~= sqrt(2) - 1
    static final double XY_CORRECTION = Math.sqrt((DIAG_CORRECTION * DIAG_CORRECTION) / 2);
    static final Point offset = new Point();
    static int xFire = -1;
    static int yFire = -1;
    static boolean shoot = false;
    int shipX = (SCREEN_WIDTH - UNIT_SIZE) / 2;
    int shipY = (SCREEN_HEIGHT - UNIT_SIZE) / 2;
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
            g.fillRect(shipX, shipY, UNIT_SIZE, UNIT_SIZE);

        }
    }

    public void move() {
        switch (offset.x) {
            case -1: shipX = shipX - (int)(0.25 * UNIT_SIZE); break;
            case 1: shipX = shipX + (int)(0.25 * UNIT_SIZE); break;
        }
        switch (offset.y) {
            case 1: shipY = shipY - (int)(0.25 * UNIT_SIZE); break;
            case -1: shipY = shipY + (int)(0.25 * UNIT_SIZE); break;
        }

        // checking for diagonal movement to correct for its larger move size
        if (offset.x == 1 && offset.y == 1) {
            shipY = shipY + (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
            shipX = shipX - (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
        }
        if (offset.x == -1 && offset.y == -1) {
            shipY = shipY - (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
            shipX = shipX + (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
        }
        if (offset.x == -1 && offset.y == 1) {
            shipY = shipY + (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
            shipX = shipX + (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
        }
        if (offset.x == 1 && offset.y == -1) {
            shipY = shipY - (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
            shipX = shipX - (int)(XY_CORRECTION * 0.25 * UNIT_SIZE);
        }
    }

    public void aim() {
        if (MouseControls.inScreen) {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
        else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void checkCollisions() {

    }

    public void gameOver(Graphics g) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            aim();
            checkCollisions();
        }
        repaint();
    }

    public class asteroid {

    }

    public class laser {
        final int LASER_SIZE = (int)(UNIT_SIZE * 0.25);

        int slope;
        int laserX;
        int laserY;

        laser() {
            // starting laser coordinates
            if (xFire >= 0 && yFire >= 0) {
                laserX = shipX + (UNIT_SIZE / 2);
                laserY = shipY + (UNIT_SIZE / 2);
            }

            // find laser slope
            slope = (yFire - laserY) / (xFire - laserX);



            shoot = false;
        }
    }
}
