package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.utils.VisionThreadSingleFrame;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.vision.VisionRunner;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

public class CameraInterface implements Updatable
{
	public static enum CAMERAS {GEARSIDE, INTAKESIDE}
	private static int[] CAMERA_MAP = {0,1};
	public static enum CAMERA_MODE {SINGLE, MULTI}
	
	private static CameraInterface _instance = new CameraInterface();
	
	private UsbCamera[] _cameras = new UsbCamera[CAMERAS.values().length];
	private MjpegServer[] _servers = new MjpegServer[CAMERAS.values().length +1];
	VisionThreadSingleFrame test;
	private CAMERAS _active_camera = null;
	
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
		
		for(int i = 0; i < _servers.length; i++)
			server_ports += "\t" + _servers[i].getName() + " at port " + _servers[i].getPort() + "\n";
		
		System.out.print("Camera Interface Initialized\n" + server_ports);
		
		GripPipeline _pipe = new GripPipeline();
		//VisionRunner<VisionPipeline> _runner = new VisionRunner<>(_cameras[get_active_camera().ordinal()], _pipe, null);
		//_runner.runOnce();
		VisionRunner.Listener<GripPipeline> _listener = new VisionRunner.Listener<GripPipeline>() {
			private int called_times = 0;
			public void copyPipelineOutputs(GripPipeline pipeline) { System.out.println("Image processed "+(++called_times)+" times"); }
		};
		test = new VisionThreadSingleFrame(_cameras[CAMERAS.GEARSIDE.ordinal()], _pipe, _listener);
		test.processImage();
		System.out.println("Image processed in " + test.lastExecutionTime());
		test.processImage();
		System.out.println("Image processed in " + test.lastExecutionTime());
		test.processImage();
		System.out.println("Image processed in " + test.lastExecutionTime());
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
	
	public CAMERAS get_active_camera()
	{
		return _active_camera;
	}
	
	public void semaphore_update()
	{
		process();
		//System.out.println("camera interface sem update " + test.lastExecutionTime());

	}
	
	public void process()
	{
		test.processImage();
		//System.out.println("Image processed in " + test.lastExecutionTime());
	}
}