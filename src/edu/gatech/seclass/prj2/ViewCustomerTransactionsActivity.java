package edu.gatech.seclass.prj2;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import edu.gatech.seclass.prj2.pojos.Transaction;


/*
 *  ViewCustomerTransactionsActivity
 *  
 *  Purpose: query the transactions table in the database using the customerID from ViewCustomerActivity,
 *           then display them in a selectable list format
 *           
 */
public class ViewCustomerTransactionsActivity extends ListActivity {
	  private CustomerOperations datasource;
	  private int custID;

 
      /*
       *  onCreate
       *  
       *  Method Purpose: set the view and create the list from the db query for creating the list view
       *           
       */	      
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_customer_transactions);

        // get passed in parameters
        Intent intent = getIntent();
	    custID = intent.getIntExtra("customerID", 0);
	    String firstName = intent.getStringExtra("firstName");
	    String lastName = intent.getStringExtra("lastName");
	    
	    // get display size for column alignment
	    Display display = getWindowManager().getDefaultDisplay();
	    Point size = new Point();
	    display.getSize(size);
	    int width = size.x;
	    
        TextView tt1 = (TextView) findViewById(R.id.ct_DateLabel);
        TextView tt2 = (TextView) findViewById(R.id.ct_AmountStatusLabel);
        TextView tt3 = (TextView) findViewById(R.id.ct_GoldLabel);
        TextView tt4 = (TextView) findViewById(R.id.ct_RewardAppliedLabel);

        setWidth(tt1, width);
        setWidth(tt2, width);
        setWidth(tt3, width);
        setWidth(tt4, width);
	    
        // query the db and populate the transactions list
	    datasource = new CustomerOperations(this);
	    List<Transaction> listT = new ArrayList<Transaction>();
	    try {
    	datasource.open();
	    listT = datasource.getCustomerTransactions(custID);
        } catch (Exception e) {
          // send paramters to next intent/activity
    	    Intent errIntent = new Intent(this, MainActivity.class);
    	    errIntent.putExtra("msgText", "database Error");
  	        startActivity(errIntent);
  	        finish();        	
        }	      
		TextView custName = (TextView) findViewById(R.id.customerId);
		custName.setText("Transactions for:  " + firstName + " " + lastName + "");
	    
		// the adapter displays the transactions results
	    TransactionArrayAdapter adapter = new TransactionArrayAdapter(this, R.layout.transitemlistrow, listT);	       

	    setListAdapter(adapter);
	  }

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.view_customer_transactions, menu);
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

		 
		 /*
		  *  setWidth
		  *  
		  *  Method Purpose: divide the view into 4 equal sections
		  *           
		  */
		   private void setWidth (TextView txtv, int size) {
		       LayoutParams txtv_param = txtv.getLayoutParams();
		       txtv_param.width = size/4;
		       txtv.setLayoutParams(txtv_param);     
		   }		 		 

		   
}	
