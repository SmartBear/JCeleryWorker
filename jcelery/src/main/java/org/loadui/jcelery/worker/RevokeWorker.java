package org.loadui.jcelery.worker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
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
		AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType( "application/json" ).build();
		getChannel().queueDeclare( getExchange(), true, false, false, new HashMap<String, Object>() );
		getChannel().basicPublish( "", getExchange(), props, response.getBytes() );
	}

	@Override
	protected void run() throws Exception
	{
		createConnectionIfRequired();

		if( getChannel() != null )
		{
			getChannel().exchangeDeclare( getQueue(), "fanout" );
			getChannel().queueDeclare( getQueue(), true, false, false, null );
			getChannel().queueBind( getQueue(), getQueue(), "" );

			Consumer rabbitConsumer = getMessageConsumer().initialize( getChannel() );
			getChannel().basicConsume( getQueue(), true, rabbitConsumer );
		}
		while( isRunning() )
		{
			String message = getMessageConsumer().nextMessage();
			try
			{
				if( message != null )
				{
					RevokeJob task = RevokeJob.fromJson( message, this );
					if( onJob != null )
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

