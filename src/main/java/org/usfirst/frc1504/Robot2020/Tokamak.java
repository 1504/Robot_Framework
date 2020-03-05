package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DriverStation;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Timer;

public class Tokamak implements Updatable {
    private static final Tokamak instance = new Tokamak();
    private DriverStation _ds = DriverStation.getInstance();

    public static WPI_TalonSRX snake;
    public static WPI_TalonSRX serializer;

    private boolean _manual = false;

    public static Tokamak getInstance() // sets instance
    {
        return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
    }

    private Tokamak() {
        snake = new WPI_TalonSRX(Map.TOKAMAK_TOP); // serializer
        serializer = new WPI_TalonSRX(Map.TOKAMAK_BOTTOM);

        Update_Semaphore.getInstance().register(this);
        System.out.println("Tokamak is generating plasma");
    }

    private static double get_current(WPI_TalonSRX motor) {
        return motor.getSupplyCurrent();
    }

    public static boolean current_check(WPI_TalonSRX motor) {
        if (get_current(motor) > Map.TOKAMAK_CURRENT) {
            for (int i = 0; i < Map.JIGGLE_REPITITIONS; i++) {
                motor.set(Map.TOKAMAK_JIGGLE_SPEED);
                Timer.delay(Map.JIGGLE_INTERVAL);
                motor.set(-(Map.TOKAMAK_JIGGLE_SPEED));
            }
        }
        return true;
    }

    private void update() {
        if (IO.god_state) {
            snake.set(IO.snake());
            serializer.set(-IO.serializer());
        } else if (!IO.tb_activate() && !IO.ion_high() && !IO.ion_low()) { // this is so snake knows when to stop, we
                                                                           // should add another conditional for shooter
                                                                           // state
            snake.set(0);
            serializer.set(0);
        }
        
    }

    public void semaphore_update() // updates robot information
    {
        if (_ds.isDisabled()) // only runs in teleop
            return;

        update();
    }
}