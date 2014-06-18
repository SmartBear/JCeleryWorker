package org.loadui.jcelery.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class JobUtils
{
	private static final String START_JOB = "tasks.controller.start";

	public static String startJob(String id, String label, String arg)
	{
		return "{\"id\": \"" + id + "\", \"task\": \"" + label + "\", \"args\": [\"" + arg + "\"]}";
	}

	public static String startJob(String id)
	{
		  return startJob(id, START_JOB, "");
	}
	public static String startJob(String id, String arg)
	{
		return startJob(id, START_JOB, arg);
	}

	public static void waitUntil( String message, Callable<Boolean> predicate, long timeout, TimeUnit unit )
			throws InterruptedException
	{
		long lastTime = System.currentTimeMillis() + unit.toMillis( timeout );
		while( System.currentTimeMillis() < lastTime )
		{
			try
			{
				if( predicate.call() )
					return;
			}
			catch( Exception e )
			{
				throw new AssertionError( "Predicate failed with an exception", e );
			}
			Thread.sleep( 100 );
		}
		throw new AssertionError( "Predicate not satisfied within timeout: " + message );
	}
}
