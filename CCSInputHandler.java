package pt.ipp.isep.dei.formacao.android.culturecontroler;



import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CCSInputHandler {
	
	private String in_str;
	
	private boolean device_valido = false;
	private boolean duration_valido = false;
	private boolean value_valido = false;

	
	private SQLiteDatabase db = null;
	private ContentValues cv = null;
	
	public CCSInputHandler(SQLiteDatabase db) {
		this.db = db;
	}
	
	
	public void InserirLeituras() {
		
		String dt_msg=null;
		String date_msg=null;
		String time_msg=null;
		Float t_value=null;
		Float h_value=null;
		Float hg_value=null;
		
		try {
			if(in_str!=""){

				String[] in_str_arr = in_str.split(" ",0);
				
				if(in_str_arr!=null){
						
					dt_msg=in_str_arr[0];
					String[] date_str=in_str_arr[1].split("/");
					String[] time_str=in_str_arr[2].split(":");
					
					date_msg=String.format("%s %s", in_str_arr[1],in_str_arr[2]).replace("/", "-");
					
					t_value=Float.valueOf(in_str_arr[3]);
					h_value=Float.valueOf(in_str_arr[4]);
					hg_value=Float.valueOf(in_str_arr[5]);
					
					
					
					
				}
			
			}

		} catch (Exception e) {
			
			StackTraceElement[] st = e.getStackTrace();
			Log.d("CCS", e.toString());
			for (StackTraceElement el : st)
				Log.d("CCS", el.toString());
		}
	}
	
	public void Parse() {
		
		
	}
	
	

}
