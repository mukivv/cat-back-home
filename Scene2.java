import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Scene2 extends JPanel implements ActionListener{
    private Cat cat;
    private Floor floor = new Floor();
    private Timer timer;
    private Image exitImage = new ImageIcon("image/exit.png").getImage();

    public Scene2(SceneManager manager,Cat cat) {
        this.cat = cat;
        setLayout(null); // ใช้ตำแหน่งแบบอิสระ
        setBackground(Color.WHITE);

        // จัดตำแหน่งเริ่มต้นของแมว
        cat.x = 100;
        cat.y = 330;

        // ตั้ง timer ให้ repaint ทุก 30ms (ประมาณ 33 FPS)
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
                            manager.showScene3(cat);
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
        g.drawImage(exitImage, 25, 20, this);
        cat.draw(g, this);
    }
}
