package com.bluetoothexample.thundercap_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.ToggleButton;
import android.os.Handler;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
//import android.support.v7.app.AppCompatActivity;


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
    EditText launchDelay1;
    EditText launchDelay2;
    EditText launchDelay3;
    EditText launchDelay4;


        private Handler mHandler; // Our main handler that will receive callback notifications
        private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
       // private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

        private final static UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        // #defines for identifying shared types between calling functions
        private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
        private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
        private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothStatus = (TextView)findViewById(R.id.bluetoothStatus);
        mScanBtn = (Button)findViewById(R.id.scan);
        mOffBtn = (Button)findViewById(R.id.off);
        mDiscoverBtn = (Button)findViewById(R.id.discover);
        //Switch switch1 = findViewById(R.id.switch1);
        Button launchbutton = findViewById(R.id.button);
        //launchbutton.setBackgroundColor(Color.GREEN);
        //EditText launchDelay1;
        //EditText launchDelay2;
      // EditText launchDelay3;
       // EditText launchDelay4;

        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = (ListView)findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // GUI Handler (Potentially an issue with "Lint" in imports)
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



        // Button Listeners (Shouldn't need additional code)
        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {
/*
            switch1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("1");
                }
            });*/

            launchbutton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    launchButton(v);
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

            /* mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(v);
                }
            });*/

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover(v);
                }
            });
        }

    }
    // Bluetooth OFF and ON (Shouldn't need to change)
    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchButton(View view){
        if(mConnectedThread != null) {
            launchDelay1 = findViewById(R.id.editTextNumber);
            launchDelay2 = findViewById(R.id.editTextNumber2);
            launchDelay3 = findViewById(R.id.editTextNumber3);
            launchDelay4 = findViewById(R.id.editTextNumber4);
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
                    mConnectedThread.write("1");

                }
            }, finalValue1 * 1000);

            final Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Button b2 = findViewById(R.id.button2);
                    b2.setBackgroundColor(Color.GREEN);
                    mConnectedThread.write("2");

                }
            }, finalValue2 * 1000);

            final Handler handler3 = new Handler();
            handler3.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Button b3 = findViewById(R.id.button3);
                    b3.setBackgroundColor(Color.GREEN);
                    mConnectedThread.write("3");

                }
            }, finalValue3 * 1000);

            final Handler handler4 = new Handler();
            handler4.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Button b4 = findViewById(R.id.button5);
                    b4.setBackgroundColor(Color.GREEN);
                    mConnectedThread.write("4");

                }
            }, finalValue4 * 1000);


        }

    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("Enabled");
            } else
                mBluetoothStatus.setText("Disabled");
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Bluetooth disabled");
        Toast.makeText(getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    // Discover Devices (Shouldn't need to be changed)
    private void discover(View view){
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };


    // Connecting as a Client (Shouldn't need to be changed)
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }
/*
    public void onBtnClick(View view) {
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

            ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton2);
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                    Button button = findViewById(R.id.button);
                        button.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){

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
                        });

                    }else {
                            // The toggle is disabled

                    }
                }


            });

    }*/




    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        // This is not needed as this is reading the input bluetooth stream
        /*public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }*/

        // Call this from the main activity to send data to the remote device
        // Throw the functionality code here?
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    }




       /* mBluetoothStatus = (TextView) findViewById(R.id.bluetoothStatus);
        //mReadBuffer = (TextView) findViewById(R.id.readBuffer);
        mScanBtn = (Button) findViewById(R.id.scan);
        mOffBtn = (Button) findViewById(R.id.off);
        mDiscoverBtn = (Button) findViewById(R.id.discover);
        //mListPairedDevicesBtn = (Button)findViewById(R.id.PairedBtn);
        //mLED1 = (CheckBox)findViewById(R.id.checkboxLED1);

        mBTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = (ListView) findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == MESSAGE_READ) {
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mReadBuffer.setText(readMessage);
                }

                if (msg.what == CONNECTING_STATUS) {
                    if (msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (String) (msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };
        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(), "Bluetooth device not found!", Toast.LENGTH_SHORT).show();
        } else {

            mLED1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("1");
                }
            });


            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOff(v);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discover(v);
                }
            });
        *///}
        //}
/*
        public void switching (View view){
            //TextView txtHello = findViewById(R.id.txtHello);
            //Button button = findViewById(R.id.button);
            // button.setBackgroundColor(45);
            //Button b1 = findViewById(R.id.button);
            // b1.setBackgroundColor(Color.parseColor("FF51F60B"));
            //Switch switch1 = findViewById(R.id.switch1);

            // switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Button button = findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Button launchbutton = findViewById(R.id.button);
                    // launchbutton.setBackgroundColor(Color.GREEN);
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

            });
*/


/*
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
                }, finalValue4 * 1000);*/


//}

