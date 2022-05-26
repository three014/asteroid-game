import java.awt.event.*;
import java.util.*;

public class KeyControls extends KeyAdapter {

    private final Set<Integer> pressedKeys = new HashSet<>();

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        offsetRefresh();
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
        offsetRefresh();
    }

    private synchronized void offsetRefresh() {
        GamePanel.direction.y = 0;
        GamePanel.direction.x = 0;

        for (Integer pressedKey : pressedKeys) {
            switch (pressedKey) {
                case KeyEvent.VK_W:
                    if (GamePanel.direction.y < 1)
                        GamePanel.direction.y += 1;
                    else
                        GamePanel.direction.y = 1;
                    break;
                case KeyEvent.VK_A:
                    if (GamePanel.direction.x > -1)
                        GamePanel.direction.x -= 1;
                    else
                        GamePanel.direction.x = -1;
                    break;
                case KeyEvent.VK_S:
                    if (GamePanel.direction.y > -1)
                        GamePanel.direction.y -= 1;
                    else
                        GamePanel.direction.y = -1;
                    break;
                case KeyEvent.VK_D:
                    if (GamePanel.direction.x < 1)
                        GamePanel.direction.x += 1;
                    else
                        GamePanel.direction.x = 1;
                    break;
            }
        }

        // System.out.println(GamePanel.direction);
    }
}
