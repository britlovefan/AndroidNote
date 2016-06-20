package com.example.qianwang.realmpractice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import com.example.qianwang.realmpractice.Photo;


public class MainActivity extends AppCompatActivity {
    private AddressResultReceiver mResultReceiver;
    Bundle bundle;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = (Button)findViewById(R.id.load_photo);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loadPictures();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // get the exif data from the pictures in a folder
    // the directory of the photos should be modified to be the folder of photos in android phone
    // if not using emulator
    public void loadPictures() {
        File sdcard = Environment.getExternalStorageDirectory();
        File[] files = sdcard.listFiles();
        //File[] files = new File("/Users/qianwang/Desktop/pictures").listFiles();
        for (File file : files) {
            if (!file.isFile()) continue;
            String[] bits = file.getName().split(".");
            if (bits.length > 0 && bits[bits.length - 1].equalsIgnoreCase("jpg")) {
                String imagePath = file.getAbsolutePath();
                Location curLocation = readGeoTagImage(imagePath);
                double latitude = curLocation.getLatitude();
                double longitude = curLocation.getLongitude();
                update(file.getName(),latitude,longitude);

                //what trigger this kind of service??(Still not sure what to put in the background thread)
                startIntentService(curLocation);
            }
        }
    }

    // start intent service for each GPS data pair
    protected void startIntentService(Location location) {
        Intent intent = new Intent(this, FetchAddress.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }
    // update in the database
    public void update(String id,double latitude,double longitude){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Photo photo = realm.createObject(Photo.class);
        photo.setLatitude(latitude);
        photo.setId(id);
        photo.setLongitude(longitude);
        realm.commitTransaction();
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.qianwang.realmpractice/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.qianwang.realmpractice/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    //
    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            //Deal with the addressoutput here

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.address_found);
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}