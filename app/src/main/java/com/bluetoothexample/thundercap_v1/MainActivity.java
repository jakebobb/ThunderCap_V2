package com.bluetoothexample.thundercap_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.EditText;
import android.os.Handler;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private BluetoothSocket mBTSocket = null;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;

    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            bluetoothStatus = (TextView)findViewById(R.id.bluetoothStatus);
            mReadBuffer = (TextView) findViewById(R.id.readBuffer);
            scanBtn = (Button)findViewById(R.id.scan);
            offBtn = (Button)findViewById(R.id.off);
            discoverBtn = (Button)findViewById(R.id.discover);
            listPairedDevicesBtn = (Button)findViewById(R.id.PairedBtn);
            mLED1 = (CheckBox)findViewById(R.id.checkboxLED1);

        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = (ListView)findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mReadBuffer.setText(readMessage);
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };
        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {

            mLED1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("1");
                }
            });


            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff(v);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover(v);
                }
            });
    }


        public void onBtnClick (View view){
            //TextView txtHello = findViewById(R.id.txtHello);
            //Button button = findViewById(R.id.button);
            // button.setBackgroundColor(45);
            //Button b1 = findViewById(R.id.button);
            // b1.setBackgroundColor(Color.parseColor("FF51F60B"));
            Switch switch1 = findViewById(R.id.switch1);

            switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){

                    }

                }
            });


                Button launchbutton = findViewById(R.id.button);
                launchbutton.setBackgroundColor(Color.GREEN);
                EditText launchDelay1 = findViewById(R.id.editTextNumber);
                EditText launchDelay2 = findViewById(R.id.editTextNumber2);
                EditText launchDelay3 = findViewById(R.id.editTextNumber3);
                EditText launchDelay4 = findViewById(R.id.editTextNumber4);
                // Button b1 = findViewById(R.id.button4);
                String delay1 = launchDelay1.getText().toString();
                String delay2 = launchDelay2.getText().toString();
                String delay3 = launchDelay3.getText().toString();
                String delay4 = launchDelay4.getText().toString();

                int finalValue1 = Integer.parseInt(delay1);
                int finalValue2 = Integer.parseInt(delay2);
                int finalValue3 = Integer.parseInt(delay3);
                int finalValue4 = Integer.parseInt(delay4);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Button b1 = findViewById(R.id.button4);
                        b1.setBackgroundColor(Color.GREEN);

                    }
                }, finalValue1 * 1000);

                final Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Button b2 = findViewById(R.id.button2);
                        b2.setBackgroundColor(Color.GREEN);

                    }
                }, finalValue2 * 1000);

                final Handler handler3 = new Handler();
                handler3.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Button b3 = findViewById(R.id.button3);
                        b3.setBackgroundColor(Color.GREEN);

                    }
                }, finalValue3 * 1000);

                final Handler handler4 = new Handler();
                handler4.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Button b4 = findViewById(R.id.button5);
                        b4.setBackgroundColor(Color.GREEN);

                    }
                }, finalValue4 * 1000);

        }
    }
