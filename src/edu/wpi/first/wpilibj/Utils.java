package edu.wpi.first.wpilibj;

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
}
