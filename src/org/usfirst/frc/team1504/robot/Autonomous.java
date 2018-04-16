package org.usfirst.frc.team1504.robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import org.usfirst.frc.team1504.robot.Pickup;
import org.usfirst.frc.team1504.robot.Pickup.flipper;
import org.usfirst.frc.team1504.robot.Pickup;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;

public class Autonomous 
{
	public static class Autonomus_Waypoint
	{
		// Distance: Use encoders to move a delta
		// Time: Emulate joystick inputs for a time
		// Angle: Turn robot to a certain delta angle (using feedback)
		// Jostle: Emulate joystick inputs and wait for the robot to bounce over an obstacle
		// Fire: Aim and fire. Ideally done last.
		public enum TYPE {DISTANCE, TIME, ANGLE, JOSTLE, FIRE}
		public TYPE type;
		public double timeout;
		public double[] setpoint = new double[2];
		public Autonomus_Waypoint() {}
		public Autonomus_Waypoint(TYPE t, double t_o, double[] sp)
			{ type = t; timeout = t_o; setpoint = sp; }
	}
	
	private static class Auto_Task extends TimerTask
	{

        private Autonomous _task;

        Auto_Task(Autonomous a)
        {
            _task = a;
        }

        public void run()
        {
            _task.auto_task();
        }
    }
	
	private static final Autonomous instance = new Autonomous();
	
	//private Groundtruth _groundtruth = Groundtruth.getInstance();
	private Drive _drive = Drive.getInstance();
	private Pickup _pickup = Pickup.getInstance();
	private Lift _lift = Lift.getInstance();
	//private Solenoid plate_solenoid = new Solenoid(Map.LIFT_PLATE_SOLENOID_PORT);
	private static GripPipeline _pipe = GripPipeline.getInstance();
	private Timer _task_timer;
	private volatile boolean _thread_alive = true;
	private long _start_time;
	private double[][] _path;
	private int _path_step;
	int step = 0;
	boolean next_step = false;
	protected Autonomous()
	{
		//
		System.out.println("Auto Nom Ous");
		
	}
	/*
	public static void check_to_scale() 
	{
		if(robot_position == Robot.GAME_MESSAGE[1]) 
		{
			//run
		} else () 
		{
			//don't run
		}
	}
	*/
	public static Autonomous getInstance()
	{
		return instance;
	}
	
	public void setup_path(double[][] path)
	{
		for(int i = 0; i < path.length; i++){
			if(path[i][3] == 13){
				double angle = path[i][0];
				double speed = path[i][1]; //1.2 is a multiplier for the horizontal to have better angle;
				double[] arr = _drive.follow_angle(angle, speed);
				path[i][0] = arr[0];
				path[i][1] = arr[1] * Map.HORIZONTAL_MULTIPLIER;
				path[i][3] = 12;
			}
		}
		_path_step = -1;
		_path = path;
	}
	public double[][] build_auton(double[][] first, double[][] second) //should let us combine multiple double arrays
	{
		if(first == null || second == null)
		{
			return first;
		}
		double[][] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	public void start()
	{
		if(_path == null)
			return;
		
		_path_step = -1;
		step = 0;
		_thread_alive = true;
		_start_time = System.currentTimeMillis();
		next_step = false;
		_task_timer = new Timer();
		_task_timer.scheduleAtFixedRate(new Auto_Task(this), 0, 20);
		System.out.println("Autonomous loop started");
	}
	
	public void stop()
	{
		_drive.drive_inputs(0.0, 0.0, 0.0);

		if(!_thread_alive)
			return;
		
		_thread_alive = false;
		
		_task_timer.cancel();
		
		System.out.println("Autonomous loop stopped @ " + (System.currentTimeMillis() - _start_time));
	}
	
	protected void auto_task()
	{
		//while(_thread_alive)
		{
			// Don't drive around if we're not getting good sensor data
			// Otherwise we drive super fast and out of control
			/*if(!_groundtruth.getDataGood())
				continue;*/

			// Calculate the program step we're on, quit if we're at the end of the list
			while(next_step || step < _path.length && _path[step][4] < (System.currentTimeMillis() - _start_time))
			{
				_start_time = System.currentTimeMillis();
				next_step = false;
				step++;
				//System.out.println("Iteration" + "Step: " + step + " Path Length: " + _path.length);
			}
			
			// Alert user on new step
			if(step > _path_step)
			{
				//System.out.println("\tAutonomous step " + step + " @ " + (double)(System.currentTimeMillis() - _start_time)/1000);
				_path_step = step;
			}
			
			// Quit if there are no more steps left
			if(step >= _path.length)
			{
				//System.out.println("Quitting" + "Step: " + step + " Path Length: " + _path.length);
				stop();
				return;
			}
			
			// Get the target position and actual current position
			
			double[] output = new double[3];
			
			if(_path[step][3] == 0)
			{
				// Calculate P(ID) output for the drive thread 
				for(int value = 0; value < 3; value++) // P loop
					output[value] = _path[step][value];
			}
			else if(_path[step][3] == 1) //bring arm down
			{
				_pickup.set_state(Pickup.arm_position.DOWN);
			}
			else if(_path[step][3] == 2) //open arm
			{
				_pickup.set_state(Pickup.flipper.CLOSE);
				//_pickup._grab_piston.set(DoubleSolenoid.Value.kForward);
			}
			else if(_path[step][3] == 3) //eject cube
			{
				_pickup.set_intake_speed(-1);
			}
			else if(_path[step][3] == 4) //bring arm up
			{
				_pickup.set_state(Pickup.arm_position.UP);
			}
			else if(_path[step][3] == 5) //close arm changed this?
			{
				_pickup.set_state(Pickup.flipper.OPEN);
				//_pickup._grab_piston.set(DoubleSolenoid.Value.kReverse);
			}
			else if(_path[step][3] == 6) //intake a cube
			{
				_pickup.set_intake_speed(1);
			}
			else if(_path[step][3] == 7) //extend lift all the way up
			{
				next_step = !_lift.set_velocity(-1.0);
			}
			else if(_path[step][3] == 8) //extend lift all the way down
			{
				next_step = !_lift.set_velocity(1.0);
			}
			else if(_path[step][3] == 9) //stop flippers
			{
				_pickup.set_intake_speed(0);
			}
			else if(_path[step][3] == 14) //push out cube from lift plate
			{
				_lift.plate_solenoid.set(true);
			}
			/*
			else if(_path[step][3] == 10) //Auton Scale drop
			{
				_lift.plate_angle(45.0);
			}*/
			else if(_path[step][3] == 11) //go at an angle, speed
			{
				double angle = _path[step][0];
				double speed = _path[step][1]; //1.2 is a multiplier for the horizontal to have better angle
				double[] arr = _drive.follow_angle(angle, speed);
				output[0] = arr[0];
				output[1] = arr[1] * Map.HORIZONTAL_MULTIPLIER;
			} else if(_path[step][3] == 12) //drive until crash
			{
				double[] temp_path = {29, 5, 4};
				for(int value = 0; value < 3; value++)
					output[value] = _path[step][value]; //set output to crash bandicoot check
				temp_path = _drive.roborio_crash_bandicoot_check(_path[step], System.currentTimeMillis() - _start_time, Map.CRASH_DETECTION_MODE);
				if(temp_path[0] + temp_path[1] + temp_path[2] == 0){ //if we crashed
					for(int value = 0; value < 3; value++)
						output[value] = temp_path[value];
					System.out.println("crashed");
					next_step = true;
				}
				//System.out.println("Crashed" + "Step: " + step + " Path Length: " + _path.length);	
			}
			_drive.drive_inputs(output);
			
			try {
				Thread.sleep(15);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}