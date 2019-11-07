package org.usfirst.frc.team1504.utils;

import edu.wpi.cscore.VideoSource;
import edu.wpi.first.wpilibj.vision.VisionPipeline;
import edu.wpi.first.wpilibj.vision.VisionRunner;

public class VisionThreadSingleFrame
{
	private long _last_run_time = 0;
	private Object _lock = new Object();
	/**
	 * Creates a vision thread that runs a {@link VisionPipeline} on request.
	 *
	 * @param visionRunner the runner for a vision pipeline
	 */
	public VisionThreadSingleFrame(VisionRunner<?> visionRunner)
	{
		// Spawn a thread that will process a single frame using the VisionRunner when notified.
		new Thread(
				new Runnable()
				{
					public void run()
					{
						System.out.println("Starting Vision Single Frame pool thread " + this.getClass().getSimpleName());
						while(true)
						{
							try {
								synchronized(_lock)
								{
									_lock.wait(); // Will wait indefinitely until notified
								}
								
								long start_time = System.currentTimeMillis();
								visionRunner.runOnce();
								_last_run_time = System.currentTimeMillis() - start_time;
							} catch (InterruptedException error) {
								error.printStackTrace();
							}
						}
					}
				}
		).start();
	}

	/**
	 * Creates a new vision thread that runs the given vision pipeline on request. This is equivalent
	 * to {@code new VisionThreadSingleFrame(new VisionRunner<>(videoSource, pipeline, listener))}.
	 *
	 * @param videoSource the source for images the pipeline should process
	 * @param pipeline    the pipeline to run
	 * @param listener    the listener to copy outputs from the pipeline after it runs
	 * @param <P>         the type of the pipeline
	 */
	public <P extends VisionPipeline> VisionThreadSingleFrame(VideoSource videoSource,
	                                               P pipeline,
	                                               VisionRunner.Listener<? super P> listener)
	{
		this(new VisionRunner<>(videoSource, pipeline, listener));
	}
	
	public void processImage()
	{
		synchronized(_lock)
		{
			_lock.notifyAll();
		}
	}
	
	public long lastExecutionTime()
	{
		return _last_run_time;
	}
}