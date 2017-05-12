package edu.gatech.seclass.prj2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import edu.gatech.seclass.prj2.pojos.Customer;
import edu.gatech.seclass.prj2.pojos.Money;

public class ModifyCustomerActivity extends Activity {

    private int customerID;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String zipcode;
    private String rewardAmount;
    private String goldStatus;
    private String yearlyTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_customer);
        // get data from intent
        Intent intent = getIntent();
        setCustomerID(intent.getIntExtra("customerID", 0));
        setFirstName(intent.getStringExtra("firstName"));
        setLastName(intent.getStringExtra("lastName"));
        setEmailAddress(intent.getStringExtra("emailAddress"));
        setZipcode(intent.getStringExtra("zipcode"));
        setRewardAmount(intent.getStringExtra("rewardAmount"));
        setGoldStatus(intent.getStringExtra("goldStatus"));
        setYearlyTotal(intent.getStringExtra("yearlyTotal"));
        // get fields from view
        EditText firstNameText = (EditText) findViewById(R.id.editFirstName);
        EditText lastNameText = (EditText) findViewById(R.id.editLastName);
        EditText zipcodeText = (EditText) findViewById(R.id.editZipcode);
        EditText emailText = (EditText) findViewById(R.id.editEmailAddress);
        // set text in the fields
        firstNameText.setText(getFirstName());
        lastNameText.setText(getLastName());
        zipcodeText.setText(getZipcode());
        emailText.setText(getEmailAddress());
        // set error label as invisible
        final TextView errorLabel = (TextView) findViewById(R.id.modifyCustomerErrorLabel);
        errorLabel.setVisibility(View.INVISIBLE);
        // set save status label as invisible
        final TextView saveLabel = (TextView) findViewById(R.id.modifyCustomerSaveLabel);
        saveLabel.setVisibility(View.INVISIBLE);
        // create text watcher to set the save status label to invisible when text fields change
        TextWatcher resetSaveLabel = new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) 
            {
                saveLabel.setVisibility(View.INVISIBLE);
                errorLabel.setVisibility(View.INVISIBLE);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void afterTextChanged(Editable s) {}
            
        };
        // add text watcher to the edit text fields
        firstNameText.addTextChangedListener(resetSaveLabel);
        lastNameText.addTextChangedListener(resetSaveLabel);
        emailText.addTextChangedListener(resetSaveLabel);
        zipcodeText.addTextChangedListener(resetSaveLabel);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.modify_customer, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * click handler for "Home" button
     * returns user to home screen
     * @param view
     */
    public void goHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     * click handler for "Back to View Customer" button
     * sets customer values in intent to be passed to 
     * next activity
     * @param view
     */
    public void goBackToViewCustomer(View view) {
        Intent intent = new Intent(this, ViewCustomerActivity.class);
        // put data in intent
        intent.putExtra("customerID", getCustomerID());
        intent.putExtra("firstName", getFirstName());
        intent.putExtra("lastName", getLastName());
        intent.putExtra("zipcode", getZipcode());
        intent.putExtra("emailAddress", getEmailAddress());
        intent.putExtra("rewardAmount", getRewardAmount().toString());
        intent.putExtra("goldStatus", getGoldStatus());
        intent.putExtra("yearlyTotal", getYearlyTotal());
        startActivity(intent);
        finish();
    }
    /**
     * click handler for "Save" button
     * gets values from edit text fields in order to update
     * customer data
     * @param view
     */
    public void saveModifiedCustomer(View view) {
        boolean valid = true;
        // get the edit text fields from the layout
        EditText editFirstName = (EditText)findViewById(R.id.editFirstName);
        EditText editLastName = (EditText)findViewById(R.id.editLastName);
        EditText editEmail = (EditText)findViewById(R.id.editEmailAddress);
        EditText editZip = (EditText)findViewById(R.id.editZipcode);
        // validate form
        if ((editFirstName).getText().toString().isEmpty())
        {
            editFirstName.setError("Please enter first name");
            valid = false;
        }
        if ((editLastName).getText().toString().isEmpty())
        {
            editLastName.setError("Please enter last name");
            valid = false;
        }
        if (!ValidatorTools.isValidEmail(editEmail.getText().toString()))
        {
                editEmail.setError("Please enter a valid email address");
                valid = false;
        }
        if (!ValidatorTools.isValidZip(editZip.getText().toString()))
        {
            editZip.setError("Please enter a 5 digit zipcode");
            valid = false;
        }
        // if all fields are valid, save updated customer
        if (valid)
        {
            // create customer with data from text fields
            Customer c = new Customer();
            c.setCustomerID(getCustomerID());
            c.setFirstName(((EditText) findViewById(R.id.editFirstName)).getText().toString());
            c.setLastName(((EditText) findViewById(R.id.editLastName)).getText().toString());
            c.setEmailAddress(((EditText) findViewById(R.id.editEmailAddress)).getText().toString());
            c.setZipCode(((EditText) findViewById(R.id.editZipcode)).getText().toString());
            c.setRewardSum(convertStringToMoney(getRewardAmount()));
            c.setHasGoldStatus("true" == getGoldStatus());
            // get datasource to edit customer
            CustomerOperations datasource = new CustomerOperations(this);
            try {
                datasource.open();
            } catch (Exception e) {
                //display error on home screen
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("msgText", "Database Error");
                startActivity(intent);
                finish();
                return;
            }
            try {
                datasource.editCustomer(c);
                // set save status label to be visible
                TextView saveLabel = (TextView) findViewById(R.id.modifyCustomerSaveLabel);
                saveLabel.setVisibility(View.VISIBLE);
                //set the local values after successful save
                setFirstName(c.getFirstName());
                setLastName(c.getLastName());
                setEmailAddress(c.getEmailAddress());
                setZipcode(c.getZipCode());
            } catch (Exception e) {
                // set error status label to be visible
                TextView errorLabel = (TextView) findViewById(R.id.modifyCustomerErrorLabel);
                errorLabel.setVisibility(View.VISIBLE);
            }
            datasource.close();
        }
    }
    /**
     * converts the string for reward amount into Money
     * by removing the '$' in the string,
     * parsing the substring w/o the '$' into a double,
     * then creating a Money object from the double
     * @param amount
     * @return
     */
    private Money convertStringToMoney(String amount){
        String amountWithoutDollarSign = amount.substring(amount.indexOf('$') + 1);
        return new Money(Double.parseDouble(amountWithoutDollarSign));
    }
    // getters and setters
    public int getCustomerID() {
        return customerID;
    }
    
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
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
    
    public String getZipcode() {
        return zipcode;
    }
    
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
    
    public String getRewardAmount() {
        return rewardAmount;
    }
    
    public void setRewardAmount(String rewardAmount) {
        this.rewardAmount = rewardAmount;
    }
    
    public String getGoldStatus() {
        return goldStatus;
    }
    
    public void setGoldStatus(String goldStatus) {
        this.goldStatus = goldStatus;
    }
    
    public String getYearlyTotal() {
        return yearlyTotal;
    }
    
    public void setYearlyTotal(String yearlyTotal) {
        this.yearlyTotal = yearlyTotal;
    }
}
