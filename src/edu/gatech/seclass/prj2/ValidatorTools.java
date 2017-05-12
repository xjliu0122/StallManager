package edu.gatech.seclass.prj2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidatorTools {

    /**
     * Returns true if target is a valid email address
     * @param target
     * @return
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    /**
     * returns true if target is a valid 5 digit zip code
     * @param target
     * @return
     */
    public final static boolean isValidZip(CharSequence target) {
        Pattern p = Pattern.compile("[0-9]{5}");
        if (null == target) {
            return false;
        } else if ((p.matcher(target)).matches()) {
            return true;
        } else {
            return false;
        }
    }

    
    /**
     * Returns true if target is a 16 digit number
     * @param target
     * @return
     */
    public final static boolean isValidCreditCard(CharSequence target) {
        Pattern p = Pattern.compile("[0-9]{16}");
        if (null == target) {
            return false;
        } else if ((p.matcher(target)).matches()) {
            return true;
        } else {
            return false;
        }
    }
    
    
    /**
     * Returns true if target is a 3 digit number
     * @param target
     * @return
     */
    public final static boolean isValidCCV(CharSequence target) {
        Pattern p = Pattern.compile("[0-9]{3}");
        if (null == target) {
            return false;
        } else if ((p.matcher(target)).matches()) {
            return true;
        } else {
            return false;
        }
    }
    
    
    /**
     * Returns true if target is a date in MM/dd/yyyy format
     * @param target
     * @return
     */
    public final static boolean isValidDate(CharSequence target) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);
        try {
            Date date= dateFormat.parse(target.toString());
        } catch (ParseException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }
    
    
    
}
