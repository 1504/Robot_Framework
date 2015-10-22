package org.usfirst.frc.team1504.robot;

import java.util.List;
import java.util.ArrayList;

public class Update_Semaphore
{
	interface Updatable
	{
		void semaphore_update();
	}
	
	private List<Updatable> _list = new ArrayList<Updatable>();
	
	private static Update_Semaphore instance = new Update_Semaphore();
	
	protected Update_Semaphore()
	{
	}
	
	public static Update_Semaphore getInstance()
	{
		return instance;
	}
	
	public void register(Updatable e)
	{
		_list.add(e);
	}
	
	public void newData()
	{
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
