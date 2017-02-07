package org.usfirst.frc.team1504.robot;

import com.kauailabs.navx.frc.AHRS;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.SerialPort;

public class Navx implements Updatable
{
	private static AHRS _ahrs = new AHRS(SerialPort.Port.kUSB, AHRS.SerialDataType.kRawData, (byte)10);
	private static Navx _instance = new Navx();
	
	private Navx()
	{
		Update_Semaphore.getInstance().register(this);

		System.out.println("Navx board initialzed");
		//getData();
	}
	
	public static Navx getInstance()
	{
		return _instance;
	}
	
	public void semaphore_update()
	{
		getData();
	}
	
	public void getData()
	{
		System.out.println("RawMag_X = " + _ahrs.getRawMagX());
		System.out.println("RawMag_Y = " + _ahrs.getRawMagY());
		System.out.println("RawMag_Z = " + _ahrs.getRawMagZ());
	}
}