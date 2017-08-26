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

public class ProgramListAdapter extends ArrayAdapter<Program> {
	
	private final static String TAG = "PROGRAMLIST_ADAPTER";
	
	private ArrayList<Program> programs;
	
	private Context context;
	
	public ProgramListAdapter(Context context, ArrayList<Program> objects) {
        super(context, R.layout.row_programs, objects);

        this.programs = objects;
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
            v = vi.inflate(R.layout.row_programs, null);
        }
        
        

        Program program = programs.get(position);
        
        Log.d(TAG, program.getDescription());

        if (program != null) {
        	
 
            TextView txtprogramdescription = (TextView) v.findViewById(R.id.idprogramdescription);
            TextView txtprogramdevice = (TextView) v.findViewById(R.id.idprogramdevice);
            TextView txtprogramtime = (TextView) v.findViewById(R.id.idprogramtime);
            TextView txtprogramduration = (TextView) v.findViewById(R.id.idprogramduration);
            // ... e define os seus valores
            
            MyDbHelper dbHelper = new MyDbHelper(context);
    		SQLiteDatabase db = dbHelper.getWritableDatabase();
    		
    		
    		Device d = Device.get(program.getId_device(), db);
    		
    		
            String description="";
            String device="";
            String time="";
            String duration="";
            
            if(d != null){
            	description=program.getDescription();
            	device=String.format("%s( %d )",d.getTypeMS(), d.getNumber());
            	time=program.getTime();
            	duration=String.format("%d min.", program.getDuration());
            }
            
            txtprogramdescription.setText(description);
            txtprogramdevice.setText(device);
            txtprogramtime.setText(time);
            txtprogramduration.setText(duration);
            
            db.close();
        }

        return v;
    }
	
	@Override
	public void remove(Program object) {
		// TODO Auto-generated method stub
		super.remove(object);
		
		
	}


}
