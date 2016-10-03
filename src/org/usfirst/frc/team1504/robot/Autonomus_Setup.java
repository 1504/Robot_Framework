package org.usfirst.frc.team1504.robot;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team1504.robot.Autonomous.Autonomus_Waypoint;
import org.usfirst.frc.team1504.robot.Autonomous.Autonomus_Waypoint.TYPE;

public class Autonomus_Setup
{
	private static Digit_Board _digit = Digit_Board.getInstance();
	
	public static Autonomus_Waypoint[] getPath()
	{
		//Autonomus_Waypoint[] path = null;
		List<Autonomus_Waypoint> path = new ArrayList<Autonomus_Waypoint>();
		String sample = "";
		int position = _digit.getPosition();
		double delay = _digit.getDelay();
		// Set up delay before running
		path.add(new Autonomus_Waypoint(TYPE.TIME, delay, new double[] {0.0, 0.0}));
		
		switch(sample)
		{
		case "LowB":
			break;
		default:
			return null;
		}
		
		//path.add(new Autonomus_Waypoint(TYPE.FIRE, 5.0, new double[] {0.0, 0.0}));
		
		return (Autonomus_Waypoint[]) path.toArray();
	}
}
