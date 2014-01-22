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
			public void handle( CeleryTask e ) throws IOException
			{
				System.out.println( "Received task: " + e );
				e.complete( SUCCESS, 42 );
			}
		} );

		celeryService.startAsync();
		celeryService.awaitRunning();

		Thread.sleep( 300000 );

		celeryService.stopAsync();
		celeryService.awaitTerminated();
	}

}
