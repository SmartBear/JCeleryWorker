package org.loadui.jcelery;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;

public class CeleryService extends AbstractExecutionThreadService
{
	private final static String QUEUE_NAME = "celery";

	private final String host;

	private TaskHandler onTask;
	private Channel channel;

	public void setTaskHandler( TaskHandler handler )
	{
		this.onTask = handler;
	}

	public CeleryService()
	{
		this( "localhost" );
	}

	public CeleryService( String host )
	{
		this.host = host;
	}

	void respond(String id, String response) throws IOException
	{
		channel.basicPublish("", id, null, response.getBytes());
		System.out.println("Responded to task: " + response);
	}

	@Override
	protected void run() throws Exception
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		Connection connection = factory.newConnection();
		channel = connection.createChannel();

		channel.queueDeclare( QUEUE_NAME, true, false, false, null );
		System.out.println("Waiting for tasks from host "+connection.getAddress() + ".");

		QueueingConsumer consumer = new QueueingConsumer( channel );
		channel.basicConsume( QUEUE_NAME, true, consumer );

		while (isRunning()) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());

			System.out.println("Input: "+ message);

			CeleryTask task = CeleryTask.fromJson( message, this );

			if(onTask != null)
			{
				onTask.handle( task ); // This is blocking!
			}
		}
	}

}
