package com.example.ariperkkio.btwifiscan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

public class subPrevScanActivity extends Activity implements View.OnClickListener, ListView.OnItemClickListener, ListView.OnItemLongClickListener {

    private Button back;
    private Button rename;
    private Button map;

    private EditText scanNameField;
    private TextView scanDate;
    private TextView btNumber;
    private TextView wifiNumber;
    private int numberOfBtDevices = 0;
    private int numberOfWifiNetworks = 0;
    private int scanId;

    private ListView list;
    private databaseManager database;
    private subPrevScanCursorAdapter listAdapter;
    private Cursor btCursor;
    private Cursor wifiCursor;
    private Cursor bothCursors; //Used to combine bt and wifi results for one list
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Set full screen
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //Hide automatic keyboard
        setContentView(R.layout.activity_sub_prev_scan);

        back = (Button) findViewById(R.id.subPrevScanBack);
        back.setOnClickListener(this);
        rename = (Button) findViewById(R.id.subPrevScanRename);
        rename.setOnClickListener(this);
        map = (Button) findViewById(R.id.subPrevScanMap);
        map.setOnClickListener(this);
        scanNameField = (EditText) findViewById(R.id.subPrevScanName);
        scanDate = (TextView) findViewById(R.id.subPrevScanDate);
        btNumber = (TextView) findViewById(R.id.subPrevScanBtNumber);
        wifiNumber = (TextView) findViewById(R.id.subPrevScanWifiNumber);
        database = new databaseManager(this);

        // Set scan details to text fields
        scanNameField.setText(getIntent().getExtras().getString("scanName"));
        scanDate.setText(getIntent().getExtras().getString("scanDate"));
        scanId = getIntent().getExtras().getInt("scanId");

        list = (ListView) findViewById(R.id.subPrevScanList);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);

        try{
            database.open();
            // BtResults and WifiResults have different columns - both need own cursors
            btCursor = database.getBtResultsById(scanId); // Get bt results by scan id
            wifiCursor = database.getWifiResultsById(scanId); // Get wifi results by id
            numberOfBtDevices = database.getNumberOfBtById(scanId); // Get number of bt devices by id
            numberOfWifiNetworks = database.getNumberOfWifiById(scanId); // Get number of wifi results by id
            database.close();
        }catch (SQLException e) {
            Log.e("getBtResultsById: ", e.toString());
        }
        // MergeCursor combines both cursors into one
        bothCursors = new MergeCursor(new Cursor[] {btCursor, wifiCursor});
        listAdapter = new subPrevScanCursorAdapter(this, bothCursors, 0);
        list.setAdapter(listAdapter);
        btNumber.setText(numberOfBtDevices + ""); // Setting as int only causes crashing
        wifiNumber.setText(numberOfWifiNetworks+"");
    }

    public void onClick(View v) {
        switch(v.getId()){

            case (R.id.subPrevScanBack): // 'Back' button
                finish();
            break;

            case (R.id.subPrevScanRename): // 'Rename' button

                if(scanNameField.getText().toString().equals("")) // Check if empty name
                    Toast.makeText(this, "Scan name can't be empty.", Toast.LENGTH_SHORT).show();
                // Check if rename has been pressed without making changes to scan name
                else if(scanNameField.getText().toString().equals(getIntent().getExtras().getString("scanName")))
                    Toast.makeText(this, "Insert new scan name first.", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        database.open();
                        database.renameScan(scanId, scanNameField.getText().toString());
                        database.close();
                    } catch (SQLException e) {
                        Log.e("getBtResultsById: ", e.toString());
                    }
                    Toast.makeText(this, getIntent().getExtras().getString("scanName")+ " renamed to " +scanNameField.getText().toString(), Toast.LENGTH_SHORT).show();
                    Intent refresh = new Intent(this, previousScansActivity.class);
                    startActivity(refresh); // Start previous activity again in order to refresh list
                    this.finish();
                }
            break;

            case (R.id.subPrevScanMap):
                // TODO: Use Parcelable
                intent = new Intent(getApplicationContext(), scanMap.class);
                intent.putExtra("Count", bothCursors.getCount());
                bothCursors.moveToFirst();
                for(int i = 0;i<bothCursors.getCount();i++) {
                    if(bothCursors.getColumnCount() == 6)
                        intent.putExtra("location " + i, bothCursors.getString(5));
                    else
                        intent.putExtra("location " + i, bothCursors.getString(6));
                    bothCursors.moveToNext();
                }
                startActivity(intent);

            break;
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View v, int position, long arg)
    {
        Cursor selectedObject = (Cursor) (list.getItemAtPosition(position));
        intent = new Intent(subPrevScanActivity.this, resultDetailsActivity.class);

        // Fill in details of selected scan
        // Bt has 6 columns, wifi has 7
        // Using technology to id which attributes to getExtra
        if(selectedObject.getColumnCount()==6) {
            intent.putExtra("technology", "Bluetooth");
            intent.putExtra("btDevName", selectedObject.getString(1));
            intent.putExtra("btDevAddr", selectedObject.getString(2));
            intent.putExtra("btDevType", selectedObject.getString(3));
            intent.putExtra("btRSSI", selectedObject.getString(4));
            intent.putExtra("location", selectedObject.getString(5));
        }

        if(selectedObject.getColumnCount()==7) {
            intent.putExtra("technology", "Wifi");
            intent.putExtra("wifiSSID", selectedObject.getString(1));
            intent.putExtra("wifiBSSID", selectedObject.getString(2));
            intent.putExtra("wifiCapabilities", selectedObject.getString(3));
            intent.putExtra("wifiFrequency", selectedObject.getString(4));
            intent.putExtra("wifiRSSI", selectedObject.getString(5));
            intent.putExtra("location", selectedObject.getString(6));
        }
        startActivity(intent); // Start resultDetailsActivity but keep this one alive
    }


    // Long click listener for removing results
    // Once scan result is removed, both cursors have to be reloaded from database
    // and list adapter refreshed by swap cursor. Numbers of bt and wifi results are also updated
    public boolean onItemLongClick(AdapterView<?> adapterView, View v, int position, long arg){
        final Cursor selectedObject = (Cursor) (list.getItemAtPosition(position));

        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Create new builder for alert dialog
        builder.setMessage("Do you want to remove scan result "+selectedObject.getString(1)+" / "+selectedObject.getString(2)+"?")
                .setTitle("Remove scan result"); // Add message and title for dialog
        // Add 'Remove' button for dialog - using anonymous click listener
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String technology = ""; // String to identify which result it is, used in databaseManager class
                if (selectedObject.getColumnCount() == 5)
                    technology = "Bluetooth";
                if (selectedObject.getColumnCount() == 6)
                    technology = "Wifi";
                try {
                    database.open();
                    database.deleteResult(scanId, technology, selectedObject.getString(2)); // Delete scan result
                    btCursor = database.getBtResultsById(scanId); // Get bt results by scan id
                    wifiCursor = database.getWifiResultsById(scanId); // Get wifi results by id
                    numberOfBtDevices = database.getNumberOfBtById(scanId); // Get number of bt devices by id
                    numberOfWifiNetworks = database.getNumberOfWifiById(scanId); // Get number of wifi results by id
                    database.close();
                } catch (SQLException e) {
                    Log.e("deleteResult: ", e.toString());
                }
                Toast.makeText(subPrevScanActivity.this, "Scan result "+selectedObject.getString(1)+" / "+selectedObject.getString(2)+" deleted.", Toast.LENGTH_LONG).show();
                bothCursors = new MergeCursor(new Cursor[] {btCursor, wifiCursor}); // Combine cursors
                listAdapter.swapCursor(bothCursors); // Swap cursor to new one with updated values
                listAdapter.notifyDataSetChanged(); // Refresh list with new cursor
                btNumber.setText(numberOfBtDevices + ""); // Setting as int only causes crashing
                wifiNumber.setText(numberOfWifiNetworks + "");
            }
        });
        // Add 'Cancel' button for dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel(); // Hide dialog
            }
        });
        AlertDialog dialog = builder.create(); // Create dialog from previous attributes
        dialog.show(); // Show dialog
        return true;
    }

    // Custom Cursor adapter to handle results from database
    // Allows use of both wifi and bluetooth results - identified by columnCount
    private class subPrevScanCursorAdapter extends CursorAdapter {
        private LayoutInflater cursorInflater;
        public subPrevScanCursorAdapter (Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
            cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void bindView(View view, Context context, Cursor cursor) {
            // See XML for clarification about IDs
            ImageView icon = (ImageView) view.findViewById(R.id.scanrowIcon);
            TextView fieldOne = (TextView) view.findViewById(R.id.scanrowOne);
            TextView fieldTwo = (TextView) view.findViewById(R.id.scanrowTwo);
            TextView fieldThree = (TextView) view.findViewById(R.id.scanrowThree);
            TextView fieldFour = (TextView) view.findViewById(R.id.scanrowFour);
            TextView fieldFive = (TextView) view.findViewById(R.id.scanrowFive);
            TextView fieldSix = (TextView) view.findViewById(R.id.scanrowSix);

            // Bluetooth rows have 6 columns
            if (cursor.getColumnCount() == 6) {
                icon.setImageResource(R.drawable.bticon);
                fieldOne.setText(cursor.getString(1));
                fieldTwo.setText("");
                fieldThree.setText(cursor.getString(2));
                fieldFour.setText(cursor.getString(3));
                fieldFive.setText("");
                if (cursor.getString(4).equals("0")) // btRSSI is 0 when user unchecks it from newScanActivity
                    fieldSix.setText("");
                else
                    fieldSix.setText("RSSI: " + cursor.getString(4) + " dBm");
            }

            // Wifi results have 7 rows
            if (cursor.getColumnCount()==7) {
                icon.setImageResource(R.drawable.wifiicon);
                fieldOne.setText(cursor.getString(1));
                fieldTwo.setText(cursor.getString(2));
                fieldThree.setText(cursor.getString(3));
                fieldFour.setText("");
                if(cursor.getString(4).equals("0")) // wifiFrequency is 0 when user unchecks it from newScanActivity
                    fieldFive.setText("");
                else
                    fieldFive.setText(cursor.getString(4) + " MHz");
                if(cursor.getString(5).equals("0")) // wifiRSSI is 0 when user unchecks it from newScanActivity
                    fieldSix.setText("");
                else
                    fieldSix.setText("RSSI: "+cursor.getString(5)+" dbm"); // crashes when only int as text
            }
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return cursorInflater.inflate(R.layout.scanrow, parent, false);
        }
    }// Close inner class
}
