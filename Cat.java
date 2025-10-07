import java.awt.*;
import javax.swing.*;

public class Cat {
    Image imgCat = new ImageIcon("cat.png").getImage();
    Image stand[] = new Image[2];
    private float HP;
    final private float skill1;
    final private float skill2;
    final private float heal;

    public Cat(float HP, float skill1, float skill2, float heal) {
        this.HP = HP;
        this.skill1 = skill1;
        this.skill2 = skill2;
        this.heal = heal;
        stand[0] = new ImageIcon("catStand1.png").getImage();
        stand[1] = new ImageIcon("catStand1.png").getImage();
    }

    public float getSkill1() { //ใช้สกิล 1 โจมตี
        return this.skill1;
    }

    public float getSkill2() { //ใช้สกิล 2 โจมตี
        return this.skill2;
    }

    public float getHeal() { //ใช้สกิล 3 ฮีล
        this.HP += this.heal;
        return this.HP;
    }

    public float getSkill3() { //ใช้สกิล 3 โจมตี
        return this.heal;
    }

    public float setHP(float damage) {
        this.HP -= damage;
        return this.HP;
    }
}
