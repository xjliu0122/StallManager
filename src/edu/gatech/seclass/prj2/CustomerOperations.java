package edu.gatech.seclass.prj2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import edu.gatech.seclass.prj2.pojos.Customer;
import edu.gatech.seclass.prj2.pojos.Discount;
import edu.gatech.seclass.prj2.pojos.GoldMemberDiscount;
import edu.gatech.seclass.prj2.pojos.Money;
import edu.gatech.seclass.prj2.pojos.RewardDiscount;
import edu.gatech.seclass.prj2.pojos.Transaction;

public class CustomerOperations {

  private DatabaseWrapper databaseWrapper;
  private SQLiteDatabase db;

  public CustomerOperations(Context context) {
    databaseWrapper = new DatabaseWrapper(context);
  }
  
  // a constructor for changing developmentData status
  public CustomerOperations(Context context, boolean fillDB) {
	    databaseWrapper = new DatabaseWrapper(context, fillDB);
	  }

  /**
   * Open the database.
   * 
   * @throws SQLException
   *           Error if the database failed to open.
   */
  public void open() throws Exception {
    db = databaseWrapper.getWritableDatabase();
  }

  /**
   * Close the database.
   */
  public void close() {
    databaseWrapper.close();
  }

  /**
   * Add a new customer to the database.
   * 
   * @param customer
   *          The update customer object
   * @throws SQLiteException
   *           Insert failed so throw SQLiteException
   */
  public long addNewCustomer(Customer customer) throws Exception {
    ContentValues values = new ContentValues();
    values.put("firstName", customer.getFirstName());
    values.put("lastName", customer.getLastName());
    values.put("email", customer.getEmailAddress());
    values.put("zip", customer.getZipCode());
    values.put("goldStatus", 0);
    values.put("rewardBalance", 0.0);
    long row = db.insert("customer", null, values);
    if (row == -1L) {
      throw new Exception("Error inserting customer into database");
    }
    return row;
  }

  /**
   * Updates the customer attributes in the database.
   * 
   * @param customer
   *          Customer to edit
   * @throws SQLiteException
   *           Database error
   */
  public void editCustomer(Customer customer) throws Exception {
    ContentValues values = new ContentValues();
    values.put("firstName", customer.getFirstName());
    values.put("lastName", customer.getLastName());
    values.put("email", customer.getEmailAddress());
    values.put("zip", customer.getZipCode());
    values.put("goldStatus", customer.isHasGoldStatus());
    values.put("rewardBalance", customer.getRewardSum().getValue()
        .toPlainString());
    db.update("customer", values, "_id=" + customer.getCustomerID(), null);
  }

  /**
   * Returns the customer with the specified customer ID.
   * 
   * @param id
   *          Customer ID
   * @return The customer with the specified ID
   * @throws SQLiteException
   *           Database error
   */
  public Customer getCustomer(int id) throws Exception {
    Cursor cursor = null;
    Customer customer = null;
    try {
      cursor = db.query("customer", null, "_id=" + String.valueOf(id), null,
          null, null, null);
      cursor.moveToFirst();
      customer = cursorToCustomer(cursor);
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return customer;
  }

  /**
   * Returns a list of customers matching the specified search criteria.
   * 
   * @param firstName
   *          Customer's first name
   * @param lastName
   *          Customer's last name
   * @param email
   *          Customer's email address
   * @param zip
   *          Customer's zip code
   * @return The list of customers who match the search criteria
   * @throws SQLiteException
   *           Database error
   */
  public List<Customer> getMatchingCustomers(String firstName, String lastName,
      String email, String zip) throws Exception {
    List<Customer> customers = new ArrayList<Customer>();
    Cursor cursor = null;
    try {
      cursor = db
          .query(
              "customer",
              null,
              "firstName LIKE ? AND lastName LIKE ? AND email LIKE ? AND zip LIKE ?",
              new String[] { '%' + firstName + '%', '%' + lastName + '%',
                  '%' + email + '%', '%' + zip + '%' }, null, null, null);
      if (cursor == null) {
        // return empty list (no matches)
        return customers;
      }
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        Customer customer = cursorToCustomer(cursor);
        customers.add(customer);
        cursor.moveToNext();
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }

    return customers;
  }

  /**
   * Returns a list of transactions for a particular customer.
   * 
   * @param customerID
   *          Customer's ID
   * @return List of transactions
   * @throws SQLiteException
   *           Database error
   */
  public List<Transaction> getCustomerTransactions(int customerID)
      throws Exception {
    Customer customer = getCustomer(customerID);
    List<Transaction> transactions = new ArrayList<Transaction>();
    Cursor cursor = null;
    try {
      String[] args = { String.valueOf(customerID) };
      cursor = db.rawQuery(
          "SELECT * FROM \"transaction\" WHERE customerID = ?", args);
      // cursor = db.query("transaction", null, "_id=" +
      // String.valueOf(customerID), null, null, null, null);
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        Transaction transaction = cursorToTransaction(cursor, customer);
        // get the discounts based on transactionID
        List<Discount> discounts = getTransactionDiscounts(cursor.getInt(cursor.getColumnIndex("_id")));
        Discount[] new_discounts = new Discount[discounts.size()];
        for (int i = 0; i < discounts.size(); i++) {
          new_discounts[i] = discounts.get(i);
        }
        transaction.setDiscountsApplied(new_discounts);
        transactions.add(transaction);
        cursor.moveToNext();
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return transactions;
  }

  /**
   * Returns a list of discounts for a particular transaction.
   * 
   * @param transactionID
   *          The transaction ID
   * @return List of discounts for transaction
   * @throws SQLiteException
   *           Database error
   */
  public List<Discount> getTransactionDiscounts(int transactionID)
      throws Exception {
    List<Discount> discounts = new ArrayList<Discount>();
    Cursor cursor = null;
    try {
      String[] args = { String.valueOf(transactionID) };
      cursor = db.rawQuery("SELECT * FROM discount WHERE transactionID = ?",
          args);
      if (cursor.moveToFirst()) {
        do {
          Discount discount = cursorToDiscount(cursor);
          discounts.add(discount);
        } while (cursor.moveToNext());
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return discounts;
  }

  /**
   * Returns a cursor to iterate over matching customers.
   * 
   * @param filter
   * @return
   * @throws SQLiteException
   *           Database error
   */
  public Cursor filteredCursor(String filter) throws Exception {
    String query = "SELECT _id, firstName || \" \" || lastName || \" <\" || email || \">\" AS fullName FROM customer";
    if (filter != null) {
      query = query + " WHERE firstName LIKE '" + filter
          + "%' OR lastName LIKE '" + filter + "%' OR email LIKE '%" + filter
          + "%'";
    }
    Cursor c = db.rawQuery(query, null);
    return c;
  }

  /**
   * Returns a customer object for the current cursor row. Precondition: The
   * cursor must point to a row in the customer table.
   * 
   * @param cursor
   *          Database cursor
   * @return Customer Result of current cursor row as Customer
   */
  private Customer cursorToCustomer(Cursor cursor) throws Exception {
    if (cursor == null || cursor.isClosed()) {
      throw new SQLException(
          "Error: Cannot call cursorToCustomer on a null or closed cursor.");
    }
    Customer customer = new Customer();
    customer.setCustomerID(cursor.getInt(cursor.getColumnIndex("_id")));
    customer.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
    customer.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
    customer.setEmailAddress(cursor.getString(cursor.getColumnIndex("email")));
    customer.setZipCode(cursor.getString(cursor.getColumnIndex("zip")));
    customer.setHasGoldStatus(cursor.getInt(cursor.getColumnIndex("goldStatus")) != 0);
    customer.setRewardSum(new Money(cursor.getDouble(cursor.getColumnIndex("rewardBalance"))));
    return customer;
  }

  /**
   * Returns a transaction from the cursor.
   * 
   * @param cursor
   *          Database cursor
   * @param customer
   *          Customer for the transaction
   * @return Transaction object for the data pointed to by cursor
   */
  private Transaction cursorToTransaction(Cursor cursor, Customer customer) {
    Transaction transaction = new Transaction();
    Money finalAmount = new Money(cursor.getDouble(cursor.getColumnIndex("amount")));
    transaction.setCustomer(customer);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("date")));
    transaction.setDate(calendar.getTime());
    transaction.setFinalAmount(finalAmount);
    return transaction;
  }

  /**
   * Returns a Discount object from the cursor. 
   * 
   * @param cursor Database cursor
   * @return Discount
   */
  private Discount cursorToDiscount(Cursor cursor) {
    Discount discount;
    if (cursor.getInt(cursor.getColumnIndex("discountTypeID")) == Discount.GOLD_DISCOUNT) {
      discount = new GoldMemberDiscount();
    } else {
      discount = new RewardDiscount();
    }
    discount.setDiscountAmount(new Money(cursor.getDouble(cursor.getColumnIndex("discountAmount"))));
    return discount;
  }
  
  /**
   * Return a sum of all transactions for the current year.
   * @param customerID The customer ID
   * @return Money Sum of all transactions
   * @throws SQLiteException Database error
   */
  public Money getCustomerYearlyTotal(int customerID) throws Exception {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.set(Calendar.DAY_OF_YEAR, 1);
    long start = calendar.getTime().getTime();
    calendar.set(Calendar.DAY_OF_YEAR, 365);
    long end = calendar.getTime().getTime();
    double sum = 0d;
    Cursor cursor = null;
    String query = "SELECT SUM(amount) AS total FROM \"transaction\" WHERE customerID = "
        + String.valueOf(customerID)
        + " AND date > "
        + String.valueOf(start)
        + " AND date < " + String.valueOf(end);
    try {
      cursor = db.rawQuery(query, null);
      if (cursor == null) {
        return new Money(sum);
      }
      cursor.moveToFirst();
      sum = cursor.getDouble(cursor.getColumnIndex("total"));
    } catch (Exception e) {
      System.err.println(e.getMessage());
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return new Money(sum);
  }

  public Transaction getTransaction(long transactionId) throws Exception {
    Transaction transaction = new Transaction();
    Cursor c = db.query("\"transaction\"", null, "_id = ?", new String[] {String.valueOf(transactionId)}, null, null, null);
    c.moveToFirst();
    Customer customer = getCustomer(c.getInt(c.getColumnIndex("customerID")));
    transaction.setCustomer(customer);
    transaction.setDate(new Date(c.getLong(c.getColumnIndex("date"))));
    transaction.setFinalAmount(new Money(c.getDouble(c.getColumnIndex("amount"))));
    List<Discount> discounts = new ArrayList<Discount>();
    Cursor d = db.query("discount", null, "transactionID = ?", new String[] {String.valueOf(c.getInt(c.getColumnIndex("_id")))}, null, null, null);
    if (d != null) {
      d.moveToFirst();
      while (!d.isAfterLast()) {
        Discount discount;
        if (d.getInt(d.getColumnIndex("discountTypeID")) == Discount.GOLD_DISCOUNT) {
          discount = new GoldMemberDiscount();
        } else {
          discount = new RewardDiscount();
        }
        discount.setDiscountAmount(new Money(d.getDouble(d.getColumnIndex("discountAmount"))));
        discounts.add(discount);
        d.moveToNext();
      }
    }
    transaction.setDiscountsApplied(discounts.toArray(new Discount[0]));
    return transaction;
  }
  
}
