package com.example.qianwang.realmpractice;
import android.support.v4.media.MediaMetadataCompat;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by qianwang on 6/17/16.
 */
public class Photo extends RealmObject {
    @PrimaryKey
    private String id;
    private Long timeStamp;
    private double Latitude;
    private double Longitude;
    private String zipCode;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Long getTimeStamp(){
        return timeStamp;
    }
    public void setTimeStamp(Long timeStamp){
        this.timeStamp = timeStamp;
    }
    public double getLatitude() {
        return Latitude;
    }
    public void setLatitude(double Altitude) {
        this.Latitude = Altitude;
    }
    public double getLongitude() {
        return Longitude;
    }
    public void setLongitude(double Longitude) {
        this.Longitude = Longitude;
    }
    public void setZipCode(String zipCode){this.zipCode = zipCode;}
    public String getZipCode(){return zipCode;}


}
