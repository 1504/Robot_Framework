package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Tractor_Beam implements Updatable {
    private static final Tractor_Beam instance = new Tractor_Beam();
    private DriverStation _ds = DriverStation.getInstance();

    private WPI_TalonSRX _beam;
    public static DoubleSolenoid _ef_engager;
    public static Timer tb_timer = new Timer();

    private boolean _enabled = false;

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
        _ef_engager = new DoubleSolenoid(Map.EF_ENGAGER_HIGHSIDE_PORT, Map.EF_ENGAGER_LOWSIDE_PORT);
        Update_Semaphore.getInstance().register(this);
        System.out.println("Tractor Beam Engaged");
    }

    public void enable(boolean enable)
    {
        _enabled = enable;
    }

    public boolean enabled()
    {
        return _enabled;
    }

    private void update_god()
    {
        _beam.set(IO.god_tb() > 0 ? -IO.god_tb() : 0);
        if (IO.god_ef())
            _ef_engager.set(_ef_engager.get() == DoubleSolenoid.Value.kForward ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward);
        return;
    }

    private void update()
    {
        if (_enabled)
        {
            _ef_engager.set(DoubleSolenoid.Value.kForward);
            _beam.set(Map.TRACTOR_BEAM_SPEED  * (IO.snake_reverse() ? -1.0 : 1.0));
        }
        else
        {
            _ef_engager.set(DoubleSolenoid.Value.kReverse);
            _beam.set(0);
        }
    }

    private void update_dashboard()
    {

    }

    public void semaphore_update() // updates robot information
    {
        update_dashboard();

        if (_ds.isDisabled()) // only runs in teleop
            return;

        if(IO.god_state)
        {
            update_god();
            return;
        }
        
        if(_ds.isOperatorControl())
            enable(IO.tb_activate());
        update();
    }
}