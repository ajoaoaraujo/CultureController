package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import pt.ipp.isep.dei.formacao.android.culturecontroler.BluetoothToolkit.BluetoothToolKitListener;


import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
//import android.provider.Settings.Global;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class DeviceListActivity extends ListActivity implements OnClickListener, BluetoothToolKitListener {
	    
    //interface views
    private Button scanButton;
    
    private BluetoothAdapter bluetoothAdapter;
    
    private DeviceListAdapter deviceAdapter;
    private List<BluetoothDevice> deviceArray = new ArrayList<BluetoothDevice>();
    
    private BluetoothToolkit btToolkit;
    
    private void refreshDevices() {
    	deviceArray.clear();
    	
    	deviceArray.addAll(btToolkit.getPairedDevices());
    	
    	List<BluetoothDevice> discoveredDevices = btToolkit.getDiscoveredDevices();
    	for(BluetoothDevice device : discoveredDevices) {
    		if(!deviceArray.contains(device))
    			deviceArray.add(device);
    	}
    	
    	deviceAdapter.notifyDataSetChanged();
    }
   
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	    requestWindowFeature(Window.FEATURE_PROGRESS);
	    setContentView(R.layout.device_list);
	    
	    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    if(bluetoothAdapter == null) {
	    	Toast.makeText(this, R.string.bluetooth_n_a, Toast.LENGTH_LONG).show();
	    	finish();
	    	return;
	    }
	    
	    btToolkit = new BluetoothToolkit(this, bluetoothAdapter);
	    btToolkit.setListener(this);
	    
	    scanButton = (Button) findViewById(R.id.button_scan);
	    scanButton.setOnClickListener(this);

	    // dispositivos emparelhados
	    deviceAdapter = new DeviceListAdapter(this, deviceArray);
        setListAdapter(deviceAdapter);  
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(bluetoothAdapter.isEnabled()){
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, Global.REQUEST_ENABLE_BT);
		
		}
		
		refreshDevices();
	}
	
	@Override
	protected void onPause() {
		btToolkit.cancelDeviceDiscovery();
		super.onPause();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.button_scan) {
			if(btToolkit.startDeviceDiscovery()){
				v.setEnabled(false);
				setProgressBarIndeterminateVisibility(true);
				setTitle(R.string.scanning);
			}
		}	
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		BluetoothDevice device = deviceArray.get(position);
		
		
		/* use json library to put an object device in the SharedPreferences */
		SharedPreferences appSharedPrefs = PreferenceManager
				  .getDefaultSharedPreferences(this.getApplicationContext());
		Editor prefsEditor = appSharedPrefs.edit();
		Gson gson = new Gson();
		String json = gson.toJson(device);
		prefsEditor.putString("bt_device", json);
		prefsEditor.commit();
		
		Intent intent = new Intent(this, CultureControlActivity.class);
		intent.putExtra(CultureControlService.DEVICE_EXTRA, device);
		startActivity(intent);
	}

	@Override
	public void discoveryComplete(List<BluetoothDevice> devices) {
		scanButton.setEnabled(true);
		refreshDevices();
		setProgressBarIndeterminateVisibility(false);
		setTitle(R.string.select_device);
	}

	
}


