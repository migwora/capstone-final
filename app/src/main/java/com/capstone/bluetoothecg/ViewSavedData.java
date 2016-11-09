package com.capstone.bluetoothecg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class ViewSavedData extends Activity {
    private String DEVICE_NAME = "name";
    private XYMultipleSeriesRenderer renderer;
    private XYSeriesRenderer rendererSeries;
    private XYSeries xySeries;
    private XYMultipleSeriesDataset dataset;
    private GraphicalView view;
    // TODO: The sampling rate here needs to be accurate (it's not now)
    private double samplingRate = 1;
//    private int xLookahead = 1;
    private int yMax = 800;
    private int yMin = 400;
    private ArrayList<Double> ecgData = new ArrayList<Double>();

    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ECGDATA";
    Double [] dataArray;

    private String fileName;
    /* TODO: encapsulate this cross-app
    * Gets the appropriate file to write to. If multiple users are supported, should have a new file for
    each
    * and some way to know who's file to access.
    *
    * For now, just use/overwrite the same file each time the service is started.
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_chart);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileName = extras.getString(DEVICE_NAME);
        }
        File root  = new File(path);;
        Scanner scanner;
        try {
            scanner = new Scanner(new File(root, fileName));
            while(scanner.hasNextDouble()){
                ecgData.add(scanner.nextDouble());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

         dataArray= new Double[ecgData.size()];


        displaySavedData();
    }

    public void displaySavedData() {
        initChartStuff();
        dataset = new XYMultipleSeriesDataset();
        xySeries = new XYSeries(renderer.getChartTitle());
// fill series
        double currentX = 0;
        for (Double d : ecgData) {
            Log.d("TOM",d.toString());

            xySeries.add(currentX, d);
            currentX += samplingRate;
            if (d > yMax) {
                yMax = d.intValue();
            }
            if ( d < yMin) {
                yMin = d.intValue();
            }
        }
        dataset.addSeries(xySeries);
// TODO: Setup the X axis max and mins once we have a filled series
        int xCurr = xySeries.getItemCount();
        int xMin = 0;
        renderer.setXAxisMax(ecgData.size());
        renderer.setXAxisMin(xMin);
        renderer.setYAxisMax(yMax);
        renderer.setYAxisMin(yMin);
        view = ChartFactory.getLineChartView(getApplicationContext(), dataset, renderer);
        view.refreshDrawableState();
        setContentView(view);
    }
    // TODO: This stuff taken from ECGChartActivity
    private void initChartStuff() {
        renderer = new XYMultipleSeriesRenderer();
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.BLACK);//argb(100, 50, 50, 50));
        renderer.setLabelsTextSize(35);
        renderer.setLegendTextSize(35);
        renderer.setAxesColor(Color.WHITE);
        renderer.setAxisTitleTextSize(35);
        renderer.setChartTitle("ECG Heartbeat");
        renderer.setChartTitleTextSize(35);
        renderer.setFitLegend(true);
        renderer.setGridColor(Color.BLACK);
        renderer.setPanEnabled(false, false); // TODO
        renderer.setPointSize(10);
        renderer.setXTitle("X");
        renderer.setYTitle("Y");
        renderer.setMargins(new int []{5, 50, 50, 5}); // TODO: i doubled
        renderer.setZoomButtonsVisible(false);
//        renderer.setZoomEnabled(true); // TODO: try true
        renderer.setZoomEnabled(false,false);
        renderer.setBarSpacing(10);
        renderer.setShowGrid(false);
// TODO: Reset the MAX AND MIN VALUES!!
        renderer.setYAxisMax(yMax);//2.4);
        renderer.setYAxisMin(yMin);//0.4);
        rendererSeries = new XYSeriesRenderer();
        rendererSeries.setColor(Color.GREEN);
        rendererSeries.setLineWidth(5f);
        renderer.addSeriesRenderer(rendererSeries);
    }
}