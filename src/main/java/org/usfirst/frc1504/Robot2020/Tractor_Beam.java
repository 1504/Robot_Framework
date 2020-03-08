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

    private static boolean _ef_god_state = false;
    private boolean _overcurrent = false;
    private int _overcurrent_count = 0;

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
                _beam.set(-IO.god_tb());
            } else {
                _beam.set(0);
            }
            if (IO.god_ef()) {
                _ef_god_state = !_ef_god_state;
            }

            if (_ef_god_state) {
                _ef_engager.set(DoubleSolenoid.Value.kForward);
            } else {
                _ef_engager.set(DoubleSolenoid.Value.kReverse);
            }
        } else {

            if (IO.tb_activate()) {
                _ef_engager.set(DoubleSolenoid.Value.kForward);
                _beam.set(-Map.TRACTOR_BEAM_SPEED  * (IO.snake_reverse() ? -1.0 : 1.0));
                Tokamak.serializer.set(-Map.SERIALIZER_SPEED  * (IO.snake_reverse() ? -1.0 : 1.0));
                //if (Tokamak.current_check(Tokamak.snake)) 
                {
                    if(Math.abs(Tokamak.snake.getStatorCurrent()) > 31.0 || _overcurrent)
                        _overcurrent_count++;
                    else
                        _overcurrent_count = 0;

                    if(IO.snake_reverse())
                    {
                        _overcurrent_count = 0;
                        _overcurrent = false;
                    }
                    
                    if((!_overcurrent && _overcurrent_count < 30) || IO.ion_vision() || (_overcurrent && _overcurrent_count / 8 % 2 == 0))
                    {
                        Tokamak.snake.set(Map.TOKAMAK_SPEED * (IO.snake_reverse() ? -1.0 : 1.0));
                    }
                    else
                    {
                        _overcurrent = true;
                        Tokamak.snake.set(0);
                    }
                }
            }
            if (!IO.tb_activate() && !IO.ion_high() && !IO.ion_low()) {
                _ef_engager.set(DoubleSolenoid.Value.kReverse);
                _beam.set(0);
                Tokamak.serializer.set(0);
                _overcurrent_count = 0;
            }

            if(IO.ion_high() || IO.ion_low())
                _overcurrent = false;
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
        SmartDashboard.putNumber("Overcurrent Count", _overcurrent_count);
        SmartDashboard.putBoolean("Overcurrent", _overcurrent);

        if (_ds.isDisabled()) // only runs in teleop
            return;

        update();
    }
}