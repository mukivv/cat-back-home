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
        JPanel next = new Scene4(this,cat); //ไปฉากเกมที่ 3
        parent.add(next, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
        next.requestFocusInWindow();
    }

    public void showScene5(Cat cat) {
        parent.removeAll();
        JPanel next = new Scene5(this,cat); //ไปฉากเกมที่ 3
        parent.add(next, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
        next.requestFocusInWindow();
        // ไม่ต้อง requestFocus() เพราะยังไม่มีการควบคุม
    }

    public void showScene6() {
        // 1. ล้างจอ
        parent.removeAll();
        // (เราจะใช้ BorderLayout.CENTER เพื่อให้ GIF อยู่ตรงกลาง)
        parent.setLayout(new BorderLayout()); 

        // 2. โหลด GIF ที่คุณต้องการเล่น
        // ⭐️ (เปลี่ยน "image/your-ending-gif.gif" เป็นชื่อไฟล์ GIF ของคุณ)
        ImageIcon endingGif = new ImageIcon("image/endCutScene.gif");
        JLabel gifLabel = new JLabel(endingGif);

        // (เผื่อ GIF มีพื้นหลังโปร่งใส ให้ตั้งค่าสีพื้นหลัง)
        gifLabel.setOpaque(true);
        gifLabel.setBackground(this.bg); // (ใช้สี bg จาก Manager)

        // 3. เพิ่ม GIF เข้าไปในจอ
        parent.add(gifLabel, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();

        BGSound.playSound(1);

        // 4. (สำคัญ!) ตั้งเวลาให้ตรงกับความยาว GIF
        // ⭐️ (คุณต้องหาความยาว GIF ของคุณเอง แล้วใส่ค่าเป็นมิลลิวินาที)
        //    (ตัวอย่าง: ถ้า GIF ยาว 8.5 วินาที ให้ใส่ 8500)
        int gifDurationInMs = 30000; // <--- แก้ค่านี้! (8000 = 8 วินาที)

        Timer backToHomeTimer = new Timer(gifDurationInMs, (e) -> {
            // 5. พอครบ 8 วินาที (GIF เล่นจบ) ก็เรียกหน้า Home
            showHomeScreen();
        });
        
        backToHomeTimer.setRepeats(false); // (สำคัญ) ให้ทำงานแค่ครั้งเดียว
        backToHomeTimer.start();
    }
}
