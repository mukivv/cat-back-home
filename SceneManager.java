import java.awt.*;
import javax.swing.*;

public class SceneManager {
    private final Container parent;
    private final Color blue;
    static String[] CutScene = {
            "image/eyeCutscene.gif",
            "image/endCutScene.gif",
        };

    public SceneManager(Container parent, Color b) {
        this.parent = parent;
        this.blue = b;
    }

    public void showHomeScreen() {
        JPanel home = new JPanel();
        home.setBackground(blue);
        home.setLayout(new BoxLayout(home, BoxLayout.Y_AXIS));
        parent.removeAll();
        parent.add(home);

        ImageIcon GameName = new ImageIcon("image/CatBackHome_BG.png");
        JLabel imgGameName = new JLabel(GameName);
        imgGameName.setAlignmentX(Component.CENTER_ALIGNMENT);
        home.add(Box.createVerticalGlue());
        home.add(imgGameName);

        Timer bounceTimer = new Timer(1000, null);
        final int[] dy = {3};
        bounceTimer.addActionListener(e -> {
            Point location = imgGameName.getLocation();
            int newY = location.y + dy[0];
            if (newY < 50 || newY > 70) dy[0] = -dy[0];
            imgGameName.setLocation(location.x, newY);
        });
        bounceTimer.start();

        ImageIcon icon = new ImageIcon("image/start.png");
        ImageIcon iconClicked = new ImageIcon("image/start_clicked.png");

        JButton startBt = new JButton(icon);

        startBt.setBorderPainted(false);
        startBt.setContentAreaFilled(false);
        startBt.setFocusPainted(false);
        startBt.setAlignmentX(Component.CENTER_ALIGNMENT);

        Timer startBtTimer = new Timer(1000, null);
        final int[] dyButton = {3};
        startBtTimer.addActionListener(e -> {
            Point location = startBt.getLocation();
            int newY = location.y + dyButton[0];
            if (newY < 80 || newY > 90) dyButton[0] = -dyButton[0];
            startBt.setLocation(location.x, newY);
        });
        startBtTimer.start();

        startBt.addActionListener(e -> {
            Sound.SoundFadeOut();
            Sound.playSoundEffect(8);

            startBtTimer.stop();
            startBt.setIcon(iconClicked);

            showTransition(bounceTimer); 
        });

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
            darkScene.setBackground(blue);
            parent.add(darkScene, BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();

            Timer darkTimer = new Timer(2000, ev2 -> {
                parent.removeAll();
                JPanel cutscene = showCutscene(0, 6900, this);
                parent.add(cutscene, BorderLayout.CENTER);
                parent.revalidate();
                parent.repaint();
            });
            darkTimer.setRepeats(false);
            darkTimer.start();
        });
        transitionTimer.setRepeats(false);
        transitionTimer.start();
    }

    private JPanel showCutscene(int s, int durationMs, SceneManager manager) {
            JPanel cutscene = new JPanel();
            cutscene.setLayout(new BorderLayout());

            ImageIcon icon = new ImageIcon(CutScene[s]);
            JLabel label = new JLabel(icon);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            cutscene.add(label, BorderLayout.CENTER);
            cutscene.setBackground(Color.WHITE);

            cutscene.setPreferredSize(new Dimension(800, 600));

            Timer t = new Timer(durationMs, e -> {
               if (s==0) showScene1();
               if (s==1) showHomeScreen();
            });
            t.setRepeats(false);
            t.start();

            return cutscene;
    }

    public void showScene1() {
        parent.removeAll();
        JPanel next = new Scene1(this);
        parent.add(next, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
        next.requestFocusInWindow();
    }

    public void showScene2(Cat cat) {
        parent.removeAll();
        JPanel next = new Scene2(this);
        parent.add(next, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
        next.requestFocusInWindow();
    }

    public void showScene3(Cat cat) {
        parent.removeAll();
        JPanel next = new Scene3(this,cat);
        parent.add(next, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
        next.requestFocusInWindow();
    }

    public void showScene4(Cat cat) {
        parent.removeAll();
        JPanel next = new Scene4(this,cat);
        parent.add(next, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
        next.requestFocusInWindow();
    }

    public void showScene5(Cat cat) {
        parent.removeAll();
        JPanel next = new Scene5(this,cat);
        parent.add(next, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
        next.requestFocusInWindow();
    }

    public void showScene6() {
        parent.removeAll();
        JPanel cutscene = showCutscene(1, 30000, this);
        parent.add(cutscene, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();

        Sound.playSound(1);
    }
}
