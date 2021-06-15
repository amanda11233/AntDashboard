package com.example.antdashboard.repo;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.antdashboard.DashboardActivity;
import com.example.antdashboard.utils.Constants;
import com.physicaloid.lib.Physicaloid;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

public class SpeechRecognitionBuilder extends ContextWrapper implements RecognitionListener {


    private SpeechRecognizer recognizer;
    private static final String TAG = "SpeechRecognitionBuilde";
    private RelativeLayout mapsLay;
    private TextToSpeech tts;
    private Physicaloid mPhysicaloid;

    public SpeechRecognitionBuilder(Context base, RelativeLayout mapsLay, TextToSpeech tts, Physicaloid physicaloid) {
        super(base);
        this.mapsLay = mapsLay;
        this.tts = tts;
        this.mPhysicaloid = physicaloid;
    }


    public static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<SpeechRecognitionBuilder> activityReference;
       public SetupTask(SpeechRecognitionBuilder activity){
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try{
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                activityReference.get().setupRecognizer(assetDir);
            }catch (IOException e){
                return e;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                Log.d("MainAc", "Failed to init recognizer" + result );
            } else {
                activityReference.get().switchSearch(Constants.KWS_SEARCH);
            }
        }
    }


    public void onDestroy(){
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }




    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(Constants.KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);

//        String caption = getResources().getString(captions.get(searchName));
//        ((TextView) findViewById(R.id.caption_text)).setText(caption);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(Constants.KWS_SEARCH, Constants.KEYPHRASE);

        // Create grammar-based search for selection between demos
        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(Constants.MENU_SEARCH, menuGrammar);

        // Create grammar-based search for digit recognition
        File digitsGrammar = new File(assetsDir, "digits.gram");
        recognizer.addGrammarSearch(Constants.DIGITS_SEARCH, digitsGrammar);


        // Create language model search
//        File languageModel = new File(assetsDir, "weather.dmp");
//        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);

        // Phonetic search
//        File phoneticModel = new File(assetsDir, "en-phone.dmp");
//        recognizer.addAllphoneSearch(PHONE_SEARCH, phoneticModel);
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(Constants.KWS_SEARCH))
            switchSearch(Constants.KWS_SEARCH);
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        if (text.equals(Constants.KEYPHRASE))
            switchSearch(Constants.MENU_SEARCH);
        else if (text.equals(Constants.DIGITS_SEARCH))
            switchSearch(Constants.DIGITS_SEARCH);
//        else if (text.equals(PHONE_SEARCH))
//            switchSearch(PHONE_SEARCH);
//        else if (text.equals(FORECAST_SEARCH))
//            switchSearch(FORECAST_SEARCH);
        else
            Log.d(TAG, "onPartialResult: " + text);
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

            switch (text){
                case Constants.MAP_OPEN:
                    mapsLay.setVisibility(View.VISIBLE);
                    tts.speak("Opening Map", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case Constants.MAP_CLOSE:
                    mapsLay.setVisibility(View.GONE);
                    tts.speak("Closing Map", TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case Constants.LIGHTS:
                    if(mPhysicaloid.isOpened()){
                        if("4".length()>0) {
                            byte[] buf = "4".getBytes();
                            mPhysicaloid.write(buf, buf.length);
                        }
                    }
                    tts.speak("HeadLights turned on!", TextToSpeech.QUEUE_FLUSH, null);
                    break;

            }
        }
    }

    @Override
    public void onError(Exception e) {
        Log.d(TAG, "onError: " + e.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(Constants.KWS_SEARCH);
    }
}
