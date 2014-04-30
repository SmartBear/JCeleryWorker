package org.loadui.jcelery.demo;

import org.loadui.jcelery.Job;
import org.loadui.jcelery.JobService;
import org.loadui.jcelery.TaskHandler;
import org.loadui.jcelery.base.CeleryService;
import org.loadui.jcelery.tasks.InvokeJob;

import java.io.IOException;
import java.util.List;

public class DemoApp
{
	public static void main( String[] _ ) throws Exception
	{

		JobService celeryService = new CeleryService("localhost", 5672, "guest", "guest");
		celeryService.setInvokeHandler( new TaskHandler<InvokeJob>()
		{
			@Override
			public void handle( InvokeJob t ) throws IOException
			{
				switch( t.getMethod() )
				{
					case "tasks.add":
						t.complete( Job.Status.SUCCESS, add( t ) );
				}
			}
		} );

		celeryService.setRevokeHandler( new TaskHandler<InvokeJob>()
		{
			@Override
			public void handle( InvokeJob t ) throws IOException
			{
				switch( t.getMethod() )
				{
					case "revoke":
						t.complete( Job.Status.REVOKED, "" );
				}
			}
		} );

		celeryService.startService();

		Thread.sleep( 300_000 );

		celeryService.stopService();
	}

	private static long add( InvokeJob t )
	{
		List<Object> args = t.getArgs();
		long x = ( long )args.get( 0 );
		long y = ( long )args.get( 1 );
		return x + y;
	}

}
