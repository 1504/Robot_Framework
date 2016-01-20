package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

public class Lego_Shooter implements Updatable
{
	// Position states: Down, Clear, Shoot, Store
	// Action states: Ready, Intake, Fire, Reload
	public static enum LEGO_SHOOTER_POSITION_STATE { PICKUP, CLEAR, FIRE, STORE }
	public static enum LEGO_SHOOTER_ACTION_STATE { READY, PICKUP, FIRE, RELOAD }
	
	public void semaphore_update()
	{
		// TODO Auto-generated method stub
	}
}
