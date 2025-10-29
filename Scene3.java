import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Scene3 extends SceneAsset implements ActionListener {
    private int enemyAttackCooldown = 100;

    public Scene3(SceneManager manager, Cat cat) {
        this.manager = manager;
        this.cat = cat;
        setLayout(null);
        setBackground(Color.WHITE);

        enemy = new Slime();

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
                manager.showScene4(cat);
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
                    cat.applyPoison();
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
                    System.out.println("Cat uses Meow! HIT! enemy HP: " + enemy.getHP());
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

            cat.updatePoisonStatus();
        }
        repaint();
    }

    @Override
    protected void updateEnemyAI() {
        if (enemyAttackCooldown > 0) {
            enemyAttackCooldown--;
        }

        if (enemyAttackCooldown == 0 && enemy.getState().equals("stand")) {
            enemy.setState("attack");
            enemyAttackCooldown = 100 + (int) (Math.random() * 50);
        }
    }
}
