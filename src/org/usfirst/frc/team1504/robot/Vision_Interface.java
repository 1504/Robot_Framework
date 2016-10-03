package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
//import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Vision_Interface implements Updatable
{
	private static final Vision_Interface instance = new Vision_Interface();	
	private enum AimState {WAIT_FOR_IMAGE_GOOD, GET_IMAGE, AIM_ROBOT, AIMED, BAD_IMAGE}
	private Vision_Tracker _tracker;
	private PID _pid;
	private char _direction = 0;
		
	private TimerTask _osc = new TimerTask() { public void run() { _direction++; } };
	private Timer _timer = new Timer();
	
	protected Vision_Interface()
	{
//		_contour_table = NetworkTable.getTable("GRIP/contours");
		_tracker = new Vision_Tracker();
		
		_timer.scheduleAtFixedRate(_osc, 0, 1000);
		
		SmartDashboard.putNumber("P", -.02);
		SmartDashboard.putNumber("I", .00);
		SmartDashboard.putNumber("D", 0);
		_pid = new PID(-.02, 0, 0);
		_pid.set(0.0);
		
		SmartDashboard.putNumber("Vision Aim Offset", Map.VISION_INTERFACE_AIM_OFFSET);
		
		Update_Semaphore.getInstance().register(this);
		
		System.out.println("Vision Interface Initialized.");
	}
	
	public static Vision_Interface getInstance()
    {
        return Vision_Interface.instance;
    }
	
	private Timer _image_wait;
//	private NetworkTable _contour_table;
	private ADXRS450_Gyro _gyro = new ADXRS450_Gyro();
	
	private double _target_position = -1.0;
	private AimState _state = AimState.BAD_IMAGE;
	
	private void settle_camera()
	{
		/*if(!_tracker.getCameraInit())
		{
			_state = AimState.BAD_IMAGE;
			return;
		}*/
		
		_state = AimState.WAIT_FOR_IMAGE_GOOD;
		
		//_gyro.reset();
		_state = AimState.GET_IMAGE;
		update_camera();
		
		/*_image_wait = new Timer();
		_image_wait.schedule(
				new TimerTask() { public void run() {
					if(_state != AimState.WAIT_FOR_IMAGE_GOOD)
						return;
					_gyro.reset();
					_state = AimState.GET_IMAGE;
					update_camera();
				} },
				10
				//Map.VISION_INTERFACE_IMAGE_CAPTURE_SETTLE_TIMEOUT
		);*/
	}
	
	private void update_camera()
	{
		/*double[] default_value = {-1.0};
		double[] size   = _contour_table.getNumberArray("height", default_value);
		//double[] size    = _contour_table.getNumberArray("width", default_value);
		//double[] position = _contour_table.getNumberArray("centerX", default_value);
		double[] position = _contour_table.getNumberArray("centerY", default_value);
		
		// No data from the Network Tables, do nothing
		if(size == default_value)*/
		
		double[][] vals = _tracker.get();
		double[] size = vals[3]; // Area of targets
		double[] position = vals[0];
		
		if(size.length == 0)
		{
			_state = AimState.BAD_IMAGE;
			return;
		}
		
		// Find the widest target - that's the one we're closest to pointing straight at
		int table_index = 0;
		for(int i = 0; i < size.length; i++)
		{
			if(size[i] > size[table_index])
				table_index = i;
		}
		
		// Find all targets within a percentage of the size of the largest
		/*List<Integer> indices = new ArrayList<Integer>();
		for(int i = 0; i < size.length; i++)
		{
			if(size[i] / size[table_index] > 0.87)
				indices.add(i);
		}
		
		// Shoot left goal or closest goal to aim, depending on number of detected targets
		for(int i : indices)
		{
			// Front-on - point at centermost
			if(indices.size() > 2)
			{
				if(Math.abs(position[i] - Map.VISION_INTERFACE_VIDEO_WIDTH/2) < Math.abs(position[table_index] - Map.VISION_INTERFACE_VIDEO_WIDTH/2))
						table_index = i;
			}
			// Left side - point at leftmost
			else
			{
				if(position[i] > position[table_index])
					table_index = i;
			}
		}*/
		
		_target_position = (2 * position[table_index] / Map.VISION_INTERFACE_VIDEO_WIDTH) - 1;
		_target_position *= Map.VISION_INTERFACE_VIDEO_FOV / -2.0;
		
		Map.VISION_INTERFACE_AIM_OFFSET = SmartDashboard.getNumber("Vision Aim Offset");
		
		_target_position += Map.VISION_INTERFACE_AIM_OFFSET;//-8.8;
		
		//_direction++;
		_pid.ClearIAccum();
		_pid.ClearError();
		_gyro.reset();
		
		if(Math.abs(_target_position) < Map.VISION_INTERFACE_AIM_DEADZONE)
			_state = AimState.AIMED;
		else
			_state = AimState.AIM_ROBOT;
		
	}
	
	private double offset_aim_factor()
	{
		return _target_position - _gyro.getAngle(); // offset
	}
	
	public boolean getAimGood()
	{
		return _state == AimState.AIMED;
	}
	
	public double[] getInputCorrection(boolean first_aim)
	{
System.out.println(offset_aim_factor() + " - " + _state.toString());
_pid.setPID(SmartDashboard.getNumber("P"), SmartDashboard.getNumber("I"), SmartDashboard.getNumber("D"));
		if(first_aim)			
			settle_camera();
		
		if(_state == AimState.AIM_ROBOT)
		{
			// Compute the speed we need to turn the robot to point at the target
			if(Math.abs(offset_aim_factor()) > Map.VISION_INTERFACE_AIM_DEADZONE)
			{
				//double output_factor = 0.20 - 0.35 * Math.abs(2 * offset_aim_factor() / Map.VISION_INTERFACE_VIDEO_FOV);
				//System.out.println(offset_aim_factor() + " - " + output_factor);
				//return new double[] {0.0, offset_aim_factor() * output_factor};
				
				//System.out.println(offset_aim_factor());
				return new double[] {/*0.19*/ 0.25 * (((_direction & 1) == 0) ? 1.0 : -1.0) , /*0.285*/ 0.31 * Math.signum(offset_aim_factor())};
				
				//_pid.update(offset_aim_factor());
				//System.out.println(offset_aim_factor() + " - " + _pid.get());
				//return new double[] {0.0, _pid.get()};
				//return new double[] {0.0, _pid.update(offset_aim_factor())};//offset_aim_factor() * Map.VISION_INTERFACE_TURN_GAIN};
			}
			else
				settle_camera();
		}
		
		return new double[] {0.0, 0.0};
	}
	
	public void snapshot(String s)
	{
		try {
			_tracker.getImage(s).free();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void semaphore_update()
	{
		// TODO Auto-generated method stub
		SmartDashboard.putBoolean("AIM", getAimGood());
		SmartDashboard.putBoolean("Vision Camera Initialized", _tracker.getCameraInit());
		SmartDashboard.putString("Vision State", _state.toString());
		SmartDashboard.putNumber("Vision Error", offset_aim_factor());
	}

}
