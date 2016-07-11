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

import com.example.qianwang.realmpractice.model.Photo;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
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
        //Insert the Test
        long[] timeElapseMonth = TestSelectQuery();
        Log.v("Select Month Test", timeElapseMonth[0] + "");
        Log.v("Select Range Test", timeElapseMonth[1] + "");

        results = realm.where(Photo.class).findAll();
        Log.v("timeline total", results.size() + "");
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

    protected long[] TestSelectQuery() {
        Realm realm = Realm.getDefaultInstance();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            long[] monthRange = RandomGenerateMonth();
            RealmResults<Photo> resultsByMonth = realm.where(Photo.class).between("timeStamp", monthRange[0], monthRange[1]).findAll();
        }
        long timeElapse1 = System.currentTimeMillis() - startTime;
        //Test the speed of select query of certain range.
        long startTime2 = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            long[] dateRange = RandomGenerateRange();
            RealmResults<Photo> resultsByRange = realm.where(Photo.class).between("timeStamp", dateRange[0], dateRange[1]).findAll();
        }
        long timeElapse2 = System.currentTimeMillis() - startTime2;
        return new long[]{timeElapse1, timeElapse2};
    }

    //function that returns the range of time in millis in random month/year
    protected long[] RandomGenerateMonth() {
        SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        int year = randBetween(2015, 2016);
        int month = randBetween(0, 11);
        int hour = randBetween(7, 22);
        int min = randBetween(0, 59);
        int sec = randBetween(0, 59);
        GregorianCalendar gc1 = new GregorianCalendar(year, month, 1);
        GregorianCalendar gc2 = new GregorianCalendar(year, month, gc1.getActualMaximum(gc1.DAY_OF_MONTH));
        gc1.set(year, month, 1, hour, min, sec);
        gc2.set(year, month, gc1.getActualMaximum(gc1.DAY_OF_MONTH), hour, min, sec);
        return new long[]{gc1.getTimeInMillis(), gc2.getTimeInMillis()};
    }

    protected int randBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    //function that returns random time range
    protected long[] RandomGenerateRange() {
        SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        int year = randBetween(2015, 2016);
        int month = randBetween(0, 11);
        int month2 = randBetween(month, 11);
        int hour = randBetween(7, 22);
        int hour2 = randBetween(hour, 22);
        int min = randBetween(0, 59);
        int sec = randBetween(0, 59);
        GregorianCalendar gc1 = new GregorianCalendar(year, month, 1);
        GregorianCalendar gc2 = new GregorianCalendar(year, month, 1);
        gc1.set(year, month, randBetween(0, gc1.getActualMaximum(gc1.DAY_OF_MONTH)), hour, min, sec);
        gc2.set(year, month2, gc2.getActualMaximum(gc2.DAY_OF_MONTH), hour2, min, sec);
        return new long[]{gc1.getTimeInMillis(), gc2.getTimeInMillis()};
    }

    // function that test the query of select photo within distance to
    // center[0]-lat,center[1]-long
    protected double[] RandomPoint(float[] center, int radius) {
        Random random = new Random();
        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextFloat();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(center[1]);
        double foundLatitude = new_x + center[0];
        double foundLongitude = y + center[1];
        return new double[]{foundLatitude, foundLongitude};
    }
    // Test the query speed of finding the closest location of photo to a random location
    protected void TestLocationQuery(){
        ArrayList<LatLng> randomLocations = randomGenerate();
        
    }
    //random generate points near the locations of every point online
    protected ArrayList<LatLng> randomGenerate() {
        double lat;
        double lng;
        ArrayList<LatLng>randomLocations = new ArrayList<>();
        Random generator = new Random();
        for (int i = 0; i < results.size(); i++) {
            LatLng origin = new LatLng(results.get(i).getLatitude(),results.get(i).getLongitude());
            for (int j = 0; j < 50; j++) {
                lat = generator.nextDouble() / 3;
                lng = generator.nextDouble() / 3;
                if (generator.nextBoolean()) {
                    lat = -lat;
                }
                if (generator.nextBoolean()) {
                    lng = -lng;
                }
                randomLocations.add(new LatLng(origin.latitude + lat, origin.longitude + lng));
            }
        }
        return randomLocations;
    }
}

