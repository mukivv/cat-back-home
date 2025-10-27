import java.awt.*;
import javax.swing.*;

class Cutscene extends JPanel {
        static String[] sceneName = {
            "image/eyeCutscene.gif",
            "background_music.wav",
            "victory_sound.wav"
        };

        Cutscene(int s, int durationMs, SceneManager manager) {
            setLayout(new BorderLayout());

            ImageIcon icon = new ImageIcon(sceneName[s]);
            JLabel label = new JLabel(icon);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            add(label, BorderLayout.CENTER);

            setPreferredSize(new Dimension(800, 600));

            // Timer จบ cutscene
            Timer t = new Timer(durationMs, e -> {
               manager.showScene1();
            });
            t.setRepeats(false);
            t.start();
        }
    }