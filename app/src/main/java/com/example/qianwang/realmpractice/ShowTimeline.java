package com.example.qianwang.realmpractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by qianwang on 6/28/16.
 */
public class ShowTimeline extends AppCompatActivity {
    private static final int daysInMonth= 31;
    private SeekBar seekBar;
    private TextView daysDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_map);
        seekBar = (SeekBar)findViewById(R.id.seekBar1);
        daysDisplay = (TextView)findViewById(R.id.days);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressVal, boolean fromUser) {
                progress = progressVal;
                Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setText(progress);
                Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void setText(int progress) {
        int months = progress / daysInMonth + 1;
        String monthString;
        switch (months) {
            case 1:
                monthString = "January";
                break;
            case 2:
                monthString = "February";
                break;
            case 3:
                monthString = "March";
                break;
            case 4:
                monthString = "April";
                break;
            case 5:
                monthString = "May";
                break;
            case 6:
                monthString = "June";
                break;
            case 7:
                monthString = "July";
                break;
            case 8:
                monthString = "August";
                break;
            case 9:
                monthString = "September";
                break;
            case 10:
                monthString = "October";
                break;
            case 11:
                monthString = "November";
                break;
            case 12:
                monthString = "December";
                break;
            default:
                monthString = "Invalid month";
                break;
        }
        daysDisplay.setText(monthString);
    }



}
