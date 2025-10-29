
import java.io.File;
import javax.sound.sampled.*;

public class Sound {

    private static String[] bgSong = {
        "sound/HomeSound.wav",
        "sound/endGame.wav"
    };

    private static String[] sound = {
        "sound/SlimeLaser.wav",
        "sound/catSkill1.wav",
        "sound/catSkill2.wav",
        "sound/catHeal.wav",
        "sound/HumanWalk.wav",
        "sound/win.wav",
        "sound/poison.wav",
        "sound/dadSound.wav",
        "sound/click.wav"
    };

    static Clip clip;
    static AudioInputStream audioIn;

    public static void playSound(int song) {
        try {
            File file = new File(bgSong[song]);
            audioIn = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            setVolume(0.8f);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playSoundEffect(int n) {
        new Thread(() -> {
            try {
                File file = new File(sound[n]);
                try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    clip.start();
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void SoundFadeOut() {
        try {
            int fadeout = 10000;

            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float max = gain.getValue();
            float min = gain.getMinimum();

            int steps = 100;
            int delay = fadeout / steps;

            new Thread(() -> {
                try {
                    for (int i = 0; i <= steps; i++) {
                        float value = max - ((max - min) * i / steps);
                        gain.setValue(value);
                        Thread.sleep(delay);
                    }
                    clip.stop();
                    clip.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopSound() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    public static void setVolume(float volume) {
        try {
            if (clip != null) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * volume) + gainControl.getMinimum();
                gainControl.setValue(gain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
