package com.capstone.bluetoothecg;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class RealtimeGraph extends MainActivity implements SensorEventListener {
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private GraphView graphView1;
    private GraphViewSeries exampleSeries1;
    private double sensorX = 0;
    private List<GraphViewData> seriesX;
    int dataCount = 1;

    //the Sensor Manager
    private SensorManager sManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.graphs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Realtime ECG");


        seriesX = new ArrayList<GraphViewData>();

        //get a hook to the sensor service
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);

//        new Thread(new Task()).start();
//        System.out.println("I GOT HERE  " + Value);

        // init example series data
        exampleSeries1 = new GraphViewSeries(new GraphViewData[] {});
        exampleSeries1.getStyle().thickness = 5;
        exampleSeries1.getStyle().color = Color.GREEN;
        graphView1 = new LineGraphView(this, "Realtime ECG");
        graphView1.setDrawingCacheBackgroundColor(Color.BLACK);

        graphView1.setManualYAxisBounds(800, 0);
        graphView1.addSeries(exampleSeries1); // data
        ((LineGraphView) graphView1).setDrawBackground(true);
        graphView1.setBackgroundColor(Color.BLACK);

        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(graphView1);


    }


    public void getData() {
        sensorX = MainActivity.Value;

        seriesX.add(new GraphViewData(dataCount, sensorX));

        dataCount++;

        if (seriesX.size() > 500) {
            seriesX.remove(0);
            graphView1.setViewPort(dataCount - 500, 500);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        //if sensor is unreliable, return void
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }
        sensorX = MainActivity.Value;

        seriesX.add(new GraphViewData(dataCount, sensorX));

        dataCount++;


        if (seriesX.size() > 500) {
            seriesX.remove(0);
            graphView1.setViewPort(dataCount - 500, 500);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
        //Do nothing.
    }

    @Override
    protected void onStop()
    {
        //unregister the sensor listener
        sManager.unregisterListener(this);
        super.onStop();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_FASTEST);

        mTimer1 = new Runnable() {
            @Override
            public void run() {
                GraphViewData[] gvd = new GraphViewData[seriesX.size()];
                seriesX.toArray(gvd);
                exampleSeries1.resetData(gvd);
                mHandler.post(this); //, 100);
            }
        };
        mHandler.postDelayed(mTimer1, 10);
    }
}
