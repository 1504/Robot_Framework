package org.usfirst.frc1504.Robot2019;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;

public class Groundtruth implements Updatable {
	private static final Groundtruth instance = new Groundtruth();
	
	private Logger _logger = Logger.getInstance();
	private Arduino _arduino = Arduino.getInstance();
	//private volatile byte[] _raw_data = null;
	private volatile List<Byte> _raw_data = new ArrayList<Byte>();
	private double[] _position = {0.0, 0.0, 0.0};
	private double[] _position_error = {0.0, 0.0, 0.0};
	
	private boolean _data_good = false;
	private double[] _speed = {0.0, 0.0, 0.0};
	private double[] _acceleration = {0.0, 0.0, 0.0};
	private double[][] _speed_samples = new double[Map.GROUNDTRUTH_SPEED_AVERAGING_SAMPLES][3];
	private double[][] _acceleration_samples = new double[Map.GROUNDTRUTH_SPEED_AVERAGING_SAMPLES][3];
	private int _sample_index = 0;
	private long _last_update;
	
	protected Groundtruth()
	{
		double[] initializer = {0.0, 0.0, 0.0};
		for(int i = 0; i < Map.GROUNDTRUTH_SPEED_AVERAGING_SAMPLES; i++)
		{
			_speed_samples[i] = initializer;
			if(i < (Map.GROUNDTRUTH_SPEED_AVERAGING_SAMPLES - 1))
				_acceleration_samples[i] = initializer;
		}
		
		_raw_data.add((byte) 0);
		
		Update_Semaphore.getInstance().register(this);
		
		System.out.println("Groundtruth Initialized");
	}
	
	public static Groundtruth getInstance()
	{
		return instance;
	}
	
	/**
	 * Gets if the data is good from the groundtruth sensor
	 * @return Boolean - if we should trust the Groundtruth sensor
	 */
	public boolean getDataGood()
	{
		return _data_good;
	}
	
	/**
	 * Gets the robot's current position.
	 * @return double[] array containing: {Forward position, Right position, Anticlockwise position}
	 */
	public double[] getPosition()
	{
		return _position;
	}
	
	/**
	 * Sets the robot's current position index.
	 * Useful for setting at the start of the match or resetting at a known point on the field.
	 * @param p - forward, right, and anticlockwise position values.
	 */
	public void setPosition(double[] p)
	{
		_position = p;
	}
	
	/**
	 * Gets the robot's current speed
	 * @return double[] array containing: {Forward speed, Right speed, Anticlockwise speed}
	 */
	public double[] getSpeed()
	{
		return _speed;
	}
	
	/**
	 * Gets the robot's current acceleration
	 * @return double[] array containing: {Forward acceleration, Right acceleration, Anticlockwise acceleration}
	 */
	public double[] getAcceleration()
	{
		return _acceleration;
	}
	
	/**
	 * Input the current data from the ground truth sensors
	 * @param data - Data format: LEFT_X, LEFT_Y, LEFT_SQUAL, RIGHT_X, RIGHT_Y, RIGHT_SQUAL
	 */
	public void getData()
	{
		// Data format: LEFT_X LEFT_Y LEFT_SQUAL RIGHT_X RIGHT_Y RIGHT_SQUAL
		//_raw_data = data;
		byte[] data = _arduino.getSensorData();
		_raw_data.set(0, (byte) (_raw_data.get(0) + 1));
		for(byte b : data)
			_raw_data.add(b);
		
		if(data[2] < Map.GROUNDTRUTH_QUALITY_MINIMUM || data[5] < Map.GROUNDTRUTH_QUALITY_MINIMUM)
			return;
		
		double[] normalized_data = new double[6];
		double[] motion = new double[3];
		
		// Normalize from raw counts to distance
		for(int i = 0; i < data.length; i++)
			normalized_data[i] = data[i] / Map.GROUNDTRUTH_DISTANCE_PER_COUNT;
		
		// Forward
		motion[0] = (normalized_data[1] + normalized_data[4]) / 2.0;
		// Right
		motion[1] = (normalized_data[0] + normalized_data[3]) / 2.0;
		// Anticlockwise
		motion[2] = (normalized_data[4] - normalized_data[1]) / (2.0 * Map.GROUNDTRUTH_TURN_CIRCUMFERENCE);
		
		// TODO: Compute rolling positional error
		for(int i = 0; i < _position.length; i++)
		{
			_position[i] += motion[i];
			_position_error[i] += 1.0 / Map.GROUNDTRUTH_DISTANCE_PER_COUNT;
		}
		
		// Update global speed and acceleration values
		long update_time = System.currentTimeMillis();
		long elapsed_time = _last_update - update_time;
		_last_update = update_time;
		
		// Put current speed value into averaging array
		for(int i = 0; i < motion.length; i++)
			_speed_samples[_sample_index][i] = motion[i] / elapsed_time;
		// Compute acceleration array
		for(int i = 0; i < motion.length; i++)
			_acceleration_samples[_sample_index][i] = (_speed_samples[_sample_index][i] - _speed_samples[(_sample_index + Map.GROUNDTRUTH_SPEED_AVERAGING_SAMPLES) % Map.GROUNDTRUTH_SPEED_AVERAGING_SAMPLES][i]) / elapsed_time;
		
		// Find average speed
		double[] speed = {0.0, 0.0, 0.0};
		for(int i = 0; i < _speed_samples.length; i++)
		{
			for(int j = 0; j < 3; j++)
				speed[j] += _speed_samples[i][j];
		}
		for(int i = 0; i < speed.length; i++)
			speed[i] /= Map.GROUNDTRUTH_SPEED_AVERAGING_SAMPLES;
		
		// Find average acceleration
		double[] acceleration = {0.0, 0.0, 0.0};
		for(int i = 0; i < _acceleration_samples.length; i++)
		{
			for(int j = 0; j < 3; j++)
				acceleration[j] += _acceleration_samples[i][j];
		}
		for(int i = 0; i < acceleration.length; i++)
			acceleration[i] /= Map.GROUNDTRUTH_SPEED_AVERAGING_SAMPLES;
		
		// Set global variables
		_speed = speed;
		_acceleration = acceleration;
		
		_sample_index = (_sample_index + 1) % Map.GROUNDTRUTH_SPEED_AVERAGING_SAMPLES;
	}

	public void semaphore_update()
	{
		if(_raw_data.get(0) == 0)
			return;
		
		byte[] data = new byte[_raw_data.size()];
		for(int index = 0; index < _raw_data.size(); index++)
			data[index] = _raw_data.get(index);
		
		_raw_data.clear();
		_raw_data.add((byte) 0);
		
		_logger.log(Map.LOGGED_CLASSES.GROUNDTRUTH, data);
		
		
		/*if(_raw_data != null)
			_logger.log(Map.LOGGED_CLASSES.GROUNDTRUTH, _raw_data);
		_raw_data = null;*/
	}
}
