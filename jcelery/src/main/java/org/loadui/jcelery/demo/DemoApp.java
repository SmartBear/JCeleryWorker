package org.loadui.jcelery.demo;

import org.loadui.jcelery.JobService;
import org.loadui.jcelery.internal.CeleryService;
import org.loadui.jcelery.CeleryTask;
import org.loadui.jcelery.TaskHandler;

import java.io.IOException;

import static org.loadui.jcelery.Status.SUCCESS;

public class DemoApp
{
	public static void main(String[] _)	throws Exception {

		JobService celeryService = new CeleryService();
		celeryService.setTaskHandler( new TaskHandler()
		{
			@Override
			public void handle( CeleryTask t ) throws IOException
			{
				switch( t.task )
				{
					case "tasks.add": t.complete( SUCCESS, add(t) );
				}
			}
		} );

		celeryService.startAsynchronous();
		celeryService.waitUntilRunning();

		Thread.sleep( 300_000 );

		celeryService.stopAsynchronous();
		celeryService.waitUntilTerminated();
	}

	private static long add(CeleryTask t)
	{
		long x = (long) t.args.get( 0 );
		long y = (long) t.args.get( 1 );
		return x + y;
	}

}
