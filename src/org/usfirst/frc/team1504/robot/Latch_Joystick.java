package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.Joystick;

public class Latch_Joystick extends Joystick implements Updatable
{
	private final int _num_buttons;
	private volatile int _button_mask, _button_mask_rising;
	private volatile int _button_mask_rising_last = 0;
	
	public Latch_Joystick(final int port)
	{
		super(port);
		
		_num_buttons = getButtonCount();
		_button_mask  = _button_mask_rising = get_button_mask();
		
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
		// Compute a clearing mask for the button. ex: 11111 - 00100 = 11011, will turn off button 3
		int clear_mask = ((1 << _num_buttons) - 1) - (1 << (button - 1));
		// Get the value of the button - 1 or 0
		boolean value = (_button_mask & (1 << (button - 1))) != 0;
		// Mask this and only this button back to 0
		_button_mask &= clear_mask;
		return value;
    }
	
	/**
	 * Get buttons, but will latch any button press on until the button is read. 
     * The appropriate button is returned as a boolean value.
     * 
	 * @param button The button index, beginning at 1.
	 * @return State of the button
	 */
	public boolean getRawButtonOnRisingEdge(final int button) {
		// Compute a clearing mask for the button. ex: 11111 - 00100 = 11011, will turn off button 3
		int clear_mask = ((1 << _num_buttons) - 1) - (1 << (button - 1));
		// Get the value of the button - 1 or 0
		boolean value = (_button_mask_rising & (1 << (button - 1))) != 0;
		// Mask this and only this button back to 0
		_button_mask_rising &= clear_mask;
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
			mask |= (getRawButton(button + 1) ? 1: 0) << (button);
		return mask;
	}
	
	/**
	 * Synchronized with new data from the Driver Station, will update the button mask.
	 * Ensures that a button event will not be missed.
	 */
	public void semaphore_update()
	{
		int current_mask = get_button_mask();

		_button_mask |= current_mask;

		_button_mask_rising |= (~_button_mask_rising_last & current_mask);
		_button_mask_rising_last = current_mask;
	}
}
