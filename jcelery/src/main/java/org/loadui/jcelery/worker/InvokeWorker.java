package org.loadui.jcelery.worker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
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
		Channel channel = getChannel();
		channel.queueDeclare( getExchange(), true, false, false, new HashMap<String, Object>() );
		AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType( "application/json" ).build();
		channel.basicPublish( "", getExchange(), props, response.getBytes() );
	}

	@Override
	public void run() throws Exception
	{
		createConnectionIfRequired();
		Channel channel = getChannel();

		if( channel != null )
		{
			Consumer rabbitConsumer = getMessageConsumer().initialize( channel );
			channel.queueDeclare( getQueue(), true, false, false, new HashMap<String, Object>() );
			channel.basicConsume( getQueue(), true, rabbitConsumer );
		}
		while( isRunning() )
		{
			String message = getMessageConsumer().nextMessage();
			try
			{
				if( message != null )
				{
					InvokeJob task = InvokeJob.fromJson( message, this );

					if( onJob != null && task != null )
					{
						onJob.handle( task ); // This is blocking!
					}
				}
			}
			catch( NullPointerException e )
			{
				log.error( "job could not be parsed, is it the correct format? Supported formats: [JSON], Non-supported formats: [ Pickle, MessagePack, XML ]" );
			}
		}
	}
}
