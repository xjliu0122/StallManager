package edu.gatech.seclass.prj2;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.gatech.seclass.prj2.pojos.Discount;
import edu.gatech.seclass.prj2.pojos.Money;
import edu.gatech.seclass.prj2.pojos.Transaction;

/*
 *  TransactionArrayAdapter
 *  
 *  Purpose: dynamically display the customer transactions list in formatted and selectable rows
 *           
 */
public class TransactionArrayAdapter extends ArrayAdapter<Transaction> {

public TransactionArrayAdapter(Context context, int textViewResourceId) {
    super(context, textViewResourceId);
}

public TransactionArrayAdapter(Context context, int resource, List<Transaction> items) {
    super(context, resource, items);
}

/*
 *  getView
 *  
 *  Method Purpose: define the reused selectable section
 *           
 */
@SuppressLint("InflateParams")
@Override
public View getView(int position, View convertView, ViewGroup parent) {

    View v = convertView;
    

    if (v == null) {

        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        v = vi.inflate(R.layout.transitemlistrow, null);

    }

    Transaction t = getItem(position);
    

    if (t != null) {

        TextView tt1 = (TextView) v.findViewById(R.id.til_dateId);
        TextView tt2 = (TextView) v.findViewById(R.id.til_amountId);
      
        TextView tt3 = (TextView) v.findViewById(R.id.til_goldStatusId);
        TextView tt4 = (TextView) v.findViewById(R.id.til_rewardSumId);
        
        setWidth(tt1, v.getWidth());
        setWidth(tt2, v.getWidth());
        setWidth(tt3, v.getWidth());
        setWidth(tt4, v.getWidth());
        
        Money rewardSumValue = null;
        Money goldStatusValue = null;
        
        Discount[] transDiscounts = t.getDiscountsApplied();
        
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);      	    
        tt1.setText(df.format(t.getDate()));
        tt2.setText(String.valueOf(t.getFinalAmount()));
        
        if (transDiscounts.length > 0) {
          String zero_class = transDiscounts[0].getClass().getName();
          boolean gold0 = zero_class.contains("Gold");
          if (gold0) {
        	  goldStatusValue = transDiscounts[0].getDiscountAmount();
          } else {
        	  rewardSumValue = transDiscounts[0].getDiscountAmount();
          }
        }
        if (transDiscounts.length > 1) {
            String one_class = transDiscounts[1].getClass().getName();
            boolean gold1 = one_class.contains("Gold");
            if (gold1) {
          	  goldStatusValue = transDiscounts[1].getDiscountAmount();
            } else {
          	  rewardSumValue = transDiscounts[1].getDiscountAmount();
            }            
        }
        
        if(goldStatusValue != null) {
            tt3.setText(goldStatusValue.toString());
          } else {
              tt3.setText("$0.00");   	
          }
          
        if(rewardSumValue != null) {
            tt4.setText(rewardSumValue.toString());
          } else {
              tt4.setText("$0.00");   	
          }
    
    }
	  


    return v;

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