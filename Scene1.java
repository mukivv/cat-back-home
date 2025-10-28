import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Scene1 extends JPanel implements ActionListener {
    private Cat cat;
    private Floor floor;
    private Timer timer;

    public Scene1(SceneManager manager) {
        setLayout(null); // ใช้ตำแหน่งแบบอิสระ
        setBackground(Color.WHITE);

        // สร้างวัตถุต่าง ๆ
        floor = new Floor();
        cat = new Cat(100, 20, 15, 10);

        // จัดตำแหน่งเริ่มต้นของแมว
        cat.x = 100;
        cat.y = 330;

        // ตั้ง timer ให้ repaint ทุก 30ms
        timer = new Timer(30, this);
        timer.start();

        // ควบคุมการเคลื่อนไหวด้วยคีย์บอร์ด
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_D:
                        cat.setDirection(true);
                        cat.x += 10;
                        if (cat.x + cat.getWidth()/2 > 800) {
                            manager.showScene2(cat);
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
        floor.draw(g);
        cat.draw(g, this);
    }
}
