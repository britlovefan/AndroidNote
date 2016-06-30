package com.example.qianwang.realmpractice;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.qianwang.realmpractice.model.Photo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by qianwang on 6/30/16.
 */
    public class MapCluster extends FragmentActivity implements OnMapReadyCallback {

        private GoogleMap mMap;
        private RealmResults<Photo> results;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.map_cluster);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            final RealmConfiguration config = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded()
                    .build();
            Realm realm =  Realm.getInstance(config);
            results = realm.where(Photo.class).findAll();
            Log.v("total",results.size()+"");
        }


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            for(Photo photo:results){
                LatLng place = new LatLng(photo.getLatitude(),photo.getLongitude());
                mMap.addMarker(new MarkerOptions().position(place));
            }
            LatLng first_place = new LatLng(results.get(0).getLatitude(),results.get(0).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(first_place));
        }
    }
