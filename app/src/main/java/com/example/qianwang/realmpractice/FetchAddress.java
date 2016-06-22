package com.example.qianwang.realmpractice;

import android.app.IntentService;
import android.content.Intent;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
    protected ResultReceiver mLocationReceiver;
    String errorMessage;
    List<Address> possibleAddress = null;
    public FetchAddress(){
        super("FetchAddress");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        try {
            possibleAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = possibleAddress.get(0).getAddressLine(0);
            String state = possibleAddress.get(0).getAdminArea();
            String country = possibleAddress.get(0).getCountryName();
            String postalCode = possibleAddress.get(0).getPostalCode();
            String knownName = possibleAddress.get(0).getFeatureName();

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
            deliverResult(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = possibleAddress.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            deliverResult(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
        }
    //deliver the result code revealing whether the address have been successfully retrieved
    //Along with the address itself
    private void deliverResult(int resultCode,String message){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mLocationReceiver.send(resultCode, bundle);
    }
}
