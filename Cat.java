import java.awt.*;
import javax.swing.*;

public class Cat {
    private Image imgCat; // รูปปัจจุบันของแมว
    private Image[] stand = new Image[2];
    private Image[] walk = new Image[2];
    private Image[] jump = new Image[1];
    private Image[] crouch = new Image[1];
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

    private float maxMP = 100;
    private float maxHP = 100;
    private int mpRegenCounter = 0;
    private int mpRegenTicks = 5; //5*30 scene 3 = ? s re mp

    private boolean facingRight = true; // true = หันขวา, false = หันซ้าย

    private int frameIndex = 0; // เฟรมที่เล่นอยู่
    private String state = "stand"; // สถานะปัจจุบัน: stand, walk, jump, crouch
    private Timer animationTimer;

    private float HP;
    private float MP;
    private final float skill1;
    private final float skill2;
    private final float heal;

    public int x, y; // ตำแหน่งบนหน้าจอ

    public Cat(float HP, float skill1, float skill2, float heal) {
        this.HP = this.maxHP;
        this.MP = this.maxMP;
        this.skill1 = skill1;
        this.skill2 = skill2;
        this.heal = heal;

        // โหลดรูปอนิเมชัน (ตัวอย่างชื่อไฟล์)
        stand[0] = new ImageIcon("image/catStand1.png").getImage();
        stand[1] = new ImageIcon("image/catStand2.png").getImage();

        walk[0] = new ImageIcon("image/catWalkRight1.png").getImage();
        walk[1] = new ImageIcon("image/catWalkRight2.png").getImage();

        jump[0] = new ImageIcon("image/jump.png").getImage();
         
        crouch[0] = new ImageIcon("image/crouch.png").getImage();

        skill1img[0] = new ImageIcon("image/catSkill1.png").getImage();

        skill2img[0] = new ImageIcon("image/catSkill2.png").getImage();
        healimg[0] = new ImageIcon("image/catSkillHeal.png").getImage();

        imgCat = stand[0];

        // ตั้ง Timer เพื่อเปลี่ยนเฟรมทุก 150ms
        animationTimer = new Timer(standDelay, e -> updateFrame());
        animationTimer.start();
    }

    private void updateFrame() {
        if (state.equals("jump")) return;
        Image[] currentFrames = getCurrentFrames();
        frameIndex = (frameIndex + 1) % currentFrames.length;
        imgCat = currentFrames[frameIndex];
    }

    private Image[] getCurrentFrames() {
        switch (state) {
            case "walk":
                return walk;
            case "crouch":
                return crouch;
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

    if (state.equals("skill1")) {
        currentWidth = 162;
        currentHeight = Height;
    } else {
        currentWidth = Width;
        currentHeight = Height;
    }

    // --- 2. ใชขนาดที่เลือกในการวาด ---
    if (facingRight) {
        g2.drawImage(imgCat, x, y, currentWidth, currentHeight, c);
    } else {
        // พลิก horizontal
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
                default:
                    delay = standDelay; break;
            }
            animationTimer.setDelay(delay);
        }
    }

      // --------------------------
    // เปลี่ยนทิศทาง
    // --------------------------
    public void setDirection(boolean right) {
        facingRight = right;
    }

    public String getState() {
        return this.state;
    }

    // --------------------------
    // ฟังก์ชันเกมเพลย์
    // --------------------------

    public void resetStats() {
        this.HP = this.maxHP;
        this.MP = this.maxMP;
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

    public float getMaxMP(){
        return this.maxMP;
    }

    public float getMP(){
        return this.MP;
    }

    public void updateMP() {
        mpRegenCounter++;
        if (mpRegenCounter >= mpRegenTicks) {
            mpRegenCounter = 0;
            if (this.MP < this.maxMP) {
                this.MP += 1;
            }
        }
    }

    public boolean useSkill1() {
        if (this.MP >= 30) {
            this.MP -= 30;
            return true;
        }
        return false;
    }

    public int getWidth(){
        return this.Width;
    }
}
