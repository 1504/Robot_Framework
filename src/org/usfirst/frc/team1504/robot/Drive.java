package org.usfirst.frc.team1504.robot;
import com.analog.adis16448.frc.ADIS16448_IMU;
import java.util.ArrayList;

import java.nio.ByteBuffer;
import java.util.TimerTask;
import java.util.Timer;

import java.lang.Math;


import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import org.usfirst.frc.team1504.utils.LinearRegression;

//import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
//import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.interfaces.*;

import edu.wpi.first.wpilibj.AnalogInput;
public class Drive implements Updatable
{
	private BuiltInAccelerometer accel = new BuiltInAccelerometer(Accelerometer.Range.k8G);
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
	
	private static final Drive instance = new Drive();
	 public static final ADIS16448_IMU imu = new ADIS16448_IMU();
	 
	private Thread _main_thread;
	
	private Thread _dumptruck;//BEEP BEEP BEEP BEEP BEEP BEEP BEEP
	private Object _dumplock;
	private boolean _dump = false;
	
	private volatile boolean _thread_alive = true;
	
	private char _dir = 0;
	private TimerTask _osc = new TimerTask(){public void run() { _dir++;}};
	private Timer _timer = new Timer();
	
	private ArrayList<Integer> autonDistances = new ArrayList<Integer>();
	private ArrayList<Long> autonTimes = new ArrayList<Long>();
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
		
	}
	public void release()
	{
		_thread_alive = false;
	}
	
	private DriverStation _ds = DriverStation.getInstance();
	private Logger _log = Logger.getInstance();
	private DriveGlide _glide = new DriveGlide();
	private Groundtruth _groundtruth = Groundtruth.getInstance();
	//private CameraInterface _camera = CameraInterface.getInstance();
	
	private volatile boolean _new_data = false;
	private volatile int _loops_since_last_dump = 0;
	
	private volatile double[] _input = {0.0, 0.0, 0.0};
	private volatile double _rot_offset = 0.0;
	private volatile double[] _orbit_point = {0.0, -1.15}; //{0.0, 1.15};

	private WPI_TalonSRX[] _motors = new WPI_TalonSRX[Map.DRIVE_MOTOR_PORTS.length];
	public static AnalogInput sanic = new AnalogInput(3);

	/**
	 * set up motors
	 */
	private void DInit()
	{
		for(int i = 0; i < Map.DRIVE_MOTOR_PORTS.length; i++)
		{
			_motors[i] = new WPI_TalonSRX(Map.DRIVE_MOTOR_PORTS[i]);
		}		
	}
	
	/**
	 * called when Driver Station has new data.
	 */
	public void semaphore_update()
	{
		if(!_ds.isAutonomous())
		{
			if(IO.drive_wiggle() != 0.0)
			{
				drive_inputs(new double[] { 0.25 * (((_dir & 1) == 0) ? 1.0 : -1.0) , 0.31 * IO.drive_wiggle()});
			}
			else
			{
				drive_inputs(IO.drive_input());
			}
		}

	}
	
	/**
	 * Logs drive data, updates SmartDashboard.
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
		
		if(_log != null)
		{
			if(_log.log(Map.LOGGED_CLASSES.DRIVE, output))
				_loops_since_last_dump -= loops_since_last_dump;
		}
		
		// So we stay off the CAN bus as much as possible here
		update_dash(new byte[] {output[1], output[4], output[7], output[10]});
	}
	
	/**
	 * Updates motors as fast as possible, but joysticks will be computed only when there's new data.
	 */
	private void mainTask()
	{
		double[] input;
		double[] output;
		while(_thread_alive)
		{
			input = _input;
			if(_ds.isEnabled())
			{
				if (_new_data)
				{
					if(_ds.isOperatorControl())
					{
						input = detents(input);
						if(IO.reset_front_side())
						{
							fSideAngleDegrees(0.0);
						}
						input = frontside(input);
						if (!IO.get_drive_op_toggle())
						{
							input = orbit_point(input);
						}
//						input = _glide.gain_adjust(input);
					}
					_new_data = false;
					_dump = true;
					_input = input;
				}
				
				//_groundtruth.getData();
				//input = groundtruth_correction(input);
				input = accelerometer_correction(input);
				//if(IO.gear_input())
					//input = _gear.setDriveInput();
				//if(IO.camera_shooter_input())
					//input = _camera.set_drive_input(); 
				
				output = outputCompute(input);
//				System.out.println("output computed, input: " + input[0] + " " + input[1] + " " + input[2] + "|||| output: " + output[0] + " " + output[1] + " " + output[2] + " " + output[3]);
				motorOutput(output);
				
				_loops_since_last_dump++;
				
				if(_dump || _loops_since_last_dump > Map.DRIVE_MAX_UNLOGGED_LOOPS)
				{
					synchronized (_dumplock)
					{
						//_dumplock.notifyAll();
					}
//					_dump = false;
				}
			}
			//when disabled:
			else
			{
				update_dash();
				try
				{
					Thread.sleep(25);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
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
	 * Front Side
	 **/
	public void fSideAngle(double rot)
	{
		_rot_offset = rot;
	}
	public void fSideAngleDegrees(double rot)
	{
		fSideAngle(rot * Math.PI / 180.0);
	}
	private double[] frontside(double[] input)
	{
		double[] offset = new double[3];
		offset[0] = input[0] * Math.cos(_rot_offset) + input[1] * Math.sin(_rot_offset);
		offset[1] = input[1] * Math.cos(_rot_offset) - input[0] * Math.sin(_rot_offset);
		offset[2] = input[2];
		return offset;	
	}
	
	/**
	 * Orbit point changes the pivot point that the robot rotates around when turning.
	 * borrowed from @cowplex
	 */
	private double[] orbit_point(double[] input)
	{
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
	public void set_orbit_point(double[] orbit_point)
	{
		_orbit_point = orbit_point;
	}
		
	/**
	 * Detented controller correction methods, and helper methods.
	 */
	private double[] detents(double[] input)
	{
		double y = input[0];
		double x = input[1];
		double w = input[2];
		
		double angle = Math.atan2(input[0], input[1]);
		
		double dx = fix_x(angle) * Utils.distance(y, x) * 0.25;
		double dy = fix_y(angle) * Utils.distance(y, x);
		
		double[] fixed = new double[3];
		
		fixed[0] = y + dy;
		fixed[1] = x + dx;
		fixed[2] = w;
		
		return fixed;
	}
	private double fix_x(double theta) {
		return -Math.sin(theta) * (-Math.sin(8 * theta) - 0.25 * Math.sin(4 * theta));
	}
	private double fix_y(double theta) {
		return Math.cos(theta) * (-Math.sin(8 * theta) - 0.25 * Math.sin(4 * theta));
	}

	/**
	 * Corrections based off of two onboard ADNS-2620 mouse sensors.
	 */
	private double[] groundtruth_correction(double[] input)
	{
		if(!_groundtruth.getDataGood())
			return input;
		
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
	private double[] accelerometer_correction(double[] input)
	{
		if (Math.abs(input[2]) < 0.001){
			//correct for amount off
			double off = imu.getAngleX(); //get reading
			double threshold = 5.0;//margin of error so it stops jittering.
			if (Math.abs(off) > threshold){ //needs to be replaced with checking if the gyro is 0 yet.
				 //get reading
				input[2] = Math.signum(off)*.2; //.2 is turn speed
				return input;
			}
		} else
		{
			imu.reset();
		}
		return input;
	}

	double initialSpike = 0.0;
	double highestTravelingSpike = 0.0;
	double accelSign = -1.0;
	public double[] roborio_crash_bandicoot_check(double[] input, long time, int type) {//uses roborio built in accelerometer
		double[] null_response = {0.0, 0.0, 0.0, 0, 0};
		double robot_accel = 0;
		if(type == 1)
		{
			accelSign = Math.signum((accel.getX()*accel.getX()));
			robot_accel = (accel.getX()*accel.getX());
		}
		else if(type == 2)
		{
			accelSign = Math.signum((accel.getZ()*accel.getZ()));
			robot_accel = (accel.getZ()*accel.getZ());
		}
		else
		{
			accelSign = Math.signum((accel.getX()*accel.getX()+accel.getZ()*accel.getZ()));
			robot_accel = Math.pow((Math.pow(accel.getX()*accel.getX()+accel.getZ()*accel.getZ(),2)),0.5);
		}
		double spikeSign = Math.signum(initialSpike);
		System.out.println("Initial Spike: " + initialSpike + " RobotAccel: " + robot_accel + " highestTravelingSpike: " + highestTravelingSpike);
		if(time > Map.DETECTION_DELAY)
		{
			if(robot_accel > Math.pow(Math.pow(initialSpike,2),0.5))
			{
				highestTravelingSpike = robot_accel*accelSign;
			}
			if(spikeSign > 0)
			{
				if(robot_accel*accelSign > Map.CRASH_DETECTION_THRESHOLD_MULTIPLIER*initialSpike)
				{
					System.out.println("Null returned");
					spike_reset();
					return null_response;
				}
			}
			else if (spikeSign < 0)
			{
				if(robot_accel*accelSign < Map.CRASH_DETECTION_THRESHOLD_MULTIPLIER*initialSpike)
				{
					System.out.println("Null returned");
					spike_reset();
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
	/*public double[] roborio_crash_bandicoot_check(double[] input, long time, int mode) {
		double[] null_response = {0.0, 0.0, 0.0, 0, 0};
		autonDistances.add(sanic_value());
		autonTimes.add(time);		
		double[] autonDistancesDouble = new double[autonDistances.size()];
		double[] autonTimesDouble = new double[autonTimes.size()];
		for(int i = 0; i < autonDistances.size(); i++)
		{
			autonDistancesDouble[i] = autonDistances.get(i);
			autonTimesDouble[i] = (double)autonTimes.get(i);
		}
		LinearRegression regression = new LinearRegression(autonTimesDouble, autonDistancesDouble);
		if(sanic.getAverageValue() + regression.slope()*Map.GET_AVERAGE_TIME_DELAY < Map.CRASH_DETECTION_DISTANCE_THRESHOLD)
		{
			System.out.println("slope: " + regression.slope() + " dist: " + sanic.getAverageValue());
			autonDistances = new ArrayList<Integer>();
			autonTimes = new ArrayList<Long>();
			return null_response;
		}
		return input;
	}*/
	/*public double[] roborio_crash_bandicoot_check(double[] input, long time, int mode) {
		double[] null_response = {0.0, 0.0, 0.0, 0, 0};
		if (sanic.getAverageValue() < Map.CRASH_DETECTION_DISTANCE_THRESHOLD)
		{
			return null_response;
		}
		return input;
	} //simple crash detection, no lin reg
	 */
	public void spike_reset() {
		initialSpike = 0.0;
		highestTravelingSpike = 0.0;
	}
	public int sanic_value() {
			return sanic.getAverageValue();
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
	
	/**
	 * Sends the latest values to the SmartDashboard
	 */
	private void update_dash()
	{
		byte[] currents = new byte[Map.DRIVE_MOTOR.values().length];
		for(int i = 0; i < Map.DRIVE_MOTOR.values().length; i++)
			currents[i] = (byte) _motors[i].getOutputCurrent();
		update_dash(currents);
	}	
	private void update_dash(byte[] currents)
	{
		SmartDashboard.putNumber("Drive input forward", _input[0]);
		SmartDashboard.putNumber("Drive input right", _input[1]);
		SmartDashboard.putNumber("Drive input anticlockwise", _input[2]);
		
		SmartDashboard.putNumber("Drive rotation offset", _rot_offset);
		
		SmartDashboard.putNumber("Drive FL current", currents[0]);
		SmartDashboard.putNumber("Drive BL current", currents[1]);
		SmartDashboard.putNumber("Drive BR current", currents[2]);
		SmartDashboard.putNumber("Drive FR current", currents[3]);
		SmartDashboard.putNumber("Distance (ft)", sanic_value());
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