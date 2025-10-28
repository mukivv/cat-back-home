import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;

public class Scene5 extends JPanel implements ActionListener {
    private Cat cat;
    private Floor floor = new Floor();
    private Timer timer;
    private SceneManager manager; 
    private Color blue = new Color(34, 64, 111);

    private Human human; // <--- เปลี่ยน
    
    private String gameState = "fighting"; // สถานะ: fighting, win, lose

    // --- AI ตัวแปรใหม่ ---
    private int humanWaitTimer = 100; // (100 ticks * 30ms = 3000ms = 3 วิ)
    private final int HUMAN_COOLDOWN = 100;
    private int humanStepSize = 25; // ระยะที่เดิน 1 ก้าว

    // --- ตัวแปรสำหรับ Win/Lose (เหมือนเดิม) ---
    private JButton tryAgainButton;
    private ImageIcon tryAgainIcon;
    private ImageIcon tryAgainClickedIcon;
    private Image exitImage; // (คุณอาจจะไม่ต้องใช้ในฉากนี้)

    // --- ตัวแปร UI สกิล (เหมือนเดิม) ---
    private Image iconSkill1;
    private Image iconSkill1_click;
    private Image iconSkill2;
    private Image iconSkill2_click;
    private Image iconHeal;
    private Image iconHeal_click;

    private Timer skill1BlinkTimer, skill2BlinkTimer, healBlinkTimer;
    private boolean isSkill1Blinking = false;
    private boolean isSkill2Blinking = false;
    private boolean isHealBlinking = false;

    public Scene5(SceneManager manager, Cat cat) {
        this.manager = manager; 
        this.cat = cat;
        cat.resetStats();
        setLayout(null); 
        setBackground(Color.WHITE);

        human = new Human(); // <--- เปลี่ยน

        loadImages();
        
        setupTryAgainButton();
        setupBlinkTimers();

        // --- ตั้งค่าตำแหน่งเริ่มต้น ---
        cat.x = 100;
        cat.y = 330; // (ให้แน่ใจว่า y ของ Cat คือ "เท้า")
        cat.setState("stand"); 

        timer = new Timer(30, this);
        timer.start();

        // --- ควบคุมการเคลื่อนไหว (แก้ไข L) ---
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameState.equals("lose")) {
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D:
                        cat.setDirection(true);
                        cat.x += 10;
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
                        
                    case KeyEvent.VK_J: // <--- สกิล J (ทำให้ไม่มีผล)
                        if (gameState.equals("fighting")) { 
                            if (!cat.getState().equals("stand")) break; 
                            if (cat.useSkill1()) { // (ยังใช้ MP และ Cooldown)
                                cat.setState("skill1");
                                SFXSound.playSound(1);
                                isSkill1Blinking = true;
                                skill1BlinkTimer.start();
                                System.out.println("Cat attacks! (Ineffective against Human)");
                            }
                        }
                        break;
                    case KeyEvent.VK_K: // <--- สกิล K (ทำให้ไม่มีผล)
                        if (gameState.equals("fighting")) { 
                            if (!cat.getState().equals("stand")) break; 
                            if (cat.useSkill2()) {
                                cat.setState("skill2");
                                SFXSound.playSound(2);
                                isSkill2Blinking = true;
                                skill2BlinkTimer.start();
                                System.out.println("Cat uses Meow! (Ineffective against Human)");
                            }
                        }
                        break;
                        
                    // --- (สำคัญ) แก้ไขปุ่ม L ---
                    case KeyEvent.VK_L: 
                        if (gameState.equals("fighting")) {
                            if (!cat.getState().equals("stand")) break;

                            // 1. ตรวจสอบ MP (ใช้ 40 MP เหมือนเดิม)
                            if (cat.useHeal()) {
                                SFXSound.playSound(3); // <--- เล่นเสียง
                                cat.setState("heal"); // <--- เล่นท่า
                                isHealBlinking = true; 
                                healBlinkTimer.start();
                                
                                // --- 2. (เปลี่ยน) ทำดาเมจแทนฮีล ---
                                int healDamage = 20; // (ตั้งค่าดาเมจ)
                                human.takeDamage(healDamage); // <--- โจมตี Human
                                System.out.println("Cat uses Heal... to attack! Human HP: " + human.getHP());
                                // cat.getHeal(); // <--- ลบบรรทัดนี้ (ไม่ฮีลตัวเอง)
                            } else {
                                System.out.println("NOT ENOUGH MP!");
                            }
                        }
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (gameState.equals("lose")) return;
                if (cat.getState().equals("jump") || 
                    cat.getState().equals("skill1") ||
                    cat.getState().equals("skill2") ||
                    cat.getState().equals("heal")) {
                    return;
                }
                cat.setState("stand");
            }
        });

        setFocusable(true);
    }

    // ... (loadImages, setupTryAgainButton, setupBlinkTimers ... เหมือนเดิม) ...

    private void loadImages() {
        tryAgainIcon = new ImageIcon("image/tryagain.png");
        tryAgainClickedIcon = new ImageIcon("image/tryagain_clicked.png");
        exitImage = new ImageIcon("image/exit.png").getImage();

        iconSkill1 = new ImageIcon("image/iconskill1.png").getImage();
        iconSkill1_click = new ImageIcon("image/iconskill1_click.png").getImage();
        iconSkill2 = new ImageIcon("image/iconskill2.png").getImage();
        iconSkill2_click = new ImageIcon("image/iconskill2_click.png").getImage();
        iconHeal = new ImageIcon("image/iconheal.png").getImage();
        iconHeal_click = new ImageIcon("image/iconheal_click.png").getImage();
    }

    private void setupTryAgainButton() {
        tryAgainButton = new JButton(tryAgainIcon);
        tryAgainButton.setBounds(250, 200, tryAgainIcon.getIconWidth(), tryAgainIcon.getIconHeight());
        
        tryAgainButton.setBorderPainted(false);
        tryAgainButton.setContentAreaFilled(false);
        tryAgainButton.setFocusPainted(false);
        
        tryAgainButton.setVisible(false); // ซ่อนไว้ก่อน
        
        tryAgainButton.addActionListener(e -> {
            playClickSound("sound/click.wav");
            tryAgainButton.setIcon(tryAgainClickedIcon);
            
            Timer resetTimer = new Timer(500, ev -> resetScene());
            resetTimer.setRepeats(false);
            resetTimer.start();
        });
        
        add(tryAgainButton);
    }

    private void setupBlinkTimers() {
        int blinkDuration = 200; // 200ms (0.2 วินาที)

        skill1BlinkTimer = new Timer(blinkDuration, e -> isSkill1Blinking = false);
        skill1BlinkTimer.setRepeats(false);

        skill2BlinkTimer = new Timer(blinkDuration, e -> isSkill2Blinking = false);
        skill2BlinkTimer.setRepeats(false);

        healBlinkTimer = new Timer(blinkDuration, e -> isHealBlinking = false);
        healBlinkTimer.setRepeats(false);
    }

    private void resetScene() {
        // 1. รีเซ็ตสถานะเกม
        gameState = "fighting";
        
        // 2. รีเซ็ตค่าพลัง
        cat.resetStats();
        human.resetHP(); 
        
        // 3. รีเซ็ตตำแหน่ง
        cat.x = 100;
        cat.y = 330;
        cat.setState("stand");
        
        // 4. ซ่อนปุ่ม
        tryAgainButton.setVisible(false);
        tryAgainButton.setIcon(tryAgainIcon);
        
        // 5. คืนการควบคุม
        requestFocusInWindow();
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState.equals("fighting")) {
            updateEnemyAI(); // <--- เขียน AI ใหม่
            checkGameState();
            cat.updateMP(); 
        }
        repaint();
    }

    // --- (สำคัญ) AI ใหม่ทั้งหมด ---
    private void updateEnemyAI() {
        
        // 1. ตรวจสอบการชน (Insta-kill)
        // (เราต้องสมมติว่า cat.y และ human.y คือ "เท้า" ทั้งคู่)
        if (cat.getWidth() + cat.x > human.x+100) {
            cat.setHP(100); // ฆ่าแมวทันที
        }

        // 2. ตรรกะการเดิน (3 วินาที 1 ก้าว)
        // (Human.java จะเล่น animation วนลูปเอง)
        // เราแค่รอ timer แล้วขยับ "x"
        
        humanWaitTimer--;
        
        if (humanWaitTimer <= 0) {
            // --- ถึงเวลาขยับ 1 ก้าว ---
        
            human.x -= humanStepSize;
            
            // --- รีเซ็ตคูลดาวน์ (รออีก 3 วิ) ---
            humanWaitTimer = HUMAN_COOLDOWN;
        }
    }

    private void checkGameState() {
        // --- เช็ก Win ---
        if (human.getHP() <= 0) { 
        gameState = "win";
        System.out.println("YOU WIN");
        SFXSound.playSound(5);

        // --- (เพิ่มใหม่) ตั้งเวลา 8 วิ แล้วไปฉาก 7 ---
        Timer winTimer = new Timer(6000, (ActionEvent ev) -> {
            manager.showScene6(); // <--- สั่งให้ SceneManager เปลี่ยนฉาก
        });
        winTimer.setRepeats(false); // <--- (สำคัญ) ให้ทำงานแค่ครั้งเดียว
        winTimer.start();
        // --- (จบส่วนที่เพิ่มใหม่) ---
    }

        // --- เช็ก Lose ---
        if (cat.getHP() <= 0) {
            gameState = "lose";
            // (human ไม่มี state "stand" เลยไม่ต้องสั่ง)
            cat.setState("dead");
            System.out.println("GAME OVER");
            tryAgainButton.setVisible(true); 
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        floor.draw(g);
        
        human.draw(g, this); // <--- เปลี่ยน
        
        cat.draw(g, this);

        // --- วาด UI (เหมือนเดิม) ---
        drawHPBar(g);
        drawMPBar(g);
        int iconY = 90; // ตำแหน่ง Y ของไอคอน
        int iconX1 = 60;
        int iconX2 = 100; // (ขยับไป 40px)
        int iconX3 = 140; // (ขยับไป 40px)

        // วาดไอคอน สกิล 1 (J)
        if (isSkill1Blinking) {
            g.drawImage(iconSkill1_click, iconX1, iconY, this);
        } else if (cat.getMP() < 30) { // <--- เช็ก MP Cost
            g.drawImage(iconSkill1_click, iconX1, iconY, this);
        } else {
            g.drawImage(iconSkill1, iconX1, iconY, this);
        }

        // วาดไอคอน สกิล 2 (K)
        if (isSkill2Blinking) {
            g.drawImage(iconSkill2_click, iconX2, iconY, this);
        } else if (cat.getMP() < 30) { // <--- เช็ก MP Cost
            g.drawImage(iconSkill2_click, iconX2, iconY, this);
        } else {
            g.drawImage(iconSkill2, iconX2, iconY, this);
        }

        // วาดไอคอน ฮีล (L)
        if (isHealBlinking) {
            g.drawImage(iconHeal_click, iconX3, iconY, this);
        } else if (cat.getMP() < 40) { // <--- เช็ก MP Cost (40 MP)
            g.drawImage(iconHeal_click, iconX3, iconY, this);
        } else {
            g.drawImage(iconHeal, iconX3, iconY, this);
        }

            drawEnemyHPBar(g);
        
        // (คุณอาจจะไม่ต้องใช้ exitImage ในฉากนี้)
        // if (gameState.equals("win")) { ... }
    }

    private void drawHPBar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int maxHP = (int) cat.getMaxHP(); // (อันนี้จะทำงานได้แล้ว)
        float currentHP = cat.getHP();
        if (currentHP < 0) currentHP = 0;
        if (currentHP > maxHP) currentHP = maxHP;
        int barWidth = 300;
        int barHeight = 25; 
        int x = 60;
        int y = 30;
        int hpWidth = (int) ((currentHP / maxHP) * barWidth);
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, barWidth, barHeight);
        g2.setColor(blue);
        g2.fillRect(x, y, hpWidth, barHeight);
        g2.drawRect(x, y, barWidth, barHeight);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        g2.drawString("HP", x - 40, y + 20);
    }

    // -----------------------------
    // ฟังก์ชันวาดแถบ MP (ของแมว)
    // -----------------------------
    private void drawMPBar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int maxMP = (int) cat.getMaxMP(); // (อันนี้จะทำงานได้แล้ว)
        float currentMP = cat.getMP();
        if (currentMP > maxMP) currentMP = maxMP;
        int barWidth = 250;
        int barHeight = 15;
        int x = 60;
        int y = 65; 
        int mpWidth = (int) ((currentMP / maxMP) * barWidth);
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, barWidth, barHeight);
        g2.setColor(blue);
        g2.fillRect(x, y, mpWidth, barHeight);
        g2.drawRect(x, y, barWidth, barHeight);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        g2.drawString("MP", x - 40, y + 20);
    }

    // --- ฟังก์ชันวาดแถบ HP (ของศัตรู) ---
    private void drawEnemyHPBar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int maxHP = (int) human.getMaxHP(); // <--- เปลี่ยน
        float currentHP = human.getHP();    // <--- เปลี่ยน
        if (currentHP < 0) currentHP = 0;
        if (currentHP > maxHP) currentHP = maxHP;
        
        int barWidth = 300;
        int barHeight = 25;
        int x = 800 - barWidth - 60; 
        int y = 30;
        int hpWidth = (int) ((currentHP / maxHP) * barWidth);
        
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, barWidth, barHeight);
        g2.setColor(blue);
        g2.fillRect(x, y, hpWidth, barHeight);
        g2.drawRect(x, y, barWidth, barHeight);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        g2.drawString("HP", x + barWidth + 10, y + 20);
    }
}