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
	private CANTalon _shooter = new CANTalon(Map.SHOOTER_MOTOR);
	private CANTalon _helicopter = new CANTalon(Map.HELICOPTER_MOTOR);

	private static final Shooter instance = new Shooter();
	private static final DriverStation _ds = DriverStation.getInstance();
	//private static final CameraInterface _camera = CameraInterface.getInstance();
	private Thread thread;

	private double [][] PID = {{.03, .00015}, {.05, .00017}};
	private static Preferences _pref = Preferences.getInstance();
	
	public Shooter()
	{
		if(_shooter.isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative) == CANTalon.FeedbackDeviceStatus.FeedbackStatusPresent)
		{
			_shooter.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
			_shooter.changeControlMode(TalonControlMode.Speed);
			_shooter.setP(PID[0][0]);
			_shooter.setI(PID[0][1]);
			_shooter.reverseSensor(false);
		}
		
		//SmartDashboard.putNumber("Shooter Target Speed", Map.SHOOTER_TARGET_SPEED);
		//SmartDashboard.putBoolean("Shooter port encoder good", (_sensor_status & 1) != 0);
		//SmartDashboard.putBoolean("Shooter star encoder good", (_sensor_status & 2) != 0);
		
		Update_Semaphore.getInstance().register(this);
		System.out.println("Shooter Initialized");
	}
	
	public static Shooter getInstance()
	{
		return instance;
	}
	
	public static void initialize()
	{
		getInstance();
	}

	public boolean getSpeedGood()
	{
		return Math.abs(getTargetSpeed() - SmartDashboard.getNumber("Shooter Target Speed", 0.0)) < Map.SHOOTER_PID_DEADZONE; //pref
	}
	
	/*public boolean getSpeedGoodfromCamera()
	{
		return Math.abs(getTargetSpeed() - setLinearSpeed(_camera._pipe.getDistance())) < Map.SHOOTER_PID_DEADZONE;
	}*/
	
	public double setLinearSpeed(double distance) //distance in inches for now cuz reasons
	{
		return distance * 14.054 + 2980;
	}
	
	public double setCubicSpeed(double distance) 
	{
		double speed = .0096*Math.pow(distance, 3) - 1.8304*Math.pow(distance,  2) + 120.1*distance + 1144.3;
		
		System.out.println("Speed is " + speed + "for distance of " + distance);

		return speed;
	}
	
	public double getTargetSpeed()
	{
		return -SmartDashboard.getNumber("Shooter target speed", 0.0);
	}
	
	public void setTargetSpeed(double speed)
	{
		SmartDashboard.putNumber("Shooter Target Speed", speed);
		//_pref.putDouble("Shooter Target Speed", speed);
		
	}
	private void update_dashboard()
	{
		//Map.SHOOTER_TARGET_SPEED = SmartDashboard.getNumber("Shooter Target Speed");
//		SmartDashboard.putNumber("Shooter Target Speed", getTargetSpeed());
		SmartDashboard.putNumber("Shooter Speed", _shooter.getSpeed());
		//SmartDashboard.putBoolean("Shooter speed good", _speed_good);
		//SmartDashboard.putString("Shooter State", _state.toString());
		//SmartDashboard.putBoolean("Shooter Override", _override);
		
		//_stopmotion.set_speeds(_shooter_motor_port.getSpeed(), _shooter_motor_star.getSpeed());
	}
	
	public void semaphore_update()
	{		
		if(_ds.isEnabled() && IO.shooter_input())
		{
			System.out.println("should shoot");
			_shooter.setP(PID[0][0]);
			_shooter.setI(PID[0][1]);
			_shooter.set(-SmartDashboard.getNumber("Shooter Target Speed", 0.0));//getTargetSpeed());
			
			if(-SmartDashboard.getNumber("Shooter Target Speed", 0.0) != getTargetSpeed())
				setTargetSpeed(-SmartDashboard.getNumber("Shooter Target Speed", 0.0));
			System.out.println("speed is " + -SmartDashboard.getNumber("Shooter Target Speed", 0.0));
			//_shooter.set(setLinearSpeed(_camera._pipe.getDistance()));

			if(IO.helicopter_pulse())
 			{
 				_helicopter.set(1.0);
 				
 			}
			
			else if(getSpeedGood() || IO.shooter_override())
			{
 				_helicopter.set(-1.0); 
			}
			
			else
			{
 				_helicopter.set(0.0); 
			}
		}

		else// if(!IO.shooter_input())
		{
			//_shooter.setP(PID[0][0]);
			//_shooter.setI(PID[0][1]);
			_shooter.set(0.0);
		}
		
		/*else if(_ds.isEnabled() && IO.camera_shooter_input()) //|| _camera._isAimed)
		{
			if(_camera._isAimed)
			{
				_shooter.setP(PID[0][0]);
				_shooter.setI(PID[0][1]);
				_shooter.set(_camera.get_shooter_speed());
			}
			else
			{
				_shooter.setP(PID[1][0]);
				_shooter.setI(PID[1][1]);
				_shooter.set(_camera.get_shooter_speed());
			}
			_camera.set_drive_input(); //align to target
		}*/	
		//update_dashboard();
	}
	

	
}