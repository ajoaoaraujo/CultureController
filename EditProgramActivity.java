package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditProgramActivity extends Activity {

	private Button timeBtn;
	private Button submitBtn;
	private EditText duration;
	
	DateFormat formatDateTime=DateFormat.getDateTimeInstance();
	Calendar dateTime=Calendar.getInstance();
	
	private TextView timeLabel;
	
	int hours;
	int minutes;
	
	
	private int id_program=0;
	
	private Context context;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.edit_program);
	    
	    context = this.getApplicationContext();
	    
	    timeLabel=(TextView)findViewById(R.id.id_time);
	    
	    duration=(EditText)findViewById(R.id.id_duration);
	    
	    timeBtn=(Button)findViewById(R.id.id_timeBtn);
	    
	    
	    MyDbHelper dbHelper = new MyDbHelper(this.getApplicationContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
	    
	    Bundle extras = getIntent().getExtras();
        if (extras != null) {
             id_program = extras.getInt("PROGRAMID");
        }
        
        if(id_program != 0){
        	
        	Program p = Program.getFromId(id_program, db);
        	if(p!=null){
        		
        		duration.setText(String.valueOf(p.getDuration()));
        		
        		String date=p.getDate();
        		
        		String h=date.substring(11, 13);
        		String m=date.substring(14, 16);
        		
        		
        		hours=Integer.valueOf(h);
        		minutes=Integer.valueOf(m);
        		
        		dateTime.set(Calendar.HOUR_OF_DAY, hours);
    			dateTime.set(Calendar.MINUTE,minutes);
        		
    			updateLabel();
        	
        	}
        }
	    
	    db.close();
	    
	   
	    
	    addListenerOnButton();
	
	    // TODO Auto-generated method stub
	}
	
	public void chooseTime(){
    	new TimePickerDialog(EditProgramActivity.this, t, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true).show();
    }
	
	
	TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			dateTime.set(Calendar.MINUTE,minute);
			
			hours = hourOfDay;
			minutes = minute;
			
			updateLabel();
		}
	};
	
	private void updateLabel() {
		
		//timeLabel.setText(formatDateTime.format(dateTime.getTime()));
		
		SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");
		
		timeLabel.setText(sdfDate.format(dateTime.getTime()));
		
	}
	
	public void addListenerOnButton() {
		
		
		submitBtn = (Button) findViewById(R.id.id_button_submit);
		
		timeBtn = (Button) findViewById(R.id.id_timeBtn);
		
		timeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				chooseTime();
		}});
		
		
		submitBtn.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				  
				  String timelabelstr="";
				  int durationint=-1;
				  
				  if(duration.getText().toString()!=""){
					  durationint=Integer.valueOf(duration.getText().toString());
				  }
				  
				  if(id_program != 0){
					  
					  	MyDbHelper dbHelper = new MyDbHelper(context);
						SQLiteDatabase db = dbHelper.getWritableDatabase();
			        	
			        	Program p = Program.getFromId(id_program, db);
			        	if(p!=null){
			        	
			        		String old_date = p.getDate();
			        		String old_date_1=old_date.substring(0, 10);
			        		String old_date_2=old_date.substring(16, 19);
			        		
			        		String new_date = String.format("%s %02d:%02d%s", old_date_1, hours, minutes, old_date_2);
			        		
			        		p.setDate(new_date);
			        		p.setDuration(durationint);
			        		
			        		
			        		try {
								  p.update(db);
							  } catch (Exception e) {
							// TODO Auto-generated catch block
								  e.printStackTrace();
							  }
						  
							  Toast.makeText(EditProgramActivity.this,
									  "Programa alterado com sucesso!",
										Toast.LENGTH_LONG).show();
						  
							  Intent newIntent = new Intent(EditProgramActivity.this,
					                ProgramListActivity.class);
							  startActivity(newIntent);
			        		
			        	}
				  
				  
				  }
		}});
			  
	}

}
