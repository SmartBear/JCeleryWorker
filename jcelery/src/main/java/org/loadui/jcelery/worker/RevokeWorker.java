package org.loadui.jcelery.worker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.QueueingConsumer;
import org.loadui.jcelery.Exchange;
import org.loadui.jcelery.Queue;
import org.loadui.jcelery.base.AbstractWorker;
import org.loadui.jcelery.tasks.RevokeJob;

import java.io.IOException;
import java.util.HashMap;

public class RevokeWorker extends AbstractWorker
{
	public RevokeWorker( String host )
	{
		super( host, Queue.REVOKE, Exchange.RESULTS );
		System.out.println( "Waiting for revoke tasks from host: " + host + " on " + Queue.REVOKE + "" );
	}

	@Override
	public void respond( String id, String response ) throws IOException
	{
		String routingKey = id.replace( "-", "" );

		AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType( "application/json" ).build();

		getChannel().queueDeclare( getExchange(), true, false, false, new HashMap<String, Object>() );
		//getChannel().exchangeDeclare( getExchange(), "direct" );
		//getChannel().queueBind( getExchange(), getExchange(), routingKey );

		getChannel().basicPublish( "", getExchange(), props, response.getBytes() );
		System.out.println( "Responded to task: " + id + " with " + response );
	}

	@Override
	protected void run() throws Exception
	{
		createConnectionIfRequired();

		getChannel().exchangeDeclare( getQueue(), "fanout" );
		getChannel().queueDeclare( getQueue(), true, false, false, null );
		getChannel().queueBind( getQueue(), getQueue(), "" );

		System.out.println( "RevokeWorker: Waiting for tasks from host " + getConnection().getAddress() + " on x-change: " + getQueue() + " bound to queue: " + getQueue() );

		QueueingConsumer consumer = new QueueingConsumer( getChannel() );
		getChannel().basicConsume( getQueue(), true, consumer );

		while( isRunning() )
		{
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String( delivery.getBody() );

			System.out.println( delivery.getEnvelope().getRoutingKey() + " @ " + delivery.getEnvelope().getExchange() + " @ " + delivery.getEnvelope().getDeliveryTag() + " @ " + message );

			RevokeJob task = RevokeJob.fromJson( message, this );

			if( onJob != null )
			{
				onJob.handle( task ); // This is blocking!
			}
		}
	}
}
