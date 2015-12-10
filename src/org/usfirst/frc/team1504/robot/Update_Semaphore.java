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
	private Logger _logger = Logger.getInstance();
	private long _last_update;
	
	private static final Update_Semaphore instance = new Update_Semaphore();
	
	protected Update_Semaphore()
	{
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
	}
	
	private void dump()
	{
		byte[] ret = new byte[8];
		ByteBuffer.wrap(ret).putLong(_last_update - IO.ROBOT_START_TIME);
		
		_logger.log(Map.LOGGED_CLASSES.SEMAPHORE, ret);
	}
	
	public void newData()
	{
		_last_update = System.currentTimeMillis();
		dump();
		
		for (Updatable obj : _list)
		{
			//obj.semaphore_update();
			
			// Let's see about this. Creating several threads at 20hz might be an overhead issue...
			new Thread(new Runnable() {
				public void run() {
					obj.semaphore_update();
				}
			}).start();
		}
	}
}
