package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class AddProgramActivity extends Activity {

	
	private Button timeBtn;
	private Button dateBtn;
	private Button submitBtn;
	private Button cancelBtn;
	
	private EditText description;
	private EditText duration;
	
	private EditText tmax,tmin;
	private EditText hmax,hmin;
	private EditText hgmax,hgmin;
	
	
	//Context c = getApplicationContext();
	
	private Spinner spinnerPeriodType;
	
	private Spinner spinnerDevice;
	
	DateFormat formatDateTime=DateFormat.getDateTimeInstance();
	Calendar dateTime=Calendar.getInstance();
	
	private TextView timeLabel;
	/** Called when the activity is first created. */
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_program);
        
        timeLabel=(TextView)findViewById(R.id.id_time);
        description=(EditText)findViewById(R.id.id_description);
        
     	duration=(EditText)findViewById(R.id.id_duration);
        tmax=(EditText)findViewById(R.id.id_tmax);
        tmin=(EditText)findViewById(R.id.id_tmin);
        hmax=(EditText)findViewById(R.id.id_hmax);
        hmin=(EditText)findViewById(R.id.id_hmin);
        hgmax=(EditText)findViewById(R.id.id_hgmax);
        hgmin=(EditText)findViewById(R.id.id_hgmin);
        
        duration.setText("0");
        tmax.setText("0");
        tmin.setText("0");
        hmax.setText("0");
        hmin.setText("0");
        hgmax.setText("0");
        hgmin.setText("0");
        
        timeBtn=(Button)findViewById(R.id.id_timeBtn);
        dateBtn=(Button)findViewById(R.id.id_dateBtn);
        
        spinnerDevice = (Spinner) findViewById(R.id.id_device);
        
        spinnerPeriodType = (Spinner) findViewById(R.id.id_period_type);
        
        updateLabel();
        
        addItemsOnSpinnerDevice();
    	addListenerOnButton();
    	//addListenerOnSpinnerItemSelection();
        
    	
    }
	
	
	
    
	public void chooseDate(){
    	new DatePickerDialog(AddProgramActivity.this, d, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }
    
	public void chooseTime(){
    	new TimePickerDialog(AddProgramActivity.this, t, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true).show();
    }
	
	DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			dateTime.set(Calendar.YEAR,year);
			dateTime.set(Calendar.MONTH, monthOfYear);
			dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateLabel();
		}
	};
	
	TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			dateTime.set(Calendar.MINUTE,minute);
			updateLabel();
		}
	};
	
	private void updateLabel() {
		
		//timeLabel.setText(formatDateTime.format(dateTime.getTime()));
		
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		timeLabel.setText(sdfDate.format(dateTime.getTime()));
		
	}
	
	
	@SuppressLint("DefaultLocale")
	public void addItemsOnSpinnerDevice() {
		
		Context c = getApplicationContext();
		
		MyDbHelper dbHelper = new MyDbHelper(c);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		ArrayList<Device> devices = Device.getAll(null, db);
		 
		spinnerDevice = (Spinner) findViewById(R.id.id_device);
		
		List<String> list = new ArrayList<String>();
		
		String str_dev="";
		
		for(Device d:devices){
			if(d.isMotor()){
				str_dev=String.format("%s:%s(%d):%d", d.getTypeMS(),d.getType(),d.getNumber(),d.getId());
			}
			if(d.isSensor()){
				str_dev=String.format("%s:%s(%d):%d", d.getTypeMS(),d.getType(),d.getNumber(),d.getId());
			}
				
			list.add(str_dev);
		}
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDevice.setAdapter(dataAdapter);
		
		
		
	  }
	
	  public void addListenerOnSpinnerItemSelection() {
		  spinnerPeriodType = (Spinner) findViewById(R.id.id_period_type);
		  //spinnerPeriodType.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	  }
	  
	
	
	// get the selected dropdown list value
	  public void addListenerOnButton() {
	 
		
		//spinnerPeriodType = (Spinner) findViewById(R.id.id_period_type);
		submitBtn = (Button) findViewById(R.id.id_button_submit);
		cancelBtn = (Button) findViewById(R.id.id_button_cancel);
		
		dateBtn = (Button) findViewById(R.id.id_dateBtn);
		timeBtn = (Button) findViewById(R.id.id_timeBtn);
		
		dateBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				chooseDate();
			}
		
		
		});
		
		timeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				chooseTime();
		}});
		
		
		submitBtn.setOnClickListener(new OnClickListener() {
	 
		  @Override
		  public void onClick(View v) {
			  
			 
			  
			  String descriptionstr="";
			  String timelabelstr="";
			  int durationint=-1;
			  String periodtype="";
			  int type_device_m_s=-1;
			  int id_device=0;
			  String type_device="";
			  
			  
			  float txt_tmax=-100;
			  float txt_tmin=-100;
			  float txt_hmax=-1; 
			  float txt_hmin=-1;
			  float txt_hgmax=-1;
			  float txt_hgmin=-1;
			  
			  
			  MyDbHelper dbHelper = new MyDbHelper(v.getContext());
			  SQLiteDatabase db = dbHelper.getWritableDatabase();
			  
			  String str_device = (String) spinnerDevice.getSelectedItem();
			  String[] arr_device = str_device.split(":");
			 
			  //type_device = arr_device[1];
			  //type_device_m_s = Device.getTypeDeviceProgram(arr_device[0]);
			  
			  if(arr_device[2]!=null){
				  try {
					  id_device = Integer.valueOf(arr_device[2]);
				  }catch(NumberFormatException e){
					  
				  }
			  }
			  
			  
			  Device d = Device.get(id_device, db);
			  if(d!=null){
				  type_device = d.getType();
				  type_device_m_s = Device.getTypeDeviceProgram(d.getTypeMS());
			  }
			  
			  descriptionstr=description.getText().toString();
			  timelabelstr=timeLabel.getText().toString();
			  if(duration.getText().toString()!=""){
				  durationint=Integer.valueOf(duration.getText().toString());
			  }
		      periodtype=spinnerPeriodType.getSelectedItem().toString();
			  
			  if(tmax.getText().toString()!=""){
				  txt_tmax=Float.parseFloat(tmax.getText().toString());
			  }
			  if(tmin.getText().toString()!=""){
				  txt_tmin=Float.parseFloat(tmin.getText().toString());
			  }
			  if(hmax.getText().toString()!=""){
				  txt_hmax=Float.parseFloat(hmax.getText().toString());
			  }
			  if(hmin.getText().toString()!=""){
			      txt_hmin=Float.parseFloat(hmin.getText().toString());
			  }
			  if(hgmax.getText().toString()!=""){
				  txt_hgmax=Float.parseFloat(hgmax.getText().toString());
			  }
			  if(hgmin.getText().toString()!=""){
				  txt_hgmin=Float.parseFloat(hgmin.getText().toString());
			  }
			  
			  
			  if(id_device!=0 && type_device_m_s!=-1 && descriptionstr!="" && timelabelstr!="" && durationint!=-1 && periodtype!=""){
				  
				  Program p =new Program(0,id_device,type_device_m_s,descriptionstr,timelabelstr,durationint,periodtype,
						  txt_tmax,txt_tmin,txt_hmax,txt_hmin,txt_hgmax,txt_hgmin);
				  
				  if(p!=null)
				  {
					  try {
						  p.insert(db);
					  } catch (Exception e) {
					// TODO Auto-generated catch block
						  e.printStackTrace();
					  }
				  
					  Toast.makeText(AddProgramActivity.this,
							  "Programa inserido com sucesso!",
								Toast.LENGTH_LONG).show();
				  
					  Intent newIntent = new Intent(AddProgramActivity.this,
			                ProgramListActivity.class);
					  startActivity(newIntent);
				  }
			  } 
			  
			  db.close();
		   
		  }
	 
		});
		
		cancelBtn.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				  
				  Intent newIntent = new Intent(AddProgramActivity.this,
			                ProgramListActivity.class);
				  startActivity(newIntent);
			  }
		});
	  }
	  
	

}
