package org.usfirst.frc1504.Robot2021;

import edu.wpi.first.wpilibj.DriverStation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.usfirst.frc1504.Robot2021.Update_Semaphore.Updatable;

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
    private Solenoid _locking_activator;
    private double _lightsaber_correction = 0;

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
        _lightsaber_correction = (_bottom_encoder.getPosition() - _top_encoder.getPosition()) * Map.LS_CORRECTIONAL_GAIN;
        _lightsaber_top.set(speed + _lightsaber_correction);
        _lightsaber_bottom.set(speed - _lightsaber_correction);
    }

    private void set_lightsaber_raw(double left, double right) {
        _lightsaber_top.set(left);
        _lightsaber_bottom.set(right);
    }

    public void reset_encoders() {
        _lightsaber_top.getEncoder().setPosition(0.0);
        _lightsaber_bottom.getEncoder().setPosition(0.0);
    }

    private void update_god()
    {
        _locking_activator.set(true);
        set_lightsaber(IO.lightsaber());
    }

    private void update()
    {
        if(
            (_bottom_encoder.getPosition() <= Map.MAX_ENCODER_POSITION && IO.lightsaber() < 0) || // Full up
            (_bottom_encoder.getPosition() >= Map.MIN_ENCODER_POSITION && IO.lightsaber() > 0)    // Full down
          )
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
        

    }

    private void update_dashboard()
    {
        SmartDashboard.putNumber("Winch 1 Position", _bottom_encoder.getPosition());
        SmartDashboard.putNumber("Winch 2 Position", _top_encoder.getPosition());
        SmartDashboard.putNumber("Winch 1 Speed: ", _bottom_encoder.getVelocity());
        SmartDashboard.putNumber("Winch 2 Speed: ", _top_encoder.getVelocity());
        SmartDashboard.putNumber("Lightsaber Gap: ", _lightsaber_correction);
    }

    public void semaphore_update() // updates robot information
    {
        update_dashboard();

        if (_ds.isDisabled()) // only runs in teleop
            return;
        else if(_ds.isTest())
        {
            _locking_activator.set(true);
            set_lightsaber_raw(IO.snake() * 0.4, (IO.ion_shoot() ? IO.serializer() : IO.snake()) * 0.4);
            return;
        }

        if (IO.god_state)
            update_god();
        else if(IO.safe_state)
            return;
        else
            update();
    }
}