package edu.gatech.seclass.prj2;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.EditText;

/*
 *  SearchCustomerActivity
 *  
 *  Purpose: Provide an activity to collect searchable data for retrieving
 *           customer entries from the database
 *           
 */
public class SearchCustomerActivity extends Activity {
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String zipCode;
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_customer);

    }
   
    
    /*
     * handleSearchClick handles the activity when the user presses the Search button
     * 
     * the method passes the completed fields (filled or left empty) to CustomerResultsActivity.class
     * which are used to query the database and then display the results 
     */
    public void handleSearchClick(View view) {
        Intent intent = new Intent(this, CustomerResultsActivity.class);
             
        // get search criteria from fields
        firstName = ((EditText) findViewById(R.id.sc_firstName)).getText().toString();
        lastName = ((EditText) findViewById(R.id.sc_lastName)).getText().toString();
        zipCode = ((EditText) findViewById(R.id.sc_zipCode)).getText().toString();
        emailAddress = ((EditText) findViewById(R.id.sc_emailAddr)).getText().toString();
        
        // send search criteria to next intent/activity
        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        intent.putExtra("zipCode", zipCode);
        intent.putExtra("emailAddress", emailAddress);
        startActivity(intent);
        //finish();

    }
  
    /** getters and setters */
    
    public void setFirstName(String s){
        this.firstName = s;
    }
    public String getFirstName(){
        return firstName;
    }
 
    public void setLastName(String s){
        this.lastName = s;
    }
    public String getLastName(){
        return lastName;
    }    
 
    public void setEmailAddress(String s){
        this.emailAddress = s;
    }
    public String getEmailAddress(){
        return emailAddress;
    }

    public void setZipCode(String s){
        this.zipCode = s;
    }
    public String getZipCode(){
        return zipCode;
    }

    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
      }
    
}    

