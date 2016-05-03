package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class Digit_Board
{
	private static class DigitTask implements Runnable
	{
        private Digit_Board _d;

        DigitTask(Digit_Board d)
        {
            _d = d;
        }

        public void run()
        {
            _d.standalone_task();
        }
    }
	
	private static final Digit_Board instance = new Digit_Board();
	
	public static enum DIGIT_BLINK_RATE {OFF, TWO_HZ, ONE_HZ, HALF_HZ}
	
	private static final int A_BUTTON_MASK = 0b0000000000000001;//0b0001000000000000;
	private static final int B_BUTTON_MASK = 0b0000000000000010;//0b0010000000000000;
	private static final int POTENTIOMETER_MASK = 0b0000111111111000;
	
	private static final long INPUT_TIMEOUT = 2500;
	
	private static final String DEFENSES[] = {"LowB", "Ptcs", "Chiv", "Moat", "Ramp", "Draw", "SlPt", "Rock", "Ruff"};
	private static final String POSITIONS[] = {"P  1", "P  2", "P  3", "P  4", "P  5", "Spy "};
	private static enum ACTION {VOLTAGE, DEFENSE, POSITION, AUTO_DELAY}
	
	private static final byte SECRET_CODE = 42;
	private byte _secret_code_input = ~SECRET_CODE;
	
	private int _current_defense = 0;
	private int _current_position = 0;
	private double _current_delay = 0.0;
	
	private boolean _standalone = false;
	private Thread _task_thread;
	
	private DriverStation _ds = DriverStation.getInstance();
	
	private I2C _digit;
	
	private DigitalInput _a_button;
	private DigitalInput _b_button;
	private AnalogInput _potentiometer;
	
	private volatile int _input_mask, _input_mask_rising, _input_mask_rising_last;
	
	private Digit_Board()
	{
		// Initialize the I2C interface to the board
		_digit = new I2C(Port.kMXP, 0x70);
		
		// Initialize the buttons and potentiometer
		_a_button = new DigitalInput(19);
		_b_button = new DigitalInput(20);
		_potentiometer = new AnalogInput(3);
		
		
		start();
		
		System.out.println("Digit Board Initialized");
	}
	
	public void start()
	{
		/*if(_task_thread != null)
			return;*/
		
		_standalone = true;
		_task_thread = new Thread(new DigitTask(this));
		_task_thread.start();
	}
	
	public void stop()
	{
		_standalone = false;
	}
	
	private void standalone_task() {
		setStandbyOff(true);
		setDisplay(DIGIT_BLINK_RATE.OFF, true);
		setBrightness(16);
		
		ACTION action = ACTION.VOLTAGE;
		long refresh_time = 0; //System.currentTimeMillis();
		
		while(_standalone)
		{
			update();
			
			OMGSECRETS(action);
			
			if(System.currentTimeMillis() - refresh_time > INPUT_TIMEOUT)
				action = ACTION.VOLTAGE;
			
			boolean update_refresh = true;
			int potval = getPotentiometerChange();
			
			if(potval != 0)
			{
				action = ACTION.AUTO_DELAY;
				_current_delay = potval / 400.0;
			}
			else if(getAOnRisingEdge())
			{
				if(action == ACTION.POSITION)
					_current_position = (_current_position + 1) % POSITIONS.length;
				action = ACTION.POSITION;
			}
			else if(getBOnRisingEdge())
			{
				if(action == ACTION.DEFENSE)
					_current_defense = (_current_defense + 1) % DEFENSES.length;
				action = ACTION.DEFENSE;
			}
			else
				update_refresh = false;
			
			
			if(_current_position == 0)
				_current_defense = 0;
			
			if(update_refresh)
				refresh_time = System.currentTimeMillis();
			
			switch(action)
			{
				case AUTO_DELAY:
					String delay = Double.toString(_current_delay);
					int point = delay.indexOf(".");
					writeDigits("A" + (point < 2 ? " " : "") + delay.substring(0, point + 2));
					break;
				case POSITION:
					writeDigits(POSITIONS[_current_position]);
					break;
				case DEFENSE:
					writeDigits(DEFENSES[_current_defense]);
					break;
				case VOLTAGE:
				default:
					String volts = Double.toString(_ds.getBatteryVoltage());
					volts = volts.substring(0, volts.indexOf(".") + 2);
					if(volts.length() < 4)
						volts += " ";
					writeDigits(volts + "V");
					break;
			}
			
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		setDisplay(DIGIT_BLINK_RATE.OFF, false);
		setStandbyOff(false);
	}

	public static Digit_Board getInstance()
	{
		return instance;
	}
	
	public int getPosition()
	{
		return _current_position;
	}
	
	public String getDefense()
	{
		return DEFENSES[_current_defense];
	}
	
	public double getDelay()
	{
		return 0.0;
	}
	
	public void update()
	{
		int current_mask = get_input_mask();

		_input_mask |= current_mask;

		_input_mask_rising |= (~_input_mask_rising_last & current_mask);
		_input_mask_rising_last = current_mask;
	}
	
	private int get_input_mask()
	{
		int mask = 0;
		//mask = getPotentiometer() & POTENTIOMETER_MASK; // Disregard noisy low bits
		mask |= (getA() ? 1 : 0) << A_BUTTON_MASK;
		mask |= (getB() ? 1 : 0) << B_BUTTON_MASK;
		return mask;
	}
	
	private boolean getRawButtonLatch(int button_mask)
	{
		// Compute a clearing mask for the button.
		int clear_mask = 0b1111111111111111 - button_mask;
		// Get the value of the button - 1 or 0
		boolean value = (_input_mask & button_mask) != 0;
		// Mask this and only this button back to 0
		_input_mask &= clear_mask;
		return value;
	}
	
	private boolean getRawButtonOnRisingEdge(int button_mask)
	{
		button_mask = button_mask << 1;
		// Compute a clearing mask for the button.
		int clear_mask = 0b1111111111111111 - button_mask;
		// Get the value of the button - 1 or 0
		boolean value = (_input_mask_rising & button_mask) != 0;
		// Mask this and only this button back to 0
		_input_mask_rising &= clear_mask;
		return value;
	}
	
	public boolean getA()
	{
		return(!_a_button.get());
	}
	
	public boolean getALatch()
	{
		return getRawButtonLatch(A_BUTTON_MASK);
	}
	
	public boolean getAOnRisingEdge()
	{
		return getRawButtonOnRisingEdge(A_BUTTON_MASK);
	}
	
	public boolean getB()
	{
		return(!_b_button.get());
	}
	
	public boolean getBLatch()
	{
		return getRawButtonLatch(B_BUTTON_MASK);
	}
	
	public boolean getBOnRisingEdge()
	{
		return getRawButtonOnRisingEdge(B_BUTTON_MASK);
	}
	
	public int getPotentiometer()
	{
		return(_potentiometer.getAverageValue());
	}
	
	public int getPotentiometerChange()
	{
		//TODO: Check this code.
		// This should use the latch code to ignore potentiometer changes under the last few LSB
		// Compute a clearing mask for the Potentiometer
		int clear_mask = 0b1111111111111111 - POTENTIOMETER_MASK;
		// Get the value of the button - 1 or 0
		int value = (_input_mask_rising & POTENTIOMETER_MASK);
		// Mask this and only this button back to 0
		_input_mask_rising &= clear_mask;
		return value;
	}
	
	public void setStandbyOff(boolean enable)
	{
		byte[] write_value = new byte[1];
		write_value[0] = (byte) (0b00100000 + (enable ? 1 : 0));
		_digit.writeBulk(write_value);
	}
	
	public void setBrightness(int brightness)
	{
		if(brightness < 1 || brightness > 16)
			return;
		brightness--;
		
		byte[] write_value = new byte[1];
		write_value[0] = (byte) (0b11100000 + brightness);
		_digit.writeBulk(write_value);
	}
	
	public void setDisplay(DIGIT_BLINK_RATE rate, boolean enable)
	{
		byte[] write_value = new byte[1];
		write_value[0] = (byte) (0b10000000 + (rate.ordinal()<<1) + (enable ? 1 : 0));
		_digit.writeBulk(write_value);
	}
	
	public void writeDigits(String output)
	{
		//if(output.length() != 4)
		//	return;
		output += "    "; // Cheap and easy way to clear and prevent index out of bounds errors
		
		byte[] output_buffer = new byte[10];
		output_buffer[0] = (byte)(0b0000111100001111);
		
		int offset = 0;
		
		for(int i = 0; i < 4; i++)
		{
			char letter = output.charAt(i + offset);
			/*if(letter == 32) // space
				continue; // Array defaults to initialize to zeros
			
			//int offset = -1;
			
			if(letter >= 48 && letter <= 57) // 0-9
				offset = letter - 48;
			if(letter >= 65 && letter <= 90) // A-Z
				offset = letter - 55;
			if(letter >= 97 && letter <= 122) // a-z
				offset = letter - 87;
			
			if(offset < 0)
				continue;
			
			output_buffer[(3-i)*2+2] = CHAR[offset][0];
			output_buffer[(3-i)*2+3] = CHAR[offset][1];*/
			
			//TODO: Decimal handling (light DP segment, not its own character)
			while(/*letter < 32 ||*/ letter == '.')
			{
				if(letter == '.')
				{
					if(i != 0)
						output_buffer[(4-i)*2+3] |= (byte)0b01000000;
				}
				
				offset++;
				letter = output.charAt(i + offset);
			}
			//if(letter < 32)
			//	return;
			output_buffer[(3-i)*2+2] = CHAR[letter-32][0];
			output_buffer[(3-i)*2+3] = CHAR[letter-32][1];
		}
		
		_digit.writeBulk(output_buffer);
	}
	
	private static final byte[][] CHAR = 
		{
			{(byte)0b00000000, (byte)0b00000000}, //   
			{(byte)0b00000110, (byte)0b00000000}, // ! 
			{(byte)0b00100000, (byte)0b00000010}, // " 
			{(byte)0b11001110, (byte)0b00010010}, // # 
			{(byte)0b11101101, (byte)0b00010010}, // $ 
			{(byte)0b00100100, (byte)0b00100100}, // % 
			{(byte)0b01011101, (byte)0b00001011}, // & 
			{(byte)0b00000000, (byte)0b00000100}, // ' 
			{(byte)0b00000000, (byte)0b00001100}, // ( 
			{(byte)0b00000000, (byte)0b00100001}, // ) 
			{(byte)0b11000000, (byte)0b00111111}, // * 
			{(byte)0b11000000, (byte)0b00010010}, // + 
			{(byte)0b00000000, (byte)0b00100000}, // , 
			{(byte)0b11000000, (byte)0b00000000}, // - 
			{(byte)0b00000000, (byte)0b00000000}, // . 
			{(byte)0b00000000, (byte)0b00100100}, // / 
			{(byte)0b00111111, (byte)0b00100100}, // 0 
			{(byte)0b00000110, (byte)0b00000000}, // 1 
			{(byte)0b11011011, (byte)0b00000000}, // 2 
			{(byte)0b10001111, (byte)0b00000000}, // 3 
			{(byte)0b11100110, (byte)0b00000000}, // 4 
			{(byte)0b01101001, (byte)0b00001000}, // 5 
			{(byte)0b11111101, (byte)0b00000000}, // 6 
			{(byte)0b00000111, (byte)0b00000000}, // 7 
			{(byte)0b11111111, (byte)0b00000000}, // 8 
			{(byte)0b11101111, (byte)0b00000000}, // 9 
			{(byte)0b00000000, (byte)0b00010010}, // : 
			{(byte)0b00000000, (byte)0b00100010}, // ; 
			{(byte)0b00000000, (byte)0b00001100}, // < 
			{(byte)0b11001000, (byte)0b00000000}, // = 
			{(byte)0b00000000, (byte)0b00100001}, // > 
			{(byte)0b10000011, (byte)0b00010000}, // ? 
			{(byte)0b10111011, (byte)0b00000010}, // @ 
			{(byte)0b11110111, (byte)0b00000000}, // A 
			{(byte)0b10001111, (byte)0b00010010}, // B 
			{(byte)0b00111001, (byte)0b00000000}, // C 
			{(byte)0b00001111, (byte)0b00010010}, // D 
			{(byte)0b11111001, (byte)0b00000000}, // E 
			{(byte)0b01110001, (byte)0b00000000}, // F 
			{(byte)0b10111101, (byte)0b00000000}, // G 
			{(byte)0b11110110, (byte)0b00000000}, // H 
			{(byte)0b00000000, (byte)0b00010010}, // I 
			{(byte)0b00011110, (byte)0b00000000}, // J 
			{(byte)0b01110000, (byte)0b00001100}, // K 
			{(byte)0b00111000, (byte)0b00000000}, // L 
			{(byte)0b00110110, (byte)0b00000101}, // M 
			{(byte)0b00110110, (byte)0b00001001}, // N 
			{(byte)0b00111111, (byte)0b00000000}, // O 
			{(byte)0b11110011, (byte)0b00000000}, // P 
			{(byte)0b00111111, (byte)0b00001000}, // Q 
			{(byte)0b11110011, (byte)0b00001000}, // R 
			{(byte)0b11101101, (byte)0b00000000}, // S 
			{(byte)0b00000001, (byte)0b00010010}, // T 
			{(byte)0b00111110, (byte)0b00000000}, // U 
			{(byte)0b00110000, (byte)0b00100100}, // V 
			{(byte)0b00110110, (byte)0b00101000}, // W 
			{(byte)0b00000000, (byte)0b00101101}, // X 
			{(byte)0b00000000, (byte)0b00010101}, // Y 
			{(byte)0b00001001, (byte)0b00100100}, // Z 
			{(byte)0b00111001, (byte)0b00000000}, // [ 
			{(byte)0b00000000, (byte)0b00001001}, // \ 
			{(byte)0b00001111, (byte)0b00000000}, // ] 
			{(byte)0b00000011, (byte)0b00100100}, // ^ 
			{(byte)0b00001000, (byte)0b00000000}, // _ 
			{(byte)0b00000000, (byte)0b00000001}, // ` 
			{(byte)0b01011000, (byte)0b00010000}, // a 
			{(byte)0b01111000, (byte)0b00001000}, // b 
			{(byte)0b11011000, (byte)0b00000000}, // c 
			{(byte)0b10001110, (byte)0b00100000}, // d 
			{(byte)0b01011000, (byte)0b00100000}, // e 
			{(byte)0b01110001, (byte)0b00000000}, // f 
			{(byte)0b10001110, (byte)0b00000100}, // g 
			{(byte)0b01110000, (byte)0b00010000}, // h 
			{(byte)0b00000000, (byte)0b00010000}, // i 
			{(byte)0b00001110, (byte)0b00000000}, // j 
			{(byte)0b00000000, (byte)0b00011110}, // k 
			{(byte)0b00110000, (byte)0b00000000}, // l 
			{(byte)0b11010100, (byte)0b00010000}, // m 
			{(byte)0b01010000, (byte)0b00010000}, // n 
			{(byte)0b11011100, (byte)0b00000000}, // o 
			{(byte)0b01110000, (byte)0b00000001}, // p 
			{(byte)0b10000110, (byte)0b00000100}, // q 
			{(byte)0b01010000, (byte)0b00000000}, // r 
			{(byte)0b10001000, (byte)0b00001000}, // s 
			{(byte)0b01111000, (byte)0b00000000}, // t 
			{(byte)0b00011100, (byte)0b00000000}, // u 
			{(byte)0b00000100, (byte)0b00001000}, // v 
			{(byte)0b00010100, (byte)0b00101000}, // w 
			{(byte)0b11000000, (byte)0b00101000}, // x 
			{(byte)0b00001100, (byte)0b00001000}, // y 
			{(byte)0b01001000, (byte)0b00100000}, // z 
			{(byte)0b01001001, (byte)0b00100001}, // { 
			{(byte)0b00000000, (byte)0b00010010}, // | 
			{(byte)0b10001001, (byte)0b00001100}, // } 
			{(byte)0b00100000, (byte)0b00000101}, // ~ 
			{(byte)0b11111111, (byte)0b00111111}  // DEL 
		};
	
	// Shamelessly stolen from Team 1493
	/*private static final byte[][] CHAR = 
		{
			{(byte)0b00111111, (byte)0b00000000}, //0
			{(byte)0b00000110, (byte)0b00000000}, //1
			{(byte)0b11011011, (byte)0b00000000}, //2
			{(byte)0b11001111, (byte)0b00000000}, //3
			{(byte)0b11100110, (byte)0b00000000}, //4
			{(byte)0b11101101, (byte)0b00000000}, //5
			{(byte)0b11111101, (byte)0b00000000}, //6
			{(byte)0b00000111, (byte)0b00000000}, //7
			{(byte)0b11111111, (byte)0b00000000}, //8
			{(byte)0b11101111, (byte)0b00000000}, //9
			{(byte)0b11110111, (byte)0b00000000}, //A
			{(byte)0b10001111, (byte)0b00010010}, //B
			{(byte)0b00111001, (byte)0b00000000}, //C
			{(byte)0b00001111, (byte)0b00010010}, //D
			{(byte)0b11111001, (byte)0b00000000}, //E
			{(byte)0b11110001, (byte)0b00000000}, //F
			{(byte)0b10111101, (byte)0b00000000}, //G
			{(byte)0b11110110, (byte)0b00000000}, //H
			{(byte)0b00001001, (byte)0b00010010}, //I
			{(byte)0b00011110, (byte)0b00000000}, //J
			{(byte)0b01110000, (byte)0b00001100}, //K
			{(byte)0b00111000, (byte)0b00000000}, //L
			{(byte)0b00110110, (byte)0b00000101}, //M
			{(byte)0b00110110, (byte)0b00001001}, //N
			{(byte)0b00111111, (byte)0b00000000}, //O
			{(byte)0b11110011, (byte)0b00000000}, //P
			{(byte)0b00111111, (byte)0b00001000}, //Q
			{(byte)0b11110011, (byte)0b00001000}, //R
			{(byte)0b10001101, (byte)0b00000001}, //S
			{(byte)0b00000001, (byte)0b00010010}, //T
			{(byte)0b00111110, (byte)0b00000000}, //U
			{(byte)0b00110000, (byte)0b00100100}, //V
			{(byte)0b00110110, (byte)0b00101000}, //W
			{(byte)0b00000000, (byte)0b00101101}, //X
			{(byte)0b00000000, (byte)0b00010101}, //Y
			{(byte)0b00001001, (byte)0b00100100}  //Z
		};*/
	
	private void OMGSECRETS(ACTION z)
	{
		//enum FIZZBUZZ {BYZANTINE, HARBINGER, QUIZZICAL, BLIZZARDY, FLAPJACKS, FUZZBELLY, SWIZZLERS, MAXIMIZER}
		
		/*boolean BYZANTINE = true;
		char HARBINGER = '^', QUIZZICAL;
		int BLIZZARDY = z.ordinal() + 17 - 4;
		HARBINGER += '"';
		byte FLAPJACKS = _secret_code_input;
		char FUZBELLY = '[' - 'Z';
		double SWIZZLERS = 0.31029995664;
		char MAXIMIZER = (char) ('s' - ('f' + '%'));
		
		while(FLAPJACKS >> (byte)Math.ceil(Math.log(HARBINGER)/SWIZZLERS) << MAXIMIZER == (SECRET_CODE & HARBINGER)  || BYZANTINE)
		{
			
		}
		*/
		if(_secret_code_input == SECRET_CODE)
		{
			//System.out.println("SECRET CODE TIME!");
			return;
		}
		/*int i = 7;
		while((_secret_code_input >> i & 1) == (SECRET_CODE >> i & 1))
			i--;
		if(((_secret_code_input >> i) & 1) != (z.ordinal()-1))
			_secret_code_input ^= 1 << i;
		else
			_secret_code_input = ~SECRET_CODE;*/
	}
}
