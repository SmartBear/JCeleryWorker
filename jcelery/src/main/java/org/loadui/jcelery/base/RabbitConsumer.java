package org.loadui.jcelery.base;

import com.rabbitmq.client.*;
import org.loadui.jcelery.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitConsumer implements MessageConsumer
{
	private QueueingConsumer consumer;

	private Logger log = LoggerFactory.getLogger( MessageConsumer.class );

	@Override
	public String nextMessage()
	{
		try
		{
			String message = new String( consumer.nextDelivery().getBody() );
			return message;
		}
		catch( InterruptedException e )
		{
			log.error( "Communication interrupted", e );
		}
		return null;
	}


	@Override
	public Consumer initialize( Channel channel )
	{
		consumer = new QueueingConsumer( channel );
		return consumer;
	}
}
