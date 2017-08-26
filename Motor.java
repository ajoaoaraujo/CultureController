package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Motor extends Device {

	private int state;
	private int water_level;
	
	public final static String TBL_NAME = "tbl_motor";
	public final static String ID_DEVICE = "id_device";
	public final static String STATE = "state";
	public final static String WATER_LEVEL = "water_level";
	
	
	public final static String[] FIELDS = { ID_DEVICE, STATE, WATER_LEVEL};
	
	public Motor(int id_device, int number, int state, int water_level){
		
		super(id_device, number, "ACTUATOR");
		
		setState(state);
		setWaterLevel(water_level);
	}
		
	public void setState(int state){
		
		this.state=state;
	}
	
	public void setWaterLevel(int wl){
		this.water_level=wl;
	}
	
	public int getState() {
		return state;
	}
	
	public int getWaterLevel() {
		return water_level;
	}
	
	public boolean on(){
		return (state==1)?true:false;
	}
		
	public static ArrayList<Motor> getAll(ArrayList<Motor> alist, SQLiteDatabase db) {
		ArrayList<Motor> mlist = (alist == null ? new ArrayList<Motor>()
				: alist);

		mlist.clear();
		Cursor c = db.rawQuery("SELECT * FROM " + TBL_NAME
				+ " ORDER BY " + ID + " ASC", null);
		
		if (c.moveToFirst()) {
			if(!c.isNull(0))
			{
			do {
				
				Motor m = new Motor(c.getInt(0),c.getInt(1),c.getInt(2),c.getInt(3));
				mlist.add(m);
				
				
			} while (c.moveToNext());
			
			}
		}
		c.close();
		return mlist;
	
	}
	
	public void update(SQLiteDatabase db) throws Exception{
		
		ContentValues values= new ContentValues();
		
		values.put(Motor.STATE, getState());
		values.put(Motor.WATER_LEVEL, getWaterLevel());
		
		long rowId = db.update(TBL_NAME, values, "id=" + String.valueOf(getId()) ,null);
		
		if (rowId < 0) {
			throw new Exception("Não foi possível actualizar o motor na Base de Dados");
		}
	
	}


	public static Motor setMotor(int id, SQLiteDatabase db) {
		Motor m = null;

		Device d = Device.get(id, db);
		
		if(d!=null){
		
			Cursor c = db.query(TBL_NAME, FIELDS, ID_DEVICE + "=" + id, null, null, null, null);
			if (c.moveToFirst()) {
			
				if(!c.isNull(0)){
					m = new Motor(c.getInt(0), d.getNumber(), c.getInt(1), c.getInt(2));
				}
				c.close();

			}
		}
		return m;
	}
	

}
