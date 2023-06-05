package com.vladigg.bgwords;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;


@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    private ArrayList<String> dictionary;

    private AdView mAdView;

    TextView word, minNum, maxNum;
    Button newWord;
    SeekBar seekBarMin, seekBarMax;
    Integer min, max;
    Switch capsSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Думички");

        MobileAds.initialize(this, initializationStatus -> {
        });

        RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                .toBuilder()
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_T)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        word = findViewById(R.id.word);
        minNum = findViewById(R.id.minNum);
        maxNum = findViewById(R.id.maxNum);
        newWord = findViewById(R.id.newWord);
        seekBarMin = findViewById(R.id.minSeek);
        seekBarMax = findViewById(R.id.maxSeek);
        capsSwitch = findViewById(R.id.capsSwitch);

        //seekBarMin.setMin(1);
        seekBarMin.setMax(15);
        //seekBarMax.setMin(1);
        seekBarMax.setMax(15);

        seekBarMin.setProgress(1);
        seekBarMax.setProgress(15);
        minNum.setText("1");
        maxNum.setText("15");
        min = 1;
        max = 15;



        seekBarMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                minNum.setText(String.valueOf(progress));
                min = progress;
                if (progress > max) {
                    seekBarMin.setProgress(max);
                    minNum.setText(String.valueOf(max));
                    min = max;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                maxNum.setText(String.valueOf(progress));
                max = progress;
                if (progress < min) {
                    seekBarMax.setProgress(min);
                    maxNum.setText(String.valueOf(min));
                    max = min;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });





        newWord.callOnClick();
    }




    private void createDictionary(int min, int max){
        dictionary = new ArrayList<String>();

        BufferedReader dict = null; //Holds the dictionary file
        AssetManager am = this.getAssets();

        try {
            //dictionary.txt should be in the assets folder.
            dict = new BufferedReader(new InputStreamReader(am.open("dict2.txt")));

            String word;
            while((word = dict.readLine()) != null){
                if(word.length() >= min && word.length() <= max){
                    dictionary.add(word);
                }
            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            dict.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //Precondition: the dictionary has been created.
    private String getRandomWord(){
        return dictionary.get((int)(Math.random() * dictionary.size()));
    }

    public void newWord(View view) {
        int[] androidColors = getResources().getIntArray(R.array.androidcolors);
        if (max == 0 || max < min) {
            return;
        }
        createDictionary(min, max);
        String newWord = getRandomWord();
        String newW = "";
        for (int i = 0; i <= newWord.length() - 1; i++) {
            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            newW += "<font color="+randomAndroidColor+">" + String.valueOf(newWord.charAt(i)) + "</font>";
        }

        if (capsSwitch.isChecked()) {
            word.setAllCaps(true);
        } else {
            word.setAllCaps(false);
        }
        word.setText(Html.fromHtml(newW));
        String url = "https://rechnik.chitanka.info/w/" + newWord;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });



    }
}