package org.loadui.jcelery.worker;

import com.rabbitmq.client.*;
import org.loadui.jcelery.ConnectionProvider;
import org.loadui.jcelery.Exchange;
import org.loadui.jcelery.MessageConsumer;
import org.loadui.jcelery.Queue;
import org.loadui.jcelery.base.AbstractWorker;
import org.loadui.jcelery.tasks.RevokeJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

public class RevokeWorker extends AbstractWorker
{
	Logger log = LoggerFactory.getLogger( RevokeWorker.class );

	public RevokeWorker( String host )
	{
		super( host, Queue.REVOKE, Exchange.RESULTS );
	}

	public RevokeWorker( ConnectionProvider connectionFactory, MessageConsumer consumer )
	{
		super( connectionFactory, consumer, Queue.REVOKE, Exchange.RESULTS );
	}

	@Override
	public void respond( String id, String response ) throws IOException
	{
		log.debug( getClass().getSimpleName() + ": Trying to respond " + response + " for job " + id );

		Channel channel = getChannel();
		if( channel != null )
		{
			AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType( "application/json" ).build();
			channel.queueDeclare( getExchange(), true, false, false, new HashMap<String, Object>() );
			channel.basicPublish( "", getExchange(), props, response.getBytes() );
		}
		else
		{
			log.error( "Unable to respond to message " + id + " with message: " + response + " channel is unavailable." );
		}
	}

	@Override
	protected void run() throws Exception
	{
		createConnectionIfRequired();
		Channel channel = getChannel();

		if( channel != null )
		{
			channel.exchangeDeclare( getQueue(), "fanout" );
			channel.queueDeclare( getQueue(), true, false, false, null );
			channel.queueBind( getQueue(), getQueue(), "" );

			Consumer rabbitConsumer = getMessageConsumer().initialize( channel );
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
					if(message.contains( "\"method\": \"revoke\"" ))
					{
						RevokeJob task = RevokeJob.fromJson( message, this );
						if( onJob != null )
						{
							log.info( "Handling task: " + message );
							onJob.handle( task ); // This is blocking!
						}
					}else if(message.contains( "\"method\": \"dump_conf\"" )){
						log.debug( "asked by Celery to dump configuration, not yet supported by JCeleryWorker" );
					}else if(message.contains( "\"method\": \"heartbeat\"" )){
						log.debug( "asked by Celery to provide a heartbeat, not yet supported by JCeleryWorker" );
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
				waitAndReconnect();
			}
			catch( ConsumerCancelledException e )
			{
				log.info( "Consumer cancelled", e );
				waitAndReconnect();
			}
			catch( Exception e )
			{
				log.error( "Critical error, unable to inform the caller about failure.", e );
			}
		}
	}
}

