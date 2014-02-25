package org.loadui.jcelery.worker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
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
	}

	public RevokeWorker( ConnectionFactory connectionFactory )
	{
		super( connectionFactory, Queue.REVOKE, Exchange.RESULTS );
	}

	@Override
	public void respond( String id, String response ) throws IOException
	{
		AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType( "application/json" ).build();
		getChannel().queueDeclare( getExchange(), true, false, false, new HashMap<String, Object>() );

		getChannel().basicPublish( "", getExchange(), props, response.getBytes() );
	}

	@Override
	protected void run() throws Exception
	{
		createConnectionIfRequired();

		getChannel().exchangeDeclare( getQueue(), "fanout" );
		getChannel().queueDeclare( getQueue(), true, false, false, null );
		getChannel().queueBind( getQueue(), getQueue(), "" );


		QueueingConsumer consumer = new QueueingConsumer( getChannel() );
		getChannel().basicConsume( getQueue(), true, consumer );

		while( isRunning() )
		{
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String( delivery.getBody() );

			RevokeJob task = RevokeJob.fromJson( message, this );

			if( onJob != null )
			{
				onJob.handle( task ); // This is blocking!
			}
		}
	}
}
