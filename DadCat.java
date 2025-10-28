import java.awt.*;
import javax.swing.*;

public class DadCat extends Enemy {
    private Image imgCat; // รูปปัจจุบันของแมว
    private Image[] stand = new Image[2];
    private Image[] walk = new Image[2];
    private Image[] jump = new Image[1];
    private Image[] skill1img = new Image[1];
    private Image[] skill2img = new Image[1];
    private Image[] healimg = new Image[1];

    private boolean isJumping = false;
    private int jumpVelocity = 0;

    private int Width = 143;
    private int Height = 105;

    private int standDelay = 600; // ms
    private int walkDelay = 150;
    private int jumpDelay = 300;
    private int crouchDelay = 300;
    private int skill1Delay = 150;
    private int skill2Delay = 300;
    private int healDelay = 300;

    private boolean facingLeft = true; // true = หันซ้าย, false = หันขวา

    private int frameIndex = 0; // เฟรมที่เล่นอยู่
    private String state = "stand"; // สถานะปัจจุบัน: stand, walk, jump, crouch
    private Timer animationTimer;

    public int x = 600; // ตำแหน่งเริ่มต้น
    public int y = 330;

    public DadCat() {
        super(150, 30, 30, 20);
        this.HP = this.maxHP;

        // โหลดรูปอนิเมชัน (ตัวอย่างชื่อไฟล์)
        stand[0] = new ImageIcon("image/DadStand1.png").getImage();
        stand[1] = new ImageIcon("image/DadStand2.png").getImage();

        walk[0] = new ImageIcon("image/DadWalkRight1.png").getImage();
        walk[1] = new ImageIcon("image/DadWalkRight2.png").getImage();

        jump[0] = new ImageIcon("image/jump.png").getImage();
         
        jump[0] = new ImageIcon("image/Dadjump.png").getImage();

        skill1img[0] = new ImageIcon("image/DadSkill1.png").getImage();
        skill2img[0] = new ImageIcon("image/DadSkill2.png").getImage();
        healimg[0] = new ImageIcon("image/DadSkillHeal.png").getImage();

        imgCat = stand[0];

        // ตั้ง Timer เพื่อเปลี่ยนเฟรมทุก 150ms
        animationTimer = new Timer(standDelay, e -> updateFrame());
        animationTimer.start();
    }

    private void updateFrame() {
        if (state.equals("jump")) return;
        Image[] currentFrames = getCurrentFrames();
        if (state.equals("skill1") || state.equals("skill2") || state.equals("heal")) {
        // --- Logic สำหรับเล่นอนิเมชั่นครั้งเดียว (สำหรับโจมตี) ---
        frameIndex++;
        if (frameIndex >= currentFrames.length) {
            // เมื่อเล่นจนจบ
            frameIndex = 0; // รีเซ็ตเฟรม
            setState("stand"); // กลับไปท่ายืน (ซึ่งจะเปลี่ยนรูปทันทีเพราะเราแก้ setState แล้ว)
        } else {
            imgCat = currentFrames[frameIndex];
        }
         } else {
        // --- Logic สำหรับเล่นวนซ้ำ (ยืน, เดิน, หมอบ) ---
            frameIndex = (frameIndex + 1) % currentFrames.length;
            imgCat = currentFrames[frameIndex];
        }
    }

    private Image[] getCurrentFrames() {
        switch (state) {
            case "walk":
                return walk;
            case "skill1":
                return skill1img;
            case "skill2":
                return skill2img;
            case "heal":
                return healimg;
            default:
                return stand;
        }
    }

    public void jump() {
        if (!isJumping) {
            jumpVelocity = -15; // ยกตัวขึ้น
            isJumping = true;
            imgCat = jump[0];
            setState("jump");
        }
    }

    public void updatePosition() {
        if (isJumping) {
            y += jumpVelocity;
            imgCat = jump[0];
            jumpVelocity += 1; // gravity
            if (y >= 330) {   // floor level
                 y = 330;
                 isJumping = false;
                 setState("stand");
            }
        }
    }

    public void draw(Graphics g, Component c) {
        updatePosition();
        Graphics2D g2 = (Graphics2D) g;
        // --- 1. เลือกขนาดภาพตามสถานะ ---
    int currentWidth;
    int currentHeight;

    if (state.equals("skill1") || state.equals("skill2") || state.equals("heal")) {
        currentWidth = 162;
        currentHeight = Height;
    } else {
        currentWidth = Width;
        currentHeight = Height;
    }

    // --- 2. ใชขนาดที่เลือกในการวาด ---
    if (facingLeft) {
        g2.drawImage(imgCat, x + currentWidth, y, -currentWidth, currentHeight, c);
    } else {
        g2.drawImage(imgCat, x + currentWidth, y, -currentWidth, currentHeight, c);
    }
    }


    // --------------------------
    // สั่งเปลี่ยนสถานะอนิเมชัน
    // --------------------------
    public void setState(String newState) {
        if (!state.equals(newState)) {
            state = newState;
            frameIndex = 0;

            // ปรับความเร็ว Timer ตาม state
            int delay;
            switch (state) {
                case "walk":
                    delay = walkDelay; break;
                case "jump":
                    delay = jumpDelay; break;
                case "crouch":
                    delay = crouchDelay; break;
                case "skill1":
                    delay = skill1Delay; break;
                case "skill2":
                    delay = skill2Delay; break;
                case "skill3":
                    delay = healDelay; break;
                default:
                    delay = standDelay; break;
            }
            animationTimer.setDelay(delay);
            // --- 2 บรรทัดที่เพิ่มเข้ามา (สำคัญมาก!) ---
            // 1. สั่งให้เปลี่ยนรูปเป็นเฟรมแรกของท่าใหม่ "ทันที"
            imgCat = getCurrentFrames()[frameIndex]; 
            // 2. สั่งให้ timer เริ่มนับใหม่ด้วย delay ค่าใหม่ "ทันที"
            animationTimer.restart();
        }
    }

      // --------------------------
    // เปลี่ยนทิศทาง
    // --------------------------
    public void setDirection(boolean left) {
        facingLeft = left;
    }

    public String getState() {
        return this.state;
    }

    // --------------------------
    // ฟังก์ชันเกมเพลย์
    // --------------------------

    public void resetStats() {
        this.HP = this.maxHP;
    }

    public float getSkill1() {
        return this.skill1;
    }

    public float getSkill2() {
        return this.skill2;
    }

    public float getHeal() {
        this.HP += this.heal;
        return this.HP;
    }

    public float setHP(float damage) {
        this.HP -= damage;
        return this.HP;
    }

    public float getHP(){
        return this.HP;
    }

    public float getMaxHP(){
        return this.maxHP;
    }

    public int getWidth(){
        return this.Width;
    }

    public int getHeight(){
        return this.Height;
    }

    @Override
    public float getSkill1(Cat cat) {
        // เมธอดนี้จะไม่ทำดาเมจตรงๆ แล้ว แต่จะใช้เป็น "ตัวสั่ง" ให้เริ่มโจมตี
        setState("attack");
        return 0; // ไม่คืนค่าดาเมจ เพราะดาเมจจะเกิดตอนเลเซอร์โดนแมว
    }

    @Override
    public float getSkill2(Cat cat) {
        // (เผื่อไว้ ถ้ามีสกิล 2)
        return cat.setHP(super.skill2); 
    }
}
