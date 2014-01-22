package org.loadui.jcelery;

import java.io.IOException;

import static org.loadui.jcelery.Status.SUCCESS;

public class DemoApp
{
	public static void main(String[] _)	throws Exception {

		CeleryService celeryService = new CeleryService();
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

		celeryService.startAsync();
		celeryService.awaitRunning();

		Thread.sleep( 300000 );

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
