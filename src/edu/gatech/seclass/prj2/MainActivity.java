package edu.gatech.seclass.prj2;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        TextView msg = (TextView)findViewById(R.id.mainErrorMsg);
        if (null != intent) {
            msg.setText(intent.getStringExtra("msgText"));
        } else {
            msg.setText("");
        }
        /* Create DB */
        SQLiteDatabase d = new DatabaseWrapper(this).getWritableDatabase();
        d.close();
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
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

    public void goViewTransactions(View view) {
        Intent intent = new Intent(this, ViewTransactionsActivity.class);
        startActivity(intent);
    }

    public void goCreateCustomer(View view) {
        Intent intent = new Intent(this, CreateCustomerActivity.class);
        startActivity(intent);
    }

    public void goCreateTransaction(View view) {
        Intent intent = new Intent(this, CreateTransactionActivity.class);
        startActivity(intent);
    }
    
    public void goSearchCustomer(View view) {
        Intent intent = new Intent(this, SearchCustomerActivity.class);
        startActivity(intent);
    }
    
}
