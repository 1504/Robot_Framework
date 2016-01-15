package org.usfirst.frc.team1504.robot;

import java.nio.ByteBuffer;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;

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
	
	private static final Drive instance = new Drive();
	
	private Thread _task_thread;
	private Thread _dump_thread;
	private volatile boolean _thread_alive = true;
	
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
		_task_thread = new Thread(new DriveTask(this), "1504_Drive");
		_task_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2);
		_task_thread.start();
		
		Update_Semaphore.getInstance().register(this);
		
		DriveInit();
		
		System.out.println("Drive Initialized");
	}
	
	public void release()
	{
		_thread_alive = false;
    }
	
/** 
 * 
 * User code here
 * 
 */
	
	private DriverStation _ds = DriverStation.getInstance();
	private Logger _logger = Logger.getInstance();
	private volatile boolean _new_data = false;
	private volatile double[] _input = {0.0, 0.0};
	private volatile double _rotation_offset = 0.0;
	private DriveGlide _glide = new DriveGlide();
	private Groundtruth _groundtruth = Groundtruth.getInstance();
	
	private CANTalon[] _motors = new CANTalon[Map.DRIVE_MOTOR_PORTS.length];
	
	private volatile int _loops_since_last_dump = 0;
	
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
		if(!_ds.isAutonomous())
			drive_inputs(IO.drive_input());
		// so "_new_data = true" at the VERY END OF EVERYTHING
	}
	
	/**
	 * Put data into the processing queue.
	 * Usable from both the semaphore and autonomous methods.
	 */
	public void drive_inputs(double forward, double anticlockwise)
	{
		double[] inputs = {forward, anticlockwise};
		drive_inputs(inputs);
	}
	public void drive_inputs(double[] input)
	{
		if(_new_data)
			return;
		
		_input = input;
		_new_data = true;
	}
	
	/**
	 * Programmatically switch the direction the robot goes when the stick gets pushed
	 */
	private double[] front_side(double[] input) {
		double[] dir_offset = input;
		if(_rotation_offset == 180.0)
			dir_offset[0] *= -1.0;
		return dir_offset;
	}
	
	public void setFrontAngle(double rotation_offset)
	{
		_rotation_offset = rotation_offset;
	}
	
	/**
	 * Detented controller correction methods (and helper methods)
	 */
	private double[] detents(double[] input) {

		double theta = Math.atan2(input[0], input[1]);

		double dx = correct_x(theta) * distance(input[1], input[0]) * 0.25;
		double dy = correct_y(theta) * distance(input[1], input[0]) * 0.25;

		double[] detented = new double[2];

		detented[0] = input[0] + dy; // y
		detented[1] = input[1] + dx; // x

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
	 * Ground truth sensor corrections
	 * @param input - Joystick input to correct towards
	 * @return
	 */
	private double[] groundtruth_correction(double[] input)
	{
		double[] normal_input = input;
		double[] output = input;
		double[] speeds = _groundtruth.getSpeed();
		
		// Normalize the inputs and actual speeds
		if(groundtruth_normalize(speeds) == 0)
			return input;
		groundtruth_normalize(normal_input);
		
		// Apply P(ID) correction factor to the joystick values
		// TODO: Determine gain constant and add to the Map
		for(int i = 0; i < input.length; i++)
			output[i] += (normal_input[i] - speeds[i]) * -0.01;
		
		return output;
	}
	
	/**
	 * Normalization function for arrays to normalize full scale to +- 1 <br>
	 * Note: THIS FUNCTION OPERATES ON THE REFERENCE INPUT ARRAY AND WILL CHANGE IT!
	 * @param input - The array to normalize
	 * @return Maximum value in the array
	 */
	private double groundtruth_normalize(double[] input)
	{
		double max = 0;
		for(int i = 0; i < input.length; i++)
			max = Math.max(Math.abs(input[1]), max);
		
		if(max == 0)
			return 0;
		
		max = max == 0 ? 1 : max;
		for(int i = 0; i < input.length; i++)
			input[i] /= max;
		
		return max;
	}
	
	/**
	 * Convert the Forward, Right and Antoclockwise values into 4 motor outputs
	 * @param input - Double array containing Forward, Right and Antoclockwise values
	 * @param output - Double array containing motor output values
	 */
	private double[] outputCompute(double[] input) {
		double[] output = new double[4];
		/*double max = Math.max(1.0, Math.abs(input[0]) + Math.abs(input[1]) + Math.abs(input[2]));

		output[0] = (input[0] + input[1] - input[2]) / max;
		output[1] = (input[0] - input[1] - input[2]) / max;
		output[2] = (input[0] + input[1] + input[2]) / max;
		output[3] = (input[0] - input[1] + input[2]) / max;
		
		return output;*/
		
		double rotation_factor = 1.0 / Math.sqrt(2.0); // cos(45) = sin(45) = 1/sqrt(2)
		double degrees_45 = Math.PI / 4;
		double degrees_90 = Math.PI / 2;
		
		double y = input[0];
		double x = input[1];
		
		double angle = Math.atan2(y, x) + 2*Math.PI; // Get angle of the joystick
		double offset = angle % degrees_45 - (Math.floor(angle / degrees_45) % 2) * degrees_45; // Correction factors to account for the square
		offset = Math.cos(offset) / Math.cos(offset - degrees_45 + degrees_90 * ((offset < 0) ? 1.0 : 0.0)); // Choose the correct equation based on current octant
		output[0] = output[1] = offset * rotation_factor * (y + x); // Rotate X by -45 degrees and correct to the square
		output[2] = output[3] = offset * rotation_factor * (y - x); // Rotate Y by -45 degrees and correct to the square
		
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
	private void dump()
	{
		byte[] output = new byte[12+4+4];
		
		int loops_since_last_dump = _loops_since_last_dump;
		//_loops_since_last_dump = 0;
		
		// Dump motor set point, current, and voltage
		for(int i = 0; i < Map.DRIVE_MOTOR.values().length; i++)
		{
			output[i] = Utils.double_to_byte(_motors[i].get()); // Returns as 11-bit, downconvert to 8
			output[i+1] = (byte) _motors[i].getOutputCurrent();
			output[i+2] = (byte) (_motors[i].getBusVoltage() * 10);
			// From CANTalon class: Bus voltage * throttle = output voltage
		}
		ByteBuffer.wrap(output, 12, 4).putInt(loops_since_last_dump);
		ByteBuffer.wrap(output, 16, 4).putInt((int)(System.currentTimeMillis() - IO.ROBOT_START_TIME));
		
		if(_logger != null)
		{
			if(_logger.log(Map.LOGGED_CLASSES.DRIVE, output))
				_loops_since_last_dump -= loops_since_last_dump;
		}
	}
	
	/**
	 * Update motors as fast as possible, but only compute all the joystick stuff when there's new data
	 */
	private void fastTask()
	{
		// Damn you, Java, and your lack of local static variables!
		double[] input;
		boolean dump = false;
		
		while(_thread_alive)
		{
			input = _input;
			
			if(_ds.isEnabled())
			{
				// Process new joystick data - only when new data happens
				if(_new_data)
				{	
					if(_ds.isOperatorControl())
					{
						// Switch front side if we need to
						double rotation_offset = IO.front_side();
						if(!Double.isNaN(rotation_offset))
							setFrontAngle(rotation_offset);
					
						// Detents
						input = detents(input);
						// Frontside
						input = front_side(input);
						// Glide
						input = _glide.gain_adjust(input);
						
						// Save corrected input for fast loop
						_input = input;
					}
					
					_new_data = false;
					dump = true;
				}
				
				// Ground speed offset
				input = groundtruth_correction(input);
				// Output to motors - as fast as this loop will go
				motorOutput(outputCompute(input));
				
				_loops_since_last_dump++;
				
				// Log on new data, after the first computation
				if(dump)
				{
					// Dump in a separate thread, so we can loop as fast as possible
					if(_dump_thread == null || !_dump_thread.isAlive())
					{
						_dump_thread = new Thread(new Runnable() {
							public void run() {
								dump();
							}
						});
						_dump_thread.start();
					}
					dump = false;
				}
			}
		}
	}
}
