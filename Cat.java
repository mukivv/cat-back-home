import java.awt.*;
import javax.swing.*;

public class Cat {
    private Image imgCat;
    private Image[] stand = new Image[2];
    private Image[] walk = new Image[2];
    private Image[] jump = new Image[1];
    private Image[] crouch = new Image[1];
    private Image[] skill1img = new Image[1];
    private Image[] skill2img = new Image[1];
    private Image[] healimg = new Image[1];
    private Image[] dead = new Image[1];

    private boolean isJumping = false;
    private int jumpVelocity = 0;

    private int width = 143;
    private int height = 105;

    private int standDelay = 600;
    private int walkDelay = 150;
    private int jumpDelay = 300;
    private int crouchDelay = 300;
    private int skill1Delay = 150;
    private int skill2Delay = 300;
    private int healDelay = 300;

    private boolean isPoisoned = false;
    private int poisonNumLeft = 0;
    private int poisonTimer = 0;
    private final int poisonTime = 67;
    private final int poisonDamage = 10;
    private final int poisonNum = 5;

    private int maxMP = 100;
    private int maxHP = 100;

    private int mpRegenCount = 0;
    private int mpRegenNum = 5;

    private boolean direction = true;

    private int indexFrame = 0;
    private String state = "stand";
    private Timer animationTimer;

    private int HP;
    private int MP;
    private int skill1 = 20;
    private int skill2 = 10;
    private int heal = 20;

    public int x, y;

    public Cat() {
        this.HP = this.maxHP;
        this.MP = this.maxMP;

        stand[0] = new ImageIcon("image/catStand1.png").getImage();
        stand[1] = new ImageIcon("image/catStand2.png").getImage();

        walk[0] = new ImageIcon("image/catWalkRight1.png").getImage();
        walk[1] = new ImageIcon("image/catWalkRight2.png").getImage();

        jump[0] = new ImageIcon("image/jump.png").getImage();
         
        crouch[0] = new ImageIcon("image/crouch.png").getImage();

        skill1img[0] = new ImageIcon("image/catSkill1.png").getImage();

        skill2img[0] = new ImageIcon("image/catSkill2.png").getImage();
        healimg[0] = new ImageIcon("image/catSkillHeal.png").getImage();

        dead[0] = new ImageIcon("image/catDead.png").getImage();

        imgCat = stand[0];

        animationTimer = new Timer(standDelay, e -> updateFrame());
        animationTimer.start();
    }

    private void updateFrame() {
        if (state.equals("jump")) { return;}
        Image[] currentFrame = getCurrentFrame();
        if (state.equals("skill1") || state.equals("skill2") || state.equals("heal")) {
            indexFrame++;
            if (indexFrame >= currentFrame.length) {
                indexFrame = 0;
                setState("stand");
            } else {
                imgCat = currentFrame[indexFrame];
            }
        } else {
            indexFrame = (indexFrame + 1) % currentFrame.length;
            imgCat = currentFrame[indexFrame];
        }
    }

    private Image[] getCurrentFrame() {
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
            case "dead":
                return dead;
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

    public void draw(Graphics g, Component c) {
        updatePosition();
        int currentWidth;
        int currentHeight;

        if (state.equals("skill1") || state.equals("skill2") || state.equals("heal")) {
            currentWidth = 162;
            currentHeight = height;
        } else {
            currentWidth = width;
            currentHeight = height;
        }

        if (direction) {
            g.drawImage(imgCat, x, y, currentWidth, currentHeight, c);
        } else {
            g.drawImage(imgCat, x + currentWidth, y, -currentWidth, currentHeight, c);
        }
    }

    public void setState(String newState) {
        if (!state.equals(newState)) {
            state = newState;
            indexFrame = 0;

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

            imgCat = getCurrentFrame()[indexFrame]; 
            animationTimer.restart();
        }
    }

    public void setDirection(boolean right) {
        direction = right;
    }

    public boolean getDirection() {
        return direction;
    }

    public void resetStat() {
        this.HP = this.maxHP;
        this.MP = this.maxMP;
        this.isPoisoned = false;
        this.poisonNumLeft = 0;
        this.poisonTimer = 0;
    }

    public int getSkill1() {
        return this.skill1;
    }

    public int getSkill2() {
        return this.skill2;
    }

    public int getHeal(){
        return this.heal;
    }

    public int Heal() {
        this.HP += this.heal;
        return this.HP;
    }

    public int setHP(int damage) {
        this.HP -= damage;
        return this.HP;
    }

    public int getHP(){
        return this.HP;
    }

    public int getMaxHP(){
        return this.maxHP;
    }

    public int getMaxMP(){
        return this.maxMP;
    }

    public int getMP(){
        return this.MP;
    }

    public void updateMP() {
        mpRegenCount++;
        if (mpRegenCount >= mpRegenNum) {
            mpRegenCount = 0;
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

    public boolean useSkill2() {
        if (this.MP >= 30) {
            this.MP -= 30;
            return true;
        }
        return false;
    }

    public boolean useHeal() {
        if (this.MP >= 40) {
            this.MP -= 40;
            return true;
        }
        return false;
    }


    public void applyPoison() {
        if (isPoisoned) return;

        isPoisoned = true;
        poisonNumLeft = poisonNum;
        poisonTimer = poisonTime;
        
        Sound.playSoundEffect(6);
        System.out.println("Cat is POISONED!");
    }

    public void updatePoisonStatus() {
        if (!isPoisoned) return;

        poisonTimer--;
        
        if (poisonTimer <= 0) {
            this.setHP(poisonDamage);
            System.out.println("Poison tick! HP: " + this.getHP());
            
            poisonNumLeft--;
            
            if (poisonNumLeft <= 0) {
                isPoisoned = false;
                System.out.println("Poison has worn off.");
            } else {
                poisonTimer = poisonTime;
            }
        }
    }

    public boolean isPoisoned() {
        return isPoisoned;
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public String getState() {
        return this.state;
    }

    public void drawHPBar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (HP < 0) HP = 0;
        if (HP > maxHP) HP = maxHP;
        int barWidth = 300;
        int barHeight = 25; 
        int x = 60, y = 30;
        int hpWidth = (int) (( (double)HP / maxHP) * barWidth);
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, barWidth, barHeight);
        g2.setColor(new Color(34, 64, 111));
        g2.fillRect(x, y, hpWidth, barHeight);
        g2.drawRect(x, y, barWidth, barHeight);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        g2.drawString("HP", x - 40, y + 20);
    }

    public void drawMPBar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (MP < 0) MP = 0;
        if (MP > maxMP) MP = maxMP;
        int barWidth = 250;
        int barHeight = 15;
        int x = 60, y = 65; 
        int mpWidth = (int) (( (double)MP / maxMP) * barWidth);
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, barWidth, barHeight);
        g2.setColor(new Color(34, 64, 111));
        g2.fillRect(x, y, mpWidth, barHeight);
        g2.drawRect(x, y, barWidth, barHeight);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        g2.drawString("MP", x - 40, y + 20);
    }
}
