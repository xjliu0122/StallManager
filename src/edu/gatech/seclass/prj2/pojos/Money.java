package edu.gatech.seclass.prj2.pojos;

import java.math.BigDecimal;

public class Money{
  
  private BigDecimal amount;
  private static final int rMode = BigDecimal.ROUND_CEILING;
  
  public Money(double amount){
    this.amount = new BigDecimal(Double.toString(amount));
    this.amount = this.amount.setScale(2, rMode);
  }
    
  public static Money add(Money m1, Money m2){
      Money result = new Money(0);
      result.amount = m1.amount.add(m2.amount);
      result.amount = result.amount.setScale(2, rMode);
      return result;
  }
  
  public static Money subtract(Money m1, Money m2) {
      Money result = new Money(0);
      result.amount = m1.amount.subtract(m2.amount);
      result.amount = result.amount.setScale(2, rMode);
      return result;
  }
  
  public BigDecimal getValue() {
      return amount;
  }
    
  public String toPlainString() {
      return this.amount.toPlainString();
  }
    
    @Override
  public String toString() {
      return "$" + amount;
  }
}
