package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.XMLFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Xml;

public class Parameters {
	
	private int id;
	private String type;
	private int value;
	
	
	public final static String TBL_NAME = "tbl_params";
	public final static String ID = "id";
	public final static String TYPE = "type";
	public final static String VALUE = "value";
	public final static String[] FIELDS = { ID, TYPE, VALUE };
	
	private static String[] TYPES = {
		"MINIMUM_WATER_LEVEL",
		"MAX_TIME_ON" ,
		"HUMIDITY"
	};
	
	public Parameters(int id, String type, int value){
		setId(id);
		setType(type);
		setValue(value);
	}
	
	public void setId(int id){
		this.id=id;
	}
	
	public void setType(String type){
		
		List<String> types = Arrays.asList(TYPES);
		
		for(String t:types){
			if(t.equals(type)){
				this.type = type;
			}
		}
	}
	
	public void setValue(int v){
		this.value = v;
	}
	
	public int getId(){
		return id;
	}
	
	public String getType(){
		return type;
	}
	
	public int getValue(){
		return value;
	}
	
	public static String getTypes(int i){
		
		return TYPES[i];
	}
	
	

	public static ArrayList<Parameters> getAll(ArrayList<Parameters> alist, SQLiteDatabase db) {
		ArrayList<Parameters> plist = (alist == null ? new ArrayList<Parameters>()
				: alist);

		plist.clear();
		Cursor c = db.rawQuery("SELECT * FROM " + TBL_NAME
				+ " ORDER BY " + ID + " ASC", null);
		
		if (c.moveToFirst()) {
			
			if(!c.isNull(0)){
			
			do {
				
				Parameters p = new Parameters(c.getInt(0), c.getString(1), c.getInt(2));
				plist.add(p);
				
				
			} while (c.moveToNext());
			
			}
		}
		c.close();
		return plist;
	}
	
	public void update(SQLiteDatabase db) throws Exception{
		
		ContentValues values= new ContentValues();
		
		values.put(Parameters.TYPE, getType());
		values.put(Parameters.VALUE, getValue());
		
		long rowId = db.update(TBL_NAME, values, "id=" + String.valueOf(getId()), null);
		
		if (rowId < 0) {
			throw new Exception("Não foi possível actualizar o parametro na Base de Dados");
		}
	
	}
	
	public static Parameters get(int id, SQLiteDatabase db){
		
		Parameters p=null;
		
		Cursor c = db.rawQuery("SELECT * FROM " + TBL_NAME
				+ " WHERE id="+String.valueOf(id), null);
		
		if(c.moveToFirst()) {
			
			
			p = new Parameters(c.getInt(0), c.getString(1), c.getInt(2));
			
			
		}else{
			
		}
		
		c.close();
		
		return p;
		 
	}
	
	public static Parameters getByType(String type, SQLiteDatabase db){
		
		Parameters p=null;
		
		Cursor c = db.rawQuery("SELECT * FROM " + TBL_NAME
				+ " WHERE type="+type, null);
		
		if(c.moveToFirst()) {
			
			
			p = new Parameters(c.getInt(0), c.getString(1), c.getInt(2));
			
			
		}else{
			
		}
		
		c.close();
		
		return p;
		 
	}
	
	public static String XMLextract(SQLiteDatabase db){
		
		String xml_str="";
		
		xml_str += "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>";
		
		Parameters p1=Parameters.get(1, db);
		Parameters p2=Parameters.get(2, db);
		
		xml_str += "<parameters>";	
		xml_str += "<"+TYPES[0]+">"+String.valueOf(p1.value)+"</"+TYPES[0]+">";
		xml_str += "<"+TYPES[1]+">"+String.valueOf(p2.value)+"</"+TYPES[1]+">";
		xml_str += "</parameters>";
		
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
