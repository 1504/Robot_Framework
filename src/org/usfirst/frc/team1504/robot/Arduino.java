package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.I2C;

public class Arduino
{
	private I2C _bus = new I2C(I2C.Port.kOnboard, Map.ARDUINO_ADDRESS);//B-U+S using ascii decimal values
	
	boolean _read_status = false;
	
	public enum SHOOTER_STATUS {OFF, AIMING, AIM_LOCK};
	
	public enum FRONTSIDE_MODE {DEFAULT, REVERSE, OFF};
	
	public enum INTAKE_LIGHT_MODE {OFF, ON};
	
	private static Arduino instance = new Arduino();
	
	public static Arduino getInstance()
	{
		return Arduino.instance;
	}
	

/**
 * Requests for groundtruth data from the sensors.
 * @return the groundtruth data, 6 bytes: LEFT_X, LEFT_Y, LEFT_SQUAL, RIGHT_X, RIGHT_Y, RIGHT_SQUAL
 */
	public byte[] getSensorData()
	{	
		byte[] buffer = new byte[2];
		byte[] sensor_data = new byte[6];
		
		buffer[0] = Map.GROUNDTRUTH_ADDRESS;
		buffer[1] = 1;
		
		_read_status = _bus.transaction(buffer, buffer.length, sensor_data, sensor_data.length);
		return sensor_data;
	}

/**
 *Requests the images from the left and right sensors
 * @return the images, 648 bytes, representing the LEFT and RIGHT sensor images in order. The first 324 bytes are the LEFT sensor image, the next 324 bytes are the RIGHT sensor image.
 */
	public byte[] getSensorImage()
	{	
		byte[] buffer = new byte[1];
		byte[] img_data = new byte[648];
		
		buffer[0] = Map.GROUNDTRUTH_ADDRESS;
		buffer[1] = 2;
		
		_read_status = _bus.transaction(buffer, buffer.length, img_data, img_data.length);
		return img_data;

	}

/**
 * Sets the color of the main robot lights
 * @param R: integer from 0-255 indicating amount of red in the color
 * @param G: integer from 0-255 indicating amount of green in the color
 * @param B: integer from 0-255 indicating amount of blue in the color
 */
	public void setMainLightsColor(int R, int G, int B)
	{
		byte[] data = new byte[4];
		
		data[0] = Map.MAIN_LIGHTS_ADDRESS;
		data[1] = (byte) R;
		data[2] = (byte) G;
		data[3] = (byte) B;
		
		_bus.writeBulk(data);
	}
	
/**
 * Changes the lights to indicate which side is the FRONT of the robot.
 * @param mode: an int, either 0, 1, or 2: 0 for default frontside, 1 for reverse lights, and 2 for both lights off
 */
	public void setFrontsideLights(FRONTSIDE_MODE mode)
	{
		byte[] data = new byte[2];
		
		data[0] = Map.FRONTSIDE_LIGHTS_ADDRESS;
		data[1] = (byte) mode.ordinal();
		
		_bus.writeBulk(data);
	}
	
/**
 * Sets the intensity of the left and right gearholder lignts.
 * @param l_intensity: integer from 0-255 indicating intensity of the left gearholder light.
 * @param r_intensity: integer from 0-255 indicating intensity of the right gearholder light.
 */
	public void setGearLights(int l_intensity, int r_intensity)
	{
		byte[] data = new byte[3];
		
		data[0] = Map.FRONTSIDE_LIGHTS_ADDRESS;
		data[1] = (byte) l_intensity;
		data[2] = (byte) r_intensity;
		
		_bus.writeBulk(data);
	}
	
/**
 * Changes the shooter lights to indicate what the robot is doing.
 * @param mode: the mode the shooter is in, either OFF, AIMING, or AIM_LOCK
 */
	public void setShooterLightStatus(SHOOTER_STATUS mode)
	{
		byte[] data = new byte[2];
		
		data[0] = Map.SHOOTER_LIGHTS_ADDRESS;
		data[1] = (byte) mode.ordinal();
		
		_bus.writeBulk(data);
	}
	
/**
 * Turns the lights on to indicate the intake is running.
 * @param mode either OFF or ON
 */
	public void setIntakeLights(INTAKE_LIGHT_MODE mode)
	{
		byte[] data = new byte[3];
		
		data[0] = Map.INTAKE_LIGHTS_ADDRESS;
		data[1] = (byte) mode.ordinal();
		
		_bus.writeBulk(data);
	}
}
