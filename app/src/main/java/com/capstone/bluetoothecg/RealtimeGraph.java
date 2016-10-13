package com.capstone.bluetoothecg;

import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class RealtimeGraph extends Activity {
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private GraphView graphView1;
    private GraphViewSeries exampleSeries1;
    private double ecgData = 0;
    private List<GraphViewData> seriesX;
    int dataCount = 1;
    boolean collect = true;

    //the Sensor Manager
    private SensorManager sManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphs);
        int x = 0;
        seriesX = new ArrayList<GraphViewData>();


        // init example series data
        exampleSeries1 = new GraphViewSeries(new GraphViewData[] {});
        graphView1 = new LineGraphView(this, "ECG Plot");

        graphView1.addSeries(exampleSeries1); // data
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(graphView1);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ecgData = MainActivity.Value;
                seriesX.add(new GraphViewData(dataCount, ecgData));
                dataCount++;
                System.out.println(ecgData);

                if (seriesX.size() > 500) {
                    seriesX.remove(0);
                    graphView1.setViewPort(dataCount - 500, 500);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        collect = false;
        moveTaskToBack(true);
    }

    @Override
    protected void onStop()
    {
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
