package com.example.qianwang.realmpractice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import com.example.qianwang.realmpractice.Photo;
import com.google.android.gms.vision.barcode.Barcode;


public class MainActivity extends AppCompatActivity {
    public AddressResultReceiver mResultReceiver;
    private String locationId;
    private Realm realm;
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
        final RealmConfiguration config = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

        final Button button = (Button)findViewById(R.id.load_photo);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //delete the data from last run on the table
                final RealmResults<Photo> results1 = realm.where(Photo.class).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        results1.deleteAllFromRealm();
                    }
                });
                //
                loadPictures();
                RealmResults<Photo> results = realm.where(Photo.class).findAll();
                    Log.v("show length of data",results.size()+"");
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    /*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadPictures();// permission was granted, yay!
                    Log.v("loaded?","yes");
                }
                else{
                    Log.v("loaded?","no");
                }
                return;
            }
        }
    }*/
    /*public void checkPermission()
    {
        int permissionCheck1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }*/

    // get the exif data from the pictures in a folder
    // the directory of the photos should be modified to be the folder of photos in android phone
    // if not using emulator

    public void loadPictures() {
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures";
        File[] files = new File(sdcard).listFiles();
        Log.v("data size",files.length+"");
        int count = 0;
        for (File file : files) {
            if (!file.isFile()) continue;
            String[] bits = file.getName().split("\\.");
            if (bits.length > 0 && bits[bits.length - 1].equalsIgnoreCase("jpg")) {
                String imagePath = file.getAbsolutePath();
                Location curLocation = readGeoTagImage(imagePath);
                double latitude = curLocation.getLatitude();
                double longitude = curLocation.getLongitude();
                long time = curLocation.getTime();
                if (latitude != 0 && longitude != 0 && time != 0) {
                    Photo photo = new Photo();
                    photo.setTimeStamp(time);
                    photo.setLongitude(longitude);
                    photo.setId(file.getName());
                    photo.setLatitude(latitude);
                    realm.beginTransaction();
                    Photo photoUser = realm.copyToRealm(photo);
                    realm.commitTransaction();
                    startIntentService(curLocation,file.getName());
                }
            }
        }
    }

    // start intent service for each GPS data pair
    protected void startIntentService(Location location,String id) {
        Intent intent = new Intent(this, FetchAddress.class);
        mResultReceiver = new AddressResultReceiver(new Handler());
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        intent.putExtra(Constants.LOCATION_ID,id);
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
            if(date!=null){
            loc.setTime(fmt_Exif.parse(date).getTime());
            }
            else{
                Log.v("file is null",imagePath+"");
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    //
    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
             List<Address> addresses = new ArrayList<>();
             addresses = resultData.getParcelableArrayList(Constants.RESULT_DATA_KEY);
             String zipCode = addresses.get(0).getPostalCode();
             String name = resultData.getString(Constants.LOCATION_ID);
            if (resultCode == Constants.SUCCESS_RESULT) {
                Photo p = realm.where(Photo.class).equalTo("id",name).findFirst();
                realm.beginTransaction();
                p.setZipCode(zipCode);
                realm.commitTransaction();
            }
        }
    }
}