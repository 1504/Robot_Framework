package org.usfirst.frc1504.Robot2020;

/**
 * 
 * WARNING - THIS FILE HAS BEEN HARD PURGED! REFERENCE master_2019 FOR PREVIOUS IMPLEMENTATIONS
 * 
 */

import java.nio.ByteBuffer;
import java.util.TimerTask;
import java.util.Timer;

import java.lang.Math;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DriverStation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Drive implements Updatable
{
	private static class DTask implements Runnable
	{
		private Drive _d;
		
		DTask(Drive d)
		{
			_d = d;
		}
		
		public void run()
		{
			_d.mainTask();
		}
	}
	boolean tst1, tst2, tst3, tst4, tst5, tst6 = false;
	private static final Drive instance = new Drive();
	 
	private Thread _main_thread;
	
	private Thread _dumptruck;//BEEP BEEP BEEP BEEP BEEP BEEP BEEP
	private Object _dumplock;
	private boolean _dump = false;
	
	private volatile boolean _thread_alive = true;
	private volatile boolean _initialized = false;
	public static double[] rotations = {0,0,0,0};

	
	private TimerTask _osc = new TimerTask() {
		public void run() {
		}
	};
	private Timer _timer = new Timer();

	private double[] _orbit_magic_numbers = new double[6];
	
	/**
	 * gets the instance of the drive.
	 * @return the drive
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
		_main_thread = new Thread(new DTask(this), "1504_New_Drive");
		_main_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2);
		_main_thread.start();
		
		Update_Semaphore.getInstance().register(this);
		
		DInit();
		
		_dumplock = new Object();
		_dumptruck = new Thread(new Runnable(){
			public void run(){
				synchronized (_dumplock)
				{
					while(!_dump)
					{
						try
						{
							_dumplock.wait();
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						
						_dump = false;
						dump();
					}
				}
			}
		});
		_dumptruck.start();//vroom vroom
		
		_timer.schedule(_osc,  0, 250);
		
		System.out.println("Drive is here to kick ass and chew bubblegum, but doesn't want to muck up the transmission with bubble gum.");
		_initialized = true;
	}
	public void release()
	{
		_thread_alive = false;
	}
	
	private DriverStation _ds = DriverStation.getInstance();
	private Logger _log = Logger.getInstance();
	//private CameraInterface _camera = CameraInterface.getInstance();
	
	private volatile boolean _new_data = false;
	private volatile int _loops_since_last_dump = 0;
	
	private volatile double[] _input = {0.0, 0.0, 0.0};
	private volatile double[] _orbit_point = {0.0, 0.8}; //-1.15}; //{0.0, 1.15};

	private CANSparkMax[] _motors = new CANSparkMax[Map.DRIVE_MOTOR_PORTS.length];

	/**
	 * set up motors
	 */
	private void DInit() {
		for (int i = 0; i < Map.DRIVE_MOTOR_PORTS.length; i++) {
			_motors[i] = new CANSparkMax(Map.DRIVE_MOTOR_PORTS[i], MotorType.kBrushless);
		}

		set_orbit_point(_orbit_point);
	}

	/**
	 * called when Driver Station has new data.
	 */
	public void semaphore_update() {
		if (!_initialized)
			return;

		if (_ds.isEnabled()) {
			if(IO.god())
			{
				IO.god_state = !IO.god_state;
			}
			if (IO.tb_activate()) {
                IO._tb_state = !IO._tb_state;
			}
            if (IO.ion_high()) {
                IO._high_state = !IO._high_state;
            }
            if (IO.ion_low()) {
                IO._low_state = !IO._low_state;
            }
			if(IO.ion_vision())
			{
				drive_inputs(Optical_Sensor.optical_alignment());
			} else {
				drive_inputs(IO.drive_input());
			}
			System.out.println("God: " + IO.god_state);
			System.out.println("TB: " + IO._tb_state);

		}
	}

	/**
	 * Logs drive data, updates SmartDashboard.
	 */
	private void dump() {
		byte[] output = new byte[12 + 4 + 4];

		int loops_since_last_dump = _loops_since_last_dump;

		// Dump motor set point, current, and voltage
		for (int i = 0; i < Map.DRIVE_MOTOR.values().length; i++) {
			output[i * 3] = Utils.double_to_byte(_motors[i].get()); // Returns as 11-bit, downconvert to 8
			output[i * 3 + 1] = (byte) _motors[i].getOutputCurrent();
			output[i * 3 + 2] = (byte) (_motors[i].getBusVoltage() * 10);
			// From CANTalon class: Bus voltage * throttle = output voltage
		}
		ByteBuffer.wrap(output, 12, 4).putInt(loops_since_last_dump);
		ByteBuffer.wrap(output, 16, 4).putInt((int) (System.currentTimeMillis() - IO.ROBOT_START_TIME));

		if (_log != null) {
			if (_log.log(Map.LOGGED_CLASSES.DRIVE, output))
				_loops_since_last_dump -= loops_since_last_dump;
		}

		// So we stay off the CAN bus as much as possible here
		// update_dash(new byte[] {output[1], output[4], output[7], output[10]});
	}

	public double[] rot_motor()
	{
		double[] rotations = {0,0,0,0};
		for(int i = 0; i < Map.DRIVE_MOTOR_PORTS.length; i++)
		{
			rotations[i] = _motors[i].getEncoder().getPosition();
		}
		return rotations;
	}
	
	/**
	 * Updates motors as fast as possible, but joysticks will be computed only when there's new data.
	 */
	private void mainTask()
	{
		double[] input = new double[4];
		double[] output = new double[4];
		while(_thread_alive)
		{
			input = _input;
			if(input.length < 3 || _input.length < 3)
			{
				System.out.print("WARNING: Input out of sync in mainTask function: ");
				for(int i = 0; i < input.length; i++){
					System.out.print(input[i] + " ");
				}
				System.out.print("\n");
				input = new double[3];
				continue;
			}
			if(_ds.isEnabled() && !_ds.isTest())
			{
				if (_new_data)
				{
					//if(_ds.isOperatorControl())
					{
						//input = detents(input);
						/*if(IO.reset_front_side())
						{
							fSideAngleDegrees(0.0);
						}
						input = frontside(input);*/
						//if (!IO.get_drive_op_toggle())
						{
							input = orbit_point(input);
						}
					// input = _glide.gain_adjust(input);
					}
					_new_data = false;
					_dump = true;
					_input = input;
				}
				
				_loops_since_last_dump++;
				
				if(_dump || _loops_since_last_dump > Map.DRIVE_MAX_UNLOGGED_LOOPS)
				{
					synchronized (_dumplock)
					{
						//_dumplock.notifyAll();
					}
				// _dump = false;
				}
				output = outputCompute(input);
				motorOutput(output);
			}
			else //when disabled:
			{
				//update_dash();
				try
				{
					Thread.sleep(25);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * put data into queue for processing
	 */
	public void drive_inputs(double fw, double lr, double w)
	{
		double[] inputs = {fw, lr, w};
		drive_inputs(inputs);
	}
	public void drive_inputs(double[] i)
	{
		if (_new_data)
		{
			return;
		}
		
		_input = i;
		_new_data = true;
	}
	
	
	/**
	 * Orbit point changes the pivot point that the robot rotates around when turning.
	 * borrowed from @cowplex
	 */
	private double[] orbit_point(double[] input)
	{
		/*
		double x = _orbit_point[0];
		double y = _orbit_point[1];
		
		double[] k = { y - 1, y + 1, 1 - x, -1 - x };

		double p = Math.sqrt((k[0] * k[0] + k[2] * k[2]) / 2) * Math.cos((Math.PI / 4) + Math.atan2(k[0], k[2]));
		double r = Math.sqrt((k[1] * k[1] + k[2] * k[2]) / 2) * Math.cos(-(Math.PI / 4) + Math.atan2(k[1], k[2]));
		double q = -Math.sqrt((k[1] * k[1] + k[3] * k[3]) / 2) * Math.cos((Math.PI / 4) + Math.atan2(k[1], k[3]));
		*/

		double p = _orbit_magic_numbers[3];
		double r = _orbit_magic_numbers[4];
		double q = _orbit_magic_numbers[5];

		double[] corrected = new double[3];
		corrected[0] = (input[2] * r + (input[0] - input[2]) * q + input[0] * p) / (q + p);
		corrected[1] = (-input[2] * r + input[1] * q - (-input[1] - input[2]) * p) / (q + p);
		corrected[2] = (2 * input[2]) / (q + p);
		return corrected;
	}
	public void set_orbit_point(double[] orbit_point)
	{
		_orbit_point = orbit_point;

		double x = _orbit_point[0];
		double y = _orbit_point[1] * -1.0;
		
		double[] k = { y - 1, y + 1, 1 - x, -1 - x };

		double p = Math.sqrt((k[0] * k[0] + k[2] * k[2]) / 2) * Math.cos((Math.PI / 4) + Math.atan2(k[0], k[2]));
		double r = Math.sqrt((k[1] * k[1] + k[2] * k[2]) / 2) * Math.cos(-(Math.PI / 4) + Math.atan2(k[1], k[2]));
		double q = -Math.sqrt((k[1] * k[1] + k[3] * k[3]) / 2) * Math.cos((Math.PI / 4) + Math.atan2(k[1], k[3]));

		_orbit_magic_numbers[0] = k[0];
		_orbit_magic_numbers[1] = k[1];
		_orbit_magic_numbers[2] = k[2];
		_orbit_magic_numbers[3] = p;
		_orbit_magic_numbers[4] = r;
		_orbit_magic_numbers[5] = q;
	}


	double initialSpike = 0.0;
	double highestTravelingSpike = 0.0;
	double accelSign = -1.0;		
	
	/**
	 * Normalization function for arrays to normalize full scale to +- 1 <br>
	 * Note: THIS FUNCTION OPERATES ON THE REFERENCE INPUT ARRAY AND WILL CHANGE IT!
	 * @param input - The array to normalize
	 * @return Maximum value in the array
	 */
	

	/**
	 * Convert the input array (forward, right, and anticlockwise) into a motor output array.
	 */
	private double[] outputCompute(double[] input)
	{
		double[] output = new double[4];
		double max = Math.max(1.0, Math.abs(input[0]) + Math.abs(input[1]) + Math.abs(input[2]));

		output[0] = (input[0] + input[1] - input[2]) / max;
		output[1] = (input[0] - input[1] - input[2]) / max;
		output[2] = (input[0] + input[1] + input[2]) / max;
		output[3] = (input[0] - input[1] + input[2]) / max;
		
		return output; 
	}
	
	/**
	 * Sends the output array to the four drive motors.
	 */
	private void motorOutput(double[] values)
	{
		for(int i = 0; i < _motors.length; i++)
		{
			_motors[i].set(values[i] * Map.DRIVE_OUTPUT_MAGIC_NUMBERS[i]);
		}
	}

	public double[] follow_angle(double angle, double speed)
	{
		
        double angle_a = Math.toRadians(angle);
        double speed_a = speed;
        
        double forward_speed = speed_a * Math.cos(angle_a);
        double tracking_direction = speed_a * Math.sin(angle_a);
        
        double[] speeds = new double[] {forward_speed, tracking_direction};
        return speeds;
        //System.out.println("Forward speed: " + forward_speed);
        //System.out.println("Tracking speed: " + tracking_direction);
	}
}