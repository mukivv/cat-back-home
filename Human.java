public class Human extends Enemy {
    public Human(float HP, float heal) {
        super(HP, heal);
    }

    public float getHeal() { //ใช้สกิล 3 ฮีล
        this.HP += this.heal;
        return this.HP;
    }

    @Override
    public float getSkill1(Cat cat) {
        return this.HP; // Human does not have skill1 to attack
    }

    @Override
    public float getSkill2(Cat cat) {
        return this.HP; // Human does not have skill2 to attack
    }
    
}
