package com.example.qianwang.realmpractice;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import com.example.qianwang.realmpractice.model.Photo;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    public AddressResultReceiver mResultReceiver;
    public TestResultReceiver sender;
    public RealmResults<Photo> result;
    private String locationId;
    public Realm realm;
    private ProgressBar progressBar;
    private MyBroadcastReceiver_Update myBroadcastReceiver_Update;
    public Button loadButton;
    public Button showButton;
    public Button testButton;
    private TextView status;
    private TextView testResult;
    Bundle bundle;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Tried to make the app full screen
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        initializeView();
        final RealmConfiguration config = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        // register receiver
        myBroadcastReceiver_Update = new MyBroadcastReceiver_Update();
        IntentFilter intentFilter_update = new IntentFilter(Constants.UPDATE);
        intentFilter_update.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver_Update, intentFilter_update);

        loadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                loadPictures();
                getMemoryUse();
                Log.d("", "path: " + realm.getPath());
            }
        });

        showButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                realm = Realm.getDefaultInstance();
                if(realm.isEmpty()){
                    Context context = getApplicationContext();
                    CharSequence text = "Load your photo first:)";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    startActivity(new Intent(getApplicationContext(), OptionChooser.class));
                }
            }
        });

        testButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                realm = Realm.getDefaultInstance();
                if(realm.isEmpty()){
                    Context context = getApplicationContext();
                    CharSequence text = "Load your photo first:)";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    //to show
                    testResult.setText("");
                    Intent intent = new Intent(getApplicationContext(), TestQuerySpeed.class);
                    sender = new TestResultReceiver(new Handler());
                    intent.putExtra(Constants.TEST_RECEIVER, sender);
                    startService(intent);
                }
            }
        });

        //clean the data to migration? maybe do not need to
        final RealmResults<Photo> results1 = realm.where(Photo.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results1.deleteAllFromRealm();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void initializeView(){
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        loadButton = (Button)findViewById(R.id.loadButton);
        showButton = (Button)findViewById(R.id.showData);
        testButton = (Button)findViewById(R.id.testQuery);
        status = (TextView)findViewById(R.id.loadStatus);
        testResult = (TextView)findViewById(R.id.results);
    }
    // get the exif data from the pictures in sdCard
    private void loadPictures() {
        //progressBar.setVisibility(View.VISIBLE);
        status.setText("We Are Busy Loading Your Photos...");
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
        //unregister receiver
        unregisterReceiver(myBroadcastReceiver_Update);
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
            int count = resultData.getInt(Constants.TOTAL_NUM);
            progressBar.setMax(count);

            Context context = getApplicationContext();
            CharSequence text = result;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            //unseen the progressbar and the text
            progressBar.setVisibility(View.INVISIBLE);
            status.setText("");
            //startActivity(new Intent(getApplicationContext(), OptionChooser.class));
        }
    }
    // The receiver for the test query speed result
    @SuppressLint("ParcelCreator")
    class TestResultReceiver extends ResultReceiver {
        public TestResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            long[] result = resultData.getLongArray(Constants.ELAPSE_TIME);
            testResult.append("\n"+"Test Equal Realm: "+result[0]+"ms");
            testResult.append("\n"+"Test Time Range: "+result[1]+"ms");
            testResult.append("\n"+"Test Nearest Location: "+result[2]+"ms");
        }
    }
    //Trying to implement the progress bar
    public class MyBroadcastReceiver_Update extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int update = intent.getIntExtra(Constants.INTENT_UPDATE, 0);
            progressBar.setProgress(update);
        }
    }
    // Get the memory usage of the app
    public void getMemoryUse(){
        final Runtime runtime = Runtime.getRuntime();
        final long usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB=runtime.maxMemory() / 1048576L;
        Log.v("usedMemInMB",usedMemInMB+"");
        Log.v("maxHeapSizeInMB",maxHeapSizeInMB+"");
    }
}