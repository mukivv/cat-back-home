import java.awt.*;
import javax.swing.*;

public class Slime extends Enemy {

    private Image currentImg;
    private Image[] stand = new Image[2];
    private Image[] attack = new Image[3];
    private Timer animationTimer;

    private int indexFrame = 0;
    private int standDelay = 600;
    private int attackDelay = 250;

    public int x = 600;
    public int y = 330;

    private boolean shootLaser = false;

    public Slime() {
        super(100, 10, 0,143,105,600,330);

        stand[0] = new ImageIcon("image/slimeStand1.png").getImage();
        stand[1] = new ImageIcon("image/slimeStand2.png").getImage();

        attack[0] = new ImageIcon("image/slimeSkill1.png").getImage();
        attack[1] = new ImageIcon("image/slimeSkill2.png").getImage();
        attack[2] = new ImageIcon("image/slimeSkill3.png").getImage();

        state = "stand";
        currentImg = stand[0];

        animationTimer = new Timer(standDelay, e -> updateFrame());
        animationTimer.start();
    }

    @Override
    protected void updateFrame() {
        Image[] currentFrames = getCurrentFrame();

        if (state.equals("attack")) {
            indexFrame++;
            if (indexFrame >= currentFrames.length) {
                indexFrame = 0;
                setState("stand");
            }
            
            shootLaser = (indexFrame == 2);
            if (indexFrame == 2) {
                Sound.playSoundEffect(0);
            }

        } else {
            indexFrame = (indexFrame + 1) % currentFrames.length;
            shootLaser = false;
        }
        
        currentImg = currentFrames[indexFrame];
    }

    @Override
    protected Image[] getCurrentFrame() {
        if (state.equals("attack")) {
            return attack;
        } else {
            return stand;
        }
    }

    @Override
    public void draw(Graphics g, Component c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(currentImg, x, y, width, height, c);
    }

    @Override
    public void setState(String newState) {
        if (state.equals(newState)) return;

        state = newState;
        indexFrame = 0;

        if (state.equals("attack")) {
            animationTimer.setDelay(attackDelay);
            shootLaser = false;
        } else {
            // "stand"
            animationTimer.setDelay(standDelay);
            shootLaser = false;
        }
    }

    public boolean shootLaser() {
        return shootLaser;
    }

}