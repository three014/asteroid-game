import java.awt.*;

public class Entities {

    public static class asteroid extends Rectangle{

        // public int locX, locY;
        public double moveX, moveY;

        asteroid() {

            final int SIZE = determineSize();
            final double SPEED_CONSTANT = (int) (GamePanel.SHIP_SPEED * 0.9);
            final double ASTEROID_SPEED = ( ( (double) GamePanel.SHIP_SIZE / (double) SIZE ) * SPEED_CONSTANT );
            width = SIZE;
            height = SIZE;

            int dirX, dirY;
            int randomFactorX = GamePanel.rand.nextInt(3);
            int randomFactorY;
            double absV;

            // determine starting location and direction
            switch(randomFactorX) {
                case 0:
                    x = GamePanel.rand.nextInt(SIZE) - SIZE;
                    dirX = GamePanel.rand.nextInt(500);
                    randomFactorY = 2;
                    break;
                case 1:
                    x = GamePanel.rand.nextInt(SIZE) + GamePanel.SCREEN_WIDTH;
                    dirX = GamePanel.rand.nextInt(500) - 500;
                    randomFactorY = 2;
                    break;
                case 2:
                    x = GamePanel.rand.nextInt(GamePanel.SCREEN_WIDTH - GamePanel.DIRECTION_LIMIT * 2) + GamePanel.DIRECTION_LIMIT;
                    dirX = GamePanel.rand.nextInt(1000) - 500;
                    randomFactorY = GamePanel.rand.nextInt(2);
                    break;
                default:
                    x = -SIZE;
                    dirX = 500;
                    randomFactorY = 0;
            }
            switch(randomFactorY) {
                case 0:
                    y = GamePanel.rand.nextInt(SIZE) - SIZE;
                    dirY = GamePanel.rand.nextInt(500);
                    break;
                case 1:
                    y = GamePanel.rand.nextInt(SIZE) + GamePanel.SCREEN_HEIGHT;
                    dirY = GamePanel.rand.nextInt(500) - 500;
                    break;
                case 2:
                    y = GamePanel.rand.nextInt(GamePanel.SCREEN_HEIGHT - GamePanel.DIRECTION_LIMIT * 2) + GamePanel.DIRECTION_LIMIT;
                    dirY = GamePanel.rand.nextInt(1000) - 500;
                    break;
                default:
                    y = -SIZE;
                    dirY = 500;
            }
            // More debugging
            System.out.println("(" + dirX + ", " + dirY + ")");

            // find normalizing factor for unit vector of initial asteroid trajectory
            absV = Math.sqrt( Math.pow( dirX, 2.0 ) + Math.pow( dirY, 2.0 ) );

            moveX = ( ( (double) dirX ) / absV ) * ASTEROID_SPEED;
            moveY = ( ( (double) dirY ) / absV ) * ASTEROID_SPEED;
        }

        void move() {
            x += (int) moveX;
            y += (int) moveY;
        }

        int determineSize() {

            int chance = GamePanel.rand.nextInt(100);

            if (chance < 60) {
                return GamePanel.SHIP_SIZE;
            }
            else if (chance < 80) {
                return (int) (GamePanel.SHIP_SIZE * 1.25);
            }
            else if (chance < 90) {
                return (int) (GamePanel.SHIP_SIZE * 0.4);
            }
            else {
                return GamePanel.SHIP_SIZE * 2;
            }
        }
    }

    public static class laser extends Rectangle{

        //public int laserY, laserX;
        private final int moveX, moveY;

        laser(int X, int Y) {

            final int LASER_SPEED = GamePanel.SHIP_SPEED * 2;
            final int LASER_SIZE = (int) (GamePanel.SHIP_SIZE * 0.25);
            width = LASER_SIZE;
            height = LASER_SIZE;

            int aimX, aimY;
            double absV;

            // starting laser coordinates
            x = X + (int) (GamePanel.SHIP_SIZE * 0.5);
            y = Y + (int) (GamePanel.SHIP_SIZE * 0.5);
            aimX = GamePanel.xFire;
            aimY = GamePanel.yFire;

            // find normalizing factor for unit vector of laser trajectory
            absV = Math.sqrt( ( ( aimX - X ) * ( aimX - X ) ) + ( ( aimY - Y ) * ( aimY - Y ) ) );

            // combine into actual laser beam movement
            moveX = (int) ( ( (double) ( aimX - X ) / absV ) * LASER_SPEED );
            moveY = (int) ( ( (double) ( aimY - Y ) / absV ) * LASER_SPEED );

                // Debugging
                //System.out.println("Normalizing factor: " + absV);
                //System.out.println("(" + aimX + ", " + aimY + ")");
                //System.out.println("(" + directionX + ", " + directionY + ")");

            GamePanel.canShoot = false;
        }

        void moveLaser() {
            x += moveX;
            y += moveY;
        }
    }
}
