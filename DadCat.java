import java.awt.*;
import javax.swing.*;

public class DadCat extends Enemy {

    private Image imgCat; // รูปปัจจุบันของแมว
    private Image[] stand = new Image[2];
    private Image[] walk = new Image[2];
    private Image[] jump = new Image[1];
    private Image[] skill1img = new Image[1];
    private Image[] skill2img = new Image[1];

    private boolean isJumping = false;
    private int jumpVelocity = 0;

    private int standDelay = 600; // ms
    private int walkDelay = 150;
    private int skill1Delay = 150;
    private int skill2Delay = 300;

    private boolean DirectionLeft = true;

    private int indexFrame = 0;
    private Timer animationTimer;

    public DadCat() {
        super(150, 30, 30,143,105,600,330);

        stand[0] = new ImageIcon("image/DadStand1.png").getImage();
        stand[1] = new ImageIcon("image/DadStand2.png").getImage();

        walk[0] = new ImageIcon("image/DadWalkRight1.png").getImage();
        walk[1] = new ImageIcon("image/DadWalkRight2.png").getImage();

        skill1img[0] = new ImageIcon("image/DadSkill1.png").getImage();
        skill2img[0] = new ImageIcon("image/DadSkill2.png").getImage();

        state = "stand";
        imgCat = stand[0];

        animationTimer = new Timer(standDelay, e -> updateFrame());
        animationTimer.start();
    }

    @Override
    protected void updateFrame() {
        if (state.equals("jump")) {
            return;
        }
        Image[] currentFrames = getCurrentFrame();
        if (state.equals("skill1") || state.equals("skill2")) {
            indexFrame++;
            if (indexFrame >= currentFrames.length) {
                indexFrame = 0;
                setState("stand");
            } else {
                imgCat = currentFrames[indexFrame];
            }
        } else {
            indexFrame = (indexFrame + 1) % currentFrames.length;
            imgCat = currentFrames[indexFrame];
        }
    }

    @Override
    protected Image[] getCurrentFrame() {
        switch (state) {
            case "walk":
                return walk;
            case "skill1":
                return skill1img;
            case "skill2":
                return skill2img;
            default:
                return stand;
        }
    }

    public void jump() {
        if (!isJumping) {
            jumpVelocity = -15;
            isJumping = true;
            imgCat = jump[0];
            setState("jump");
        }
    }

    public void updatePosition() {
        if (isJumping) {
            y += jumpVelocity;
            imgCat = jump[0];
            jumpVelocity += 1;
            if (y >= 330) {
                y = 330;
                isJumping = false;
                setState("stand");
            }
        }
    }

    @Override
    public void draw(Graphics g, Component c) {
        updatePosition();
        Graphics2D g2 = (Graphics2D) g;
        int currentWidth;
        int currentHeight;

        if (state.equals("skill1") || state.equals("skill2") || state.equals("heal")) {
            currentWidth = 162;
            currentHeight = height;
        } else {
            currentWidth = width;
            currentHeight = height;
        }

        if (DirectionLeft) {
            g2.drawImage(imgCat, x + currentWidth, y, -currentWidth, currentHeight, c);
        } else {
            g2.drawImage(imgCat, x + currentWidth, y, -currentWidth, currentHeight, c);
        }
    }
    
    @Override
    public void setState(String newState) {
        if (!state.equals(newState)) {
            state = newState;
            indexFrame = 0;

            int delay;
            switch (state) {
                case "walk":
                    delay = walkDelay;
                    break;
                case "skill1":
                    delay = skill1Delay;
                    break;
                case "skill2":
                    delay = skill2Delay;
                    break;
                default:
                    delay = standDelay;
                    break;
            }
            animationTimer.setDelay(delay);
            imgCat = getCurrentFrame()[indexFrame];
            animationTimer.restart();
        }
    }

    public void setDirection(boolean d) {
        DirectionLeft = d;
    }

    public int setHP(float damage) {
        this.HP -= damage;
        return this.HP;
    }
}
