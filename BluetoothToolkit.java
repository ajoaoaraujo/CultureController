package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothToolkit {
	
	public static interface BluetoothToolKitListener {
		public void discoveryComplete(List<BluetoothDevice> devices);
	}
	
	public BluetoothToolKitListener getListener() {
		return listener;
	}

	public void setListener(BluetoothToolKitListener listener) {
		this.listener = listener;
	}

	private BluetoothToolKitListener listener;

	private Context context;
	private BluetoothAdapter adapter;
	
	private List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
	private List<BluetoothDevice> pairedDevices = new ArrayList<BluetoothDevice>();
	
	private final IntentFilter discoveryIntentFilter = new IntentFilter();
	
	private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
		
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			
			if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				
			} else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				discoveredDevices.clear();
			} else if(BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				discoveredDevices.add(device);
			} else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				adapter.cancelDiscovery();
				context.unregisterReceiver(this);
				if(listener != null) listener.discoveryComplete(discoveredDevices);
			}
			
		}
	};
	
	public BluetoothToolkit(Context context, BluetoothAdapter adapter) {
		this.context = context;
		this.adapter = adapter;
			
		discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		discoveryIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	}
	
	public List<BluetoothDevice> getPairedDevices() {
		pairedDevices.clear();
		pairedDevices.addAll(adapter.getBondedDevices());
		return pairedDevices;
	}
	
	public List<BluetoothDevice> getDiscoveredDevices(){
		return discoveredDevices;
	}
	
	public boolean startDeviceDiscovery() {
		if(!adapter.isEnabled()) {
			return false;
		}
		
		if(adapter.isDiscovering()) {
			adapter.cancelDiscovery();
		}
		
		context.registerReceiver(discoveryReceiver, discoveryIntentFilter);
		
		adapter.startDiscovery();
		
		return true;
		
	}
	
	public void setPairedDevices(List<BluetoothDevice> pairedDevices) {
		this.pairedDevices = pairedDevices;
	}

	public void cancelDeviceDiscovery() {
		try { context.unregisterReceiver(discoveryReceiver); }
		catch(Exception e) {}
		adapter.cancelDiscovery();
	}
}
