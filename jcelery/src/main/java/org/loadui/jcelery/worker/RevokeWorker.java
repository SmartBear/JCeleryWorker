package org.loadui.jcelery.worker;

import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import org.loadui.jcelery.base.Exchange;
import org.loadui.jcelery.base.Queue;
import org.loadui.jcelery.base.Worker;
import org.loadui.jcelery.tasks.MethodTask;

import java.io.IOException;
import java.util.HashMap;

public class RevokeWorker extends Worker
{
	public RevokeWorker( String host )
	{
		super( host, Queue.REVOKE, Exchange.RESULTS );
		System.out.println( "Waiting for revoke tasks from host: " + host );
	}

	@Override
	public void respond( String id, String response ) throws IOException
	{
		Channel responseChannel = connection.createChannel();

		String responseQueue = id.replace( "-", "" );
		String routingKey = responseQueue;

		responseChannel.queueDeclare( responseQueue, true, false, true, ImmutableMap.of( "x-expires", ( Object )86400000 ) );

		responseChannel.queueBind( responseQueue, getExchange(), routingKey );

		AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType( "application/json" ).build();

		channel.basicPublish( getExchange(), routingKey, props, response.getBytes() );
		System.out.println( "Responded to task: " + id + " with " + response );
	}

	@Override
	protected void run() throws Exception
	{
		createConnectionIfRequired();
		channel = connection.createChannel();

		channel.queueDeclare( getQueue(), true, false, false, new HashMap<String, Object>() );
		System.out.println( "Waiting for tasks from host " + connection.getAddress() + "." );

		QueueingConsumer consumer = new QueueingConsumer( channel );
		channel.basicConsume( getQueue(), true, consumer );

		while( isRunning() )
		{
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String( delivery.getBody() );

			System.out.println( delivery.getEnvelope().getRoutingKey() + " @ " + delivery.getEnvelope().getExchange() + " @ " + delivery.getEnvelope().getDeliveryTag() + " @ " + message );

			MethodTask task = MethodTask.fromJson( message, this );

			if( onTask != null )
			{
				onTask.handle( task ); // This is blocking!
			}
		}
	}
}
