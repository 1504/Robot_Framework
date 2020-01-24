package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DriverStation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANEncoder;

public class Lightsaber implements Updatable {
    private static final Lightsaber instance = new Lightsaber();
    private DriverStation _ds = DriverStation.getInstance();

    private CANSparkMax _lightsaber_top;
    private CANSparkMax _lightsaber_bottom;
    private CANEncoder _top_encoder = new CANEncoder(_lightsaber_top);
    private CANEncoder _bottom_encoder = new CANEncoder(_lightsaber_bottom);
    private double lightsaber_correction = 0;
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

        Update_Semaphore.getInstance().register(this);
        System.out.println("Lightsaber is on");
    }

    private void update() {

        _lightsaber_top.set(IO.get_lightsaber_height() + lightsaber_correction);
        _lightsaber_bottom.set(IO.get_lightsaber_height() - lightsaber_correction);
        lightsaber_correction = _bottom_encoder.getPosition() - _top_encoder.getPosition();
    }

    public void semaphore_update() // updates robot information
    {
        if (_ds.isDisabled()) // only runs in teleop
            return;

        update();
    }
}