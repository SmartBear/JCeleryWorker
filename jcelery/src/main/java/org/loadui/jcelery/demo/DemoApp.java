package org.loadui.jcelery.demo;

import org.loadui.jcelery.JobService;
import org.loadui.jcelery.TaskHandler;
import org.loadui.jcelery.internal.CeleryService;
import org.loadui.jcelery.internal.CeleryTask;

import java.io.IOException;

import static org.loadui.jcelery.Status.SUCCESS;

public class DemoApp
{
	public static void main(String[] _)	throws Exception {

		JobService celeryService = new CeleryService();
		celeryService.setTaskHandler( new TaskHandler<CeleryTask>()
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

		celeryService.startAsync();
		celeryService.awaitRunning();

		Thread.sleep( 300_000 );

		celeryService.stopAsync();
		celeryService.awaitTerminated();
	}

	private static long add(CeleryTask t)
	{
		long x = (long) t.args.get( 0 );
		long y = (long) t.args.get( 1 );
		return x + y;
	}

}
