package com.example.qianwang.realmpractice;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.qianwang.realmpractice.model.GeoLocation;
import com.example.qianwang.realmpractice.model.Photo;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by qianwang on 7/11/16.
 */
public class TestQuerySpeed extends IntentService{
    private RealmResults<Photo> results;
    public ResultReceiver sender;
    public TestQuerySpeed() {
        super("TestQuerySpeed");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        sender = intent.getParcelableExtra(Constants.TEST_RECEIVER);
        Realm realm = Realm.getDefaultInstance();
        results = realm.where(Photo.class).findAll();
            long[] timeElapseMonth = TestSelectQuery();
            Log.v("Select Month Test", timeElapseMonth[0] + "");
            Log.v("Select Time Range Test", timeElapseMonth[1] + "");
            long testEqual = TestEqualQuery();
            Log.v("Equal place Test",testEqual+"");
        //Equal Range Location
            long[] result = {testEqual,timeElapseMonth[1],TestLocationQuery()};
            deliverResult(0,result);
            realm.close();
        // need to close the Realm
    }
    //Pass the result back to the UI for display
    private void deliverResult(int resultcode,long[] timeElapse) {
        Bundle bundle = new Bundle();
        bundle.putLongArray(Constants.ELAPSE_TIME,timeElapse);
        sender.send(resultcode,bundle);
    }
    protected long TestEqualQuery(){
        Realm realm = Realm.getDefaultInstance();
        long startTime = System.currentTimeMillis();
        for(int i = 0;i < 1000; i++){
            RealmResults<Photo> resultsByPlace = realm.where(Photo.class).equalTo("zipCode","Evanston").findAll();
        }
        long timeElapse = System.currentTimeMillis() - startTime;
        realm.close();
        return timeElapse;
    }
    protected long[] TestSelectQuery() {
        Realm realm = Realm.getDefaultInstance();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            long[] monthRange = RandomGenerateMonth();
            RealmResults<Photo> resultsByMonth = realm.where(Photo.class).between("timeStamp", monthRange[0], monthRange[1]).findAll();
        }
        //equal...
        long timeElapse1 = System.currentTimeMillis() - startTime;
        //Test the speed of select query of certain range.
        long startTime2 = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            long[] dateRange = RandomGenerateRange();
            RealmResults<Photo> resultsByRange = realm.where(Photo.class).between("timeStamp", dateRange[0], dateRange[1]).findAll();
        }
        long timeElapse2 = System.currentTimeMillis() - startTime2;
        realm.close();
        return new long[]{timeElapse1, timeElapse2};
    }

    //function that returns the range of time in millis in random month/year
    protected long[] RandomGenerateMonth() {
        SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        int year = randBetween(2012, 2016);
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
        int year = randBetween(2012, 2016);
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
    // Test the query speed of finding the closest location of photo to a random location
    protected long TestLocationQuery(){
        Realm realm = Realm.getDefaultInstance();
        // generate a list of random locations
        ArrayList<LatLng> randomLocations = randomGenerate();
        double earthRadius = 6371.01;
        HashMap<Double,Photo> map = new HashMap<>();
        long startTime = System.currentTimeMillis();
        for(int i = 0;i < randomLocations.size();i++){
            LatLng origin = randomLocations.get(i);
            GeoLocation myLocation = GeoLocation.fromDegrees(origin.latitude,origin.longitude);
            //set the distance bound,maybe need to readjusts if no places are found in the area
            double distanceBound = 1000;
            GeoLocation[] bounds = myLocation.boundingCoordinates(distanceBound, earthRadius);
            double latmin = bounds[0].getLatitudeInRadians();
            double lgnmin = bounds[0].getLongitudeInRadians();
            double latmax = bounds[1].getLatitudeInRadians();
            double lgnmax = bounds[1].getLongitudeInRadians();
            //How to perform the query using the bounding information
            RealmResults<Photo> results = realm.where(Photo.class).between("Latitude",latmin,latmax).findAll();
            RealmResults<Photo> results1 = results.where().between("Longitude",lgnmin,lgnmax).findAll();
            double minvalue = Integer.MAX_VALUE;
            for(int j = 0;j < results1.size();j++){
                GeoLocation location = GeoLocation.fromDegrees(results1.get(j).getLatitude(),results1.get(j).getLongitude());
                double distance = myLocation.distanceTo(location,earthRadius);
                map.put(distance,results1.get(j));
                if(distance<minvalue){
                    minvalue = distance;
                }
            }
            // But what if there are multiple values that are having the same nearest distance?
            Photo nearestPhoto = map.get(minvalue);
        }
        long lastTime = System.currentTimeMillis()-startTime;
        Log.v("nearest location",lastTime+"");
        realm.close();
        return lastTime;
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
