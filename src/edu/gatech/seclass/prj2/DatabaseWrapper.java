package edu.gatech.seclass.prj2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseWrapper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "prems.db";
  private static final int DATABASE_VERSION = 1;
  private boolean developmentData = true; 

  public DatabaseWrapper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
  
  // a constructor for changing developmentData status
  public DatabaseWrapper(Context context, boolean devData) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    developmentData = devData;
	  }  

  @Override
  public void onCreate(SQLiteDatabase db) {
    /* Disable Foreign Key Constraints */
    db.execSQL("PRAGMA foreign_keys = false;");

    /* Create DB Tables */
    db.execSQL("CREATE TABLE \"customer\" ( \"_id\" integer PRIMARY KEY AUTOINCREMENT, \"firstName\" text, \"lastName\" text, \"email\" text, \"zip\" text, \"goldStatus\" integer, \"rewardBalance\" real );");
    db.execSQL("CREATE TABLE \"discount\" ( \"_id\" integer PRIMARY KEY AUTOINCREMENT, \"transactionID\" integer, \"discountTypeID\" integer, \"discountAmount\" numeric, CONSTRAINT \"transactionID\" FOREIGN KEY (\"transactionID\") REFERENCES \"transaction\" (\"_id\") ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT \"discountType\" FOREIGN KEY (\"discountTypeID\") REFERENCES \"discountType\" (\"_id\") ON DELETE CASCADE ON UPDATE CASCADE );");
    db.execSQL("CREATE TABLE \"discountType\" ( \"_id\" integer PRIMARY KEY AUTOINCREMENT, \"discountName\" text );");
    db.execSQL("CREATE TABLE \"transaction\" ( \"_id\" integer PRIMARY KEY AUTOINCREMENT, \"customerID\" integer, \"amount\" numeric, \"date\" integer, CONSTRAINT \"customerID\" FOREIGN KEY (\"customerID\") REFERENCES \"customer\" (\"_id\") ON DELETE CASCADE ON UPDATE CASCADE );");
    db.execSQL("CREATE UNIQUE INDEX \"email\" ON customer (\"email\" ASC);");
    db.execSQL("CREATE INDEX \"transactionID\" ON discount (\"transactionID\");");
    db.execSQL("CREATE INDEX \"customerID\" ON \"transaction\" (\"customerID\");");
    db.execSQL("INSERT INTO \"discountType\" VALUES (1, 'Gold Member Discount');");
    db.execSQL("INSERT INTO \"discountType\" VALUES (2, 'Reward Discount');");

    if (developmentData) {
      /* Create Sample Customer */
      db.execSQL("INSERT INTO \"customer\" VALUES (1, 'Madison', 'Ruvolo', 'mruvolo@hotmail.com', 94509, 1, 10.0);");
      db.execSQL("INSERT INTO \"customer\" VALUES (2, 'Collin', 'Frisina', 'collin43@gmail.com', '03820', 0, 0.0);");
      db.execSQL("INSERT INTO \"customer\" VALUES (3, 'Juana', 'Diez', 'jd1972@yahoo.com', 27343, 1, 0.0);");
      db.execSQL("INSERT INTO \"customer\" VALUES (4, 'Zachary', 'Coutee', 'zach.coutee@ymail.com', 94568, 0, 10.0);");
      db.execSQL("INSERT INTO \"customer\" VALUES (5, 'Jeanelle', 'Hargett', 'jeanelle6@gmail.com', 35112, 0, 0.0);");

      /* Create Sample Transactions */
      db.execSQL("INSERT INTO \"transaction\" VALUES (1, 1, 46.23, 1392440400000);");
      db.execSQL("INSERT INTO \"transaction\" VALUES (2, 1, 24.75, 1426879609580);");
      db.execSQL("INSERT INTO \"transaction\" VALUES (3, 1, 234.43, 1426879609580);");
      db.execSQL("INSERT INTO \"transaction\" VALUES (4, 2, 122.63, 1392440400000);");
      db.execSQL("INSERT INTO \"transaction\" VALUES (5, 2, 623.34, 1426879609580);");
      db.execSQL("INSERT INTO \"transaction\" VALUES (6, 3, 53.32, 1426879609580);");
      db.execSQL("INSERT INTO \"transaction\" VALUES (7, 3, 21.34, 1426879609580);");
      db.execSQL("INSERT INTO \"transaction\" VALUES (8, 4, 12.43, 1426879609580);");
      db.execSQL("INSERT INTO \"transaction\" VALUES (9, 5, 65.76, 1426879609580);");

      /* Create Sample Discounts */
      db.execSQL("INSERT INTO \"discount\" VALUES (1, 3, 2, 10.0);");
      db.execSQL("INSERT INTO \"discount\" VALUES (2, 4, 2, 10.0);");
      db.execSQL("INSERT INTO \"discount\" VALUES (3, 5, 2, 10.0);");
      db.execSQL("INSERT INTO \"discount\" VALUES (4, 7, 1, 4.25);");
    }
    

    /* Enable Foreign Key Constraints */
    db.execSQL("PRAGMA foreign_keys = true;");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS customer;");
    db.execSQL("DROP TABLE IF EXISTS discount;");
    db.execSQL("DROP TABLE IF EXISTS discountType;");
    db.execSQL("DROP TABLE IF EXISTS transaction;");
    onCreate(db);
  }

}
