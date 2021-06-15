package com.example.antdashboard.repo;

import android.content.Context;
import android.content.ContextWrapper;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TTSBuilder extends ContextWrapper {
    private static TextToSpeech textToSpeech;

    public TTSBuilder(Context base) {
        super(base);
        ttsInit();
    }


    public TextToSpeech ttsInit(){
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        return textToSpeech;
    }
}
