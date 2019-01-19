package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.DigitalInput;

public class Auto_Alignment {
	static DigitalInput sensor1 = new DigitalInput(Map.sensor1);
	static DigitalInput sensor2 = new DigitalInput(Map.sensor2);
	static DigitalInput sensor3 = new DigitalInput(Map.sensor3);
	static DigitalInput sensor4 = new DigitalInput(Map.sensor4);
	static DigitalInput sensor5 = new DigitalInput(Map.sensor5);
	static DigitalInput sensor6 = new DigitalInput(Map.sensor6);
	
	public static boolean check_sensors() {
		if(!sensor1.get() && !sensor3.get())
			return false;
		else if(sensor1.get() && sensor2.get() && sensor3.get())
			return false;
		else
			return true;
	}
	
	public static double[] auto_alignment() {
		double[] NULL_RESPONSE = {0.0,0.0,0.0};
		//Code to correct course of robot once vision tape is contacted (by two sensors)
		// The code stops the moment the trigger is released, so the driver can switch back to manual if they need to
		//
		//double[] alignment_values = {SmartDashboard.getNumber("Forward", 0), SmartDashboard.getNumber("Track", 0), SmartDashboard.getNumber("Rotate", 0)};
		double[] alignment_values = {0.24, 0.24, 0.32};
		final double[] FORWARD_CLOCKWISE = {0.0, 0.0, -alignment_values[2]};
		//final double[] FORWARD_CLOCKWISE = {0.2, 0.0, -0.2};
		final double[] FORWARD_COUNTERCLOCK = {0.0, 0.0, alignment_values[2]};
		final double[] FORWARD_RIGHT = {alignment_values[0], alignment_values[1], 0.0};
		final double[] FORWARD_LEFT = {alignment_values[0], -alignment_values[1], 0.0};
		final double[] FORWARD = {alignment_values[0], 0.0, 0.0};
		if(!sensor1.get()) {
		  	if(!sensor5.get() || !sensor6.get()) {
		  		return(FORWARD_CLOCKWISE);}
		  	else if(!sensor4.get())
		  		return(FORWARD_LEFT);
		  	else
		  		return(FORWARD_LEFT);
		}
		if(!sensor3.get()){
		  	if(!sensor1.get() || !sensor4.get())
		  		return(FORWARD_COUNTERCLOCK);
		  	else if(!sensor6.get())
		  		return(FORWARD_RIGHT);
		  	else
		  		return(FORWARD_RIGHT);
		}
		if(!sensor2.get()){
		  	if(!sensor4.get())
		  		return(FORWARD_COUNTERCLOCK);
		  	else if(!sensor6.get())
		  		return(FORWARD_CLOCKWISE);
		  	else if(!sensor5.get())
		  		return(FORWARD);
		  	else 
		  		return(FORWARD);
		}
		return NULL_RESPONSE;
	}
}
