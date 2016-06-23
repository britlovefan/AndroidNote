package com.example.qianwang.realmpractice;

import android.app.IntentService;
import android.content.Intent;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by qianwang on 6/17/16. The address look up service, the address lookup should be
 * -plemented in background thread
 */
public class FetchAddress extends IntentService {
    private static final String TAG = "MyIntentService";
    public ResultReceiver mLocationReceiver;
    String errorMessage;
    List<Address> possibleAddress = null;
    String locationId;
    public FetchAddress(){
        super("FetchAddress");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mLocationReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        if(mLocationReceiver==null){
            Log.v("receiver","null");
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        locationId = intent.getStringExtra(Constants.LOCATION_ID);
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        try {
            possibleAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
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
        if(possibleAddress==null||possibleAddress.size()==0){
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResult(Constants.FAILURE_RESULT, null,null);
        } else {
            Address address = possibleAddress.get(0);
            Log.i(TAG, getString(R.string.address_found));
            deliverResult(Constants.SUCCESS_RESULT,possibleAddress,locationId);
        }
        }
    //deliver the result code revealing whether the address have been successfully retrieved
    //Along with the address itself
    private void deliverResult(int resultCode,List<Address> Address,String id){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.RESULT_DATA_KEY, (ArrayList<? extends Parcelable>) Address);
        bundle.putString(Constants.LOCATION_ID,id);
        mLocationReceiver.send(resultCode, bundle);
    }
}
