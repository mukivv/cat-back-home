import java.awt.*;
import javax.swing.*;

public class Floor extends JPanel {
    public Floor() {
        setPreferredSize(new Dimension(800, 600));
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(34, 64, 111));
        g2d.drawLine(0, 436, 800, 436); // เส้นพื้น
    }
}
