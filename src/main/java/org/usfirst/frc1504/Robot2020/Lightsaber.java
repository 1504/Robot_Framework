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

        Update_Semaphore.getInstance().register(this);
        System.out.println("Lightsaber is on. Vrrrrnnnnnnnnnn~...");
    }

    private void set_lightsaber(double speed) {
        _lightsaber_top.set(speed + lightsaber_correction);
        _lightsaber_bottom.set(speed - lightsaber_correction);
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


    private void update()
    {
        if (!IO.god_state){
            if (IO.lightsaber() > 0 && !_up) {
                //ratchet();
                _locking_activator.set(true);
                Timer.delay(0.1);
                set_lightsaber(-Map.LS_TARGET_SPEED);
                Timer.delay(0.1);
                _up = true;
            } else if (IO.lightsaber() > 0 && _up) {
                //_locking_activator.set(false);
                set_lightsaber(-IO.lightsaber());
                _up = true;
            } else if (IO.lightsaber() <= 0.01) {   
                _locking_activator.set(false);
                set_lightsaber(-IO.lightsaber());
                _up = false;
            }
        }
        System.out.println(IO.lightsaber());
        System.out.println("UP: " + _up);

        lightsaber_correction = (_bottom_encoder.getPosition() - _top_encoder.getPosition()) * Map.LS_CORRECTIONAL_GAIN;
        //SmartDashboard.putBoolean("Manual Toggle: ", toggle_manual_control());
        SmartDashboard.putNumber("Lightsaber Bottom Speeds: ", _bottom_encoder.getVelocity());
        SmartDashboard.putNumber("Lightsaber Top Speeds: ", _top_encoder.getVelocity());
        SmartDashboard.putNumber("Lightsaber Gap: ", lightsaber_correction);
        lightsaber_correction = 0;


    }

    public void semaphore_update() // updates robot information
    {
        //System.out.println( _bottom_encoder.getPosition());
        //System.out.println(_top_encoder.getPosition());
        //System.out.println("test");

        if (_ds.isDisabled()) // only runs in teleop
            return;

        update();
    }
}