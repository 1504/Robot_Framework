package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter implements Updatable
{	
	private static Shooter _instance = new Shooter();
	private static DriverStation _driver_station = DriverStation.getInstance();
	private static Preferences _preferences = Preferences.getInstance();
	
	private CANTalon _shooter = new CANTalon(30);
	private CANTalon _helicopter = new CANTalon(31);
	//private CANTalon _turret_motor;
	
	private boolean _enabled  = false;
	private boolean _override = false;
	private long _shot_estimate = 0;
	
	private double PID_values[][] = {{.03, .00015}, {.05, .00017}};
	private double PID_DEADZONE = 50;
	private double _shotCount = 0;
	
	protected Shooter()
	{
		if(_shooter.isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative) == CANTalon.FeedbackDeviceStatus.FeedbackStatusPresent)
		{
			_shooter.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
			_shooter.changeControlMode(TalonControlMode.Speed);
			//_shooter_motor.setP(Map.SHOOTER_GAIN_P);
			_shooter.setP(0.03);
			_shooter.setI(0.00015);
			//_shooter_motor.setI(Map.SHOOTER_GAIN_I);
			_shooter.reverseSensor(false);
		}
		
		// Retain last set value across robot reboots
		SmartDashboard.putNumber("Shooter Target Speed", 1500);//getTargetSpeed());
		
		SmartDashboard.putBoolean("Shooter enable", false);
		Update_Semaphore.getInstance().register(this);
		System.out.println("Shooter Initialized");
	}
	
	/**
	 * Get the Shooter singleton class instance
	 * @return the Shooter instance
	 */
	public static Shooter getInstance()
	{
		return _instance;
	}
	
	/**
	 * Called to initialize the Shooter singleton class
	 */
	public static void initialize()
	{
		getInstance();
	}
	
	/**
	 * Enable or Disable the shooter.
	 * @param enabled - True to enable the shooter, false to disable.
	 */
	public void setEnabled(boolean enabled)
	{
		_enabled = enabled;
	}
	
	/**
	 * Set the target shooter speed. Persistent across reboots.
	 * @param speed - the speed we want the shooter to run at (in RPM)
	 */
	public void setTargetSpeed(double speed)
	{
		SmartDashboard.putNumber("Shooter Target Speed", speed);
		_preferences.putDouble("Shooter Target Speed", speed);
	}
	
	/**
	 * Returns the current target speed of the shooter.
	 * @return Shooter target speed (in RPM)
	 */
	public double getTargetSpeed()
	{
		return _preferences.getDouble("Shooter Target Speed", 0.0);
	}
	
	/**
	 * Returns the current actual speed of the shooter
	 * @return Shooter speed (in RPM)
	 */
	public double getCurrentSpeed()
	{
		return _shooter.getSpeed();
	}
	
	/**
	 * Returns a boolean signifying whether or not the shooter is at its target speed
	 * @return Boolean signifying if the shooter is at its target speed
	 */
	public boolean getSpeedGood()
	{
		return (Math.abs(_shooter.getSpeed() + getTargetSpeed()) < PID_DEADZONE);
	}
	
	/**
	 * Override the vision system to force the shooter to fire when at speed and ignore the camera
	 * @param override - True to override the camera input and fire when at speed
	 */
	public void setOverride(boolean override)
	{
		_override = override;
	}
	
	public boolean countShots()
	{
		return Math.abs(_shooter.getOutputCurrent() - Map.SHOOTER_COUNT_CURRENT) < 2;
	}
	
	public boolean reverseShooter() //because a fuel is stuck in the shooter
	{
		return Math.abs(_shooter.getOutputCurrent() - Map.SHOOTER_REVERSE_CURRENT) < 2;
	}
	
	public void semaphore_update()
	{
		if(_driver_station.isOperatorControl())
		{
			setEnabled(IO.shooter_enable());
			//setOverride(IO.operator_override()); TODO
		}
		
		// Update stored speed value if changed from the DS.
		if(SmartDashboard.getNumber("Shooter Target Speed", 0.0) != getTargetSpeed())
			setTargetSpeed(SmartDashboard.getNumber("Shooter Target Speed", 0.0));
		
		if(_enabled)
		{
			_shooter.set(-getTargetSpeed());
			
			if(getSpeedGood() || _override)
			{
				_shooter.setP(PID_values[1][0]);
				_shooter.setI(PID_values[1][1]);
				//_helicopter.set(1.0);
				
				if(countShots())
				{
					_shotCount++;
				}
				
				if(reverseShooter() || IO.helicopter_reverse_override())
				{
					_helicopter.set(1.0);
				}
				
				else
				{
					_helicopter.set(-1.0);
				}
			}
			else
			{
				_shooter.setP(PID_values[0][0]);
				_shooter.setI(PID_values[0][1]);
				_helicopter.set(0.0);
			}
		}
		else
		{
			_shooter.setP(PID_values[0][0]);
			_shooter.setI(PID_values[0][1]);
			_shooter.set(0);
			_helicopter.set(0.0);
		}
		
		SmartDashboard.putNumber("Shooter Speed", getCurrentSpeed());
		SmartDashboard.putBoolean("Shooter At Speed", getSpeedGood());
	}

}