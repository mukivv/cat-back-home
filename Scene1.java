import java.awt.*;
import javax.swing.*;

public class Scene1 extends JPanel {
    public Scene1() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        Floor floor = new Floor();
        add(floor, BorderLayout.CENTER); // เพิ่ม Floor ลงใน Scene1

        
    }
}
