package org.loadui.jcelery.worker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.QueueingConsumer;
import org.loadui.jcelery.Exchange;
import org.loadui.jcelery.Queue;
import org.loadui.jcelery.base.AbstractWorker;
import org.loadui.jcelery.tasks.InvokeJob;

import java.io.IOException;
import java.util.HashMap;

public class InvokeWorker extends AbstractWorker
{
	public InvokeWorker( String host )
	{
		super( host, Queue.CELERY, Exchange.RESULTS );
	}

	public void respond( String id, String response ) throws IOException
	{
		String routingKey = id.replace( "-", "" );

		getChannel().queueDeclare( getExchange(), true, false, false, new HashMap<String, Object>() );
		getChannel().exchangeDeclare( getExchange(), "fanout" );
		getChannel().queueBind( getExchange(), getExchange(), routingKey );

		AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType( "application/json" ).build();

		System.out.println( "Responded to task to exchange: " + getExchange() + " for task: " + id + " with " + response );
		getChannel().basicPublish( getExchange(), routingKey, props, response.getBytes() );
	}

	@Override
	public void run() throws Exception
	{
		createConnectionIfRequired();

		getChannel().queueDeclare( getQueue(), true, false, false, new HashMap<String, Object>() );
		System.out.println( "InvokeWorker: Waiting for tasks from host " + getConnection().getAddress() + " on x-change: " + getExchange() + " bound to queue: " + getQueue() );

		QueueingConsumer consumer = new QueueingConsumer( getChannel() );
		getChannel().basicConsume( getQueue(), true, consumer );

		while( isRunning() )
		{
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String( delivery.getBody() );

			System.out.println( delivery.getEnvelope().getRoutingKey() + " @ " + delivery.getEnvelope().getExchange() + " @ " + delivery.getEnvelope().getDeliveryTag() + " @ " + message );

			InvokeJob task = InvokeJob.fromJson( message, this );

			if( onJob != null )
			{
				onJob.handle( task ); // This is blocking!
			}
		}
	}
}
