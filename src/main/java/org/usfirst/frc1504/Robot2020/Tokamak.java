package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Timer;

public class Tokamak implements Updatable {
    private static final Tokamak instance = new Tokamak();
    private DriverStation _ds = DriverStation.getInstance();

    private Tractor_Beam _tractor_beam = Tractor_Beam.getInstance();
    private Ion_Cannon _ion_cannon = Ion_Cannon.getInstance();

    private WPI_TalonSRX _snake;
    private WPI_TalonSRX _serializer;

    private boolean _overcurrent = false;
    private int _overcurrent_count = 0;

    public static Tokamak getInstance() // sets instance
    {
        return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
    }

    private Tokamak()
    {
        _snake = new WPI_TalonSRX(Map.TOKAMAK_TOP); // serializer
        _serializer = new WPI_TalonSRX(Map.TOKAMAK_BOTTOM);

        Update_Semaphore.getInstance().register(this);
        System.out.println("Tokamak is generating plasma");
    }

    private void update_god()
    {
        _snake.set(IO.snake());
        _serializer.set(-IO.serializer());
    }

    private void update()
    {
        if(_tractor_beam.enabled() || (_ion_cannon.enabled() && _ion_cannon.speed_good()))
        {
            double reverse = (IO.snake_reverse() ? -1.0 : 1.0);

            _serializer.set(Map.SERIALIZER_SPEED  * reverse);

            if(_ion_cannon.enabled())
            {
                _overcurrent_count = 0;
                _overcurrent = false;
            }
            else
            {
                if(Math.abs(_snake.getStatorCurrent()) > 31.0 || _overcurrent)
                    _overcurrent_count++;
                else
                    _overcurrent_count = 0;

                if(IO.snake_reverse())
                {
                    _overcurrent_count = 0;
                    _overcurrent = false;
                }
            }

            if(
                (!_overcurrent && _overcurrent_count < 30) ||           // Must overcurrent for a time
                (_overcurrent && _overcurrent_count / 8 % 2 == 0)       // Pulse when overcurrented
              )
            {
                _snake.set(Map.TOKAMAK_SPEED * (IO.snake_reverse() ? -1.0 : 1.0));
            }
            else
            {
                _overcurrent = true;
                _snake.set(0);
            }
        }
        else
        {
            _serializer.set(0);
            _snake.set(0);
        }
        
    }

    private void update_dashboard()
    {
        SmartDashboard.putNumber("Snake Current", _snake.getStatorCurrent());
        SmartDashboard.putNumber("Serializer Current", _serializer.getStatorCurrent());
        SmartDashboard.putNumber("Overcurrent Count", _overcurrent_count);
        SmartDashboard.putBoolean("Overcurrent", _overcurrent);
    }

    public void semaphore_update() // updates robot information
    {
        update_dashboard();

        if (_ds.isDisabled()) // only runs in teleop
            return;

        if(IO.god_state)
            update_god();
        else
            update();
    }
}