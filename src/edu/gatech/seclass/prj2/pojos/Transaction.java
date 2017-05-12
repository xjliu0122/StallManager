package edu.gatech.seclass.prj2.pojos;

import java.util.Date;

public class Transaction {
    private Customer customer;
    private Date date;
    private Money finalAmount;
    private Discount[] discountsApplied;
    
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public Money getFinalAmount() {
        return finalAmount;
    }
    public void setFinalAmount(Money finalAmount) {
        this.finalAmount = finalAmount;
    }
    public Discount[] getDiscountsApplied() {
        return discountsApplied;
    }
    public void setDiscountsApplied(Discount[] discountsApplied) {
        this.discountsApplied = discountsApplied;
    }
}
