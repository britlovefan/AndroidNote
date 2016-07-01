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
    public class MapCluster extends BaseDemoActivity implements OnMapReadyCallback {

        private GoogleMap mMap;
        private RealmResults<Photo> results;
        private ClusterManager<MyItem> mClusterManager;
        @Override
        protected void startDemo() {
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));
            mClusterManager = new ClusterManager<MyItem>(this, getMap());
            getMap().setOnCameraChangeListener(mClusterManager);
        }

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
    private void setUpClusterer() {
        // Declare a variable for the cluster manager.
        // Position the map.
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, getMap());
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        getMap().setOnCameraChangeListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {
        // Set some lat/lng coordinates to start with.
        double lat = 51.5145160;
        double lng = -0.1270060;
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
