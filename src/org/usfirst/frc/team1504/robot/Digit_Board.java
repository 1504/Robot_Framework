package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.DigitBoard;
import edu.wpi.first.wpilibj.DriverStation;

public class Digit_Board
{
	private static class Board_Task implements Runnable
	{
		private Digit_Board _b;

		Board_Task(Digit_Board b)
		{
			_b = b;
		}

		public void run()
		{
			_b.board_task();
		}
	}


	private Thread _task_thread;
	private boolean _run = false;

	protected Digit_Board()
	{
		_task_thread = new Thread(new Board_Task(this), "1504_Display_Board");
		_task_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2);

		_ds = DriverStation.getInstance(); 
		_board = DigitBoard.getInstance();
		
		start();

		System.out.println("1504 Digit Board Encapsulation Successful.");
	}
	public void start()
	{
		if(_run)
			return;
		_run = true;
		_task_thread = new Thread(new Board_Task(this));
		_task_thread.start();
	}
	public void stop()
	{
		_run = false;
	}
	private DriverStation _ds;	
	private DigitBoard _board;
	
	private double _voltage;
	
	private double _current_pot;
	private double _last_pot;
	
	private static Digit_Board instance = new Digit_Board();
	
	public static Digit_Board getInstance()
	{
		return Digit_Board.instance;
	}
	
	//Updates the values.
	public void update()
	{
		_current_pot = _board.getPotentiometer();
		_voltage = _ds.getBatteryVoltage();
	}
	
	//Writes the values.
	public void write()
	{
		if (_current_pot != _last_pot)
		{
			_board.writeDigits("  " + Double.toString(_current_pot));
		}
		else
		{
			_board.writeDigits(Double.toString(_voltage).substring(0, 4) + "V");
		}
		_last_pot = _current_pot;
	}
	private void board_task()
	{	

		while (_run)
		{	
			update();
			write();
			try
			{
				Thread.sleep(750); // wait a while because people can't read that
									// fast
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
