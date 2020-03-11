package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Timer;

public class Ion_Cannon implements Updatable {
    public enum ION_CANNON_STATE {
        LOW, HIGH, DISABLED
    }

    private static final Ion_Cannon instance = new Ion_Cannon();
    private DriverStation _ds = DriverStation.getInstance();
    private static final boolean _top_enabled = false;

    private CANSparkMax _top_shoot;
    private static CANSparkMax _bottom_shoot;
    public static CANEncoder _top_encoder;
    public static CANEncoder _bottom_encoder;

    private static CANPIDController _top_pid;
    private static CANPIDController _bottom_pid;

    public static Solenoid _top_extender;
    public static DoubleSolenoid _extender;

    public static double speed_offset = 0;

    // private double cannon_spin = 0;

    private static final double[] _setpoints = { 2000, 4800, 0 };

    private static ION_CANNON_STATE _state = ION_CANNON_STATE.DISABLED;

    public static Ion_Cannon getInstance() // sets instance
    {
        return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
    }

    private Ion_Cannon() {

        if (_top_enabled) {
            _top_shoot = new CANSparkMax(Map.ION_CANNON_TOP, MotorType.kBrushless);
            _top_shoot.setIdleMode(CANSparkMax.IdleMode.kBrake);
            _top_encoder = _top_shoot.getEncoder();
            _top_pid = _top_shoot.getPIDController();
            _top_pid.setP(0.00014);
            _top_pid.setFF(0.00017);
        }

        _bottom_shoot = new CANSparkMax(Map.ION_CANNON_BOTTOM, MotorType.kBrushless);
        _bottom_shoot.setIdleMode(CANSparkMax.IdleMode.kBrake);
        _bottom_encoder = _bottom_shoot.getEncoder();
        _bottom_pid = _bottom_shoot.getPIDController();
        _bottom_pid.setP(0.00014);
        _bottom_pid.setFF(0.00017);

        _top_extender = new Solenoid(Map.SHOOTER_TOP_SOLENOID_PORT);
        _extender = new DoubleSolenoid(Map.SHOOTER_BOTTOM_EXTEND_HP, Map.SHOOTER_BOTTOM_EXTEND_LP);

        Update_Semaphore.getInstance().register(this);
        System.out.println("Ion Cannon charged");
    }

    public static void shoot(ION_CANNON_STATE state) {
        _state = state;
    }

    public boolean enabled()
    {
        return _state != ION_CANNON_STATE.DISABLED;
    }

    public boolean speed_good() 
    {
        if(_state == ION_CANNON_STATE.DISABLED)
            return false;
        // Only shoot when up to speed and the trigger is pulled
        //return IO.ion_shoot() && (Math.abs(Math.abs(_bottom_shoot.getEncoder().getVelocity()) - _setpoints[_state.ordinal()]) < 250.0);
        return (IO.ion_high() || IO.ion_low()) && (Math.abs(Math.abs(_bottom_shoot.getEncoder().getVelocity()) - _setpoints[_state.ordinal()]) < 250.0);
    }

    private void spin_wheels(double speed, double offset)
    {
        if(_top_enabled)
            _top_pid.setReference(-1.0 * (speed + speed_offset), ControlType.kVelocity);
        _bottom_pid.setReference(speed - offset, ControlType.kVelocity);
    }

    private void update_god()
    {
        if (IO.god_ex())
            _extender.set(_extender.get() == DoubleSolenoid.Value.kForward ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward);
        _bottom_shoot.set(IO.god_ion());
    }
    private void update()
    {
        if (enabled())
        {
            if(_extender.get() != DoubleSolenoid.Value.kForward)
            {
                Tractor_Beam._ef_engager.set(DoubleSolenoid.Value.kForward);
                _extender.set(DoubleSolenoid.Value.kForward);
                if(_state == ION_CANNON_STATE.HIGH)
                    _top_extender.set(true);
                else
                    _top_extender.set(false);
                Timer.delay(Map.IC_DEPLOY_DELAY);
            }

            spin_wheels(_setpoints[_state.ordinal()], speed_offset);

        } else {
            _extender.set(DoubleSolenoid.Value.kReverse);
            _top_extender.set(false);
            spin_wheels(0, 0);
        }
        
    }

    private void update_dashboard()
    {
        if(_top_enabled)
            SmartDashboard.putNumber("Shooter Top Speed", _top_encoder.getVelocity());
        SmartDashboard.putNumber("Shooter Bottom Speed", _bottom_encoder.getVelocity());
        SmartDashboard.putString("Spin Diff", (speed_offset + " RPM"));
        SmartDashboard.putString("Shooter State", _state.toString());
        SmartDashboard.putNumber("Shooter Set Speed", enabled() ? _setpoints[_state.ordinal()] : 0);
    }

    public void semaphore_update() // updates robot information
    {
        update_dashboard();

        if (_ds.isDisabled()) // only runs in teleop
            return;
        
        if(_ds.isOperatorControl())
        {
            if(IO.god_state)
            {
                update_god();
                return;
            }

            if(IO.ion_high())
                shoot(ION_CANNON_STATE.HIGH);
            else if(IO.ion_low() || IO.ion_shoot())
                shoot(ION_CANNON_STATE.LOW);
            else
                shoot(ION_CANNON_STATE.DISABLED);
        }

        update();
    }
}