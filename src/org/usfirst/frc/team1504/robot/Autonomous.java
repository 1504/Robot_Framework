package org.usfirst.frc.team1504.robot;

public class Autonomous {

	private static final Autonomous instance = new Autonomous();
	
	protected Autonomous()
	{
		//
	}
	
	public Autonomous getInstance()
	{
		return instance;
	}
}
