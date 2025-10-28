import java.awt.*;
import javax.swing.*;

public class Slime extends Enemy {

    // --- Animation & State ---
    private Image currentImg;
    private Image[] standFrames = new Image[2];
    private Image[] attackFrames = new Image[3];
    private Timer animationTimer;
    private String state = "stand";
    private int frameIndex = 0;
    private int standDelay = 600; // ms
    private int attackDelay = 250; // ms ต่อเฟรม (เพื่อให้ 3 ภาพแสดงผลเร็วขึ้น)

    // --- Position & Size ---
    public int x = 600; // ตำแหน่งเริ่มต้น
    public int y = 330;
    public int Width = 143;
    public int Height = 105;

    // --- Gameplay State ---
    private boolean isFiringLaser = false;

    public Slime(float HP, float skill1, float skill2, float heal) {
        super(HP, skill1, skill2, heal); // skill1 จะถูกใช้เป็นค่าดาเมจของเลเซอร์

        this.HP = HP; 
        this.maxHP = HP;

        // โหลดภาพอนิเมชันท่ายืน
        standFrames[0] = new ImageIcon("image/slimeStand1.png").getImage();
        standFrames[1] = new ImageIcon("image/slimeStand2.png").getImage();

        // โหลดภาพอนิเมชันท่าโจมตี 3 ภาพ
        attackFrames[0] = new ImageIcon("image/slimeSkill1.png").getImage();
        attackFrames[1] = new ImageIcon("image/slimeSkill2.png").getImage();
        attackFrames[2] = new ImageIcon("image/slimeSkill3.png").getImage();

        currentImg = standFrames[0];

        // สร้าง Timer ของ Slime เอง
        animationTimer = new Timer(standDelay, e -> updateFrame());
        animationTimer.start();
    }

    private void updateFrame() {
        Image[] currentFrames = getCurrentFrames();

        if (state.equals("attack")) {
            frameIndex++;
            if (frameIndex >= currentFrames.length) {
                // ถ้าเล่นอนิเมชันโจมตีจบ
                frameIndex = 0;
                setState("stand"); // กลับไปท่ายืน
            }
            
            // --- Logic สำคัญ: ยิงเลเซอร์ในเฟรมสุดท้าย (index 2) ---
            isFiringLaser = (frameIndex == 2);

        } else {
            // "stand" (ท่ายืน)
            frameIndex = (frameIndex + 1) % currentFrames.length;
            isFiringLaser = false; // ท่ายืน ไม่ยิงเลเซอร์
        }
        
        currentImg = currentFrames[frameIndex];
    }

    private Image[] getCurrentFrames() {
        if (state.equals("attack")) {
            return attackFrames;
        } else {
            return standFrames; // default
        }
    }

    public void draw(Graphics g, Component c) {
        Graphics2D g2 = (Graphics2D) g;
        // วาดแบบกลับด้าน (ให้หันหน้าไปทางซ้ายหาแมว)
        g2.drawImage(currentImg, x, y, Width, Height, c);
    }

    // --- เมธอดสำหรับให้ Scene3 เรียกใช้ ---

    public void setState(String newState) {
        if (state.equals(newState)) return; // ไม่ต้องรีเซ็ตถ้าอยู่ในสถานะเดิม

        state = newState;
        frameIndex = 0; // รีเซ็ตเฟรมอนิเมชัน

        if (state.equals("attack")) {
            animationTimer.setDelay(attackDelay);
            isFiringLaser = false; // เลเซอร์จะยิงแค่ในเฟรมที่ 3
        } else {
            // "stand"
            animationTimer.setDelay(standDelay);
            isFiringLaser = false;
        }
    }

    public String getState() {
        return state;
    }

    public int getHitbox(){
        return super.hitbox;
    }

    // เมธอดนี้สำคัญมาก! Scene3 จะใช้เช็กว่าต้องวาดเลเซอร์ไหม
    public boolean isFiringLaser() {
        return isFiringLaser;
    }

    // เมธอดสำหรับดึงค่าดาเมจ
    public float getLaserDamage() {
        return super.skill1; // ใช้ค่า skill1 ที่เราส่งเข้ามาใน constructor
    }

    // --- เราจะเปลี่ยนเมธอดดั้งเดิม ---
    
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