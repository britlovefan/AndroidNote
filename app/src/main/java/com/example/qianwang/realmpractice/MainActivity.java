package com.example.qianwang.realmpractice;

import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // get the exif data from the pictures in a folder
        String pathToImage;
        File[] files = new File("${rootDir}/../pictures").listFiles();
        for (File file : files) {
            if (!file.isFile()) continue;
            String[] bits = file.getName().split(".");
            if (bits.length > 0 && bits[bits.length - 1].equalsIgnoreCase("jpg")) {
                try {
                    ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                    String timeDate = exif.getAttribute(ExifInterface.TAG_DATETIME);
                    String gpsLongitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                    String gpsAltitude = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }




}
