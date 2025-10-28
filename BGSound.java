import java.io.File;
import javax.sound.sampled.*;

public class BGSound {
    static String[] bgSong = {
        "sound/HomeSound.wav",
        "sound/endGame.wav",
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

    public static void playSoundFadeOut() {
    try {
        int fadeDurationMillis = 10000; // ระยะเวลาในการ fade out เป็นมิลลิวินาที

        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float max = gain.getValue();
        float min = gain.getMinimum();

        int steps = 100; // ยิ่งมาก ยิ่งเนียน
        int delay = fadeDurationMillis / steps;

            // Thread สำหรับ fade out
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
            // volume: 0.0 = ปิดเสียง, 1.0 = เสียงดังสุด
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}