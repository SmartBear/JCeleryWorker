package org.loadui.jcelery.demo;

import org.loadui.jcelery.api.JobService;
import org.loadui.jcelery.api.TaskHandler;
import org.loadui.jcelery.base.CeleryService;
import org.loadui.jcelery.tasks.MethodWorker;

import java.io.IOException;
import java.util.List;

import static org.loadui.jcelery.base.Status.SUCCESS;

public class DemoApp
{
	public static void main(String[] _)	throws Exception {

		JobService celeryService = new CeleryService();
		celeryService.setJobHandler( new TaskHandler<MethodWorker>()
		{
			@Override
			public void handle( MethodWorker t ) throws IOException
			{
				switch( t.getTask() )
				{
					case "tasks.add":
						t.complete( SUCCESS, add( t ) );
				}
			}
		} );

		celeryService.startService();

		Thread.sleep( 300_000 );

		celeryService.stopService();
	}

	private static long add(MethodWorker t)
	{
		List<Object> args = t.getArgs();
		long x = (long) args.get( 0 );
		long y = (long) args.get( 1 );
		return x + y;
	}

}
