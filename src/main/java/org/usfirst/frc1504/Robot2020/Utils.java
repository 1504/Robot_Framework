package org.usfirst.frc1504.Robot2020;

public class Utils

{
	public static double distance(double a, double b) {
		return Math.sqrt(a * a + b * b);
	}

	public static double snap(double val, double min, double max)
	{
		return Math.min(Math.max(val, min), max);
	}
	
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
	
	public static String toCamelCase(final String init)
	{
	    if (init==null)
	        return null;

	    final StringBuilder ret = new StringBuilder(init.length());

	    for (final String word : init.split("_")) {
	        if (!word.isEmpty()) {
	            ret.append(word.substring(0, 1).toUpperCase());
	            ret.append(word.substring(1).toLowerCase());
	        }
	        if (!(ret.length()==init.length()))
	            ret.append("_");
	    }

	    return ret.toString();
	}
}
