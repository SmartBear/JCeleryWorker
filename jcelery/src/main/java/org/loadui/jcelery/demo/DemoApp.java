package org.loadui.jcelery.demo;

import org.loadui.jcelery.JobService;
import org.loadui.jcelery.Task;
import org.loadui.jcelery.TaskHandler;
import org.loadui.jcelery.base.CeleryService;
import org.loadui.jcelery.tasks.MethodTask;

import java.io.IOException;
import java.util.List;

public class DemoApp
{
	public static void main( String[] _ ) throws Exception
	{

		JobService celeryService = new CeleryService();
		celeryService.setJobHandler( new TaskHandler<MethodTask>()
		{
			@Override
			public void handle( MethodTask t ) throws IOException
			{
				switch( t.getTask() )
				{
					case "tasks.add":
						t.complete( Task.Status.SUCCESS, add( t ) );
				}
			}
		} );

		celeryService.startService();

		Thread.sleep( 300_000 );

		celeryService.stopService();
	}

	private static long add( MethodTask t )
	{
		List<Object> args = t.getArgs();
		long x = ( long )args.get( 0 );
		long y = ( long )args.get( 1 );
		return x + y;
	}

}
