package org.usfirst.frc.team1504.robot;

import java.util.List;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Update_Semaphore
{
	interface Updatable
	{
		void semaphore_update();
	}
	
	private List<Updatable> _list = new ArrayList<Updatable>();

	private List<Thread> _thread_pool = new ArrayList<Thread>();
	private Logger _logger = Logger.getInstance();
	private long _last_update;
	
	private Object _test = new Object();
	
	private static final Update_Semaphore instance = new Update_Semaphore();
	
	protected Update_Semaphore()
	{
		ClassLoader class_loader = Update_Semaphore.class.getClassLoader();
		for(int i = 1; i < Map.LOGGED_CLASSES.values().length; i++)
		{
			String subclass = Utils.toCamelCase(Map.LOGGED_CLASSES.values()[i].toString());
			try {
				System.out.println("Semaphore - Attempting to load org.usfirst.frc.team1504.robot." + subclass);
				class_loader.loadClass("org.usfirst.frc.team1504.robot." + subclass);
				//Class.forName("org.usfirst.frc.team1504.robot." + subclass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Semaphore Initialized");
	}
	
	public static Update_Semaphore getInstance()
	{
		return instance;
	}
	
	public void register(Updatable e)
	{
		_list.add(e);
		System.out.println("\tSemaphore - registered " + e.getClass().getName());
		_thread_pool.add(
				new Thread(
						new Runnable()
						{
							public void run()
							{
								System.out.println("\tSemaphore - starting pool thread for " + e.getClass().getName());
								//Update_Semaphore semaphore = Update_Semaphore.getInstance();
								while(true)
								{
									try {
										synchronized (_test)
										{
											_test.wait(); // Will wait indefinitely until notified
										}
										e.semaphore_update();
									} catch (InterruptedException error) {
										error.printStackTrace();
									}
								}
							}
						}
				)
		);
		_thread_pool.get(_thread_pool.size() - 1).start();
	}
	
	private void dump()
	{
		byte[] ret = new byte[4];
		ByteBuffer.wrap(ret).putInt((int)(_last_update - IO.ROBOT_START_TIME));
		
		_logger.log(Map.LOGGED_CLASSES.SEMAPHORE, ret);
	}
	
	public void newData()
	{
		_last_update = System.currentTimeMillis();
		dump();
		
		synchronized (_test)
		{
			_test.notifyAll();
		}
	}
	
	/*public void newData()
	{
		_last_update = System.currentTimeMillis();
		dump();
		//System.out.println("semaphore");
		
		for(int i = 0; i < _list.size(); i++)
		{
			if(_tlist.get(i) == null || !_tlist.get(i).isAlive())
			{
				Updatable u = _list.get(i);
				Thread t = new Thread(new Runnable() {
					public void run() {
						u.semaphore_update();
					}
				});
				_tlist.set(i, t);
				_tlist.get(i).start(); //t.start()
				
			//	_button_mask &= _clear_mask;
			//	_button_mask_rising_edge &= _clear_mask_rising_edge;
			}
			else
			{
//				System.out.println("thread not updated!");
			}
		}
	}*/
}