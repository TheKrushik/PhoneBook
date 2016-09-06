package info.krushik.android.phonebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import info.krushik.android.phonebook.data.DatabaseHelper;

public class SignUpActivity extends AppCompatActivity {

    DatabaseHelper helper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void onSignUpClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                EditText login = (EditText) findViewById(R.id.TFlogin);
                EditText pass1 = (EditText) findViewById(R.id.TFpass1);
                EditText pass2 = (EditText) findViewById(R.id.TFpass2);

                String loginStr = login.getText().toString();
                String pass1Str = pass1.getText().toString();
                String pass2Str = pass2.getText().toString();

                if (pass1Str.equals(pass2Str)) {
                    //insert the details in database
                    User user = new User();
                    user.setLogin(loginStr);
                    user.setPassword(pass1Str);

                    helper.insertUser(user);

                    Intent intentLogin = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intentLogin);

                } else {

                    //popup msg
                    Toast.makeText(SignUpActivity.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }
}
