package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MenuActivity extends AppCompatActivity {

    private Button btn_tra, btn_lea, btn_ext;
    String address = "B8:27:EB:BD:37:5C";                                                   // RaspberryPi MAC address
    private ProgressDialog progress;                                                        // progress Dialog shows progress on establishing connection
    public BluetoothAdapter myBluetooth = null;                                             // BT adaptor
    BluetoothSocket btSocket = null;                                                        // BT socket
    private boolean isBtConnected = false;                                                  // flag that keeps track on connection status
    static final UUID myUUID = UUID.fromString("1e0ca4ea-299d-4335-93eb-27fcfe7fa848");     // SPP Universally unique identifier - 128-bit number


    //--------------------------------- onCreate ---------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // that triggers the Thread that works in the background and is responsible for BT connection
        new ConnectBT().execute();

        btn_tra = findViewById(R.id.btn_train);         // finds the views from the layout resource file
        btn_lea = findViewById(R.id.btn_learn);
        btn_ext = findViewById(R.id.btn_exit);

        // loads the TrainActivity and takes user to Train Mode
        btn_tra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btSocket!=null) {
                    try {
                        btSocket.getOutputStream().write("TRAIN".getBytes());   // send "TRAIN" String to server
                    }
                    catch (IOException e) {
                        msg("Error");
                    }
                }
                Intent myIntent = new Intent(MenuActivity.this, TrainActivity.class);
                // myIntent.putExtra(ConnectActivity.EXTRA_ADDRESS, address); //Optional parameters
                // Log.d("--ADDRESS: ",address);
                MenuActivity.this.startActivity(myIntent);
            }
        });

        // loads the LearnActivity and takes user to Learn Mode
        btn_lea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btSocket!=null) {
                    try {
                        btSocket.getOutputStream().write("LEARN".getBytes());       // send "LEARN" String to server
                    }
                    catch (IOException e) {
                        msg("Error");
                    }
                }
                Intent myIntent = new Intent(MenuActivity.this, LearnActivity.class);
                // myIntent.putExtra(ConnectActivity.EXTRA_ADDRESS, address); //Optional parameters
                // Log.d("--ADDRESS: ",address);
                MenuActivity.this.startActivity(myIntent);
            }
        });

        // leaves application
        btn_ext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btSocket!=null) {                                               // if the btSocket is busy
                    try {
                        btSocket.getOutputStream().write("DISCONNECT".getBytes());  // send "DISCONNECT" String to server
                        btSocket.close();                                           // close socket
                        new ConnectBT().cancel(true);               // close connection
                    }
                    catch (IOException e) {
                        msg("Error during disconnection");
                    }
                }
                finishAffinity();                                                    // exit the application
            }
        });
    }
    //--------------------------------- onCreate ENDS HERE -----------------------------------------


    // method to use Toast Messaging system
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


    //------------------------ ConnectBT class STARTS HERE -----------------------------------------

    private class ConnectBT extends AsyncTask<Void, Void, Void>  {                              // UI thread
        private boolean ConnectSuccess = true;                                                  // if it's here, it's almost connected
                                                                                                // flags if connection is on/off
        @Override
        protected void onPreExecute() {
            Log.d("-- MENU ACTIVITY --", "onPreExecute");
            progress = ProgressDialog.show(MenuActivity.this, "Connecting...", "Please wait!!!");  // show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices){                                         // while the progress dialog is shown, the connection is done in background
            Log.d("-- MENU ACTIVITY --", "doInBackground started");

            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();                         // get the mobile bluetooth device
                    BluetoothDevice remote = myBluetooth.getRemoteDevice(address);              // connects to the device's address and checks if it's available
                    btSocket = remote.createInsecureRfcommSocketToServiceRecord(myUUID);        // create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();                     // cancel the current device discovery process
                    btSocket.connect();                                                         // start connection
                }
                if(isCancelled()){
                    Log.d("-- MENU ctivity --", "Canceled triggered");
                }
            }
            catch (IOException e) {
                ConnectSuccess = false;                                                         // if the try failed
                Log.d("-- MENU ACTIVITY --","Exception thrown");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)    {                                           // after the doInBackground, it checks if everything went fine
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}