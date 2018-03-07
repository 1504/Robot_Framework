package org.usfirst.frc.team1504.robot;

import java.nio.ByteBuffer;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
//import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	private BuiltInAccelerometer accel = new BuiltInAccelerometer(Accelerometer.Range.k8G);
	private Thread _task_thread;
	private Thread _dump_thread;
	private Object _dump_lock;
	private boolean _dump = false;
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
    
    public static void initialize()
	{
		getInstance();
	}
    
	protected Drive()
	{
		_task_thread = new Thread(new DriveTask(this), "1504_Drive");
		_task_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2);
		_task_thread.start();
		
		Update_Semaphore.getInstance().register(this);
		
		DriveInit();
		
		_dump_lock = new Object();
		_dump_thread = new Thread(new Runnable() {
			public void run() {
				synchronized (_dump_lock)
				{
					try
					{
						while(!_dump)
							_dump_lock.wait();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					
					_dump = false;
					dump();
				}
			}
		});
		_dump_thread.start();
				
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
	private volatile double[] _input = {0.0, 0.0, 0.0};
	private volatile double _rotation_offset = 0.0;
	private volatile double[] _orbit_point = {0.0, -1.15}; //{0.0, 1.15};
	private Groundtruth _groundtruth = Groundtruth.getInstance();
	
	private WPI_TalonSRX[] _motors = new WPI_TalonSRX[Map.DRIVE_MOTOR_PORTS.length];
	
	private volatile int _loops_since_last_dump = 0;
	
	/**
	 * Set up everything that will be needed for the drive class
	 */
	private void DriveInit()
	{
		// Set up the drive motors
		for(int i = 0; i < Map.DRIVE_MOTOR_PORTS.length; i++)
		{
			_motors[i] = new WPI_TalonSRX(Map.DRIVE_MOTOR_PORTS[i]);
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
	public void drive_inputs(double forward, double track, double anticlockwise)
	{
		double[] inputs = {forward, track, anticlockwise};
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
	
	public void setFrontAngle(double rotation_offset)
	{
		if(Double.isNaN(rotation_offset))
			return;
		_rotation_offset = rotation_offset;
	}
	
	public void setFrontAngleDegrees(double rotation_offset)
	{
		if(Double.isNaN(rotation_offset))
			return;
		setFrontAngle(rotation_offset * Math.PI / 180.0);
	}
	
	/**
	 * Programmatically switch the direction the robot goes when the stick gets pushed
	 */
	private double[] front_side(double[] input) {
		double[] dir_offset = new double[3];
		dir_offset[0] = input[0] * Math.cos(_rotation_offset) + input[1] * Math.sin(_rotation_offset);
		dir_offset[1] = input[1] * Math.cos(_rotation_offset) - input[0] * Math.sin(_rotation_offset);
		dir_offset[2] = input[2];
		return dir_offset;
	}
	
	/**
	 * Orbit point
	 */
	private double[] orbit_point(double[] input) {
		double x = _orbit_point[0];
		double y = _orbit_point[1];

		double[] k = { y - 1, y + 1, 1 - x, -1 - x };

		double p = Math.sqrt((k[0] * k[0] + k[2] * k[2]) / 2) * Math.cos((Math.PI / 4) + Math.atan2(k[0], k[2]));
		double r = Math.sqrt((k[1] * k[1] + k[2] * k[2]) / 2) * Math.cos(-(Math.PI / 4) + Math.atan2(k[1], k[2]));
		double q = -Math.sqrt((k[1] * k[1] + k[3] * k[3]) / 2) * Math.cos((Math.PI / 4) + Math.atan2(k[1], k[3]));

		double[] corrected = new double[3];
		corrected[0] = (input[2] * r + (input[0] - input[2]) * q + input[0] * p) / (q + p);
		corrected[1] = (-input[2] * r + input[1] * q - (-input[1] - input[2]) * p) / (q + p);
		corrected[2] = (2 * input[2]) / (q + p);
		return corrected;
	}
	
	public void setOrbitPoint(double[] orbit_point)
	{
		_orbit_point = orbit_point;
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
	 * Ground truth sensor corrections
	 * @param input - Joystick input to correct towards
	 * @return
	 */
	private double[] groundtruth_correction(double[] input)
	{
		if(!_groundtruth.getDataGood())
			return input;
		
		
		double[] normal_input = input.clone();// input;
		double[] output = input.clone();
		double[] speeds = _groundtruth.getSpeed().clone();
		
		// Normalize the inputs and actual speeds
		if(groundtruth_normalize(speeds) == 0 || groundtruth_normalize(normal_input) == 0)
			return input;
		//groundtruth_normalize(normal_input);
		
		// Apply P(ID) correction factor to the joystick values
		// TODO: Determine gain constant and add to the Map
		for(int i = 0; i < input.length; i++)
			output[i] += (normal_input[i] - speeds[i]) * 0.15;
		
		System.out.println(normal_input[0] + " " + normal_input[1] + " " + normal_input[2] + " - " + speeds[0] + " " + speeds[1] + " " + speeds[2]);
		System.out.println((normal_input[0] - speeds[0]) + " " + (normal_input[1] - speeds[1]) + " " + (normal_input[2] - speeds[2]));

		//return output;
		return input;
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
			max = Math.max(Math.abs(input[i]), max);
		
		if(max == 0)
			return 0;
		
		max = max == 0 ? 1 : max;
		for(int i = 0; i < input.length; i++)
			input[i] /= max;
		
		return max;
	}
	
	/**
	 * Convert the Forward, Right and Anticlockwise values into 4 motor outputs
	 * @param input - Double array containing Forward, Right and Anticlockwise values
	 * @param output - Double array containing motor output values
	 */
	private double[] outputCompute(double[] input) {
		double[] output = new double[4];
		double max = Math.max(1.0, Math.abs(input[0]) + Math.abs(input[1]) + Math.abs(input[2]));

		output[0] = (input[0] + input[1] - input[2]) / max;
		output[1] = (input[0] - input[1] - input[2]) / max;
		output[2] = (input[0] + input[1] + input[2]) / max;
		output[3] = (input[0] - input[1] + input[2]) / max;
		
		return output; 
		
		/*double rotation_factor = 1.0 / Math.sqrt(2.0); // cos(45) = sin(45) = 1/sqrt(2)
		double degrees_45 = Math.PI / 4;
		double degrees_90 = Math.PI / 2;
		
		double y = input[0];
		double x = input[1];
		
		double angle = Math.atan2(y, x) + 2*Math.PI; // Get angle of the joystick
		double offset = angle % degrees_45 - (Math.floor(angle / degrees_45) % 2) * degrees_45; // Correction factors to account for the square
		offset = Math.cos(offset) / Math.cos(offset - degrees_45 + degrees_90 * ((offset < 0) ? 1.0 : 0.0)); // Choose the correct equation based on current octant
		output[2] = output[3] = offset * rotation_factor * (y + x); // Rotate X by -45 degrees and correct to the square
		output[0] = output[1] = offset * rotation_factor * (y - x); // Rotate Y by -45 degrees and correct to the square
		
		return output;*/
	}
	
	/**
	 * Output values to motors.
	 * Input: array of motor values to output in Map.DRIVE_MOTOR order.
	 */
	private void motorOutput(double[] values) {
		for(int i = 0; i < _motors.length; i++)
		{
			// There are no Sync Groups for CANTalons. Apparently.
			try
			{
				_motors[i].set(values[i] * Map.DRIVE_OUTPUT_MAGIC_NUMBERS[i]);
			}
			catch(Exception e) { }
		}
	}
	
	private void update_dashboard()
	{
		byte[] currents = new byte[Map.DRIVE_MOTOR.values().length];
		for(int i = 0; i < Map.DRIVE_MOTOR.values().length; i++)
			currents[i] = (byte) _motors[i].getOutputCurrent();
		update_dashboard(currents);
	}
	
	private void update_dashboard(byte[] currents)
	{
		SmartDashboard.putNumber("Drive input forward", _input[0]);
		SmartDashboard.putNumber("Drive input right", _input[1]);
		SmartDashboard.putNumber("Drive input anticlockwise", _input[2]);
		
		SmartDashboard.putNumber("Drive rotation offset", _rotation_offset);
		
		SmartDashboard.putNumber("Drive FL current", currents[0]);
		SmartDashboard.putNumber("Drive BL current", currents[1]);
		SmartDashboard.putNumber("Drive BR current", currents[2]);
		SmartDashboard.putNumber("Drive FR current", currents[3]);
	}
	
	/**
	 * Dump class for logging
	 */
	private void dump()
	{
		byte[] output = new byte[12+4+4];
		
		int loops_since_last_dump = _loops_since_last_dump;
		
		// Dump motor set point, current, and voltage
		for(int i = 0; i < Map.DRIVE_MOTOR.values().length; i++)
		{
			output[i*3] = Utils.double_to_byte(_motors[i].get()); // Returns as 11-bit, downconvert to 8
			output[i*3+1] = (byte) _motors[i].getOutputCurrent();
			output[i*3+2] = (byte) (_motors[i].getBusVoltage() * 10);
			// From CANTalon class: Bus voltage * throttle = output voltage
		}
		ByteBuffer.wrap(output, 12, 4).putInt(loops_since_last_dump);
		ByteBuffer.wrap(output, 16, 4).putInt((int)(System.currentTimeMillis() - IO.ROBOT_START_TIME));
		
		if(_logger != null)
		{
			if(_logger.log(Map.LOGGED_CLASSES.DRIVE, output))
				_loops_since_last_dump -= loops_since_last_dump;
		}
		
		// So we stay off the CAN bus as much as possible here
		update_dashboard(new byte[] {output[1], output[4], output[7], output[10]});
	}
	double initialSpike = 0.0;
	double lowestSpike = 0.0;
	double accelSign = -1.0;
	public double[] roborio_crash_bandicoot_check(double[] input, long time) {//uses roborio built in accelerometer
		double[] null_response = {0.0, 0.0, 0.0, 0, 0};
		accelSign = Math.signum((accel.getX()*accel.getX()+accel.getZ()*accel.getZ()));
		double robot_accel = Math.pow((Math.pow(accel.getX()*accel.getX()+accel.getZ()*accel.getZ(),2)),0.5);
		double spikeSign = Math.signum(initialSpike);
		System.out.println("Initial Spike: " + initialSpike + "RobotAccel: " + robot_accel);
	
		if(time > 1000)
		{
			if(spikeSign > 0)
			{
				if(robot_accel*accelSign > Map.CRASH_DETECTION_THRESHOLD_MULTIPLIER*initialSpike)
				{
					System.out.println("Null returned");
					initial_spike_reset();
					return null_response;
				}
			}
			else if (spikeSign < 0)
			{
				if(robot_accel*accelSign < Map.CRASH_DETECTION_THRESHOLD_MULTIPLIER*initialSpike)
				{
					System.out.println("Null returned");
					initial_spike_reset();
					return null_response;
				}
			}
		}
		else if(robot_accel > Math.pow(Math.pow(initialSpike,2),0.5))
		{
			initialSpike = robot_accel*accelSign;
		}
		return input;
	}
	public void initial_spike_reset() {
		initialSpike = 0.0;
	}
	/**
	 * Update motors as fast as possible, but only compute all the joystick stuff when there's new data
	 */
	private void fastTask()
	{
		// Damn you, Java, and your lack of local static variables!
		double[] input;
		
		while(_thread_alive)
		{
			input = _input;
			_groundtruth.getData(); // Always be getting groundtruth data
			
			if(_ds.isEnabled())
			{
				// Process new joystick data - only when new data happens
				if(_new_data)
				{
					// Don't do the fancy driver convenience stuff when we're PID controlling
					if(_ds.isOperatorControl())
					{
						//setFrontAngleDegrees(IO.drive_frontside_degrees());
						
						if(_rotation_offset == (Math.PI / 2.0))
							input[1] *= 0.5;
						// Detents
						input = detents(input);
						// Orbit point
						input = orbit_point(input);
						// Frontside
						input = front_side(input);
						
						_input = input;
					}
					
					_new_data = false;
					_dump = true;
				}
								
				// Ground speed offset
//				_groundtruth.getData();
//				if(SmartDashboard.getBoolean("Groundtruth Correction Enable", false))
//					input = groundtruth_correction(input);
				
				// Output to motors - as fast as this loop will go
				motorOutput(outputCompute(input));
				
				_loops_since_last_dump++;
				
				// Log on new data, after the first computation
				if(_dump || _loops_since_last_dump > Map.DRIVE_MAX_UNLOGGED_LOOPS)
				{
					// Dump in a separate thread, so we can loop as fast as possible
					synchronized (_dump_lock)
					{
						_dump_lock.notifyAll();
					}
					//_dump = false;
				}
			}
			else
			{
				update_dashboard();
				//Timer.delay(.025);
				try {
					Thread.sleep(25);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}