package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Device {
	
	public static String[] TYPES = {"TEMPERATURE", "HUMIDITY", "HUMIDITY_G", "WATER_LEVEL", "ACTUATOR"};
	
	private int id;
	
	private int number;
	
	private String type;
	
	
	public final static String TBL_NAME = "tbl_device";
	public final static String ID = "id";
	public final static String NUMBER = "number";
	public final static String TYPE = "type";
	
	
	
	public final static String[] FIELDS = { ID, NUMBER, TYPE};
	
	
	public Device(int id, int number, String type){
		
		setId(id);
		setNumber(number);
		setType(type);
		
	}
	
	public Device(int id){
		setId(id);
	}
	
	public void setId(int id){
		
		this.id = id;
	}
	
	public void setNumber(int number){
		this.number = number;
	}
	
	public void setType(String type){
		
		List<String> types = Arrays.asList(TYPES);;
		
		for(String t:types){
			if(t.equals(type)){
				this.type = type;
			}
		}
	}

	public static String getTypes(int i) {
		return TYPES[i];
	}

	public String getType() {
		return type;
	}

	public int getId() {
		return id;
	}
	
	public int getNumber() {
		return number;
	}
	
	
	public static boolean isSensor(Device d){
		
		
		if(d instanceof Sensor){
				return true;
		}
		return false;
	}
	
	public static boolean isMotor(Device d){
		if(d instanceof Motor){
			return true;
		}
		return false;
	}
	
	public boolean isMotor(){
		
		if(this.getType().equals(Device.getTypes(4))){return true;}
		return false;
	}
	
	public boolean isSensor(){
		
		if(this.getType().equals(Device.getTypes(0)) ||
		this.getType().equals(Device.getTypes(1)) ||
		this.getType().equals(Device.getTypes(2)) ||
		this.getType().equals(Device.getTypes(3))){
			return true;
		}
		return false;
	}

	
	public static int getTypeDeviceProgram(String d){
		
		String[] types = {"SENSOR","MOTOR"};
		if(types[0].equals(d)){return 0;};
		if(types[1].equals(d)){return 1;};
		return 0;
	}
	
	public String getTypeMS(){
		
		if(isMotor()){ return "MOTOR";}
		if(isSensor()){ return "SENSOR"; }
		return null;
	}
	
	
	
	public static Device get(int id, SQLiteDatabase db) {
		Device d = null;

		Cursor c = db.query(TBL_NAME, FIELDS, ID + "=" + id, null, null, null, null);
		if (c.moveToFirst()) {
			
			if(!c.isNull(0)){
				d = new Device(c.getInt(0), c.getInt(1), c.getString(2));
			}
		}
		c.close();

		return d;
	}
	
	
	
	public static ArrayList<Device> getAll(ArrayList<Device> alist, SQLiteDatabase db) {
		ArrayList<Device> dlist = (alist == null ? new ArrayList<Device>()
				: alist);

		dlist.clear();
		Cursor c = db.rawQuery("SELECT * FROM " + TBL_NAME
				+ " ORDER BY " + ID + " ASC", null);
		
		if (c.moveToFirst()) {
			
			if(!c.isNull(0))
			{
				
			
			do {
				
				Device d = new Device(c.getInt(0), c.getInt(1), c.getString(2));
				dlist.add(d);
				
				
			} while (c.moveToNext());
			
			}
		}
		c.close();
		return dlist;
	}
	
	public byte getDevice(){
		
		if(id>0 && id<10)
		{
			switch(id){
				case 1:
					return Global.SENSOR_1_TEMPERATURE_SENSOR;
				case 2:
					return Global.SENSOR_2_TEMPERATURE_SENSOR;
				case 3:
					return Global.SENSOR_3_TEMPERATURE_SENSOR;
				case 4:
					return Global.SENSOR_1_HUMIDITY_SENSOR;
				case 5:
					return Global.SENSOR_2_HUMIDITY_SENSOR;
				case 6:
					return Global.SENSOR_3_HUMIDITY_SENSOR;
				case 7:
					return Global.SENSOR_1_HUMIDITY_G_SENSOR;
				case 8:
					return Global.SENSOR_2_HUMIDITY_G_SENSOR;
				case 9:
					return Global.SENSOR_3_HUMIDITY_G_SENSOR;
				default:
					return 0;
			}
		}
		else{
			return 0;
		}
		
	}
	
	public static Device getDeviceFromAddr(byte addr, SQLiteDatabase db){
	
		int id = 0;
		
		switch(addr){
				case Global.SENSOR_1_TEMPERATURE_SENSOR:
					id = 1;
				case Global.SENSOR_2_TEMPERATURE_SENSOR:
					id = 2;
				case Global.SENSOR_3_TEMPERATURE_SENSOR:
					id = 3;
				case Global.SENSOR_1_HUMIDITY_SENSOR:
					id = 4;
				case Global.SENSOR_2_HUMIDITY_SENSOR:
					id = 5;
				case Global.SENSOR_3_HUMIDITY_SENSOR:
					id = 6;
				case Global.SENSOR_1_HUMIDITY_G_SENSOR:
					id = 7;
				case Global.SENSOR_2_HUMIDITY_G_SENSOR:
					id = 8;
				case Global.SENSOR_3_HUMIDITY_G_SENSOR:
					id = 9;
				case Global.SENSOR_TYPE_ACTUATOR:
					id = 10;
		}
			
			
		Device d = null;
		if(id != 0){
				d = Device.get(id, db);
		}
		return d;
			
	}
	
	public static String getSymbolValue(String devicetype){
		
		char degree = '\u00B0';
		
		if(devicetype.equals("TEMPERATURE")){return String.valueOf(degree) + "C";}
		if(devicetype.equals("HUMIDITY") || devicetype=="HUMIDITY_G"){return "%";}
		
		return null;
	}
	

}
