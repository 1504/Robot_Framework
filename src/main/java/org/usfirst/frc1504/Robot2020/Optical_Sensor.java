package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Optical_Sensor implements Updatable
{
    private static final Optical_Sensor instance = new Optical_Sensor();
    private DriverStation _ds = DriverStation.getInstance();

    public NetworkTableEntry isDriverMode;
    NetworkTable table;
    NetworkTableEntry targetX;
    NetworkTableEntry targetY;

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
        
        table=NetworkTableInstance.getDefault().getTable("chameleon-vision").getSubTable("MyCamName");

        targetX=table.getEntry("yaw");
        targetY=table.getEntry("pitch");

        Update_Semaphore.getInstance().register(this);
        System.out.println("Optical Sensor is Blinking");
    }

    private void update()
    {
        System.out.println(targetX);
        System.out.println(targetY);
    }

    public void semaphore_update() // updates robot information
	{		
		if (_ds.isDisabled()) // only runs in teleop
			return;

		update();
    }
}