import java.awt.*;
import javax.swing.*;

public class Floor extends JPanel {

    

    public Floor() {
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // ล้างพื้นหลัง

        Graphics2D g2d = (Graphics2D) g;

        // ตั้งค่าความหนาของเส้น
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(34, 64, 111));

        // วาดเส้นตรงจาก (x1,y1) ไป (x2,y2)
        g2d.drawLine(0, 400, 800, 400);
    }
}
