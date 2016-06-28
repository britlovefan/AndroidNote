package com.example.qianwang.realmpractice;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.qianwang.realmpractice.model.Photo;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        String sdcard = intent.getStringExtra(Constants.FILE_ID);
        File[] files = new File(sdcard).listFiles();
        Log.v("data size", files.length + "");
        int count = 0;
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
                if (latitude != 0 && longitude != 0 && time != 0) {
                    try {
                        possibleAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException ioException) {
                        // Catch network or other I/O problems.
                        errorMessage = getString(R.string.service_not_available);
                        Log.e(TAG, errorMessage, ioException);
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
                        deliverResult(Constants.FAILURE_RESULT);
                    } else {
                        Address address = possibleAddress.get(0);
                        //Log.i(TAG, getString(R.string.address_found));
                        //perform the add  zipcode column not by finding
                        zipCode = address.getPostalCode();
                        Photo photo = new Photo();
                        photo.setId(name);
                        photo.setTimeStamp(time);
                        photo.setLongitude(longitude);
                        photo.setLatitude(latitude);
                        photo.setZipCode(zipCode);
                        realm.beginTransaction();
                        Photo photoUser1 = realm.copyToRealmOrUpdate(photo);
                        realm.commitTransaction();
                    }
                }
            }
        }
        deliverResult(Constants.SUCCESS_RESULT);
        RealmResults<Photo> results = realm.where(Photo.class).findAll();
        Log.v("show length of data",results.size()+"");
        realm.close();
    }

    //deliver the result code revealing whether the address have been successfully retrieved
    //Along with the address itself
    private void deliverResult(int resultCode) {
        Bundle bundle = new Bundle();
        if(resultCode == 0) {
            bundle.putString(Constants.PROCESSED_ID, "We have processed your photo");
        }
        else{
            bundle.putString(Constants.PROCESSED_ID,"We didn't process your photo correctly");
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
}

