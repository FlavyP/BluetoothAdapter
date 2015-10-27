package thirdsem.flavy.bluetoothandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button refreshButton;
    private Button enableBtButton;
    private Button discoverBtDevicesButton;
    private TextView phoneDetails;
    private TextView phoneBondedDevices;
    private ListView devicesList;

    private ArrayList<String> pairedDevices = new ArrayList<String>();
    private Set<BluetoothDevice> bondedDevices;

    private BluetoothAdapter BA;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponents();
        checkBluetooth();
    }

    private void initializeComponents () {
        refreshButton = (Button) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);
        enableBtButton = (Button) findViewById(R.id.enableBtButton);
        enableBtButton.setOnClickListener(this);
        discoverBtDevicesButton = (Button) findViewById(R.id.discoverBtDevicesButton);
        discoverBtDevicesButton.setOnClickListener(this);

        phoneDetails = (TextView) findViewById(R.id.phoneDetails);
        phoneBondedDevices = (TextView) findViewById(R.id.phoneBondedDevices);
        devicesList = (ListView) findViewById(R.id.devicesList);

        BA = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.enableBtButton:
                if (!BA.isEnabled()) {
                    Intent turnBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnBluetoothOn, 0);
                    phoneDetails.append("\nBT is enabled on phone.");
                } else {
                    phoneDetails.append("\nBT is already enabled on phone.");
                }
                checkBondedDevices();
                break;
            case R.id.discoverBtDevicesButton:
                BA.startDiscovery();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(bluetoothReceiver, filter);
                break;
            case R.id.refreshButton:
                phoneDetails.setText("");
                checkBluetooth();
                phoneBondedDevices.setText("");
                devicesList.setAdapter(null);
                break;
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive (Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                pairedDevices.add(device.getName() + " " + device.getAddress());
                devicesList.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, pairedDevices));
            }
        }
    };

    private void checkBluetooth () {
        if (BA != null) {
            phoneDetails.setText("Phone supports BT");
        } else {
            phoneDetails.setText("Phone does not support BT");
        }
    }

    private void checkBondedDevices () {
        bondedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();

        for (BluetoothDevice bt : bondedDevices) {
            String device = bt.getName() + " " + bt.getAddress();
            list.add(device);
        }
        for (int i = 0; i < list.size(); i++)
            phoneBondedDevices.setText("" + list.get(i) + "\n");
    }
}
