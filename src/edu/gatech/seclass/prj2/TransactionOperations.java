package edu.gatech.seclass.prj2;

import java.util.ArrayList;
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

public class TransactionOperations {

  private DatabaseWrapper databaseWrapper;
  private SQLiteDatabase db;

  public TransactionOperations(Context context) {
    databaseWrapper = new DatabaseWrapper(context);
  }

  /**
   * Opens a database connection.
   * @throws SQLException
   */
  public void open() throws Exception {
    db = databaseWrapper.getWritableDatabase();
  }

  /**
   * Closes a database connection.
   */
  public void close() {
    databaseWrapper.close();
  }

  /**
   * Returns true if database connection is open.
   * @return
   */
  public boolean isOpen() {
    if (db != null) {
      return db.isOpen();
    }
    return false;
  }

  /**
   * Add a new transactions to the database.
   * @param transaction
   */
  public long addNewTransaction(Transaction transaction) throws Exception {
    ContentValues values = new ContentValues();
    values.put("customerID", transaction.getCustomer().getCustomerID());
    values.put("amount", transaction.getFinalAmount().toPlainString());
    values.put("date", transaction.getDate().getTime());
    long transactionID = db.insert("\"transaction\"", null, values);
    if (transactionID == -1L) {
      throw new Exception("Error inserting transaction into database");
    }
    for (Discount discount : transaction.getDiscountsApplied()) {
      ContentValues discountValues = new ContentValues();
      discountValues.put("transactionID", transactionID);
      if (discount instanceof GoldMemberDiscount) {
        discountValues.put("discountTypeID", Discount.GOLD_DISCOUNT);
      } else if (discount instanceof RewardDiscount) {
        discountValues.put("discountTypeID", Discount.REWARD_DISCOUNT);
      }
      discountValues.put("discountAmount", discount.getDiscountAmount()
          .toPlainString());
      long result = db.insert("discount", null, discountValues);
      if (result == -1L) {
        throw new Exception("Error inserting discount into database");
      }
    }
    return transactionID;
  }

  /**
   * Get a list of all transactions with pagination.
   * @param page
   * @return
   * @throws SQLiteException
   */
  public List<Transaction> getAllTransactions(int page) throws Exception {
    String query = "SELECT \"transaction\".\"_id\", \"transaction\".customerID, \"transaction\".amount, \"transaction\".date, customer.firstName, customer.lastName, customer.email, customer.zip, customer.goldStatus, customer.rewardBalance FROM \"transaction\" INNER JOIN customer ON \"transaction\".customerID = customer.\"_id\" LIMIT "
        + String.valueOf(20 * page) + ", 20;";
    List<Transaction> transactions = new ArrayList<Transaction>();
    Cursor c = null;
    try {
      c = db.rawQuery(query, null);
      c.moveToFirst();
      while (!c.isAfterLast()) {
        /* create customer */
        Customer customer = new Customer();
        customer.setCustomerID(c.getInt(c.getColumnIndex("customerID")));
        customer.setFirstName(c.getString(c.getColumnIndex("firstName")));
        customer.setLastName(c.getString(c.getColumnIndex("lastName")));
        customer.setEmailAddress(c.getString(c.getColumnIndex("email")));
        customer.setZipCode(c.getString(c.getColumnIndex("zip")));
        customer.setHasGoldStatus(c.getInt(c.getColumnIndex("goldStatus")) != 0);
        customer.setRewardSum(new Money(c.getDouble(c.getColumnIndex("rewardBalance"))));
        /* create transaction */
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setDate(new Date(c.getLong(c.getColumnIndex("date"))));
        transaction.setFinalAmount(new Money(c.getDouble(c.getColumnIndex("amount"))));
        /* create discounts */
        List<Discount> discounts = new ArrayList<Discount>();
        Cursor d = db.query("discount", null,
            "transactionID = " + String.valueOf(c.getInt(c.getColumnIndex("_id"))), null, null, null,
            null);
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
        transactions.add(transaction);
        c.moveToNext();
      }
    } finally {
      if (c != null) {
        c.close();
      }
    }
    return transactions;
  }

}
