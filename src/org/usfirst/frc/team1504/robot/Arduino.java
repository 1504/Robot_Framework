package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.I2C;

public class Arduino
{
	private I2C _bus = new I2C(I2C.Port.kOnboard, 64);//B-U+S using ascii decimal values
	
	boolean _read_status = false;
	private byte _transaction_address = 0;
	private byte _LED_address = 75; //L-E+D using ascii decimal values
	private byte _groundtruth_address = 89; //S-E+N-S+O-R+S, using ascii decimal values
	
	private static Arduino instance = new Arduino();
	
	public static Arduino getInstance()
	{
		return Arduino.instance;
	}
	
	public void setLEDMode()
	{
		_transaction_address = _LED_address; 
	}
	
	
	public byte[] getSensor()
	{
		_transaction_address = _groundtruth_address;
		
		byte[] buffer = new byte[1];
		byte[] sensor_data = new byte[6];
		for (int i = 0; i < sensor_data.length; i++)
			{
			sensor_data[i] = 0;
			}
		buffer[0] = _transaction_address;
		_read_status = _bus.transaction(buffer, buffer.length, sensor_data, sensor_data.length);
		return sensor_data;
	}
	
	
}
