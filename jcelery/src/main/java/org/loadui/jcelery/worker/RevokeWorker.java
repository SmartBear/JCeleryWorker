package org.loadui.jcelery.worker;

import com.rabbitmq.client.AMQP;
import org.loadui.jcelery.ConnectionProvider;
import org.loadui.jcelery.Exchange;
import org.loadui.jcelery.MessageConsumer;
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

		getChannel().exchangeDeclare( getQueue(), "fanout" );
		getChannel().queueDeclare( getQueue(), true, false, false, null );
		getChannel().queueBind( getQueue(), getQueue(), "" );
		MessageConsumer messageConsumer = getMessageConsumer();

		getChannel().basicConsume( getQueue(), true, messageConsumer.getConsumer() );

		while( isRunning() )
		{
			try
			{
				String message = messageConsumer.nextMessage();

				RevokeJob task = RevokeJob.fromJson( message, this );

				if( onJob != null )
				{
					onJob.handle( task ); // This is blocking!
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}
}
