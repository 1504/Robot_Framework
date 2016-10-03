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
		
		String defense = _digit.getDefense();
		int position = _digit.getPosition();
		double delay = _digit.getDelay();
		
		if(position != 0 && defense == "LowB")
			return null;
		
		// Set up delay before running
		path.add(new Autonomus_Waypoint(TYPE.TIME, delay, new double[] {0.0, 0.0}));
		
		//"LowB", "Ptcs", "Chiv", "Moat", "Ramp", "Draw", "SlPt", "Rock", "Ruff"
		switch(defense)
		{
		case "LowB":
			path.add(new Autonomus_Waypoint(TYPE.DISTANCE, 3.0, new double[] {15.0, 0.0}));
			break;
			
		case "Moat":
		case "Ruff":
		case "Rock":
		case "Ramp":
			path.add(new Autonomus_Waypoint(TYPE.DISTANCE, 3.0, new double[] {15.0, 0.0}));
			path.add(new Autonomus_Waypoint(TYPE.JOSTLE, 3.0, new double[] {0.75, 0.0}));
			break;
		
		case "Ptcs":
		case "Chiv":
		case "Draw":
		case "SlPt":
		default:
			return null;
		}
		
		switch(position)
		{
		case 0:
			path.add(new Autonomus_Waypoint(TYPE.ANGLE, 3.0, new double[] {0.0, 30.0}));
			break;
		case 1:
			path.add(new Autonomus_Waypoint(TYPE.ANGLE, 3.0, new double[] {0.0, 20.0}));
			break;
		case 2:
			path.add(new Autonomus_Waypoint(TYPE.ANGLE, 3.0, new double[] {0.0, 10.0}));
			break;
		case 3:
			path.add(new Autonomus_Waypoint(TYPE.ANGLE, 3.0, new double[] {0.0, 0.0}));
			break;
		case 4:
			path.add(new Autonomus_Waypoint(TYPE.ANGLE, 3.0, new double[] {0.0, -10.0}));
			break;
		}
		
		path.add(new Autonomus_Waypoint(TYPE.FIRE, 5.0, new double[] {0.0, 0.0}));
		
		return (Autonomus_Waypoint[]) path.toArray();
	}
}
