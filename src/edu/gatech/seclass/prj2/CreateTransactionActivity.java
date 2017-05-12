package edu.gatech.seclass.prj2;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import edu.gatech.seclass.prj2.pojos.CreditCard;
import edu.gatech.seclass.prj2.pojos.Customer;
import edu.gatech.seclass.prj2.pojos.Discount;
import edu.gatech.seclass.prj2.pojos.GoldMemberDiscount;
import edu.gatech.seclass.prj2.pojos.Money;
import edu.gatech.seclass.prj2.pojos.RewardDiscount;
import edu.gatech.seclass.prj2.pojos.Transaction;
import edu.gatech.seclass.services.CreditCardService;
import edu.gatech.seclass.services.EmailService;
import edu.gatech.seclass.services.PaymentService;

public class CreateTransactionActivity extends Activity {

  private AutoCompleteTextView autocompleteCustomers;
  private SimpleCursorAdapter sca;
  private CustomerOperations customerdata;
  private TransactionOperations transactiondata;
  private Date currentDate;
  private Customer customer;
  private List<Discount> discounts;
  private Money total;
  private Money remainingReward;
  private Money earnedReward;
  private CreditCard creditCard;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_transaction);

    discounts = new ArrayList<Discount>();
    total = new Money(0d);
    remainingReward = new Money(0d);
    earnedReward = new Money(0d);

    /* initialize date */
    TextView textViewDate = (TextView) findViewById(R.id.ctTextViewDate);
    currentDate = new Date();
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String dateString = dateFormat.format(currentDate);
    textViewDate.setText(dateString);

    customerdata = new CustomerOperations(this);
    try {
      customerdata.open();
    } catch (Exception e) {
      // display error on home screen
      Intent intent = new Intent(this, MainActivity.class);
      intent.putExtra("msgText", "Database Error");
      startActivity(intent);
      finish();
      return;
    }
    transactiondata = new TransactionOperations(this);
    try {
      transactiondata.open();
    } catch (Exception e) {
      // display error on home screen
      Intent intent = new Intent(this, MainActivity.class);
      intent.putExtra("msgText", "Database Error");
      startActivity(intent);
      finish();
      return;
    }
    initAutoComplete();
    initSubtotalTextChangeListener();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
  // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.create_transaction, menu);
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

  /**
   * Initialized the auto-complete text view. source:
   * http://www.mekya.com/2013/09/17/how-to-use-autocompletetextview-and
   * -simplecursoradapter-together-in-android/
   */
  private void initAutoComplete() {
    autocompleteCustomers = (AutoCompleteTextView) findViewById(R.id.ctAutoCompleteTextViewCustomer);
    String[] from = new String[] { "fullName" };
    int[] to = new int[] { android.R.id.text1 };
    sca = new SimpleCursorAdapter(this,
        android.R.layout.simple_dropdown_item_1line, null, from, to, 0);
    sca.setFilterQueryProvider(new FilterQueryProvider() {
      public Cursor runQuery(CharSequence str) {
        Cursor cursor = null;
        String filter = null;
        if (str != null) {
          filter = str.toString();
        }
        try {
          cursor = customerdata.filteredCursor(filter);
        } catch (Exception e) {
          // display error on home screen
          Intent intent = new Intent(CreateTransactionActivity.this,
              MainActivity.class);
          intent.putExtra("msgText", "Database Error");
          startActivity(intent);
          finish();
        }
        return cursor;
      }
    });

    sca.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
      @Override
      public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(1);
      }
    });

    autocompleteCustomers.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        Cursor cursor = (Cursor) sca.getItem(position);

        /*
         * TOAST FOR DEBUGGING PURPOSES ONLY
         * Toast.makeText(CreateTransactionActivity.this, cursor.getString(0),
         * Toast.LENGTH_LONG).show(); /* END DEBUGGING STATEMENTS
         */

        // set the customer
        int customerID = cursor.getInt(0);

        try {
          customer = customerdata.getCustomer(customerID);
        } catch (Exception e) {
          // display error on home screen
          Intent intent = new Intent(CreateTransactionActivity.this,
              MainActivity.class);
          intent.putExtra("msgText", "Database Error");
          startActivity(intent);
          finish();
        }

        // enable input of disabled fields
        TextView editTextSubtotal = (TextView) findViewById(R.id.ctEditTextSubtotal);
        editTextSubtotal.setEnabled(true);
        autocompleteCustomers.setEnabled(false);
      }

    });

    // set the simple cursor adapter
    autocompleteCustomers.setAdapter(sca);

    // set focus on customer name
    autocompleteCustomers.requestFocus();
  }

  /**
   * Updates the transaction total and discounts when the subtotal value is
   * updated.
   */
  private void initSubtotalTextChangeListener() {
    EditText t = (EditText) findViewById(R.id.ctEditTextSubtotal);
    t.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        // if the subtotal is not empty, set total to subtotal
        // else set subtotal to zero
        String subtotal = s.toString();
        if (subtotal.length() > 0) {
          setTotal(new Money(Double.valueOf(subtotal)));
          applyDiscounts(Double.valueOf(subtotal));
        } else {
          setTotal(new Money(0d));
          applyDiscounts(0d);
        }
        updateDiscountView();
        updateTotalView();
      }
    });

  }

  /**
   * Applies reward and gold member discounts to transaction.
   * 
   * @param subtotal
   *          The transaction subtotal
   */
  private void applyDiscounts(double subtotal) {
    // clear discounts array
    discounts.clear();
    // Apply Gold Member Discount
    if (customer.isHasGoldStatus()) {
      Money gmdDiscountAmount = new Money(subtotal
          * GoldMemberDiscount.DISCOUNT_PERCENTAGE);
      Discount gmd = new GoldMemberDiscount();
      gmd.setDiscountAmount(gmdDiscountAmount);
      discounts.add(gmd);
      setTotal(Money.subtract(total, gmdDiscountAmount));
    }
    // Apply Reward Discount
    if (customer.getRewardSum().getValue().compareTo(BigDecimal.ZERO) > 0) {
      Money rewardSum = customer.getRewardSum();
      if (total.getValue().compareTo(rewardSum.getValue()) >= 0) {
        setTotal(Money.subtract(total, rewardSum));
        remainingReward = new Money(0d);
        Discount rewardDiscount = new RewardDiscount();
        rewardDiscount.setDiscountAmount(rewardSum);
        discounts.add(rewardDiscount);
      } else {
        remainingReward = Money.subtract(rewardSum, total);
        Discount rewardDiscount = new RewardDiscount();
        rewardDiscount.setDiscountAmount(total);
        discounts.add(rewardDiscount);
        setTotal(new Money(0d));
      }
    }
  }

  /**
   * Set the transaction total.
   * 
   * @param m
   *          Money
   */
  private void setTotal(Money m) {
    total = m;
  }

  /**
   * Updates the transaction discount amount label.
   */
  private void updateDiscountView() {
    TextView discountView = (TextView) findViewById(R.id.ctTextViewDiscount);
    Money totalDiscount = new Money(0d);
    for (Discount d : discounts) {
      totalDiscount = Money.add(totalDiscount, d.getDiscountAmount());
    }
    discountView.setText(totalDiscount.toString());
  }

  /**
   * Updates the transaction total label.
   */
  private void updateTotalView() {
    TextView discountView = (TextView) findViewById(R.id.ctTextViewTransactionTotal);
    discountView.setText(total.toString());
  }

  /**
   * Retrieves credit card from scanner and populates form.
   * 
   * @param view
   *          View
   */
  public void swipe(View view) {
    TextView ccNumber = (TextView) findViewById(R.id.ctEditTextCreditCard);
    TextView ccExpiration = (TextView) findViewById(R.id.ctEditTextExpiration);
    TextView ccCCV = (TextView) findViewById(R.id.ctEditTextCCV);
    // clear existing entries
    ccNumber.setText("");
    ccExpiration.setText("");
    ccCCV.setText("");
    setMessage("");
    // get credit card from credit card scanner
    String cardInfo = CreditCardService.getCardInfo();
    if (cardInfo.equals("Error")) {
      setMessage("Error reading credit card. Try again.");
      return;
    } else {
      creditCard = new CreditCard(cardInfo);
    }
    ccNumber.setText(creditCard.getAccountNumber());
    ccExpiration.setText(new SimpleDateFormat("MM/dd/yyyy").format(creditCard
        .getExpirationDate()));
    ccCCV.setText(creditCard.getSecurityCode());
  }

  /**
   * Sets the status/error message to provide user with feedback about potential
   * issues during transactions, and upon success.
   * 
   * @param msg
   *          The message to display
   */
  private void setMessage(String msg) {
    TextView errorLabel = (TextView) findViewById(R.id.ctErrorLabel);
    errorLabel.setText(msg);
  }

  /**
   * Returns true if all form data passes validation.
   * 
   * @return boolean True if validation passed, false otherwise
   */
  private boolean validFormInput() {
    // check that a customer has been selected
    if (customer == null) {
      setMessage("Error - Select a customer.");
      return false;
    }
    // check subtotal is not empty
    if (((TextView) findViewById(R.id.ctEditTextSubtotal)).getText().toString()
        .isEmpty()) {
      setMessage("Error - Please enter subtotal.");
      return false;
    }
    // check credit card is valid
    if (!ValidatorTools
        .isValidCreditCard(((TextView) findViewById(R.id.ctEditTextCreditCard))
            .getText().toString())) {
      setMessage("Error - Please enter credit card number.");
      return false;
    }
    // check CCV is valid
    if (!ValidatorTools
        .isValidCCV(((TextView) findViewById(R.id.ctEditTextCCV)).getText()
            .toString())) {
      setMessage("Error - Please enter CCV.");
      return false;
    }
    // check card expiration is valid date
    if (!ValidatorTools
        .isValidDate(((TextView) findViewById(R.id.ctEditTextExpiration))
            .getText().toString())) {
      setMessage("Error - Please enter expiration date (mm/dd/yyyy).");
      return false;
    }
    return true;
  }

  /**
   * Processes the transaction.
   * 
   * @param view
   *          View
   */
  public void processTransactions(View view) {
    // check all forms for valid entries before proceeding with transactions
    if (!validFormInput()) {
      return;
    }

    // create new transaction
    Transaction transaction = new Transaction();
    transaction.setCustomer(customer);
    transaction.setDate(currentDate);
    transaction.setDiscountsApplied(discounts.toArray(new Discount[0]));
    transaction.setFinalAmount(total);

    // get approval from payment processing service
    DateFormat paymentDateFormat = new SimpleDateFormat("MMddyyyy");
    boolean paymentApproved = PaymentService.processPayment(
        creditCard.getCardHolderFirstName(),
        creditCard.getCardHolderLastName(),
        creditCard.getAccountNumber(),
        paymentDateFormat.format(creditCard.getExpirationDate()),
        creditCard.getSecurityCode(),
        transaction.getFinalAmount().getValue().doubleValue());

    // display message if declined
    if (!paymentApproved) {
      setMessage("Credit Card Declined.");
      return;
    }

    // update/add customers rewards and notify customers,
    addRewardsToCustomer();

    // save back to database
    try {
      customerdata.editCustomer(customer);
      transactiondata.addNewTransaction(transaction);
    } catch (Exception e) {
      // display error on home screen
      Intent intent = new Intent(CreateTransactionActivity.this,
          MainActivity.class);
      intent.putExtra("msgText", "Database Error");
      startActivity(intent);
      finish();
    }

    setMessage("Transaction Approved");

    Money zeroAmount = new Money(0.00);
    String goldStatusDiscountApplied = zeroAmount.toString(), rewardsApplied = zeroAmount
        .toString();
    Discount[] discounts = transaction.getDiscountsApplied();
    for (int j = 0; j < discounts.length; j++) {
      if (discounts[j] instanceof GoldMemberDiscount) {
        goldStatusDiscountApplied = discounts[j].getDiscountAmount().toString();
      } else if (discounts[j] instanceof RewardDiscount) {
        rewardsApplied = discounts[j].getDiscountAmount().toString();
      } else {
        System.err.println("Unknown Discount type");
      }
    }
    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);

    Intent intent = new Intent(this, ViewPaymentStatusActivity.class);
    intent.putExtra("customerID", customer.getCustomerID());
    intent.putExtra("date", df.format(transaction.getDate()));
    intent.putExtra("totalCharged", transaction.getFinalAmount().toString());
    intent.putExtra("goldStatusDiscountApplied", goldStatusDiscountApplied);
    intent.putExtra("rewardsApplied", rewardsApplied);
    intent.putExtra("earnedReward", earnedReward.toString());

    startActivity(intent);
    finish();
  }

  // We attempt to send an email, but we if it fails, we do not resend

  /**
   * Adds elegible rewards to customer.
   */
  private void addRewardsToCustomer() {
    // rewards
    if (total.getValue().doubleValue() >= 100d) {
      // they earn $10
      earnedReward = new Money(10d);
      customer.setRewardSum(Money.add(remainingReward, new Money(10d)));
      EmailService.sendEmail(customer.getEmailAddress(), "Reward",
          "You earned a $10 reward"
              + " for spending  >= $100 in a single transaction.");
    } else {
      customer.setRewardSum(remainingReward);
    }
    // gold member status
    double yearlyTotal = 0d;
    try {
      yearlyTotal = customerdata
          .getCustomerYearlyTotal(customer.getCustomerID()).getValue()
          .doubleValue();
    } catch (Exception e) {
      // display error on home screen
      Intent intent = new Intent(CreateTransactionActivity.this,
          MainActivity.class);
      intent.putExtra("msgText", "Database Error");
      startActivity(intent);
      finish();
    }

    yearlyTotal += total.getValue().doubleValue();
    if (yearlyTotal >= 1000d) {
      customer.setHasGoldStatus(true);
      EmailService
          .sendEmail(customer.getEmailAddress(), "Gold Status", "You unlocked "
              + "Gold Status by spending >= $1000 during the year!");
    }
  }

  /**
   * Returns to the main activity.
   * 
   * @param view
   *          View
   */
  public void goHome(View view) {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }

}
