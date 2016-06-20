package com.example.qianwang.realmpractice;
import android.support.v4.media.MediaMetadataCompat;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by qianwang on 6/17/16.
 */
public class Photo extends RealmObject {
    private String id;
    private double Latitude;
    private double Longitude;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
}
