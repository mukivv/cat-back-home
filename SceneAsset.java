import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public abstract class SceneAsset extends JPanel {

    protected JButton tryAgainButton;
    protected ImageIcon tryAgainIcon;
    protected ImageIcon tryAgainClickedIcon;
    protected Image exitImage;
    protected Image poisonIcon;

    protected Image iconSkill1;
    protected Image iconSkill1_click;
    protected Image iconSkill2;
    protected Image iconSkill2_click;
    protected Image iconHeal;
    protected Image iconHeal_click;

    protected Timer skill1BlinkTimer, skill2BlinkTimer, healBlinkTimer;
    protected boolean isSkill1Blinking = false;
    protected boolean isSkill2Blinking = false;
    protected boolean isHealBlinking = false;

    protected String gameState = "fighting";
    protected Cat cat;
    protected Enemy enemy;
    protected Color blue = new Color(34, 64, 111);

    protected SceneManager manager;
    protected Timer timer;

    public void drawFloor(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setPreferredSize(new Dimension(800, 600));
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(34, 64, 111));
        g2d.drawLine(0, 436, 800, 436);
    }

    protected void loadImages() {
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

    protected void setupTryAgainButton() {
        tryAgainButton = new JButton(tryAgainIcon);
        tryAgainButton.setBounds(250, 200, tryAgainIcon.getIconWidth(), tryAgainIcon.getIconHeight());

        tryAgainButton.setBorderPainted(false);
        tryAgainButton.setContentAreaFilled(false);
        tryAgainButton.setFocusPainted(false);

        tryAgainButton.setVisible(false);

        tryAgainButton.addActionListener(e -> {
            Sound.playSoundEffect(8);
            tryAgainButton.setIcon(tryAgainClickedIcon);

            Timer resetTimer = new Timer(500, ev -> resetScene());
            resetTimer.setRepeats(false);
            resetTimer.start();
        });

        add(tryAgainButton);
    }

    protected void setupBlinkTimers() {
        int blinkDuration = 200;

        skill1BlinkTimer = new Timer(blinkDuration, e -> isSkill1Blinking = false);
        skill1BlinkTimer.setRepeats(false);

        skill2BlinkTimer = new Timer(blinkDuration, e -> isSkill2Blinking = false);
        skill2BlinkTimer.setRepeats(false);

        healBlinkTimer = new Timer(blinkDuration, e -> isHealBlinking = false);
        healBlinkTimer.setRepeats(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawFloor(g);

        if (!gameState.equals("win") || enemy instanceof Human) {
            enemy.draw(g, this); 
        }

        if (!gameState.equals("win")) {
            enemy.drawHPBar(g);
        }

        if (gameState.equals("win") && !(enemy instanceof Human)) {
            g.drawImage(exitImage, 25, 20, this);
        }

        cat.draw(g, this);

        if (enemy instanceof Slime) {
            if (((Slime) enemy).shootLaser() && !gameState.equals("win")) {
                int laserY = enemy.y + 78;
                int laserHeight = 13;

                g.setColor(new Color(34, 64, 111));
                g.fillRect(0, laserY, enemy.x, laserHeight);

                Rectangle laserRect = new Rectangle(0, laserY, enemy.x, laserHeight);
                Rectangle catRect = new Rectangle(cat.x, cat.y, cat.getWidth(), 105);

                if (laserRect.intersects(catRect) && !cat.getState().equals("jump")) {
                    cat.setHP(enemy.getSkill1());
                    System.out.println("Cat hit by laser! HP: " + cat.getHP());
                }
            }
        }

        cat.drawHPBar(g);
        cat.drawMPBar(g);

        int iconY = 90;
        int iconX1 = 60;
        int iconX2 = 100;
        int iconX3 = 140;

        if (isSkill1Blinking) {
            g.drawImage(iconSkill1_click, iconX1, iconY, this);
        } else if (cat.getMP() < 30) {
            g.drawImage(iconSkill1_click, iconX1, iconY, this);
        } else {
            g.drawImage(iconSkill1, iconX1, iconY, this);
        }

        if (isSkill2Blinking) {
            g.drawImage(iconSkill2_click, iconX2, iconY, this);
        } else if (cat.getMP() < 30) {
            g.drawImage(iconSkill2_click, iconX2, iconY, this);
        } else {
            g.drawImage(iconSkill2, iconX2, iconY, this);
        }

        if (isHealBlinking) {
            g.drawImage(iconHeal_click, iconX3, iconY, this);
        } else if (cat.getMP() < 40) {
            g.drawImage(iconHeal_click, iconX3, iconY, this);
        } else {
            g.drawImage(iconHeal, iconX3, iconY, this);
        }

        if (cat.isPoisoned()) {
            int poisonX = iconX1 + 240;
            g.drawImage(poisonIcon, poisonX, iconY, this);
        }
    }

    protected void checkGameState() {
        if (enemy.getHP() <= 0) {
            gameState = "win";
            enemy.setState("stand");
            System.out.println("YOU WIN");
            Sound.playSoundEffect(5);
        }

        if (cat.getHP() <= 0) {
            gameState = "lose";
            cat.setState("dead");
            enemy.setState("stand");
            System.out.println("GAME OVER");
            tryAgainButton.setVisible(true);
        }
    }

    protected void resetScene() {
        gameState = "fighting";

        cat.resetStat();
        enemy.resetStat();

        cat.x = 100;
        cat.y = 330;

        cat.setState("stand");
        if (!(enemy instanceof Human)) {
            enemy.setState("stand");
        }

        tryAgainButton.setVisible(false);
        tryAgainButton.setIcon(tryAgainIcon);

        requestFocusInWindow();
    }

    protected void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameState.equals("lose")) {
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                        cat.setDirection(false);
                        cat.x -= 10;
                        if (cat.x < 30) {
                            cat.x = 30;
                        }
                        cat.setState("walk");
                        break;
                    case KeyEvent.VK_W:
                        cat.setState("jump");
                        cat.jump();
                        break;
                    case KeyEvent.VK_S:
                        cat.setState("crouch");
                        break;

                    case KeyEvent.VK_D:
                        handleD(e);
                        break;
                    case KeyEvent.VK_J:
                        handleJ(e);
                        break;
                    case KeyEvent.VK_K:
                        handleK(e);
                        break;
                    case KeyEvent.VK_L:
                        handleL(e);
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (gameState.equals("lose")) {
                    return;
                }

                if (cat.getState().equals("jump")
                        || cat.getState().equals("skill1")
                        || cat.getState().equals("skill2")
                        || cat.getState().equals("heal")) {
                    return;
                }
                cat.setState("stand");
            }
        });
    }

    abstract protected void updateEnemyAI();

    abstract protected void handleD(KeyEvent e);

    abstract protected void handleJ(KeyEvent e);

    abstract protected void handleK(KeyEvent e);

    abstract protected void handleL(KeyEvent e);
}
