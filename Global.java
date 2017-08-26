package pt.ipp.isep.dei.formacao.android.culturecontroler;


public class Global {
	
	
	
	public static final byte MESSAGE_TYPE_REQUEST_LAST_VALUES = 0x30;
	public static final byte MESSAGE_TYPE_REQUEST_HISTORIC_VALUES = 0x31;
	
	public static final byte MESSAGE_TYPE_SEND_PARAMETERS = 0x32;
	public static final byte MESSAGE_TYPE_SEND_PROGRAMS = 0x33;
	
	public static final byte MESSAGE_TYPE_ACTION = 0x34;
	
	
	public static final byte MESSAGE_TYPE_RESPONSE_LAST_VALUES = 0x35;
	public static final byte MESSAGE_TYPE_RESPONSE_SENSOR_GROUP_1_HISTORIC_VALUES = 0x36;
	public static final byte MESSAGE_TYPE_RESPONSE_SENSOR_GROUP_2_HISTORIC_VALUES = 0x37;
	public static final byte MESSAGE_TYPE_RESPONSE_SENSOR_GROUP_3_HISTORIC_VALUES = 0x38;
	public static final byte MESSAGE_TYPE_RESPONSE_MOTOR_HISTORIC_VALUES = 0x39;
	
	public static final byte MESSAGE_TYPE_SEND_PROGRAMS_RESET = 0x40;
	
	
	public static final byte SENSOR	= 0x20;
	public static final byte SENSOR_TYPE_TEMPERATURE	= 0x21;
	public static final byte SENSOR_TYPE_HUMIDITY		= 0x22;
	public static final byte SENSOR_TYPE_HUMIDITY_G		= 0x23;
	public static final byte SENSOR_TYPE_WATER_LEVEL	= 0x24;
	public static final byte SENSOR_TYPE_ACTUATOR		= (byte) 0xF0;//motor
	
	public static final byte SENSOR_1_TEMPERATURE_SENSOR 	= 0x01;
	public static final byte SENSOR_2_TEMPERATURE_SENSOR 	= 0x02;
	public static final byte SENSOR_3_TEMPERATURE_SENSOR 	= 0x03;
	
	public static final byte SENSOR_1_HUMIDITY_SENSOR		= 0x04;
	public static final byte SENSOR_2_HUMIDITY_SENSOR		= 0x05;
	public static final byte SENSOR_3_HUMIDITY_SENSOR		= 0x06;
	
	public static final byte SENSOR_1_HUMIDITY_G_SENSOR		= 0x07;
	public static final byte SENSOR_2_HUMIDITY_G_SENSOR		= 0x08;
	public static final byte SENSOR_3_HUMIDITY_G_SENSOR		= 0x09;
	
	public static final byte SENSOR_WATER_LEVEL			= 0x10;

	
	public static final byte SET_MOTOR_ON =0x73;
	public static final byte SET_MOTOR_OFF=0x74;
	
	public static final int REQUEST_ENABLE_BT = 2;
	
	
}
