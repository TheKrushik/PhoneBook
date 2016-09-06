// AddressBookDatabaseHelper.java
// Субкласс SQLiteOpenHelper, определяющий базу данных приложения
package info.krushik.android.phonebook.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import info.krushik.android.phonebook.User;
import info.krushik.android.phonebook.data.DatabaseDescription.Contact;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AddressBook.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME_USER = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LOGIN = "login";
    private static final String COLUMN_PASSWORD = "password";

    int COL_ID_INDEX = 0;
    int COL_LOGIN_INDEX = 1;
    int COL_PASSWORD_INDEX = 2;

    private SQLiteDatabase db;


    // Конструктор
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Создание таблицы contacts при создании базы данных
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_NAME_USER + "(" +
                COLUMN_ID + " integer primary key not null, " +
                COLUMN_LOGIN + " TEXT not null , " +
                COLUMN_PASSWORD + " TEXT not null);";
        db.execSQL(CREATE_USER_TABLE);
        this.db = db;

        // Команда SQL для создания таблицы contacts
        final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + Contact.TABLE_NAME + "(" +
                Contact._ID + " integer primary key, " +
                Contact.COLUMN_NAME + " TEXT, " +
                Contact.COLUMN_PHONE + " TEXT, " +
                Contact.COLUMN_EMAIL + " TEXT);";
        db.execSQL(CREATE_CONTACTS_TABLE); // Создание таблицы contacts
    }

    // Обычно определяет способ обновления при изменении схемы базы данных
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertUser(User user) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "select * from " + TABLE_NAME_USER;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        values.put(COLUMN_ID, count);
        values.put(COLUMN_LOGIN, user.getLogin());
        values.put(COLUMN_PASSWORD, user.getPassword());

        db.insert(TABLE_NAME_USER, null, values);
        db.close();
    }

    public String searchPass(String login) {
        db = this.getReadableDatabase();
        String query = "select * from " + TABLE_NAME_USER;
        Cursor cursor = db.rawQuery(query, null);
        String a, b;
        b = "not faund";
        if (cursor.moveToFirst()) {
            do {
                a = cursor.getString(COL_LOGIN_INDEX);
                b = cursor.getString(COL_PASSWORD_INDEX);
                if (a.equals(login)){
                    b = cursor.getString(COL_PASSWORD_INDEX);
                    break;
                }
            }
            while (cursor.moveToNext());
        }
        return b;
    }


}
