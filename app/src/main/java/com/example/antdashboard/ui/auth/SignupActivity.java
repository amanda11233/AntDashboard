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
import com.example.antdashboard.presenters.auth.SignupPresenter;
import com.example.antdashboard.utils.Constants;
import com.example.antdashboard.utils.Validator;
import com.example.antdashboard.views.AuthContract;

public class SignupActivity extends AppCompatActivity implements  AuthContract.View {
    private Button signupbtn;
    private EditText fullnametxt, contacttxt, agetxt, emailtxt, passwordtxt, addresstxt, emContacttxt;
    private SignupPresenter presenter;
    private SharedPreferences sharedPreferences;
    private String token;
    private TextView linktxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        viewInit();
    }

    private void viewInit(){
        fullnametxt = findViewById(R.id.fullnametxt);
        contacttxt = findViewById(R.id.contacttxt);
        agetxt = findViewById(R.id.agetxt);
        emailtxt = findViewById(R.id.emailtxt);
        addresstxt = findViewById(R.id.addresstxt);
        emContacttxt  = findViewById(R.id.emContacttxt);
        passwordtxt = findViewById(R.id.passwordtxt);
        signupbtn = findViewById(R.id.signupbtn);
        linktxt = findViewById(R.id.linktxt);


        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        presenter = new SignupPresenter(this, sharedPreferences);

        token = sharedPreferences.getString(Constants.TOKEN, null);

        if(token != null){
            startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
        }else{

        }

        linktxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyForm();
            }
        });
    }


    private void verifyForm(){
        String fullname = fullnametxt.getText().toString();
        String contact = contacttxt.getText().toString();
        String age = agetxt.getText().toString();
        String emContact = emContacttxt.getText().toString();
        String address = addresstxt.getText().toString();
        String email = emailtxt.getText().toString();
        String password = passwordtxt.getText().toString();

        int err = 0;

        if(!Validator.validateFields(fullname)){
            err++;
            fullnametxt.setError("Enter Valid Fullname");
            fullnametxt.requestFocus();
        }

        if(!Validator.validateFields(contact)){
            err++;
            contacttxt.setError("Enter Valid Contact");
            contacttxt.requestFocus();
        }
        if(!Validator.validateFields(age)){
            err++;
            agetxt.setError("Enter Valid Age");
            agetxt.requestFocus();
        }

        if(!Validator.validateFields(emContact)){
            err++;
            emContacttxt.setError("Enter Valid Contact");
            emContacttxt.requestFocus();
        }

        if(!Validator.validateFields(address)){
            err++;
            addresstxt.setError("Enter Valid Address");
            addresstxt.requestFocus();
        }

        if(!Validator.validateEmail(email)){
            err++;
            emailtxt.setError("Enter Valid Email");
            emailtxt.requestFocus();
        }

        if(!Validator.validatePassword(password, password)){
            err++;
            passwordtxt.setError("Enter Valid Password");
            passwordtxt.requestFocus();
        }

        if(err == 0){
            signup(fullname, email, contact, emContact, address, password, age );
        }


    }


    private void signup(String fullname, String email, String contact, String emContact, String address, String password, String age){
        presenter.register(new User(fullname, email, contact, emContact, address, password, age));
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "User Registered!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
    }

    @Override
    public void onFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}