package com.example.qianwang.realmpractice.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qianwang.realmpractice.R;
import com.example.qianwang.realmpractice.model.Photo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.Algorithm;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.algo.PreCachingAlgorithmDecorator;
import com.google.maps.android.clustering.algo.StaticCluster;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by qianwang on 6/30/16.
 */
    public class MapCluster extends BaseDemoActivity implements ClusterManager.OnClusterClickListener<MyItem>, ClusterManager.OnClusterInfoWindowClickListener<MyItem>, ClusterManager.OnClusterItemClickListener<MyItem>, ClusterManager.OnClusterItemInfoWindowClickListener<MyItem> {
    private static int CAMERA_MOVE_REACT_THRESHOLD_MS = 500;
    private long lastCallMs = Long.MIN_VALUE;
    private RealmResults<Photo> results;
    private ClusterManager<MyItem> mClusterManager;
    //add a bound variable to store the Latlgn bound info
    private LatLngBounds currentBounds;

    @Override
    protected void startDemo() {

        mClusterManager = new ClusterManager<MyItem>(this, getMap());
        mClusterManager.setRenderer(new PhotoRenderer());
        //getMap().setOnCameraChangeListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        //mClusterManager.setAlgorithm(new GridBasedAlgorithm<MyItem>());

        // Specify the Realm Database
        final RealmConfiguration config = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(config);
        results = realm.where(Photo.class).findAll();

        UiSettings uiSettings = getMap().getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        // Set the vision to the places where photos are located
        //getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.2102699279785,16.3654479980469),2));
        //getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(results.get(0).getLatitude(),results.get(0).getLongitude()),2));
        addItems();
        mClusterManager.cluster();
        realm.close();
    }
  // Add the items in a viewable region
    private void addItems() {
        /*currentBounds = getMap().getProjection().getVisibleRegion().latLngBounds;
        mClusterManager.clearItems();
        getMap().setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLngBounds bounds = getMap().getProjection().getVisibleRegion().latLngBounds;
                currentBounds = bounds;
                //getMap().setOnCameraChangeListener(mClusterManager);
            }
        });*/
        for (Photo photo : results) {
            LatLng place = new LatLng(photo.getLatitude(), photo.getLongitude());
            //getMap().addMarker(new MarkerOptions().position(place));
           // if(currentBounds.contains(place)) {
                double lat = place.latitude;
                double lng = place.longitude;
                MyItem offsetItem = new MyItem(lat, lng, photo.getId());
                mClusterManager.addItem(offsetItem);
            //}
        }
        //update the new function
        getMap().setOnCameraIdleListener(mClusterManager);
    }
    private class PhotoRenderer extends DefaultClusterRenderer<MyItem> {
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mClusterImageView;
        private final int mDimension;
        private final ImageView mImageView;
        private final TextView mTextView;
        public PhotoRenderer(){
            super(getApplicationContext(), getMap(), mClusterManager);
            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile,null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
            mTextView = (TextView) multiProfile.findViewById(R.id.amu_text);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }
        @Override
        protected void onBeforeClusterItemRendered(MyItem photo, MarkerOptions options){
            // Show a single photo, set the info photo to show their path
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/"+photo.fileName;

            File imgFile = new  File(filePath);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                mImageView.setImageBitmap(myBitmap);
            }
            Bitmap icon = mIconGenerator.makeIcon();
            options.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(photo.fileName);
        }
        @Override
        protected void onBeforeClusterRendered(Cluster<MyItem> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (MyItem p : cluster.getItems()) {
                if (profilePhotos.size() == 4) break;
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/"+p.fileName;
                File imgFile = new File(filePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                Drawable drawable = new BitmapDrawable(getResources(),myBitmap);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);
            mTextView.setText(String.valueOf(cluster.getSize()));
            mClusterImageView.setImageDrawable(multiDrawable);

            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }
        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize()>=1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<MyItem> cluster) {
        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();

        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();
        // Animate camera to the bounds,make the view to the new bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MyItem> cluster) {

        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(MyItem item) {
        // Does nothing, but you could go into the user's profile page, for example.
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MyItem item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }

}

