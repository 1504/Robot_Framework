package org.usfirst.frc.team1504.robot;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.vision.VisionRunner;
import edu.wpi.first.wpilibj.vision.VisionThread;
import edu.wpi.first.wpilibj.vision.VisionRunner.Listener;

public class CameraInterface implements VisionRunner.Listener<GripPipeline>
{
	public static enum CAMERAS {GEARSIDE, INTAKESIDE}
	private static int[] CAMERA_MAP = {0,1};
	public static enum CAMERA_MODE {SINGLE, MULTI}
	
	private static CameraInterface _instance = new CameraInterface();
	
	private UsbCamera[] _cameras = new UsbCamera[CAMERAS.values().length];
	private MjpegServer[] _servers = new MjpegServer[CAMERAS.values().length +1];
	
	private CAMERAS _active_camera = null;
	private GripPipeline _pipe = new GripPipeline();
	private VisionThread _thread;
	private VisionRunner _runner;
	private VisionRunner.Listener<GripPipeline> _listener = new VisionRunner.Listener<GripPipeline>() {
		public void copyPipelineOutputs(GripPipeline pipeline) {}
	};
	
	protected CameraInterface()
	{
		String server_ports = "";
		for(int i = 0; i < _cameras.length; i++)
		{
			_cameras[i] = new UsbCamera(CAMERAS.values()[i] + " Camera", CAMERA_MAP[i]);
			CameraServer.getInstance().addCamera(_cameras[i]);
			_servers[i] = CameraServer.getInstance().addServer("serve_" + _cameras[i].getName());
			_servers[i].setSource(_cameras[i]);
		}
		
		_servers[_servers.length - 1] = CameraServer.getInstance().addServer("serve_combi");
		set_active_camera(CAMERAS.GEARSIDE);
		_runner = new VisionRunner(_cameras[get_active_camera().ordinal()], _pipe, _listener);
		
		if(Drive._dir == 1)
			set_active_camera(CAMERAS.INTAKESIDE);
		else
			set_active_camera(CAMERAS.GEARSIDE);
			
		for(int i = 0; i < _servers.length; i++)
			server_ports += "\t" + _servers[i].getName() + " at port " + _servers[i].getPort() + "\n";
		
		System.out.print("Camera Interface Initialized\n" + server_ports);
		
		//_runner.runOnce();
		
		Thread _camThread = new Thread(new Runnable() {
			public void run() {
				while(IO.camera_port())
				{
					System.out.println("in camera processing thread");
					getImage();	
				}
				}
		});
		_camThread.start();
	}

	public void getImage() 
	{
        _thread = new VisionThread(_cameras[get_active_camera().ordinal()], _pipe, this);
		_thread.start(); //vision runner instead 
	}
	
	public static CameraInterface getInstance()
	{
		return _instance;
	}
	
	public void set_active_camera(CAMERAS camera)
	{
		_active_camera = camera;
		_servers[_servers.length - 1].setSource(_cameras[camera.ordinal()]);
	}
	
	public CAMERAS get_active_camera()
	{
		return _active_camera;
	}
	
	public void copyPipelineOutputs(GripPipeline pipeline) {}
}