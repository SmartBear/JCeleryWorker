package org.loadui.jcelery;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.Callable;

public class CeleryConsumer extends AbstractExecutionThreadService
{
	private final static String QUEUE_NAME = "celery";

	private String host = "localhost";

	private TaskHandler onTask;

	public void setTaskHandler( TaskHandler handler )
	{
		this.onTask = handler;
	}

	public CeleryConsumer(String host)
	{
		this.host = host;
	}

	@Override
	protected void run() throws Exception
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		System.out.println("Waiting for tasks from host "+connection.getAddress() + ".");

		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(QUEUE_NAME, true, consumer);

		while (isRunning()) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());

			CeleryTask task = CeleryTask.fromJson( message );

			if(onTask != null)
			{
				onTask.handle( task );
			}
		}
	}

}
