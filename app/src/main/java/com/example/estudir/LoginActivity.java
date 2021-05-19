package com.example.estudir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText login;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.editTextTextPersonName);
        password = findViewById(R.id.editTextTextPassword);

    }

    public void loginApp(View v){
        String credLogin = getResources().getString(R.string.admin_login);
        String credPass = getResources().getString(R.string.admin_pass);
        if(login.getText().toString().equals(credLogin) && password.getText().toString().equals(credPass)){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }else
        {
            Toast toast = Toast.makeText(this,"Senha inválida",Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}