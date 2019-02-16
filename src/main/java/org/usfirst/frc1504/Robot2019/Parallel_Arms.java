package org.usfirst.frc1504.Robot2019;
import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DriverStation;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Parallel_Arms implements Updatable {
	private WPI_TalonSRX firstActuator;
    private WPI_TalonSRX secondActuator;

    private static final Parallel_Arms instance = new Parallel_Arms();
	private DriverStation _ds = DriverStation.getInstance();
	public static Parallel_Arms getInstance() // sets instance
	{
		return instance;
	}
	private Parallel_Arms() // arms constructor
	{
		firstActuator = new WPI_TalonSRX(Map.FIRST_ACTUATOR);
        secondActuator = new WPI_TalonSRX(Map.SECOND_ACTUATOR);
	}
	public static void initialize() //initialize
	{
		getInstance();
    }
    public void setSpeed(double speed)
    {
        firstActuator.set(speed);
        secondActuator.set(-speed);

    }
	public void semaphore_update() //updates robot information
	{
        if(_ds.isOperatorControl() && !_ds.isDisabled()) //only runs in teleop
		{

		}
	}
}
