package org.usfirst.frc1504.Robot2021;

import edu.wpi.first.wpilibj.I2C;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * 
 *                              ./+sydmmNNNNNmmdys+/.                              
                        `/sdNNNNNNNNNNNNNNNNNNNNNNNds/`                         
                     .+hmhyssyhNNNNNNNNNNNNNNNNNNNNNNNNd+.                      
                   /hmo-        :sNNNNNNNNmdhyhhmNNNNNNNNNh/                    
                `+dNh` .sSdhs     .dNmms:`       `-+dNNNNNmmd+`                 
               /dmmh   -sSdd`      `mo`              :dmmmmmmmd/                
             `ymmmm/     SSs         ::s:+d.           :mmmmmmmmmy.              
            -hddddd+                :hdoyM+           -ddddddddddd-             
``         :dddddddh.              -d++ys/           .ydddddddddddd:            
 /so/-    -hhhhhhhhhy:         `...:::-.....`     `:ohhhhhhhhhhhhhhh-           
  -yddmhs/yyyyyyyyyyyys/.     `-o----------//+++osyhyhyyyyyyyyyyyyhyy`          
   `sdmmNhyyyyyyyyyyyyyyys+:--.`yd:------.     `.--:+osyyyyyyyyyyyyyy+      `-/-
     /dmmssssssssssss+:.`       /MN:----.              `-:+sssssssssss..:+sdNd- 
      .hhoooooooo/-`            .ms `--`                    .:+oooooooshddmm+   
        +ooooo/-                `/`  ``                        `-+ooooohdds`    
        +ooo:`                   `                                ./ooohy-      
        /o:`                                                        .+o:        
        :.                THIS CODE WAS WRITTEN BY                                            .         
                    FRC TEAM 1504: THE DESPARATE PENGUINS                                                      
                                   ENJOY
                                                                                
          .-----..``                                                            
         `-----------.`                                                         
          --------------`                                  `.----.`             
          `---------------.                             `.---------             
           `----------------.                         `.-----------             
             .---------------.`                     `.------------.             
              `.---------------`                   `-------------.              
                `.--------------                  .--------------               
                   `.-----------`                .-------------.                
                      `..------.                .-------------.                 
                           ```                 `------------.`                  
                                               `----------.`                    
                                               `--------.`                      
                                                `....``                         
 * 
 * 
 *This code is a free-to-use Java library for FRC Teams using the MXP Digit Board with their roboRIO.
 *The Digit Board runs in its own thread; it starts automatically.
 *About the button get functions - getButton will return when the button is pressed down, getButtonOnRisingEdge will return true when the button is released, getButtonLatch will toggle between true and false.
 *The getPotentiometer function returns an integer cast as a double.
 */

public class DigitBoard {
	private static class Board_Task implements Runnable {
		private DigitBoard _b;

		Board_Task(DigitBoard b) {
			_b = b;
		}

		public void run() {
			_b.board_task();
		}
	}

	private static DigitBoard instance = new DigitBoard();

	private Thread _task_thread;
	private boolean _run = false;

	/**
	 * Returns the instance of the Digit Board
	 * 
	 * @return DigitBoard
	 */
	public static DigitBoard getInstance() {
		return DigitBoard.instance;
	}

	public static void initialize() {
		getInstance();
	}

	protected DigitBoard() {
		_task_thread = new Thread(new Board_Task(this), "MXP_Display_Board");
		_task_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2);

		DisplayInit();

		start();
		System.out.println("MXP Board Initialization Successful.");
	}

	public void start() {
		if (_run)
			return;
		_run = true;
		_task_thread = new Thread(new Board_Task(this));
		_task_thread.start();
	}

	public void stop() {
		_run = false;
	}

	private I2C _display_board;
	private DigitalInput _a;
	private DigitalInput _b;

	byte[] _output_buffer;

	private static final int A_MASK = 0b0000000000000001;
	private static final int B_MASK = 0b0000000000000010;
	private volatile int _input_mask, _input_mask_rising, _input_mask_rising_last;

	private AnalogInput _potentiometer;

	private void DisplayInit() {
		_display_board = new I2C(I2C.Port.kMXP, 0x70);

		_output_buffer = new byte[10];

		_a = new DigitalInput(19);
		_b = new DigitalInput(20);
		_potentiometer = new AnalogInput(7);

	}

	/**
	 * Function for updating buttons, and its helper functions. getA and getB will
	 * simply return if the button is pushed (the button being pushed returns false)
	 * getALatch and getBLatch will remain true until the data is read.
	 * getAonRisingEdge and getBOnRisingEdgeis true only when the value has gone
	 * from false to true (it will only be true when the button is released.
	 */
	public void update() {
		int current_mask = get_input_mask();

		_input_mask |= current_mask;

		_input_mask_rising |= (~_input_mask_rising_last & current_mask);
		_input_mask_rising_last = current_mask;
	}

	private int get_input_mask() {
		int mask = 0;
		mask |= (getA() ? 1 : 0) << A_MASK;
		mask |= (getB() ? 1 : 0) << B_MASK;
		return mask;
	}

	private boolean getRawButtonOnRisingEdge(int button_mask) {
		button_mask = button_mask << 1;
		// Compute a clearing mask for the button.
		int clear_mask = 0b1111111111111111 - button_mask;
		// Get the value of the button - 1 or 0
		boolean value = (_input_mask_rising & button_mask) != 0;
		// Mask this and only this button back to 0
		_input_mask_rising &= clear_mask;
		return value;
	}

	private boolean getRawButtonLatch(int button_mask) {
		// Compute a clearing mask for the button.
		int clear_mask = 0b1111111111111111 - button_mask;
		// Get the value of the button - 1 or 0
		boolean value = (_input_mask & button_mask) != 0;
		// Mask this and only this button back to 0
		_input_mask &= clear_mask;
		return value;
	}

	public boolean getA() {
		return (!_a.get());
	}

	public boolean getALatch() {
		return getRawButtonLatch(A_MASK);
	}

	public boolean getAOnRisingEdge() {
		return getRawButtonOnRisingEdge(A_MASK);
	}

	public boolean getB() {
		return (!_b.get());
	}

	public boolean getBLatch() {
		return getRawButtonLatch(B_MASK);
	}

	public boolean getBOnRisingEdge() {
		return getRawButtonOnRisingEdge(B_MASK);
	}

	/**
	 * Returns the current value of the potentiometer, scaled to be between 0 and 1.
	 * 
	 * @return an integer corresponding to the position of the dial
	 */
	public double getPotentiometer() {
		double val = (double) _potentiometer.getAverageValue();// integer between 4 and 4042 (furthest CCW is 400,
																// furthest CW is 3)
		val = Math.min((val / 4000), 1.0); // number between 0 and 10
		val = 1.0 - val;
		// val = (Math.round(val * 10.0)) / 10.0;
		return val;
	}

	public void writeDigits(String output) {
		output += "    "; // Cheap and easy way to clear and prevent index out of bounds errors

		_output_buffer[0] = (byte) (0b0000111100001111);

		int offset = 0;

		for (int i = 0; i < 4; i++) {
			char letter = output.charAt(i + offset);

			while (/* letter < 32 || */ letter == '.') {
				if (letter == '.') {
					if (i != 0)
						_output_buffer[(4 - i) * 2 + 3] |= (byte) 0b01000000;
				}

				offset++;
				letter = output.charAt(i + offset);
			}
			_output_buffer[(3 - i) * 2 + 2] = CHARS[letter - 32][0];
			_output_buffer[(3 - i) * 2 + 3] = CHARS[letter - 32][1];
		}

	}

	public void writeRaw(byte[][] buffer) {
		byte[] temp_buffer = new byte[10];
		temp_buffer[0] = (byte) (0b0000111100001111);
		for (int i = 0; i < buffer.length; i++) {
			//
			temp_buffer[(3 - i) * 2 + 2] = buffer[i][0];
			temp_buffer[(3 - i) * 2 + 3] = buffer[i][1];
		}
		_output_buffer = temp_buffer;
	}

	/**
	 * The controller function, doing logic to decide what to display based on the
	 * inputs it is getting. This is the function being used by the thread.
	 */
	private void board_task() {
		byte[] osc = new byte[1];
		byte[] blink = new byte[1];
		byte[] bright = new byte[1];
		osc[0] = (byte) 0x21;
		blink[0] = (byte) 0x81;
		bright[0] = (byte) 0xEF;

		_display_board.writeBulk(osc);
		_display_board.writeBulk(bright);
		_display_board.writeBulk(blink);

		while (_run) {
			update();
			_display_board.writeBulk(_output_buffer);

		}
	}

	// Thanks @Team 1493
	private static final byte[][] CHARS = { { (byte) 0b00000000, (byte) 0b00000000 }, //
			{ (byte) 0b00000110, (byte) 0b00000000 }, // !
			{ (byte) 0b00100000, (byte) 0b00000010 }, // "
			{ (byte) 0b11001110, (byte) 0b00010010 }, // #
			{ (byte) 0b11101101, (byte) 0b00010010 }, // $
			{ (byte) 0b00100100, (byte) 0b00100100 }, // %
			{ (byte) 0b01011101, (byte) 0b00001011 }, // &
			{ (byte) 0b00000000, (byte) 0b00000100 }, // '
			{ (byte) 0b00000000, (byte) 0b00001100 }, // (
			{ (byte) 0b00000000, (byte) 0b00100001 }, // )
			{ (byte) 0b11000000, (byte) 0b00111111 }, // *
			{ (byte) 0b11000000, (byte) 0b00010010 }, // +
			{ (byte) 0b00000000, (byte) 0b00100000 }, // ,
			{ (byte) 0b11000000, (byte) 0b00000000 }, // -
			{ (byte) 0b00000000, (byte) 0b00000000 }, // .
			{ (byte) 0b00000000, (byte) 0b00100100 }, // /
			{ (byte) 0b00111111, (byte) 0b00100100 }, // 0
			{ (byte) 0b00000110, (byte) 0b00000000 }, // 1
			{ (byte) 0b11011011, (byte) 0b00000000 }, // 2
			{ (byte) 0b10001111, (byte) 0b00000000 }, // 3
			{ (byte) 0b11100110, (byte) 0b00000000 }, // 4
			{ (byte) 0b01101001, (byte) 0b00001000 }, // 5
			{ (byte) 0b11111101, (byte) 0b00000000 }, // 6
			{ (byte) 0b00000111, (byte) 0b00000000 }, // 7
			{ (byte) 0b11111111, (byte) 0b00000000 }, // 8
			{ (byte) 0b11101111, (byte) 0b00000000 }, // 9
			{ (byte) 0b00000000, (byte) 0b00010010 }, // :
			{ (byte) 0b00000000, (byte) 0b00100010 }, // ;
			{ (byte) 0b00000000, (byte) 0b00001100 }, // <
			{ (byte) 0b11001000, (byte) 0b00000000 }, // =
			{ (byte) 0b00000000, (byte) 0b00100001 }, // >
			{ (byte) 0b10000011, (byte) 0b00010000 }, // ?
			{ (byte) 0b10111011, (byte) 0b00000010 }, // @
			{ (byte) 0b11110111, (byte) 0b00000000 }, // A
			{ (byte) 0b10001111, (byte) 0b00010010 }, // B
			{ (byte) 0b00111001, (byte) 0b00000000 }, // C
			{ (byte) 0b00001111, (byte) 0b00010010 }, // D
			{ (byte) 0b11111001, (byte) 0b00000000 }, // E
			{ (byte) 0b01110001, (byte) 0b00000000 }, // F
			{ (byte) 0b10111101, (byte) 0b00000000 }, // G
			{ (byte) 0b11110110, (byte) 0b00000000 }, // H
			{ (byte) 0b00000000, (byte) 0b00010010 }, // I
			{ (byte) 0b00011110, (byte) 0b00000000 }, // J
			{ (byte) 0b01110000, (byte) 0b00001100 }, // K
			{ (byte) 0b00111000, (byte) 0b00000000 }, // L
			{ (byte) 0b00110110, (byte) 0b00000101 }, // M
			{ (byte) 0b00110110, (byte) 0b00001001 }, // N
			{ (byte) 0b00111111, (byte) 0b00000000 }, // O
			{ (byte) 0b11110011, (byte) 0b00000000 }, // P
			{ (byte) 0b00111111, (byte) 0b00001000 }, // Q
			{ (byte) 0b11110011, (byte) 0b00001000 }, // R
			{ (byte) 0b11101101, (byte) 0b00000000 }, // S
			{ (byte) 0b00000001, (byte) 0b00010010 }, // T
			{ (byte) 0b00111110, (byte) 0b00000000 }, // U
			{ (byte) 0b00110000, (byte) 0b00100100 }, // V
			{ (byte) 0b00110110, (byte) 0b00101000 }, // W
			{ (byte) 0b00000000, (byte) 0b00101101 }, // X
			{ (byte) 0b00000000, (byte) 0b00010101 }, // Y
			{ (byte) 0b00001001, (byte) 0b00100100 }, // Z
			{ (byte) 0b00111001, (byte) 0b00000000 }, // [
			{ (byte) 0b00000000, (byte) 0b00001001 }, // \
			{ (byte) 0b00001111, (byte) 0b00000000 }, // ]
			{ (byte) 0b00000011, (byte) 0b00100100 }, // ^
			{ (byte) 0b00001000, (byte) 0b00000000 }, // _
			{ (byte) 0b00000000, (byte) 0b00000001 }, // `
			{ (byte) 0b01011000, (byte) 0b00010000 }, // a
			{ (byte) 0b01111000, (byte) 0b00001000 }, // b
			{ (byte) 0b11011000, (byte) 0b00000000 }, // c
			{ (byte) 0b10001110, (byte) 0b00100000 }, // d
			{ (byte) 0b01011000, (byte) 0b00100000 }, // e
			{ (byte) 0b01110001, (byte) 0b00000000 }, // f
			{ (byte) 0b10001110, (byte) 0b00000100 }, // g
			{ (byte) 0b01110000, (byte) 0b00010000 }, // h
			{ (byte) 0b00000000, (byte) 0b00010000 }, // i
			{ (byte) 0b00001110, (byte) 0b00000000 }, // j
			{ (byte) 0b00000000, (byte) 0b00011110 }, // k
			{ (byte) 0b00110000, (byte) 0b00000000 }, // l
			{ (byte) 0b11010100, (byte) 0b00010000 }, // m
			{ (byte) 0b01010000, (byte) 0b00010000 }, // n
			{ (byte) 0b11011100, (byte) 0b00000000 }, // o
			{ (byte) 0b01110000, (byte) 0b00000001 }, // p
			{ (byte) 0b10000110, (byte) 0b00000100 }, // q
			{ (byte) 0b01010000, (byte) 0b00000000 }, // r
			{ (byte) 0b10001000, (byte) 0b00001000 }, // s
			{ (byte) 0b01111000, (byte) 0b00000000 }, // t
			{ (byte) 0b00011100, (byte) 0b00000000 }, // u
			{ (byte) 0b00000100, (byte) 0b00001000 }, // v
			{ (byte) 0b00010100, (byte) 0b00101000 }, // w
			{ (byte) 0b11000000, (byte) 0b00101000 }, // x
			{ (byte) 0b00001100, (byte) 0b00001000 }, // y
			{ (byte) 0b01001000, (byte) 0b00100000 }, // z
			{ (byte) 0b01001001, (byte) 0b00100001 }, // {
			{ (byte) 0b00000000, (byte) 0b00010010 }, // |
			{ (byte) 0b10001001, (byte) 0b00001100 }, // }
			{ (byte) 0b00100000, (byte) 0b00000101 }, // ~
			{ (byte) 0b11111111, (byte) 0b00111111 } // DEL
	};
}