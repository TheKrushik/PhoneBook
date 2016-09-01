package info.krushik.android.phonebook;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import info.krushik.android.phonebook.data.LoginDataBaseAdapter;


public class EntranceActivity extends AppCompatActivity {

    Button mBtnSignIn, mBtnSignUp;
    LoginDataBaseAdapter loginDataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        // create a instance of SQLite Database
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();

        // Get The Refference Of Buttons
        mBtnSignIn = (Button) findViewById(R.id.buttonSignIN);
        mBtnSignUp = (Button) findViewById(R.id.buttonSignUP);

        // Set OnClick Listener on SignUp button
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /// Create Intent for SignUpActivity  abd Start The Activity
                Intent intentSignUP = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intentSignUP);
            }
        });
    }

    // Methos to handleClick Event of Sign In Button
    public void signIn(View V) {
        final Dialog dialog = new Dialog(EntranceActivity.this);
        dialog.setContentView(R.layout.view_login);
        dialog.setTitle("Login");

        // get the Refferences of views
        final EditText editTextUserName = (EditText) dialog.findViewById(R.id.editTextUserNameToLogin);
        final EditText editTextPassword = (EditText) dialog.findViewById(R.id.editTextPasswordToLogin);

        Button btnSignIn = (Button) dialog.findViewById(R.id.buttonSignIn);

        // Set On ClickListener
        btnSignIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // get The User name and Password
                String userName = editTextUserName.getText().toString();
                String password = editTextPassword.getText().toString();

                // fetch the Password form database for respective user name
                String storedPassword = loginDataBaseAdapter.getSinlgeEntry(userName);

                // check if the Stored password matches with  Password entered by user
                if (password.equals(storedPassword)) {
                    Intent intent = new Intent(EntranceActivity.this, MainActivity.class);
                    intent.putExtra(LoginDataBaseAdapter.COLUMN_USERNAME, userName);

                    startActivity(intent);
//                    Toast.makeText(getApplicationContext(), "Congrats: Login Successfull", Toast.LENGTH_LONG).show();
                    dialog.dismiss();

                } else {
                    Toast.makeText(EntranceActivity.this, "User Name or Password does not match", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        loginDataBaseAdapter.close();
    }
}
