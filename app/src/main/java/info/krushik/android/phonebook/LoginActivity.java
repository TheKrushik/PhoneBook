package info.krushik.android.phonebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import info.krushik.android.phonebook.data.DatabaseHelper;


public class LoginActivity extends AppCompatActivity {

    DatabaseHelper helper = new DatabaseHelper(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (PrefsManager.getBoolean(this, "FIRST_LOAD", true)){
            insertAdminDefault();
            PrefsManager.setBoolean(this,"FIRST_LOAD", false);
        }

    }

    private void insertAdminDefault(){
        String login = "admin";
        String password = "123456";

        //insert the details in database
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);

        helper.insertUser(user);


    }
    
    public void onButtonClick(View v){
        switch (v.getId()) {
            case R.id.btnLogin:
                EditText a = (EditText) findViewById(R.id.login);
                String strLogin = a.getText().toString();
                EditText b = (EditText) findViewById(R.id.password);
                String pass = b.getText().toString();

                String password = helper.searchPass(strLogin);
                if(pass.equals(password)){
                    Intent intentContacts = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intentContacts);
                } else{
                    Toast.makeText(LoginActivity.this, "Username and Password don't match!", Toast.LENGTH_SHORT).show();
                }

                break;
//            case R.id.buttonSignUp:
//                Intent intentSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
//                startActivity(intentSignUp);
//                break;
        }
    }
}

