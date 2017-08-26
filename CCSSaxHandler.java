package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// handler para parsing de XML desenvolvido por WebService 
// faz o parsing 
public class CCSSaxHandler extends DefaultHandler {

	private String xml_str;
	
	private boolean in_leitura = false;
	private boolean in_device = false;
	private boolean in_date = false;
	private boolean in_duration = false;
	private boolean in_value = false;

	private boolean device_valido = false;
	private boolean duration_valido = false;
	private boolean value_valido = false;
	
	private SQLiteDatabase db = null;
	private ContentValues cv = null;

	public CCSSaxHandler(SQLiteDatabase db) {
		this.db = db;
	}

	// actualizar so para um lugar
	public void InserirLeituras() {
		try {
			if(xml_str!=""){

				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
	
				// handler para lidar com o parsing do XML
				xr.setContentHandler(this);
				xr.parse(this.xml_str);
			
			}

		} catch (Exception e) {
			// FIXME e pode ser null?
			StackTraceElement[] st = e.getStackTrace();
			Log.d("CCS", e.toString());
			for (StackTraceElement el : st)
				Log.d("CCS", el.toString());
		}
	}

	@Override
	public void startDocument() throws SAXException {
		// NADA
	}

	@Override
	public void endDocument() throws SAXException {
		// NADA
	}

	// invocado na abertura de cada tag XML
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {

		// abertura da tag "weather" ou "current_condition"
		if (localName.equals("leitura")) {
			cv = new ContentValues();
			device_valido = false;
			duration_valido = false;
			value_valido = false;
			
		}

		if (localName.equals("device")) {
			in_device = true;
		}

		if (localName.equals("date"))
			in_date = true;

		if (localName.equals("duration"))
			in_duration = true;

		if (localName.equals("value"))
			in_value = true;
	}

	// invocado no encerramento de cada tag XML
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("device")) {
			in_device = false;
			if(device_valido && duration_valido && value_valido){
				try {
					Leitura.insertCv(cv, db);
					Log.d("TAG","Leitura inserida");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("TAG","Erro na inserção da leitura");
				}
			}
		}

		if (localName.equals("device")) {
			in_device = false;
		}

		if (localName.equals("date"))
			in_date = false;

		if (localName.equals("duration"))
			in_duration = false;

		if (localName.equals("value"))
			in_value = false;

	}

	// invocado para se obter o conteúdo que está no interior das tags XML
	@Override
	public void characters(char ch[], int start, int length) {

		if (in_leitura) {
			
			if (in_device) {
				
				String device_str = new String(ch, start, length).trim();
				try {
					if(Device.get(Integer.getInteger(device_str), db) != null){
						cv.put(Leitura.ID_DEVICE, Integer.valueOf(device_str));
						device_valido=true;
					}else{
						
					}
				} catch (Exception e) {
					Log.d("TAG","Erro na inserção do device");
				}
			}

			if (in_date) {
				String date_str = new String(ch, start, length).trim();
				cv.put(Leitura.DATE, date_str);
			}

			if (in_duration) {
				String duration_str = new String(ch, start, length).trim();
				try{
					cv.put(Leitura.DURATION, duration_str);
					duration_valido=true;
				}catch(Exception e){
					Log.d("TAG","Erro na inserção da duração");
				}
			}

			if (in_value) {
				String value_str = new String(ch, start, length).trim();
				try{
					cv.put(Leitura.VALUE, Float.valueOf(value_str));
					value_valido=true;
				} catch(Exception e){
					Log.d("TAG","Erro na inserção do valor");
				}
			
			}
			
		}
	}
}
