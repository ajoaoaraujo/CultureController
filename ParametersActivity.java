package pt.ipp.isep.dei.formacao.android.culturecontroler;

import pt.ipp.isep.dei.formacao.android.culturecontroler.CultureControlService.CultureControlServiceBinder;

import com.google.gson.Gson;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ParametersActivity extends Activity {
	
	
	public static final String TAG = ParametersActivity.class.getSimpleName();

	private TextView txt_parameter1;
	private TextView txt_parameter2;
	private TextView txt_parameter3;
	
	private EditText parameter1;
	private EditText parameter2;
	private EditText parameter3;
	
	private Button submitBtn;
	
	

	
	private TextView txtBluetoothStatus;
	
	private CultureControlService mCultureService;
	
	private BluetoothDevice device;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.parameters_layout);
	    
	    //initialize bluetooth device
	    SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		Gson gson = new Gson();
		String json = appSharedPrefs.getString("bt_device", "");
		device = gson.fromJson(json, BluetoothDevice.class);
    	
    	if(device == null){
    		Log.e(TAG, "A BluetoothDevice must be sent along with the startActivity Intent!");
    		finish();
    		return;
    	}
    	
    	txtBluetoothStatus = (TextView) findViewById(R.id.txtBluetoothStatus);
	    
	    
	    txt_parameter1=(TextView)findViewById(R.id.id_txt_parameter1);
	    txt_parameter2=(TextView)findViewById(R.id.id_txt_parameter2);
	    txt_parameter3=(TextView)findViewById(R.id.id_txt_parameter3);
	    
	    txt_parameter1.setText(Parameters.getTypes(0));
	    txt_parameter2.setText(Parameters.getTypes(1));
	    txt_parameter3.setText(Parameters.getTypes(2));
	    
	    parameter1=(EditText)findViewById(R.id.id_parameter1);
	    parameter2=(EditText)findViewById(R.id.id_parameter2);
	    parameter3=(EditText)findViewById(R.id.id_parameter3);
	    
	    
	    MyDbHelper dbHelper = new MyDbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		  
		Parameters p_1 = Parameters.get(1, db);
		Parameters p_2 = Parameters.get(2, db);
		Parameters p_3 = Parameters.get(3, db);
		
		db.close();
	
	    parameter1.setText(String.valueOf(p_1.getValue()));
	    parameter2.setText(String.valueOf(p_2.getValue()));
	    parameter3.setText(String.valueOf(p_3.getValue()));
	    
	    addListenerOnButton();
	    
	    
	    
	    
	    
	    // TODO Auto-generated method stub
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
			mCultureService = ((CultureControlServiceBinder) service).getService();
			mCultureService.setHandler(mHandler);
			handleStateMsg(mCultureService.getState());
			
		}
		// executado quando a ligação ao serviço é destruída
		// normalmente acontece quando o serviço falha ou é morto
		public void onServiceDisconnected(ComponentName className) {
			mCultureService = null;
			//mControlService.setHandler(null);
		}
		
	};
	
	
	private final Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			
			int state = msg.arg1;
			handleStateMsg(state);
			
			return true;
			
		}
	});
	
	
	public void sendDataParameters(){
		
		if (mCultureService != null) {
			

			
			MyDbHelper dbHelper = new MyDbHelper(this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			Parameters p_1 = Parameters.get(1, db);
			Parameters p_2 = Parameters.get(2, db);
			Parameters p_3 = Parameters.get(3, db);
			
			db.close();
			
			mCultureService.send_parameters(p_1,p_2,p_3);
		
		
			
			Toast.makeText(ParametersActivity.this,
					  "Envio de parâmetros realizado com sucesso!",
						Toast.LENGTH_LONG).show();
			
		}else{
			Toast.makeText(ParametersActivity.this,
					  "Erro!Falha no serviço.",
						Toast.LENGTH_LONG).show();
			
		}
	}
	
	 public void addListenerOnButton() {
		 
			
			//spinnerPeriodType = (Spinner) findViewById(R.id.id_period_type);
			submitBtn = (Button) findViewById(R.id.id_button_submit);
			//cancelBtn = (Button) findViewById(R.id.id_button_cancel);
		
			
			submitBtn.setOnClickListener(new OnClickListener() {
		 
			  @Override
			  public void onClick(View v) {
				  
				  
				  
				  int txt_parameter_1=-1;
				  int txt_parameter_2=-1;
				  int txt_parameter_3=-1;

				  
				  if(parameter1.getText()!=null){
					  txt_parameter_1=Integer.valueOf(parameter1.getText().toString());
				  }
				  if(parameter2.getText()!=null){
					  txt_parameter_2=Integer.valueOf(parameter2.getText().toString());
				  }
				  
				  if(parameter3.getText()!=null){
					  txt_parameter_3=Integer.valueOf(parameter3.getText().toString());
				  }
							  
				  MyDbHelper dbHelper = new MyDbHelper(v.getContext());
				  SQLiteDatabase db = dbHelper.getWritableDatabase();
				  
				  
				  
				  Parameters p_1 = Parameters.get(1, db);
				  Parameters p_2 = Parameters.get(2, db);
				  Parameters p_3 = Parameters.get(3, db);
				  
				  
				  boolean changed_value=false;
				  
				  if(txt_parameter_1!=-1){
					  
					  p_1.setValue(txt_parameter_1);
					  changed_value=true;
				  }
				  
				  if(txt_parameter_2!=-1){
					  
					  p_2.setValue(txt_parameter_2);
					  changed_value=true;
				  }
				  
				  if(txt_parameter_3!=-1){
					  
					  p_3.setValue(txt_parameter_3);
					  changed_value=true;
				  }
				  
				  if(changed_value){
					  try {
							  p_1.update(db);
							  p_2.update(db);
							  p_3.update(db);
						  } catch (Exception e) {
						// TODO Auto-generated catch block
							  e.printStackTrace();
							  Log.d("PARAMETERSACTIVITY", "Error updating.");
							  Toast.makeText(ParametersActivity.this,
									  "Erro na actualizaçao de parametros.",
										Toast.LENGTH_LONG).show();
						  }
					  
						  Toast.makeText(ParametersActivity.this,
								  "Parâmetros actualizados com sucesso!",
									Toast.LENGTH_LONG).show();
					  
						  
				  }
				   
				  
				  db.close();
			   
			  }
		 
			});
			
		  }
	 
	 
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
		    //Log.d(TAG, "onCreateOptionsMenu called.");

		    menu.setQwertyMode(true);

		    // Menu.add(int groupId, int itemId, int order, int titleRes)

		    // opção "return to culturecontrolactivity" 
	
		    // opção "download parameters" 
		    MenuItem mnu2 = menu.add(0, 0, 0, R.string.send_data);
		    mnu2.setAlphabeticShortcut('d');
		    mnu2.setIcon(R.drawable.ic_menu_download);
		    

		    return super.onCreateOptionsMenu(menu);
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {


		    case 0: // registo de leituras (envia parameters para arduino)
		    	
		    	sendDataParameters();
		    	
		    return true;
		    	
		    }

		    return false;
		}

}

