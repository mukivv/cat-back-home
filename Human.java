import java.awt.*;
import javax.swing.*;

// สมมติว่าสืบทอดมาจากคลาส Enemy ที่มี hp, x, y, state, setState, getState, takeDamage
public class Human extends Enemy {

    // --- Animation Assets ---
    private Image[] walk;
    private Image dead;
    
    // --- Animation Control ---
    private int animationTick = 0;
    private int frameIndex = 0;
    private int animationSpeed = 20; // 20 ticks (จาก Timer 30ms) = 600ms (0.6 วิ) ต่อเฟรม
    private int frameCount = 3; // 3 เฟรม

    // --- Stats (สำหรับตรวจสอบการชน) ---
    private int width = 160; // *ปรับค่านี้* ให้เท่ากับความกว้างจริงของภาพ
    private int height = 305; // *ปรับค่านี้* ให้เท่ากับความสูงจริงของภาพ

    public int x = 600; // ตำแหน่งเริ่มต้น
    public int y = 130;
    
    public Human() {
        // --- โหลดรูปภาพ ---
        // (ให้แน่ใจว่า path ถูกต้อง)
        walk = new Image[frameCount];
        walk[0] = new ImageIcon("image/humanWalk1.png").getImage();
        walk[1] = new ImageIcon("image/humanWalk2.png").getImage();
        walk[2] = new ImageIcon("image/humanWalk3.png").getImage();

        dead = new ImageIcon("image/humanDead.gif").getImage();
        // --- ตั้งค่าเริ่มต้น ---
        this.maxHP = 200;
        this.HP = this.maxHP;
        this.frameCount = 3; // (จำนวนเฟรมเดิน)
    }

    public void draw(Graphics g, Component c) {
        if (this.HP <= 0) {
        // --- ถ้าตาย ---
        int X = this.x - 200;
        int Y = this.y - 19;
        g.drawImage(dead, X, Y, c);
        } else {
        // --- ถ้ายังไม่ตาย (เดิน) ---
        updateAnimation(); // อัปเดตเฟรมแอนิเมชัน
        Image imageToDraw = walk[frameIndex];
        g.drawImage(imageToDraw, x , y, width, height, c);
    }
    }
    
    // --- หัวใจของแอนิเมชัน 3 เฟรม ---
    private void updateAnimation() {

        animationTick++;
        
        if (animationTick > animationSpeed) {
            animationTick = 0;
            frameIndex++; // เลื่อนเฟรม
            
            // ถ้าเล่นครบ 3 เฟรม (0, 1, 2)
            if (frameIndex >= frameCount) {
                frameIndex = 0;       // กลับไปเฟรมแรก
            }
        }
    }

    // --- เมธอดสำหรับตรวจสอบการชน ---
    public int getWidth() {
        // *ปรับค่านี้* ให้เป็น hitbox ที่เหมาะสม
        return this.width; 
    }

    public int getHeight() {
        // *ปรับค่านี้* ให้เป็น hitbox ที่เหมาะสม
        return this.height;
    }

    @Override
    public float getSkill1(Cat c){return (float)0.0;}
    @Override
    public float getSkill2(Cat c){return (float)0.0;}
}