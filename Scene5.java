import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Scene5 extends SceneAsset implements ActionListener {
    private int enemyWaitTimer = 100;
    private final int enemyCooldown = 100;
    private final int enemyStepSize = 25;

    public Scene5(SceneManager manager, Cat cat) {
        this.manager = manager;
        this.cat = cat;
        cat.resetStat();
        setLayout(null);
        setBackground(Color.WHITE);

        enemy = new Human();

        loadImages();
        setupTryAgainButton();
        setupBlinkTimers();

        cat.x = 100;
        cat.y = 330;

        enemy.x = 600;
        enemy.y = 130;

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
        cat.setState("walk");
    }

    @Override
    protected void handleJ(KeyEvent e) {
        if (gameState.equals("fighting")) {
            if (!cat.getState().equals("stand")) {
                return;
            }
            if (cat.useSkill1()) {
                cat.setState("skill1");
                Sound.playSoundEffect(1);
                isSkill1Blinking = true;
                skill1BlinkTimer.start();
                System.out.println("Cat attacks! (Ineffective against enemy)");
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
                cat.setState("skill2");
                Sound.playSoundEffect(2);
                isSkill2Blinking = true;
                skill2BlinkTimer.start();
                System.out.println("Cat uses Meow! (Ineffective against enemy)");
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

                if (cat.getDirection()) {
                    enemy.takeDamage(cat.getHeal());
                    System.out.println("Cat uses Heal... to attack! enemy HP: " + enemy.getHP());
                }
            } else {
                System.out.println("NOT ENOUGH MP!");
            }
        }
    }

    @Override
    protected void resetScene() {
        gameState = "fighting";

        cat.resetStat();
        enemy.resetStat();

        cat.x = 100;
        cat.y = 330;

        enemy.x = 600;
        enemy.y = 130;

        cat.setState("stand");

        tryAgainButton.setVisible(false);
        tryAgainButton.setIcon(tryAgainIcon);

        requestFocusInWindow();
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

        if (cat.getWidth() + cat.x > enemy.x + 100) {
            cat.setHP(100);
        }
        enemyWaitTimer--;

        if (enemyWaitTimer <= 0) {
            enemy.x -= enemyStepSize;
            enemyWaitTimer = enemyCooldown;
        }
    }

    @Override
    protected void checkGameState() {
        if (enemy.getHP() <= 0) {
            gameState = "win";
            System.out.println("YOU WIN");

            Timer winTimer = new Timer(6000, (ActionEvent ev) -> {
                manager.showScene6();
            });
            winTimer.setRepeats(false);
            winTimer.start();
        }

        if (cat.getHP() <= 0) {
            gameState = "lose";
            cat.setState("dead");
            System.out.println("GAME OVER");
            tryAgainButton.setVisible(true);
        }
    }
}
