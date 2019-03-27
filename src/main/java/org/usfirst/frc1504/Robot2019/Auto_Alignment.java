package org.usfirst.frc1504.Robot2019;

import edu.wpi.first.wpilibj.DigitalInput;


public class Auto_Alignment {
	static DigitalInput sensor1 = new DigitalInput(Map.sensor1);
	static DigitalInput sensor2 = new DigitalInput(Map.sensor2);
	static DigitalInput sensor3 = new DigitalInput(Map.sensor3);
	static DigitalInput sensor4 = new DigitalInput(Map.sensor4);
	static DigitalInput sensor5 = new DigitalInput(Map.sensor5);
	static DigitalInput sensor6 = new DigitalInput(Map.sensor6);
	
	static long recordedTime = 0;
	
	enum alignment_position {PICKUP_TRACKING, PICKUP, PLACEMENT_TRACKING, PLACEMENT, UNACTIVATED};
	static alignment_position alignment_state = alignment_position.UNACTIVATED;

	private static double[] NULL_RESPONSE = {0.0,0.0,0.0};
	private static double[] alignment_values = {0.1, 0.1, 0.2};
	private static double[] FORWARD_CLOCKWISE = orbit_point(0.0, 0.0, -alignment_values[2], 0.0, .85);
	private static double[] FORWARD_COUNTERCLOCK = orbit_point(0.0, 0.0, alignment_values[2], 0.0, .85);
	private static double[] FORWARD_RIGHT = orbit_point(alignment_values[0], alignment_values[1], 0.0, 0.0, .85);
	private static double[] FORWARD_LEFT = orbit_point(alignment_values[0], -alignment_values[1], 0.0, 0.0, .85);
	private static double[] FORWARD = orbit_point(alignment_values[0], 0.0, 0.0, 0.0, .85);
	private static double[] REVERSE = {-0.3, 0.0, 0.0};


	/*FORWARD_CLOCKWISE = FORWARD_CLOCKWISE;
	FORWARD_COUNTERCLOCK = FORWARD_COUNTERCLOCK;
	FORWARD_RIGHT = FORWARD_RIGHT;
	FORWARD_LEFT = FORWARD_LEFT;
	FORWARD = FORWARD;
	REVERSE = orbit_point(REVERSE, 0, .85);*/

	private static double[] orbit_point(double fwd, double rgt, double ccw, double x, double y)
	{
		//double x = _orbit_point[0];
		//double y = _orbit_point[1];
		
		double[] input = {fwd, rgt, ccw};

		double[] k = { y - 1, y + 1, 1 - x, -1 - x };

		double p = Math.sqrt((k[0] * k[0] + k[2] * k[2]) / 2) * Math.cos((Math.PI / 4) + Math.atan2(k[0], k[2]));
		double r = Math.sqrt((k[1] * k[1] + k[2] * k[2]) / 2) * Math.cos(-(Math.PI / 4) + Math.atan2(k[1], k[2]));
		double q = -Math.sqrt((k[1] * k[1] + k[3] * k[3]) / 2) * Math.cos((Math.PI / 4) + Math.atan2(k[1], k[3]));

		double[] corrected = new double[3];
		corrected[0] = (input[2] * r + (input[0] - input[2]) * q + input[0] * p) / (q + p);
		corrected[1] = (-input[2] * r + input[1] * q - (-input[1] - input[2]) * p) / (q + p);
		corrected[2] = (2 * input[2]) / (q + p);
		return corrected;
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
		/*double[] NULL_RESPONSE = {0.0,0.0,0.0};
		//Code to correct course of robot once vision tape is contacted (by two sensors)
		// The code stops the moment the trigger is released, so the driver can switch back to manual if they need to
		//
		//double[] alignment_values = {SmartDashboard.getNumber("Forward", 0), SmartDashboard.getNumber("Track", 0), SmartDashboard.getNumber("Rotate", 0)};
		double[] alignment_values = {0.1, 0.1, 0.2};
		final double[] FORWARD_CLOCKWISE = {0.0, 0.0, -alignment_values[2]};
		//final double[] FORWARD_CLOCKWISE = {0.2, 0.0, -0.2};
		final double[] FORWARD_COUNTERCLOCK = {0.0, 0.0, alignment_values[2]};
		final double[] FORWARD_RIGHT = {alignment_values[0], alignment_values[1], 0.0};
		final double[] FORWARD_LEFT = {alignment_values[0], -alignment_values[1], 0.0};
		final double[] FORWARD = {alignment_values[0], 0.0, 0.0};
		final double[] REVERSE = {-0.3, 0.0, 0.0};*/
		if(Hatch.getInstance().getHatchInput())
		{
			//Auto-grabbing
			if(alignment_state == alignment_position.UNACTIVATED) 
			{
				//Arms.open_grabber();
				Hatch.getInstance().set_state(Hatch.HATCH_STATE.OPEN);
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
				//Arms.close_grabber();
				Hatch.getInstance().set_state(Hatch.HATCH_STATE.CLOSED);
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
		  		return(FORWARD_COUNTERCLOCK);
		  	else if(!sensor4.get())
		  		return(FORWARD_LEFT);
		  	else
		  		return(FORWARD_LEFT);
		}
		if(!sensor3.get())
		{
		  	if(!sensor5.get() || !sensor4.get())
		  		return(FORWARD_CLOCKWISE);
		  	else if(!sensor6.get())
		  		return(FORWARD_RIGHT);
		  	else
		  		return(FORWARD_RIGHT);
		}
		if(!sensor2.get())
		{
		  	if(!sensor4.get())
		  		return(FORWARD_CLOCKWISE);
		  	else if(!sensor6.get())
		  		return(FORWARD_COUNTERCLOCK);
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
		int xcenter = ai.getValue();
		System.out.println(xcenter);
		double[] alignment_values = {0.24, 0.24, 0.32};
		final double[] FORWARD_RIGHT = {alignment_values[0], alignment_values[1], 0.0};
		final double[] FORWARD_LEFT = {alignment_values[0], -alignment_values[1], 0.0};
		final double[] FORWARD = {alignment_values[0], 0.0, 0.0};

		if(get_grabber_trigger()) {
			if(xcenter == 0){
				break;
			}
			else if(xcenter > 100) {
				return(FORWARD_LEFT);
			}
			else if(xcenter < -100) {
				return(FORWARD_RIGHT);
			}
		}
	}
*/
}
