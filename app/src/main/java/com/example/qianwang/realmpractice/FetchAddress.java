package com.example.qianwang.realmpractice;

import android.app.IntentService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.example.qianwang.realmpractice.model.Photo;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by qianwang on 6/17/16. The address look up service, the address lookup should be
 * -plemented in background thread.
 * on 6/27/16 The database "write" should be implemented in background too.
 */
public class FetchAddress extends IntentService {
    private static final String TAG = "MyIntentService";
    public ResultReceiver mLocationReceiver;
    String errorMessage;
    List<Address> possibleAddress = null;
    String locationId;
    String zipCode;
    ArrayList<String> photoId;
    // The arraylists to first store the data and later insert into the Realm database
    ArrayList<Address> photoAddress;
    ArrayList<Location> photoLocation;

    public FetchAddress() {
        super("FetchAddress");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //set up the Realm Instant
        final RealmConfiguration config = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();
        mLocationReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        locationId = intent.getStringExtra(Constants.LOCATION_ID);

        photoId = new ArrayList<>();
        photoAddress = new ArrayList<>();
        photoLocation = new ArrayList<>();

        String sdcard = intent.getStringExtra(Constants.FILE_ID);
        File[] files = new File(sdcard).listFiles();
        int num = 0;
        //if there is no internet connection,stop the process
            for (File file : files) {
                if (!file.isFile()) continue;
                String[] bits = file.getName().split("\\.");
                if (bits.length > 0 && bits[bits.length - 1].equalsIgnoreCase("jpg")) {
                    String imagePath = file.getAbsolutePath();
                    Location location = readGeoTagImage(imagePath);
                    String name = file.getName();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    long time = location.getTime();
                    Date date = new Date(time);

                    if (latitude != 0 && longitude != 0 && time != 0) {
                        num++;
                        try {
                            possibleAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        } catch (IOException ioException) {
                            // Catch network or other I/O problems.
                            errorMessage = getString(R.string.service_not_available);
                            Log.e(TAG, errorMessage, ioException);
                            //? How to add
                        } catch (IllegalArgumentException illegalArgumentException) {
                            // Catch invalid latitude or longitude values.
                            errorMessage = getString(R.string.invalid_lat_long_used);
                            Log.e(TAG, errorMessage + ". " +
                                    "Latitude = " + location.getLatitude() +
                                    ", Longitude = " +
                                    location.getLongitude(), illegalArgumentException);
                        }
                        //if no address is found
                        if (possibleAddress == null || possibleAddress.size() == 0) {
                            if (errorMessage.isEmpty()) {
                                errorMessage = getString(R.string.no_address_found);
                                Log.e(TAG, errorMessage);
                            }
                            deliverResult(Constants.FAILURE_RESULT, 0);
                        } else {
                            photoId.add(name);
                            photoAddress.add(possibleAddress.get(0));
                            photoLocation.add(location);
                        }
                    }
                }
            }

        Log.v("number", num + "");
        Intent intentUpdate = new Intent();
        intentUpdate.setAction(Constants.UPDATE);
        // Add to the Database and start the timer

        long timeSum = 0;
        for (int i = 0; i < photoId.size(); i++) {
            zipCode = photoAddress.get(i).getLocality();
            Photo photo = new Photo();
            photo.setId(photoId.get(i));
            photo.setTimeStamp(photoLocation.get(i).getTime());
            photo.setLongitude(photoLocation.get(i).getLongitude());
            photo.setLatitude(photoLocation.get(i).getLatitude());
            photo.setZipCode(zipCode);
            long startTime = System.currentTimeMillis();
            realm.beginTransaction();
            Photo photoUser1 = realm.copyToRealmOrUpdate(photo);
            realm.commitTransaction();
            timeSum = timeSum + System.currentTimeMillis() - startTime;
            intentUpdate.putExtra(Constants.INTENT_UPDATE, i);
            sendBroadcast(intentUpdate);
        }
        realm.close();
        Log.v("Time Elapse", timeSum + "ms");
        deliverResult(Constants.SUCCESS_RESULT, photoId.size());

    }

    //deliver the result code revealing whether the address have been successfully retrieved
    //deliver the total number of photo processed by the database
    private void deliverResult(int resultCode, int count) {
        Bundle bundle = new Bundle();
        if (resultCode == 0) {
            bundle.putInt(Constants.TOTAL_NUM, count);
            bundle.putString(Constants.PROCESSED_ID, "We have processed your photo");
        } else {
            bundle.putString(Constants.PROCESSED_ID, "We didn't process your photo correctly");
        }
        mLocationReceiver.send(resultCode, bundle);
    }

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
            if (date != null) {
                loc.setTime(fmt_Exif.parse(date).getTime());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return loc;
    }

    //the function that checks whether the device is connected to the internet
    /*public static boolean connectInternet() {
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        return ( netInfo != null && netInfo.isConnected());
    }*/
}

