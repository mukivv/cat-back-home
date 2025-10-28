import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;

public class Scene3 extends JPanel implements ActionListener {
    private Cat cat;
    private Floor floor = new Floor();
    private Timer timer;
    private SceneManager manager; 
    private Color blue = new Color(34, 64, 111);

    private Slime slime;
    
    private String gameState = "fighting"; // สถานะ: fighting, win, lose
    private int enemyAttackCooldown = 100; 

    // --- ตัวแปรสำหรับ Win/Lose ---
    private JButton tryAgainButton;
    private ImageIcon tryAgainIcon;
    private ImageIcon tryAgainClickedIcon;
    private Image exitImage;
    private Image poisonIcon;

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

    public Scene3(SceneManager manager, Cat cat) {
        this.manager = manager; 
        this.cat = cat;
        setLayout(null); 
        setBackground(Color.WHITE);

        // --- สร้าง Slime ---
        slime = new Slime(100, 10, 0, 0); 

        // --- โหลดรูปภาพสำหรับ Win/Lose ---
        loadImages();
        
        // --- สร้างปุ่ม Try Again ---
        setupTryAgainButton();
        setupBlinkTimers();

        // จัดตำแหน่งเริ่มต้นของแมว
        cat.x = 100;
        cat.y = 330;
        cat.setState("stand"); 

        timer = new Timer(30, this);
        timer.start();

        // ควบคุมการเคลื่อนไหวด้วยคีย์บอร์ด
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // ถ้าแพ้ (รอรีเซ็ต) ห้ามขยับ
                if (gameState.equals("lose")) {
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D:
                        cat.setDirection(true);
                        cat.x += 10;
                        
                        if (gameState.equals("win")) {
                            // ถ้าชนะแล้ว, เช็กว่าเดินถึงขอบจอหรือยัง
                            if (cat.x + cat.getWidth()/2 > 800) {
                                timer.stop(); // หยุดเกมก่อนเปลี่ยนฉาก
                                manager.showScene4(cat); 
                             }
                        } else {
                            // ถ้ายังสู้, ไม่ให้เดินเลยกลางจอ
                            int catFront = cat.x + cat.getWidth();
                            int slimeFront = slime.x;

                            // ถ้าด้านหน้าของแมว เกือบจะชน Slime ก็ให้หยุด
                            if (catFront > slimeFront) { 
                            cat.x = slimeFront - cat.getWidth(); // ให้แมวหยุดพอดี
                        }
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
                        
                    case KeyEvent.VK_J:
                        if (gameState.equals("fighting")) { 
                            if (!cat.getState().equals("stand")) break; 

                            // --- 1. คำนวณระยะห่าง ---
                            int catFront = cat.x + cat.getWidth();
                            int slimeFront = slime.x;
                            int distance = slimeFront - catFront; // ระยะห่างระหว่างด้านหน้าของแมวกับ Slime

                            // --- 2. ตรวจสอบ MP ---
                            if (cat.useSkill1()) {
                                cat.setState("skill1"); // (แสดงท่าโจมตี ไม่ว่าจะโดนหรือไม่)
                                SFXSound.playSound(1);
                                isSkill1Blinking = true; // <--- เพิ่ม
                                skill1BlinkTimer.start(); // <--- เพิ่ม
                                // --- 3. ตรวจสอบระยะห่าง ---
                                if (distance <= slime.getHitbox()) {
                                    // (ถ้าอยู่ในระยะโจมตี)
                                    slime.takeDamage(cat.getSkill1()); 
                                    cat.applyPoison();
                                    System.out.println("Cat attacks! HIT! Slime HP: " + slime.getHP());
                                } else {
                                    // (ถ้าไกลเกินไป)
                                    System.out.println("Cat attacks! MISS! (Too far)");
                                }
                            } else {
                                System.out.println("NOT ENOUGH MP!");
                            }
                        }
                        break;
                    case KeyEvent.VK_K: 
                        if (gameState.equals("fighting")) { 
                            if (!cat.getState().equals("stand")) break; 

                            // --- 1. ตรวจสอบ MP (ใช้ 30) ---
                            if (cat.useSkill2()) {
                                SFXSound.playSound(2); // <--- เล่นเสียง
                                cat.setState("skill2"); // <--- เล่นท่า
                                isSkill2Blinking = true; // <--- เพิ่ม
                                skill2BlinkTimer.start();
                                
                                // --- 2. โจมตีทันที (ไม่ต้องเช็กระยะ) ---
                                slime.takeDamage(cat.getSkill2()); 
                                System.out.println("Cat uses Meow! HIT! Slime HP: " + slime.getHP());
                            } else {
                                System.out.println("NOT ENOUGH MP!");
                            }
                        }
                        break;
                        
                    // --- โค้ดที่เพิ่มเข้ามา (L) ---
                    case KeyEvent.VK_L: 
                        if (gameState.equals("fighting")) {
                            if (!cat.getState().equals("stand")) break;

                            // --- 1. ตรวจสอบ MP (ใช้ 10) ---
                            if (cat.useHeal()) {
                                SFXSound.playSound(3); // <--- เล่นเสียง
                                cat.setState("heal"); // <--- เล่นท่า
                                isHealBlinking = true; // <--- เพิ่ม
                                healBlinkTimer.start();
                                
                                // --- 2. ฮีลตัวเอง (10 HP) ---
                                cat.getHeal(); // เมธอดนี้จะบวก HP 10 ให้เอง
                                System.out.println("Cat heals! HP: " + cat.getHP());
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

    private void loadImages() {
        tryAgainIcon = new ImageIcon("image/tryagain.png");
        tryAgainClickedIcon = new ImageIcon("image/tryagain_clicked.png");
        exitImage = new ImageIcon("image/exit.png").getImage();
        poisonIcon = new ImageIcon("image/poison.png").getImage();

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
        slime.resetHP(); 
        
        // 3. รีเซ็ตตำแหน่ง
        cat.x = 100;
        cat.y = 330;
        cat.setState("stand");
        
        slime.setState("stand");
        
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
            updateEnemyAI();
            checkGameState();
            cat.updateMP(); 

            cat.updatePoisonStatus();
        }
        repaint();
    }

    private void updateEnemyAI() {
        if (enemyAttackCooldown > 0) {
            enemyAttackCooldown--;
        }

        if (enemyAttackCooldown == 0 && slime.getState().equals("stand")) {
            slime.setState("attack"); 
            enemyAttackCooldown = 100 + (int)(Math.random() * 50); 
        }
    }

    private void checkGameState() {
        if (slime.getHP() <= 0) {
            gameState = "win";
            slime.setState("stand"); 
            System.out.println("YOU WIN");
            SFXSound.playSound(5);
        }

        if (cat.getHP() <= 0) {
            gameState = "lose";
            slime.setState("stand");
            System.out.println("GAME OVER");
            tryAgainButton.setVisible(true); 
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        floor.draw(g);
        
        if (!gameState.equals("win")) {
            slime.draw(g, this);
        }
        
        cat.draw(g, this);

        if (slime.isFiringLaser() && !gameState.equals("win")) {
            int laserY = slime.y + 78; 
            int laserHeight = 13;
            
            g.setColor(new Color(34, 64, 111)); 
            g.fillRect(0, laserY, slime.x, laserHeight); 

            Rectangle laserRect = new Rectangle(0, laserY, slime.x, laserHeight);
            Rectangle catRect = new Rectangle(cat.x, cat.y, cat.getWidth(), 105); 

            if (laserRect.intersects(catRect) && !cat.getState().equals("jump")) {
                 cat.setHP(slime.getLaserDamage()); 
                 System.out.println("Cat hit by laser! HP: " + cat.getHP());
            }
        }

        // วาด UI
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

        // --- วาดไอคอนพิษ (ย้ายตำแหน่ง) ---
        if (cat.isPoisoned()) {
            // (พิกัด x = 300 (80% ของหลอด HP)
            int poisonX = 60 + (int) (300 * 0.8); // 60 + 240 = 300
            g.drawImage(poisonIcon, poisonX, iconY, this);
        }

        if (!gameState.equals("win")) {
            drawEnemyHPBar(g);
        }

        if (gameState.equals("win")) {
            g.drawImage(exitImage, 25, 20, this);
        }
    }

    // -----------------------------
    // ฟังก์ชันวาดแถบ HP (ของแมว)
    // -----------------------------
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

    // -----------------------------
    // ฟังก์ชันวาดแถบ HP (ของศัตรู)
    // -----------------------------
    private void drawEnemyHPBar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int maxHP = 100; // (เราตั้งค่า Slime HP 100 ตอนสร้าง)
        float currentHP = slime.getHP();
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