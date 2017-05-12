package edu.gatech.seclass.prj2.pojos;

public class Customer {
    private int customerID;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String zipCode;
    private boolean hasGoldStatus;
    private Money rewardSum;
    
    public int getCustomerID() {
        return customerID;
    }
    
    public void setCustomerID(int id) {
        customerID = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public boolean isHasGoldStatus() {
        return hasGoldStatus;
    }
    
    public void setHasGoldStatus(boolean hasGoldStatus) {
        this.hasGoldStatus = hasGoldStatus;
    }
    
    public Money getRewardSum() {
        return rewardSum;
    }
    
    public void setRewardSum(Money rewardSum) {
        this.rewardSum = rewardSum;
    }
}
