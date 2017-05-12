package edu.gatech.seclass.prj2;

import edu.gatech.seclass.prj2.pojos.Customer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ViewPaymentStatusActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_payment_status);
    
    Intent intent = getIntent();
    int customerID = intent.getIntExtra("customerID", 0);
    
    CustomerOperations datasource = new CustomerOperations(this);

    try {
      datasource.open();
      Customer customer = datasource.getCustomer(customerID);
      
      TextView dateText = (TextView) findViewById(R.id.psDate);
      TextView totalChargedText = (TextView) findViewById(R.id.psTotal);
      TextView goldStatusDiscountText = (TextView) findViewById(R.id.psGoldStatus);
      TextView rewardsAppliedText = (TextView) findViewById(R.id.psRewardsApplied);
      TextView rewardEarnedText = (TextView) findViewById(R.id.psRewardsEarned);
      TextView yearlyTotalText = (TextView) findViewById(R.id.psYearlyTotal);
      TextView goldStatusText = (TextView) findViewById(R.id.psGoldStatusValue);
      TextView currentRewardsText = (TextView) findViewById(R.id.psTotalRewards);
      
      dateText.setText(intent.getStringExtra("date"));
      totalChargedText.setText(intent.getStringExtra("totalCharged"));
      goldStatusDiscountText.setText(intent.getStringExtra("goldStatusDiscountApplied"));
      rewardsAppliedText.setText(intent.getStringExtra("rewardsApplied"));
      rewardEarnedText.setText(intent.getStringExtra("earnedReward"));
      yearlyTotalText.setText(datasource.getCustomerYearlyTotal(customerID).toString());
      goldStatusText.setText(customer.isHasGoldStatus() ? "True" : "False");
      currentRewardsText.setText(customer.getRewardSum().toString());
    } catch (Exception e) {
      intent = new Intent(this, MainActivity.class);
      intent.putExtra("msgText", "Database Error");
      startActivity(intent);
      finish();
    }
    
    datasource.close();
  }

  public void goHome(View view) {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}	
