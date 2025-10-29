import java.awt.*;

public abstract class Enemy {
    protected int HP;
    protected int skill1;
    protected int skill2;
    protected int maxHP;
    protected int hitbox = 20;
    protected int width;
    protected int height;
    protected int x;
    protected int y;

    protected String state;

    public Enemy(int maxHP, int skill1, int skill2,int wight,int height,int x,int y) {
        this.maxHP = maxHP;
        this.HP = maxHP;
        this.skill1 = skill1;
        this.width = wight;
        this.height = height;
        this.skill2 = skill2;
        this.x = x;
        this.y = y;
    }

    public Enemy(int maxHP,int wight,int height) {
        this.maxHP = maxHP;
        this.HP = maxHP;
        this.width = wight;
        this.height = height;
    }

    abstract protected void updateFrame();
    abstract protected Image[] getCurrentFrame();
    abstract public void draw(Graphics g, Component c);
    abstract public void setState(String newState);

    public int getHP() {
        return this.HP;
    }

    public int getMaxHP() {
        return this.maxHP;
    }

    public int getSkill1() {
        return this.skill1;
    }

    public int getSkill2() {
        return this.skill2;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void resetStat() {
        this.HP = this.maxHP;
    }

    public String getState() {
        return state;
    }

    public void takeDamage(int damage) {
        this.HP -= damage;
    }

    public int getHitbox(){
        return hitbox;
    }

    public void drawHPBar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (HP < 0) HP = 0;
        if (HP > maxHP) HP = maxHP;
        int barWidth = 300;
        int barHeight = 25;
        int x = 800 - barWidth - 60; 
        int y = 30;
        int hpWidth = (int) (( (double)HP / maxHP) * barWidth);
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, barWidth, barHeight);
        g2.setColor(new Color(34, 64, 111));
        g2.fillRect(x, y, hpWidth, barHeight);
        g2.drawRect(x, y, barWidth, barHeight);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        g2.drawString("HP", x + barWidth + 10, y + 20);
    }
}
