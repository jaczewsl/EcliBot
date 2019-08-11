package com.example.test;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.UUID;


public class TrainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //--------------------------------- VARIABLES --------------------------------------------------

    Button btnFor, btnBac, btnLef, btnRig, btnTem, btnDis;
    Spinner colour;                                                                         // switching colours red-green-blue-no colour
    Switch buzzer;                                                                          // switch will turn the buzzer on/off
    TextView temp;                                                                          // Will display temperature
    String address = "B8:27:EB:BD:37:5C";                                                   // RaspberryPi MAC address
    public BluetoothAdapter myBluetooth = null;                                             // BT adaptor
    BluetoothSocket btSocket = null;                                                        // BT socket
    private boolean isBtConnected = false;                                                  // flag that keeps track on connection status
    static final UUID myUUID = UUID.fromString("1e0ca4ea-299d-4335-93eb-27fcfe7fa848");     // SPP Universally unique identifier - 128-bit number


    private int refresh = 1;
    String s;

    //--------------------------------- onCreate STARTS HERE ---------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);    // view of the Train Activity

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // call the widgtes
        btnFor = findViewById(R.id.btn_trn_for);
        btnBac = findViewById(R.id.btn_trn_bac);
        btnDis = findViewById(R.id.btn_trn_dis);
        btnLef = findViewById(R.id.btn_trn_lef);
        btnRig = findViewById(R.id.btn_trn_rig);
        btnTem = findViewById(R.id.btn_trn_tem);

        colour = findViewById(R.id.spi_col);
        buzzer = findViewById(R.id.swt_buzz);
        temp = findViewById(R.id.txt_tem);

        new ConnectBT().execute();                      // Call the class to connect

        //commands to be sent to bluetooth
        btnFor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goForward();                            // method to go forward
            }
        });

        btnBac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goBackward();                           // method to go backward
            }
        });

        btnLef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goLeft();                               // method to go left by 90 degree
            }
        });

        btnRig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goRight();                              // method to go right by 90 degree
            }
        });

        btnTem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showTemp();                             // method to display temperature
            }
        });

//        btnDis.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Disconnect(); //close connection
//            }
//        });


        buzzer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if (btSocket!=null) {
                        try {
                            btSocket.getOutputStream().write("BUZZER ON".getBytes());
                            Log.d("--- TRAIN A. SWITCH ---", "Buzzer On");
                        }
                        catch (IOException e) {
                            msg("Error");
                        }
                    }
                }
                else{
                    if (btSocket!=null) {
                        try {
                            btSocket.getOutputStream().write("BUZZER OFF".getBytes());
                            Log.d("--- TRAIN A. SWITCH ---", "Buzzer Off");
                        }
                        catch (IOException e) {
                            msg("Error");
                        }
                    }
                }
            }
        });


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.colours, R.layout.support_simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);     // Specify the layout to use when the list of choices appears
        colour.setAdapter(adapter);                                                         // Apply the adapter to the spinner
        colour.setOnItemSelectedListener(this);                                             // Set listener on spinner

    }

    //--------------------------------- onCreate ENDS HERE -----------------------------------------


    // ---------------------------- SENDING STREAM MESSAGES THROUGH SOCKET -------------------------

    private void goBackward() {                                             // send String message through socket
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("BACKWARD".getBytes());    // send "BACKWARD" String to server
                Log.d("-- TA Backward --", "BACKWARD");
            }
            catch (IOException e) {
                msg("Error going backward");
            }
        }
    }

    private void goForward() {                                              // send String message through socket
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("FORWARD".getBytes());     // send "FORWARD" String to server
            }
            catch (IOException e) {
                msg("Error going forward");
            }
        }
    }

    private void goLeft() {                                                 // send String message through socket
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("LEFT".getBytes());        // send "LEFT" String to server
            }
            catch (IOException e) {
                msg("Error turning left");
            }
        }
    }

    private void goRight() {                                                // send String message through socket
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("RIGHT".getBytes());       // send "RIGHT" String to server
            }
            catch (IOException e) {
                msg("Error turning right");
            }
        }
    }

    private void showTemp() {                                               // send String message through socket
                                                                            // and then received one from server
        if (btSocket!=null) {                                               // and displays it on 'temp' TextView
            try {
                btSocket.getOutputStream().write("TEMP".getBytes());        // send "TEMP" String to server

                // Changes made on 27/07/2019
                byte[] buff = new byte[1024];                               // buffer of bytes used for receiving stream of data
                String s;                                                   // variable used for changing TextView property of text

                if(btSocket.getInputStream().available() == 0){             // is stream available for connection?
                    Log.d("--TA TEMP INPUT --", "getInputStream available");
                    int i = btSocket.getInputStream().read(buff);           // read stream from the buffer
                    s = new String(buff);                                   // change byte message to String
                    temp.setText(s);                                        // set text attribute to TextView 'temp'
                }
                else{
                    Log.d("--TA TEMP INPUT --", "getInputStream unavailable");
                }
            }
            catch (IOException e) {
                msg("Error showing temperature");                                             // if error thrown a Toast will be shown
            }
        }
    }


    // method to use Toast Messaging system
    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // no inspection Simplifiable If Statement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == android.R.id.home){

            if (btSocket!=null)       {                                             // if the btSocket is busy
                try {
                    btSocket.getOutputStream().write("DISCONNECT".getBytes());      // send "DISCONNECT" String to server
                    btSocket.close();                                               // close socket
                    new ConnectBT().cancel(true);                   // close connection
                }
                catch (IOException e) {
                    msg("Error closing connection");                                                 // if error thrown, a Toast will be shown
                }
            }
            this.finish();                                                           // return to the previous Activity -> MenuActivity
            Log.d("-- TA GO BACK --", "Did I reach it");
            Intent myIntent = new Intent(TrainActivity.this, MenuActivity.class);
            TrainActivity.this.startActivity(myIntent);
            Log.d("-- TA GO BACK --", "Did I reach it 2");
        }
        return super.onOptionsItemSelected(item);
    }


    // stream messages from the spinner 'colour' to the server through socket
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Object item = adapterView.getItemAtPosition(i);

        if(item != null){

            if(refresh > 1) {
                s = String.valueOf(refresh);                                                // changes integer to String
                Log.d("-- TA onItemSel --",s);                                          // Logs refresh value

                if (btSocket != null) {                                                     // if the btSocket is busy
                    Log.d("-- TA onItemSel --", "socket is not null");

                    try {
                        btSocket.getOutputStream().write(item.toString().getBytes());       // send String from spinner to server ("Red", "Green", "Blue" or "No Colour")
                        Log.d("-- TA onItemSel --", "btSocket stream");
                        //Log.d("Backward", "BACKWARD");
                    } catch (IOException e) {
                        Log.d("-- TA onItemSel --", "error");
                        msg("Error reading colours");                                                    // if error thrown, a Toast will be shown
                    }
                }
            }
        }
        refresh += 1;                                                                       // increment value
    }

    // must be override because class implements AdapterView.OnItemSelectedListener
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    //------------------------ ConnectBT class STARTS HERE -----------------------------------------

    private class ConnectBT extends AsyncTask<Void, Void, Void>  {                          // UI thread

        private boolean ConnectSuccess = true;                                              // if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            Log.d("-- TRAIN ACTIVITY --", "onPreExecute");

        }

        @Override
        protected Void doInBackground(Void... devices) {                                    // while the progress dialog is shown, the connection is done in background
            Log.d("-- TRAIN ACTIVITY --", "doInBackground started");

            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();                      // get the mobile bluetooth device
                    BluetoothDevice remote = myBluetooth.getRemoteDevice(address);           // connects to the device's address and checks if it's available
                    btSocket = remote.createInsecureRfcommSocketToServiceRecord(myUUID);     // create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();                  // cancel the current device discovery process
                    btSocket.connect();                                                      // start connection
                }
                if(isCancelled()){
                    Log.d("-- TRAIN ACTIVITY --", "Canceled triggered");
                }
            }
            catch (IOException e) {
                ConnectSuccess = false;                                                      // if the try failed, you can check the exception here
                Log.d("-- TRAIN ACTIVITY --","Exception thrown");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {                                          //after the doInBackground, it checks if everything went fine
            Log.d("-- TRAIN ACTIVITY --", "onPostExcute started");
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");              // when problem occurs with connection a Toast is thrown
                finish();
            }
            else {
                msg("Train Mode Entered");                                                        // when connection successful Train Mode is Shown through Toast
                isBtConnected = true;
            }
        }
    }
}