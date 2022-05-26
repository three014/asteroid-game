import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    // Panel settings
    static final int SCREEN_WIDTH = 1920;
    static final int SCREEN_HEIGHT = 1080;
    final int UPDATES_PER_SEC = 60; // Correlates to game speed as well
    boolean running = false;
    Timer timer;

    // Ship settings
    static final int SHIP_SIZE = 70;
    final float DIAG_CORRECTION = 0.414214f; // ~= sqrt(2) - 1
    final int MAX_LASERS = 3;
    final int LASER_SIZE = (int) (SHIP_SIZE * 0.25);

    static final int SHIP_SPEED = (int)(0.12 * SHIP_SIZE);
    final double XY_CORRECTION = Math.sqrt((DIAG_CORRECTION * DIAG_CORRECTION) / 2);
    static final Point direction = new Point();
    final int[] border = { 1, 1, 1, 1 };
    int shipX = (SCREEN_WIDTH - SHIP_SIZE) / 2;
    int shipY = (SCREEN_HEIGHT - SHIP_SIZE) / 2;

    static int xFire = -1;
    static int yFire = -1;
    static boolean canShoot = false;
    int score = 0;
    // int health = 3;
    laser[] laserGun = new laser[MAX_LASERS];

    // Asteroid settings
    final int MAX_ASTEROIDS = 10;
    int ASTEROID_FREQ = 3;
    int clock = 0;
    asteroid[] asteroids;
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
        asteroids = new asteroid[MAX_ASTEROIDS];
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
                    g.fillOval(laserGun[i].laserX, laserGun[i].laserY, LASER_SIZE, LASER_SIZE);
                }
            }

            // draw asteroids
            for (int i = 0; i < MAX_ASTEROIDS; i++) {
                if (asteroids[i] != null) {
                    g.setColor(Color.gray);
                    g.fillOval(asteroids[i].locX, asteroids[i].locY, asteroids[i].SIZE, asteroids[i].SIZE);
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
                        laserGun[i] = new laser(shipX, shipY);
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
                if ((laserGun[i].laserX >= (SCREEN_WIDTH - LASER_SIZE)) || (laserGun[i].laserX < 0)) {
                    laserGun[i] = null;
                    break;
                }
                if ((laserGun[i].laserY >= (SCREEN_HEIGHT - LASER_SIZE)) || (laserGun[i].laserY < 0)) {
                    laserGun[i] = null;
                    break;
                }
            }
        }

        // check asteroid collisions with border
        for (int i = 0; i < MAX_ASTEROIDS; i++) {
            if (asteroids[i] != null) {
                if (asteroids[i].locX > (SCREEN_WIDTH + asteroids[i].SIZE * 2)) {
                    System.out.println("\t\tMade asteroid " + i + " null");
                    asteroids[i] = null;
                    break;
                }
                if (asteroids[i].locX < (-1 * asteroids[i].SIZE * 2)) {
                    System.out.println("\t\tMade asteroid " + i + " null");
                    asteroids[i] = null;
                    break;
                }
                if (asteroids[i].locY > SCREEN_HEIGHT + asteroids[i].SIZE * 2) {
                    System.out.println("\t\tMade asteroid " + i + " null");
                    asteroids[i] = null;
                    break;
                }
                if (asteroids[i].locY < (-1 * asteroids[i].SIZE * 2)) {
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
                    asteroids[i] = new asteroid();
                        // System.out.println("Created asteroid " + i);
                    break;
                }
                    // debug
                    else {
                        System.out.println("\t" + i + " Init: (" + asteroids[i].locX + ", " + asteroids[i].locY + ") - Mvt: (" + asteroids[i].moveX + ", " + asteroids[i].moveY + ")");
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

    public static class asteroid {
        public final int SIZE = determineSize();

        public int locX, locY;
        public final double moveX, moveY;

        asteroid() {

            final double SPEED_CONSTANT = (int) (SHIP_SPEED * 0.9);
            final double ASTEROID_SPEED = ( ( (double) SHIP_SIZE / (double) SIZE ) * SPEED_CONSTANT );

            int dirX, dirY;
            int randomFactorX = rand.nextInt(3);
            int randomFactorY;
            double absV;

            switch(randomFactorX) {
                case 0:
                    locX = rand.nextInt(SIZE) - SIZE;
                    dirX = rand.nextInt(500);
                    randomFactorY = 2;
                    break;
                case 1:
                    locX = rand.nextInt(SIZE) + SCREEN_WIDTH;
                    dirX = rand.nextInt(500) - 500;
                    randomFactorY = 2;
                    break;
                case 2:
                    locX = rand.nextInt(SCREEN_WIDTH - 100) + 50;
                    dirX = rand.nextInt(1000) - 500;
                    randomFactorY = rand.nextInt(2);
                    break;
                default:
                    locX = -SIZE;
                    dirX = 500;
                    randomFactorY = 0;
            }
            switch(randomFactorY) {
                case 0:
                    locY = rand.nextInt(SIZE) - SIZE;
                    dirY = rand.nextInt(500);
                    break;
                case 1:
                    locY = rand.nextInt(SIZE) + SCREEN_HEIGHT;
                    dirY = rand.nextInt(500) - 500;
                    break;
                case 2:
                    locY = rand.nextInt(SCREEN_HEIGHT - 100) + 50;
                    dirY = rand.nextInt(1000) - 500;
                    break;
                default:
                    locY = -SIZE;
                    dirY = 500;
            }
                // More debugging
                // System.out.println("(" + dirX + ", " + dirY + ")");

            absV = Math.sqrt( Math.pow( dirX, 2.0 ) + Math.pow( dirY, 2.0 ) );

            moveX = ( ( (double) dirX ) / absV ) * ASTEROID_SPEED;
            moveY = ( ( (double) dirY ) / absV ) * ASTEROID_SPEED;
        }

        void move() {
            locX += (int) moveX;
            locY += (int) moveY;
        }

        int determineSize() {

            int chance = rand.nextInt(100);

            if (chance < 60) {
                return SHIP_SIZE;
            }
            else if (chance < 80) {
                return (int) (SHIP_SIZE * 1.25);
            }
            else if (chance < 90) {
                return (int) (SHIP_SIZE * 0.4);
            }
            else {
                return SHIP_SIZE * 2;
            }
        }


    }

    public static class laser {
        public int laserY, laserX;
        private int moveX, moveY;

        laser(int X, int Y) {

            // see if it's okay to shoot a laser in the first place
            if (canShoot) {

                final int LASER_SPEED = SHIP_SPEED * 3;

                int aimX, aimY;
                double absV;

                // starting laser coordinates
                laserX = X + (SHIP_SIZE / 2);
                laserY = Y + (SHIP_SIZE / 2) ;
                aimX = xFire;
                aimY = yFire;

                // find normalizing factor for unit vector of laser trajectory
                absV = Math.sqrt( ( ( aimX - X ) * ( aimX - X ) ) + ( ( aimY - Y ) * ( aimY - Y ) ) );

                    // find direction for vector [OLD CODE, I originally misunderstood some math]
                    /*
                    if ( (aimY - Y) < 0) { directionY = -1; }
                    else if ( (aimY - Y) == 0) { directionY = 0; }
                    else { directionY = 1; }

                    if ( (aimX - X) < 0) { directionX = -1; }
                    else if ( (aimX - X) == 0) { directionX = 0; }
                    else { directionX = 1; }
                     */

                // combine into actual laser beam movement
                moveX = (int) ( ( (double) ( aimX - X ) / absV ) * LASER_SPEED );
                moveY = (int) ( ( (double) ( aimY - Y ) / absV ) * LASER_SPEED );

                    // Debugging
                    //System.out.println("Normalizing factor: " + absV);
                    //System.out.println("(" + aimX + ", " + aimY + ")");
                    //System.out.println("(" + directionX + ", " + directionY + ")");
            }

            canShoot = false;
        }

        void moveLaser() {
            laserX += moveX;
            laserY += moveY;
            // laserX += 10;
            // laserY += 10;
        }
    }
}
