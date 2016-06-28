package com.example.qianwang.realmpractice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import com.example.qianwang.realmpractice.model.Photo;


public class MainActivity extends AppCompatActivity {
    public AddressResultReceiver mResultReceiver;
    private String locationId;
    private Realm realm;
    Bundle bundle;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RealmConfiguration config = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        final RealmResults<Photo> results1 = realm.where(Photo.class).findAll();
        RealmResults<Photo> result = realm.where(Photo.class).findAll();
        // 6/27 Trying to sort the result by time
                /*result = result.sort("timeStamp");
                Log.v("The first Element",result.get(0).getId()+"");*/
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results1.deleteAllFromRealm();
            }
        });
        loadPictures();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // get the exif data from the pictures in a folder
    // the directory of the photos should be modified to be the folder of photos in android phone
    // if not using emulator
    private void loadPictures() {
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures";
        startIntentService(sdcard);
    }

    // start intent service for each GPS data pair
    protected void startIntentService(String id) {
        Intent intent = new Intent(this, FetchAddress.class);
        mResultReceiver = new AddressResultReceiver(new Handler());
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.FILE_ID,id);
        startService(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.qianwang.realmpractice/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.qianwang.realmpractice/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
    //
    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String result = resultData.getString(Constants.PROCESSED_ID);
            // Want to make a toast message to the user
            Context context = getApplicationContext();
            CharSequence text = result;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            //start another activity page
            startActivity(new Intent(getApplicationContext(), ShowTimeline.class));
        }
    }
}