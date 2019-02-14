package org.usfirst.frc1504.Robot2019;

import java.util.List;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Update_Semaphore
{
	interface Updatable
	{
		void semaphore_update();
	}
	
	private class Pool_Thread implements Runnable
	{
		public volatile boolean run = false;
		private Object _lock;
		private Updatable _update_class; 
		
		/**
		 * Thread pool class for running the semaphore
		 * @param l - Lock object to sync on
		 * @param u - Updatable class to run on semaphore update
		 */
		Pool_Thread(Object l, Updatable u)
		{
			_lock = l;
			_update_class = u;
		}
		
		public void run()
		{
			System.out.println("\tSemaphore - starting pool thread for " + _update_class.getClass().getName());
			while(true)
			{
				try {
					synchronized (_lock)
					{
						while(!run) // Prevent spurious wakeups
							_lock.wait(); // Will wait indefinitely until notified
						run = false;
					}
					_update_class.semaphore_update();
				} catch (InterruptedException error) {
					error.printStackTrace();
				}
			}
		}
	}
	
	private List<Updatable> _list = new ArrayList<Updatable>();
	private List<Pool_Thread> _thread_pool = new ArrayList<Pool_Thread>();
	private Logger _logger = Logger.getInstance();
	private long _last_update;
	
	private final Object _lock = new Object();
	
	private static final Update_Semaphore instance = new Update_Semaphore();

	
	protected Update_Semaphore()
	{
		System.out.println("Semaphore Initialized");
	}
	
	public static Update_Semaphore getInstance()
	{
		return instance;
	}
	
	public static void initialize()
	{
		getInstance();
	}
	
	public void register(Updatable update_class)
	{
		_list.add(update_class);
		
		_thread_pool.add(new Pool_Thread(_lock, update_class));
		new Thread(_thread_pool.get(_thread_pool.size() - 1)).start();		
		
		System.out.println("\tSemaphore - registered " + update_class.getClass().getName());
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
		
		synchronized (_lock)
		{
			for(Pool_Thread item : _thread_pool)
				item.run = true;
			_lock.notifyAll();
		}
	}
}