public abstract class Enemy {
    protected float HP;
    protected float skill1;
    protected float skill2;
    protected float heal;
    protected float maxHP;
    protected int hitbox = 20;

    public Enemy(float HP, float skill1, float skill2, float heal) {
        this.maxHP = HP;
        this.skill1 = skill1;
        this.skill2 = skill2;
        this.heal = heal;
    }

    public Enemy(float HP, float heal) {
        this.maxHP = HP;
        this.heal = heal;
    }

    public float autoheal() {
        this.HP += heal;
        return this.HP;
    }

    abstract public float getSkill1(Cat c); //ใช้สกิล 1 โจมตี
    abstract public float getSkill2(Cat c); //ใช้สกิล 2 โจมตี

    public float getHP() {
        return this.HP;
    }

    public void resetHP() {
        this.HP = this.maxHP;
    }

    public void takeDamage(float damage) {
        this.HP -= damage;
    }
}
