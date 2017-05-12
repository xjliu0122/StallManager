package edu.gatech.seclass.prj2;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.gatech.seclass.prj2.pojos.Customer;


/*
 *  CustomerArrayAdapter
 *  
 *  Purpose: dynamically display the customer list in formatted and selectable rows
 *           
 */
public class CustomerArrayAdapter extends ArrayAdapter<Customer> {

public CustomerArrayAdapter(Context context, int textViewResourceId) {
    super(context, textViewResourceId);
}

public CustomerArrayAdapter(Context context, int resource, List<Customer> items) {
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
        v = vi.inflate(R.layout.itemlistrow, null);

    }

    Customer c = getItem(position);

    if (c != null) {
	    
        TextView tt1 = (TextView) v.findViewById(R.id.il_fnameId);
        TextView tt2 = (TextView) v.findViewById(R.id.il_lnameId);
        TextView tt3 = (TextView) v.findViewById(R.id.il_zipId);
        TextView tt4 = (TextView) v.findViewById(R.id.il_emailId);

        setWidth(tt1, v.getWidth());
        setWidth(tt2, v.getWidth());
        setWidth(tt3, v.getWidth());
        setWidth(tt4, v.getWidth());
        
        tt1.setText(c.getFirstName());
        tt2.setText(c.getLastName());
        tt3.setText(c.getZipCode());
        tt4.setText(c.getEmailAddress());

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