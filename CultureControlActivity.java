package pt.ipp.isep.dei.formacao.android.culturecontroler;

import com.google.gson.Gson;

import pt.ipp.isep.dei.formacao.android.culturecontroler.CultureControlService.CultureControlServiceBinder;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CultureControlActivity extends Activity implements OnClickListener {
	
	private TextView txtTemp1;
	private TextView txtTemp2;
	private TextView txtTemp3;
	
	private TextView txtHum1;
	private TextView txtHum2;
	private TextView txtHum3;
	
	private TextView txtHumg1;
	private TextView txtHumg2;
	private TextView txtHumg3;
	
	private Button btnRefresh;
	
	private ToggleButton tgButton;

	private TextView txtBluetoothStatus;
	
	public static final String TAG = CultureControlActivity.class.getSimpleName();
	private CultureControlService mControlService;
	private BluetoothDevice device;
	
	private Motor m = null;
	private Sensor st1 = null;
	private Sensor st2 = null;
	private Sensor st3 = null;
	private Sensor sh1 = null;
	private Sensor sh2 = null;
	private Sensor sh3 = null;
	private Sensor shg1 = null;
	private Sensor shg2 = null;
	private Sensor shg3 = null;
	
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.motor_control);
	
	    //Intent i = getIntent();
	    //device = i.getExtras().getParcelable(CultureControlService.DEVICE_EXTRA);
	    //if (device == null) {
	    	
	    	
	    	SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
			Gson gson = new Gson();
			String json = appSharedPrefs.getString("bt_device", "");
			device = gson.fromJson(json, BluetoothDevice.class);
	    	
	    	if(device == null){
	    		Log.e(TAG, "A BluetoothDevice must be sent along with the startActivity Intent!");
	    		finish();
	    		return;
	    	}
	    //}
	    
	    txtTemp1 = (TextView) findViewById(R.id.txtTemp1);
	    txtTemp2 = (TextView) findViewById(R.id.txtTemp2);
	    txtTemp3 = (TextView) findViewById(R.id.txtTemp3);

	    txtHum1 = (TextView) findViewById(R.id.txtHum1);
	    txtHum2 = (TextView) findViewById(R.id.txtHum2);
	    txtHum3 = (TextView) findViewById(R.id.txtHum3);

	    txtHumg1 = (TextView) findViewById(R.id.txtHumg1);
	    txtHumg2 = (TextView) findViewById(R.id.txtHumg2);
	    txtHumg3 = (TextView) findViewById(R.id.txtHumg3);

		txtBluetoothStatus = (TextView) findViewById(R.id.txtBluetoothStatus);

		tgButton = (ToggleButton) findViewById(R.id.motor_button);
		tgButton.setText(R.string.txt_motor_off);
		tgButton.setOnClickListener(this);
		
		btnRefresh = (Button) findViewById(R.id.read_sensors);
		btnRefresh.setOnClickListener(this);

		
		MyDbHelper dbHelper = new MyDbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		
		m = Motor.setMotor(10, db);		
		st1 = Sensor.setSensor(1, db);
		st2 = Sensor.setSensor(2, db);
		st3 = Sensor.setSensor(3, db);
		sh1 = Sensor.setSensor(4, db);
		sh2 = Sensor.setSensor(5, db);
		sh3 = Sensor.setSensor(6, db);
		shg1 = Sensor.setSensor(7, db);
		shg2 = Sensor.setSensor(8, db);
		shg3 = Sensor.setSensor(9, db);
	
		db.close();
		
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	    Intent bindIntent = new Intent(this, CultureControlService.class);
	    bindIntent.putExtra(CultureControlService.DEVICE_EXTRA, device);
	    getApplicationContext().bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		MyDbHelper dbHelper = new MyDbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		
		m = Motor.setMotor(10, db);		
		st1 = Sensor.setSensor(1, db);
		st2 = Sensor.setSensor(2, db);
		st3 = Sensor.setSensor(3, db);
		sh1 = Sensor.setSensor(4, db);
		sh2 = Sensor.setSensor(5, db);
		sh3 = Sensor.setSensor(6, db);
		shg1 = Sensor.setSensor(7, db);
		shg2 = Sensor.setSensor(8, db);
		shg3 = Sensor.setSensor(9, db);
		
		db.close();
		
		
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
	    super.onStop();
	    getApplicationContext().unbindService(mConnection);
	}
	
	
	private void handleStateMsg(int state) {
		switch(state) {
		case CultureControlService.STATE_CONNECTED:
			txtBluetoothStatus.setText(getString(R.string.bt_connected_to) + " " + device.getName());
			break;
		case CultureControlService.STATE_DISCONNECTED:
			txtBluetoothStatus.setText(R.string.bt_disconnected);
			break;
		case CultureControlService.STATE_ERROR:
			txtBluetoothStatus.setText(R.string.bt_error);
			break;
		}
	}
	
	
	private ServiceConnection mConnection = new ServiceConnection() {
		// executado quando o onBind ou onRebind do serviço é concluido
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mControlService = ((CultureControlServiceBinder) service).getService();
			mControlService.setHandler(mHandler);
			handleStateMsg(mControlService.getState());
			//mControlService.requestSensor1Temperature();
			
		}
		// executado quando a ligação ao serviço é destruída
		// normalmente acontece quando o serviço falha ou é morto
		public void onServiceDisconnected(ComponentName className) {
			mControlService = null;
			mControlService.setHandler(null);
		}
		
	};
	

	private final Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			
			int id_device_1 = 0;
			int id_device_2 = 0;
			int id_device_3 = 0;
			
			int duration = 0;
			
			DateContainer dc = new DateContainer(DateContainer.DateContainerType.DATE);
			String date = dc.getDateTimeString();
			
			float value_1 = -1000;
			float value_2 = -1000;
			float value_3 = -1000;
			
			
			int state = msg.arg1;
			handleStateMsg(state);
			
			Context c = getApplicationContext();
			
			MyDbHelper dbHelper = new MyDbHelper(c);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			switch (msg.what) {
			case CultureControlService.MESSAGE_RECEIVED:
				byte[] readBuf = (byte[]) msg.obj;
				
				if (readBuf[0] == Global.MESSAGE_TYPE_RESPONSE_LAST_VALUES) {
				
							//sensor temperatura
						
						if(readBuf[1] == Global.SENSOR_1_TEMPERATURE_SENSOR){
							txtTemp1.setText("" + (readBuf[2] & 0xFF));
							try {
								value_1=Float.parseFloat((String) txtTemp1.getText());
								id_device_1 = 1;
								st1.setValue(value_1);
								st1.update(db);
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					
							//sensor humidade
						if(readBuf[1] == Global.SENSOR_1_HUMIDITY_SENSOR){
							txtHum1.setText("" + (readBuf[2] & 0xFF));
							try {
								value_2=Float.parseFloat((String) txtHum1.getText());
								id_device_2 = 4;
								sh1.setValue(value_2);
								sh1.update(db);
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						}
						if(readBuf[1] == Global.SENSOR_1_HUMIDITY_G_SENSOR){
							//sensor humidade solo
							txtHumg1.setText("" + (readBuf[2] & 0xFF));
							try {
								value_3=Float.parseFloat((String) txtHumg1.getText());
								id_device_3 = 7;
								shg1.setValue(value_3);
								shg1.update(db);
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
							//motor
							
						if (readBuf[1] == Global.SENSOR_TYPE_ACTUATOR) {
							if(readBuf[2] == 0){
								tgButton.setChecked(false);
								tgButton.setText(R.string.txt_motor_off);
								m.setState(0);
							} else {
								tgButton.setChecked(true);
								tgButton.setText(R.string.txt_motor_on);
								m.setState(1);
							}
						}
						if (readBuf[1] == Global.SENSOR_WATER_LEVEL) {
							if(readBuf[2] == 0)
								m.setWaterLevel(0);
						} else {
								m.setWaterLevel(1);
						}
						try {
							m.update(db);
						} catch (Exception e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
				//return true;
			}
			if(id_device_1 != 0 && value_1!=-1000){
				Leitura l_1 = new Leitura(0, id_device_1, date, duration, value_1);
				if(l_1 != null){
					try {
						l_1.insert(db);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast t = Toast.makeText(c,
							"Leitura ST1 gravada.",
			                Toast.LENGTH_LONG);
			        t.show();
				}
			}
			if(id_device_2 != 0 && value_2!=-1000){
				Leitura l_2 = new Leitura(0, id_device_2, date, duration, value_2);
				if(l_2 != null){
					try {
						l_2.insert(db);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast t = Toast.makeText(c,
							"Leitura SH1 gravada.",
			                Toast.LENGTH_LONG);
			        t.show();
				}
			}
			if(id_device_3 != 0 && value_3!=-1000){
				Leitura l_3 = new Leitura(0, id_device_3, date, duration, value_3);
				if(l_3 != null){
					try {
						l_3.insert(db);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast t = Toast.makeText(c,
							"Leitura SGH1 gravada.",
			                Toast.LENGTH_LONG);
			        t.show();
				}
			}
			
			db.close();
		
			return true;
			
		}

		
	});
	
	
	@Override
	public void onClick(View v) {
		if (mControlService != null) {
			switch (v.getId()) {
			case R.id.motor_button:
				mControlService.setMotorState(tgButton.isChecked());
				if(tgButton.isChecked()){
					tgButton.setText(R.string.txt_motor_on);
				}else{
					tgButton.setText(R.string.txt_motor_off);
				}
//				mControlService.requestGardenLightState(); // para manter a sincronização
				break;
			case R.id.read_sensors:
				mControlService.requestSensorTemperature();
				mControlService.requestSensorHumidity();
				mControlService.requestSensorHumidityG();
				mControlService.requestMotorState();
				mControlService.requestWaterLevel();
				break;
			}
		}
	}

	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    //Log.d(TAG, "onCreateOptionsMenu called.");

	    menu.setQwertyMode(true);

	    // Menu.add(int groupId, int itemId, int order, int titleRes)

	    // opção "parâmetros" 
	    MenuItem mnu1 = menu.add(0, 0, 0, R.string.config_label);
	    mnu1.setAlphabeticShortcut('c');
	    mnu1.setIcon(R.drawable.ic_menu_config);

	    // opção "lista de leituras" 
	    MenuItem mnu2 = menu.add(0, 1, 0, R.string.regist_label);
	    mnu2.setAlphabeticShortcut('r');
	    mnu2.setIcon(R.drawable.ic_menu_list);
	    
	    // opção programas
	    MenuItem mnu3 = menu.add(0, 2, 0, R.string.program_label);
	    mnu3.setAlphabeticShortcut('p');
	    mnu3.setIcon(R.drawable.ic_menu_list);

	    return super.onCreateOptionsMenu(menu);
	}

	// executado quando o utilizador selecciona um item no menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case 0: // configurações (lança intent para avançar para ParametersActivity)
	    	// Parâmetros configuraveis
	    	
	    	Intent newIntentp = new Intent(CultureControlActivity.this,
	                ParametersActivity.class);
	        startActivity(newIntentp);
	        
	    return true;

	    case 1: // registo de leituras (lança intent para avançar para LeiturasListActivity)
	    	
	    	Intent newIntentl = new Intent(CultureControlActivity.this,
	                LeituraListActivity.class);
	        startActivity(newIntentl);
	    return true;
	    	
	    case 2: // lista de programas (lança intent para avançar para LocalListActivity)
	        
	    	Intent newIntentpr = new Intent(CultureControlActivity.this,
	                ProgramListActivity.class);
	        startActivity(newIntentpr);
	        
	    return true;
	    }

	    return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	

	
	
}

