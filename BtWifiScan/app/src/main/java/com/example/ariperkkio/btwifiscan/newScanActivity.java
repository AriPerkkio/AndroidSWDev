package com.example.ariperkkio.btwifiscan;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class newScanActivity extends Activity implements View.OnClickListener {

    //
    private Intent intent;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    int REQUEST_ENABLE_BT;

    //General widgets
    private Button back;
    private Button startScan;
    private EditText scanName;
    private NumberPicker sampleRate;

    //Widgets for Bluetooth options:
    private Switch switchBluetooth;
    private CheckBox btDevName;
    private CheckBox btDevAddr;
    private CheckBox btDevType;
    private CheckBox btRSSI;
    private TextView btEnabled;
    private Intent enableBtIntent;

    //Widgets for Wifi options
    private Switch switchWifi;
    private CheckBox wifiSSID;
    private CheckBox wifiBSSID;
    private CheckBox wifiCapabilities;
    private CheckBox wifiRSSI;
    private CheckBox wifiFrequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Set full screen
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //Hide automatic keyboard
        setContentView(R.layout.activity_new_scan);

        //General widgets
        back = (Button) findViewById(R.id.newScanBack);
        back.setOnClickListener(this);
        startScan = (Button) findViewById(R.id.newScanStartScan);
        startScan.setOnClickListener(this);
        scanName = (EditText) findViewById(R.id.newScanName);
        sampleRate = (NumberPicker) findViewById(R.id.newScanRateNumber);
        sampleRate.setMinValue(30);
        sampleRate.setMaxValue(900);

        // Widgets for Bluetooth
        switchBluetooth = (Switch) findViewById(R.id.newScanBtSwitch);
        switchBluetooth.setOnClickListener(this);
        btDevName = (CheckBox) findViewById(R.id.newScanDevNameChk);
        btDevName.setOnClickListener(this);
        btDevAddr = (CheckBox) findViewById(R.id.newScanDevAddrChk);
        btDevAddr.setOnClickListener(this);
        btDevType = (CheckBox) findViewById(R.id.newScanDevTypChk);
        btDevType.setOnClickListener(this);
        btRSSI = (CheckBox) findViewById(R.id.newScanBtRssiChk);
        btRSSI.setOnClickListener(this);
        btEnabled = (TextView) findViewById(R.id.newScanBtStatus);

        // Widgets for Wifi
        switchWifi = (Switch) findViewById(R.id.newScanWifiSwitch);
        switchWifi.setOnClickListener(this);
        wifiSSID = (CheckBox) findViewById(R.id.newScanWifiSsidChk);
        wifiSSID.setOnClickListener(this);
        wifiBSSID = (CheckBox) findViewById(R.id.newScanWifiBssidChk);
        wifiBSSID.setOnClickListener(this);
        wifiCapabilities = (CheckBox) findViewById(R.id.newScanWifiCapaChk);
        wifiCapabilities.setOnClickListener(this);
        wifiRSSI = (CheckBox) findViewById(R.id.newScanWifiRssiChk);
        wifiRSSI.setOnClickListener(this);
        wifiFrequency = (CheckBox) findViewById(R.id.newScanWifiFreqChk);
        wifiFrequency.setOnClickListener(this);

        if(switchBluetooth.isChecked()) {
            // Check if device's bluetooth is enabled/disabled
            if (btAdapter.isEnabled())
                btEnabled.setText("Enabled");
            else {
                btEnabled.setText("Disabled");
                setBtOptionsOFF();
                switchBluetooth.setChecked(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT){
            if (resultCode == RESULT_OK){
                switchBluetooth.setChecked(true);
                setBtOptionsON();
                btEnabled.setText("Enabled");
            }
            if(resultCode == RESULT_CANCELED){
                switchBluetooth.setChecked(false);
                setBtOptionsOFF();
                btEnabled.setText("Disabled");
            }
        }
    }//

    public void onClick(View v) {
        switch (v.getId()) {

            // 'Back' button finish activity
            case (R.id.newScanBack):
                //Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
                finish();
            break;

            // Switch to enable/disable options for bluetooth
            case (R.id.newScanBtSwitch):
                // Check if device's bluetooth is enabled/disabled
                if (!btAdapter.isEnabled()) {
                    enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                // When switch is ON, enable option-checkboxes and set color default
                if(switchBluetooth.isChecked() && btAdapter.isEnabled())
                    setBtOptionsON();
                else
                    setBtOptionsOFF();

            break;

            // Switch to enable/disable options for wifi
            case (R.id.newScanWifiSwitch):
                // When switch is ON, enable option-checkboxes and set color default
                if(switchWifi.isChecked()) {
                    wifiSSID.setEnabled(true);
                    wifiBSSID.setEnabled(true);
                    wifiCapabilities.setEnabled(true);
                    wifiFrequency.setEnabled(true);
                    wifiRSSI.setEnabled(true);
                    wifiSSID.setTextColor(getResources().getColor(R.color.mainColorCyan));
                    wifiBSSID.setTextColor(getResources().getColor(R.color.mainColorCyan));
                    wifiCapabilities.setTextColor(getResources().getColor(R.color.mainColorCyan));
                    wifiFrequency.setTextColor(getResources().getColor(R.color.mainColorCyan));
                    wifiRSSI.setTextColor(getResources().getColor(R.color.mainColorCyan));
                }
                // When switch is OFF, disable option-checkboxes and set color grey
                else {
                    wifiSSID.setEnabled(false);
                    wifiBSSID.setEnabled(false);
                    wifiCapabilities.setEnabled(false);
                    wifiFrequency.setEnabled(false);
                    wifiRSSI.setEnabled(false);
                    wifiSSID.setChecked(false);
                    wifiBSSID.setChecked(false);
                    wifiCapabilities.setChecked(false);
                    wifiFrequency.setChecked(false);
                    wifiRSSI.setChecked(false);
                    wifiSSID.setTextColor(getResources().getColor(R.color.material_grey_600));
                    wifiBSSID.setTextColor(getResources().getColor(R.color.material_grey_600));
                    wifiCapabilities.setTextColor(getResources().getColor(R.color.material_grey_600));
                    wifiFrequency.setTextColor(getResources().getColor(R.color.material_grey_600));
                    wifiRSSI.setTextColor(getResources().getColor(R.color.material_grey_600));
                }
            break;

            // 'Start scanning' button to start new activity with chosen options
            // Scanning requires at least one option checked, scan name and sample rate
            case (R.id.newScanStartScan):
                // Check that either bluetooth or wifi is checked
                if(!switchWifi.isChecked() && !switchBluetooth.isChecked()) {
                    Toast.makeText(this, "Please check at least one technology", Toast.LENGTH_SHORT).show();
                    break;
                }
                // For bluetooth scanning: Check that there is at least one Bluetooth option checked
                else if(switchBluetooth.isChecked() && !btDevName.isChecked() && !btDevAddr.isChecked() && !btDevType.isChecked() && !btRSSI.isChecked()) {
                    Toast.makeText(this, "Please check Bluetooth options", Toast.LENGTH_SHORT).show();
                    break;
                }
                // For wifi scanning: Check that there is at least one wifi option checked
                else if(switchWifi.isChecked() && !wifiSSID.isChecked() && !wifiBSSID.isChecked() && !wifiCapabilities.isChecked() && !wifiFrequency.isChecked() && !wifiRSSI.isChecked()) {
                    Toast.makeText(this, "Please check Wifi options", Toast.LENGTH_SHORT).show();
                    break;
                }
                // Scan name must be inserted
                else if(scanName.length() <= 0 ){
                    Toast.makeText(this, "Please insert scan name", Toast.LENGTH_SHORT).show();
                    break;
                }

                else {
                    intent = new Intent(newScanActivity.this, scanActivity.class); //Create intent for scanActivity
                    // Fill in options for bluetooth scanning
                    if(switchBluetooth.isChecked()) {
                        intent.putExtra("btStatus", switchBluetooth.isChecked());
                        intent.putExtra("btDevName", btDevName.isChecked());
                        intent.putExtra("btDevAddr", btDevAddr.isChecked());
                        intent.putExtra("btDevType", btDevType.isChecked());
                        intent.putExtra("btRSSI", btRSSI.isChecked());
                    }
                    // Fill in options for wifi scanning
                    if(switchWifi.isChecked()) {
                        intent.putExtra("wifiStatus", switchWifi.isChecked());
                        intent.putExtra("wifiSSID", wifiSSID.isChecked());
                        intent.putExtra("wifiBSSIDr", wifiBSSID.isChecked());
                        intent.putExtra("wifiCapabilities", wifiCapabilities.isChecked());
                        intent.putExtra("wifiFrequency", wifiFrequency.isChecked());
                        intent.putExtra("wifiRSSI", wifiRSSI.isChecked());
                    }

                    finish(); //Finish this activity and start new scan
                    startActivity(intent);

                }

            break;


        }

    }

    private void setBtOptionsOFF() {
        btDevName.setEnabled(false);
        btDevAddr.setEnabled(false);
        btDevType.setEnabled(false);
        btRSSI.setEnabled(false);
        btDevName.setChecked(false);
        btDevAddr.setChecked(false);
        btDevType.setChecked(false);
        btRSSI.setChecked(false);
        btDevName.setTextColor(getResources().getColor(R.color.material_grey_600));
        btDevAddr.setTextColor(getResources().getColor(R.color.material_grey_600));
        btDevType.setTextColor(getResources().getColor(R.color.material_grey_600));
        btRSSI.setTextColor(getResources().getColor(R.color.material_grey_600));
    }
    private void setBtOptionsON() {
        btDevName.setEnabled(true);
        btDevAddr.setEnabled(true);
        btDevType.setEnabled(true);
        btRSSI.setEnabled(true);
        btDevName.setTextColor(getResources().getColor(R.color.mainColorCyan));
        btDevAddr.setTextColor(getResources().getColor(R.color.mainColorCyan));
        btDevType.setTextColor(getResources().getColor(R.color.mainColorCyan));
        btRSSI.setTextColor(getResources().getColor(R.color.mainColorCyan));
    }
}
