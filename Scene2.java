import java.awt.*;
import javax.swing.*;

public class Scene2 extends Scene1{
    private final Image exitImage = new ImageIcon("image/exit.png").getImage();

    public Scene2(SceneManager manager) {
        super(manager);
    }

    @Override
    protected void nextScene(){
        manager.showScene3(cat);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawFloor(g);
        g.drawImage(exitImage, 25, 20, this);
        cat.draw(g, this);
    }
}
