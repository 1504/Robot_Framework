package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Utils;

public class Drive implements Updatable {
	
	private static class DriveTask implements Runnable
	{

        private Drive _d;

        DriveTask(Drive d)
        {
            _d = d;
        }

        public void run()
        {
            _d.fastTask();
        }
    }
	
	private static Drive instance = new Drive();
	
	private Thread _taskThread;
	private volatile boolean _threadAlive = true;
	
    /**
     * Gets an instance of the Drive
     *
     * @return The Drive.
     */
    public static Drive getInstance()
    {
        return Drive.instance;
    }
    
	protected Drive()
	{
		_taskThread = new Thread(new DriveTask(this), "1504_Drive");
		_taskThread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2);
		_taskThread.start();
		
		Update_Semaphore.getInstance().register(this);
		
		DriveInit();
	}
	
	public void release()
	{
        _threadAlive = false;
    }
	
/** 
 * 
 * User code here
 * 
 */
	
	private DriverStation _ds = DriverStation.getInstance();
	private volatile boolean _new_data = false;
	private volatile double[] _input = {0.0, 0.0, 0.0};
	private volatile double _rotation_offset = 0.0;
	private volatile double[] _orbit_point = {0.0, 1.15};
	private DriveGlide _glide = new DriveGlide();
	
	private CANTalon[] _motors = new CANTalon[Map.DRIVE_MOTOR_PORTS.length];
	
	/**
	 * Set up everything that will be needed for the drive class
	 */
	private void DriveInit()
	{
		// Set up the drive motors
		for(int i = 0; i < Map.DRIVE_MOTOR_PORTS.length; i++)
		{
			_motors[i] = new CANTalon(Map.DRIVE_MOTOR_PORTS[i]);
		}
	}
	
	/**
	 * Method called when there is new data from the Driver Station.
	 * @see org.usfirst.frc.team1504.robot.Update_Semaphore
	 */
	public void semaphore_update()
	{
		// Get new values from the map
		// Do all configurating first (orbit, front, etc.)
		drive_inputs(IO.mecanum_input());
		// so "_new_data = true" at the VERY END OF EVERYTHING
	}
	
	/**
	 * Put data into the processing queue.
	 * Usable from both the semaphore and autonomous methods.
	 */
	public void drive_inputs(double[] inputs)
	{
		drive_inputs(inputs[0], inputs[1], inputs[2]);
	}
	public void drive_inputs(double forward, double right, double anticlockwise)
	{
		_input[0] = forward;
		_input[1] = right;
		_input[2] = anticlockwise;
		_new_data = true;
	}
	
	/**
	 * Programmatically switch the direction the robot goes when the stick gets pushed
	 */
	private double[] front_side(double[] dircn) {
		double[] dir_offset = new double[3];
		dir_offset[0] = dircn[0] * Math.cos(_rotation_offset) + dircn[1] * Math.sin(_rotation_offset);
		dir_offset[1] = dircn[1] * Math.cos(_rotation_offset) - dircn[0] * Math.sin(_rotation_offset);
		dir_offset[2] = dircn[2];
		return dir_offset;
	}
	
	public void setFrontAngle(double rotation_offset)
	{
		_rotation_offset = rotation_offset;
	}
	
	/**
	 * Orbit point
	 */
	private double[] orbit_point(double[] dircn) {
		double x = _orbit_point[0];
		double y = _orbit_point[1];

		double[] k = { y - 1, y + 1, 1 - x, -1 - x };

		double p = Math.sqrt((k[0] * k[0] + k[2] * k[2]) / 2) * Math.cos((Math.PI / 4) + Math.atan2(k[0], k[2]));
		double r = Math.sqrt((k[1] * k[1] + k[2] * k[2]) / 2) * Math.cos(-(Math.PI / 4) + Math.atan2(k[1], k[2]));
		double q = -Math.sqrt((k[1] * k[1] + k[3] * k[3]) / 2) * Math.cos((Math.PI / 4) + Math.atan2(k[1], k[3]));

		double[] corrected = new double[3];
		corrected[0] = (dircn[2] * r + (dircn[0] - dircn[2]) * q + dircn[0] * p) / (q + p);
		corrected[1] = (-dircn[2] * r + dircn[1] * q - (-dircn[1] - dircn[2]) * p) / (q + p);
		corrected[2] = (2 * dircn[2]) / (q + p);
		return corrected;
	}
	
	/**
	 * Detented controller correction methods (and helper methods)
	 */
	private double[] detents(double[] input) {

		double theta = Math.atan2(input[0], input[1]);

		double dx = correct_x(theta) * distance(input[1], input[0]) * 0.25;
		double dy = correct_y(theta) * distance(input[1], input[0]) * 0.25;

		double[] detented = new double[3];

		detented[0] = input[0] + dy; // y
		detented[1] = input[1] + dx; // x
		detented[2] = input[2];// angular

		return detented;
	}
	private double correct_x(double theta) {
		return -Math.sin(theta) * (-Math.sin(8 * theta) - 0.25 * Math.sin(4 * theta));
	}
	private double correct_y(double theta) {
		return Math.cos(theta) * (-Math.sin(8 * theta) - 0.25 * Math.sin(4 * theta));
	}
	private double distance(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}
	
	/**
	 * Convert the Forward, Right and Antoclockwise values into 4 motor outputs
	 * Input: Double array containing Forward, Right and Antoclockwise values
	 * Output: Double array containing motor output values
	 */
	private double[] outputCompute(double[] input) {
		double[] output = input;
		double max = Math.max(1.0, Math.abs(input[0]) + Math.abs(input[1]) + Math.abs(input[2]));

		output[0] = (input[0] + input[1] - input[2]) / max;
		output[1] = (input[0] - input[1] + input[2]) / max;
		output[2] = (input[0] - input[1] - input[2]) / max;
		output[3] = (input[0] + input[1] + input[2]) / max;
		
		return output;
	}
	
	/**
	 * Output values to motors.
	 * Input: array of motor values to output in Map.DRIVE_MOTOR order.
	 */
	private void motorOutput(double[] values) {
		for(int i = 0; i < _motors.length; i++)
		{
			// There are no Sync Groups for CANTalons. Apparently.
			_motors[i].set(values[i] * Map.DRIVE_OUTPUT_MAGIC_NUMBERS[i]);
		}
	}
	
	/**
	 * Dump class for logging
	 */
	public byte[] dump()
	{
		byte[] output = new byte[12];
		
		// Dump motor set point, current, and voltage
		for(int i = 0; i < Map.DRIVE_MOTOR.values().length; i++)
		{
			output[i] = Utils.double_to_byte(_motors[i].get()); // Returns as 11-bit, downconvert to 8
			output[i+1] = (byte) _motors[i].getOutputCurrent();
			output[i+2] = (byte) (_motors[i].getBusVoltage() * 10);
			// From CANTalon class: Bus voltage * throttle = output voltage
		}
		return output;
	}
	
	/**
	 * Update motors as fast as possible, but only compute all the joystick stuff when there's new data
	 */
	private void fastTask()
	{
		// Damn you, Java, and your lack of local static variables!
		double[] input;
		
		while(_threadAlive)
		{
			input = _input;
			
			if(_ds.isEnabled())
			{
				// Process new joystick data - only when new data happens
				if(_new_data)
				{
					// Switch front side if we need to
					if(_ds.isOperatorControl())
					{
						double rotation_offset = IO.front_side();
						if(!Double.isNaN(rotation_offset))
							setFrontAngle(rotation_offset);
					}
					
					// Detents
					input = detents(input);
					// Frontside
					input = front_side(input);
					// Orbit point
					input = orbit_point(input);
					// Glide
					input = _glide.gain_adjust(input);
					// Osc
				}
				// Ground speed offset
				// Output to motors - as fast as this loop will go
				motorOutput(outputCompute(input));
			}
		}
	}
}
