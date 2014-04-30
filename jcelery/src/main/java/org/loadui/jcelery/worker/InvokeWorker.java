package org.loadui.jcelery.worker;

import com.rabbitmq.client.*;
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

	public InvokeWorker( String host, int port )
	{
		super( host, port, Queue.CELERY, Exchange.RESULTS );
	}

	public InvokeWorker( ConnectionProvider connectionFactory, MessageConsumer consumer )
	{
		super( connectionFactory, consumer, Queue.CELERY, Exchange.RESULTS );
	}

	public void respond( String id, String response ) throws IOException
	{
		log.debug( getClass().getSimpleName() + ": Trying to respond " + response + " for job " + id );
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
			log.debug( getClass().getSimpleName() + "Waiting for tasks" );
			String message = getMessageConsumer().nextMessage();
			try
			{
				if( message != null )
				{
					log.debug( "Received message: " + message );

					InvokeJob task = InvokeJob.fromJson( message, this );

					if( onJob != null && task != null )
					{
						log.info( "Handling task: " + message );
						onJob.handle( task ); // This is blocking!
					}
				}
			}
			catch( NullPointerException e )
			{
				log.error( "Message could not be parsed, is it the correct format? Supported formats: [JSON], Non-supported formats: [ Pickle, MessagePack, XML ]", e );
			}
			catch( ShutdownSignalException e )
			{
				log.error( "Broker shutdown detected, retrying connection in " + DEFAULT_TIMEOUT / 1000 + " seconds", e );

			}
			catch( ConsumerCancelledException e )
			{
				log.info( "Consumer cancelled", e );
			}
			catch( Exception e )
			{
				log.error( "Critical error, unable to inform the caller about failure.", e );
			}

		}
	}
}
