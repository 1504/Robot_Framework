package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DriverStation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANEncoder;

import edu.wpi.first.wpilibj.Solenoid;

public class Lightsaber implements Updatable {
    private static final Lightsaber instance = new Lightsaber();
    private DriverStation _ds = DriverStation.getInstance();

    private CANSparkMax _lightsaber_top;
    private CANSparkMax _lightsaber_bottom;
    private CANEncoder _top_encoder;
    private CANEncoder _bottom_encoder;
    private double lightsaber_correction = 0;
    private boolean _inverted = false;
    private boolean _manual = false;
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

    private boolean toggle_manual_control() {
            return (IO.get_god_button() ? !_manual : _manual);
    }
    private boolean toggled_lightsaber_direction() {
        return (IO.ls_retract_toggle() ? !_inverted : _inverted);
    }
    private void set_lightsaber(double speed) {
        _lightsaber_top.set(speed + lightsaber_correction);
        _lightsaber_bottom.set(speed - lightsaber_correction);
    }
    
    private void update() {

        if (toggle_manual_control()) {
            set_lightsaber(IO.ls_manual_target_speed());
        } else {
            if (IO.ls_extend_button() && toggled_lightsaber_direction()) {
                _locking_activator.set(true);
                set_lightsaber(-Map.LS_TARGET_SPEED);
            } else if (IO.ls_extend_button() && !toggled_lightsaber_direction()) {
                set_lightsaber(Map.LS_TARGET_SPEED);
                _locking_activator.set(false);
            } else {
                set_lightsaber(0);
            }
        }

        lightsaber_correction = (_bottom_encoder.getPosition() - _top_encoder.getPosition()) * Map.LS_CORRECTIONAL_GAIN;
    }

    public void semaphore_update() // updates robot information
    {
        if (_ds.isDisabled()) // only runs in teleop
            return;

        update();
    }
}