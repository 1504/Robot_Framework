package org.usfirst.frc.team1504.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;

public class Logger implements Updatable {
	
	private Compressor _compressor = new Compressor();
	private BuiltInAccelerometer _accelerometer = new BuiltInAccelerometer();
	private PowerDistributionPanel _pdp = new PowerDistributionPanel();
	private DriverStation _ds = DriverStation.getInstance();
	
	private File _outfile;
	private FileOutputStream _file_output;
	
	private long _start_time;
	
	private Drive _drive = Drive.getInstance();
	
	private static Logger instance = new Logger();
	
	protected Logger()
	{
		// Register with the update semaphore to get notified of new data
		Update_Semaphore.getInstance().register(this);
	}
	
	public static Logger getInstance()
	{
		return instance;
	}
	
	public void semaphore_update()
	{
		Timer.delay(0.01); // Wait to make sure all other classes have updated before we log them
		log_data();
	}
	
	public void start(String prefix) {
		Calendar cal = new GregorianCalendar();
		String filetime = Long.toString(cal.getTimeInMillis());
		_outfile = new File("/home/lvuser/log/" + prefix + "-" + filetime + ".log");

		try {
			_file_output = new FileOutputStream(_outfile);
		} catch (FileNotFoundException e) {
			System.out.println("Could not open file.");
			e.printStackTrace();
		}

		_start_time = System.currentTimeMillis();
	}
	
	public void stop()
	{
		if (_file_output == null) {
			System.out.println("disable called on null");
		} else {
			try {
				_file_output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void log_data()
	{
		try {
			byte[] time = new byte[8];
			ByteBuffer.wrap(time).putLong(System.currentTimeMillis() - _start_time);
			_file_output.write(time);
			_file_output.write(_drive.dump());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
