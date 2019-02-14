package org.usfirst.frc1504.Robot2019;

import org.usfirst.frc1504.utils.VisionThreadSingleFrame;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;

public class CameraInterface implements Updatable
{
	public static enum CAMERAS {GEARSIDE, INTAKESIDE}
	public static enum CAMERA_MODE {SINGLE, MULTI}
	
	private static CameraInterface _instance = new CameraInterface();
	
	private UsbCamera[] _cameras = new UsbCamera[CAMERAS.values().length];
	private MjpegServer[] _servers = new MjpegServer[CAMERAS.values().length +1];
	VisionThreadSingleFrame test;
	private CAMERAS _active_camera = null;
	public boolean _isAimed = false;
	private double _shooter_speed = 0.0;
	public GripPipeline _pipe;
	protected CameraInterface()
	{
		String server_ports = "";
		set_active_camera(CAMERAS.GEARSIDE);
		
		for(int i = 0; i < _servers.length; i++)
			server_ports += "\t" + _servers[i].getName() + " at port " + _servers[i].getPort() + "\n";
		
		System.out.print("Camera Interface Initialized\n" + server_ports);
		
		_pipe = new GripPipeline();
		_isAimed = _pipe.checkAim();
		_shooter_speed = _pipe.setShooterSpeed();
	
		//test.processImage();
		
		/*test.processImage();
		System.out.println("Image processed in " + test.lastExecutionTime());
		test.processImage();
		System.out.println("Image processed in " + test.lastExecutionTime());*/
		Update_Semaphore.getInstance().register(this);
		System.out.println("camera interface initialized");
	}
	
	public static CameraInterface getInstance()
	{
		return _instance;
	}
	
	public static void initialize()
	{
		getInstance();
	}
	
	public void set_active_camera(CAMERAS camera)
	{
		_active_camera = camera;
		_servers[_servers.length - 1].setSource(_cameras[camera.ordinal()]);
	}
	
	public double get_shooter_speed()
	{
		return _shooter_speed;
	}
	
	public CAMERAS get_active_camera()
	{
		return _active_camera;
	}
	
	public void semaphore_update()
	{
		process();
		//System.out.println("camera interface sem update " + test.lastExecutionTime());

	}
	
	public double [] set_drive_input()
	{
		return _pipe.set_drive_input();

		/*if(IO.camera_shooter_input())
	 		return _pipe.set_drive_input();

		else
			return new double [] {0.0, 0.0};*/
	}
	
	public void process()
	{
		test.processImage();
		set_drive_input();
		
		//System.out.println("Image processed in " + test.lastExecutionTime());
	}
}