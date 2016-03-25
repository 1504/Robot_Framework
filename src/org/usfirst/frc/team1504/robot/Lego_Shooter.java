/*package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Lego_Shooter implements Updatable
{
	private static final Lego_Shooter instance = new Lego_Shooter();
	
	// Position states: Down, Clear, Shoot, Store
	// Action states: Ready, Intake, Fire, Reload
	public static enum LEGO_SHOOTER_POSITION_STATE { PICKUP, CLEAR, FIRE, STORE }
	public static enum LEGO_SHOOTER_ACTION_STATE { READY, PICKUP, PICKUP_OUT, FIRE, RELOAD }
	
	private static LEGO_SHOOTER_ACTION_STATE _action_state = LEGO_SHOOTER_ACTION_STATE.READY;
	private static LEGO_SHOOTER_POSITION_STATE _position_state = LEGO_SHOOTER_POSITION_STATE.STORE;
	
	private Thread _action_task;
//	private Thread _position_task;
//	private Thread _intake_pulse_task; // Maybe for an input auto-pulse to hang on to the ball. We'll see.
	
	private CANTalon _intake_motor;
	private DoubleSolenoid _shooter_release;
	private DoubleSolenoid _shooter_reload;
	private DoubleSolenoid _pickup_position;
	
	protected Lego_Shooter()
	{
		_shooter_release = new DoubleSolenoid(1,2);
		_shooter_reload = new DoubleSolenoid(1,2);
		_pickup_position = new DoubleSolenoid(1,2);
		
		Update_Semaphore.getInstance().register(this);
		System.out.println("LEGO Shooter Initialized");
	}
	
    /**
     * Gets an instance of the LEGO Shooter
     *
     * @return The Lego_Shooter.
     *
	public static Lego_Shooter getInstance()
	{
		return Lego_Shooter.instance;
	}
	
	public void set_position(LEGO_SHOOTER_POSITION_STATE state)
	{
		if(state == null)
			return;
		
		switch(state)
		{
		default:
			break;
		}
	}
	
	public void set_action(LEGO_SHOOTER_ACTION_STATE state)
	{
		if(state == null)
			return;
		
		double pickup_multiplier = 1.0;
		switch(state)
		{
			case FIRE:
				// Fire a shot, but only if we're ready and in position
				if(_action_state == LEGO_SHOOTER_ACTION_STATE.READY && _position_state == LEGO_SHOOTER_POSITION_STATE.FIRE && _action_task == null)
				{
					_action_state = LEGO_SHOOTER_ACTION_STATE.FIRE;
					_action_task = new Thread(new Runnable() {
						public void run() {
							// Open claw
							_pickup_position.set(DoubleSolenoid.Value.kReverse);
							try {
								Thread.sleep(20);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							// Release the catch
							_shooter_release.set(DoubleSolenoid.Value.kReverse);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							// Start reloading
							//set_action(LEGO_SHOOTER_ACTION_STATE.RELOAD);
							_action_state = LEGO_SHOOTER_ACTION_STATE.RELOAD;
							
							// Push back the plunger
							_shooter_reload.set(DoubleSolenoid.Value.kForward);
							try {
								Thread.sleep(750);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							// Close the latch
							_shooter_release.set(DoubleSolenoid.Value.kForward);
							try {
								Thread.sleep(20);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							// Retract the plunger
							_shooter_reload.set(DoubleSolenoid.Value.kReverse);
							try {
								Thread.sleep(150);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							// Close the claw
							_pickup_position.set(DoubleSolenoid.Value.kForward);
							
							// Loop back to ready state
							set_action(LEGO_SHOOTER_ACTION_STATE.READY);
						}
					});
					_action_task.start();
				}
				break;
				
			/*case RELOAD:
				// Reload if we just fired a shot
				if(_action_state == LEGO_SHOOTER_ACTION_STATE.FIRE)
				{
					_action_state = LEGO_SHOOTER_ACTION_STATE.RELOAD;
					_action_task  = new Thread(new Runnable() {
						public void run() {
							
						}
					});
					_action_task.start();
				}
				break;*
			
			case PICKUP_OUT:
				pickup_multiplier = -1.0;
			case PICKUP:
				if(_action_state == LEGO_SHOOTER_ACTION_STATE.READY)
				{
					_intake_motor.set(pickup_multiplier * Map.LEGO_SHOOTER_INTAKE_SPEED);
					_action_state = state;
				}
				break;
			
			default:
				_action_state = LEGO_SHOOTER_ACTION_STATE.READY;
			case READY:
				// Pickup motor off, claw closed
				_intake_motor.set(0.0);
				_pickup_position.set(DoubleSolenoid.Value.kForward);
				break;
		}
	}
	
	public void semaphore_update()
	{
		set_action(IO.lego_shooter_action());
	}
}*/