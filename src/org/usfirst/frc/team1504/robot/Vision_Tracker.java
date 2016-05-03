package org.usfirst.frc.team1504.robot;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.Point;
import com.ni.vision.NIVision.RawData;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.image.HSLImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
//import edu.wpi.first.wpilibj.vision.AxisCamera;
import edu.wpi.first.wpilibj.vision.USBCamera;

public class Vision_Tracker
{
	//private AxisCamera_STFU _camera;
	//private USBCamera _camera;
	private double[][] _output;
	private boolean _camera_initialized = true;
	
	Image _frame;
	
	//CameraServer server;
	
	Vision_Tracker()
	{
		//System.load("/usr/local/lib/lib_OpenCV/java/libopencv_java2410.so");
		//_camera = new AxisCamera("axis-1504.local");
		
		init();
		
		//server = CameraServer.getInstance();
		//server.setQuality(50);
		//server.startAutomaticCapture("cam1");
	}
	
	// Run init in a thread so starting the Tracker doesn't block literally everything else
	private void init()
	{
		new Thread(new Runnable() {
			public void run() {
				//Timer.delay(240);
				//_camera = new AxisCamera_STFU("10.15.4.42"/*"axis-1504.local"*/);
				//_camera = new USBCamera("cam1");
				//_camera.openCamera();
				//_camera.startCapture();
				/*while(!_camera_initialized)
				{
					Timer.delay(1);
					_camera_initialized = _camera.initialized;
				}*/
				System.load("/usr/local/lib/lib_OpenCV/java/libopencv_java2410.so");
				System.out.println("Camera initialized @ " + System.currentTimeMillis() + " \n\t(Took "+(System.currentTimeMillis()-IO.ROBOT_START_TIME)+" to initialize)");
				
				_frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
				int currSession = NIVision.IMAQdxOpenCamera("cam1", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
				NIVision.IMAQdxConfigureGrab(currSession);
				while(true)
				{
					
					NIVision.IMAQdxGrab(currSession, _frame, 0);
					CameraServer.getInstance().setImage(_frame);
				}
			}
		}).start();
	}
	
	public boolean getCameraInit()
	{
		return _camera_initialized;
	}
	
	private void image_to_dashboard(HSLImage i)
	{
		new Thread(new Runnable() {
			public void run() {
				HSLImage im = null;
				
				try {
					im = new HSLImage();
					NIVision.imaqReadFile(im.image, "/home/lvuser/log/images/process_highgui.png");
				} catch (NIVisionException e) {
					e.printStackTrace();
				}
				
				if(im != null)
					NIVision.imaqAdd(im.image, i.image, im.image);
				
				try {
					i.free();
				} catch (NIVisionException e) {
					e.printStackTrace();
				}
				if(im != null)
					CameraServer.getInstance().setImage(im.image);
			}
		}).start();
	}
	
	public Image getImage()
	{
		return getImage("");
	}
	public Image getImage(String s)
	{
		if(!_camera_initialized)
			return null;
		
		Image temp_image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);// = _frame;
		//NIVision.imaqCopyCalibrationInfo2(temp_image, _frame, new Point(0,0));
		NIVision.imaqDuplicate(temp_image, _frame);
		
		Calendar cal = new GregorianCalendar();
		String filetime = Long.toString(cal.getTimeInMillis());
		
		try
		{
			NIVision.imaqWriteJPEGFile(temp_image, "/home/lvuser/log/images/" + filetime + s + ".jpg", 255, null);
			return temp_image;
			
			//HSLImage temp_image = _camera.getImage();
			//temp_image.write("/home/lvuser/log/images/" + filetime + s + ".jpg");
			//return temp_image;
			
			//Image temp_image;
			//_camera.getImage(temp_image);
			//ByteBuffer temp_image = ByteBuffer.allocate(1);
			//_camera.getImageData(temp_image);
			//FileChannel out = new FileOutputStream("/home/lvuser/log/images/" + filetime + s + ".jpg").getChannel();
			//out.write(temp_image);
			//out.close();
			//return new HSLImage("/home/lvuser/log/images/" + filetime + s + ".jpg");
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			return null;
		}
	}
	
	public double[][] get()
	{
		Image temp_image = getImage();
		
		if(temp_image == null)
			return new double[0][0];
		
		try {
			NIVision.imaqWritePNGFile2(temp_image, "/home/lvuser/log/images/process.png", 100, null, 8);
			//temp_image.write("/home/lvuser/log/images/process.png");
			temp_image.free();
		} catch (Exception e) {
			e.printStackTrace();
			return new double[0][0];
		}
		
		try{
			// Load image
			Mat p = Highgui.imread("/home/lvuser/log/images/process.png", Highgui.CV_LOAD_IMAGE_COLOR);
			
			// Convert to HSL (Actually, HLS from BGR)
			Imgproc.cvtColor(p, p, Imgproc.COLOR_BGR2HSV);// Imgproc.COLOR_BGR2HLS);
			
			// HSL threshold
			Scalar low = new Scalar(80/*81*/, /*70*/120, /*95*/200);
			Scalar high = new Scalar(95/*95*/, 255, 255);
			Core.inRange(p, low, high, p);
			
			// Blur
			Imgproc.blur(p, p, new Size(3,3));
			
			// Write debug image
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
				output[4][i] = Imgproc.contourArea(contours.get(i), false);
				//contours.get(i).
				System.out.println(bb[i].x + " " + bb[i].y + " " + bb[i].width + " " + bb[i].height + " " + output[4][i]);
			}
			
			_output = output;
			
			System.out.println(" - ");
			
			//image_to_dashboard(temp_image);
			temp_image.free();
		    	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// Ugly hack to try to prevent Out Of Memory Errors
		// Unintentionally hilarious because I'm doing it in a anonymous thread
		new Thread(new Runnable() {
			public void run() {
				System.gc(); // This might actually be legit evil.
			}
		}).start();
		
		return _output;
	}
}
