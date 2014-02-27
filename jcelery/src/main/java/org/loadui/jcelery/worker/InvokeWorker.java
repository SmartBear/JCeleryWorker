package org.loadui.jcelery.worker;

import com.rabbitmq.client.AMQP;
import org.loadui.jcelery.ConnectionProvider;
import org.loadui.jcelery.Exchange;
import org.loadui.jcelery.MessageConsumer;
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

	public InvokeWorker( ConnectionProvider connectionFactory, MessageConsumer consumer )
	{
		super( connectionFactory, consumer, Queue.CELERY, Exchange.RESULTS );
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
		MessageConsumer messageConsumer = getMessageConsumer();

		getChannel().queueDeclare( getQueue(), true, false, false, new HashMap<String, Object>() );
		getChannel().basicConsume( getQueue(), true, messageConsumer.getConsumer() );

		while( isRunning() )
		{
			try{

			String message = messageConsumer.nextMessage();

			InvokeJob task = InvokeJob.fromJson( message, this );

			if( onJob != null )
			{
				onJob.handle( task ); // This is blocking!
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
