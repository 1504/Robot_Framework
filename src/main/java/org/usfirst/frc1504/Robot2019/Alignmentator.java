package org.usfirst.frc1504.Robot2019;

import org.usfirst.frc1504.Robot2019.Elevator.ELEVATOR_MODE;
import org.usfirst.frc1504.Robot2019.Hatch.HATCH_STATE;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;


public class Alignmentator
{
	public enum ALIGNMENTATOR_STATUS {PICKUP_TRACKING, PICKUP, PLACEMENT_TRACKING, PLACEMENT};
	public enum PICKPLACE_STATE {DISABLED, MANIPULATOR, REVERSE}
	private static final Alignmentator instance = new Alignmentator();

	private static class Alignment_Sensors
	{
		private DigitalInput[] _sensors = new DigitalInput[Map.ALIGNMENT_SENSOR_MAP.length];
		private boolean[] _values = new boolean[Map.ALIGNMENT_SENSOR_MAP.length];
		
		public Alignment_Sensors()
		{
			for(int i = 0; i < Map.ALIGNMENT_SENSOR_MAP.length; i++)
				_sensors[i] = new DigitalInput(Map.ALIGNMENT_SENSOR_MAP[i]);
		}

		public void update()
		{
			for(int i = 0; i < _sensors.length; i++)
				_values[i] = !_sensors[i].get();
		}

		public int get()
		{
			int output = 0;
			for(int i = 0; i < _values.length; i++)
				output = (output << 1) + (_values[i]?1:0);
			return output;
		}

		public boolean[] get_array()
		{
			return _values;
		}
	}

	private class Align_Thread implements Runnable
	{
		public volatile boolean _run_sequence;
		private volatile PICKPLACE_STATE _state;
		
		Align_Thread()
		{
			_run_sequence = false;
			_state = PICKPLACE_STATE.DISABLED;
		}
		
		public void run()
		{
			System.out.println("Alignmentator task thread initialized");
			while(true)
			{
				_state = PICKPLACE_STATE.DISABLED;
				while(!_run_sequence)
					Timer.delay(.02);
				
				_state = PICKPLACE_STATE.MANIPULATOR;
				Timer.delay(.4);

				_state = PICKPLACE_STATE.REVERSE;
				for(int i = 0; i < 15 && _run_sequence; i++)
				{
					Timer.delay(0.1);
					_run_sequence = IO.get_auto_alignment() || IO.get_auto_placement();
				}

				_run_sequence = false;
			}
		}

		public void run_sequence()
		{
			_run_sequence = true;
		}

		public PICKPLACE_STATE get_state()
		{
			return _state;
		}
	}

	private Alignment_Sensors _sensors = new Alignment_Sensors();
	private Hatch _hatch = Hatch.getInstance();
	private boolean _last_hatch = false;
	private boolean _last_button = false;
	private ALIGNMENTATOR_STATUS _state = ALIGNMENTATOR_STATUS.PICKUP_TRACKING;
	private Thread _task_thread;
	private Align_Thread _task;

	public static Alignmentator getInstance()
	{
		return instance;
    }

    private Alignmentator()
    {	
				_task = new Align_Thread();
				_task_thread = new Thread(_task, "1504_Alignmentator Task Thread");
				_task_thread.start();

        System.out.println("Alignmentator initialized");
	}
	
	public void update()
	{
		_sensors.update();

		if((IO.get_auto_alignment() || IO.get_auto_placement()) && !_last_button)
		{
			if(_hatch.getState() == HATCH_STATE.HOLDING || Elevator.getInstance().getMode() == ELEVATOR_MODE.CARGO)
				_state = ALIGNMENTATOR_STATUS.PLACEMENT_TRACKING;
			else
				_state = ALIGNMENTATOR_STATUS.PICKUP_TRACKING;
		}

		if(_hatch.getHatchInput() && !_last_hatch && _state == ALIGNMENTATOR_STATUS.PICKUP_TRACKING)
		{
			_state = ALIGNMENTATOR_STATUS.PICKUP;
			_task.run_sequence();
		}

		if(IO.get_auto_placement() && !_last_button && _state == ALIGNMENTATOR_STATUS.PLACEMENT_TRACKING)
		{
			_state = ALIGNMENTATOR_STATUS.PLACEMENT;
			_task.run_sequence();
		}

		_last_button = IO.get_auto_alignment() || IO.get_auto_placement();
		_last_hatch = _hatch.getHatchInput();
	}

	public boolean get_sensor_good()
	{
		int configuration = _sensors.get();
		int front = (configuration >> 3) & 7;
		int rear = configuration & 7;
		switch(front)
		{
			case 0:
			case 5:
			case 7:
				return false;
		}
		switch(rear)
		{
			case 5:
			case 7:
				return false;
		}
		return true;
	}

	public boolean[] get_sensor_array()
	{
		return _sensors.get_array();
	}

	public ALIGNMENTATOR_STATUS status()
	{
		return _state;
	}

	public PICKPLACE_STATE pickplace_status()
	{
		return _task.get_state();
	}

	public double[] drive()
	{
		double[] moves = {0.0, 0.0, 0.0};

		if(_state == ALIGNMENTATOR_STATUS.PICKUP || _state == ALIGNMENTATOR_STATUS.PLACEMENT)
		{
			if(_task.get_state() == PICKPLACE_STATE.MANIPULATOR)
			{
				if(_state == ALIGNMENTATOR_STATUS.PICKUP)
					_hatch.set_state(Hatch.HATCH_STATE.OPEN);
				else
					_hatch.set_state(Hatch.HATCH_STATE.CLOSED);
				
				moves[0] = .1;
				return moves;
			}
			if(_task.get_state() == PICKPLACE_STATE.REVERSE)
			{
				moves[0] = -.3;
				if(_state == ALIGNMENTATOR_STATUS.PLACEMENT)
					moves[0] /= 2.0;
				return moves;
			}

			return moves;
			//if(_task.get_state() == PICKPLACE_STATE.DISABLED)
			//	_state = ALIGNMENTATOR_STATUS.
		}

		if(!get_sensor_good())
			return moves;

		int configuration = _sensors.get();
		int front = (configuration >> 3) & 7;
		int rear = configuration & 7;

		moves[0] = ((front >> 1) & 1) + ((rear >> 1) & 1);
		moves[1] = ((front & 1) & (rear & 1)) - ((front >> 2 & 1) & (rear >> 2 & 1));
		moves[2] = (front >> 2 & 1) + (rear & 1) - (front & 1) - (rear >> 2 & 1);

		moves[0] *= 0.125;
		moves[1] *= 0.15;
		moves[2] *= 0.15;

		moves[1] += -moves[2] * 0.6; // Adds some tracking in to if a single side is activated

		if(_state == ALIGNMENTATOR_STATUS.PLACEMENT_TRACKING) // prioritize forward motion w/ minimal side jitter when placing
		{
			moves[0] *= 2.0;
			moves[1] *= 0.8;
			moves[2] *= 0.7;
		}

		return moves;

			//1, 6 - cw   32, 1, (33) 100001
			//3, 4 - ccw  8, 4, (12)  001100
			//2, 5 - fwd  16, 2, (18) 010010
			// 100100 - right
			// 001001 - left
			// Valid: 100000, 010000, 001000, 110000, 011000, 
			//        100100, 100010, 100001
			//        110100, 110110, 110010, 110011, 1100001
			//
			// 000000, 100000, 010000, 110000, 101000, 111000     011100  Masks: 101000, 000101
			// 000100, 100100, 010100, 110100, 101100, 111100     111100  100, 110, 010, 011, 001
			// 000010, 100010, 010010, 110010, 101010, 111010     111100  000, 101, 111
			// 000110, 100110, 010110, 110110, 101110, 111110     111100
			// 000001, 100001, 010001, 110001, 101001, 111001     111100
			// 000101, 100101, 010101, 110101, 101101, 111101     000000
			// 000011, 100011, 010011, 110011, 101011, 111011     111100
			// 000111, 100111, 010111, 110111, 101111, 111111     000000
	}
}
