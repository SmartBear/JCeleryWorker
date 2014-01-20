package org.loadui.jcelery;

import java.io.IOException;

public class DemoApp
{
	public static void main(String[] _)	throws IOException, InterruptedException {

		CeleryConsumer celeryConsumer = new CeleryConsumer();
		celeryConsumer.setTaskHandler( new TaskHandler()
		{
			@Override
			public void handle( CeleryTask e )
			{
				System.out.println("Received task: " + e);
			}
		} );

		celeryConsumer.startAsync();
		celeryConsumer.awaitRunning();

		Thread.sleep( 10000 );

		celeryConsumer.stopAsync();
		celeryConsumer.awaitTerminated();
	}

}
