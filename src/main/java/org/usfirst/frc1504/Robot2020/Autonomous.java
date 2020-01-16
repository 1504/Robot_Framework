package org.usfirst.frc1504.Robot2020;

/**
 * 
 * WARNING - THIS FILE HAS BEEN HARD PURGED! REFERENCE master_2019 FOR PREVIOUS IMPLEMENTATIONS
 * 
 */

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

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
	
	private Drive _drive = Drive.getInstance();
	private Timer _task_timer;
	private volatile boolean _thread_alive = true;
	private long _start_time;
	private double[][] _path;
	int step = 0;
	boolean next_step = false;
	protected Autonomous()
	{
		//
		System.out.println("Auto Nom Ous");
		
	}
	public static Autonomous getInstance()
	{
		return instance;
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
	
	protected void auto_task()
	{
		{
			// Calculate the program step we're on, quit if we're at the end of the list
			while(next_step || step < _path.length && _path[step][4] < (System.currentTimeMillis() - _start_time))
			{
				_start_time = System.currentTimeMillis();
				next_step = false;
				step++;
				//System.out.println("Iteration" + "Step: " + step + " Path Length: " + _path.length);
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
			_drive.drive_inputs(output);
			
			try {
				Thread.sleep(15);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}