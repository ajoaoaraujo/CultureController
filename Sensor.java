package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



public class Sensor extends Device {

	private float value;
	
	public final static String TBL_NAME = "tbl_sensor";
	public final static String ID_DEVICE = "id_device";
	public final static String VALUE = "value";
	
	
	public final static String[] FIELDS = { ID_DEVICE, VALUE};
	
	public Sensor(int id, int number, String type, float value){
		
		super(id, number, type);
		
		setValue(value);
	}
	
	public void setValue(float v){
		
		this.value = v;
	}



	public float getValue() {
		return value;
	}
	
	
	public static ArrayList<Sensor> getAll(ArrayList<Sensor> alist, SQLiteDatabase db) {
		ArrayList<Sensor> slist = (alist == null ? new ArrayList<Sensor>()
				: alist);

		slist.clear();
		Cursor c = db.rawQuery("SELECT * FROM " + TBL_NAME
				+ " ORDER BY " + ID + " ASC", null);
		
		if (c.moveToFirst()) {
			
			if(!c.isNull(0))
			{
				
			
			do {
				
				Sensor s = new Sensor(c.getInt(0), c.getInt(1), c.getString(2), c.getFloat(3));
				slist.add(s);
				
				
			} while (c.moveToNext());
			
			}
		}
		c.close();
		return slist;
	}
	
	public void update(SQLiteDatabase db) throws Exception{
		
		ContentValues values= new ContentValues();
		
		values.put(Sensor.VALUE, getValue());
		
		long rowId = db.update(TBL_NAME, values, ID_DEVICE + "=" + String.valueOf(getId()), null);
		
		if (rowId < 0) {
			throw new Exception("Não foi possível actualizar o sensor na Base de Dados");
		}
	
	}
	
	public static Sensor setSensor(int id, SQLiteDatabase db) {
		Sensor s = null;

		Device d = Device.get(id, db);
		
		if(d!=null){
		
			Cursor c = db.query(TBL_NAME, FIELDS, ID_DEVICE + "=" + id, null, null, null, null);
			if (c.moveToFirst()) {
			
				if(!c.isNull(0)){
					s = new Sensor(c.getInt(0), d.getNumber(), d.getType(), c.getFloat(1));
				}
				c.close();

			}
		}
		return s;
	}

	



		

}
