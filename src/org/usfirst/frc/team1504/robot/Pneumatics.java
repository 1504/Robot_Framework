package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pneumatics implements Updatable
{
	private static class Pneumatics_state
	{
	    public volatile char _highside_pressure, _lowside_pressure;
	    public volatile float _compressor_current;
	    public volatile boolean _pressure_switch, _compressor_enabled;
	    
	    public byte[] dump()
	    {
	    	return new byte[] {
	    			(byte) _highside_pressure, 
	    			(byte) _lowside_pressure,
	    			Utils.double_to_byte(_compressor_current),
	    			(byte) ((_pressure_switch ? 2 : 0) + (_compressor_enabled ? 1 : 0))
	    	};
	    }
	}
	
	private static final Pneumatics instance = new Pneumatics();
	
	private Logger _logger = Logger.getInstance();
	
	private Compressor _compressor;
	private AnalogInput _highside_pressure_input, _lowside_pressure_input;
	private Pneumatics_state _state;
	
	private Thread _dashboard_task;
	
	protected Pneumatics()
	{
		_compressor = new Compressor();
		_highside_pressure_input = new AnalogInput(Map.PNEUMATICS_HIGHSIDE_PORT);
		_lowside_pressure_input =  new AnalogInput(Map.PNEUMATICS_LOWSIDE_PORT);
		
		_state = new Pneumatics_state();
		
		_dashboard_task = new Thread(new Runnable() {
			public void run() {
				while(true)
				{
					update();
					update_dashboard();
					Timer.delay(.05);
				}
			}
		});
    	_dashboard_task.start();
		
		Update_Semaphore.getInstance().register(this);
		
		System.out.println("Pneumatics Initialized.");
	}
	
	public Pneumatics getInstance()
	{
		return instance;
	}
	
	private char voltage_to_pressure(int voltage)
	{
		return (char) ((250 * voltage / 5) - 25);
	}
	
	public void update()
	{
		_state._highside_pressure = voltage_to_pressure(_highside_pressure_input.getAverageValue());
		_state._lowside_pressure = voltage_to_pressure(_lowside_pressure_input.getAverageValue());
		_state._compressor_current = _compressor.getCompressorCurrent();
		_state._pressure_switch = _compressor.getPressureSwitchValue();
		_state._compressor_enabled = _compressor.enabled();
	}
	
	private void update_dashboard()
	{
		SmartDashboard.putNumber("Pneumatics highside pressure", _state._highside_pressure);
		SmartDashboard.putNumber("Pneumatics lowside pressure", _state._lowside_pressure);
		SmartDashboard.putNumber("Pneumatics compressor current", _state._compressor_current);
		SmartDashboard.putBoolean("Pneumatics pressure switch", _state._pressure_switch);
	}
	
	private void dump()
	{
		_logger.log(Map.LOGGED_CLASSES.PNEUMATICS, _state.dump());
	}
	
	@Override
	public void semaphore_update()
	{
		//update();
		dump();
		//update_dashboard();
	}
}
