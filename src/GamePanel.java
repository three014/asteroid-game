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
    final int SHIP_SPEED = (int)(0.12 * UNIT_SIZE);
    // final int MAX_LASERS = 3;
    static final Point offset = new Point();
    static int xFire = -1;
    static int yFire = -1;
    static boolean shoot = false;
    int shipX = (SCREEN_WIDTH - UNIT_SIZE) / 2;
    int shipY = (SCREEN_HEIGHT - UNIT_SIZE) / 2;
    // int health = 3;
    boolean running = false;
    // laser[] l = new laser[MAX_LASERS]; FIXME: turn into array of lasers when laser class works properly
    laser l = null;
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
            g.setColor(Color.gray);
            g.fillRect(shipX, shipY, UNIT_SIZE, UNIT_SIZE);


            // draw laser
            if (l != null) { // FIXME: turn l into array of lasers when laser class works
                g.setColor(Color.green);
                g.fillOval(l.laserX, l.laserY, l.LASER_SIZE, l.LASER_SIZE);
            }

        }
    }

    public void move() {

        // moving the spaceship part 1
        switch (offset.x) {
            case -1:
                shipX = shipX - SHIP_SPEED;
                break;
            case 1:
                shipX = shipX + SHIP_SPEED;
                break;
        }
        switch (offset.y) {
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
        if (offset.x == 1 && offset.y == 1) {
            shipY = shipY + (int) (XY_CORRECTION * SHIP_SPEED);
            shipX = shipX - (int) (XY_CORRECTION * SHIP_SPEED);
        }
        if (offset.x == -1 && offset.y == -1) {
            shipY = shipY - (int) (XY_CORRECTION * SHIP_SPEED);
            shipX = shipX + (int) (XY_CORRECTION * SHIP_SPEED);
        }
        if (offset.x == -1 && offset.y == 1) {
            shipY = shipY + (int) (XY_CORRECTION * SHIP_SPEED);
            shipX = shipX + (int) (XY_CORRECTION * SHIP_SPEED);
        }
        if (offset.x == 1 && offset.y == -1) {
            shipY = shipY - (int) (XY_CORRECTION * SHIP_SPEED);
            shipX = shipX - (int) (XY_CORRECTION * SHIP_SPEED);
        }

        if (l != null) { l.moveLaser(); }
    }

    public void aim() {
        if (MouseControls.inScreen) {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

            if (shoot) {
                l = new laser(shipX, shipY);
            }
        }
        else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void checkCollisions() {

        // check laser collisions with border
        if (l != null) {
            if ( ( l.laserX >= ( SCREEN_WIDTH - l.LASER_SIZE ) ) || ( l.laserX < 0 ) ) {
                l = null;
            } else if ( ( l.laserY >= ( SCREEN_HEIGHT - l.LASER_SIZE ) ) || ( l.laserY < 0 ) ) {
                l = null;
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

    }

    public static class laser {
        final int LASER_SIZE = (int) ( UNIT_SIZE * 0.25 );
        final int LASER_SPEED = (int) ( UNIT_SIZE * 0.3 );

        public int laserY, laserX;
        private int moveX, moveY;

        laser(int X, int Y) {

            // see if it's okay to shoot a laser in the first place
            if (shoot) {

                int aimX, aimY;
                //int directionX, directionY;
                double absV;

                // starting laser coordinates
                laserX = X + ( UNIT_SIZE / 2 );
                laserY = Y + ( UNIT_SIZE / 2) ;
                aimX = xFire;
                aimY = yFire;

                // find normalizing factor for unit vector of laser trajectory
                absV = Math.sqrt( ( ( aimX - X ) * ( aimX - X ) ) + ( ( aimY - Y ) * ( aimY - Y ) ) );

                // find direction for vector
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

            shoot = false;
        }

        void moveLaser() {
            laserX += moveX;
            laserY += moveY;
            // laserX += 10;
            // laserY += 10;
        }
    }
}
