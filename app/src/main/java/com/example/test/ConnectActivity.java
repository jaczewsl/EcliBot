package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class ConnectActivity extends AppCompatActivity {

    // widgets
    Button btnPaired;                                           // set to trigger the paired devices
    ListView devicelist;                                        // all paired devices will be shown here

    // bluetooth
    private BluetoothAdapter myBluetooth = null;                // is required for any and all Bluetooth activity
    private Set<BluetoothDevice> pairedDevices;                 // all paired devices will be stores here
//    public static String EXTRA_ADDRESS = "MAC";                 // my RaspberryPi MAC address


    //--------------------------------- onCreate ---------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        // calling widgets
        btnPaired = findViewById(R.id.button);                  // finds the view from the layout resource file
        devicelist = findViewById(R.id.listView);               // that are attached with current Activity

        // if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();     // returns BTAdapter that represents the device's
                                                                // own Bluetooth adapter
        if(myBluetooth == null) {
            // show a message that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            // finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled()) {
            // ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        // show the paired devices in a list view by calling method pairedDevicesList
        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });
    }

    //--------------------------------- onCreate ENDS HERE -----------------------------------------

    //
    private void pairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();     // get a set of BTDevices objects representing all paired devices
        ArrayList list = new ArrayList();                   // all paired devices will be stored here

        if (pairedDevices.size()>0) {
            for(BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress()); // get the device's name and the address and add it to the ArrayList 'list'
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        // array adapter to provide the view
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); // method called when the device from the list is clicked
    }

    // loads new MenuActivity once the user pressed the device from the device list
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            Log.d("MAC address: ", address);

            // make an intent to start next activity.
            Intent i = new Intent(ConnectActivity.this, MenuActivity.class);
//            i.putExtra(EXTRA_ADDRESS, address);                                             //this will be received at MenuActivity
            startActivity(i);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}