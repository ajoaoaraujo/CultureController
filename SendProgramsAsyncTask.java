package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.util.ArrayList;



import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;


public class SendProgramsAsyncTask extends AsyncTask<Void, Integer, Integer> {

	public static final String TAG = SendProgramsAsyncTask.class.getSimpleName();
		
	public static final String PROGRAMS_CANCELED_ACTION = "pt.ipp.isep.dei.formacao.android.culturecontrol.PROGRAMS_CANCELED";
	public static final String PROGRAMS_SEND_ACTION = "pt.ipp.isep.dei.formacao.android.culturecontrol.PROGRAMS_SEND";
	
	public static final String URL_EXTRA = "URL_EXTRA";
		
	private ArrayList<Program> programs = new ArrayList<Program>();
		
	private CultureControlService service;	
	
	private boolean canceled = false;
		
		
	public SendProgramsAsyncTask(CultureControlService serv){
		super();
		this.service = serv;
	}
		
		
	@Override
	protected Integer doInBackground(Void... params){
			
		int mResult=0;
		try{
			

					
			MyDbHelper dbHelper = new MyDbHelper(service.getApplicationContext());
			 
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			
			programs = Program.getAll(programs, db);
			
			if(programs != null){
				
				service.sendProgramReset();
				
				for(Program p:programs){
					
					Thread.sleep(3000);
					
					if(p!=null){
						
						if(!canceled){
							try {
								
								int h = Integer.parseInt(p.getHourFromDate());
								int m = Integer.parseInt(p.getMinutesFromDate());
								
								service.sendProgram(h,m,p.getDuration());
								
							} catch(NumberFormatException e){
								
								Log.d("SENDPROGRAMASYNCTASK", "Number format not valid.");
							}
						}
					
					}
					
					
						
				}
			}
			
			
			service.sendDone(mResult);
		
			
	         mResult = 1;
				
		} catch(Exception e){
				
			return -1;
		}
			
		return mResult;
	}
		
		
		@Override
		protected void onCancelled(Integer result){
			Intent mIntent = new Intent(PROGRAMS_CANCELED_ACTION);
			
			service.sendBroadcast(mIntent);
			//service.stopSelf();
		}
		
		//thread executada com exito
		@Override
		protected void onPostExecute(Integer result){
			Intent mIntent = new Intent(PROGRAMS_SEND_ACTION);
			
			service.sendBroadcast(mIntent);
			//service.stopSelf();		
			
		}

}
