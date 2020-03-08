package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DriverStation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANEncoder;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

public class Lightsaber implements Updatable {
    private static final Lightsaber instance = new Lightsaber();
    private DriverStation _ds = DriverStation.getInstance();

    private CANSparkMax _lightsaber_top;
    private CANSparkMax _lightsaber_bottom;
    private CANEncoder _top_encoder;
    private CANEncoder _bottom_encoder;
    private double lightsaber_correction = 0;
    private boolean _up = true;
    private Solenoid _locking_activator;

    public static Lightsaber getInstance() // sets instance
    {
        return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
    }

    private Lightsaber() {
        _lightsaber_top = new CANSparkMax(Map.LIGHTSABER_TOP, MotorType.kBrushless); // serializer
        _lightsaber_bottom = new CANSparkMax(Map.LIGHTSABER_BOTTOM, MotorType.kBrushless);

        _top_encoder = _lightsaber_top.getEncoder();
        _bottom_encoder = _lightsaber_bottom.getEncoder();

        _locking_activator = new Solenoid(Map.LOCKING_ACTIVATOR_PORT);

        reset_encoders();

        Update_Semaphore.getInstance().register(this);
        System.out.println("Lightsaber is on. Vrrrrnnnnnnnnnn~...");
    }

    private void set_lightsaber(double speed) {
        _lightsaber_top.set(speed + lightsaber_correction);
        _lightsaber_bottom.set(speed - lightsaber_correction);
    }

    public void set_lightsaber_raw(double left, double right) {
        _lightsaber_top.set(left);
        _lightsaber_bottom.set(right);
    }

    public void reset_encoders() {
        _lightsaber_top.getEncoder().setPosition(0.0);
        _lightsaber_bottom.getEncoder().setPosition(0.0);
    }

    private boolean activated() {
        return (_up ? !_up : _up);
    }

    private void ratchet() {
        set_lightsaber(-Map.LS_TARGET_SPEED);
        Timer.delay(0.2);
        _locking_activator.set(true);
        Timer.delay(0.2);
        _up = true;
    }

    private double calculate_speed() {
        return -(IO.lightsaber() / _bottom_encoder.getPosition()) * 10;
    }

    private void update()
    {

        lightsaber_correction = (_bottom_encoder.getPosition() - _top_encoder.getPosition()) * Map.LS_CORRECTIONAL_GAIN;
        // SmartDashboard.putBoolean("Manual Toggle: ", toggle_manual_control());
        SmartDashboard.putNumber("Lightsaber Bottom Speeds: ", _bottom_encoder.getVelocity());
        SmartDashboard.putNumber("Lightsaber Top Speeds: ", _top_encoder.getVelocity());
        SmartDashboard.putNumber("Lightsaber Gap: ", lightsaber_correction);

        if (_ds.isTest() || _ds.isDisabled())
            return;

        if (!IO.god_state)
        {
            if(_bottom_encoder.getPosition() <= Map.MAX_ENCODER_POSITION && IO.lightsaber() < 0)
            {
                set_lightsaber_raw(0, 0);
                return;
            }
            if(_bottom_encoder.getPosition() >= Map.MIN_ENCODER_POSITION && IO.lightsaber() > 0)
            {
                set_lightsaber_raw(0, 0);
                return;
            }

            if(IO.lightsaber() < 0)
            {
                if(!_locking_activator.get())
                {
                    _locking_activator.set(true);
                    Timer.delay(0.1);
                    set_lightsaber_raw(Map.LS_TARGET_SPEED, Map.LS_TARGET_SPEED);
                    Timer.delay(0.04);
                }

                set_lightsaber(IO.lightsaber() / (1.0 + _bottom_encoder.getPosition() / (Map.MAX_ENCODER_POSITION / 2.0))); // 
            }
            else
            {
                _locking_activator.set(false);
                if(IO.lightsaber() > 0)
                    set_lightsaber(IO.lightsaber());
                else
                    set_lightsaber_raw(0, 0);
            }

            
            /*if (_bottom_encoder.getPosition() <= Map.MAX_ENCODER_POSITION
                    && _bottom_encoder.getPosition() <= Map.MIN_ENCODER_POSITION) {
                set_lightsaber(0);
            }*/
            /*if (IO.lightsaber() > 0 && !_up) {
                // ratchet();
                _locking_activator.set(true);
                Timer.delay(0.1);
                set_lightsaber(-Map.LS_TARGET_SPEED);
                Timer.delay(0.01);
                _up = true;
            } else if (IO.lightsaber() > 0 && _up) {
                // _locking_activator.set(false);
                set_lightsaber(-calculate_speed());
                _up = true;
            } else if (IO.lightsaber() <= 0.01) {
                _locking_activator.set(false);
                set_lightsaber(-IO.lightsaber());
                _up = false;
            }*/
        }

    }

    public void semaphore_update() // updates robot information
    {
        // System.out.println( _bottom_encoder.getPosition());
        // System.out.println(_top_encoder.getPosition());
        // System.out.println("test");
        SmartDashboard.putNumber("Winch 1 Position", _bottom_encoder.getPosition());
        SmartDashboard.putNumber("Winch 2 Position", _top_encoder.getPosition());

        if (_ds.isDisabled()) // only runs in teleop
            return;
        else if(_ds.isTest())
            _locking_activator.set(true);

        update();
    }
}