
package com.example.antdashboard;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.antdashboard.helpers.PermissionHelper;
import com.example.antdashboard.helpers.SnackbarHelper;
import com.example.antdashboard.models.Activity;
import com.example.antdashboard.observables.GeofenceObserver;
import com.example.antdashboard.presenters.ActivityPresenter;
import com.example.antdashboard.providers.LocationProvider;
import com.example.antdashboard.repo.GoogleMapsBuilder;
import com.example.antdashboard.repo.OnSwipeTouchListener;
import com.example.antdashboard.repo.SpeechRecognitionBuilder;
import com.example.antdashboard.repo.TTSBuilder;
import com.example.antdashboard.utils.Constants;
import com.example.antdashboard.utils.Tools;
import com.example.antdashboard.views.ActivityContract;

import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;
import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import eo.view.batterymeter.BatteryMeterView;
import me.ibrahimsn.lib.Speedometer;

import static android.widget.Toast.makeText;

public class DashboardActivity extends AppCompatActivity implements ActivityContract.View, View.OnClickListener,  Observer {


    private static final String TAG = "b";
    private RelativeLayout speedLay;
    private ImageButton fwdbtn, revbtn, open_map, uphill, settings;
    private Button close_map;
    private TextView connecttxt;
    private static Physicaloid mPhysicaloid;
    private ArchitectView arview;
    private TextToSpeech textToSpeech;


    private boolean forwardFlag = true;
    private MediaPlayer mpIntro;
    private MediaPlayer mpAcc;
    private AlertDialog alertDialog;

    private Handler mHandler = new Handler();

    private CircularProgressButton switchBtn;
    private String value;

    private Speedometer speedometer;
    private TickerView distView;

    private TextView chargeTxt;
    private BatteryMeterView batteryView;
    private String data2 = "";
    private String speed = "";
    private String distance = "";
    private String currentDistance = "";
    private boolean randomCheck = true;
    private String revValue = "";
    private String battery = "";

    private LinearLayout.LayoutParams params;
    private RelativeLayout mapslay;

    //google map object
    private MediaPlayer mpBad;

    private LocationProvider locationProvider;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SnackbarHelper snackbarHelper;

    private LottieAnimationView upAnim, downAnim, rightAnim, leftAnim;
    private DatabaseReference myRef;
    private DatabaseReference batteryRef;
    private Date currentTime;
    private ActivityPresenter presenter;
    private String token;
    private ImageButton lightsbtn;
    private ImageButton profilebtn;
    private ImageButton lightsoffbtn;
    private long startMillis = System.currentTimeMillis();
    private List<Integer> speedArr;
    private SpeechRecognitionBuilder speechRecognitionBuilder;
    private GoogleMapsBuilder googleMapsBuilder;
    private TTSBuilder tts;
    private DigitSpeedView digitalSpeed;
    private SharedPreferences mSharedPreference;
    private boolean revvSoundStatus;
    private boolean carStartFlag = false;
    private boolean speedThreshFlag = false;
    private int speedThreshhold = 60;
    private int mode = Constants.NORMAL;
    private Dialog dialog;
    private Button okBtn;

    private boolean chargingFlag = false;
    private boolean usbConnectFlag = false;
    private ImageView carimg;
    private BufferedReader input;
    private Handler handler2 = new Handler();

    private ImageView cameraview;
    private Thread camThread;
    private boolean collisionFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        
        //Requesting Required Permissions
        permissionRequests();
        //Initializing View
        viewInit();
        camThread = new Thread(new ReverseCameraThread());
        camThread.start();

        BroadcastReceiver detachReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){

                    carStop();

                    usbConnectFlag = false;
                    connecttxt.setText("Not Connected");
                    if(alertDialog != null){
                        if(!alertDialog.isShowing()){
                            alertDialog.show();
                        }
                    }

                    if(mPhysicaloid.isOpened()){
                        mPhysicaloid.close();
                        mPhysicaloid.clearReadListener();
                    }


                }else if(intent.getAction().equals(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)){
                   if(alertDialog != null){
                       alertDialog.dismiss();
                   }
                    usbConnectFlag = true;

                }

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction("android.hardware.usb.action.USB_STATE");
        registerReceiver(detachReceiver, filter);

    }

    private void permissionRequests(){
        if(!PermissionHelper.hasCameraPermission(this)){
            if(PermissionHelper.shouldShowRequestPermissionRationale(this, PermissionHelper.CAMERA_PERMISSION)){
                PermissionHelper.requestCameraPermission(this);
            }else{
                PermissionHelper.requestCameraPermission(this);
            }
        }
       if(!PermissionHelper.hasLocationPermission(this)){
            if(PermissionHelper.shouldShowRequestPermissionRationale(this, PermissionHelper.FINE_LOCATION_PERMISSION)){
                PermissionHelper.requestLocationPermission(this);
            }else{
                PermissionHelper.requestLocationPermission(this);
            }
        }else{
        }
        if(!PermissionHelper.hasAudioPermission(this)){
            if(PermissionHelper.shouldShowRequestPermissionRationale(this, PermissionHelper.AUDIO_PERMISSION)){
                PermissionHelper.requestLocationPermission(this);
            }else{
                PermissionHelper.requestLocationPermission(this);
            }
        }else{
        }
        if(!PermissionHelper.hasStoragePermission(this)){
            if(PermissionHelper.shouldShowRequestPermissionRationale(this, PermissionHelper.EXTERNAL_STORAGE_PERMISSION)){
                PermissionHelper.requestStoragePermission(this);
            }else{
                PermissionHelper.requestStoragePermission(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PermissionHelper.FINE_LOCATION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }else if(requestCode == PermissionHelper.PERMISSIONS_REQUEST_RECORD_AUDIO){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                new SpeechRecognitionBuilder.SetupTask(speechRecognitionBuilder).execute();
            }else{

            }
        }else if (requestCode == PermissionHelper.PERMISSIONS_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mediaInit();
                revMediaInit();
            } else {
            }
        }else if(requestCode == PermissionHelper.PERMISSIONS_REQUEST_CAMERA){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                creatARView();
            }else{
            }
        }else if (requestCode == PermissionHelper.BACKGROUND_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                makeText(this, "Geofence Initalized!", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mediaInit(){
        try {
            AssetFileDescriptor afd = getAssets().openFd("start.mp3");
            mpIntro = new MediaPlayer();
            mpIntro.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            afd.close();
            mpIntro.prepare();
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class RevThread extends  Thread{
        @Override
        public void run() {
            try{
                AssetFileDescriptor afd2 = getAssets().openFd("rev.mp3");
                mpAcc = new MediaPlayer();
                mpAcc.setDataSource(afd2.getFileDescriptor(), afd2.getStartOffset(), afd2.getLength());
                afd2.close();
                mpAcc.prepare();
                mpAcc.setLooping(true);
                mpAcc.setVolume(0f, 0f);
              if(revvSoundStatus){
                  mpAcc.start();
              }else{
                  mpAcc.stop();
              }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    public class ReverseCameraThread implements Runnable{

        @Override
        public void run() {
            Socket socket;
            String imgString = null;
            String collision = null;
            try{
                while (true){
                    socket = new Socket(Constants.CAM_IP, Constants.CAM_PORT);
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    DataInputStream in = new DataInputStream(socket.getInputStream());
//                    PrintWriter output = new PrintWriter(socket.getOutputStream());

                       String message = input.readLine();

                       String[] splitmsg = message.split("#");

                       if(splitmsg.length > 1){
                           imgString = splitmsg[0];
                            collision = splitmsg[1];
                       }

                if(imgString != null){
                    byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    cameraview.setImageBitmap(decodedByte);

                  if(!forwardFlag){
                      if(collision.equals("True")){
                          if(!collisionFlag){
                              Log.d(TAG, "run: " + collisionFlag);

                              runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      textToSpeech.speak("Collision Warning! 1.5 meters", TextToSpeech.QUEUE_FLUSH, null);
                                  }
                              });


                              collisionFlag = true;
                          }

                      }else if(collision.equals("False")){
                          if(collisionFlag){
                              collisionFlag = false;
                          }
                      }
                  }
                }




                }
            }catch(Exception e){
                Log.d(TAG, "run: " + e.getLocalizedMessage());
                e.printStackTrace();

        }
        }

    }

    public class Thread2 implements Runnable{

        @Override
        public void run() {

        }
    }

    private void revMediaInit(){
        new RevThread().start();
    }

    @Override
    public void onDestroy() {
        speechRecognitionBuilder.onDestroy();
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if(mpAcc != null){
            mpAcc.stop();
        }
        if(mPhysicaloid.isOpened()){
            mPhysicaloid.close();
            mPhysicaloid.clearReadListener();
        }
        arview.onDestroy();
        super.onDestroy();
    }

    private double getElapsedTimeInSeconds(){
        return (System.currentTimeMillis() - this.startMillis) / 1000.0;
    }

    @Override
    public void onPause(){
//        if(textToSpeech !=null){
//            textToSpeech.stop();
//            textToSpeech.shutdown();
//        }

//        if(mPhysicaloid.close()){
//            mPhysicaloid.clearReadListener();
//        }


        arview.onPause();
        // pause location updates
        if(locationProvider != null){
            locationProvider.onPause();

        }
        if(mpAcc != null){
            if(switchBtn.getText().toString().equals("Turn off")){
               mpAcc.stop();
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mPhysicaloid.isOpened()) {
            dockingConnection();
        }
        textToSpeech = tts.ttsInit();
        arview.onResume();
        // start location updates
        if(locationProvider != null){
            locationProvider.onResume();
        }
        mSharedPreference = getSharedPreferences("Settings", MODE_PRIVATE);
        revvSoundStatus = mSharedPreference.getBoolean(Constants.REVV_SOUND, false);
        mode = mSharedPreference.getInt(Constants.DRIVING_MODE, Constants.NORMAL);

        if(revvSoundStatus){
            if(switchBtn.getText().toString().equals("Turn off")){
                revMediaInit();
            }else{

            }
        }else{
            if(mpAcc != null){
                mpAcc.stop();
            }
        }
        token = sharedPreferences.getString(Constants.TOKEN, null);
    }

    private void viewInit(){
         speedAlert();
         snackbarHelper = new SnackbarHelper();

         speedLay = findViewById(R.id.speedlay);
         connecttxt = findViewById(R.id.connecttxt);
         switchBtn = findViewById(R.id.switchbtn);
         speedometer = findViewById(R.id.speedometer);
         params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
         distView = findViewById(R.id.tickerView);
         fwdbtn = findViewById(R.id.fwdbtn);
         revbtn = findViewById(R.id.revbtn);
         mapslay = findViewById(R.id.mapslay);
         lightsbtn = findViewById(R.id.lightsbtn);
         profilebtn = findViewById(R.id.profile);
         cameraview = findViewById(R.id.cameraview);


         arview = findViewById(R.id.arview);
         batteryView = findViewById(R.id.batteryview);
         batteryView.setCharging(false);
         chargeTxt = findViewById(R.id.chargeText);
         open_map = findViewById(R.id.open_map);
        close_map = findViewById(R.id.close_map);

        upAnim = findViewById(R.id.upAnim);
        downAnim = findViewById(R.id.downAnim);
        rightAnim = findViewById(R.id.rightAnim);
        leftAnim = findViewById(R.id.leftAnim);
        uphill = findViewById(R.id.uphill);
        settings = findViewById(R.id.settings);
        digitalSpeed = findViewById(R.id.speedview);
        carimg = findViewById(R.id.carimg);


         sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
         mSharedPreference = getSharedPreferences("Settings", MODE_PRIVATE);

         revvSoundStatus = mSharedPreference.getBoolean(Constants.REVV_SOUND, false);
         mode = mSharedPreference.getInt(Constants.DRIVING_MODE, Constants.NORMAL);

         editor = sharedPreferences.edit();
         token = sharedPreferences.getString(Constants.TOKEN, null);

         lightsoffbtn = findViewById(R.id.lightsoffbtn);

         fwdbtn.setVisibility(View.GONE);
         revbtn.setVisibility(View.GONE);
         open_map.setVisibility(View.GONE);
         upAnim.setVisibility(View.GONE);
         downAnim.setVisibility(View.GONE);
        rightAnim.setVisibility(View.GONE);
        leftAnim.setVisibility(View.GONE);
        uphill.setVisibility(View.GONE);
        settings.setVisibility(View.VISIBLE);

         presenter = new ActivityPresenter(this);

         distView.setCharacterLists(TickerUtils.provideNumberList());
         distView.setAnimationDuration(500);
         distView.setAnimationInterpolator(new OvershootInterpolator());
         distView.setPreferredScrollingDirection(TickerView.ScrollingDirection.ANY);

         mPhysicaloid = new Physicaloid(this);
         mPhysicaloid.setBaudrate(9600);

         switchBtn.setOnClickListener(this);
         fwdbtn.setOnClickListener(this);
         revbtn.setOnClickListener(this);
         lightsbtn.setOnClickListener(this);
         profilebtn.setOnClickListener(this);
         lightsoffbtn.setOnClickListener(this);
         open_map.setOnClickListener(this);
         close_map.setOnClickListener(this);
         uphill.setOnClickListener(this);
         settings.setOnClickListener(this);


         lightsbtn.setVisibility(View.GONE);
         lightsoffbtn.setVisibility(View.GONE);

         speedArr = new ArrayList<>();


          tts= new TTSBuilder(this);
         textToSpeech = tts.ttsInit();

        creatARView();

        ToggleSwitch toggle = findViewById(R.id.modeswitch);

        toggle.setCheckedPosition(0);

        toggle.setOnChangeListener(new ToggleSwitch.OnChangeListener() {
            @Override
            public void onToggleSwitchChanged(int i) {

                switch(i+""){
                    case  "0":
                        editor.putInt(Constants.DRIVING_MODE, Constants.SLOW);
                        break;
                    case "1":
                        editor.putInt(Constants.DRIVING_MODE, Constants.NORMAL);
                        break;

                }
            }
        });


        speechRecognitionBuilder = new SpeechRecognitionBuilder(this, mapslay, textToSpeech, mPhysicaloid);
        googleMapsBuilder = new GoogleMapsBuilder(this, DashboardActivity.this, snackbarHelper);
        googleMapsBuilder.mapInit();
        firebaseHandler();
        mediaInit();

        dockingConnection();
        locationInit();
        new SpeechRecognitionBuilder.SetupTask(speechRecognitionBuilder).execute();
        GeofenceObserver.getInstance().addObserver(this);

        carInit();

        dialogCreate();

//      batteryRef.setValue(10);
    }

    private void carInit(){
        carimg.setOnTouchListener(new OnSwipeTouchListener(DashboardActivity.this){
            @Override
            public void onSwipeRight() {
                textToSpeech.speak("Going Right", TextToSpeech.QUEUE_FLUSH, null);
                rightAnim.setVisibility(View.VISIBLE);
                leftAnim.setVisibility(View.GONE);
                onClickWrite(null, "r");
            }

            @Override
            public void onSwipeLeft() {
                textToSpeech.speak("Going Left", TextToSpeech.QUEUE_FLUSH, null);
                leftAnim.setVisibility(View.VISIBLE);
                rightAnim.setVisibility(View.GONE);
                onClickWrite(null, "7");
            }

            @Override
            public void onSwipeTop() {
                if(forwardFlag){
                    textToSpeech.speak("Going Straight", TextToSpeech.QUEUE_FLUSH, null);
                    leftAnim.setVisibility(View.GONE);
                    rightAnim.setVisibility(View.GONE);
                    onClickWrite(null, "q");

                }else{

                }

            }

            @Override
            public void onSwipeBottom() {
                if(!forwardFlag){
                    textToSpeech.speak("Going Back", TextToSpeech.QUEUE_FLUSH, null);
                    leftAnim.setVisibility(View.GONE);
                    rightAnim.setVisibility(View.GONE);
                    onClickWrite(null, "q");
                }else{

                }
            }
        });
    }

    private void locationInit(){
        locationProvider = new LocationProvider(this, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location!=null && DashboardActivity.this.arview != null ) {
                    // check if location has altitude at certain accuracy level & call right architect method (the one with altitude information)
                    if ( location.hasAltitude() && location.hasAccuracy() && location.getAccuracy()<7) {
                        DashboardActivity.this.arview.setLocation( 27.706209462165667, 85.33003552829794, location.getAltitude(), location.getAccuracy());

//                        DashboardActivity.this.arview.setLocation( location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy() );
                    } else {
                        DashboardActivity.this.arview.setLocation( 27.706209462165667, 85.33003552829794, location.hasAccuracy() ? location.getAccuracy() : 1000 );
//                        DashboardActivity.this.arview.setLocation( location.getLatitude(), location.getLongitude(), location.hasAccuracy() ? location.getAccuracy() : 1000 );
                    }
//                    makeText(DashboardActivity.this, location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override public void onProviderEnabled(String s) {}
            @Override public void onProviderDisabled(String s) {}
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        arview.onPostCreate();
        arview.setVisibility(View.GONE);
//        try{
//            arview.load("index.html");
//        }catch(Exception e){
//            e.printStackTrace();
//        }
    }


    private void creatARView(){
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey(Constants.API_KEY);
        arview.onCreate( config );
    }

    private void changeState(String state){
        switch (state){
            case "fwd":
                fwdbtn.setVisibility(View.GONE);
                revbtn.setVisibility(View.VISIBLE);
                upAnim.setVisibility(View.VISIBLE);
                downAnim.setVisibility(View.GONE);
                uphill.setVisibility(View.VISIBLE);
                forwardFlag = true;
                break;
            case "rev":
                fwdbtn.setVisibility(View.VISIBLE);
                revbtn.setVisibility(View.GONE);
                upAnim.setVisibility(View.GONE);
                uphill.setVisibility(View.GONE);
                downAnim.setVisibility(View.VISIBLE);
                forwardFlag = false;
                break;
            default:
                fwdbtn.setVisibility(View.GONE);
                revbtn.setVisibility(View.VISIBLE);
                upAnim.setVisibility(View.VISIBLE);
                uphill.setVisibility(View.GONE);
                downAnim.setVisibility(View.GONE);
                break;

        }
    }

    private void speedAlert(){
        dialog = new Dialog(this,R.style.Theme_AntDashboard_ModalTheme);
        dialog.setContentView(R.layout.speed_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        okBtn = dialog.findViewById(R.id.btnretry);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void firebaseHandler(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");
        batteryRef = database.getReference("battery");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                    value = dataSnapshot.getValue(String.class);
                    if(value != null){
                        if(value.equals("Start Car")){
                            carStart();
                        }else if(value.equals("Stop Car")){
                            carStop();
                        }
                    }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }



    private void dockingConnection(){
        if(mPhysicaloid.open()) {
            final String str = "";
            connecttxt.setText("Connected");
            if(alertDialog!=null){
                alertDialog.dismiss();
            }
            mPhysicaloid.addReadListener(new ReadLisener() {
                @Override
                public void onRead(int size) {

                    byte[] buf = new byte[size];
                    mPhysicaloid.read(buf);

                    String data = new String(buf, StandardCharsets.UTF_8);
                    data2 += data;

                    try{
                        if(!data2.isEmpty()){
                            String[] split = data2.split("#");
                            if(split.length > 0){
                                String[] splitValues = split[split.length - 1].split(",");
                                if(splitValues.length > 6){
                                    speed = splitValues[0];
                                    if(speed != null || speed != "" || !speed.isEmpty()){
                                        changeSpeed(speed);
                                    }
                                    distance = splitValues[1];
                                    if(distance != null || distance != "" || !distance.isEmpty()){
//                                        currentdistance = distance;
                                        changeDistance(distance);
                                    }
                                    battery = splitValues[2];
                                    String chargeStatus = splitValues[3];
                                    if(battery != null ||battery != "" || !battery.isEmpty()){

                                        checkBattery(Tools.convertToInt(battery), chargeStatus);
                                    }
                                    revValue = splitValues[4];

                                    if(revValue != null || revValue != "" || revValue.isEmpty()){
                                        generateSound(revValue);
                                    }

                                    String lrValue = splitValues[5];
                                    lrSignal(lrValue);

                                }
                            }
                            if(split.length > 10){
                                data2 = "";
                            }
                        }

                    }catch(Exception e) {
                        makeToast(e.toString());
                    }
                }
            });

        } else {
            connecttxt.setText("Not Connected");
            if(alertDialog != null){
                alertDialog.show();
            }
        }
    }

    private void lrSignal(String value){
        switch (value){
            case "1":
                leftAnim.setVisibility(View.VISIBLE);
                rightAnim.setVisibility(View.GONE);
                break;
            case "2":
                leftAnim.setVisibility(View.GONE);
                rightAnim.setVisibility(View.VISIBLE);
                break;
            case "0":
                leftAnim.setVisibility(View.GONE);
                rightAnim.setVisibility(View.GONE);
                break;
        }
    }

    private void checkBattery(int batteryVal, String chargeStatus){
        batteryRef.setValue(batteryVal);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chargeTxt.setText(batteryVal + " %");
                batteryView.setChargeLevel(batteryVal);
         if(chargeStatus.equals("1")){
             if(!chargingFlag){
                 chargingFlag  = true;
                 batteryView.setCharging(true);
             }
         }else{
             chargingFlag = false;
             batteryView.setCharging(false);
         }
            }
        });
    }

    private void generateSound(String value){
//        makeToast(value);
        String startstatus = switchBtn.getText().toString();
        int val = Tools.convertToInt(value);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mpAcc != null) {
                               if(startstatus.equals("Turn off")) {
                                        if (val > 0 && val < 10) {
                                            mpAcc.setVolume(0.1f, 0.1f);
                                        } else if (val > 10 && val < 20) {
                                            mpAcc.setVolume(0.2f, 0.2f);
                                        } else if (val > 20 && val < 30) {
                                            mpAcc.setVolume(0.3f, 0.3f);
                                        } else if (val > 30 && val < 40) {
                                            mpAcc.setVolume(0.4f, 0.4f);
                                        } else if (val > 40 && val < 50) {
                                            mpAcc.setVolume(0.5f, 0.5f);
                                        } else if (val > 50 && val < 60) {
                                            mpAcc.setVolume(0.6f, 0.6f);
                                        } else if (val > 60 && val < 70) {
                                            mpAcc.setVolume(0.7f, 0.7f);
                                        } else if (val > 70 && val < 80) {
                                            mpAcc.setVolume(0.8f, 0.8f);
                                        } else if (val > 80 && val < 90) {
                                            mpAcc.setVolume(0.9f, 0.9f);
                                        } else if (val > 90 && val < 100) {
                                            mpAcc.setVolume(1f, 1f);
                                        } else if (val == 0) {
                                            mpAcc.setVolume(0f, 0f);
                                        }
                               }else{
                                   mpAcc.setVolume(0f, 0f);
                               }
                }
            }
        });
    }

    public void onClickClose(View v) {
        if(mPhysicaloid.close()) {
            mPhysicaloid.clearReadListener();
        }
    }


    public void onClickWrite(View v, String value) {
            Log.d(TAG, "onClickWrite: " +  value);
        if(value.length()>0) {
            byte[] buf = value.getBytes();
            mPhysicaloid.write(buf, buf.length);
        }
    }

    private void makeToast(String text) {

        final String ftext = text;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                makeText(DashboardActivity.this, ftext, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void changeDistance(String distance){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(!distance.isEmpty() || distance != null || distance != ""){
                        distView.setText(distance);
//                        editor.putString(Constants.DISTANCE, distance);
//                        editor.apply();
                    }else{
                    }
                }catch(Exception e){
                    makeText(DashboardActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetTimer(){
        this.startMillis = System.currentTimeMillis();
    }

    private void changeSpeed(String speed){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(getElapsedTimeInSeconds() >= 60){
                        if(Tools.convertToInt(speed) > 0){
                            speedArr.add(Tools.convertToInt(speed));
                        }
                        resetTimer();
                    }
                    if(Tools.convertToInt(speed) > speedThreshhold){
                        if(!speedThreshFlag){
                            speedThreshFlag = true;
                            dialog.show();
                        }
                    }else{
                     if(speedThreshFlag){
                         speedThreshFlag = false;
                         dialog.dismiss();
                     }
                    }

                    if(!speed.isEmpty() || speed != null || speed != ""){
                        if(Integer.parseInt(speed.trim()) > 0){
                            digitalSpeed.updateSpeed(Tools.convertToInt(speed));
//                            speedometer.setSpeed(10, 500L, null);
                        }else{
//                            speedometer.setSpeed(0, 500L, null);
                            digitalSpeed.updateSpeed(0);
                        }
                    }else{
                        digitalSpeed.updateSpeed(0);
                        speedometer.setSpeed(0, 500L, null);
                    }
                }catch(Exception e){
//               makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void dialogCreate(){
        alertDialog =  new AlertDialog.Builder(this)
                .setTitle("USB Not Connected!")
                .setMessage("Do you want to retry?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(
                        getResources().getString(R.string.positive),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent mStartActivity = new Intent(getApplicationContext(), DashboardActivity.class);
                                int mPendingIntentId = 123456;
                                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() +50, mPendingIntent);
                                System.exit(0);

                            }
                        })
                .setNegativeButton(
                        getResources().getString(R.string.negative),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        }).create();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    private void  carStart(){
        myRef.setValue("Start Car");
        currentTime = Calendar.getInstance().getTime();
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        if(token != null){
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            presenter.addActivity(token, new Activity("Ant Three Wheeler", time, date ));
            makeText(DashboardActivity.this, "Activity Tracking Started", Toast.LENGTH_SHORT).show();
        }else{
//            snackbarHelper.showMessageWithDismiss(this, "Login to keep track of your activities!");
        }

        onClickWrite(null, "1");
        onClickWrite(null, mode + "");

        Log.d(TAG, "onDataChange: " + value);
//        t1.speak("Your car has been started", TextToSpeech.QUEUE_FLUSH, null);
        mpIntro.start();

        new android.os.Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    public void run() {
//                      mpBad.start();
                        revMediaInit();
                    }
                },
                1000);

        switchBtn.setText("Turn off");
        revbtn.setVisibility(View.VISIBLE);
        fwdbtn.setVisibility(View.GONE);
        lightsoffbtn.setVisibility(View.VISIBLE);
        lightsbtn.setVisibility(View.GONE);
        open_map.setVisibility(View.VISIBLE);
        upAnim.setVisibility(View.VISIBLE);
        uphill.setVisibility(View.VISIBLE);
    }

    private void carStop(){
        myRef.setValue("");

        if(mpAcc != null){
            if(mpAcc.isPlaying()){
                mpAcc.stop();
            }
        }
        currentTime = Calendar.getInstance().getTime();
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        if(token != null){
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            presenter.addActivity(token, new Activity( time, Tools.calculatingAverageSpeed(speedArr), Tools.convertToInt(distance), date ));
        }else{
//            snackbarHelper.showMessageWithDismiss(this, "Login to keep track of your activities!");
        }
//        mpBad.pause();

        Log.d(TAG, "onDataChange: " + value);
        onClickWrite(null, "0");
        switchBtn.setText("Turn on");
        textToSpeech.speak("Shutting down", TextToSpeech.QUEUE_FLUSH, null);

        revbtn.setVisibility(View.GONE);
        fwdbtn.setVisibility(View.GONE);
        lightsbtn.setVisibility(View.GONE);
        lightsoffbtn.setVisibility(View.GONE);
        open_map.setVisibility(View.GONE);
        upAnim.setVisibility(View.GONE);
        downAnim.setVisibility(View.GONE);
        rightAnim.setVisibility(View.GONE);
        leftAnim.setVisibility(View.GONE);
        uphill.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switchbtn:
                if(switchBtn.getText().toString().equals("Turn on")){ carStart(); }else if(switchBtn.getText().toString().equals("Turn off")){carStop();}
                break;
            case R.id.fwdbtn:
                onClickWrite(v, "2");
                changeState("fwd");
                cameraview.setVisibility(View.GONE);
                textToSpeech.speak("Going Forward", TextToSpeech.QUEUE_FLUSH, null);

                break;
            case R.id.revbtn:
                onClickWrite(v, "3");
                changeState("rev");
                textToSpeech.speak("Reverse mode on", TextToSpeech.QUEUE_FLUSH, null);
                cameraview.setVisibility(View.VISIBLE);
                cameraview.setAlpha(0.f);
                cameraview.setScaleX(0.f);
                cameraview.setScaleY(0.f);
                cameraview.animate()
                        .alpha(1.f)
                        .scaleX(1.f).scaleY(1.f)
                        .setDuration(300)
                        .start();
                break;
            case R.id.lightsoffbtn:
                onClickWrite(v, "4");
                lightsoffbtn.setVisibility(View.GONE);
                lightsbtn.setVisibility(View.VISIBLE);
                textToSpeech.speak("Head lights turned on", TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.lightsbtn:
                onClickWrite(v, "5");
                lightsbtn.setVisibility(View.GONE);
                lightsoffbtn.setVisibility(View.VISIBLE);
                textToSpeech.speak("Head lights turned off", TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.profile:
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                break;
            case R.id.open_map:
                mapslay.setVisibility(View.VISIBLE);
                textToSpeech.speak("Opening Map", TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.close_map:
                mapslay.setVisibility(View.GONE);
                textToSpeech.speak("Closing Map", TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.settings:
                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
                break;
            case R.id.uphill:
                textToSpeech.speak("Uphill mode!", TextToSpeech.QUEUE_FLUSH, null);
                onClickWrite(v, "6");
                break;
        }
    }


    @Override
    public void update(Observable o, Object value) {
        switch (value.toString()){
            case "1":
                makeText(DashboardActivity.this, "Softwarica College Nearby!", Toast.LENGTH_SHORT).show();
                textToSpeech.speak("Softwarica College is nearby!", TextToSpeech.QUEUE_FLUSH, null);
                arview.setVisibility(View.VISIBLE);
                break;
            case "2":
                makeText(DashboardActivity.this, "AR Created!", Toast.LENGTH_SHORT).show();
                try {
                    arview.load( "index.html" );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "3":
                makeText(DashboardActivity.this, "Exiting Location", Toast.LENGTH_SHORT).show();
                arview.setVisibility(View.GONE);
                arview.clearCache();
        }
    }

    @Override
    public void onSuccess(String message) {
        makeText(DashboardActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(String message) {
        makeText(DashboardActivity.this, message, Toast.LENGTH_SHORT).show();
    }




}