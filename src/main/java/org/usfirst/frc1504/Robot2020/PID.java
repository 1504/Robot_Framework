package org.usfirst.frc1504.Robot2020;

public class PID {
	private double _p_gain, _i_gain, _d_gain, _i_accum;
	private long _last_loop;
	private double _error, _last_error, _set_point;

	PID() {
		_last_loop = System.currentTimeMillis();
	}

	PID(double p, double i, double d) {
		_p_gain = p;
		_i_gain = i;
		_d_gain = d;
		_last_loop = System.currentTimeMillis();
	}

	public void setPID(double p, double i, double d) {
		_p_gain = p;
		_i_gain = i;
		_d_gain = d;
	}

	public void ClearIAccum() {
		_i_accum = 0.0;
	}

	public void ClearError() {
		_last_error = 0.0;
	}

	public void set(double set_point) {
		_set_point = set_point;
	}

	public double update(double position) {
		long time = System.currentTimeMillis();
		_last_error = _error;

		_error = _set_point - position;
		_i_accum += _error * (time - _last_loop) / 1000.0;

		_last_loop = time;

		return get();
	}

	public double get() {
		return (_error * _p_gain) + (_i_accum * _i_gain) + ((_error - _last_error) * _d_gain);
	}
}
