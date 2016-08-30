// AddEditFragment.java
// Фрагмент для добавления нового или изменения существующего контакта
package info.krushik.android.phonebook;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import info.krushik.android.phonebook.data.DatabaseDescription.Contact;

public class AddEditFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Определяет метод обратного вызова, реализованный MainActivity
    public interface AddEditFragmentListener {
        // Вызывается при сохранении контакта
        void onAddEditCompleted(Uri contactUri);
    }

    // Константа для идентификации Loader
    private static final int CONTACT_LOADER = 0;

    private AddEditFragmentListener listener; // MainActivity
    private Uri contactUri; // Uri выбранного контакта
    private boolean addingNewContact = true; // Добавление (true) или изменение

    // Компоненты EditText для информации контакта
    private TextInputLayout nameTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
    private TextInputLayout emailTextInputLayout;

    private FloatingActionButton saveContactFAB;
    private CoordinatorLayout coordinatorLayout; // Для SnackBar

    // Назначение AddEditFragmentListener при присоединении фрагмента
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
    }

    // Удаление AddEditFragmentListener при отсоединении фрагмента
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // Вызывается при создании представлений фрагмента
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // У фрагмента есть команды меню

        // Заполнение GUI и получение ссылок на компоненты EditText
        View view =
                inflater.inflate(R.layout.fragment_add_edit, container, false);
        nameTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.nameTextInputLayout);
        nameTextInputLayout.getEditText().addTextChangedListener(
                nameChangedListener);
        phoneTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.phoneTextInputLayout);
        emailTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.emailTextInputLayout);


        // Назначение слушателя событий FloatingActionButton
        saveContactFAB = (FloatingActionButton) view.findViewById(
                R.id.saveFloatingActionButton);
        saveContactFAB.setOnClickListener(saveContactButtonClicked);
        updateSaveButtonFAB();

        // Используется для отображения SnackBar с короткими сообщениями
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(
                R.id.coordinatorLayout);

        Bundle arguments = getArguments(); // null при создании контакта

        if (arguments != null) {
            addingNewContact = false;
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);
        }

        // При изменении существующего контакта создать Loader
        if (contactUri != null)
            getLoaderManager().initLoader(CONTACT_LOADER, null, this);

        return view;
    }

    // Обнаруживает изменения в тексте поля EditTex, связанного
    // с nameTextInputLayout, для отображения или скрытия saveButtonFAB
    private final TextWatcher nameChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        // Вызывается при изменении текста в nameTextInputLayout
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            updateSaveButtonFAB();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    // Кнопка saveButtonFAB видна, если имя не пусто
    private void updateSaveButtonFAB() {
        String input =
                nameTextInputLayout.getEditText().getText().toString();

        // Если для контакта указано имя, показать FloatingActionButton
        if (input.trim().length() != 0)
            saveContactFAB.show();
        else
            saveContactFAB.hide();
    }

    // Реагирует на событие, генерируемое при сохранении контакта
    private final View.OnClickListener saveContactButtonClicked =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Скрыть виртуальную клавиатуру
                    ((InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getView().getWindowToken(), 0);
                    saveContact(); // Сохранение контакта в базе данных
                }
            };

    // Сохранение информации контакта в базе данных
    private void saveContact() {
        // Создание объекта ContentValues с парами "ключ—значение"
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contact.COLUMN_NAME,
                nameTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_PHONE,
                phoneTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_EMAIL,
                emailTextInputLayout.getEditText().getText().toString());


        if (addingNewContact) {
            // Использовать объект ContentResolver активности для вызова
            // insert для объекта AddressBookContentProvider
            Uri newContactUri = getActivity().getContentResolver().insert(
                    Contact.CONTENT_URI, contentValues);

            if (newContactUri != null) {
                Snackbar.make(coordinatorLayout,
                        R.string.contact_added, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newContactUri);
            } else {
                Snackbar.make(coordinatorLayout,
                        R.string.contact_not_added, Snackbar.LENGTH_LONG).show();
            }
        } else {
            // Использовать объект ContentResolver активности для вызова
            // update для объекта AddressBookContentProvider
            int updatedRows = getActivity().getContentResolver().update(
                    contactUri, contentValues, null, null);

            if (updatedRows > 0) {
                listener.onAddEditCompleted(contactUri);
                Snackbar.make(coordinatorLayout,
                        R.string.contact_updated, Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(coordinatorLayout,
                        R.string.contact_not_updated, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // Вызывается LoaderManager для создания Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Создание CursorLoader на основании аргумента id; в этом
        // фрагменте только один объект Loader, и команда switch не нужна
        switch (id) {
            case CONTACT_LOADER:
                return new CursorLoader(getActivity(),
                        contactUri, // Uri отображаемого контакта
                        null, // Все столбцы
                        null, // Все записи
                        null, // Без аргументов
                        null); // Порядок сортировки
            default:
                return null;
        }
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

            // Заполнение компонентов EditText полученными данными
            nameTextInputLayout.getEditText().setText(
                    data.getString(nameIndex));
            phoneTextInputLayout.getEditText().setText(
                    data.getString(phoneIndex));
            emailTextInputLayout.getEditText().setText(
                    data.getString(emailIndex));

            updateSaveButtonFAB();
        }
    }

    // Вызывается LoaderManager при сбросе Loader
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
