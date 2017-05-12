package edu.gatech.seclass.prj2;


import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.ListView;
import edu.gatech.seclass.prj2.pojos.Customer;


/*
 *  CustomerResultsActivity
 *  
 *  Purpose: query the customer table in the database using the entries from SearchCustomerActivity,
 *           then display them in a selectable list format to be view in ViewCustomerActivity
 *           
 */
public class CustomerResultsActivity extends ListActivity {
	  private CustomerOperations datasource;
	  private String firstName;
	  private String lastName;
	  private String emailAddress;
	  private String zipCode;
	  private String yearlyTotal;
	  private List<Customer> cList;
      
      
      /*
       *  onCreate
       *  
       *  Method Purpose: set the view and create the list from the db query for creating the list view
       *           
       */	  
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_customer_results);
	    
	    	  
        // get passed in values
	    Intent intent = getIntent();
	    firstName = intent.getStringExtra("firstName");
	    lastName = intent.getStringExtra("lastName");
	    zipCode = intent.getStringExtra("zipCode");
	    emailAddress = intent.getStringExtra("emailAddress");

	    // open db and populate Customer list
	    datasource = new CustomerOperations(this);
        cList = new ArrayList<Customer>();
        try {
        datasource.open();
        cList = datasource.getMatchingCustomers(firstName, lastName, emailAddress, zipCode);
        } catch (Exception e) {
            // send paramters to next intent/activity
      	    Intent errIntent = new Intent(this, MainActivity.class);
      	    errIntent.putExtra("msgText", "database Error");
    	    startActivity(errIntent);
    	    finish();        	
        }
        // the adapter displays the customer list
        CustomerArrayAdapter adapter = new CustomerArrayAdapter(this, R.layout.itemlistrow, cList);
          
        // Assign adapter to List
        setListAdapter(adapter);  
        
	  }

      /*
       *  onListItemClick
       *  
       *  Method Purpose: pass the information on the selected customer to the ViewCustomerActivity 
       *           
       */
      @Override
     protected void onListItemClick(ListView l, View v, int position, long id) {
           
           super.onListItemClick(l, v, position, id);
              
           // ListView Clicked item value
           Customer  itemValue    = (Customer) l.getItemAtPosition(position);
       	   
           // a new db query is required to get yearly total
           datasource = new CustomerOperations(this);
           try {
       	   datasource.open();
           yearlyTotal = String.valueOf(datasource.getCustomerYearlyTotal(itemValue.getCustomerID()));
   	       } catch (Exception e) {
               // send paramters to next intent/activity
         	    Intent errIntent = new Intent(this, MainActivity.class);
         	    errIntent.putExtra("msgText", "database Error");
       	        startActivity(errIntent);
       	        finish();        	
           }
           
           // send paramters to next intent/activity
     	    Intent intent = new Intent(this, ViewCustomerActivity.class);
     	    intent.putExtra("customerID", itemValue.getCustomerID());
   	        intent.putExtra("firstName", itemValue.getFirstName());
   	        intent.putExtra("lastName", itemValue.getLastName());
   	        intent.putExtra("zipcode", itemValue.getZipCode());
   	        intent.putExtra("emailAddress", itemValue.getEmailAddress());	    
   	        intent.putExtra("rewardAmount", String.valueOf(itemValue.getRewardSum()));
   	        intent.putExtra("goldStatus", String.valueOf(itemValue.isHasGoldStatus()));
   	        intent.putExtra("yearlyTotal", yearlyTotal);
   	        startActivity(intent);
   	        //finish();
     }

      
		 /*
		  *  handleHomeClick
		  *  
		  *  Method Purpose: handle the activity of clicking the Done button by returning
		  *                  the user to the home screen
		  *           
		  */
		 public void handleHomeClick( View view ) {
			    Intent intent = new Intent(this, MainActivity.class);
			    startActivity(intent);
			    finish();
		 }
		 
      /** getters and setters */
      
      public void setCustomerList (List<Customer> list){
          this.cList = list;
      }
      public List<Customer> getCustomerList(){
          return cList;
      }
      
      public void onBackPressed(){
          NavUtils.navigateUpFromSameTask(this);
        }      
      
}