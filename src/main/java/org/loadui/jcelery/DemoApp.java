package org.loadui.jcelery;

public class DemoApp
{
	public static void main(String[] _)	throws Exception {

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
