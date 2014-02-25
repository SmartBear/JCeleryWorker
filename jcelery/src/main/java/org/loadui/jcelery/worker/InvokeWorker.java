package org.loadui.jcelery.worker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
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

	public InvokeWorker( ConnectionFactory connectionFactory )
	{
		super( connectionFactory, Queue.CELERY, Exchange.RESULTS );
	}

	public void respond( String id, String response ) throws IOException
	{
		getChannel().queueDeclare( getExchange(), true, false, false, new HashMap<String, Object>() );
		AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType( "application/json" ).build();

		getChannel().basicPublish( "", getExchange(), props, response.getBytes() );
	}

	@Override
	public void run() throws Exception
	{
		createConnectionIfRequired();

		getChannel().queueDeclare( getQueue(), true, false, false, new HashMap<String, Object>() );

		QueueingConsumer consumer = new QueueingConsumer( getChannel() );
		getChannel().basicConsume( getQueue(), true, consumer );

		while( isRunning() )
		{
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String( delivery.getBody() );

			InvokeJob task = InvokeJob.fromJson( message, this );

			if( onJob != null )
			{
				onJob.handle( task ); // This is blocking!
			}
		}
	}
}
