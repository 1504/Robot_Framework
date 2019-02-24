package org.usfirst.frc1504.Robot2019;

import edu.wpi.first.wpilibj.DigitalInput;

public class Auto_Alignment {
	static DigitalInput sensor1 = new DigitalInput(Map.sensor1);
	static DigitalInput sensor2 = new DigitalInput(Map.sensor2);
	static DigitalInput sensor3 = new DigitalInput(Map.sensor3);
	static DigitalInput sensor4 = new DigitalInput(Map.sensor4);
	static DigitalInput sensor5 = new DigitalInput(Map.sensor5);
	static DigitalInput sensor6 = new DigitalInput(Map.sensor6);
	static DigitalInput auto_grabber_switch = new DigitalInput(Map.AUTO_GRABBER_SWITCH);
	
	static long recordedTime = 0;
	
	enum alignment_position {PICKUP_TRACKING, PICKUP, PLACEMENT_TRACKING, PLACEMENT, UNACTIVATED};
	static alignment_position alignment_state = alignment_position.UNACTIVATED;

	public static boolean get_grabber_trigger() 
	{
		return auto_grabber_switch.get();
	}
	
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
		double[] alignment_values = {0.2, 0.24, 0.43};
		final double[] FORWARD_CLOCKWISE = {0.0, 0.0, -alignment_values[2]};
		//final double[] FORWARD_CLOCKWISE = {0.2, 0.0, -0.2};
		final double[] FORWARD_COUNTERCLOCK = {0.0, 0.0, alignment_values[2]};
		final double[] FORWARD_RIGHT = {alignment_values[0], alignment_values[1], 0.0};
		final double[] FORWARD_LEFT = {alignment_values[0], -alignment_values[1], 0.0};
		final double[] FORWARD = {alignment_values[0], 0.0, 0.0};
		final double[] REVERSE = {-0.4, 0.0, 0.0};
		if(!get_grabber_trigger())
		{
			//Auto-grabbing
			if(alignment_state == alignment_position.UNACTIVATED) 
			{
				Arms.open_grabber();
				recordedTime = System.currentTimeMillis();
				alignment_state = alignment_position.PICKUP;
			}
			if(alignment_state == alignment_position.PICKUP && (System.currentTimeMillis()-recordedTime) > 1900)
			{	
				alignment_state = alignment_position.PLACEMENT_TRACKING;
				recordedTime = 0;
			}
			if(alignment_state == alignment_position.PICKUP) 
			{
				if(System.currentTimeMillis()-recordedTime > 400)
					return REVERSE;
				else
					return FORWARD;
			}
			
			//Auto-placement
			if(alignment_state == alignment_position.PLACEMENT_TRACKING && IO.get_auto_placement())
			{
				Arms.close_grabber();
				recordedTime = System.currentTimeMillis();
				alignment_state = alignment_position.PLACEMENT;
			}
		}
		else if(!(alignment_state == alignment_position.PLACEMENT))
		{
			alignment_state = alignment_position.UNACTIVATED;
		}
		if(alignment_state == alignment_position.PLACEMENT) 
		{
			if (System.currentTimeMillis()-recordedTime > 1900){
				alignment_state = alignment_position.UNACTIVATED;
				recordedTime = 0;
			}
			else if((System.currentTimeMillis()-recordedTime) > 400)
				return REVERSE;
			else
				return FORWARD;
		}
		if(!sensor1.get()) 
		{
		  	if(!sensor5.get() || !sensor6.get()) 
		  		return(FORWARD_CLOCKWISE);
		  	else if(!sensor4.get())
		  		return(FORWARD_LEFT);
		  	else
		  		return(FORWARD_LEFT);
		}
		if(!sensor3.get())
		{
		  	if(!sensor1.get() || !sensor4.get())
		  		return(FORWARD_COUNTERCLOCK);
		  	else if(!sensor6.get())
		  		return(FORWARD_RIGHT);
		  	else
		  		return(FORWARD_RIGHT);
		}
		if(!sensor2.get())
		{
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
/*
	//Didn't know if this would break the code so I commented it out, also need to set the serial ports for xcenter

	public static double[] Ball_Alignment() {
		double[] alignment_values = {0.24, 0.24, 0.32};
		final double[] FORWARD_RIGHT = {alignment_values[0], alignment_values[1], 0.0};
		final double[] FORWARD_LEFT = {alignment_values[0], -alignment_values[1], 0.0};
		final double[] FORWARD = {alignment_values[0], 0.0, 0.0};

		if(get_grabber_trigger()) {
			if(Map.xcenter > 100) {
				return(FORWARD_RIGHT);
			}
			else if(Map.xcenter < -100) {
				return(FORWARD_LEFT);
			} else {
				return(FORWARD);
			}
		}
	}
*/
}
