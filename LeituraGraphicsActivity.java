package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

public class LeituraGraphicsActivity extends Activity {

	private Button date1Btn;
	private Button date2Btn;
	private Button refreshButton;
	
	private String date1="";
	private String date2="";
	
	Calendar dateTime1=Calendar.getInstance();
	Calendar dateTime2=Calendar.getInstance();
	
	GraphView graphView = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    date1Btn=(Button)findViewById(R.id.id_date1Btn);
        date2Btn=(Button)findViewById(R.id.id_date2Btn);

        refreshButton=(Button)findViewById(R.id.id_refreshBtn);
	    // TODO Auto-generated method stub
        
        addGraph();
 	  	
	  	LinearLayout layout = (LinearLayout) findViewById(R.id.graphic_layout);
	  	layout.addView(graphView);
	        
	}
	
	
	public void chooseDate1(){
    	new DatePickerDialog(LeituraGraphicsActivity.this, d1, dateTime1.get(Calendar.YEAR),dateTime1.get(Calendar.MONTH), dateTime1.get(Calendar.DAY_OF_MONTH)).show();
    }
    
	public void chooseDate2(){
		new DatePickerDialog(LeituraGraphicsActivity.this, d2, dateTime2.get(Calendar.YEAR),dateTime2.get(Calendar.MONTH), dateTime2.get(Calendar.DAY_OF_MONTH)).show();
    }
	
	DatePickerDialog.OnDateSetListener d1=new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			dateTime1.set(Calendar.YEAR,year);
			dateTime1.set(Calendar.MONTH, monthOfYear);
			dateTime1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateDates();
		}
	};
	
	DatePickerDialog.OnDateSetListener d2=new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			dateTime2.set(Calendar.YEAR,year);
			dateTime2.set(Calendar.MONTH, monthOfYear);
			dateTime2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateDates();
		}
	};
	
	
	private void updateDates() {
		
		//timeLabel.setText(formatDateTime.format(dateTime.getTime()));
		
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		
		date1 = sdfDate.format(dateTime1.getTime());
		date2 = sdfDate.format(dateTime2.getTime());
		
	}
	
	
	private void addGraph(){
	
	       	MyDbHelper dbHelper = new MyDbHelper(this.getApplicationContext());
			SQLiteDatabase db = dbHelper.getWritableDatabase();
	        
	        
	        ArrayList<Leitura> st1l=Leitura.getFromDates(null, db, 1, date1, date2);
	        ArrayList<Leitura> sh1l=Leitura.getFromDates(null, db, 4, date1, date2);
	        ArrayList<Leitura> shg1l=Leitura.getFromDates(null, db, 7, date1, date2);
	        
	        
	        GraphViewData[] data_st1=null;
	        int i=0;
	        for(Leitura l:st1l){
	        	
	        	String d=l.getDate().substring(1, 2);
	        	data_st1[i] = new GraphViewData(Integer.parseInt(d), l.getValue());
	        }
	        
	        GraphViewData[] data_sh1=null;
	        i=0;
	        for(Leitura l:sh1l){
	        	data_sh1[i] = new GraphViewData(i, l.getValue());
	        }
	        
	        
	        GraphViewData[] data_shg1=null;
	        for(Leitura l:shg1l){
	        	data_shg1[i] = new GraphViewData(i, l.getValue());
	        } 
	        
	        GraphViewSeries leitura_st1 = new GraphViewSeries(data_st1);
	        GraphViewSeries leitura_sh1 = new GraphViewSeries(data_sh1);
	        GraphViewSeries leitura_shg1 = new GraphViewSeries(data_shg1);
		
		  	graphView = new LineGraphView(
		  	      this // context
		  	      , "Grafico Leituras" // heading
		  	);
		  	graphView.addSeries(leitura_st1); // data
		  	graphView.addSeries(leitura_sh1);
		  	graphView.addSeries(leitura_shg1);

		
	}
	
	public void addListenerOnButton() {
		
		
		refreshButton=(Button)findViewById(R.id.id_refreshBtn);
		
		
		date1Btn=(Button)findViewById(R.id.id_date1Btn);
        date2Btn=(Button)findViewById(R.id.id_date2Btn);
		
		date1Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				chooseDate1();
			}
		
		
		});
		
		date2Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				chooseDate2();
		}});
		
		refreshButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				addGraph();
				
			}
			
		});
		
		
	}
	
	

}
