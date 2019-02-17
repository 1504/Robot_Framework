package org.usfirst.frc1504.Robot2019;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.*;

public class Serial {
	
	SerialPort _serial = new SerialPort(1200, SerialPort.Port.kOnboard);
	
	public byte[] read()
	{
		byte[] info = _serial.read(2); //TODO change byte number
		if(info.length == 0)
		{
			return new byte[] {0, 0};
		}
		return info;
	}
	
}
