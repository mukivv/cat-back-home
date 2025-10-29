import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Scene4 extends SceneAsset implements ActionListener {
    private int enemyDecisionCooldown = 0;
    private final int decisionCooldown = 150;
    private boolean enemyMovingRight = true;
    private int enemyIdleTimer = 0;

    public Scene4(SceneManager manager, Cat cat) {
        this.manager = manager;
        this.cat = cat;
        cat.resetStat();
        setLayout(null);
        setBackground(Color.WHITE);

        enemy = new DadCat();

        loadImages();

        setupTryAgainButton();
        setupBlinkTimers();

        cat.x = 100;
        cat.y = 330;
        cat.setState("stand");

        timer = new Timer(30, this);
        timer.start();

        setupKeyListener();

        setFocusable(true);
    }

    @Override
    protected void handleD(KeyEvent e) {
        cat.setDirection(true);
        cat.x += 10;

        if (gameState.equals("win")) {
            if (cat.x + cat.getWidth() / 2 > 800) {
                timer.stop();
                manager.showScene5(cat);
            }
        } else {
            int catFront = cat.x + cat.getWidth();
            int enemyFront = enemy.x;

            if (catFront > enemyFront) {
                cat.x = enemyFront - cat.getWidth();
            }
        }
        cat.setState("walk");
    }

    @Override
    protected void handleJ(KeyEvent e) {
        if (gameState.equals("fighting")) {
            if (!cat.getState().equals("stand")) {
                return;
            }

            int catFront = cat.x + cat.getWidth();
            int enemyFront = enemy.x;
            int distance = enemyFront - catFront;

            if (cat.useSkill1()) {
                cat.setState("skill1");
                Sound.playSoundEffect(1);
                isSkill1Blinking = true;
                skill1BlinkTimer.start();
                if (distance <= enemy.getHitbox() && cat.getDirection()) {
                    enemy.takeDamage(cat.getSkill1());
                    System.out.println("Cat attacks! HIT! enemy HP: " + enemy.getHP());
                } else {
                    System.out.println("Cat attacks! MISS! (Too far)");
                }
            } else {
                System.out.println("NOT ENOUGH MP!");
            }
        }
    }

    @Override
    protected void handleK(KeyEvent e) {
        if (gameState.equals("fighting")) {
            if (!cat.getState().equals("stand")) {
                return;
            }

            if (cat.useSkill2()) {
                Sound.playSoundEffect(2);
                cat.setState("skill2");
                isSkill2Blinking = true;
                skill2BlinkTimer.start();

                if (cat.getDirection()) {
                    enemy.takeDamage(cat.getSkill2());
                    System.out.println("Cat uses Meow! HIT! Slime HP: " + enemy.getHP());
                }
            } else {
                System.out.println("NOT ENOUGH MP!");
            }
        }
    }

    @Override
    protected void handleL(KeyEvent e) {
        if (gameState.equals("fighting")) {
            if (!cat.getState().equals("stand")) {
                return;
            }

            if (cat.useHeal()) {
                Sound.playSoundEffect(3);
                cat.setState("heal");
                isHealBlinking = true;
                healBlinkTimer.start();

                cat.Heal();
                System.out.println("Cat heals! HP: " + cat.getHP());
            } else {
                System.out.println("NOT ENOUGH MP!");
            }
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

    @Override
    protected void updateEnemyAI() {
        if (enemyDecisionCooldown > 0) {
            enemyDecisionCooldown--;

            enemyIdleTimer++;

            if (enemyIdleTimer >= 30) {
                enemyIdleTimer = 0;
                enemyMovingRight = !enemyMovingRight;
            }

            if (!enemy.getState().equals("skill1") && !enemy.getState().equals("skill2") && !enemy.getState().equals("heal")) {
                enemy.setState("walk");
                ((DadCat) enemy).setDirection(!enemyMovingRight);

                if (enemyMovingRight) {
                    enemy.x += 5;
                    if (enemy.x > 600) {
                        enemy.x = 600;
                        enemyMovingRight = false;
                    }
                } else {

                    int enemyFront = enemy.x;
                    int catBack = cat.x + cat.getWidth();

                    if (enemyFront > catBack) {
                        enemy.x -= 5;
                    }
                    if (enemy.x < 200) {
                        enemy.x = 200;
                        enemyMovingRight = true;
                    }
                }
            }

            return;
        }

        int catFront = cat.x + cat.getWidth();
        int enemyFront = enemy.x;
        int distance = Math.abs(enemyFront - catFront);

        if (distance > 100) {
            enemy.setState("skill2");
            ((DadCat) enemy).setDirection(true);
            Sound.playSoundEffect(7);

            if (!cat.getState().equals("crouch")) {
                cat.setHP(enemy.getSkill2());
                System.out.println("enemy uses Skill 2! Cat HP: " + cat.getHP());
            }

            enemyDecisionCooldown = decisionCooldown;
            enemyIdleTimer = 0;

        } else {

            if (distance > 20) {
                enemy.setState("walk");
                ((DadCat) enemy).setDirection(true);

                int catBack = cat.x + cat.getWidth();

                if (enemyFront > catBack) {
                    enemy.x -= 5;
                    if (enemy.x < catBack) {
                        enemy.x = catBack;
                    }
                }
            } else {
                enemy.setState("skill1");
                ((DadCat) enemy).setDirection(true);
                Sound.playSoundEffect(1);

                cat.setHP(enemy.getSkill1());
                System.out.println("enemy uses Skill 1! Cat HP: " + cat.getHP());

                enemyDecisionCooldown = decisionCooldown;
                enemyIdleTimer = 0;
            }
        }
    }
}
