// AddressBookDatabaseHelper.java
// Субкласс SQLiteOpenHelper, определяющий базу данных приложения
package info.krushik.android.phonebook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import info.krushik.android.phonebook.data.DatabaseDescription.Contact;

class AddressBookDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AddressBook.db";
    private static final int DATABASE_VERSION = 1;

    // Конструктор
    public AddressBookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Создание таблицы contacts при создании базы данных
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + LoginDataBaseAdapter.TABLE_NAME + " ("
                + LoginDataBaseAdapter.COLUMN_ID + " integer primary key autoincrement,"
                + LoginDataBaseAdapter.COLUMN_USERNAME + " text,"
                + LoginDataBaseAdapter.COLUMN_PASSWORD + " text);");

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
}
