import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    // Panel settings
    static final int SCREEN_WIDTH = 1280;
    static final int SCREEN_HEIGHT = 800;
    final int UPDATES_PER_SEC = 60; // Correlates to game speed as well
    boolean running = false;
    Timer timer;

    // Ship settings
    final int MAX_LASERS = 3;
    static final int SHIP_SIZE = (int) (Math.abs(SCREEN_WIDTH - SCREEN_HEIGHT) * 0.12);
    final float DIAG_CORRECTION = 0.414214f; // ~= sqrt(2) - 1
    static final int SHIP_SPEED = (int) (0.12 * SHIP_SIZE);
    final double XY_CORRECTION = Math.sqrt((DIAG_CORRECTION * DIAG_CORRECTION) * 0.5);
    static final Point direction = new Point();
    final int[] border = { 1, 1, 1, 1 };
    int shipX = (int) ((SCREEN_WIDTH - SHIP_SIZE) * 0.5);
    int shipY = (int) ((SCREEN_HEIGHT - SHIP_SIZE) * 0.5);

    static int xFire = -1;
    static int yFire = -1;
    static boolean canShoot = false;
    int score = 0;
    // int health = 3;
    Entities.laser[] laserGun = new Entities.laser[MAX_LASERS];

    // Asteroid settings
    final int MAX_ASTEROIDS = 5;
    static final int DIRECTION_LIMIT = (int)((SCREEN_HEIGHT + SCREEN_WIDTH) * 0.25 * 0.130719);
    int ASTEROID_FREQ = 5;
    int clock = 0;
    Entities.asteroid[] asteroids;
    static Random rand;

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
        rand = new Random();
        asteroids = new Entities.asteroid[MAX_ASTEROIDS];
        ASTEROID_FREQ *= 2;
        timer = new Timer((1000/UPDATES_PER_SEC), this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {

            // draw spaceship
            g.setColor(Color.white);
            g.fillRect(shipX, shipY, SHIP_SIZE, SHIP_SIZE);

            // draw laser
            for (int i = 0; i < MAX_LASERS; i++) {
                if (laserGun[i] != null) {
                    g.setColor(Color.green);
                    g.fillOval((int)laserGun[i].getX(), (int)laserGun[i].getY(),
                            (int)laserGun[i].getWidth(), (int)laserGun[i].getHeight());
                }
            }

            // draw asteroids
            for (int i = 0; i < MAX_ASTEROIDS; i++) {
                if (asteroids[i] != null) {
                    g.setColor(Color.gray);
                    g.fillOval((int)asteroids[i].getX(), (int)asteroids[i].getY(), (int)asteroids[i].getWidth(), (int)asteroids[i].getHeight());
                }
            }
        }
    }

    public void move() {

        // moving the spaceship part 1
        switch (direction.x) {
            case -1:
                shipX = shipX - SHIP_SPEED * border[0];
                break;
            case 1:
                shipX = shipX + SHIP_SPEED * border[2];
                break;
        }
        switch (direction.y) {
            case 1:
                shipY = shipY - SHIP_SPEED * border[1];
                break;
            case -1:
                shipY = shipY + SHIP_SPEED * border[3];
                break;
        }

        /* moving the spaceship part 2:
         * checking for diagonal movement to correct for its larger move size
         */
        if (direction.x == 1 && direction.y == 1) {
            shipY = shipY + (int) (XY_CORRECTION * SHIP_SPEED) * border[1];
            shipX = shipX - (int) (XY_CORRECTION * SHIP_SPEED) * border[2];
        }
        if (direction.x == -1 && direction.y == -1) {
            shipY = shipY - (int) (XY_CORRECTION * SHIP_SPEED) * border[3];
            shipX = shipX + (int) (XY_CORRECTION * SHIP_SPEED) * border[0];
        }
        if (direction.x == -1 && direction.y == 1) {
            shipY = shipY + (int) (XY_CORRECTION * SHIP_SPEED) * border[1];
            shipX = shipX + (int) (XY_CORRECTION * SHIP_SPEED) * border[0];
        }
        if (direction.x == 1 && direction.y == -1) {
            shipY = shipY - (int) (XY_CORRECTION * SHIP_SPEED) * border[3];
            shipX = shipX - (int) (XY_CORRECTION * SHIP_SPEED) * border[2];
        }

        // moving the lasers
        for (int i = 0; i < MAX_LASERS; i++) {
            if (laserGun[i] != null) { laserGun[i].moveLaser(); }
        }

        // moving the asteroids
        for (int i = 0; i < MAX_ASTEROIDS; i++) {
            if (asteroids[i] != null) { asteroids[i].move(); }
        }

    }

    public void aim() {
        if (MouseControls.onScreen) {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

            if (canShoot) {
                for (int i = 0; i < MAX_LASERS; i++) {

                    // only allows 3 lasers to be on screen at once
                    if (laserGun[i] == null) {
                        laserGun[i] = new Entities.laser(shipX, shipY);
                        break;
                    }
                }
                canShoot = false;
            }
        }
        else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void checkCollisions() {

        // check if spaceship hit border
        if (shipX < 0) { border[0] = 0; }
        else border[0] = 1;
        if (shipY < 0) { border[1] = 0; }
        else border[1] = 1;
        if (shipX >= (SCREEN_WIDTH - SHIP_SIZE)) { border[2] = 0; }
        else border[2] = 1;
        if (shipY >= (SCREEN_HEIGHT - SHIP_SIZE)) { border[3] = 0; }
        else border[3] = 1;

        // check laser collisions with border
        for (int i = 0; i < MAX_LASERS; i++) {
            if (laserGun[i] != null) {
                if (((int)laserGun[i].getX() > (SCREEN_WIDTH - (int)laserGun[i].getWidth())) || ((int)laserGun[i].getX() < 0)) {
                    laserGun[i] = null;
                    break;
                }
                if (((int)laserGun[i].getY() > (SCREEN_HEIGHT - (int)laserGun[i].getHeight())) || ((int)laserGun[i].getY() < 0)) {
                    laserGun[i] = null;
                    break;
                }
            }
        }

        // check asteroid collisions with border
        for (int i = 0; i < MAX_ASTEROIDS; i++) {
            if (asteroids[i] != null) {
                if ((int)asteroids[i].getX() > (SCREEN_WIDTH + (int)asteroids[i].getWidth() * 2)) {
                    System.out.println("\t\tMade asteroid " + i + " null");
                    asteroids[i] = null;
                    break;
                }
                if ((int)asteroids[i].getX() < (-1 * (int)asteroids[i].getWidth() * 2)) {
                    System.out.println("\t\tMade asteroid " + i + " null");
                    asteroids[i] = null;
                    break;
                }
                if ((int)asteroids[i].getY() > SCREEN_HEIGHT + (int)asteroids[i].getHeight() * 2) {
                    System.out.println("\t\tMade asteroid " + i + " null");
                    asteroids[i] = null;
                    break;
                }
                if ((int)asteroids[i].getY() < (-1 * (int)asteroids[i].getHeight() * 2)) {
                    System.out.println("\t\tMade asteroid " + i + " null");
                    asteroids[i] = null;
                    break;
                }
            }
        }
    }

    public void asteroidClock() {
        clock++;
        if (clock % (30 * ASTEROID_FREQ) == 0) {
            clock = 0;
            for (int i = 0; i < MAX_ASTEROIDS; i++) {
                if (asteroids[i] == null) {
                    asteroids[i] = new Entities.asteroid();
                        // System.out.println("Created asteroid " + i);
                    break;
                }
                    // debug
                    else {
                        System.out.println("\t" + i + " Init: (" + asteroids[i].getX() + ", " + asteroids[i].getY() + ")" +
                                " - Mvt: (" + asteroids[i].moveX + ", " + asteroids[i].moveY + ")");
                    }

            }
        }

        if (score > 10) {
            ASTEROID_FREQ--;
        }
    }

    public void gameOver(Graphics g) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            asteroidClock();
            move();
            aim();
            checkCollisions();
        }
        repaint();
    }
}