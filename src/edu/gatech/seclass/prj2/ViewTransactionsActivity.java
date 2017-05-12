package edu.gatech.seclass.prj2;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.gatech.seclass.prj2.pojos.Discount;
import edu.gatech.seclass.prj2.pojos.GoldMemberDiscount;
import edu.gatech.seclass.prj2.pojos.Money;
import edu.gatech.seclass.prj2.pojos.RewardDiscount;
import edu.gatech.seclass.prj2.pojos.Transaction;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class ViewTransactionsActivity extends Activity {
  private TransactionOperations datasource;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_transactions);
    
    List<Transaction> transactionsList = new ArrayList<Transaction>();
    
    //get transactions from database
    datasource = new TransactionOperations(this);
    int page = 0;
    try {
      datasource.open();
      transactionsList = datasource.getAllTransactions(page);
    } catch (Exception e) {
      Intent intent = new Intent(this, MainActivity.class);
      intent.putExtra("msgText", "Database Error");
      startActivity(intent);
      finish();
    }
    
    TableLayout table = (TableLayout) findViewById(R.id.view_transactions_table);
    TableRow row;
    TextView t1, t2, t3, t4, t5;
    DateFormat df;
    
    //each table row is a transaction 
    //dynamically add each row 
    while(transactionsList.size() > 0){
      for(int i=0; i<transactionsList.size(); i++){
        Transaction transaction = transactionsList.get(i);
        
        row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT,
            1f));
        
        t1 = new TextView(this);
        t2 = new TextView(this);
        t3 = new TextView(this);
        t4 = new TextView(this);
        t5 = new TextView(this);
        
        TableRow.LayoutParams params = new TableRow.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            1.0f);
        t1.setLayoutParams(params);
        t2.setLayoutParams(params);
        t3.setLayoutParams(params);
        t4.setLayoutParams(params);
        t5.setLayoutParams(params);
        
        t1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        t2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        t3.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        t4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        t5.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        
        //Set text for each TextView
        df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
        t1.setText(df.format(transaction.getDate()));
        t2.setText(transaction.getCustomer().getFirstName() + " " +
            transaction.getCustomer().getLastName());
        t3.setText(transaction.getFinalAmount().toString());
        Money amt = new Money(0.00);
        t4.setText(amt.toString()); 
        t5.setText(amt.toString()); 
        Discount[] discounts = transaction.getDiscountsApplied();
        for(int j=0; j<discounts.length; j++){
          if (discounts[j] instanceof GoldMemberDiscount) {
            t4.setText(discounts[j].getDiscountAmount().toString());  
          } else if (discounts[j] instanceof RewardDiscount) {
            t5.setText(discounts[j].getDiscountAmount().toString());  
          }
          else{
            System.err.println("Unknown Discount type");
          }
        }
        
        row.addView(t1);
        row.addView(t2);
        row.addView(t3);
        row.addView(t4);
        row.addView(t5);
        table.addView(row);
      }
      
      //get next page of transactions
      page++;
      try {
        transactionsList = datasource.getAllTransactions(page);
      } catch (Exception e) {
        System.out.println(e);
      }
    }
    
    //add Home button
    row = new TableRow(this);
    row.setLayoutParams(new TableRow.LayoutParams(
        TableRow.LayoutParams.MATCH_PARENT,
        TableRow.LayoutParams.MATCH_PARENT,
        1f));  
      
    Button home = new Button(this);
    home.setText("Home");
    home.setId(R.id.vt_home_button);
    TableRow.LayoutParams params = new TableRow.LayoutParams(
        LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT,
        1.0f);
    home.setLayoutParams(params);
    home.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Intent intent = new Intent(ViewTransactionsActivity.this, 
            MainActivity.class);
        startActivity(intent);
        finish();
      }
    });
    row.addView(home);
    table.addView(row);
    
    datasource.close();
  }
  public void onBackPressed(){
    NavUtils.navigateUpFromSameTask(this);
  }
}	
