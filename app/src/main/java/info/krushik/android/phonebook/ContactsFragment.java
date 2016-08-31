// ContactsFragment.java
// Субкласс Fragment для вывода алфавитного списка имен контактов
package info.krushik.android.phonebook;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.krushik.android.phonebook.data.DatabaseDescription.Contact;

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Метод обратного вызова, реализуемый MainActivity
    public interface ContactsFragmentListener {
        // Вызывается при выборе контакта
        void onContactSelected(Uri contactUri);

        // Вызывается при нажатии кнопки добавления
        void onAddContact();
    }

    private static final int CONTACTS_LOADER = 0; // Идентификатор Loader

    // Сообщает MainActivity о выборе контакта
    private ContactsFragmentListener listener;

    private ContactsAdapter contactsAdapter; // Адаптер для recyclerView

    // Настройка графического интерфейса фрагмента
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // У фрагмента есть команды меню

        // Заполнение GUI и получение ссылки на RecyclerView
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // recyclerView выводит элементы в вертикальном списке
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        // создание адаптера recyclerView и слушателя щелчков на элементах
        contactsAdapter = new ContactsAdapter(new ContactsAdapter.ContactClickListener() {
            @Override
            public void onClick(Uri contactUri) {
                listener.onContactSelected(contactUri);
            }
        });
        recyclerView.setAdapter(contactsAdapter); // Назначение адаптера

        // Присоединение ItemDecorator для вывода разделителей
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        // Улучшает быстродействие, если размер макета RecyclerView не изменяется
        recyclerView.setHasFixedSize(true);

        // Получение FloatingActionButton и настройка слушателя
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    // Отображение AddEditFragment при касании FAB
                    @Override
                    public void onClick(View view) {
                        listener.onAddContact();
                    }
                });

        return view;
    }

    // Присваивание ContactsFragmentListener при присоединении фрагмента
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ContactsFragmentListener) context;
    }

    // Удаление ContactsFragmentListener при отсоединении фрагмента
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // Инициализация Loader при создании активности этого фрагмента
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CONTACTS_LOADER, null, this);
    }

    // Вызывается из MainActivity при обновлении базы данных другим фрагментом
    public void updateContactList() {
        contactsAdapter.notifyDataSetChanged();
    }

    // Вызывается LoaderManager для создания Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Создание CursorLoader на основании аргумента id; в этом
        // фрагменте только один объект Loader, и команда switch не нужна
        switch (id) {
            case CONTACTS_LOADER:
                return new CursorLoader(getActivity(),
                        Contact.CONTENT_URI, // Uri таблицы contacts
                        null, // все столбцы
                        null, // все записи
                        null, // без аргументов
                        Contact.COLUMN_NAME + " COLLATE NOCASE ASC"); // сортировка
            default:
                return null;
        }
    }

    // Вызывается LoaderManager при завершении загрузки
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        contactsAdapter.swapCursor(data);
    }

    // Вызывается LoaderManager при сбросе Loader
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        contactsAdapter.swapCursor(null);
    }
}