// MainActivity.java
// Управление фрагментами приложения и обмен данными между ними
package info.krushik.android.phonebook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ContactsFragment.ContactsFragmentListener,
        DetailFragment.DetailFragmentListener, AddEditFragment.AddEditFragmentListener {

    // Ключ для сохранения Uri контакта в переданном объекте Bundle
    public static final String CONTACT_URI = "contact_uri";

    private ContactsFragment contactsFragment; // Вывод списка контактов

    // Отображает ContactsFragmentListener при первой загрузке MainActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Если макет содержит fragmentContainer, используется макет для
        // телефона; отобразить ContactsFragmentListener
        if (savedInstanceState == null & findViewById(R.id.fragmentContainer) != null) {
            // Создание ContactsFragmentListener
            contactsFragment = new ContactsFragment();

            // Добавление фрагмента в FrameLayout
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, contactsFragment);
            transaction.commit(); // Вывод ContactsFragmentListener
        } else {
            contactsFragment = (ContactsFragment) getSupportFragmentManager().
                    findFragmentById(R.id.contactsFragment);
        }
    }

    // Отображение DetailFragment для выбранного контакта
    @Override
    public void onContactSelected(Uri contactUri) {
        if (findViewById(R.id.fragmentContainer) != null) // Телефон
            displayContact(contactUri, R.id.fragmentContainer);
        else { // Планшет
            // Извлечение с вершины стека возврата
            getSupportFragmentManager().popBackStack();

            displayContact(contactUri, R.id.rightPaneContainer);
        }
    }

    // Отображение AddEditFragment для добавления нового контакта
    @Override
    public void onAddContact() {
        if (findViewById(R.id.fragmentContainer) != null) // Телефон
            displayAddEditFragment(R.id.fragmentContainer, null);
        else // Планшет
            displayAddEditFragment(R.id.rightPaneContainer, null);
    }

    // Отображение информации о контакте
    private void displayContact(Uri contactUri, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // Передача URI контакта в аргументе DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(CONTACT_URI, contactUri);
        detailFragment.setArguments(arguments);

        // Использование FragmentTransaction для отображения
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // Приводит к отображению DetailFragment
    }

    // Отображение фрагмента для добавления или изменения контакта
    private void displayAddEditFragment(int viewID, Uri contactUri) {
        AddEditFragment addEditFragment = new AddEditFragment();

        // При изменении передается аргумент contactUri
        if (contactUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(CONTACT_URI, contactUri);
            addEditFragment.setArguments(arguments);
        }

        // Использование FragmentTransaction для отображения AddEditFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // Приводит к отображению AddEditFragment
    }

    // Возвращение к списку контактов при удалении текущего контакта
    @Override
    public void onContactDeleted() {
        // Удаление с вершины стека
        getSupportFragmentManager().popBackStack();
        contactsFragment.updateContactList(); // Обновление контактов
    }

    // Отображение AddEditFragment для изменения существующего контакта
    @Override
    public void onEditContact(Uri contactUri) {
        if (findViewById(R.id.fragmentContainer) != null) // Телефон
            displayAddEditFragment(R.id.fragmentContainer, contactUri);
        else // Планшет
            displayAddEditFragment(R.id.rightPaneContainer, contactUri);
    }

    // Обновление GUI после сохранения нового или существующего контакта
    @Override
    public void onAddEditCompleted(Uri contactUri) {
        // Удаление вершины стека возврата
        getSupportFragmentManager().popBackStack();
        contactsFragment.updateContactList(); // Обновление контактов

        if (findViewById(R.id.fragmentContainer) == null) { // Планшет
            // Удаление с вершины стека возврата
            getSupportFragmentManager().popBackStack();

            // На планшете выводится добавленный или измененный контакт
            displayContact(contactUri, R.id.rightPaneContainer);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionAbout:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

