import java.awt.*;
import javax.swing.*;

public class SceneManager {
    private final Container parent;
    private final Color bg;

    public SceneManager(Container parent, Color bg) {
        this.parent = parent;
        this.bg = bg;
    }

    public void showHomeScreen() {
        JPanel home = new JPanel();
        home.setBackground(bg);
        home.setLayout(new BoxLayout(home, BoxLayout.Y_AXIS));
        parent.removeAll();
        parent.add(home);

        // ภาพชื่อเกม
        ImageIcon GameName = new ImageIcon("image/CatBackHome_BG.png");
        JLabel imgGameName = new JLabel(GameName);
        imgGameName.setAlignmentX(Component.CENTER_ALIGNMENT);
        home.add(Box.createVerticalGlue());
        home.add(imgGameName);

        // Bounce ชื่อเกม
        Timer bounceTimer = new Timer(1000, null);
        final int[] dy = {3};
        bounceTimer.addActionListener(e -> {
            Point location = imgGameName.getLocation();
            int newY = location.y + dy[0];
            if (newY < 50 || newY > 70) dy[0] = -dy[0];
            imgGameName.setLocation(location.x, newY);
        });
        bounceTimer.start();

        // ปุ่ม Start
        StartButton startBt = new StartButton(() -> showTransition(bounceTimer));
        startBt.setAlignmentX(Component.CENTER_ALIGNMENT);
        home.add(Box.createRigidArea(new Dimension(0, 15)));
        home.add(startBt);
        home.add(Box.createVerticalGlue());

        parent.revalidate();
        parent.repaint();
    }

    private void showTransition(Timer bounceTimer) {
        bounceTimer.stop();

        Timer transitionTimer = new Timer(1000, ev -> {
            parent.removeAll();
            parent.setLayout(new BorderLayout());

            JPanel darkScene = new JPanel();
            darkScene.setBackground(bg);
            parent.add(darkScene, BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();

            // แสดง cutscene หลัง delay
            Timer darkTimer = new Timer(2000, ev2 -> {
                parent.removeAll();
                Cutscene cut = new Cutscene(0, 6900, this);
                parent.add(cut, BorderLayout.CENTER);
                parent.revalidate();
                parent.repaint();
            });
            darkTimer.setRepeats(false);
            darkTimer.start();
        });
        transitionTimer.setRepeats(false);
        transitionTimer.start();
    }

    public void showScene1() {
        parent.removeAll();
        JPanel next = new Scene1(this); //ไปฉากเกมที่ 1
        parent.add(next, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
        next.requestFocusInWindow();
    }

    public void showScene2(Cat cat) {
        parent.removeAll();
        JPanel next = new Scene2(this,cat); //ไปฉากเกมที่ 2
        parent.add(next, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
        next.requestFocusInWindow();
    }

    public void showScene3(Cat cat) {
        parent.removeAll();
        JPanel next = new Scene3(this,cat); //ไปฉากเกมที่ 3
        parent.add(next, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
        next.requestFocusInWindow();
    }

    public void showScene4(Cat cat) {
        parent.removeAll();
        JPanel next = new JPanel();
        next.setBackground(Color.BLACK);
        JLabel label = new JLabel("SCENE 4 - TO BE CONTINUED...");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        next.add(label);
        
        parent.add(next, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
        // ไม่ต้อง requestFocus() เพราะยังไม่มีการควบคุม
    }
}
