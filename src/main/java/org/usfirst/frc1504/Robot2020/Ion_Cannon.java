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
    public static CANEncoder _top_encoder;
    public static CANEncoder _bottom_encoder;

    private static CANPIDController _top_pid;
    private static CANPIDController _bottom_pid;

    public static DoubleSolenoid _top_extender;
    public static DoubleSolenoid _extender;

    private boolean _ion_cannon_top_active = false;
    private boolean _ion_cannon_bottom_active = false;
    private static boolean ion_timer_set = false;
    private static double ion_timer;
    private static boolean _high_state = false;
    private static boolean _low_state = false;



    public static double speedo = 0;
    public static double speed_offset = 0;
    private static boolean extender_state = false;

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

    private Ion_Cannon() {
        
        //_top_shoot = new CANSparkMax(Map.ION_CANNON_TOP, MotorType.kBrushless);
        _bottom_shoot = new CANSparkMax(Map.ION_CANNON_BOTTOM, MotorType.kBrushless);

        //_top_shoot.setIdleMode(CANSparkMax.IdleMode.kBrake);
        _bottom_shoot.setIdleMode(CANSparkMax.IdleMode.kBrake);

        //_top_encoder = _top_shoot.getEncoder();
        _bottom_encoder = _bottom_shoot.getEncoder();

        //_top_pid = _top_shoot.getPIDController();
        _bottom_pid = _bottom_shoot.getPIDController();

        //_top_extender = new DoubleSolenoid(Map.TOP_EXTEND_HP, Map.TOP_EXTEND_LP);
        _extender = new DoubleSolenoid(Map.TOP_EXTEND_HP, Map.TOP_EXTEND_LP);

        //_top_pid.setP(0.00014);
        //_top_pid.setFF(0.00017);

        _bottom_pid.setP(0.00014);
        _bottom_pid.setFF(0.00017);
        
        Update_Semaphore.getInstance().register(this);
        System.out.println("Ion Cannon charged");
    }

    public static void spin_wheels(double speed, double offset) {
        _top_pid.setReference(-1.0 * (speedo + speed_offset), ControlType.kVelocity);
        _bottom_pid.setReference(speedo - speed_offset, ControlType.kVelocity);
    }

    private void flip_out_bottom_wheels() {
        _extender.set(DoubleSolenoid.Value.kForward);
        if (!ion_timer_set) {
            ion_timer = System.currentTimeMillis();
            ion_timer_set = true;
        }
    }

    private boolean speed_good() {
        return _bottom_shoot.getEncoder().getVelocity() < Map.ION_SPEED + 100 && _bottom_shoot.getEncoder().getVelocity() > Map.ION_SPEED - 100;
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

        if (IO.god_state)
        {
            if (IO.god_ex()) {
                extender_state = !extender_state;
            }
            
            _bottom_shoot.set(IO.god_ion());
            if(extender_state) {
                _extender.set(DoubleSolenoid.Value.kForward);
            } else {
                _extender.set(DoubleSolenoid.Value.kReverse);
            }
        } else {
            if (IO.ion_high()) {
                _high_state = !_high_state;
            }
            if (IO.ion_low()) {
                _low_state = !_low_state;
            }

            if (_high_state || _low_state) {
                Tractor_Beam._ef_engager.set(DoubleSolenoid.Value.kForward);
                _extender.set(DoubleSolenoid.Value.kForward);
                //shooter top solenoid to position should go here
                spin_wheels(Map.ION_SPEED, speed_offset);
                if (speed_good())
                {
                    Tokamak.serializer.set(-Map.SERIALIZER_SPEED);
                    if (Tokamak.current_check(Tokamak.snake)) {
                        Tokamak.snake.set(-Map.TOKAMAK_SPEED);
                    }
                }
            }
        }

        //SmartDashboard.putString("Spew Top Speed", (_top_encoder.getVelocity() + "RPM"));
        //SmartDashboard.putString("Spew Bottom Speed", (_bottom_encoder.getVelocity() + "RPM"));
        //SmartDashboard.putString("Spin Diff", (speed_offset + " RPM"));
        //SmartDashboard.putNumber("Setpoint Number", setpoint_val);
        //SmartDashboard.putString("Speed", (speedo + " RPM"));

        //System.out.println("Bottom Speed: " + _bottom_encoder.getVelocity());
        //System.out.println("Top Speed: " + _top_encoder.getVelocity());
        //System.out.println(speed_offset);
    }

    public void semaphore_update() // updates robot information
    {
        if (_ds.isDisabled()) // only runs in teleop
            return;

        update();
    }
}