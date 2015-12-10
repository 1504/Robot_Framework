package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.Joystick;

public class Joystick_Helper extends Joystick implements Updatable
{
	private final int _num_buttons;
	private volatile int _button_mask;
	
	public Joystick_Helper(final int port)
	{
		super(port);
		
		_num_buttons = getButtonCount();
		_button_mask = get_button_mask();
		
		Update_Semaphore.getInstance().register(this);
	}
	
	/**
	 * Get buttons, but will latch any button press on until the button is read.
     * The appropriate button is returned as a boolean value.
     * 
	 * @param button The button index, beginning at 1.
	 * @return State of the button
	 */
	public boolean getRawButtonLatch(final int button) {
		int clear_mask = ((1 << _num_buttons) - 1) - (1 << (button - 1));
		boolean value = (_button_mask & (1 << (button - 1))) != 0;
		_button_mask &= clear_mask;
		return value;
    }
	
	/**
	 * Builds the button mask integer bitwise
	 * @return Bitwise mask of pressed buttons on the joystick
	 */
	private int get_button_mask()
	{
		int mask = 0;
		for(int button = 0; button < _num_buttons; button++)
			mask &= (getRawButton(button + 1) ? 1: 0) << (button - 1);
		return mask;
	}

	public void semaphore_update()
	{
		_button_mask |= get_button_mask();
	}
}
