package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.google.gson.Gson;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class CultureControlService extends Service {
	
	
	private static final String TAG = CultureControlService.class.getSimpleName();
	
	public static final String DEVICE_EXTRA = "BT_DEVICE";
	
	public static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // SPP
	// constants that indicate the current connection state
	
	public static final int STATE_DISCONNECTED = 0; // we're doing nothing
	
	public static final int STATE_CONNECTED = 1; // now connected to a remote device
	
	public static final int STATE_ERROR = 2;
	
	public static final int MESSAGE_STATE_CHANGE = 1;
	
	public static final int MESSAGE_RECEIVED = 2;
	
	private final IBinder mBinder = new CultureControlServiceBinder();
	
	
	
	
	private BluetoothSocket mSocket; // ligação ao controlador
	
	private BluetoothDevice mBluetoothDevice; // controlador
	
	private Handler mHandler; // handler para o envio de mensagens para a HomeControlActivity (na UI Thread)
	
	private int mState; // guarda o estado do serviço
	
	private InputStream mInStream = null; // InputStream da ligação ao controlador
	
	private OutputStream mOutStream = null; // OutputStream da ligação ao controlador
	
	private ReceiveThread mReceiveThread; // Thread de recepção de dados do controlador
	
	public ReceiveLeiturasThread mLeiturasThread; // Thread de recepção de dados do controlador
	
	private SendProgramsAsyncTask asyncTask = null;
	
	
	public class CultureControlServiceBinder extends Binder {
		public CultureControlService getService() {
			return CultureControlService.this;
		}
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		setState(STATE_DISCONNECTED);
	
	}
	
	
	@Override
	public IBinder onBind(Intent i) {
	
		mBluetoothDevice = i.getExtras().getParcelable(DEVICE_EXTRA);
		if(mBluetoothDevice == null) {
			
			SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
			Gson gson = new Gson();
			String json = appSharedPrefs.getString("bt_device", "");
			mBluetoothDevice = gson.fromJson(json, BluetoothDevice.class);
			
			if(mBluetoothDevice == null){
	
				Log.e(TAG, "A BluetoothDevice must be sent along with the bindService Intent!");
				return mBinder;
			}
		}
	
		connect();
		return mBinder;
	
	}
	
	
	@Override
	public boolean onUnbind(Intent intent) {
		
		disconnect();
		return true;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private synchronized void setState(int state) {
		mState = state;
		if(mHandler != null)
			mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, 0).sendToTarget();
	}
	
	
	

	private boolean connect() {
		
		setState(STATE_DISCONNECTED);
		
		if(mBluetoothDevice != null) {
			
			try {
				//mSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(DEVICE_UUID);
				
				mSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(DEVICE_UUID);
			
			} catch (IOException e) {
			
				Log.e(TAG, "Unable to create RFCOMM socket!", e);
				setState(STATE_ERROR);
				return false;
			}
			
			try {
			
				mSocket.connect();
			
			} catch (IOException e) {
				
				Log.e(TAG, "Unable to connect RFCOMM socket!", e);
				setState(STATE_ERROR);
		
				try {
				
					mSocket.close();
			
				} catch (IOException closeException) { }
			
				return false;
			
			}
		
			try {
			
				mInStream = mSocket.getInputStream();
				mOutStream = mSocket.getOutputStream();
		
			} catch (IOException e) {
		
				Log.e(TAG, "Unable to open IO streams from RFCOMM socket!", e);
				setState(STATE_ERROR);
		
				try {
					mSocket.close();
			
				} catch (IOException closeException) { }
		
				return false;
			}
		
			setState(STATE_CONNECTED);
		
			mReceiveThread = new ReceiveThread(this, mInStream);
		
			mReceiveThread.start();
			
			
			mLeiturasThread = new ReceiveLeiturasThread(this, mInStream);
			
			//mLeiturasThread.start();
		
			return true;
	
		}
	
		return false;
		
	}
	
	
	private void disconnect() {
		
		if(mReceiveThread != null && mReceiveThread.getState() != Thread.State.TERMINATED && !mReceiveThread.isCanceled())
		mReceiveThread.cancel();
		
		if(mLeiturasThread != null && mLeiturasThread.getState() != Thread.State.TERMINATED && !mLeiturasThread.isCanceled())
		mLeiturasThread.cancel();
		
		if(asyncTask != null && asyncTask.getStatus() != AsyncTask.Status.FINISHED)
			asyncTask.cancel(true);
		asyncTask = null;
		
		try {
		
			if(mInStream != null) mInStream.close();
			if(mOutStream != null) mOutStream.close();
			if(mSocket != null) mSocket.close();
		
			mInStream = null;
			mOutStream = null;
			mSocket = null;
			mReceiveThread = null;
			mLeiturasThread = null;
			mBluetoothDevice = null;
		
		} catch (IOException e) {
		
			Log.e(TAG, "Error disconnecting socket", e);
		}
		
	}

	
	
	
	public int getState() {
		return mState;
	}
	
	public Handler getHandler() {
		return mHandler;
	}
	
	public void setHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
	
	
	
	
	
	
	
	public void receivedMsg(byte[] msg) {
	
		//Log.d(TAG, "RCVD # Tipo Mensagem: " + (msg[0]&0xFF) + " - " + "Sensor: " + (msg[1]&0xFF) + " - " + "SensorNumber:" + (msg[2]&0xFF) + " - " + "SensorData:" + (msg[3]&0xFF));
		
		
		if(msg[0]==Global.MESSAGE_TYPE_RESPONSE_LAST_VALUES){
			
			byte[] b = new byte[3];
			
			for(int i=0;i<3;i++) b[i] = msg[i];
		
			mHandler.obtainMessage(CultureControlService.MESSAGE_RECEIVED, mState, 0, b).sendToTarget();
		}
		
		if(msg[0]==Global.MESSAGE_TYPE_RESPONSE_SENSOR_GROUP_1_HISTORIC_VALUES ||
			msg[0]==Global.MESSAGE_TYPE_RESPONSE_MOTOR_HISTORIC_VALUES){
			
			byte[] b = new byte[27];
			
			for(int i=0;i<28;i++) b[i] = msg[i];
			
			mHandler.obtainMessage(CultureControlService.MESSAGE_RECEIVED, mState, 0, b).sendToTarget();
		}
	}
	
	public void receivedLeiturasMsg(byte[] msg) {
		
		//Log.d(TAG, "RCVD # Tipo Mensagem: " + (msg[0]&0xFF) + " - " + "Sensor: " + (msg[1]&0xFF) + " - " + "SensorNumber:" + (msg[2]&0xFF) + " - " + "SensorData:" + (msg[3]&0xFF));
		
		byte[] b = new byte[27];
		for(int i=0;i<28;i++){
			b[i] = msg[i];
		}
		mHandler.obtainMessage(CultureControlService.MESSAGE_RECEIVED, mState, 0, b).sendToTarget();
	}
	

	
	
	public void receivedDone(int errorCount) {
	
		Log.d(TAG, "Remove device has disconnected!");
		//setState(STATE_DISCONNECTED);
	
	}
	
	
	public boolean startSending() {
		
		try {
			
			asyncTask = new SendProgramsAsyncTask(this);
			asyncTask.execute();
			
		} catch (Exception e) {
			
			return false;
		}
		
		return true;
	}
	
	
	public void sendDone(int s) {
		
		if(s == 1){
		
			Toast t = Toast.makeText(this,
					"Program Send sucessfully.",
	                Toast.LENGTH_LONG);
	        t.show();
			
			Log.d(TAG, "Remove device has disconnected!");

		}
		//setState(STATE_DISCONNECTED);
	
	}
	

	
	public void send_parameters(Parameters p1,Parameters p2,Parameters p3){
		
		
		String msg = "2" + " " + String.valueOf(p1.getValue()) + " " + String.valueOf(p2.getValue()) + " " + String.valueOf(p3.getValue());
		byte[] command = msg.getBytes();
		
		sendMsg(command);

	}
	
	public void requestSensors() {
		byte[] command = { Global.MESSAGE_TYPE_REQUEST_LAST_VALUES, 0x00, 0x00, 0x00 };
		sendMsg(command);
	}
	
	
	public void setMotorState(boolean on) {
		byte[] command = { Global.MESSAGE_TYPE_ACTION, (byte) (on ? Global.SET_MOTOR_ON : Global.SET_MOTOR_OFF) };
		sendMsg(command);
	}
	
	
	//send request
	public void requestLeitura(int c) {
		byte[] command = { Global.MESSAGE_TYPE_REQUEST_HISTORIC_VALUES, (byte) c };//3 ou 4 parametros?
		sendMsg(command);
	}

	public void requestSensorTemperature() {
		byte[] command = { Global.MESSAGE_TYPE_REQUEST_LAST_VALUES, Global.SENSOR_1_TEMPERATURE_SENSOR };
		sendMsg(command);
	}

	public void requestSensorHumidity() {
		byte[] command = { Global.MESSAGE_TYPE_REQUEST_LAST_VALUES, Global.SENSOR_1_HUMIDITY_SENSOR };
		sendMsg(command);
	}

	public void requestSensorHumidityG() {
		byte[] command = { Global.MESSAGE_TYPE_REQUEST_LAST_VALUES, Global.SENSOR_1_HUMIDITY_G_SENSOR };
		sendMsg(command);
	}
	
	public void requestMotorState() {
		byte[] command = { Global.MESSAGE_TYPE_REQUEST_LAST_VALUES, Global.SENSOR_TYPE_ACTUATOR };
		sendMsg(command);
	}

	public void requestWaterLevel() {
		byte[] command = { Global.MESSAGE_TYPE_REQUEST_LAST_VALUES, Global.SENSOR_WATER_LEVEL };
		sendMsg(command);
	}
	
	
	public void sendProgram(int hour, int min, int dur) {
		
		String msg = "3" + " " + String.valueOf(hour) + " " + String.valueOf(min) + " " + String.valueOf(dur);
		
		byte[] command = msg.getBytes();
		sendMsg(command);
	}
	
	public void sendProgramReset() {
		
		byte[] command = { Global.MESSAGE_TYPE_SEND_PROGRAMS_RESET };
		sendMsg(command);
	}
	
	
	public void sendMsg(byte[] command) {
		if(mOutStream != null) {
			try {
				mOutStream.write(command);
				
			} catch (IOException e) {
				Log.e(TAG, "Error sending message!", e);
			}
		}
	}
	
	
	
}


