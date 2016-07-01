package com.example.qianwang.realmpractice.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by qianwang on 6/30/16.
 */
public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    public final String fileName;

    public MyItem(double lat, double lng,String fileName) {
        mPosition = new LatLng(lat, lng);
        this.fileName = fileName;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

}