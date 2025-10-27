import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Scene3 extends JPanel implements ActionListener{
    private Cat cat;
    private Floor floor = new Floor();
    private Timer timer;

    public Scene3(SceneManager manager,Cat cat) {
        this.cat = cat;
        setLayout(null); // ใช้ตำแหน่งแบบอิสระ
        setBackground(Color.WHITE);

        // จัดตำแหน่งเริ่มต้นของแมว
        cat.x = 100;
        cat.y = 330;

        // ตั้ง timer ให้ repaint ทุก 30ms (ประมาณ 33 FPS)
        timer = new Timer(30, this);
        timer.start();

        // ควบคุมการเคลื่อนไหวด้วยคีย์บอร์ด
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D:
                        cat.setDirection(true);
                        cat.x += 10;
                        if (cat.x + cat.getWidth()/2 > 800) {
                            manager.showScene3(cat);
                         }
                        cat.setState("walk");
                        break;
                    case KeyEvent.VK_A:
                        cat.setDirection(false);
                        cat.x -= 10;
                        if (cat.x < 30) cat.x = 30;
                        cat.setState("walk");
                        break;
                    case KeyEvent.VK_W:
                        cat.setState("jump");
                        cat.jump();
                        break;
                    case KeyEvent.VK_S:
                        cat.setState("crouch");
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                cat.setState("stand");
            }
        });

        setFocusable(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        floor.draw(g);
        cat.draw(g, this);
        drawHPBar(g);
        drawMPBar(g);
        drawEnemyHPBar(g);
    }

    // -----------------------------
    // ฟังก์ชันวาดแถบ HP
    // -----------------------------
    private void drawHPBar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // ----- กำหนดค่าพื้นฐาน -----
        int maxHP = 100;
        float currentHP = cat.getHP();
        if (currentHP < 0) currentHP = 0;
        if (currentHP > maxHP) currentHP = maxHP;

        int barWidth = 300;
        int barHeight = 25; 
        int x = 60;
        int y = 30;

        // คำนวณเปอร์เซ็นต์ HP
        int hpWidth = (int) ((currentHP / maxHP) * barWidth);

        // ----- วาดกรอบ -----
        g2.setColor(new Color(34, 64, 111));
        g2.drawRect(x - 1, y - 1, barWidth + 2, barHeight + 2);

        // ----- แถบพื้นหลัง (เทา) -----
        g2.setColor(new Color(34, 64, 111));
        g2.fillRect(x, y, barWidth, barHeight);
        g2.fillRect(x, y, hpWidth, barHeight);

        // ----- ตัวเลข HP -----
        g2.setColor(new Color(34, 64, 111));
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        g2.drawString("HP", x - 40, y + 20);
    }

    // -----------------------------
    // ฟังก์ชันวาดแถบ MP
    // -----------------------------
    private void drawMPBar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // ----- กำหนดค่าพื้นฐาน -----
        int maxMP = 100;
        float currentMP = cat.getMP();
        if (currentMP > maxMP) currentMP = maxMP;

        int barWidth = 250;
        int barHeight = 5; 
        int x = 60;
        int y = 80;

        // ----- คำนวณเปอร์เซ็นต์ MP ----
        int hpWidth = (int) ((currentMP / maxMP) * barWidth);

        // ----- วาดกรอบ -----
        g2.setColor(new Color(34, 64, 111));
        g2.drawRect(x - 1, y - 1, barWidth + 2, barHeight + 2);

        // ----- แถบพื้นหลัง -----
        g2.setColor(new Color(34, 64, 111));
        g2.fillRect(x, y, barWidth, barHeight);
        g2.fillRect(x, y, hpWidth, barHeight);

        // ----- ตัวเลข MP -----
        g2.setColor(new Color(34, 64, 111));
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        g2.drawString("MP", x - 40, y + 10);
    }

    private void drawEnemyHPBar(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    int maxHP = 100;
    float currentHP = cat.getHP();
    if (currentHP < 0) currentHP = 0;
    if (currentHP > maxHP) currentHP = maxHP;

    int barWidth = 300;
    int barHeight = 25;
    int x = 800 - barWidth - 60; // วางฝั่งขวา (ถ้าหน้าจอกว้าง 800)
    int y = 30;

    int hpWidth = (int) ((currentHP / maxHP) * barWidth);

    // กรอบ
    g2.setColor(new Color(34, 64, 111));
    g2.drawRect(x - 1, y - 1, barWidth + 2, barHeight + 2);

    // พื้นหลัง (เทาเข้ม)
    g2.setColor(new Color(34, 64, 111));
    g2.fillRect(x, y, barWidth, barHeight);

    // แถบ HP (จากขวา → ซ้าย)
    g2.setColor(new Color(34, 64, 111));
    g2.fillRect(x + (barWidth - hpWidth), y, hpWidth, barHeight);

    // Label
    g2.setColor(new Color(34, 64, 111));
    g2.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
    g2.drawString("HP", x + barWidth + 10, y + 20);
    }
}
