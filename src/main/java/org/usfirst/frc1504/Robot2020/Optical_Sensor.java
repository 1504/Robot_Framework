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
        
        table=NetworkTableInstance.getDefault().getTable("chameleon-vision").getSubTable("eyeballs");

        targetX=table.getEntry("targetYaw");
        targetY=table.getEntry("targetPitch");

        Update_Semaphore.getInstance().register(this);
        System.out.println("Optical Sensor is Blinking");
    }

    private void update()
    {
        //table=NetworkTableInstance.getDefault().getTable("chameleon-vision").getSubTable("eyeballs");
        System.out.println("yaw " + targetX.getDouble(0.0));
        System.out.println("pitch" + targetY.getDouble(0.0));
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