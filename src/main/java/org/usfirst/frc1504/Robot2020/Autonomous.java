package org.usfirst.frc1504.Robot2020;

/**
 * 
 * WARNING - THIS FILE HAS BEEN HARD PURGED! REFERENCE master_2019 FOR PREVIOUS IMPLEMENTATIONS
 * 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

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

	private double[] dist_to_rot(double dist, double angle)
	{
		double[] rots = {0, 0};
		rots[0] = (dist * Math.cos(angle))/(6 * Math.PI);
		rots[1] = (dist * Math.sin(angle))/(6 * Math.PI);
		return rots;
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
	//private Solenoid plate_solenoid = new Solenoid(Map.LIFT_PLATE_SOLENOID_PORT);
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
	
	public double[] switch_angles(double x1, double x2, double y1, double y2)
	{
		double left_box = find_angle_theta(x1, y1);
		double right_box = find_angle_theta(x2, y2);
		double[] angles = new double[] {left_box, right_box};
		return angles;
	}
	
	public static double find_angle_theta(double x, double y)
	{
        double angle_theta = Math.toDegrees(Math.atan((x - (Map.CAMERA_X / 2)) / (y - Map.CAMERA_Y)));
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

	/**
	 * Detented controller correction methods, and helper methods.
	 */
	private double[] detents(double[] input)
	{
		double y = input[0];
		double x = input[1];
		double w = input[2];
		
		double angle = Math.atan2(input[0], input[1]);
		
		double dx = 0; //fix_x(angle) * Utils.distance(y, x) * 0.25;
		double dy = 0; //fix_y(angle) * Utils.distance(y, x);
		
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
	
	protected void auto_task()
	{}
}