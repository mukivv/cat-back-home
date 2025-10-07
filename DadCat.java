public class DadCat extends Enemy {
    public DadCat(float HP, float skill1, float skill2, float heal) {
        super(HP, skill1, skill2, heal);
    }

    @Override
    public float getSkill1(Cat cat) {
        return cat.setHP(super.skill1);
    }

    @Override
    public float getSkill2(Cat cat) {
        return cat.setHP(super.skill2); 
    }
    
}
