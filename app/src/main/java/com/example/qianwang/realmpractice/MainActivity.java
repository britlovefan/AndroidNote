package com.example.qianwang.realmpractice;

import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExifInterface exif = new ExifInterface(pathToImage);

    }


}
