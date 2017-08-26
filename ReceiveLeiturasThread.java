package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.io.InputStream;

import android.util.Log;

public class ReceiveLeiturasThread extends Thread {
	
	private static final String TAG = ReceiveLeiturasThread.class.getSimpleName();
	
	private CultureControlService service;
	private InputStream mInStream;
	
	private int errorCount = 0;
	private boolean canceled = false;

	
	
	public ReceiveLeiturasThread(CultureControlService service, InputStream inStream) {
		this.service = service;
		this.mInStream = inStream;
	}
	
	public void cancel() {
		canceled = true;
		}
	
	
	@Override
	public void run() {
	
		
		byte[] msg = new byte[32];//char(19) -> char ocupa 1 byte 
		
		
		while(!canceled && errorCount < 28) {
			try {
				
				for(int i=0;i<=27 && !canceled;i++) {
					
					if(i==0){
						byte b1 = (byte) mInStream.read();
						msg[0] = b1;	
					}
					//date
					if(i>=2 && i<=18){
						byte b2 = (byte) mInStream.read();
						System.arraycopy(b2,2,msg[1],i-2,1);
						
						//subbstituir i por i-3 ?
					}
					//s1t
					if(i>=20 && i<=21){
						byte b3 = (byte) mInStream.read();
						System.arraycopy(b3,20,msg[2],0,2);
						
					}
					//s1h
					if(i>=23 && i<=24){
						byte b4 = (byte) mInStream.read();
						System.arraycopy(b4,23,msg[3],0,2);
					}
					//s1hg
					if(i>=26 && i<=27){
						byte b5 = (byte) mInStream.read();
						System.arraycopy(b5,26,msg[4],0,2);
					}
					
					
					
				}
				
				//if(!canceled) service.receivedLeiturasMsg(msg);
				
			} catch(Exception e){
				if(!isCanceled()) {
					errorCount++;
				}
			}
		}
		
		service.receivedDone(errorCount);
		
	}
	

	
	public boolean isCanceled() {
		return canceled;
	}
	
	
	public int getErrorCount() {
		return errorCount;
	}
	
	

}
