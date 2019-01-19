package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
public class Pickup implements Updatable {
	private WPI_TalonSRX _grab_left;
	private WPI_TalonSRX _grab_right;
	private WPI_TalonSRX _arm;
	public DoubleSolenoid _grab_piston; 
	public DoubleSolenoid _grabber;
	private Lift _lift = Lift.getInstance();
	// Encoder encoder;
	public enum arm_position {UP, DOWN, MIDDLE, OFF}; // declares states of arms
	public double[] arm_angle = {Map.ARM_UP_ANGLE, Map.ARM_DOWN_ANGLE, Map.ARM_UP_ANGLE/2}; // Map.ARM_UP_ANGLE/2 or Map.ARM_MID_ANGLE
	public static arm_position arm_state = arm_position.DOWN; // sets arms to be down at beginning of match
	
	public enum flipper {CLOSE, OPEN}; // declares states of flippers
	public static flipper flipper_state = flipper.OPEN; // sets flippers to be closed at beginning of match
	private static final Pickup instance = new Pickup();
	private DriverStation _ds = DriverStation.getInstance();
	private static DigitalInput bottom_arm = new DigitalInput(6);
	private boolean hold_down = false;

	public void update_grabber_state() {
		if(IO.get_grabber())
			_grab_piston.set(DoubleSolenoid.Value.kForward);
		else
			_grab_piston.set(DoubleSolenoid.Value.kReverse);
			
	}
