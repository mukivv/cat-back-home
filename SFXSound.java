import java.io.File;
import javax.sound.sampled.*;

public class SFXSound {
    private static String[] sound = {
        "sound/SlimeLaser.wav",
        "sound/catSkill1.wav", 
        "sound/catSkill2.wav",
        "sound/catHeal.wav",
        "sound/HumanWalk.wav",
        "sound/win.wav",
        "sound/poison.wav"
    };

    public static void playSound(int n) {
        // เราจะสร้าง Thread ใหม่สำหรับเล่นเสียงแต่ละครั้ง
        // เพื่อให้เสียงสามารถเล่น "ซ้อนกัน" ได้ โดยไม่ตัดเสียงเก่า
        new Thread(() -> {
            try {
                File file = new File(sound[n]);
                try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    
                    // (ปรับความดัง-เบาได้ที่นี่ ถ้าต้องการ)
                    // FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    // gainControl.setValue(-10.0f); // ลดเสียง 10 dB
                    
                    clip.start();
                    
                    // ต้องมี Listener เพื่อรอให้เสียงเล่นจบ แล้วค่อยปิด Clip
                    // ไม่เช่นนั้นโปรแกรมอาจจะปิด Clip ก่อนที่เสียงจะเริ่มเล่น
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
}
