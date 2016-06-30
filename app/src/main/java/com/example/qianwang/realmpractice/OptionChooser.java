package com.example.qianwang.realmpractice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by qianwang on 6/30/16.
 */
public class OptionChooser extends AppCompatActivity {
    private Button timeLine;
    private Button mapShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_load);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        timeLine = (Button)findViewById(R.id.timeline);
        mapShow = (Button)findViewById(R.id.mapOption);
        timeLine.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ShowTimeline.class));
            }
        });
        mapShow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapCluster.class));
            }
        });
    }
}
