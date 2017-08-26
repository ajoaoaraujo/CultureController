package pt.ipp.isep.dei.formacao.android.culturecontroler;

import java.io.InputStream;

public class ReceiveThread extends Thread {
	
	private static final String TAG = ReceiveThread.class.getSimpleName();
	
	private CultureControlService service;
	private InputStream mInStream;
	
	private int errorCount = 0;
	private boolean canceled = false;

	
	
	public ReceiveThread(CultureControlService service, InputStream inStream) {
		this.service = service;
		this.mInStream = inStream;
	}
	
	public void cancel() {
		canceled = true;
		}
	
	@Override
	public void run() {
		
		byte[] msg = new byte[64];//response 3 bytes or 28 bytes
	
		while(!canceled && errorCount < 28) {
			try {
				byte b = (byte) mInStream.read();
				msg[0] = b;
				
				if(b == Global.MESSAGE_TYPE_RESPONSE_LAST_VALUES){
					for(int i=1;i<3 && !canceled;i++) {
						byte b1 = (byte) mInStream.read();
						msg[i] = b1;
					}
					
					if(!canceled) service.receivedMsg(msg);
				}
				if(b == Global.MESSAGE_TYPE_RESPONSE_SENSOR_GROUP_1_HISTORIC_VALUES || 
						b == Global.MESSAGE_TYPE_RESPONSE_MOTOR_HISTORIC_VALUES){
						for(int i=0;i<28 && !canceled;i++) {
							byte b2 = (byte) mInStream.read();
							msg[i] = b2;
						}
					if(!canceled) service.receivedLeiturasMsg(msg);
				}
				
				
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
