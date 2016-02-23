package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.util.Timer;
import java.util.TimerTask;

public class Vision_Interface implements Updatable
{
	private static final Vision_Interface instance = new Vision_Interface();	
	private enum AimState {WAIT_FOR_IMAGE_GOOD, GET_IMAGE, AIM_ROBOT, AIMED, BAD_IMAGE}
	
	protected Vision_Interface()
	{
		_contour_table = NetworkTable.getTable("GRIP/contours");
		
		//Update_Semaphore.getInstance().register(this);
		
		System.out.println("Vision Interface Initialized.");
	}
	
	public static Vision_Interface getInstance()
    {
        return Vision_Interface.instance;
    }
	
	private Timer _image_wait;
	private NetworkTable _contour_table;
	private ADXRS450_Gyro _gyro = new ADXRS450_Gyro();
	
	private double _target_position = -1.0;
	private AimState _state = null;
	
	private void settle_camera()
	{
		_state = AimState.WAIT_FOR_IMAGE_GOOD;
		
		_image_wait = new Timer();
		_image_wait.schedule(
				new TimerTask() { public void run() {
					if(_state != AimState.WAIT_FOR_IMAGE_GOOD)
						return;
					_gyro.reset();
					_state = AimState.GET_IMAGE;
					update_camera();
				} },
				Map.VISION_INTERFACE_IMAGE_CAPTURE_SETTLE_TIMEOUT
		);
	}
	
	private void update_camera()
	{
		double[] default_value = {-1.0};
		//double[] height   = _contour_table.getNumberArray("height", default_value);
		double[] width    = _contour_table.getNumberArray("width", default_value);
		double[] x_center = _contour_table.getNumberArray("centerX", default_value);
		//double[] y_center = _contour_table.getNumberArray("centerY", default_value);
		
		// No data from the Network Tables, do nothing
		if(width == default_value)
		{
			_state = AimState.BAD_IMAGE;
			return;
		}
		
		// Find the widest target - that's the one we're closest to pointing straight at
		int table_index = 0;
		for(int i = 0; i < width.length; i++)
		{
			if(width[i] > width[table_index])
				table_index = i;
		}
		
		_target_position = (2 * x_center[table_index] / Map.VISION_INTERFACE_VIDEO_WIDTH) - 1;
		_target_position *= Map.VISION_INTERFACE_VIDEO_FOV / 2;
		
		if(Math.abs(_target_position) < Map.VISION_INTERFACE_AIM_DEADZONE)
			_state = AimState.AIMED;
		else
			_state = AimState.AIM_ROBOT;
		
	}
	
	private double offset_aim_factor()
	{
		return _target_position - _gyro.getAngle();
	}
	
	public boolean getAimGood()
	{
		return _state == AimState.AIMED;
	}
	
	public double[] getInputCorrection(boolean first_aim)
	{
		if(first_aim)			
			settle_camera();
		
		if(_state == AimState.AIM_ROBOT)
		{
			// Compute the speed we need to turn the robot to point at the target
			if(Math.abs(offset_aim_factor()) > Map.VISION_INTERFACE_AIM_DEADZONE)
				return new double[] {0.0, offset_aim_factor() * Map.VISION_INTERFACE_TURN_GAIN};
			else
				settle_camera();
		}
		
		return new double[] {0.0, 0.0};
	}
	
	@Override
	public void semaphore_update()
	{
		// TODO Auto-generated method stub
	}

}
