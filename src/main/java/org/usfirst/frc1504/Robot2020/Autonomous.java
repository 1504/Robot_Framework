package org.usfirst.frc1504.Robot2020;

/**
 * 
 * WARNING - THIS FILE HAS BEEN HARD PURGED! REFERENCE master_2019 FOR PREVIOUS IMPLEMENTATIONS
 * 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimerTask;
import java.util.stream.Stream;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;

public class Autonomous {
	public static class Autonomus_Waypoint {
		// Distance: Use encoders to move a delta
		// Time: Emulate joystick inputs for a time
		// Angle: Turn robot to a certain delta angle (using feedback)
		// Jostle: Emulate joystick inputs and wait for the robot to bounce over an
		// obstacle
		// Fire: Aim and fire. Ideally done last.
		public enum TYPE {
			DISTANCE, TIME, ANGLE, JOSTLE, FIRE
		}

		public TYPE type;
		public double timeout;
		public double[] setpoint = new double[2];

		public Autonomus_Waypoint() {
		}

		public Autonomus_Waypoint(TYPE t, double t_o, double[] sp) {
			type = t;
			timeout = t_o;
			setpoint = sp;
		}
	}

	private static class Auto_Task extends TimerTask {

		private Autonomous _task;

		Auto_Task(Autonomous a) {
			_task = a;
		}

		public void run() {
			_task.auto_task();
		}
	}

	private static final Autonomous instance = new Autonomous();

	// private Groundtruth _groundtruth = Groundtruth.getInstance();
	private Drive _drive = Drive.getInstance();
	private Timer _task_timer;
	private volatile boolean _thread_alive = true;
	private long _start_time;
	private double[][] _path;
	private int _path_step;
	int step = 0;
	boolean next_step = false;

	protected Autonomous() {
		//
		System.out.println("Auto Nom Ous");

	}

	/*
	 * public static void check_to_scale() { if(robot_position ==
	 * Robot.GAME_MESSAGE[1]) { //run } else () { //don't run } }
	 */
	public static Autonomous getInstance() {
		return instance;
	}

	public void setup_path(double[][] path) {
		_path = path;
	}

	public double[][] build_auton(double[][] first, double[][] second) // should let us combine multiple double arrays
	{
		if (first == null || second == null) {
			return first;
		}
		double[][] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public void start() {
		if (_path == null)
			return;

		step = 0;
		_thread_alive = true;
		next_step = false;
		_task_timer = new Timer();
		//_task_timer.scheduleAtFixedRate(new Auto_Task(this), 0, 20);
		System.out.println("Autonomous loop started");
	}

	public void stop() {
		_drive.drive_inputs(0.0, 0.0, 0.0);

		if (!_thread_alive)
			return;

		_thread_alive = false;

		//_task_timer.cancel();

		System.out.println("Autonomous loop stopped @ " + (System.currentTimeMillis() - _start_time));
	}
	private void drive_around(double[] step)
	{
		double sx = step[0] * Math.sin(step[1] + (Math.PI/4));
		double sy = step[0] * Math.sin(step[1] - (Math.PI/4));
		Drive.setRotations(sx, sy);
		console.log(Drive.rot_motor());
	}
	protected void auto_task() 
	{
		
		if(_path[step][2] != 0) //shoot at low hoop
		{
			Ion_Cannon._extender.set(DoubleSolenoid.Value.kForward);
			Timer.delay(Map.IC_DEPLOY_DELAY);
			Ion_Cannon.shoot(Ion_Cannon.ION_CANNON_STATE.LOW);
			Timer.delay(1);
			Timer.delay(_path[step][2]);
			Ion_Cannon.shoot(Ion_Cannon.ION_CANNON_STATE.DISABLED);
		}
		
		else if(_path[step][3] > 0) //enable tractor beam
		{
			Tractor_Beam.enable(true);
		}

		else
		{
			Tractor_Beam.enable(false);
		}

		drive_around(_path[step]);
		Timer.delay(_path[step][3]);
		step++;

	}
}