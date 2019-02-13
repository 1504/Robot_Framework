package org.usfirst.frc1504.Robot2019;

public class DriveGlide
{
	private long _gaintime;
	private double[] _last_output = {0.0, 0.0};
	
	DriveGlide()
	{
		setGainTime();
	}
	
	public void setGainTime()
	{
		_gaintime = System.currentTimeMillis();
	}
	
	public double[] gain_adjust(double[] input)
	{
		long looptime = System.currentTimeMillis()-_gaintime;
		setGainTime();
		
		for(int i = 0; i < _last_output.length; i++)
		{
			boolean toward_zero = Math.abs(_last_output[i]) > Math.abs(input[i]); // Are we moving toward zero or away? (We have different gains for speeding up and slowing)
			double distance = input[i] - _last_output[i]; // The amount we want to move
			double magnitude = Math.signum(distance); // The direction we're moving (+ or -)
			double maximum_distance = looptime * magnitude * Map.DRIVE_GLIDE_GAIN[toward_zero ? 1 : 0][i]; // Maximum distance the input should move (input values per millisecond)
			
			if(Math.abs(maximum_distance) < Math.abs(distance)) // Take the smallest step of the two computed
				input[i] = _last_output[i] + maximum_distance;
			
			_last_output[i] = input[i]; //this way, when the loop next runs, it will be comparing the previous values!
		}
		
		return input;
	}
}
