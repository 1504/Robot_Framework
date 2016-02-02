package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Vision_Interface implements Updatable
{
	private static final Vision_Interface instance = new Vision_Interface();
	
	private NetworkTable _contour_table;
	
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
	
	private double _target_position = -1.0;
	
	private void update()
	{
		double[] default_value = {-1.0};
		double[] height   = _contour_table.getNumberArray("height", default_value);
		double[] width    = _contour_table.getNumberArray("width", default_value);
		double[] x_center = _contour_table.getNumberArray("centerX", default_value);
		double[] y_center = _contour_table.getNumberArray("centerY", default_value);
		
		// No data from the Network Tables, do nothing
		if(width == default_value)
		{
			_target_position = -1.0;
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
		
	}
	
	public boolean getAimGood()
	{
		return Math.abs(_target_position) < Map.VISION_INTERFACE_AIM_DEADZONE;
	}
	
	public double[] getInputCorrection()
	{
		update();
		
		if(_target_position == -1.0)
			return new double[] {0.0, 0.0};
		
		// Compute the speed we need to turn the robot to point at the target
		double turn_speed = 0.0;
		if(!getAimGood())
			turn_speed = _target_position * Map.VISION_INTERFACE_TURN_GAIN;
		
		if(Math.abs(_target_position) > Map.VISION_INTERFACE_TURN_MAX_OUTPUT)
			turn_speed = Map.VISION_INTERFACE_TURN_MAX_OUTPUT * Math.signum(_target_position);
		
		return new double[] {-0.0, turn_speed};
	}
	
	@Override
	public void semaphore_update()
	{
		// TODO Auto-generated method stub
	}

}
