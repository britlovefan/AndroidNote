package com.example.qianwang.realmpractice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qianwang.realmpractice.model.GeoLocation;
import com.example.qianwang.realmpractice.model.Photo;
import com.google.android.gms.maps.model.LatLng;

import junit.framework.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by qianwang on 6/28/16.
 */
public class ShowTimeline extends AppCompatActivity {
    private SeekBar seekBar;
    private TextView daysDisplay;
    private TextView LocationDisplay;
    private Button backToMenu;
    private RealmResults<Photo> results;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.time_map);
        InitializeView();
        final RealmConfiguration config = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();
        results = realm.where(Photo.class).findAll();
        results = results.sort("timeStamp");
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowTimeline.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //back to the home activity
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressVal, boolean fromUser) {
                progress = progressVal;
                //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int length = results.size();
                int index = (progress * (length - 1)) / seekBar.getMax();
                String name = results.get(index).getId();
                long dateTime = results.get(index).getTimeStamp();
                String zipCode = results.get(index).getZipCode();
                String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(dateTime));
                setText(dateString, zipCode);
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/" + name;
                File imgFile = new File(filePath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ImageView myImage = (ImageView) findViewById(R.id.imageView);
                    myImage.setImageBitmap(myBitmap);
                }
                //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
    protected void InitializeView() {
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        daysDisplay = (TextView) findViewById(R.id.days);
        LocationDisplay = (TextView) findViewById(R.id.address);
        backToMenu = (Button) findViewById(R.id.backToMain);
    }

    // show the exact date/time in the left text view and display the
    protected void setText(String dateTime, String dataString) {
        daysDisplay.setText(dateTime);
        LocationDisplay.setText(dataString);
    }
}

