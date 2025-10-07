public abstract class Enemy {
    protected float HP;
    protected float skill1;
    protected float skill2;
    protected float heal;

    public Enemy(float HP, float skill1, float skill2, float heal) {
        this.HP = HP;
        this.skill1 = skill1;
        this.skill2 = skill2;
        this.heal = heal;
    }

    public Enemy(float HP, float heal) {
        this.HP = HP;
        this.heal = heal;
    }

    public float autoheal() {
        this.HP += heal;
        return this.HP;
    }

    abstract public float getSkill1(Cat c); //ใช้สกิล 1 โจมตี
    abstract public float getSkill2(Cat c); //ใช้สกิล 2 โจมตี
}
