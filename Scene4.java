import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;

public class Scene4 extends JPanel implements ActionListener {
    private Cat cat;
    private Floor floor = new Floor();
    private Timer timer;
    private SceneManager manager; 
    private Color blue = new Color(34, 64, 111);

    private DadCat dad;
    
    private String gameState = "fighting"; // สถานะ: fighting, win, lose
    private int enemyDecisionCooldown = 0;
    private final int DECISION_COOLDOWN = 150;
    private String enemyBehavior = "idle"; // พฤติกรรมปัจจุบัน: idle, approaching, attacking
    private boolean enemyMovingRight = true; // ทิศทางการเดินไปมา
    private int enemyIdleTimer = 0; // ตัวนับสำหรับการเดินไปมา

    // --- ตัวแปรสำหรับ Win/Lose ---
    private JButton tryAgainButton;
    private ImageIcon tryAgainIcon;
    private ImageIcon tryAgainClickedIcon;
    private Image exitImage;

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

    public Scene4(SceneManager manager, Cat cat) {
        this.manager = manager; 
        this.cat = cat;
        cat.resetStats();
        setLayout(null); 
        setBackground(Color.WHITE);

        dad = new DadCat();

        loadImages();
        
        setupTryAgainButton();
        setupBlinkTimers();

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
                                manager.showScene5(cat); 
                             }
                        } else {
                            // ถ้ายังสู้, ไม่ให้เดินเลยกลางจอ
                            int catFront = cat.x + cat.getWidth();
                            int dadFront = dad.x;

                            // ถ้าด้านหน้าของแมว เกือบจะชน dad ก็ให้หยุด
                            if (catFront > dadFront) { 
                            cat.x = dadFront - cat.getWidth(); // ให้แมวหยุดพอดี
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
                            int dadFront = dad.x;
                            int distance = dadFront - catFront; // ระยะห่างระหว่างด้านหน้าของแมวกับ Slime

                            // --- 2. ตรวจสอบ MP ---
                            if (cat.useSkill1()) {
                                cat.setState("skill1"); // (แสดงท่าโจมตี ไม่ว่าจะโดนหรือไม่)
                                SFXSound.playSound(1);
                                isSkill1Blinking = true; // <--- เพิ่ม
                                skill1BlinkTimer.start(); // <--- เพิ่ม
                                // --- 3. ตรวจสอบระยะห่าง ---
                                if (distance <= dad.getHitbox()) {
                                    // (ถ้าอยู่ในระยะโจมตี)
                                    dad.takeDamage(cat.getSkill1()); 
                                    System.out.println("Cat attacks! HIT! Dad HP: " + dad.getHP());
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
                                dad.takeDamage(cat.getSkill2()); 
                                System.out.println("Cat uses Meow! HIT! Slime HP: " + dad.getHP());
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
        dad.resetHP(); 
        
        // 3. รีเซ็ตตำแหน่ง
        cat.x = 100;
        cat.y = 330;
        cat.setState("stand");
        
        dad.setState("stand");
        
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
        }
        repaint();
    }

    private void updateEnemyAI() {
    // ลดคูลดาวน์
    if (enemyDecisionCooldown > 0) {
        enemyDecisionCooldown--;
        
        // ระหว่างคูลดาวน์ให้เดินไปมา
        enemyIdleTimer++;
        
        // เปลี่ยนทิศทางทุก 1 วินาที (30 ticks)
        if (enemyIdleTimer >= 30) {
            enemyIdleTimer = 0;
            enemyMovingRight = !enemyMovingRight;
        }
        
        // ตั้งค่า state และทิศทาง
        if (!dad.getState().equals("skill1") && !dad.getState().equals("skill2") && !dad.getState().equals("heal")) {
            dad.setState("walk");
            dad.setDirection(!enemyMovingRight); // หัน facingLeft
            
            if (enemyMovingRight) {
                dad.x += 5; // เพิ่มความเร็วให้เห็นชัดขึ้น
                if (dad.x > 600) { // ขยายขอบเขต
                    dad.x = 600;
                    enemyMovingRight = false;
                }
            } else {

                int dadFront = dad.x;
                int catBack = cat.x + cat.getWidth();

                if (dadFront > catBack) { // เช็กว่ายังไม่ชน cat
                    dad.x -= 5; // เพิ่มความเร็วให้เห็นชัดขึ้น
                }
                if (dad.x < 200) { // ขยายขอบเขต
                    dad.x = 200;
                    enemyMovingRight = true;
                }
            }
        }
        
        return; // ยังไม่ถึงเวลาตัดสินใจ
    }
    
    // ถึงเวลาตัดสินใจแล้ว!
    
    // คำนวณระยะห่างระหว่าง dad กับ cat
    int catFront = cat.x + cat.getWidth();
    int dadFront = dad.x;
    int distance = Math.abs(dadFront - catFront);
    
    // ตัดสินใจตามระยะห่าง
    if (distance > 100) {
        // ไกลเกิน 100 -> ใช้สกิล 2 (โจมตีระยะไกล)
        dad.setState("skill2");
        dad.setDirection(true); // หันหน้าไปทางซ้าย (หาแมว)
        SFXSound.playSound(7);
        
        // ทำดาเมจให้แมวถ้าแมวไม่กดย่อ
        if (!cat.getState().equals("crouch")){
            cat.setHP(dad.getSkill2());
            System.out.println("Dad uses Skill 2! Cat HP: " + cat.getHP());
        }
        
        enemyDecisionCooldown = DECISION_COOLDOWN; // รีเซ็ตคูลดาวน์
        enemyIdleTimer = 0;

    } else {
        // ใกล้กว่า 100 -> เดินเข้าหาแล้วใช้สกิล 1

        // เดินเข้าหาก่อน
        if (distance > 20) {
            dad.setState("walk");
            dad.setDirection(true); // หันหน้าไปทางซ้าย

            int catBack = cat.x + cat.getWidth();

            if (dadFront > catBack) { // เช็กก่อนว่า dad ยังอยู่ด้านขวาของ cat (ยังไม่ชน)
                dad.x -= 5; // ถ้ายังไม่ชน ก็เดินเข้าหา

                // เช็กอีกทีว่าเดินแล้วชนพอดีมั้ย
                if (dad.x < catBack) {
                    dad.x = catBack; // ถ้าเดินแล้วชน ให้หยุดตรงนั้น
                }
            }
        } else {
            // ถึงระยะโจมตีแล้ว -> ใช้สกิล 1
            dad.setState("skill1");
            dad.setDirection(true); // หันหน้าไปทางซ้าย
            SFXSound.playSound(1);

            // ทำดาเมจให้แมวทันที
            cat.setHP(dad.getSkill1());
            System.out.println("Dad uses Skill 1! Cat HP: " + cat.getHP());
            
            enemyDecisionCooldown = DECISION_COOLDOWN; // รีเซ็ตคูลดาวน์
            enemyIdleTimer = 0;
        }
    }
}

    private void checkGameState() {
        if (dad.getHP() <= 0) {
            gameState = "win";
            dad.setState("stand"); 
            System.out.println("YOU WIN");
            SFXSound.playSound(5);
        }

        if (cat.getHP() <= 0) {
            gameState = "lose";
            dad.setState("stand");
            cat.setState("dead");
            System.out.println("GAME OVER");
            tryAgainButton.setVisible(true); 
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        floor.draw(g);
        
        if (!gameState.equals("win")) {
            dad.draw(g, this);
        }
        
        cat.draw(g, this);

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
        int maxHP = (int) dad.getMaxHP();
        float currentHP = dad.getHP();
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