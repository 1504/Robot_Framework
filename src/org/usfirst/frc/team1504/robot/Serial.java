package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.*;

public class Serial {
	
	SerialPort _serial = new SerialPort(1200, SerialPort.Port.kOnboard);
	public Serial()
	{
		
	}
	
	public double[] read()
	{
		
		return new double[] {0.0, 0.0};
	}
	
}
