package com.example.qianwang.realmpractice;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.qianwang.realmpractice.model.MyItem;
import com.example.qianwang.realmpractice.model.Photo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by qianwang on 6/30/16.
 */
    public class MapCluster extends BaseDemoActivity {

    private RealmResults<Photo> results;
    private ClusterManager<MyItem> mClusterManager;

    @Override
    protected void startDemo() {
        mClusterManager = new ClusterManager<MyItem>(this, getMap());
        getMap().setOnCameraChangeListener(mClusterManager);
        final RealmConfiguration config = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(config);
        results = realm.where(Photo.class).findAll();
        setUpClusterer();
    }
    private void setUpClusterer() {
        // Position the map to where the first photo was taken
        LatLng first_place = new LatLng(results.get(0).getLatitude(), results.get(0).getLongitude());
        getMap().moveCamera(CameraUpdateFactory.newLatLng(first_place));
        // Initialize the manager with the context and the map.
        mClusterManager = new ClusterManager<MyItem>(this, getMap());
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        getMap().setOnCameraChangeListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {
        for (Photo photo : results) {
            LatLng place = new LatLng(photo.getLatitude(), photo.getLongitude());
            getMap().addMarker(new MarkerOptions().position(place));
            double lat = place.latitude;
            double lng = place.longitude;
            // Add ten cluster items in close proximity, for purposes of this example.
            for (int i = 0; i < 10; i++) {
                double offset = i / 60d;
                lat = lat + offset;
                lng = lng + offset;
                MyItem offsetItem = new MyItem(lat, lng);
                mClusterManager.addItem(offsetItem);
            }
        }
    }
}
