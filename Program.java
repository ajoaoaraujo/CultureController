package pt.ipp.isep.dei.formacao.android.culturecontroler;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Program extends Device {
	
	private int id;
	private int id_device;
	private String description;
	private int device_type;
	private String date;
	private int duration; //seconds
	private String period_type;
	
	private Float tmax,tmin,hmax,hmin,hgmax,hgmin;
	
	public final static int[] DEVICE_TYPES = {0,1}; // 0 - sensor , 1-motor 
	
	public final static String TBL_NAME = "tbl_program";
	public final static String ID = "id";
	public final static String ID_DEVICE = "id_device";
	public final static String DEVICE_TYPE = "device_type";
	public final static String DESCRIPTION = "description";
	public final static String DATE = "date";
	public final static String DURATION = "duration";
	public final static String PERIOD_TYPE = "period_type";
	
	public final static String TMAX = "tmax";
	public final static String TMIN = "tmin";
	public final static String HMAX = "hmax";
	public final static String HMIN = "hmin";
	public final static String HGMAX = "hgmax";
	public final static String HGMIN = "hgmin";
	
	public final static String[] PERIOD_TYPES = { "DIARIO","SEMANAL","MENSAL","ANUAL" };
	public final static String[] FIELDS = { ID, ID_DEVICE, DEVICE_TYPE, DESCRIPTION, DATE, DURATION, PERIOD_TYPE, TMAX, TMIN, HMAX, HMIN, HGMAX, HGMIN };
	
	
	public Program(int id, int id_device, int device_type, String description, String date, int duration, String ptype, Float tmax, Float tmin, Float hmax, Float hmin, Float hgmax, Float hgmin){
		
		super(id_device);
		
		setId(id);
		setId_device(id_device);
		setDeviceType(device_type);
		setDescription(description);
		setDate(date);
		setDuration(duration);
		setPeriodType(ptype);
		
		setTmax(tmax);
		setTmin(tmin);
		setHmax(hmax);
		setHmin(hmin);
		setHgmax(hgmax);
		setHgmin(hgmin);
		
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
	
	public void setDeviceType(int dt){
		
		this.device_type=dt;
	}
	
	public void setDescription(String desc){
		
		this.description=desc;
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
	
	public void setPeriodType(String ptype){
		
		List<String> ptypes = Arrays.asList(PERIOD_TYPES);
		
		for(String pt:ptypes){
			if(pt.equals(ptype)){
				this.period_type = ptype;
			}
		}
	}
	
	
	
	
	public Float getTmax() {
		return tmax;
	}

	public void setTmax(Float tmax) {
		this.tmax = tmax;
	}

	public Float getTmin() {
		return tmin;
	}

	public void setTmin(Float tmin) {
		this.tmin = tmin;
	}

	public Float getHmax() {
		return hmax;
	}

	public void setHmax(Float hmax) {
		this.hmax = hmax;
	}

	public Float getHmin() {
		return hmin;
	}

	public void setHmin(Float hmin) {
		this.hmin = hmin;
	}

	public Float getHgmax() {
		return hgmax;
	}

	public void setHgmax(Float hgmax) {
		this.hgmax = hgmax;
	}

	public Float getHgmin() {
		return hgmin;
	}

	public void setHgmin(Float hgmin) {
		this.hgmin = hgmin;
	}

	public int getIdProgram(){
		return id;
	}
	
	public int getDeviceType(){
		return device_type;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getDate(){
		return date;
	}
	
	public String getHourFromDate(){
		return date.substring(11, 13);
	}
	
	public String getMinutesFromDate(){
		return date.substring(14, 16);
	}
	
	public static String getHourFromDateS(String d){
		return d.substring(11, 13);
	}
	
	public static String getMinutesFromDateS(String d){
		return d.substring(14, 16);
	}
	
	public static String setHourFromDate(String old_date, int hours){
		
		String old_date_1=old_date.substring(0, 11);
		String old_date_2=old_date.substring(13, 19);
		
		String new_date = String.format("%s%02d%s", old_date_1, hours, old_date_2);
		return new_date;
	}
	
	public static String setMinutesFromDate(String old_date, int minutes){
		
		String old_date_1=old_date.substring(0, 14);
		String old_date_2=old_date.substring(16, 19);
		
		String new_date = String.format("%s%02d%s", old_date_1, minutes, old_date_2);
		return new_date;
	}
	
	public String getTime(){
		
		String str_time=this.date.substring(11, 16);
		return str_time;
	}
	
	public int getDuration(){
		return duration;
	}
	
	public String getPeriodType(){
		return period_type;
	}
	
	public static String getPeriodTypes(int i){
		return PERIOD_TYPES[i];
	} 
	
	public int getIndexPeriodType(){
		int i=-1;
		for(i=0;i<PERIOD_TYPES.length;i++){
			if(PERIOD_TYPES[i].equals(getPeriodType())){
				return i+1;
			}
		}
		return i;
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
	
	public void setDuration(int d){
		
		duration = d;
	}
	
	public static ArrayList<Program> getAll(ArrayList<Program> alist, SQLiteDatabase db) {
		ArrayList<Program> plist = (alist == null ? new ArrayList<Program>()
				: alist);

		plist.clear();
		Cursor c = db.rawQuery("SELECT * FROM " + TBL_NAME
				+ " ORDER BY " + DATE + " ASC", null);
		
		if (c.moveToFirst()) {
			
			if(!c.isNull(0)){
			
			do {
				
				String d = setDateStatic(c.getString(4));
				Program p = new Program(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3), d,
						c.getInt(5),c.getString(6), 
						c.getFloat(7), c.getFloat(8), c.getFloat(9),
						c.getFloat(10), c.getFloat(11), c.getFloat(12));
				plist.add(p);
				
				
			} while (c.moveToNext());
			
			}
		}
		c.close();
		
		return plist;
	}
	
	public void insert(SQLiteDatabase db) throws Exception{
		
		ContentValues values= new ContentValues();
		
		values.put(Program.ID_DEVICE, getId_device());
		values.put(Program.DEVICE_TYPE, getDeviceType());
		values.put(Program.DESCRIPTION, getDescription());
		values.put(Program.DATE, getDate());
		values.put(Program.DURATION, getDuration());
		values.put(Program.PERIOD_TYPE, getPeriodType());
		
		values.put(Program.TMAX, getTmax());
		values.put(Program.TMIN, getTmin());
		values.put(Program.HMAX, getHmax());
		values.put(Program.HMIN, getHmin());
		values.put(Program.HGMAX, getHgmax());
		values.put(Program.HGMIN, getHgmin());
		
		long rowId = db.insert(TBL_NAME, null, values);
	
		if (rowId < 0) {
			throw new Exception("Não foi possível inserir o programa na Base de Dados");
		}
		
		
	}
	
	
	public static Program getFromId(int id, SQLiteDatabase db){
		
		Program p=null;
		
		Cursor c = db.rawQuery("SELECT * FROM " + TBL_NAME
				+ " WHERE id="+ String.valueOf(id), null);
		
		if(c.moveToFirst()) {
			String d = setDateStatic(c.getString(4));
			p = new Program(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3), d,
					c.getInt(5), c.getString(6),
					c.getFloat(7), c.getFloat(8), c.getFloat(9),
					c.getFloat(10),c.getFloat(11), c.getFloat(12));
			
			
		}else{
			
		}
		
		c.close();
		
		return p;
		 
	}
	
	public void update(SQLiteDatabase db) throws Exception{
		
		ContentValues values= new ContentValues();
		
		values.put(Program.ID_DEVICE, getId_device());
		values.put(Program.DEVICE_TYPE, getDeviceType());
		values.put(Program.DESCRIPTION, getDescription());
		values.put(Program.DATE, getDate());
		values.put(Program.DURATION, getDuration());
		values.put(Program.PERIOD_TYPE, getPeriodType());
		
		values.put(Program.TMAX, getTmax());
		values.put(Program.TMIN, getTmin());
		values.put(Program.HMAX, getHmax());
		values.put(Program.HMIN, getHmin());
		values.put(Program.HGMAX, getHgmax());
		values.put(Program.HGMIN, getHgmin());
		
		long rowId = db.update(TBL_NAME, values, "id=" + String.valueOf(this.getIdProgram()), null);
		
		if (rowId < 0) {
			throw new Exception("Não foi possível actualizar o parametro na Base de Dados");
		}
	
	}
	
	public void update2(SQLiteDatabase db) throws Exception{
		
		ContentValues values= new ContentValues();
		
		values.put(Program.DATE, getDate());
		values.put(Program.DURATION, getDuration());
		
		long rowId = db.update(TBL_NAME, values, "id=" + String.valueOf(this.getIdProgram()), null);
		
		if (rowId < 0) {
			throw new Exception("Não foi possível actualizar o parametro na Base de Dados");
		}
	
	}

	
	
	public static void delete(int id, SQLiteDatabase db) throws Exception {
		
		long rowsAffected = db.delete(TBL_NAME,"id=" + String.valueOf(id), null);
		if(rowsAffected <= 0){
			throw new Exception("Não foi eliminar o Programa da Base de Dados");
		}
		
		
	}
	
	
	public static String XMLextract(SQLiteDatabase db){
		
		String xml_str="";
		
		ArrayList<Program> plist = null;
		plist = getAll(plist, db);
		
		 
		xml_str += "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>";
		xml_str += "<programs>";
		
		for(Program p:plist){
			
			xml_str += "<program>";
			xml_str += "<device>"+String.valueOf(p.getId())+"</device>";
			xml_str += "<date>"+p.getDate()+"</date>";
			xml_str += "<duration>"+String.valueOf(p.getDuration())+"</duration>";
			xml_str += "<period_type>"+String.valueOf(p.getIndexPeriodType())+"</period_type>";
			xml_str += "<tmax>"+String.valueOf(p.getTmax())+"</tmax>";
			xml_str += "<tmin>"+String.valueOf(p.getTmin())+"</tmin>";
			xml_str += "<hmax>"+String.valueOf(p.getHmax())+"</hmax>";
			xml_str += "<hmin>"+String.valueOf(p.getHmin())+"</hmin>";
			xml_str += "<hgmax>"+String.valueOf(p.getHgmax())+"</hgmax>";
			xml_str += "<hgmin>"+String.valueOf(p.getHgmin())+"</hgmin>";
			xml_str += "</program>";
		}
		
		xml_str += "</programs>";
		
		return xml_str;
		
	}

	public static byte[] getBytesFromFloat(float in){
		
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		DataOutputStream ds = new DataOutputStream(bas); 
		try {
			ds.writeFloat(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] bytes = bas.toByteArray();
		
		return bytes;
			
	}
	
}
