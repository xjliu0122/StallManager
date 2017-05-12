package edu.gatech.seclass.prj2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import edu.gatech.seclass.prj2.pojos.Customer;

public class CreateCustomerActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_customer);
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.create_customer, menu);
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
  public void saveNewCustomer(View view) {
    boolean valid = true;
    // validate form
    TextView tvFirstName = (TextView) findViewById(R.id.ccEditFirstName);
    if (tvFirstName.getText().toString().isEmpty()) {
      tvFirstName.setError("Please enter first name");
      valid = false;;
    }

    TextView tvLastName = (TextView) findViewById(R.id.ccEditLastName);
    if (tvLastName.getText().toString().isEmpty()) {
      tvLastName.setError("Please enter last name");
      valid = false;
    }

    if (!ValidatorTools.isValidEmail(((EditText) findViewById(R.id.ccEditEmailAddress)).getText().toString()))
    {
      ((EditText) findViewById(R.id.ccEditEmailAddress)).setError("Please enter a valid email address");
      valid = false;
    }

    if (!ValidatorTools.isValidZip(((EditText) findViewById(R.id.ccEditZipcode)).getText().toString()))
    {
      ((EditText) findViewById(R.id.ccEditZipcode)).setError("Please enter a 5 digit zipcode");
      valid = false;
    }

    if (valid) {
      // get data from the text fields
      String firstName = ((EditText) findViewById(R.id.ccEditFirstName)).getText().toString();
      String lastName = ((EditText) findViewById(R.id.ccEditLastName)).getText().toString();
      String emailAddress = ((EditText) findViewById(R.id.ccEditEmailAddress)).getText().toString();
      String zipcode = ((EditText) findViewById(R.id.ccEditZipcode)).getText().toString();
      // create customer with data
      Customer c = new Customer();
      c.setFirstName(firstName);
      c.setLastName(lastName);
      c.setEmailAddress(emailAddress);
      c.setZipCode(zipcode);
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
        datasource.addNewCustomer(c);
        setErrorMsg("Save Successful");
      } catch (Exception e) {
        //TODO Check what type of error actually occurs (SQLiteConstraintException)
        setErrorMsg("Error - Duplicate email");
      }
      datasource.close();
    }
  }

  private void setErrorMsg(String msg) {
    TextView errorLabel = (TextView) findViewById(R.id.ccErrorLabel);
    errorLabel.setText(msg);
  }

  public void goHome(View view) {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }

}
