package org.usfirst.frc.team1504.robot;

import java.util.Timer;
import java.util.TimerTask;
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
	
	public static Autonomous getInstance()
	{
		return instance;
	}
	
	public void setup_path(double[][] path)
	{
		_path = path;
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
				//get IMU reading
				
				//correct for amount off
				double[] output = {0.0, 0.0, 0.0};
				double off = 1.0; //get reading
				double threshold = 5.0;//margin of error so it stops jittering.
				while (Math.abs(off) < threshold){ //needs to be replaced with checking if the gyro is 0 yet.
					 //get reading
					output[2] = Math.signum(off)*.2; //.2 is turn speed
					_drive.drive_inputs(output);
					off = 1.0;
				}
				//reset imu
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
			else if(_path[step][3] == 1)
			{

			}
			else if(_path[step][3] == 2)
			{
				// Calculate P(ID) output for the drive thread 
				for(int value = 0; value < 3; value++) // P loop
					output[value] = _path[step][value];
				_pipe.set_drive_input();
			}
//			double[] testoutput = {1.0, 1.0, 1.0, 1.0};
			_drive.drive_inputs(output);
			
			try {
				Thread.sleep(15);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}