package org.loadui.jcelery.worker;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import org.loadui.jcelery.*;
import org.loadui.jcelery.base.AbstractWorker;
import org.loadui.jcelery.tasks.RevokeJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RevokeWorker extends AbstractWorker<RevokeJob>
{
	Logger log = LoggerFactory.getLogger( RevokeWorker.class );

	public RevokeWorker( String host, int port, String username, String password, String vhost )
	{
		super( host, port, username, password, vhost, Queue.REVOKE, Exchange.RESULTS );
	}

	public RevokeWorker( ConnectionProvider connectionFactory, ConsumerProvider consumerProvider )
	{
		super( connectionFactory, consumerProvider, Queue.REVOKE, Exchange.RESULTS );
	}

	@Override
	protected void run() throws Exception
	{
		initializeConnection();
		while( isRunning() )
		{
			try
			{
				log.debug( " waiting for tasks" );
				QueueingConsumer.Delivery delivery = consumer.nextMessage( POLLING_TIMEOUT );
				if( delivery != null )
				{
					String message = new String( delivery.getBody() );
					log.debug( "Received message: " + message );
					if( message.contains( "\"method\": \"revoke\"" ) )
					{
						RevokeJob task = RevokeJob.fromJson( message, this );
						if( onJob != null )
						{
							log.info( "Handling task: " + message );
							task.start();
							onJob.handle( task ); // This is blocking.
							waitUntilJobCompleted( task );
						}
					}
					else if( message.contains( "\"method\": \"enable_events\"" ) )
					{
						log.debug( "asked by Celery to enable events, not yet supported by JCeleryWorker" );
					}
					else if( message.contains( "\"method\": \"dump_conf\"" ) )
					{
						log.debug( "asked by Celery to dump configuration, not yet supported by JCeleryWorker" );
					}
					else if( message.contains( "\"method\": \"heartbeat\"" ) )
					{
						log.debug( "asked by Celery to provide a heartbeat, not yet supported by JCeleryWorker" );
					}
					getChannel().basicAck( delivery.getEnvelope().getDeliveryTag(), false );
				}
			}
			catch( NullPointerException e )
			{
				log.error( "Message could not be parsed, is it the correct format? Supported formats: [JSON], Non-supported formats: [ Pickle, MessagePack, XML ]", e );
			}
			catch( IOException | AlreadyClosedException e )
			{
				log.info( "Lost rabbitMQ connection" );
			}
			catch( ShutdownSignalException e )
			{
				try
				{
					waitAndRecover( 2000 );
					log.info( "Connection recovered" );
				}
				catch( Exception ex )
				{
					log.error( "Attempted recovery failed. Reason: " + ex.getMessage() );
				}
			}
			catch( Exception e )
			{
				log.error( "Critical error, unable to inform the caller about failure.", e );
			}
		}
	}

	@Override
	protected MessageConsumer replaceConsumer( Channel channel ) throws IOException, ShutdownSignalException
	{
		return getConsumerProvider().getRevokeConsumer( channel );
	}

	@Override
	protected void initializeConnection() throws InterruptedException
	{
		while( isRunning() )
		{
			try
			{
				connect();

				Channel channel = getChannel();
				channel.exchangeDeclare( getQueue(), "fanout" );
				channel.queueDeclare( getQueue(), true, false, false, null );
				channel.queueBind( getQueue(), getQueue(), "" );

				break;
			}
			catch( IOException e )
			{
				log.error( "Unable to connect, retrying in 2 seconds" );
				Thread.sleep( 1000 );
			}
		}

	}
}

