package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.I2C;

public class Arduino
{
	private I2C _bus = new I2C(I2C.Port.kOnboard, Map.ARDUINO_ADDRESS);//B-U+S using ascii decimal values
	
	public enum SHOOTER_STATUS {OFF, AIMING, AIM_LOCK};
	
	public enum FRONTSIDE_MODE {DEFAULT, REVERSE, OFF};
	
	public enum GEAR_MODE {OFF, PULSE, INDIVIDUAL_INTENSITY};
	
	public enum INTAKE_LIGHT_MODE {OFF, ON};
	
	public enum PARTY_MODE {OFF, ON};
	private Lift _lift = Lift.getInstance();
	private Pickup _pickup = Pickup.getInstance();
	
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
		
		_bus.transaction(buffer, buffer.length, sensor_data, sensor_data.length);
		return sensor_data;
	}
	
	public void flash_colors() 
	{
		//flashes current colors
	}
	public double[] lift_gradient() 
	{
		double[] RGB = {0, 0, 0};
		int returnval;
		double margins = Map.LIFT_MAX_HEIGHT/3;
		if((0 < _lift.get_lift_height()) && (_lift.get_lift_height() < margins))
		{
			returnval = (int) ((_lift.get_lift_height() / margins) * 255);
			RGB[0] = 0;
			RGB[1] = returnval;
			RGB[2] = 0;
		}
		else if((margins < _lift.get_lift_height()) && (_lift.get_lift_height() < (2 * margins))) 
		{
			returnval = (int) ((_lift.get_lift_height() / (2 *margins)) * 255);
			RGB[0] = returnval;
			RGB[1] = returnval;
			RGB[2] = 0;
		}
		else if(((2 *margins) < _lift.get_lift_height()) && (_lift.get_lift_height() < (3 * margins))) 
		{
			returnval = (int) ((_lift.get_lift_height() / (3 *margins)) * 255);
			RGB[0] = 0;
			RGB[1] = 0;
			RGB[2] = returnval;
		}
		
		return RGB;
	}
	
	public double[] arm_gradient() 
	{
		double[] RGB = {0, 0, 0};
		int returnval;
		if((Map.ARM_DOWN_ANGLE < _pickup.arm_get_position()) && (_pickup.arm_get_position() < Map.ARM_MID_ANGLE))
		{
			returnval = (int) ((_pickup.arm_get_position() / Map.ARM_UP_ANGLE) * 255);
			RGB[0] = 0;
			RGB[1] = returnval;
			RGB[2] = 0;
		}
		else if((Map.ARM_MID_ANGLE < _pickup.arm_get_position()) && (_pickup.arm_get_position() < (2 * Map.ARM_DOWN_ANGLE))) 
		{
			returnval = (int) ((_pickup.arm_get_position() / (2 * Map.ARM_DOWN_ANGLE)) * 255);
			RGB[0] = returnval;
			RGB[1] = returnval;
			RGB[2] = 0;
		}
		else if(((2 * Map.ARM_DOWN_ANGLE) < _pickup.arm_get_position()) && (_pickup.arm_get_position() < (Map.ARM_UP_ANGLE))) 
		{
			returnval = (int) ((_pickup.arm_get_position() / (Map.ARM_UP_ANGLE)) * 255);
			RGB[0] = 0;
			RGB[1] = 0;
			RGB[2] = returnval;
		}
		
		return RGB;
	}
	
	public double[] return_colors() 
	{
		double[] RGB = {0,128,0};
		if(_lift.lift_state == Lift.lift_position.BOTTOM)
		{
			RGB[0] = 0;
			RGB[1] = 255;
			RGB[2] = 0;
		}
		else if(_lift.lift_state == Lift.lift_position.MIDDLE) {
			RGB[0] = 255;
			RGB[1] = 255;
			RGB[2] = 0;
		}
		else if(_lift.lift_state == Lift.lift_position.TOP) {
			RGB[0] = 255;
			RGB[1] = 0;
			RGB[2] = 0;
		}
		
		if(Pickup.intake_state == Pickup.intake.IN)
		{
			RGB[0] = 255;
			RGB[1] = 255;
			RGB[2] = 0;
		}
		else if(Pickup.intake_state == Pickup.intake.OUT)
		{
			RGB[0] = 0;
			RGB[1] = 0;
			RGB[2] = 255;
		}
		
		if(Pickup.arm_state == Pickup.arm_position.OFF) 
		{
			flash_colors();
		}
		return RGB;
	}

/**
 *Requests the images from the left and right sensors
 * @return the images, 648 bytes, representing the LEFT and RIGHT sensor images in order. 
 * The first 324 bytes are the LEFT sensor image, the next 324 bytes are the RIGHT sensor image.
 * The image is actually returned in 27 24-byte chunks, due to the 32-byte restriction on I2C transactions. 
 * First a 0 is written to READ OFFSET, then transactions with 1 through 27 are read from the 
 * sensor to build up the full 648-byte image array.
 */
	public synchronized char[] getSensorImage()
	{
		byte[] buffer = new byte[3];
		byte[] incoming_img_data = new byte[24];
		char[] final_image = new char[648];
		
		buffer[0] = Map.GROUNDTRUTH_ADDRESS;
		buffer[1] = 2;
		
		for(int i = 0; i <= 27; i++)
		{
			buffer[2] = (byte) i;
			if (i == 0)
			{
				_bus.writeBulk(buffer);
			}
			else
			{
				_bus.transaction(buffer, buffer.length, incoming_img_data, incoming_img_data.length);
				for(int j = 0; j < incoming_img_data.length; j++)
				{
					final_image[((i - 1) * 24) + j] = (char) incoming_img_data[j];
				}
			}
		}

		return final_image;

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
 * @param mode: either OFF, PULSE, or INDIVIDUAL INTENSITY mode. In PULSE mode, both lights pulse on and 
 * off and any other input is ignored. In INDIVIDUAL INTENSITY mode, l_intensity and r_intensity control 
 * the intensity level of the LEFT and RIGHT gearholder lights, respectively.
 * @param l_intensity: double from 0-1 indicating intensity of the left gearholder light, as a percentage.
 * @param r_intensity: double from 0-1 indicating intensity of the right gearholder light, as a percentage.
 */
	public void setGearLights(GEAR_MODE mode)
	{
		byte[] data = new byte[2];
		data[0] = Map.GEAR_LIGHTS_ADDRESS;
		data[1] = (byte) mode.ordinal();

		_bus.writeBulk(data);
	}
	public void setGearLights(GEAR_MODE mode, double l_intensity, double r_intensity)
	{
		byte[] data = new byte[4];
		data[0] = Map.GEAR_LIGHTS_ADDRESS;
		data[1] = (byte) mode.ordinal();
		
		l_intensity = Utils.snap(l_intensity, 0.0, 1.0);
		r_intensity = Utils.snap(r_intensity, 0.0, 1.0);
		
		byte l = (byte)(l_intensity * 255.0);
		byte r = (byte)(r_intensity * 255.0);
		
		data[2] = l;
		data[3] = r;
		
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
		byte[] data = new byte[2];
		
		data[0] = Map.INTAKE_LIGHTS_ADDRESS;
		data[1] = (byte) mode.ordinal();
		
		_bus.writeBulk(data);
	}

/**
 * Enables/Disables Party Mode.
 * @param mode: either OFF or ON.
 */
	public void setPartyMode(PARTY_MODE mode)
	{
		byte[] data = new byte[2];
		
		data[0] = Map.PARTY_MODE_ADDRESS;
		data[1] = (byte) mode.ordinal();
		
		_bus.writeBulk(data);
	}
	
/**
 * Sets the speed of the pulsing
 * @param speed: A number, 1 - 255, that controls how fast the pulsing happens. Higher is faster. 
 * Based on adding the pulse number to the current pulse byte at a 10ms update rate - so a pulse 
 * rate of 1 pulses OFF to ON in 2550ms.
 */
	public void setPulseSpeed(int speed)
	{
		byte[] data = new byte[2];
		
		data[0] = Map.PULSE_SPEED_ADDRESS;
		data[1] = (byte) speed;
		
		_bus.writeBulk(data);
	}
}
