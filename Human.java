import java.awt.*;
import javax.swing.*;

public class Human extends Enemy {

    private Image[] walk;
    private Image dead;
    
    private int animationDelay = 0;
    private int indexFrame = 0;
    private int animationSpeed = 20;
    private int indexCount = 3;
    
    public Human() {
        super(200,160,305);

        walk = new Image[indexCount];
        walk[0] = new ImageIcon("image/humanWalk1.png").getImage();
        walk[1] = new ImageIcon("image/humanWalk2.png").getImage();
        walk[2] = new ImageIcon("image/humanWalk3.png").getImage();

        dead = new ImageIcon("image/humanDead.gif").getImage();

        this.indexCount = 3;
    }

    @Override
    public void draw(Graphics g, Component c) {
        if (this.HP <= 0) {
            int X = this.x - 200;
            int Y = this.y - 19;
            g.drawImage(dead, X, Y, c);
        } else {
            updateFrame();
            Image imageToDraw = walk[indexFrame];
            g.drawImage(imageToDraw, x , y, width, height, c);
        }
    }

    @Override
    protected void updateFrame() {

        animationDelay++;
        
        if (animationDelay > animationSpeed) {
            animationDelay = 0;
            indexFrame++;
            
            if (indexFrame >= indexCount) {
                indexFrame = 0;
            }
        }
    }
    @Override
    protected Image[] getCurrentFrame() {return walk;}
    @Override
    public void setState(String newState) {state = newState;}

}