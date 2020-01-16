package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
public class Tractor_Beam implements Updatable
{
    private static final Tractor_Beam instance = new Tractor_Beam();
    private DriverStation _ds = DriverStation.getInstance();

    private WPI_TalonSRX _beam;
    private WPI_TalonSRX _serializer_top;
    private WPI_TalonSRX _serializer_bottom;
    private DoubleSolenoid _ef_engager;

    public static Tractor_Beam getInstance() // sets instance
	{
		return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
    }


    private Tractor_Beam()
    {
        _beam = new WPI_TalonSRX(Map.TRACTOR_BEAM);
        _serializer_top = new WPI_TalonSRX(Map.SERIALIZER_TOP); // serializer 
        _serializer_bottom = new WPI_TalonSRX(Map.SERIALIZER_BOTTOM);
        _ef_engager = new DoubleSolenoid(Map.EF_ENGAGER_HIGHSIDE_PORT, Map.EF_ENGAGER_LOWSIDE_PORT);

        Update_Semaphore.getInstance().register(this);
        System.out.println("Tractor Beam Engaged");
    }

    private void update()
    {
        if(IO.get_tractor_speed() > 0)
        {
            _beam.set(IO.get_tractor_speed());
            _serializer_top.set(1);
            _serializer_bottom.set(-1);

            _ef_engager.set(DoubleSolenoid.Value.kForward);
        } else {
            _ef_engager.set(DoubleSolenoid.Value.kReverse);
            _serializer_top.set(0);
            _serializer_bottom.set(0);
        }
        
    }

    public void semaphore_update() // updates robot information
	{		
		if (_ds.isDisabled()) // only runs in teleop
			return;

		update();
    }
}