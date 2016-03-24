package org.usfirst.frc.team1504.robot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.image.HSLImage;
//import edu.wpi.first.wpilibj.vision.AxisCamera;

public class Vision_Tracker
{
	private AxisCamera_STFU _camera;
	private double[][] _output;
	private boolean _camera_initialized = false;
	
	
	Vision_Tracker()
	{
		System.load("/usr/local/lib/lib_OpenCV/java/libopencv_java2410.so");
		//_camera = new AxisCamera("axis-1504.local");
		
		init();
	}
	
	// Run init in a thread so starting the Tracker doesn't block literally everything else
	private void init()
	{
		new Thread(new Runnable() {
			public void run() {
				//init();
				while(!_camera_initialized)
				{
					try
					{
						_camera = new AxisCamera_STFU("axis-1504.local");
						_camera_initialized = true;
						System.out.println("Camera initialized @ " + System.currentTimeMillis() + " \n\t(Took "+(System.currentTimeMillis()-IO.ROBOT_START_TIME)+" to initialize)");
					}
					catch (Error e)
					{
						Timer.delay(1);
					}
				}
			}
		}).start();
	}
	
	public boolean getCameraInit()
	{
		return _camera_initialized;
	}
	
	public HSLImage getImage()
	{
		return getImage("", false);
	}
	public HSLImage getImage(String s, boolean wait)
	{
		// If we want an image or nothing
		if(!_camera.isFreshImage() && !wait)
			return null;
		
		// Wait for a fresh image
		while(wait && !_camera.isFreshImage())
			Timer.delay(.01);
		
		Calendar cal = new GregorianCalendar();
		String filetime = Long.toString(cal.getTimeInMillis());
		
		try
		{
			HSLImage temp_image = _camera.getImage();
			temp_image.write("/home/lvuser/log/images/" + filetime + s + ".jpg");
			return temp_image;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public double[][] get()
	{
		HSLImage temp_image = getImage();
		
		if(temp_image == null)
			return new double[0][0];
		
		try {
			temp_image.write("/home/lvuser/log/images/process.png");
			temp_image.free();
		} catch (Exception e) {
			e.printStackTrace();
			return new double[0][0];
		}
		
		try{
			// Load image
			Mat p = Highgui.imread("/home/lvuser/log/images/process.png", Highgui.CV_LOAD_IMAGE_COLOR);
			
			// Convert to HSL (Actually, HLS from BGR)
			Imgproc.cvtColor(p, p, Imgproc.COLOR_BGR2HLS);
			
			// HSL threshold
			Scalar low = new Scalar(80/*81*/, 70, 95);
			Scalar high = new Scalar(95/*95*/, 255, 255);
			Core.inRange(p, low, high, p);
			
			// Blur
			Imgproc.blur(p, p, new Size(3,3));
			
			// Write debug
			Highgui.imwrite("/home/lvuser/log/images/process_highgui.png", p);
			
			// Find contours
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Imgproc.findContours(p, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
			
			// Process contours
			Rect[] bb = new Rect[(int) contours.size()];
			double[][] output = {
					new double[(int) contours.size()],
					new double[(int) contours.size()],
					new double[(int) contours.size()],
					new double[(int) contours.size()]
					};
			for(int i = 0; i < contours.size(); i++)
			{
				bb[i] = Imgproc.boundingRect(contours.get(i));
				output[0][i] = bb[i].x + bb[i].width / 2.0;
				output[1][i] = bb[i].y + bb[i].height;// / 2.0;
				output[2][i] = bb[i].width;
				output[3][i] = bb[i].height;
				//contours.get(i).
				System.out.println(bb[i].x + " " + bb[i].y + " " + bb[i].width + " " + bb[i].height);
			}
			
			_output = output;
			
			System.out.println(" - ");
		    	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// Ugly hack to try to prevent Out Of Memory Errors
		// Unintentionally hilarious because I'm doing it in a anonymous thread
		new Thread(new Runnable() {
			public void run() {
				System.gc(); // This line might actually be legit evil.
			}
		}).start();
		
		return _output;
	}
}
