package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Wheel_Shooter implements Updatable
{
	private static class ShooterTask implements Runnable
	{

        private Wheel_Shooter _task;

        ShooterTask(Wheel_Shooter t)
        {
        	_task = t;
        }

        public void run()
        {
        	_task.shooter_task();
        }
    }
	
	private static final Wheel_Shooter instance = new Wheel_Shooter();
	private static final Vision_Interface _vision = Vision_Interface.getInstance();
	
	// States: Ready, Pickup, Spinup, Shooting
	public static enum WHEEL_SHOOTER_STATE { READY, PICKUP, PICKUP_OUT, SPINUP, FIRE }
	
	private Thread _task_thread;
	private Thread _fire_task;
	
	private WHEEL_SHOOTER_STATE _state = WHEEL_SHOOTER_STATE.READY;
	private CANTalon _shooter_motor_port, _shooter_motor_star;
	private CANTalon _intake_motor;
	private final int _sensor_status;
	
	private boolean _speed_good = false;
	private boolean _thread_alive = true;

	protected Wheel_Shooter()
	{
		_shooter_motor_port = new CANTalon(Map.WHEEL_SHOOTER_PORT_SHOOTER_MOTOR);
		_shooter_motor_star = new CANTalon(Map.WHEEL_SHOOTER_STAR_SHOOTER_MOTOR);
		_intake_motor = new CANTalon(Map.WHEEL_SHOOTER_INTAKE_MOTOR);
		
		_intake_motor.enableBrakeMode(true);
		//_intake_motor.reverseOutput(true);
		
		_sensor_status = 
				(_shooter_motor_port.isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative) == CANTalon.FeedbackDeviceStatus.FeedbackStatusPresent ? 1 : 0) +
				(_shooter_motor_star.isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative) == CANTalon.FeedbackDeviceStatus.FeedbackStatusPresent? 2 : 0);
		
		if((_sensor_status & 1) != 0)
		{
			_shooter_motor_port.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
			_shooter_motor_port.changeControlMode(TalonControlMode.Speed);
		}
		if((_sensor_status & 2) != 0)
		{
			_shooter_motor_star.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
			_shooter_motor_star.changeControlMode(TalonControlMode.Speed);
		}
		
		_task_thread = new Thread(new ShooterTask(this), "1504_Shooter");
		_task_thread.start();
		
		Update_Semaphore.getInstance().register(this);
		System.out.println("Wheel Shooter Initialized");
	}
	
    /**
     * Gets an instance of the Wheel Shooter
     *
     * @return The Wheel_Shooter.
     */
	public static Wheel_Shooter getInstance()
	{
		return Wheel_Shooter.instance;
	}
	
	public boolean getSpeedGood()
	{
		return _speed_good;
	}
	
	private void shooter_task()
	{
		while(_thread_alive)
		{
			if(_state == WHEEL_SHOOTER_STATE.SPINUP || _state == WHEEL_SHOOTER_STATE.FIRE)
			{
				_shooter_motor_port.set(Map.WHEEL_SHOOTER_TARGET_SPEED);
				_shooter_motor_star.set(Map.WHEEL_SHOOTER_TARGET_SPEED);
				
				_shooter_motor_port.getSpeed();
				if(
				   (
				       (_sensor_status & 1) == 0 ||
				       Math.abs(_shooter_motor_port.getEncVelocity() - Map.WHEEL_SHOOTER_TARGET_SPEED) < Map.WHEEL_SHOOTER_SPEED_GOOD_DEADBAND
				   )
				   &&
				   (
				       (_sensor_status & 2) == 0 ||
				       Math.abs(_shooter_motor_star.getEncVelocity() - Map.WHEEL_SHOOTER_TARGET_SPEED) < Map.WHEEL_SHOOTER_SPEED_GOOD_DEADBAND
				   )
				)
					_speed_good = true;
				else
					_speed_good = false;
			}
			else
			{
				_shooter_motor_port.set(0.0);
				_shooter_motor_star.set(0.0);
				_speed_good = false;
			}
		}
	}
	
	public void set(WHEEL_SHOOTER_STATE state)
	{
		if(state == null)
			return;
		
		switch(state)
		{
			case FIRE:
				// Only fire if the wheels are spun up
				if(_state != WHEEL_SHOOTER_STATE.SPINUP || !_speed_good || _fire_task != null)
					return;
				
				// Don't shoot unless we're aimed or overridden
				if(!_vision.getAimGood() && !IO.override())
					return;
				
				_state = state;
				
				_fire_task = new Thread(new Runnable() {
					public void run() {
						_intake_motor.set(Map.WHEEL_SHOOTER_INTAKE_SPEED);
						try {
							Thread.sleep(350);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if(_state == WHEEL_SHOOTER_STATE.FIRE)
						{
							_intake_motor.set(0.0);
							_state = WHEEL_SHOOTER_STATE.SPINUP;
						}
					}
				});
				_fire_task.start();
				
				break;
				
			case SPINUP:
				if(_state != WHEEL_SHOOTER_STATE.FIRE)
				{
					if(_fire_task != null)
						return;
					
					_fire_task = new Thread(new Runnable() {
						public void run() {
							_intake_motor.set(-1.0 * Map.WHEEL_SHOOTER_INTAKE_SPEED);
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							_intake_motor.set(0.0);
							_state = WHEEL_SHOOTER_STATE.SPINUP;
						}
					});
					_fire_task.start();
				}
				else
					_state = WHEEL_SHOOTER_STATE.SPINUP;
				break;
				
			case PICKUP_OUT:
				_state = WHEEL_SHOOTER_STATE.PICKUP_OUT;
				_intake_motor.set(-1.0 * Map.WHEEL_SHOOTER_INTAKE_SPEED);
				break;
				
			case PICKUP:
				_state = WHEEL_SHOOTER_STATE.PICKUP;
				_intake_motor.set(Map.WHEEL_SHOOTER_INTAKE_SPEED);
				break;
				
			case READY:
				_intake_motor.set(0.0);
			default:
				_state = WHEEL_SHOOTER_STATE.READY;
				break;
		}
	}
	
	public void semaphore_update()
	{
		set(IO.wheel_shooter_state());
	}
}
