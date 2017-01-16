package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.vision.*;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

import org.opencv.core.Mat;

import edu.wpi.cscore.*;
import edu.wpi.first.wpilibj.CameraServer;

public class Vision implements VisionRunner.Listener<GripPipelineee>{
	
	UsbCamera _usb = new UsbCamera("camera", Map.VISION_INTERFACE_PORT1); //or path
	UsbCamera _usb1 = new UsbCamera("camera", Map.VISION_INTERFACE_PORT2); 

	private static final Vision _instance = new Vision();
	private GripPipelineee _pipe = new GripPipelineee();
	private VisionThread _thread = new VisionThread(_usb, _pipe, this);
	private ADXRS450_Gyro _gyro = new ADXRS450_Gyro();
	public double _target = 0.0;
	private enum AimState {WAIT_FOR_IMAGE_GOOD, GET_IMAGE, AIM_ROBOT, AIMED, BAD_IMAGE}
	public AimState _state;
	public static int cur_cam = Map.VISION_INTERFACE_PORT1; //0, current camera we're looking at
	public boolean port_toggle = false; //default on forward camera

	private Vision() 
	{
		System.out.println("Vision initialized");
		//getImage(_usb); 
		//_usb = CameraServer.getInstance().startAutomaticCapture(0);
		//_usb1 = CameraServer.getInstance().startAutomaticCapture(1);
		startSecondaryCapture(Drive._dir);
	}
	
	public static Vision getInstance()
	{
		return _instance;
	}
	
	public int setPort()
	{
		if(IO.camera_port() && !port_toggle)
		{
			port_toggle = true;
			return Map.VISION_INTERFACE_PORT2;
		}
		
		else if(IO.camera_port() && port_toggle)
		{
			port_toggle = false;
			return Map.VISION_INTERFACE_PORT1; //default cam
		}
		
		else //no joystick input, return current camera
			return cur_cam;
	}
	
	public void getImage(UsbCamera usb) 
	{
		//usb = _usb;
        usb = CameraServer.getInstance().startAutomaticCapture(setPort());
	}
	
	public void startSecondaryCapture(int dir)
	{		
		if(!IO.camera_port()) //no joystick input
		{	
			cur_cam = dir;
			if (dir == Map.VISION_INTERFACE_PORT1) //might be wrong depending on frontside
				_usb = CameraServer.getInstance().startAutomaticCapture(dir);
			else
				_usb1 = CameraServer.getInstance().startAutomaticCapture(dir);
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			CameraServer.getInstance().startAutomaticCapture(setPort());
	}
	
	public void settle_camera()
	{
		_state = AimState.WAIT_FOR_IMAGE_GOOD;
		
		//_gyro.reset();
		_state = AimState.GET_IMAGE;
		update();
	}
	
	public void setParams(double hue1, double hue2, double sat1, double sat2, double val1, double val2, double circ1, double circ2)
	{
		_pipe._hue1 = hue1;
		_pipe._hue2 = hue2;
		_pipe._sat1 = sat1;
		_pipe._sat2 = sat2;
		_pipe._val1 = val1;
		_pipe._val2 = val2;
		_pipe._circ1 = circ1;
		_pipe._circ2 = circ2;

	}
	
	private double offset_aim_factor()
	{
		return _target - _gyro.getAngle(); // offset
	}
	
	public void checkAim()
	{
		if(offset_aim_factor() < Map.VISION_INTERFACE_AIM_DEADZONE)
		{
			_state = AimState.AIMED;
		}
		
		else
		{
			_state = AimState.AIM_ROBOT;
		}
	}
	
	public void update()
	{
		double[] area = _pipe._output[4];
		double[] position = _pipe._output[0];
		
		if(area.length == 0)
		{
			_state = AimState.BAD_IMAGE;
			return;
		}
		
		int largest = 0;
		for(int i = 0; i < area.length; i++)
		{
			if(area[i] < area[largest])
			{
				largest = i;
			}
		}
		
		_target = largest;
		_target = (2 * position[largest] / Map.VISION_INTERFACE_VIDEO_WIDTH) - 1;
		_target *= Map.VISION_INTERFACE_VIDEO_FOV / -2.0;
		
		checkAim();
			
	}
	
	public double getInputCorrection(boolean first_aim)
	{
		System.out.println(offset_aim_factor() + " - " + _state.toString());
		if(first_aim)			
			settle_camera();
		
		if(_state == AimState.AIM_ROBOT)
		{
			// Compute the speed we need to turn the robot to point at the target
			if(Math.abs(offset_aim_factor()) > Map.VISION_INTERFACE_AIM_DEADZONE)
			{
				return  0.31 * Math.signum(offset_aim_factor()); 
				
			}
			else
				settle_camera();
		}
		
		return 0.0;
	}

	@Override
	public void copyPipelineOutputs(GripPipelineee pipeline) {
		// TODO Auto-generated method stub
		/*groundtruth
		 * 
		 * */
	}

}