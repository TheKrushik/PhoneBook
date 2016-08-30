// DatabaseDescription.java
// Класс описывает имя таблицы и имена столбцов базы данных, а также
// содержит другую информацию, необходимую для ContentProvider
package info.krushik.android.phonebook.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {
    // Имя ContentProvider: обычно совпадает с именем пакета
    public static final String AUTHORITY =
            "info.krushik.android.phonebook.data";

    // Базовый URI для взаимодействия с ContentProvider
    private static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    // Вложенный класс, определяющий содержимое таблицы contacts
    public static final class Contact implements BaseColumns {
        public static final String TABLE_NAME = "contacts"; // Имя таблицы

        // Объект Uri для таблицы contacts
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        // Имена столбцов таблицы
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_EMAIL = "email";


        // Создание Uri для конкретного контакта
        public static Uri buildContactUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}