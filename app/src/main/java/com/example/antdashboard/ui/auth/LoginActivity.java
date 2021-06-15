package com.example.antdashboard.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.antdashboard.DashboardActivity;
import com.example.antdashboard.R;
import com.example.antdashboard.models.User;
import com.example.antdashboard.presenters.auth.LoginPresenter;
import com.example.antdashboard.utils.Constants;
import com.example.antdashboard.utils.Validator;
import com.example.antdashboard.views.AuthContract;

public class LoginActivity extends AppCompatActivity implements AuthContract.View{


    private LoginPresenter presenter;
    private EditText emailtxt, passwordtxt;
    private Button loginbtn;
    private SharedPreferences sharedPreferences;
    private String token;
    private TextView linktxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewInit();
    }

    private void viewInit(){
        emailtxt = findViewById(R.id.emailtxt);
        passwordtxt = findViewById(R.id.passwordtxt);
        loginbtn = findViewById(R.id.loginbtn);
        linktxt = findViewById(R.id.linktxt);

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        presenter = new LoginPresenter(this, sharedPreferences);

        token = sharedPreferences.getString(Constants.TOKEN, null);

        if(token != null){
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }else{

        }

        linktxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyForm();
            }
        });
    }

    private void verifyForm(){
        String email = emailtxt.getText().toString();
        String password = passwordtxt.getText().toString();


        int err = 0;

        if(!Validator.validateEmail(email)){
            err++;
            emailtxt.setError("Enter valid email!");
            emailtxt.requestFocus();
        }

        if(!Validator.validateFields(password)){
            err++;
            passwordtxt.setError("Enter valid password");
            passwordtxt.requestFocus();
        }

        if(err == 0){
            login(email, password);
        }
    }


    private void login(String email, String password){
        presenter.start(new User(email, password));
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}