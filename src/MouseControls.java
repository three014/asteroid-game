import java.awt.event.*;

public class MouseControls extends MouseAdapter implements MouseMotionListener {

    @Override
    public void mousePressed(MouseEvent e) {
        GamePanel.xFire = e.getX();
        GamePanel.yFire = e.getY();
        GamePanel.shoot = true;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
