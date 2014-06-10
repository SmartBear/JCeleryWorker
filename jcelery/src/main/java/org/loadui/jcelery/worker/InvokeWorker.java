package org.loadui.jcelery.worker;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import org.loadui.jcelery.*;
import org.loadui.jcelery.base.AbstractWorker;
import org.loadui.jcelery.tasks.InvokeJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InvokeWorker extends AbstractWorker<InvokeJob>
{
	Logger log = LoggerFactory.getLogger( InvokeWorker.class );

	public InvokeWorker( String host, int port, String username, String password, String vhost )
	{
		super( host, port, username, password, vhost, Queue.CELERY, Exchange.RESULTS );
	}

	public InvokeWorker( ConnectionProvider connectionProvider, ConsumerProvider consumerProvider )
	{
		super( connectionProvider, consumerProvider, Queue.CELERY, Exchange.RESULTS );
	}

	@Override
	public void run() throws Exception
	{
		initializeConnection();
		while( isRunning() )
		{
			log.debug( " waiting for tasks" );
			try
			{
				QueueingConsumer.Delivery delivery = consumer.nextMessage( 500 );
				if( delivery != null )
				{
					String message = new String( delivery.getBody() );
					log.debug( "Received message: " + message );
					InvokeJob task = InvokeJob.fromJson( message, this );
					if( onJob != null && task != null )
					{
						log.info( "Handling task: " + message );
						onJob.handle( task ); // This is blocking!
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
				log.warn( "Lost RabbitMQ connection" );
			}
			catch( ShutdownSignalException e )
			{
				log.debug( "Attempting reconnection" );
				try
				{
					waitAndRecover( 2000 );
				}
				catch( Exception ex )
				{
					log.error( "Attempted recovery failed. Reason: " + ex.getMessage(), ex );
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
		return getConsumerProvider().getInvokeConsumer( channel );
	}

	@Override
	protected void initializeConnection() throws InterruptedException
	{
		while( isRunning() )
		{
			try
			{
				connect();
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
