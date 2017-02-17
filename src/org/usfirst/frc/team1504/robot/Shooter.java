package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter implements Updatable
{
	private CANTalon _shooter;
	private CANTalon _conveyor;

	private static final Shooter instance = new Shooter();
	private static final DriverStation _ds = DriverStation.getInstance();
	private Thread _task_thread;
	private double [][] PID = {{.03, .00015}, {.05, .00017}};
	private static Preferences _pref = Preferences.getInstance();
	
	//private final int _sensor_status;
	
	public Shooter()
	{
		_shooter = new CANTalon(Map.SHOOTER_MOTOR);
		//_conveyor = new CANTalon(Map.CONVEYOR_MOTOR);

		//_sensor_status = _shooter.isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative) == CANTalon.FeedbackDeviceStatus.FeedbackStatusPresent ? 1 : 0;
		
		//if(_shooter.getSpeed() < Map.SHOOTER_TARGET_SPEED)
		if(_shooter.isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative) == CANTalon.FeedbackDeviceStatus.FeedbackStatusPresent)
		{
			_shooter.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
			_shooter.changeControlMode(TalonControlMode.Speed);
			_shooter.setP(PID[0][0]);
			_shooter.setI(PID[0][1]);
			_shooter.reverseSensor(false);
		}
		
		SmartDashboard.putNumber("Shooter Target Speed", Map.SHOOTER_TARGET_SPEED);
		//SmartDashboard.putBoolean("Shooter port encoder good", (_sensor_status & 1) != 0);
		//SmartDashboard.putBoolean("Shooter star encoder good", (_sensor_status & 2) != 0);
		
		Update_Semaphore.getInstance().register(this);
		System.out.println("Shooter Initialized");
	}
	
	public static Shooter getInstance()
	{
		return Shooter.instance;
	}
	
	public static void initialize()
	{
		getInstance();
	}
	
	public boolean getSpeedGood()
	{
		return Math.abs(getTargetSpeed() - _pref.getDouble("Shooter Target Speed", 0.0)) < Map.SHOOTER_PID_DEADZONE;
	}
	
	public double getTargetSpeed()
	{
		return _pref.getDouble("Shooter target speed", 0.0);
	}
	private void update_dashboard()
	{
		//Map.SHOOTER_TARGET_SPEED = SmartDashboard.getNumber("Shooter Target Speed");
		SmartDashboard.putNumber("Shooter port speed", _shooter.getSpeed());
		SmartDashboard.putNumber("Shooter port current", _shooter.getOutputCurrent());
		//SmartDashboard.putBoolean("Shooter speed good", _speed_good);
		//SmartDashboard.putString("Shooter State", _state.toString());
		//SmartDashboard.putBoolean("Shooter Override", _override);
		
		//_stopmotion.set_speeds(_shooter_motor_port.getSpeed(), _shooter_motor_star.getSpeed());
	}
	
	public void semaphore_update()
	{
		
		update_dashboard();
		if(_ds.isEnabled() && IO.shooter_input())
		{
			if(getSpeedGood() || IO.shooter_override())
			{
				_shooter.setP(PID[1][0]);
				_shooter.setI(PID[1][1]);
				_shooter.set(getTargetSpeed());
			}
			else
			{
				_shooter.setP(PID[0][0]);
				_shooter.setI(PID[0][1]);
				_shooter.set(getTargetSpeed());
			}

			/*_shooter.setP(Map.SHOOTER_GAIN_P);
			_shooter.setI(Map.SHOOTER_GAIN_I);
			_shooter.set(1500);
			System.out.println("shooter running");*/
			/*if(Math.abs(_conveyor.getSpeed() - Map.SHOOTER_TARGET_SPEED) <= Map.SHOOTER_SPEED_GOOD_DEADBAND)
			{
				_conveyor.set(.75);
			}
			else
				_conveyor.set(0);*/
			
		}
		
		else
		{
			_shooter.setP(PID[0][0]);
			_shooter.setI(PID[0][1]);
			_shooter.set(0);
//			System.out.println("shooter stopped");
		}

	}
	
}