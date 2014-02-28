package org.loadui.jcelery.worker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import org.loadui.jcelery.ConnectionProvider;
import org.loadui.jcelery.Exchange;
import org.loadui.jcelery.MessageConsumer;
import org.loadui.jcelery.Queue;
import org.loadui.jcelery.base.AbstractWorker;
import org.loadui.jcelery.tasks.InvokeJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

public class InvokeWorker extends AbstractWorker
{

	Logger log = LoggerFactory.getLogger( RevokeWorker.class );

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

		Consumer rabbitConsumer = getMessageConsumer().initialize( getChannel() );

		getChannel().queueDeclare( getQueue(), true, false, false, new HashMap<String, Object>() );
		getChannel().basicConsume( getQueue(), true, rabbitConsumer );

		while( isRunning() )
		{

			String message = getMessageConsumer().nextMessage();

			try
			{
				InvokeJob task = InvokeJob.fromJson( message, this );

				if( onJob != null && task != null )
				{
					onJob.handle( task ); // This is blocking!
				}
			}
			catch( NullPointerException e )
			{
				log.error( "job could not be parsed, is it the correct format? Supported formats: [JSON], Non-supported formats: [ Pickle, MessagePack, XML ]" );
			}
		}
	}
}
