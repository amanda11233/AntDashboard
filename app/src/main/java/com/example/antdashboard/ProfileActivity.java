package com.example.antdashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.antdashboard.models.ActivityResponse;
import com.example.antdashboard.models.User;
import com.example.antdashboard.presenters.ProfilePresenter;
import com.example.antdashboard.ui.auth.LoginActivity;
import com.example.antdashboard.utils.Constants;
import com.example.antdashboard.views.ProfileContract;

public class ProfileActivity extends AppCompatActivity implements ProfileContract.View  {
    private SharedPreferences sharedPreferences;
    private String token = "";
    private TextView fullnametxt, emailtxt, contacttxt, distancetxt, speedtxt;
    private ProfilePresenter profilePresenter;
    private Button logoutbtn;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        token = sharedPreferences.getString(Constants.TOKEN, null);
        profilePresenter = new ProfilePresenter(this);
        editor = sharedPreferences.edit();
        if(token != null ){
            profilePresenter.getProfile(token);
            profilePresenter.getUserActivity(token);
        }else{
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }

        viewInit();

    }
    private void viewInit(){
        fullnametxt = findViewById(R.id.fullnametxt);
        emailtxt = findViewById(R.id.emailtxt);
        contacttxt = findViewById(R.id.contacttxt);
        distancetxt = findViewById(R.id.distancetxt);
        speedtxt = findViewById(R.id.speedtxt);

        logoutbtn = findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove(Constants.TOKEN);
                editor.commit();
                Toast.makeText(ProfileActivity.this, "Your are logged out!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    @Override
    public void onSuccess(User user) {
        fullnametxt.setText(user.getFullname());
        emailtxt.setText(user.getEmail());
        contacttxt.setText(user.getContact());
    }

    @Override
    public void getActivity(ActivityResponse activity) {
        distancetxt.setText(activity.getTotalDistance() + " KM");
        speedtxt.setText(activity.getTotalAvgSpeed() + " KM/Hr");
    }

    @Override
    public void onFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}