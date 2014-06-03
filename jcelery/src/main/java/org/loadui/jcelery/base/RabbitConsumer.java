package org.loadui.jcelery.base;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import org.loadui.jcelery.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitConsumer implements MessageConsumer
{
	private QueueingConsumer consumer;

	private Logger log = LoggerFactory.getLogger( MessageConsumer.class );

	public RabbitConsumer( Channel channel )
	{
		log.debug( "Initialized" );
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
			if( delivery != null ){
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

	@Override
	public boolean isMock()
	{
		return false;
	}

   }
