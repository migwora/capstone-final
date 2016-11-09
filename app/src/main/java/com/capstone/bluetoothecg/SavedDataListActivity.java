package com.capstone.bluetoothecg;

import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedDataListActivity extends AppCompatActivity  implements OnItemClickListener {

    private List<String> dataFiles = new ArrayList<String>();
    private SimpleAdapter adapter;
    private Map<String, String> map = null;
    private ListView listView;
    private String DEVICE_NAME = "name";
    public static final int RESULT_CODE = 101;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ECGDATA";
    ArrayAdapter<String> filesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.saved_data_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Saved Data");

        listView = (ListView) findViewById(R.id.savedDataListView);

        getDataFiles();
        filesAdapter = new ArrayAdapter<String>(this, R.layout.list_files, dataFiles);

        listView = (ListView) findViewById(R.id.savedDataListView);
        listView.setAdapter(filesAdapter);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);

    }

    public void getDataFiles(){
        File file = new File(Environment.getExternalStorageDirectory(), "ECGDATA");
        File[] data = file.listFiles();
        for (int i=0; i<data.length; i++)
        {
            dataFiles.add(data[i].getName());
        }
    }
    public void refreshListView(){
        filesAdapter.notifyDataSetChanged();
        listView.invalidateViews();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.savedDataListView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String fileName = ((TextView) info.targetView).getText().toString();
        File root = new File(path);;
        File sdfile = new File(root, fileName);
        switch(item.getItemId()) {
            case R.id.details:
                // add stuff here
                return true;
            case R.id.delete:
                sdfile.delete();
                finish();
                startActivity(getIntent());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {

        Object fileName = adapterView.getItemAtPosition(position);

        Intent intent = new Intent(this, ViewSavedData.class);
        intent.putExtra(DEVICE_NAME, fileName.toString());
        setResult(RESULT_CODE, intent);
        startActivity(intent);
    }
}
