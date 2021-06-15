package com.example.antdashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;

import com.example.antdashboard.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class SettingsActivity extends AppCompatActivity {


    private String[] drivingOptions = {"Slow", "Normal", "Fast"};
    private Spinner modeSpinner;
    private Switch revvingSound, themeSong;
    private boolean revvsoundStatus = false;
    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor editor;
    private Button profilebtn;
    private RecyclerView geofencelistview;
    private CircularProgressButton addGeoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSharedPreference = getSharedPreferences("Settings", MODE_PRIVATE);
        editor = mSharedPreference.edit();

        revvsoundStatus = mSharedPreference.getBoolean(Constants.REVV_SOUND, false);
        viewInit();
    }

    private void viewInit(){
//        modeSpinner = findViewById(R.id.modeSpinner);
        revvingSound = findViewById(R.id.revvswitch);
        profilebtn = findViewById(R.id.profilebtn);
        geofencelistview = findViewById(R.id.geofenceView);
        addGeoBtn = findViewById(R.id.addGeoBtn);
        


        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_list_item_1, drivingOptions);

//        modeSpinner.setAdapter(ad);

        revvingSound.setChecked(revvsoundStatus);

        revvingSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    revvsoundStatus = true;
                    editor.putBoolean(Constants.REVV_SOUND, revvsoundStatus);
                    editor.commit();
                }else{
                    revvsoundStatus = false;
                    editor.putBoolean(Constants.REVV_SOUND, revvsoundStatus);
                    editor.commit();
                }
            }
        });

//        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch(parent.getItemAtPosition(position).toString()){
//                    case  "Slow":
//                        editor.putInt(Constants.DRIVING_MODE, Constants.SLOW);
//                        break;
//                    case "Normal":
//                        editor.putInt(Constants.DRIVING_MODE, Constants.NORMAL);
//                        break;
//                    case "Fast":
//                        editor.putInt(Constants.DRIVING_MODE, Constants.FAST);
//                        break;
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
                finish();
            }
        });


    }


}