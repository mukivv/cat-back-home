import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Scene1 extends JPanel implements ActionListener {
    protected Cat cat;
    protected Timer timer;
    protected SceneManager manager; 

    public Scene1(SceneManager manager) {
        setLayout(null);
        setBackground(Color.WHITE);
        this.manager = manager;

        cat = new Cat();

        cat.x = 100;
        cat.y = 330;

        timer = new Timer(30, this);
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D:
                        cat.setDirection(true);
                        cat.x += 10;
                        if (cat.x + cat.getWidth()/2 > 800) {
                            nextScene();
                        }
                        cat.setState("walk");
                        break;
                    case KeyEvent.VK_A:
                        cat.setDirection(false);
                        cat.x -= 10;
                        if (cat.x < 30) cat.x = 30;
                        cat.setState("walk");
                        break;
                    case KeyEvent.VK_W:
                        cat.setState("jump");
                        cat.jump();
                        break;
                    case KeyEvent.VK_S:
                        cat.setState("crouch");
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                cat.setState("stand");
            }
        });

        setFocusable(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawFloor(g);
        cat.draw(g, this);
    }

    protected void nextScene(){
        manager.showScene2(cat);
    }

    public void drawFloor(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        setPreferredSize(new Dimension(800, 600));
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(34, 64, 111));
        g2d.drawLine(0, 436, 800, 436);
    }
}
