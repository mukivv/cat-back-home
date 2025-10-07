import java.awt.*;
import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;

public class StartButton extends JButton {
    final private ImageIcon icon = new ImageIcon("image/start.png");;
    private ImageIcon iconClicked = new ImageIcon("image/start_clicked.png");
    private Timer startBtTimer;

    public StartButton(Runnable onClick) {
        setIcon(icon);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);

        setupBounceAnimation();

        addActionListener(e -> {
            BGSound.playSoundFadeOut();
            playClickSound("sound/click.wav");

            startBtTimer.stop();
            setIcon(iconClicked);

            onClick.run();
        });
    }

    private void setupBounceAnimation() {
        startBtTimer = new Timer(1000, null);
        final int[] dy = {3};
        startBtTimer.addActionListener(e -> {
            Point location = getLocation();
            int newY = location.y + dy[0];
            if (newY < 80 || newY > 90) dy[0] = -dy[0];
            setLocation(location.x, newY);
        });
        startBtTimer.start();
    }

    private void playClickSound(String filePath) {
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath))) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}