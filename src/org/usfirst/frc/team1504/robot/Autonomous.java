package org.usfirst.frc.team1504.robot;

import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import org.usfirst.frc.team1504.robot.Pickup;
import org.usfirst.frc.team1504.robot.Pickup.flipper;

import edu.wpi.first.wpilibj.DriverStation;

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
	private static GripPipeline _pipe = GripPipeline.getInstance();
	private Timer _task_timer;
	private volatile boolean _thread_alive = true;
	private long _start_time;
	private double[][] _path;
	private int _path_step;
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
		_path = path;
	}
	public double[][] build_auton(double[][][] autons) //should let us combine multiple double arrays
	{
		return Stream.of(autons).flatMap(Stream::of).toArray(double[][]::new);
	}
	public void start()
	{
		if(_path == null)
			return;
		
		_path_step = -1;
		
		_thread_alive = true;
		_start_time = System.currentTimeMillis();
		
		_task_timer = new Timer();
		_task_timer.scheduleAtFixedRate(new Auto_Task(this), 0, 20);
		
		System.out.println("Autonomous loop started");
	}
	
	public double find_angle_theta(int x, int y)
	{
		
		long camera_height = 128;
		long camera_width = 256;
        double angle_theta = Math.toRadians(0.0);
        angle_theta = Math.atan((x - (camera_width / 2)) / (y - camera_height));
        return angle_theta;
	
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
			int step = 0;
			while(step < _path.length && _path[step][4] < (System.currentTimeMillis() - _start_time))
				step++;
			
			// Alert user on new step
			if(step > _path_step)
			{
				System.out.println("\tAutonomous step " + step + " @ " + (double)(System.currentTimeMillis() - _start_time)/1000);
				_path_step = step;
			}
			
			// Quit if there are no more steps left
			if(step == _path.length)
			{
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
				_pickup.set_state(Pickup.flipper.OPEN);
			}
			else if(_path[step][3] == 3) //eject cube
			{
				_pickup.set_intake_speed(-0.8);
			}
			else if(_path[step][3] == 4) //bring arm up
			{
				_pickup.set_state(Pickup.arm_position.UP);
			}
			else if(_path[step][3] == 5) //close arm
			{
				_pickup.set_state(Pickup.flipper.CLOSE);
			}
			else if(_path[step][3] == 6) //intake a cube
			{
				_pickup.set_intake_speed(0.8);
			}
			else if(_path[step][3] == 7) //extend lift all the way up
			{
				_lift.set_state(Lift.lift_position.TOP);
			}
			else if(_path[step][3] == 8) //extend lift all the way down
			{
				_lift.set_state(Lift.lift_position.BOTTOM);
			}
			else if(_path[step][3] == 9) //stop flippers
			{
				_pickup.flipper_stop();
			}
			else if(_path[step][3] == 10) //Auton Scale drop
			{
				_lift.plate_angle(45.0);
			}
			else if(_path[step][3] == 11) //go at an angle, speed
			{
				double angle = _path[step][0];
				double speed = _path[step][1];
				double[] arr = _drive.follow_angle(angle, speed);
				output[0] = arr[0];
				output[1] = arr[1];
			} else if(_path[step][3] == 12) //drive until crash
			{
				_path[step] = _drive.roborio_crash_bandicoot_check(_path[step]);
				for(int value = 0; value < 3; value++) // P loop
					output[value] = _path[step][value];
				if(_path[step][0] + _path[step][1] + _path[step][2] == 0){
					step++;
					}
			} else if (_path[step][3] == 13) {
				double angle = _path[step][0];
				double speed = _path[step][1];
				double[] arr = _drive.follow_angle(angle, speed);
				output[0] = arr[0];
				output[1] = arr[1];
				
				_path[step] = _drive.roborio_crash_bandicoot_check(_path[step]);
				for(int value = 0; value < 3; value++) // P loop
					output[value] = _path[step][value];
				if(_path[step][0] + _path[step][1] + _path[step][2] == 0)
					step++;
			}
			/*else if(_path[step][3] == 2)
			{
				// Calculate P(ID) output for the drive thread 
				for(int value = 0; value < 3; value++) // P loop
					output[value] = _path[step][value];
				_pipe.set_drive_input();
			}*/
//			double[] testoutput = {1.0, 1.0, 1.0, 1.0};
			_drive.drive_inputs(output);
			
			try {
				Thread.sleep(15);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void auton_slam() {
		//Dummy values for camera inputs
		int camera_input_X = 10;
		int camera_input_Y = 10;
		double[][] AUTON_SLAM_SEQUENCE = {{0.0, 0.25, 0.0, 0, (find_angle_theta(camera_input_X,camera_input_Y)/(Math.PI/2))*1000}, {0.5, 0.0, 0.0, 0}};
	}
}