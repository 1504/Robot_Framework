package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Tractor_Beam implements Updatable {
    private static final Tractor_Beam instance = new Tractor_Beam();
    private DriverStation _ds = DriverStation.getInstance();

    private WPI_TalonSRX _beam;
    private DoubleSolenoid _ef_engager;
    public static Timer tb_timer = new Timer();

    private static boolean _ef_god_state = false;
    public static boolean _tb_state = false;


    public static Tractor_Beam getInstance() // sets instance
    {
        return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
    }

    private Tractor_Beam() {
        _beam = new WPI_TalonSRX(Map.TRACTOR_BEAM);
        _ef_engager = new DoubleSolenoid(Map.EF_ENGAGER_HIGHSIDE_PORT, Map.EF_ENGAGER_LOWSIDE_PORT);
        Update_Semaphore.getInstance().register(this);
        System.out.println("Tractor Beam Engaged");
    }

    private void update() {
        if (IO.god_state) {
            if (IO.god_tb() > 0) {
                _beam.set(-Map.TRACTOR_BEAM_SPEED);
            } else {
                _beam.set(0);
            }
            if (IO.god_ef()) {
                _ef_god_state = !_ef_god_state;
            }
            
            if(_ef_god_state) {
                _ef_engager.set(DoubleSolenoid.Value.kForward);
            } else {
                _ef_engager.set(DoubleSolenoid.Value.kReverse);
            }
        } else {
            if (IO.tb_activate()) {
                _tb_state = !_tb_state;
            }

            if(_tb_state) {
                _ef_engager.set(DoubleSolenoid.Value.kForward);
                _beam.set(-Map.TRACTOR_BEAM_SPEED);
                Tokamak.serializer.set(-Map.SERIALIZER_SPEED);
                if (Tokamak.current_check(Tokamak.snake)) {
                    Tokamak.snake.set(-Map.TOKAMAK_SPEED);
                }
            } else {
                _ef_engager.set(DoubleSolenoid.Value.kReverse);
                _beam.set(0);
                Tokamak.serializer.set(0);
            }
        }
        /*
         * if (IO.get_tractor_beam_activation() == 0.1) { tb_timer.start(); } if
         * (IO.get_tractor_beam_activation() > 0) { _beam.set(-Map.TRACTOR_BEAM_SPEED);
         * System.out.println(tb_timer); _ef_engager.set(DoubleSolenoid.Value.kForward);
         * } else { _beam.set(0.0); tb_timer.stop(); tb_timer.reset();
         * _ef_engager.set(DoubleSolenoid.Value.kReverse); }
         */
    }

    public void semaphore_update() // updates robot information
    {
        if (_ds.isDisabled()) // only runs in teleop
            return;

        update();
    }
}