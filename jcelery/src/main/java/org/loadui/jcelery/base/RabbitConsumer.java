package org.loadui.jcelery.base;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.QueueingConsumer;
import org.loadui.jcelery.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitConsumer implements MessageConsumer
{
	private QueueingConsumer consumer;

	private Logger log = LoggerFactory.getLogger( RabbitConsumer.class );

	public RabbitConsumer( Channel channel )
	{
		consumer = new QueueingConsumer( channel );
	}

	@Override
	public QueueingConsumer.Delivery nextMessage()
	{
		return nextMessage( 20000 );
	}

	@Override
	public QueueingConsumer.Delivery nextMessage( int timeout )
	{
		try
		{
			QueueingConsumer.Delivery delivery = consumer.nextDelivery( timeout );
			if( delivery != null )
			{
				return delivery;
			}
		}
		catch( InterruptedException e )
		{
			log.error( "Communication interrupted", e );
		}
		return null;
	}

	@Override
	public Consumer getConsumer()
	{
		return consumer;
	}

}
