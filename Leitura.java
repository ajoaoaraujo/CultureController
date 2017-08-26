package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Leitura extends Device {
	
	private int id;
	private int id_device;
	private String date;
	private int duration;
	private float value;
	
	public final static String TBL_NAME = "tbl_reading_values";
	public final static String ID = "id";
	public final static String ID_DEVICE = "id_device";
	public final static String DATE = "date";
	public final static String DURATION = "duration";
	public final static String VALUE = "value";
	public final static String[] FIELDS = { ID, ID_DEVICE, DATE, DURATION, VALUE};
	
	public Leitura(int id, int id_device, String date, int duration, float value){
		
		super(id_device);
		
		setId(id);
		setId_device(id_device);
		setDate(date);
		setDuration(duration);
		setValue(value);
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id=id;
	}
	
	public int getId_device(){
		
		return id_device;
	}
	
	public void setId_device(int id_device){
		
		this.id_device=id_device;
	} 
	
	public String getDate(){
		return date;
	}
	
	public void setDate(String d){
		
		if(d==null){
			
			Calendar c = Calendar.getInstance();
			DateContainer dc = new DateContainer(DateContainer.DateContainerType.DATE,c.getTime().toString()); 
			
			this.date = dc.getDateTimeString();
		}else{
			this.date = d; 
		}
		
	}
	
	public int getDuration(){
		return duration;
	}
	
	public void setDuration(int d){
		this.duration=d;
	}
	
	public float getValue(){
		return value;
	}
	
	public void setValue(float v){
		this.value=v;
	}
	
	
	
	public static String setDateStatic(String d){
		
		if(d==null){
			
			Calendar c = Calendar.getInstance();
			DateContainer dc = new DateContainer(DateContainer.DateContainerType.DATE,c.getTime().toString()); 
			
			return dc.getDateTimeString();
		}else{
			return d; 
		}
	} 
	
	public static ArrayList<Leitura> getAll(ArrayList<Leitura> alist, SQLiteDatabase db) {
		ArrayList<Leitura> llist = (alist == null ? new ArrayList<Leitura>()
				: alist);

		llist.clear();
		Cursor c = db.rawQuery("SELECT * FROM " + TBL_NAME
				+ " ORDER BY " + DATE + " DESC", null);
		
		if (c.moveToFirst()) {
			
			if(!c.isNull(0)){
			
			do {
				
				String d = setDateStatic(c.getString(2));
				Leitura l = new Leitura(c.getInt(0), c.getInt(1), d, c.getInt(3),
						c.getFloat(4));
				llist.add(l);
				
				
			} while (c.moveToNext());
			
			}
		}
		c.close();
		
		return llist;
	}
	
	public static ArrayList<Leitura> getFromDates(ArrayList<Leitura> alist, SQLiteDatabase db, int id_device, String date1, String date2) {
		ArrayList<Leitura> llist = (alist == null ? new ArrayList<Leitura>()
				: alist);

		llist.clear();
		
		String sql="SELECT * FROM " + TBL_NAME + " WHERE id_device=" + id_device;
		if(date1!="" || date2!=""){
			sql += " AND ";
		}
		if(date1!=""){
			sql += " date>'"+ date1 + "'";
		}
		if(date1!="" || date2!=""){
			sql += " AND ";
		}
		if(date2!=""){
			sql += " date<'" + date2 + "'";
		}
		sql += " ORDER BY " + DATE + " DESC";
		
		
		Cursor c = db.rawQuery(sql, null);
		
		if (c.moveToFirst()) {
			
			if(!c.isNull(0)){
			
			do {
				
				String d = setDateStatic(c.getString(2));
				Leitura l = new Leitura(c.getInt(0), c.getInt(1), d, c.getInt(3),
						c.getFloat(4));
				llist.add(l);
				
				
			} while (c.moveToNext());
			
			}
		}
		c.close();
		
		return llist;
	}
	
	
	public void insert(SQLiteDatabase db) throws Exception{
		
		ContentValues values= new ContentValues();
		
		values.put(Leitura.ID_DEVICE, getId_device());
		values.put(Leitura.DATE, getDate());
		values.put(Leitura.DURATION, getDuration());
		values.put(Leitura.VALUE, getValue());
		
		long rowId = db.insert(TBL_NAME, null, values);
		
		if (rowId < 0) {
			throw new Exception("Não foi possível inserir a leitura na Base de Dados");
		}
		
		
	}
	
	public static void insertCv(ContentValues values, SQLiteDatabase db) throws Exception{
		
		long rowId = db.insert(TBL_NAME, null, values);
		
		if (rowId < 0) {
			throw new Exception("Não foi possível inserir a leitura na Base de Dados");
		}
		
		
	}
	
	
	public static Leitura getFromId(int id, SQLiteDatabase db){
		
		Leitura l=null;
		
		Cursor c = db.rawQuery("SELECT * FROM " + TBL_NAME
				+ " WHERE id="+String.valueOf(id), null);
		
		if(c.moveToFirst()) {
			
			String d = setDateStatic(c.getString(2));
			l = new Leitura(c.getInt(0), c.getInt(1), d, c.getInt(3), c.getFloat(4));
			
			
		}else{
			
		}
		
		c.close();
		
		return l;
		 
	}
	
	
	
	
	public static void delete(int id, SQLiteDatabase db) throws Exception {
		
		long rowsAffected = db.delete(TBL_NAME,"id=" + String.valueOf(id), null);
		if(rowsAffected <= 0){
			throw new Exception("Não foi eliminar a Leitura da Base de Dados");
		}
		
		
	}


	
	

}
