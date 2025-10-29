import java.awt.*;
import javax.swing.*;

public class CatBackHome extends JFrame {
    final private SceneManager sceneManager;

    public CatBackHome() {
        Color blue = new Color(34, 64, 111);
        sceneManager = new SceneManager(getContentPane(), blue);
        sceneManager.showHomeScreen();

        Sound.playSound(0);
    }

    public static void main(String[] args) {
        JFrame frame = new CatBackHome();
        frame.setTitle("Cat Back Home");
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
