package edu.gatech.seclass.prj2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ViewCustomerActivity extends Activity {

    private int customerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer);
        Intent intent = getIntent();
        // get the text view elements from the layout
        TextView firstNameText = (TextView) findViewById(R.id.viewFirstName);
        TextView lastNameText = (TextView) findViewById(R.id.viewLastName);
        TextView zipText = (TextView) findViewById(R.id.viewZipcode);
        TextView emailText = (TextView) findViewById(R.id.viewEmailAddress);
        TextView rewardAmountText = (TextView) findViewById(R.id.viewRewardAmount);
        TextView goldStatusText = (TextView) findViewById(R.id.viewGoldStatus);
        TextView yearlyTotalText = (TextView) findViewById(R.id.viewYearlyTotal);
        // get values from the intent, and set them to the text view    elements
        setCustomerID(intent.getIntExtra("customerID", 0));
        firstNameText.setText(intent.getStringExtra("firstName"));
        lastNameText.setText(intent.getStringExtra("lastName"));
        zipText.setText(intent.getStringExtra("zipcode"));
        emailText.setText(intent.getStringExtra("emailAddress"));
        rewardAmountText.setText(intent.getStringExtra("rewardAmount"));
        goldStatusText.setText(intent.getStringExtra("goldStatus"));
        yearlyTotalText.setText(intent.getStringExtra("yearlyTotal"));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_customer, menu);
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
     * click handler for "Modify Customer" button
     * gets the customer data from the text view elements and sets values 
     * in the intent to be passed to the next activity
     * @param view
     */
    public void modifyCustomer(View view) {
         Intent intent = new Intent(this, ModifyCustomerActivity.class);
         intent.putExtra("customerID", getCustomerID());
         intent.putExtra("firstName", ((TextView) findViewById(R.id.viewFirstName)).getText());
         intent.putExtra("lastName", ((TextView) findViewById(R.id.viewLastName)).getText());
         intent.putExtra("zipcode", ((TextView) findViewById(R.id.viewZipcode)).getText());
         intent.putExtra("emailAddress", ((TextView) findViewById(R.id.viewEmailAddress)).getText());
         intent.putExtra("rewardAmount", ((TextView) findViewById(R.id.viewRewardAmount)).getText());
         intent.putExtra("goldStatus", ((TextView) findViewById(R.id.viewGoldStatus)).getText());
         intent.putExtra("yearlyTotal", ((TextView) findViewById(R.id.viewYearlyTotal)).getText());
         startActivity(intent);
    }
    /**
     * click handler for "View Customer Transactions" button.
     * gets the customerID and customer email address from text view and
     * sets the values in the intent to be passed to next activity
     * @param view
     */
    public void viewCustomerTransactions(View view) {
         Intent intent = new Intent(this, ViewCustomerTransactionsActivity.class);
         intent.putExtra("customerID", getCustomerID());
         intent.putExtra("firstName", ((TextView) findViewById(R.id.viewFirstName)).getText());
         intent.putExtra("lastName", ((TextView) findViewById(R.id.viewLastName)).getText());
         startActivity(intent);
    }
    /** getters and setters **/
    public void setCustomerID(int customerID){
        this.customerID = customerID;
    }
    public int getCustomerID(){
        return customerID;
    }
      
}
