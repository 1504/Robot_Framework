package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Ion_Cannon implements Updatable {
    private static final Ion_Cannon instance = new Ion_Cannon();
    private DriverStation _ds = DriverStation.getInstance();

    private CANSparkMax _top_shoot;
    private CANSparkMax _bottom_shoot;
    private CANEncoder _top_encoder;
    private CANEncoder _bottom_encoder;

    private CANPIDController _top_pid;
    private CANPIDController _bottom_pid;

    private DoubleSolenoid _top_extender;
    private DoubleSolenoid _bottom_extender;

    private boolean _ion_cannon_top_active = false;
    private boolean _ion_cannon_bottom_active = false;
    private boolean ion_timer_set = false;
    private double ion_timer;

    private static double speedo = 0.32;
    private static double speed_offset = 0;
    // private double cannon_spin = 0;

    private int i = 0;
    private static final double[] _setpoints = { 0, 0.33, 0.5, 0.67, 1 };
    private double setpoint_val;

    public static Ion_Cannon getInstance() // sets instance
    {
        return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
    }

    private Ion_Cannon() 
    {
        _top_shoot = new CANSparkMax(Map.ION_CANNON_TOP, MotorType.kBrushless);
        _bottom_shoot = new CANSparkMax(Map.ION_CANNON_BOTTOM, MotorType.kBrushless);

        _top_shoot.setIdleMode(CANSparkMax.IdleMode.kBrake);
        _bottom_shoot.setIdleMode(CANSparkMax.IdleMode.kBrake);

        _top_encoder = _top_shoot.getEncoder();
        _bottom_encoder = _bottom_shoot.getEncoder();

        _top_pid = _top_shoot.getPIDController();
        _bottom_pid = _bottom_shoot.getPIDController();

        _top_extender = new DoubleSolenoid(Map.TOP_EXTEND_HP, Map.TOP_EXTEND_LP);
        _bottom_extender = new DoubleSolenoid(Map.BOT_EXTEND_HP, Map.BOT_EXTEND_LP);

        _top_pid.setP(0.00014);
        _top_pid.setFF(0.00017);

        _bottom_pid.setP(0.00014);
        _bottom_pid.setFF(0.00017);

        Update_Semaphore.getInstance().register(this);
        System.out.println("Ion Cannon charged");
    }

    private boolean bottom_activated() 
    {
        return (IO.bottom_ion_shoot() ? !_ion_cannon_bottom_active : _ion_cannon_bottom_active);
    }

    private boolean top_activated() 
    {
        return (IO.top_ion_shoot() ? !_ion_cannon_top_active : _ion_cannon_top_active);
    }

    private void spin_wheels() 
    {
        _top_pid.setReference(-1.0 * (speedo + speed_offset), ControlType.kVelocity);
        _bottom_pid.setReference(speedo - speed_offset, ControlType.kVelocity);
    }

    private void flip_out_bottom_wheels() 
    {
        _bottom_extender.set(DoubleSolenoid.Value.kForward);
        if (!ion_timer_set) {
            ion_timer = System.currentTimeMillis();
            ion_timer_set = true;
        }
    }

    private void update() 
    {
        if (IO.hid_N()) {
            speed_offset += 10;
        } else if (IO.hid_S()) 
        {
            speed_offset -= 10;
        }

        if (IO.hid_E()) {
            speedo += 10;
        } else if (IO.hid_W()) 
        {
            speedo -= 10;
        }

        // speedo = IO.manual_ion_speed() * 6000;
        // cannon_spin = (_bottom_encoder.getVelocity() - _top_encoder.getVelocity() +
        // speed_offset) * Map.ION_CORRECTIONAL_GAIN;

        if (IO.cycle_ion_setpoint()) 
        {
            i += 1;
            setpoint_val = _setpoints[i % _setpoints.length];
        }

        if (bottom_activated()) 
        {
            flip_out_bottom_wheels();
            _top_extender.set(DoubleSolenoid.Value.kReverse);
            if (System.currentTimeMillis() - ion_timer > Map.IC_DEPLOY_DELAY){
                spin_wheels();
            }
        } else if (top_activated()) 
        {
            flip_out_bottom_wheels();
            _top_extender.set(DoubleSolenoid.Value.kForward);
            if (System.currentTimeMillis() - ion_timer > Map.IC_DEPLOY_DELAY){
                spin_wheels();
            }
        } else 
        {
            _top_pid.setReference(0, ControlType.kVelocity);
            _bottom_pid.setReference(0, ControlType.kVelocity);

            _top_extender.set(DoubleSolenoid.Value.kReverse);
            _bottom_extender.set(DoubleSolenoid.Value.kReverse);

            ion_timer_set = false;
        }

        SmartDashboard.putString("Spew Top Speed", (_top_encoder.getVelocity() + "RPM"));
        SmartDashboard.putString("Spew Bottom Speed", (_bottom_encoder.getVelocity() + "RPM"));
        SmartDashboard.putString("Spin Diff", (speed_offset + " RPM"));
        SmartDashboard.putNumber("Setpoint Number", setpoint_val);
        SmartDashboard.putString("Speed", (speedo + " RPM"));

        System.out.println("Bottom Speed: " + _bottom_encoder.getVelocity());
        System.out.println("Top Speed: " + _top_encoder.getVelocity());
        System.out.println(speed_offset);
    }

    public void semaphore_update() // updates robot information
    {
        if (_ds.isDisabled()) // only runs in teleop
            return;

        update();
    }
}