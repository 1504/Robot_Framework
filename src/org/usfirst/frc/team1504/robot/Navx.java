package org.usfirst.frc.team1504.robot;

import com.kauailabs.navx.frc.AHRS;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.SPI;
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
		/*System.out.println("Connected = " + _ahrs.isConnected());
		System.out.println("Calibrating = " + _ahrs.isCalibrating());
		System.out.println("Roll = " + _ahrs.getRoll());
		System.out.println("Pitch = " + _ahrs.getPitch());
		System.out.println("Yaw = " + _ahrs.getYaw());	
		System.out.println("is moving is " + _ahrs.isMoving());
		System.out.println("is rotating is " + _ahrs.isRotating());
		System.out.println("RawGyro_X = " + _ahrs.getRawGyroX());
		System.out.println("RawGyro_Y = " + _ahrs.getRawGyroY());
		System.out.println("RawGyro_Z = " + _ahrs.getRawGyroZ());*/
		System.out.println("RawMag_X = " + _ahrs.getRawMagX());
		System.out.println("RawMag_Y = " + _ahrs.getRawMagY());
		System.out.println("RawMag_Z = " + _ahrs.getRawMagZ());


		

        /*"RawGyro_Z",            ahrs.getRawGyroZ());
        "RawAccel_X",           ahrs.getRawAccelX());
         SmartDashboard.putNumber(   "RawAccel_Y",           ahrs.getRawAccelY());
         SmartDashboard.putNumber(   "RawAccel_Z",           ahrs.getRawAccelZ());
         SmartDashboard.putNumber(   "RawMag_X",             ahrs.getRawMagX());
         SmartDashboard.putNumber(   "RawMag_Y",             ahrs.getRawMagY());
         SmartDashboard.putNumber(   "RawMag_Z",             ahrs.getRawMagZ());
         SmartDashboard.putNumber(   "IMU_Temp_C",           ahrs.getTempC());
         SmartDashboard.putNumber(   "IMU_Timestamp",        ahrs.getLastSensorTimestamp());*/
         

		//System.out.println("firmware version is " + _ahrs.getFirmwareVersion());
	}
}