package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Shooter implements Updatable
{
	CANTalon _shooter = new CANTalon(Map.SHOOTER_MOTOR);
	CANTalon _helicopter = new CANTalon(Map.HELICOPTER_MOTOR);
	Preferences _pref = Preferences.getInstance();
	private static Shooter _instance = new Shooter();
	
	public Shooter()
	{
		if(_shooter.isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative) == CANTalon.FeedbackDeviceStatus.FeedbackStatusPresent)
		{
			_shooter.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
			_shooter.changeControlMode(TalonControlMode.Speed);
			_shooter.setP(.03);
			_shooter.setI(.0015);
			_shooter.reverseSensor(false);
		}
		Update_Semaphore.getInstance().register(this);
		System.out.println("Shooter initialized to shoot");
	}
	
	public static Shooter getInstance()
	{
		return _instance;
	}
	
	public static void initialize()
	{
		getInstance();
	}
	
	public void set_target_speed(double speed)
	{
		_pref.putDouble("Shooter Target Speed", speed);
		SmartDashboard.putNumber("Shooter Target Speed", speed);
	}
	
	public boolean getSpeedGood()
	{
		return Math.abs(_shooter.getSpeed() - SmartDashboard.getNumber("Shooter Target Speed", 0.0)) < Map.SHOOTER_PID_DEADZONE;
	}
	
	public double get_target_speed()
	{
		return _pref.getDouble("Shooter Target Speed", 0.0);
	}
	
	public void update_dashboard()
	{
		SmartDashboard.putNumber("Shooter Target Speed", get_target_speed());
	}
	
	public void semaphore_update()
	{
		if(IO.shooter_input())
		{
			if(get_target_speed() != SmartDashboard.getNumber("Shooter Target Speed", 0.0))
			{
				set_target_speed(get_target_speed()); //map the preferences value over to SmartDashboard
			}
			
			_shooter.set(-get_target_speed());
			
			if(getSpeedGood() || IO.shooter_override())
			{
				_helicopter.set(1.0);
			}
		}
		
		else
		{
			_shooter.set(0.0);
			_helicopter.set(0.0);
		}
		update_dashboard();
	}
}
