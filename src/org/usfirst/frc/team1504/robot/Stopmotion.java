package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Timer;

public class Stopmotion
{
	private class MotionTask implements Runnable
	{

        private DigitalOutput _out;
        private double _timeout = 0;
        private double _pulse = .001;

        MotionTask(int n)
        {
        	_out = new DigitalOutput(n);
        }

        public void run()
        {
        	double time = System.currentTimeMillis();
			while(true)
			{
				if(_timeout > 0 && System.currentTimeMillis() - time >= _timeout)
				{
					time = System.currentTimeMillis();
					_out.set(true);
					Timer.delay(_pulse);
					_out.set(false);
					try {
						Thread.sleep((long) ((_timeout - 5) / 1000.0));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else if(_timeout == 0)
				{
					Timer.delay(.05);
				}
			}
        }
        
        /*public void set_timeout(double t)
        {
        	_timeout = t;
        }*/
        
        public void set_pulse(double p)
        {
        	_pulse = p;
        }
    }
	
	private MotionTask _port_task;
	private MotionTask _star_task;
	private Thread _port_thread;
	private Thread _star_thread;
	
	Stopmotion()
	{
		_port_task = new MotionTask(1);
		_port_thread = new Thread(_port_task, "Port Stopmotion");
		_port_thread.start();
		
		_star_task = new MotionTask(2);
		_star_thread = new Thread(_star_task, "Starboard Stopmotion");
		_star_thread.start();
	}
	
	public void set_speeds(double port_speed, double star_speed)
	{
		_port_task.set_pulse(port_speed > 300 ? 60000.0 / port_speed : 0.0);
		_star_task.set_pulse(star_speed > 300 ? 60000.0 / star_speed : 0.0);
	}
}
