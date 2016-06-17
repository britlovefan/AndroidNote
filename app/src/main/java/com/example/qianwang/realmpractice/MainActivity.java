package com.example.qianwang.realmpractice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    private AddressResultReceiver mResultReceiver;
    int resultcode;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the exif data from the pictures in a folder
        // the directory of the photos should be modified to be the folder of photos in android phone
        File[] files = new File("${rootDir}/../pictures").listFiles();
        for (File file : files) {
            if (!file.isFile()) continue;
            String[] bits = file.getName().split(".");
            if (bits.length > 0 && bits[bits.length - 1].equalsIgnoreCase("jpg")) {
                String imagePath = file.getAbsolutePath();
                Location curLocation = readGeoTagImage(imagePath);
                startIntentService(curLocation);
                mResultReceiver.onReceiveResult(resultcode,bundle);

            }
        }
    }

    // start intent service for each gps data pair
    protected void startIntentService(Location location) {
        Intent intent = new Intent(this, FetchAddress.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }


    // the function that returns the location object
    public Location readGeoTagImage(String imagePath) {
        Location loc = new Location("");
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            float[] latlong = new float[2];
            if (exif.getLatLong(latlong)) {
                loc.setLatitude(latlong[0]);
                loc.setLongitude(latlong[1]);
            }
            String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
            SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            loc.setTime(fmt_Exif.parse(date).getTime());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return loc;
    }

    //
    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.address_found);
                Toast toast =Toast.makeText(context,text, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}