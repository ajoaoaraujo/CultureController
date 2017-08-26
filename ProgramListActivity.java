package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.util.ArrayList;

import pt.ipp.isep.dei.formacao.android.culturecontroler.CultureControlService.CultureControlServiceBinder;


import com.google.gson.Gson;



import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
//import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ProgramListActivity extends ListActivity {

	private final static String TAG = "PROGRAMLIST_ACTIVITY";
	
	private ArrayList<Program> programs = new ArrayList<Program>();
	
    private ProgramListAdapter listAdapter = null;
    
    private ProgramServiceReceiver mProgramServiceReceiver = null;
    
	public CultureControlService mCultureService;
	
	private BluetoothDevice device;

	private TextView txtBluetoothStatus;
	
	private Context context;
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.list_programs);
	    
	    txtBluetoothStatus = (TextView) findViewById(R.id.txtBluetoothStatus);
	    
	    context = this.getApplicationContext();
	    
    	SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		Gson gson = new Gson();
		String json = appSharedPrefs.getString("bt_device", "");
		device = gson.fromJson(json, BluetoothDevice.class);
    	
    	if(device == null){
    		Log.e(TAG, "A BluetoothDevice must be sent along with the startActivity Intent!");
    		finish();
    		return;
    	}

	    
	    
	    listAdapter = new ProgramListAdapter(ProgramListActivity.this,
                programs);
        setListAdapter(listAdapter);
        
        loadProgramsToArrayList();
        
        registerForContextMenu(getListView());
        
        doServiceConfiguration();
	    
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
			
			return true;
		}
	});

	
	
	 protected void loadProgramsToArrayList() {
        
		 MyDbHelper dbHelper = new MyDbHelper(this);
		 
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
		 
         programs.clear();

         programs = Program.getAll(programs, db);

         db.close();
         
         listAdapter.notifyDataSetChanged();
        
     }
	 
	 private void removeItemFromList(int position){
		 
		 Program p = listAdapter.getItem(position);
		 
		 
		 
		 MyDbHelper dbHelper = new MyDbHelper(this);
		 
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
		 
		 if(p != null){
			 
			 
			 try {
				Program.delete(p.getId(), db);
				
				listAdapter.remove(p);
				
				Toast t = Toast.makeText(this,
		                "Program " + p.getDescription() + " deleted.",
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
	 
	 private void editProgramFromList(int position){
		 
		 Program p = listAdapter.getItem(position);
		 
		 Intent intent = new Intent(ProgramListActivity.this, EditProgramActivity.class);   
	     intent.putExtra("PROGRAMID", p.getId());
	     startActivity(intent);
		 
	 }
	
	 
	 
	 public void sendDataPrograms(){
	 
		mCultureService.startSending();
			
	 }
	 
	 	private void doServiceConfiguration() {
	    	

	        // configuração do broadcast receiver...
	        IntentFilter mIntentFilter = new IntentFilter();
	        // ...que apenas recebe as seguintes acções:
	        mIntentFilter.addAction(SendProgramsAsyncTask.PROGRAMS_SEND_ACTION);
	        mIntentFilter.addAction(SendProgramsAsyncTask.PROGRAMS_CANCELED_ACTION);
	        mProgramServiceReceiver = new ProgramServiceReceiver();

	        // regista o BroadcastReceiver de forma a receber os Broadcasts da AsyncTask
	        context.registerReceiver(mProgramServiceReceiver, mIntentFilter);
	    
	    }
	 
	 
	//broadcast para receber os intents do service
			class ProgramServiceReceiver extends BroadcastReceiver {
			    @Override
			    public void onReceive(Context c, Intent i) {

			    	if(i.getAction().compareTo(SendProgramsAsyncTask.PROGRAMS_SEND_ACTION) == 0) {   
			    		Toast.makeText(c, "Program sending service completed sucessfully!", Toast.LENGTH_LONG).show();
			    	
			    	}
			    	else      // se a acção foi o cancelamento do download...
			        
			    		if(i.getAction().compareTo(SendProgramsAsyncTask.PROGRAMS_CANCELED_ACTION) == 0) {
			            
			    			Toast.makeText(c, "The programs send service was canceled!", Toast.LENGTH_LONG).show();
			    		}  
			        
			    }
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
	 /*
	 protected String getPlacesFilter() {
		    
		    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		    
		    String PlaceFilter = prefs.getString(
		            LocalPreferenceActivity.LOCALS_PREF,
		            LocalPreferenceActivity.DEFAULT_LOCAL);
		    return PlaceFilter
		            .compareToIgnoreCase(LocalPreferenceActivity.DEFAULT_LOCAL) == 0 ? null
		            : PlaceFilter;
		}
		*/
	 
	 
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
		    Log.d(TAG, "onCreateOptionsMenu called.");

		    menu.setQwertyMode(true);

		    // Menu.add(int groupId, int itemId, int order, int titleRes)
		    
		    MenuItem mnu1 = menu.add(0, 0, 0, R.string.go_back);
		    mnu1.setAlphabeticShortcut('b');
		    mnu1.setIcon(R.drawable.ic_menu_navigation_back);

		    
		    MenuItem mnu2 = menu.add(0, 1, 0, R.string.add_program_label);
		    mnu2.setAlphabeticShortcut('a');
		    mnu2.setIcon(R.drawable.ic_menu_btn_add);
		    
		    
		 // opção "download parameters" 
		    MenuItem mnu3 = menu.add(0, 2, 0, R.string.send_data);
		    mnu3.setAlphabeticShortcut('d');
		    mnu3.setIcon(R.drawable.ic_menu_download);
		    
		    /*
		    MenuItem mnu2 = menu.add(0, 1, 0, R.string.delete_program_label);
		    mnu2.setAlphabeticShortcut('d');
		    mnu2.setIcon(R.drawable.ic_menu_delete);
			*/
		    
		    return super.onCreateOptionsMenu(menu);
		}

		// executado quando o utilizador selecciona um item no menu
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    
		    case 0:Intent newIntentcc = new Intent(ProgramListActivity.this,
                	CultureControlActivity.class);
        		startActivity(newIntentcc);
        		return true;
		    
		    case 1: // refresh
		    	 Intent newIntent = new Intent(ProgramListActivity.this,
			                AddProgramActivity.class);
			     startActivity(newIntent);
			     return true;
			
		    case 2: // registo de leituras (envia programs para arduino)
		    	
		    	sendDataPrograms();
		    	return true;
		    
			
		    }
		
		    return false;
		}
		
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
			
			Log.d(TAG, "onCreateContextMenu called.");
			super.onCreateContextMenu(menu, v, menuInfo);
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.listprogramcontextmenu, menu);
		    
		   
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
		        case R.id.editItem:
		        	editProgramFromList(info.position);
		        	return true;
		        default:
		            return super.onContextItemSelected(item);
		    }
		}


}

