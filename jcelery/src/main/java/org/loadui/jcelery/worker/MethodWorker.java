package org.loadui.jcelery.worker;

import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import org.loadui.jcelery.base.Exchange;
import org.loadui.jcelery.base.Queue;
import org.loadui.jcelery.base.Worker;

import java.io.IOException;

public class MethodWorker extends Worker
{
	public MethodWorker( String host ){
		super( host, Queue.CELERY, Exchange.RESULTS );
		System.out.println("Waiting for start tasks from host: " + host);
	}

	public void respond( String id, String response ) throws IOException
	{
		Channel responseChannel = connection.createChannel();

		String routingKey = id.replace( "-", "" );
		responseChannel.queueDeclare( getQueue(), true, false, true, ImmutableMap.of( "x-expires", ( Object )86400000 ) );
		responseChannel.queueBind( getExchange(), getQueue(), routingKey );

		AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType( "application/json" ).build();

		channel.basicPublish( getExchange(), getQueue(), props, response.getBytes() );
		System.out.println( "Responded to task: " + id + " with " + response );
	}

	@Override
	public void run() throws Exception
	{
		createConnectionIfRequired();
		channel = connection.createChannel();

		channel.queueDeclare( getQueue(), true, false, false, null );
		System.out.println( "Waiting for tasks from host " + connection.getAddress() + "." );

		QueueingConsumer consumer = new QueueingConsumer( channel );
		channel.basicConsume( getQueue(), true, consumer );

		while( isRunning() )
		{
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String( delivery.getBody() );

			System.out.println( delivery.getEnvelope().getRoutingKey() + " @ " + delivery.getEnvelope().getExchange() + " @ " + delivery.getEnvelope().getDeliveryTag() + " @ " + message );


			org.loadui.jcelery.tasks.MethodWorker task = org.loadui.jcelery.tasks.MethodWorker.fromJson( message, this );

			if( onTask != null )
			{
				onTask.handle( task ); // This is blocking!
			}
		}
	}


}
