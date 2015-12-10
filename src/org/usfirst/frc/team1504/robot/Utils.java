package org.usfirst.frc.team1504.robot;

public class Utils
{
	public static byte double_to_byte(double input)
	{
		if(input < 0) {
            return (byte) (input * 128);
        } else {
            return (byte) (input * 127);
        }
	}
	
	public static double deadzone(double input)
	{
		if(Math.abs(input) < Map.UTIL_JOYSTICK_DEADZONE)
			return 0.0;
		return (input - Map.UTIL_JOYSTICK_DEADZONE * Math.signum(input)) / (1.0 - Map.UTIL_JOYSTICK_DEADZONE);
	}
}
