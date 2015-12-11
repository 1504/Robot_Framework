package org.usfirst.frc.team1504.robot;

public class Autonomous {

	private static class Auto_Task implements Runnable
	{

        private Autonomous _task;

        Auto_Task(Autonomous d)
        {
            _task = d;
        }

        public void run()
        {
            _task.auto_task();
            _task.stop();
        }
    }
	
	private static final Autonomous instance = new Autonomous();
	
	private Groundtruth _groundtruth = Groundtruth.getInstance();
	private Drive _drive = Drive.getInstance();
	
	private Thread _task_thread;
	private volatile boolean _thread_alive = true;
	private long _start_time;
	private double[][] _path;
	
	protected Autonomous()
	{
		//
		System.out.println("Autonomous Initialized");
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
		
		_thread_alive = true;
		_start_time = System.currentTimeMillis();
		
		_task_thread = new Thread(new Auto_Task(this), "1504 Autonomous");
		_task_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2);
		_task_thread.start();
	}
	
	public void stop()
	{
		_thread_alive = false;
		_drive.drive_inputs(0.0, 0.0, 0.0);
	}
	
	protected void auto_task()
	{
		double[] current_task;
		while(_thread_alive)
		{
			// Don't drive around if we're not getting good sensor data
			// Otherwise we drive super fast and out of control
			if(!_groundtruth.getDataGood())
				continue;
			
			// Calculate the program step we're on, quit if we're at the end of the list
			int step;
			for(step = 0; step < _path.length; step++)
			{
				if(_path[step][3] < (System.currentTimeMillis() - _start_time))
					break;
			}
			if(step == _path.length)
			{
				_thread_alive = false;
				return;
			}
			
			// Get the target position and actual current position
			current_task = _path[step];
			double[] current_position = _groundtruth.getPosition();
			
			double[] output = new double[3];
			
			// Calculate P(ID) output for the drive thread 
			for(int value = 0; value < 3; value++) // P loop
				output[value] = (current_task[value] - current_position[value]) * 0.1;
			_drive.drive_inputs(output);
		}
	}
}
