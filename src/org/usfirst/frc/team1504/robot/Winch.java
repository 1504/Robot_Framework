package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DriverStation;

public class Winch implements Updatable
{
	private static Winch _instance = new Winch();

	private DriverStation _driver_station = DriverStation.getInstance();
	
	private CANTalon _winch_motor_nancy = new CANTalon(Map.WINCH_TALON_PORT_NANCY);
	private CANTalon _winch_motor_mead = new CANTalon(Map.WINCH_TALON_PORT_MEAD);
	
	protected Winch()
	{
		Update_Semaphore.getInstance().register(this);
	}
	
	public static Winch getInstance()
	{
		return _instance;
	}
	
	public void semaphore_update()
	{
		if(_driver_station.getMatchTime() > 30.0 && !IO.operator_override())
			return;
		
		// Deploy winch out the side of the robot
		//TODO: What is the mechanism?
		
		// Run that thang!
		_winch_motor_nancy.set(IO.winch_input());
		_winch_motor_mead.set(-IO.winch_input());
	}
}