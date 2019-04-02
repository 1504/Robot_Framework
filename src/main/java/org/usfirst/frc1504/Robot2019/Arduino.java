package org.usfirst.frc1504.Robot2019;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;

public class Arduino
{
	private I2C _bus = new I2C(I2C.Port.kOnboard, Map.ARDUINO_ADDRESS);//B-U+S using ascii decimal values

	private static final Arduino instance = new Arduino();
	private Lights_Thread _lights;
	private Thread _task_thread;
	
	private Arduino()
	{
		_lights = new Lights_Thread();
		_task_thread = new Thread(_lights, "1504_Arduino Lights Task Thread");
		_task_thread.start();

		System.out.println("Arduino initialized");
	}

	public static Arduino getInstance()
	{
		return instance;
	}

	public static void initialize()
	{
		getInstance();
	}
	

	private class Lights_Thread implements Runnable
	{
		private int[][] _arm_colors = {{255,115,0}, {255,255,0}, {255,105,180}, {255,0,255}, {64,224,208}};
		private int[][] _post_colors = {{103,255,0}, {255,15,2}, {255,0,230}, {160,255,141}};

		private Arduino _arduino;
		private Hatch _hatch;
		private Elevator _elevator;
		private Drive _drive;

		private Hatch.HATCH_STATE _last_hatch = null;
		private Elevator.ELEVATOR_MODE _last_elevator_mode = null;
		private int _last_elevator_setpoint = -1;
		private boolean _last_elevator_moving;
		private boolean _last_drive;

		Lights_Thread()
		{
			_arduino = Arduino.getInstance();
			_hatch = Hatch.getInstance();
			_elevator = Elevator.getInstance();
			_drive = Drive.getInstance();
		}
		
		public void run()
		{
			System.out.println("Lights task thread initialized");
			while(true)
			{
				// Set post color
				if(_hatch.getState() != _last_hatch || _last_elevator_mode != _elevator.getMode())
				{
					if(_elevator.getMode() == Elevator.ELEVATOR_MODE.CARGO)
						_arduino.setPostLightsColor(_post_colors[3][0], _post_colors[3][1], _post_colors[3][2]);
					else
					{
						int mode = _hatch.getState().ordinal();
						_arduino.setPostLightsColor(_post_colors[mode][0], _post_colors[mode][1], _post_colors[mode][2]);
					}
				}

				// Set arm color
				if(_last_elevator_setpoint != _elevator.getSetpoint())
				{
					int mode = _elevator.getSetpoint();
					_arduino.setArmLightsColor(_arm_colors[mode][0] , _arm_colors[mode][1], _arm_colors[mode][2]);
				}

				// Blink if arms are in motion
				if(_last_elevator_moving != _elevator.getMoving())
					_arduino.setArmLightsState(!_elevator.getMoving());

				// Blink if we're auto-aligning
				if(_last_drive != _drive.line_tracking())
					_arduino.setPostLightsState(_drive.line_tracking());

				_last_hatch = _hatch.getState();
				_last_elevator_mode = _elevator.getMode();
				_last_elevator_setpoint = _elevator.getSetpoint();
				_last_elevator_moving = _elevator.getMoving();
				_last_drive = _drive.line_tracking();

				Timer.delay(.02);
			}
		}
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
 * Sets the color of the arm lights
 * @param R: integer from 0-255 indicating amount of red in the color
 * @param G: integer from 0-255 indicating amount of green in the color
 * @param B: integer from 0-255 indicating amount of blue in the color
 */
	public void setArmLightsColor(int R, int G, int B)
	{
		byte[] data = new byte[4];
		
		data[0] = Map.ARM_LIGHTS_ADDRESS;
		data[1] = (byte) R;
		data[2] = (byte) G;
		data[3] = (byte) B;
		
		_bus.writeBulk(data);
	}

/**
 * Sets the color of the post lights
 * @param R: integer from 0-255 indicating amount of red in the color
 * @param G: integer from 0-255 indicating amount of green in the color
 * @param B: integer from 0-255 indicating amount of blue in the color
 */
	public void setPostLightsColor(int R, int G, int B)
	{
		byte[] data = new byte[4];
		
		data[0] = Map.POST_LIGHTS_ADDRESS;
		data[1] = (byte) R;
		data[2] = (byte) G;
		data[3] = (byte) B;
		
		_bus.writeBulk(data);
	}

/**
 * Sets the blink mode of the arm lights
 */
	public void setArmLightsState(boolean blink)
	{
		byte[] data = new byte[2];
		
		data[0] = Map.ARM_MODE_ADDRESS;
		data[1] = (byte) (blink ? 1 : 0);
		
		_bus.writeBulk(data);
	}

/**
 * Sets the blink mode of the post lights
 */
	public void setPostLightsState(boolean blink)
	{
		byte[] data = new byte[2];
		
		data[0] = Map.POST_MODE_ADDRESS;
		data[1] = (byte) (blink ? 1 : 0);
		
		_bus.writeBulk(data);
	}

/**
 * Enables/Disables Party Mode.
 * @param mode: either TRUE or FALSE for OFF or ON.
 */
	public void setPartyMode(boolean mode)
	{
		byte[] data = new byte[2];
		
		data[0] = Map.PARTY_MODE_ADDRESS;
		data[1] = (byte) (mode ? 1 : 0);
		
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
