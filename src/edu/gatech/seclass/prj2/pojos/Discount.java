package edu.gatech.seclass.prj2.pojos;

public abstract class Discount {

    private Money discountAmount;
    public static final int GOLD_DISCOUNT = 1;
    public static final int REWARD_DISCOUNT = 2;
    
    public Money getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Money discountAmount) {
        this.discountAmount = discountAmount;
    }
}
