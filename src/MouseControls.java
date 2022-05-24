import java.awt.event.*;

public class MouseControls extends MouseAdapter {

    public static boolean inScreen = false;

    @Override
    public void mousePressed(MouseEvent e) {
        if (inScreen) {
            GamePanel.xFire = e.getX();
            GamePanel.yFire = e.getY();
            GamePanel.shoot = true;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        inScreen = true;
        System.out.println("Mouse entered screen");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        inScreen = false;
        System.out.println("Mouse exited screen");
    }
}

