package org.usfirst.frc1504.Robot2020;

public class Glide {
	private long _gaintime;
	private double _last_output = 0.0;
	private double[] _gains;

	public Glide(double gain_up, double gain_down) {
		_gains = new double[] { gain_up, gain_down };
		setGainTime();
	}

	public void setGainTime() {
		_gaintime = System.currentTimeMillis();
	}

	public double gain_adjust(double input) {
		long looptime = System.currentTimeMillis() - _gaintime;
		setGainTime();

		boolean toward_zero = Math.abs(_last_output) > Math.abs(input); // Are we moving toward zero or away? (We have
																		// different gains for speeding up and slowing)
		double distance = input - _last_output; // The amount we want to move
		double magnitude = Math.signum(distance); // The direction we're moving (+ or -)
		double maximum_distance = looptime * magnitude * _gains[toward_zero ? 1 : 0]; // Maximum distance the input
																						// should move (input values per
																						// millisecond)

		if (Math.abs(maximum_distance) < Math.abs(distance)) // Take the smallest step of the two computed
			input = _last_output + maximum_distance;

		_last_output = input; // this way, when the loop next runs, it will be comparing the previous values!

		return input;
	}
}
