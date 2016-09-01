package info.krushik.android.phonebook.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;



public class LoginDataBaseAdapter {

    public static final String TABLE_NAME = "LOGIN";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_USERNAME = "USERNAME";
    public static final String COLUMN_PASSWORD = "PASSWORD";



    // Variable to hold the database instance
    public SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private AddressBookDatabaseHelper dbHelper;

    public LoginDataBaseAdapter(Context _context) {
        context = _context;
        dbHelper = new AddressBookDatabaseHelper(context);
    }

    public LoginDataBaseAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    public void insertEntry(String userName, String password) {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put(COLUMN_USERNAME, userName);
        newValues.put(COLUMN_PASSWORD, password);

        // Insert the row into your table
        db.insert(TABLE_NAME, null, newValues);
        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }

    public int deleteEntry(String UserName) {
        //String id=String.valueOf(ID);
        String where = COLUMN_USERNAME + "=?";
        int numberOFEntriesDeleted = db.delete(TABLE_NAME, where, new String[]{UserName});
        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;
    }

    public String getSinlgeEntry(String userName) {
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_USERNAME + " =?", new String[]{userName}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
        cursor.close();
        return password;
    }

    public void updateEntry(String userName, String password) {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put(COLUMN_USERNAME, userName);
        updatedValues.put(COLUMN_PASSWORD, password);

        String where = COLUMN_USERNAME + " = ?";
        db.update(TABLE_NAME, updatedValues, where, new String[]{userName});
    }
}
