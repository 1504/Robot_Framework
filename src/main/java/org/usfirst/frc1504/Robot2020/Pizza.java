package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.I2C;
//import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.Solenoid;

import com.revrobotics.ColorSensorV3;
//import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ColorMatchResult;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
//import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;

public class Pizza implements Updatable {
    private static final Pizza instance = new Pizza();
    private DriverStation _ds = DriverStation.getInstance();

    private final I2C.Port i2cPort = I2C.Port.kOnboard;

    private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);
    private final ColorMatch m_colorMatcher = new ColorMatch();

    private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
    private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
    private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
    private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);
    private static double speedo = 0.33;
    private WPI_TalonSRX _pizza_slicer;
    private static ArrayList<Character> pizza_history = new ArrayList<Character>();
    private char initial_color;
    private int colorCount = 0;
    String gameData = DriverStation.getInstance().getGameSpecificMessage();
    private Solenoid _scomp_link;

    public static Pizza getInstance() // sets instance
    {
        return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
    }

    private Pizza() {
        m_colorMatcher.addColorMatch(kBlueTarget);
        m_colorMatcher.addColorMatch(kGreenTarget);
        m_colorMatcher.addColorMatch(kRedTarget);
        m_colorMatcher.addColorMatch(kYellowTarget);

        _scomp_link = new Solenoid(Map.SCOMP_LINK_PORT);
        _pizza_slicer = new WPI_TalonSRX(Map.PIZZA_SLICER);

        _scomp_link.set(false);

        Update_Semaphore.getInstance().register(this);
        System.out.println("Pizza is ready to be cut");

    }

    private void spin_pizza(char colorChar)
    {
        if (pizza_history.size() == 0)
        {
            pizza_history.add(colorChar);
            initial_color = colorChar;
        }
        if (pizza_history.get(pizza_history.size() - 1) != colorChar)
        {
            pizza_history.add(colorChar);
        }
        for (int i = 0; i < pizza_history.size() - 1; i++)
        {
            if (pizza_history.get(i) == initial_color)
            {
                colorCount++;
            }
        }
        if (colorCount < 3)
            _pizza_slicer.set(0.5);
        else
            _pizza_slicer.set(0);

    }

    private void cut_pizza(char color)
    {
        if (gameData.charAt(0) != color)
            _pizza_slicer.set(0.5);
        else
            _pizza_slicer.set(0);
    }

    private void compute_color()
    {
        /*Color detectedColor = m_colorSensor.getColor();
        
        char colorChar;
        ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);
        
        (match.color == kBlueTarget) { colorChar = 'R'; } else if (match.color ==
        kRedTarget) { colorChar = 'B'; } else if (match.color == kGreenTarget) {
        colorChar = 'Y'; } else if (match.color == kYellowTarget) { colorChar = 'G';
        } else { colorChar = 'U'; }*/

                /*
         * Color detectedColor = m_colorSensor.getColor();
         * 
         * char colorChar; ColorMatchResult match =
         * m_colorMatcher.matchClosestColor(detectedColor);
         * 
         * // color is switched because color sensor is moved on the field if
         * (match.color == kBlueTarget) { colorChar = 'R'; } else if (match.color ==
         * kRedTarget) { colorChar = 'B'; } else if (match.color == kGreenTarget) {
         * colorChar = 'Y'; } else if (match.color == kYellowTarget) { colorChar = 'G';
         * } else { colorChar = 'U'; }
         * 
         * SmartDashboard.putNumber("Red", detectedColor.red);
         * SmartDashboard.putNumber("Green", detectedColor.green);
         * SmartDashboard.putNumber("Blue", detectedColor.blue);
         * SmartDashboard.putNumber("Confidence", match.confidence);
         * SmartDashboard.putString("Detected Color", colorChar + " ");
         * 
         * System.out.println(colorChar);
         * 
         * if(IO.get_god_button()) { _pizza_slicer.set(speedo); } else {
         * _pizza_slicer.set(0); }
         * 
         * if(IO.get_rotation_control_button()) { spin_pizza(colorChar); }
         * 
         * if(IO.get_pizza_cutter_button()) { cut_pizza(colorChar); }
         * 
         * if(IO.get_pizza_cutter_button() || IO.get_rotation_control_button()) {
         * scomp_link_state = !scomp_link_state;
         * 
         * }
         * 
         * if(scomp_link_state) { _scomp_link.set(DoubleSolenoid.Value.kForward); } else
         * { _scomp_link.set(DoubleSolenoid.Value.kReverse); }
         */
    }

    private void update()
    {
        if(IO.god_state)
        {
            return;
        }

        if (IO.pizza_extend()) {
            _scomp_link.set(!_scomp_link.get());
        }
        if (_scomp_link.get()) {
            _pizza_slicer.set(IO.pizza_spin());
        } else {
            _pizza_slicer.set(0);
        }
    }

    private void update_dashboard()
    {
        SmartDashboard.putBoolean("ControlPanel Extended", _scomp_link.get());
    }

    public void semaphore_update() // updates robot information
    {
        update_dashboard();

        if (_ds.isDisabled()) // only runs in teleop
            return;
        else if(IO.safe_state)
            return;
        else
            update();
    }
}