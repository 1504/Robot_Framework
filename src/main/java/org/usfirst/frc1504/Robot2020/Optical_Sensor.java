package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Optical_Sensor implements Updatable
{
    private static final Optical_Sensor instance = new Optical_Sensor();
    private DriverStation _ds = DriverStation.getInstance();

    private static double[] alignment_values = {0.1, 0.1, 0.2};
	private static double[] FORWARD_CLOCKWISE = orbit_point(0.0, 0.0, -alignment_values[2], 0.0, .85);
	private static double[] FORWARD_COUNTERCLOCK = orbit_point(0.0, 0.0, alignment_values[2], 0.0, .85);
	private static double[] FORWARD_RIGHT = orbit_point(alignment_values[0], alignment_values[1], 0.0, 0.0, .85);
	private static double[] FORWARD_LEFT = orbit_point(alignment_values[0], -alignment_values[1], 0.0, 0.0, .85);
	private static double[] FORWARD = orbit_point(alignment_values[0], 0.0, 0.0, 0.0, .85);
    private static double[] REVERSE = {-0.3, 0.0, 0.0};
    private static double[] NULL = {0.0, 0.0, 0.0, 0.0};

    
    public NetworkTableEntry isDriverMode;
    public static NetworkTable table;
    public static NetworkTableEntry targetX;
    public static NetworkTableEntry targetY;

    public static Optical_Sensor getInstance() // sets instance
	{
		return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
    }

    private Optical_Sensor()
    {   
        
        table=NetworkTableInstance.getDefault().getTable("chameleon-vision").getSubTable("eyeballs");

        targetX=table.getEntry("targetYaw");
        targetY=table.getEntry("targetPitch");

        Update_Semaphore.getInstance().register(this);
        System.out.println("Optical Sensor is Blinking");
    }

    public static double[] optical_alignment() 
    {
        if(targetX.getDouble(0.0) < Map.YAW_RIGHT_MARGIN)
        {
            return FORWARD_LEFT;
        } 
        else if(targetX.getDouble(0.0) > Map.YAW_LEFT_MARGIN)
        {
            return FORWARD_RIGHT;
        }

        if(targetY.getDouble(0.0) < Map.PITCH_BOTTOM_MARGIN)
        {
            return FORWARD;
        } 
        else if(targetY.getDouble(0.0) > Map.PITCH_TOP_MARGIN)
        {
            return REVERSE;
        }
        return NULL;
    }

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

    private void update()
    {
        //table=NetworkTableInstance.getDefault().getTable("chameleon-vision").getSubTable("eyeballs");

        System.out.println("yaw " + targetX.getDouble(0.0));
        System.out.println("pitch " + targetY.getDouble(0.0));
        //targetX=table.getEntry("targetYaw");
        //targetY=table.getEntry("targetPitch");
        
    }

    public void semaphore_update() // updates robot information
	{		
		if (_ds.isDisabled()) // only runs in teleop
			return;

		update();
    }
}