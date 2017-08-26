package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import com.google.gson.Gson;

import pt.ipp.isep.dei.formacao.android.culturecontroler.CultureControlService.CultureControlServiceBinder;

import android.app.ListActivity;
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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

public class LeituraListActivity extends ListActivity {

	/** Called when the activity is first created. */
	private final static String TAG = "LEITURALIST_ACTIVITY";
	
	private CultureControlService mCultureService;
	private BluetoothDevice device;
	
	private ArrayList<Leitura> leituras = new ArrayList<Leitura>();
	
    private LeituraListAdapter listAdapter = null;

    private TextView txtBluetoothStatus;
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
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
	
	    setContentView(R.layout.list_leituras);
	    
	    txtBluetoothStatus = (TextView) findViewById(R.id.txtBluetoothStatus);
	    
	    listAdapter = new LeituraListAdapter(LeituraListActivity.this,
                leituras);
        setListAdapter(listAdapter);
        
        loadLeiturasToArrayList();
        
        registerForContextMenu(getListView());
	    
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

        Log.d(TAG, "onResume called.");


    }
	
	
	
	
	 protected void loadLeiturasToArrayList() {
        
		 MyDbHelper dbHelper = new MyDbHelper(this);
		 
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
		 
         leituras.clear();

         leituras = Leitura.getAll(leituras, db);

         db.close();
         
         listAdapter.notifyDataSetChanged();
        
     }
	 
	 private void removeItemFromList(int position){
		 
		 Leitura l = listAdapter.getItem(position);
		 
		 
		 
		 MyDbHelper dbHelper = new MyDbHelper(this);
		 
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
		 
		 if(l != null){
			 
			 
			 try {
				Leitura.delete(l.getId(), db);
				
				listAdapter.remove(l);
				
				Toast t = Toast.makeText(this,
		                "Leitura " + String.valueOf(l.getId()) + " deleted.",
		                Toast.LENGTH_LONG);
		        t.show();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
		 db.close();
		 
		 listAdapter.notifyDataSetChanged();
	 }
	 
	 /*
	 @Override
	 protected void onListItemClick(ListView l, View v, int position, long id) {

	        Toast t = Toast.makeText(this,
	                "You clicked on " + programs.get(position),
	                Toast.LENGTH_LONG);
	        t.show();
	        
	        
	        Intent intent = new Intent(getApplicationContext(), ProgramEditActivity.class);
	        intent.putExtra("PROGRAMID", programs.get(position).getId());
	        startActivity(intent);
			
			
	        super.onListItemClick(l, v, position, id);
	 }
	 
*/

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
				//mControlService.requestSensor1Temperature();
				
			}
			// executado quando a ligação ao serviço é destruída
			// normalmente acontece quando o serviço falha ou é morto
			public void onServiceDisconnected(ComponentName className) {
				mCultureService = null;
				mCultureService.setHandler(null);
			}
			
		};
	 
	 
		private final Handler mHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				
				int state = msg.arg1;
				handleStateMsg(state);
				
				Context c = getApplicationContext();
				
				MyDbHelper dbHelper = new MyDbHelper(c);
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				
				
				int duration = -1;
				String date = null;
				float value_st1 = -1000;
				float value_sh1 = -1000;
				float value_shg1 = -1000;
				int water_level = -1;
				
				switch (msg.what) {
				case CultureControlService.MESSAGE_RECEIVED:
					byte[] readBuf = (byte[]) msg.obj;
					
					if (readBuf[0] == Global.MESSAGE_TYPE_RESPONSE_SENSOR_GROUP_1_HISTORIC_VALUES) {
		
							//sensors values
							
							//String strIncom = new String(readBuf, 2, msg.arg1);
							
							String strIncom = new String(readBuf);
							
							Log.d("TESTE", strIncom);
								
									byte[] d=null;
									byte[] vt1=null,vh1=null,vhg1=null;
									System.arraycopy(readBuf,2,d,0,18);
									
									System.arraycopy(readBuf,20,vt1,0,2);
									System.arraycopy(readBuf,23,vh1,0,2);
									System.arraycopy(readBuf,26,vhg1,0,2);
						
									try {
										date = new String(d, "UTF-8");
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									String vt1_str="";
									try {
										vt1_str = new String(vt1, "UTF-8");
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									String vh1_str="";
									try {
										vh1_str = new String(vt1, "UTF-8");
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									String vhg1_str="";
									try {
										vhg1_str = new String(vt1, "UTF-8");
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									value_st1 = Float.parseFloat(vt1_str);
									value_sh1 = Float.parseFloat(vh1_str);
									value_shg1 = Float.parseFloat(vhg1_str);
							
						
					}
					
					if (readBuf[0] == Global.MESSAGE_TYPE_RESPONSE_MOTOR_HISTORIC_VALUES) {
						
						//motor values
							String strIncom = new String(readBuf);
							Log.d("TESTE", strIncom);
							
								byte[] d=null;
								byte[] dur=null;
								
								System.arraycopy(readBuf,2,d,0,17);
								
								System.arraycopy(readBuf,20,dur,0,2);
								
								try {
									date = new String(d, "UTF-8");
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								String dur_str="";
								try {
									dur_str = new String(dur, "UTF-8");
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								duration = Integer.parseInt(dur_str);
						
						
								
						
						
					}
					return true;
				}
				
				if(date != null && ((value_st1!=-1000 || value_sh1!=-1000 || value_shg1!=-1000) || duration!=-1)){
					if(value_st1!=-1000){
						Leitura l_1 = new Leitura(0, 1, date, 0, value_st1);
						try {
							l_1.insert(db);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}
					
					if(value_sh1!=-1000){
						Leitura l_2 = new Leitura(0, 4, date, 0, value_sh1);
						try {
							l_2.insert(db);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}
					
					if(value_shg1!=-1000){
						Leitura l_3 = new Leitura(0, 7, date, 0, value_shg1);
						try {
							l_3.insert(db);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}
					
					if(duration!=-1){
						Leitura l_4 = new Leitura(0, 10, date, duration, 0);
						try {
							l_4.insert(db);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
						
				}
				
				db.close();
			
				return false;
				
			}

			
		});
		
		
		
		
		
		public void receive_data(){
			
			for(int c=1;c<=10;c++){
			
				mCultureService.requestLeitura(c);
				
			}			
				Toast t = Toast.makeText(this,
	                "Service started.Please Wait.",
	                Toast.LENGTH_SHORT);
				t.show();
						
			
		}
		
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
		    Log.d(TAG, "onCreateOptionsMenu called.");

		    menu.setQwertyMode(true);

		    // Menu.add(int groupId, int itemId, int order, int titleRes)

	 
		    MenuItem mnu1 = menu.add(0, 0, 0, R.string.go_back);
		    mnu1.setAlphabeticShortcut('r');
		    mnu1.setIcon(R.drawable.ic_menu_navigation_back);

		    
		    MenuItem mnu2 = menu.add(0, 1, 0, R.string.receive_data);
		    mnu2.setAlphabeticShortcut('d');
		    mnu2.setIcon(R.drawable.ic_menu_download);
			
		    
		    return super.onCreateOptionsMenu(menu);
		}

		


		// executado quando o utilizador selecciona um item no menu
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    case 0: // go back
		    	 Intent newIntent = new Intent(LeituraListActivity.this,
		    			 CultureControlActivity.class);
			     startActivity(newIntent);
			     return true;
		    
			case 1: // download leituras
				
				receive_data();
				return true;

	    	}

		    return false;
		}
		
		
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
			
			Log.d(TAG, "onCreateContextMenu called.");
			super.onCreateContextMenu(menu, v, menuInfo);
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.listleituracontextmenu, menu);
		    
		   
			/*
		    if (v.getId()==R.menu.listprogramcontextmenu) {
			    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			    menu.add(Menu.NONE, 1, 1, "view");
			    menu.add(Menu.NONE, 1, 1, "delete");
			    
			  }
			*/
		}
		
		@Override
		public boolean onContextItemSelected(MenuItem item) {
		    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		    switch (item.getItemId()) {
		        case R.id.viewItem:		            
		            return true;
		        case R.id.deleteItem:
		            removeItemFromList(info.position);
		            return true;
		        default:
		            return super.onContextItemSelected(item);
		    }
		}


}

