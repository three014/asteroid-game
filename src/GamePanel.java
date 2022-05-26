import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    // Panel settings
    static final int SCREEN_WIDTH = 1920;
    static final int SCREEN_HEIGHT = 1080;
    static final int UPDATES_PER_SEC = 60; // Correlates to game speed as well
    boolean running = false;

    // Ship settings
    static final int SHIP_SIZE = 70;
    static final float DIAG_CORRECTION = 0.414214f; // ~= sqrt(2) - 1
    final int MAX_LASERS = 3;

    static final int SHIP_SPEED = (int)(0.12 * SHIP_SIZE);
    final double XY_CORRECTION = Math.sqrt((DIAG_CORRECTION * DIAG_CORRECTION) / 2);
    static final Point direction = new Point();
    int shipX = (SCREEN_WIDTH - SHIP_SIZE) / 2;
    int shipY = (SCREEN_HEIGHT - SHIP_SIZE) / 2;

    static int xFire = -1;
    static int yFire = -1;
    static boolean CanShoot = false;
    laser[] laserGun = new laser[MAX_LASERS];
    // int health = 3;
    Timer timer;

    asteroid a;
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
        for (int i = 0; i < MAX_LASERS; i++) {
            laserGun[i] = null; // slightly redundant, but ensures they all start as null
        }
        rand = new Random();
        a = new asteroid(); // FIXME
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
                    g.fillOval(laserGun[i].laserX, laserGun[i].laserY, laserGun[i].LASER_SIZE, laserGun[i].LASER_SIZE);
                }
            }

            // draw asteroids
            if (a != null) {
                g.setColor(Color.gray);
                g.fillOval(a.locX, a.locY, a.SIZE, a.SIZE);
            }

        }
    }

    public void move() {

        // moving the spaceship part 1
        switch (direction.x) {
            case -1:
                shipX = shipX - SHIP_SPEED;
                break;
            case 1:
                shipX = shipX + SHIP_SPEED;
                break;
        }
        switch (direction.y) {
            case 1:
                shipY = shipY - SHIP_SPEED;
                break;
            case -1:
                shipY = shipY + SHIP_SPEED;
                break;
        }

        /* moving the spaceship part 2:
         * checking for diagonal movement to correct for its larger move size
         */
        if (direction.x == 1 && direction.y == 1) {
            shipY = shipY + (int) (XY_CORRECTION * SHIP_SPEED);
            shipX = shipX - (int) (XY_CORRECTION * SHIP_SPEED);
        }
        if (direction.x == -1 && direction.y == -1) {
            shipY = shipY - (int) (XY_CORRECTION * SHIP_SPEED);
            shipX = shipX + (int) (XY_CORRECTION * SHIP_SPEED);
        }
        if (direction.x == -1 && direction.y == 1) {
            shipY = shipY + (int) (XY_CORRECTION * SHIP_SPEED);
            shipX = shipX + (int) (XY_CORRECTION * SHIP_SPEED);
        }
        if (direction.x == 1 && direction.y == -1) {
            shipY = shipY - (int) (XY_CORRECTION * SHIP_SPEED);
            shipX = shipX - (int) (XY_CORRECTION * SHIP_SPEED);
        }

        // moving the lasers
        for (int i = 0; i < MAX_LASERS; i++) {
            if (laserGun[i] != null) { laserGun[i].moveLaser(); }
        }

        // moving the asteroids
        if (a != null) {
            a.move();
        }

    }

    public void aim() {
        if (MouseControls.onScreen) {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

            if (CanShoot) {
                for (int i = 0; i < MAX_LASERS; i++) {

                    // only allows 3 lasers to be on screen at once
                    if (laserGun[i] == null) {
                        laserGun[i] = new laser(shipX, shipY);
                        break;
                    }
                }
                CanShoot = false;
            }
        }
        else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void checkCollisions() {

        // check laser collisions with border
        for (int i = 0; i < MAX_LASERS; i++) {
            if (laserGun[i] != null) {
                if ((laserGun[i].laserX >= (SCREEN_WIDTH - laserGun[i].LASER_SIZE)) || (laserGun[i].laserX < 0)) {
                    laserGun[i] = null;
                    break;
                } else if ((laserGun[i].laserY >= (SCREEN_HEIGHT - laserGun[i].LASER_SIZE)) || (laserGun[i].laserY < 0)) {
                    laserGun[i] = null;
                    break;
                }
            }
        }

        if (a != null) {
            if ((a.locX >= (SCREEN_WIDTH + 400)) || (a.locX < -400)) {
                a = null;
            } else if ((a.locY >= (SCREEN_HEIGHT + 400)) || (a.locY < -400)) {
                a = null;
            }
        }
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

    public static class asteroid {

        public final int SIZE = determineSize();

        public int locX, locY;
        private int moveX, moveY;

        asteroid() {

            final int SPEED_CONSTANT = (int) (SHIP_SPEED * 0.75);
            final int ASTEROID_SPEED = ((SHIP_SIZE / SIZE) * SPEED_CONSTANT);

            int dirY = rand.nextInt(SCREEN_HEIGHT - 100) + 50;
            int dirX = rand.nextInt(SCREEN_WIDTH - 100) + 50;
            int randomFactorX = rand.nextInt(3);
            int randomFactorY;
            double absV;

            // System.out.println("(" + dirX + ", " + dirY + ")");

            switch(randomFactorX) {
                case 0:
                    locX = (rand.nextInt(150) * -1) - 150;
                    randomFactorY = 2;
                    break;
                case 1:
                    locX = rand.nextInt(150) + SCREEN_WIDTH;
                    randomFactorY = 2;
                    break;
                case 2:
                    locX = rand.nextInt(SCREEN_WIDTH - 100) + 50;
                    randomFactorY = rand.nextInt(2);
                    break;
                default:
                    locX = -150;
                    randomFactorY = 0;
            }
            switch(randomFactorY) {
                case 0:
                    locY = (rand.nextInt(150) * -1) - 150;
                    break;
                case 1:
                    locY = rand.nextInt(150) + SCREEN_HEIGHT;
                    break;
                case 2:
                    locY = rand.nextInt(SCREEN_HEIGHT - 100) + 50;
                    break;
                default:
                    locY = -150;
            }

            absV = Math.sqrt( ( ( dirX - locX ) * ( dirX - locX ) ) + ( ( dirY - locY ) * ( dirY - locY ) ) );

            moveX = (int) ( ( (double) (dirX - locX ) / absV ) * ASTEROID_SPEED );
            moveY = (int) ( ( (double) (dirY - locY ) / absV ) * ASTEROID_SPEED );
        }

        void move() {
            locX += moveX;
            locY += moveY;
        }

        int determineSize() {

            int chance = rand.nextInt(100);

            if (chance < 60) {
                return (int) (SHIP_SIZE * 0.85);
            }
            else if (chance < 80) {
                return SHIP_SIZE;
            }
            else if (chance < 90) {
                return (int) (SHIP_SIZE * 0.4);
            }
            else {
                return (int) (SHIP_SIZE * 1.75);
            }
        }


    }

    public static class laser {
        public final int LASER_SIZE = (int) (SHIP_SIZE * 0.25);

        public int laserY, laserX;
        private int moveX, moveY;

        laser(int X, int Y) {

            // see if it's okay to shoot a laser in the first place
            if (CanShoot) {

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

            CanShoot = false;
        }

        void moveLaser() {
            laserX += moveX;
            laserY += moveY;
            // laserX += 10;
            // laserY += 10;
        }
    }
}
