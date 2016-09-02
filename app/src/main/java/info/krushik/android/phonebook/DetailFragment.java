// DetailFragment.java
// Субкласс Fragment для вывода подробной информации о контакте
package info.krushik.android.phonebook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.krushik.android.phonebook.data.DatabaseDescription.Contact;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Методы обратного вызова, реализованные MainActivity
    public interface DetailFragmentListener {
        void onContactDeleted(); // Вызывается при удалении контакта

        // Передает URI редактируемого контакта DetailFragmentListener
        void onEditContact(Uri contactUri);
    }

    private static final int CONTACT_LOADER = 0; // Идентифицирует Loader

    private DetailFragmentListener listener; // MainActivity
    private Uri contactUri; // Uri выбранного контакта

    private TextView nameTextView; // Имя контакта
    private TextView phoneTextView; // Телефон
    private TextView emailTextView; // Электронная почта

    // Назначение DetailFragmentListener при присоединении фрагмента
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
    }

    // Удаление DetailFragmentListener при отсоединении фрагмента
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // Вызывается при создании представлений фрагмента
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // У фрагмента есть команды меню

        // Получение объекта Bundle с аргументами и извлечение URI
        Bundle arguments = getArguments();

        if (arguments != null)
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);

        // Заполнение макета DetailFragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // Получение компонентов EditTexts
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
        emailTextView = (TextView) view.findViewById(R.id.emailTextView);

        // Загрузка контакта
        getLoaderManager().initLoader(CONTACT_LOADER, null, this);
        return view;

    }

    // Отображение команд меню фрагмента
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    // Обработка выбора команд меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                listener.onEditContact(contactUri); // Передача Uri слушателю
                return true;
            case R.id.action_delete:
                deleteContact();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Удаление контакта
    private void deleteContact() {
        // FragmentManager используется для отображения confirmDelete
        confirmDelete.show(getFragmentManager(), "confirm delete");
    }

    // DialogFragment для подтверждения удаления контакта
    public final DialogFragment confirmDelete = new DialogFragment() {
        // Создание объекта AlertDialog и его возвращение
        @Override
        public Dialog onCreateDialog(Bundle bundle) {
            // Создание объекта AlertDialog.Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.confirm_title);
            builder.setMessage(R.string.confirm_message);

            // Кнопка OK просто закрывает диалоговое окно
            builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int button) {

                    // объект ContentResolver используется
                    // для вызова delete в PhoneBookContentProvider
                    getActivity().getContentResolver().delete(contactUri, null, null);
                    listener.onContactDeleted(); // Оповещение слушателя
                }
            });

            builder.setNegativeButton(R.string.button_cancel, null);
            return builder.create(); // Вернуть AlertDialog
        }
    };

    // Вызывается LoaderManager для создания Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Создание CursorLoader на основании аргумента id; в этом
        // фрагменте только один объект Loader, и команда switch не нужна
        CursorLoader cursorLoader;

        switch (id) {
            case CONTACT_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                        contactUri, // Uri отображаемого контакта
                        null, // Все столбцы
                        null, // Все записи
                        null, // Без аргументов
                        null); // Порядок сортировки
                break;
            default:
                cursorLoader = null;
                break;
        }

        return cursorLoader;
    }

    // Вызывается LoaderManager при завершении загрузки
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Если контакт существует в базе данных, вывести его информацию
        if (data != null && data.moveToFirst()) {
            // Получение индекса столбца для каждого элемента данных
            int nameIndex = data.getColumnIndex(Contact.COLUMN_NAME);
            int phoneIndex = data.getColumnIndex(Contact.COLUMN_PHONE);
            int emailIndex = data.getColumnIndex(Contact.COLUMN_EMAIL);

            // Заполнение TextView полученными данными
            nameTextView.setText(data.getString(nameIndex));
            phoneTextView.setText(data.getString(phoneIndex));
            emailTextView.setText(data.getString(emailIndex));

        }
    }

    // Вызывается LoaderManager при сбросе Loader
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}