package edu.gatech.seclass.prj2.pojos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreditCard {
    private String cardHolderFirstName;
    private String cardHolderLastName;
    private String accountNumber;
    private String securityCode;
    private Date expirationDate;
    
    public CreditCard(String s) {
        String[] cc = s.split("#");
        cardHolderFirstName = cc[0];
        cardHolderLastName = cc[1];
        accountNumber = cc[2];
        securityCode = cc[4];
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyy", Locale.US);
        try {
            expirationDate = simpleDateFormat.parse(cc[3]);
        } catch (ParseException e) {
            System.out.print("Error parsing date: " + e.getMessage());
        }
    }
    
    public CreditCard() {
        
    }
    
    public String getCardHolderName() {
        return cardHolderFirstName + cardHolderLastName;
    }
    
    public String getCardHolderFirstName() {
        return cardHolderFirstName;
    }
    
    public String getCardHolderLastName() {
        return cardHolderLastName;
    }
    
    public void setCardHolderFirstName(String cardHolderFirstName) {
        this.cardHolderFirstName = cardHolderFirstName;
    }
    
    public void setCardHolderLastName(String cardHolderLastName) {
        this.cardHolderLastName = cardHolderLastName;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getSecurityCode() {
        return securityCode;
    }
    
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }
    
    public Date getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
