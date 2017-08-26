package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LeituraListAdapter extends ArrayAdapter<Leitura> {
	
	
	private final static String TAG = "LEITURALIST_ADAPTER";
	
	private ArrayList<Leitura> leituras;
	
	private Context context;
	
	public LeituraListAdapter(Context context, ArrayList<Leitura> objects) {
        super(context, R.layout.row_leituras, objects);

        this.leituras = objects;
        this.context = context;
        
        Log.d(TAG, "on construtor");
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "on getView");
        // preenche a view da cidade com os elementos do layout "row_layout" 
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_leituras, null);
        }
        
        

        Leitura leitura = leituras.get(position);
        
        Log.d(TAG, "leitura");

        if (leitura != null) {
        	
 
            TextView txtleituradescription = (TextView) v.findViewById(R.id.idleiturasdescription);
            TextView txtleituraparameters = (TextView) v.findViewById(R.id.idleiturasparameters);
            // ... e define os seus valores
            
            MyDbHelper dbHelper = new MyDbHelper(context);
    		SQLiteDatabase db = dbHelper.getWritableDatabase();
    		
    		
    		Device d = Device.get(leitura.getId_device(), db);
    		
    		
            String description="";
            String parameters="";
            
            if(d != null){
            	if(d.getTypeMS()=="MOTOR"){
            		description=String.format("%s : %s( %d )",d.getTypeMS(), d.getType(), d.getNumber());
            		parameters=String.format("%s : %d(min.)", leitura.getDate(), leitura.getDuration());
            	}
            	if(d.getTypeMS()=="SENSOR"){
            		description=String.format("%s : %s( %d )",d.getTypeMS(), d.getType(), d.getNumber());
            		parameters=String.format("%s : %.2f%s", leitura.getDate(), leitura.getValue(), Device.getSymbolValue(d.getType()));
            	}
            }
            
            txtleituradescription.setText(description);
            
            txtleituraparameters.setText(parameters);
            
            db.close();
        }

        return v;
    }
	
	@Override
	public void remove(Leitura object) {
		// TODO Auto-generated method stub
		super.remove(object);
		
		
	}


}
