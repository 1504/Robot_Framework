package org.usfirst.frc1504.utils;

public class Average
{
	double [] _average = new double[4];
	double _sum = 0;
	int _index = 0;
	
	public Average()
	{
		
	}
	
	public double findAverage(double input)
	{
		_index = ++_index % _average.length;
		_sum += -_average[_index] + input;
		_average[_index] = input;
		return _sum/_average.length;
	}
}