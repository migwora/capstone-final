package com.capstone.bluetoothecg;


import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
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
    private Runnable mTimer2;
    private Runnable mTimer3;
    private GraphView graphView1;
    private GraphView graphView2;
    private GraphView graphView3;
    private GraphViewSeries exampleSeries1;
    private GraphViewSeries exampleSeries2;
    private GraphViewSeries exampleSeries3;
    private double sensorX = 0;
    private double sensorY = 0;
    private double sensorZ = 0;
    private List<GraphViewData> seriesX;
    private List<GraphViewData> seriesY;
    private List<GraphViewData> seriesZ;
    int dataCount = 1;

    //the Sensor Manager
    private SensorManager sManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphs);

        seriesX = new ArrayList<GraphViewData>();
        seriesY = new ArrayList<GraphViewData>();
        seriesZ = new ArrayList<GraphViewData>();

        //get a hook to the sensor service
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        int x = 1;
        // init example series data
        exampleSeries1 = new GraphViewSeries(new GraphViewData[] {});
        if (x == 0) {
            graphView1 = new BarGraphView(
                    this // context
                    , "GraphViewDemo" // heading
            );
        } else {
            graphView1 = new LineGraphView(
                    this // context
                    , "GraphViewDemo" // heading
            );
        }
        graphView1.addSeries(exampleSeries1); // data
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(graphView1);



    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        //if sensor is unreliable, return void
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }
        sensorX = Value;
        sensorY = event.values[1];
        sensorZ = event.values[0];

        seriesX.add(new GraphViewData(dataCount, sensorX));
        seriesY.add(new GraphViewData(dataCount, sensorY));
        seriesZ.add(new GraphViewData(dataCount, sensorZ));

        dataCount++;


/*		Context context = getApplicationContext();
		float number = (float)Math.round(sensorX * 1000) / 1000;
		//string formattedNumber = Float.toString(number);
		CharSequence text = Float.toString(number);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
*/
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
    protected void onPause() {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_FASTEST);

        mTimer1 = new Runnable() {
            @Override
            public void run() {
                GraphViewData[] gvd = new GraphViewData[seriesX.size()];
                seriesX.toArray(gvd);
                exampleSeries1.resetData(gvd);
                mHandler.post(this); //, 100);
            }
        };
        mHandler.postDelayed(mTimer1, 100);
    }
}
