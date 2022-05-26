import java.awt.event.*;

public class MouseControls extends MouseAdapter {

    public static boolean onScreen = false;

    @Override
    public void mousePressed(MouseEvent e) {
        if (onScreen) {
            GamePanel.xFire = e.getX();
            GamePanel.yFire = e.getY();
            GamePanel.canShoot = true;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        onScreen = true;
        //System.out.println("Mouse entered screen");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        onScreen = false;
        //System.out.println("Mouse exited screen");
    }
}

