package com.akashsoam.tellmewhereapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akashsoam.tellmewhereapp.model.CountryDataSource;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.transform.OutputKeys;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SPEAK_REQUEST = 10;
    TextView txtValue;

    Button btnVoiceIntent;
    public static CountryDataSource countryDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtValue = (TextView) findViewById(R.id.txtValue);
        btnVoiceIntent = (Button) findViewById(R.id.btnVoiceIntent);

        btnVoiceIntent.setOnClickListener(MainActivity.this);

        Hashtable<String, String> countriesAndMessages = new Hashtable<>();
        countriesAndMessages.put("America", "Welcome to America");
        countriesAndMessages.put("France", "Welcome to France");
        countriesAndMessages.put("Portugal", "Welcome to Portugal");
        countriesAndMessages.put("Canada", "Welcome to Canada");
        countriesAndMessages.put("Japan", "Welcome to Japan");
        countryDataSource = new CountryDataSource(countriesAndMessages);

        PackageManager packageManager = this.getPackageManager();
        List<ResolveInfo> listOfInformation = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (listOfInformation.size() > 0) {
            Toast.makeText(MainActivity.this, "Your device does support SPEECH RECOGNITION", Toast.LENGTH_SHORT).show();
            listenToTheUsersVoice();
        } else {
            Toast.makeText(MainActivity.this, "SPEECH RECOGNITION NOT SUPPORTED", Toast.LENGTH_SHORT).show();

        }
    }

    private void listenToTheUsersVoice() {
        Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk to me");
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
        startActivityForResult(voiceIntent, SPEAK_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEAK_REQUEST && resultCode == RESULT_OK) {
            ArrayList<String> voiceWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            float[] confidLevels = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

//            int index = 0;
//            for (String userword :
//                    voiceWords) {
//                if (confidLevels != null && index < confidLevels.length) {
//                    txtValue.setText(userword + " - " + confidLevels[index]);
//                }
//            }

            String countryMatchedWithUserWord = countryDataSource.matchWithMinimumConfidenceLevelOfUserWords(voiceWords, confidLevels);
            Intent myMapActivity = new Intent(MainActivity.this, MapsActivity.class);
            myMapActivity.putExtra(CountryDataSource.COUNTRY_KEY, countryMatchedWithUserWord);
            startActivity(myMapActivity);

        }
    }

    @Override
    public void onClick(View v) {
        listenToTheUsersVoice();
    }
}