package org.loadui.jcelery.base;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.QueueingConsumer;
import org.loadui.jcelery.MessageConsumer;

public class RabbitConsumer implements MessageConsumer
{
   private QueueingConsumer consumer;

	@Override
	public String nextMessage()
	{
		try{
			String message = new String ( consumer.nextDelivery().getBody() );
			return message;
		}catch( InterruptedException e ){
			e.printStackTrace();
			return new String();
		}
	}

	@Override
	public void initialize( Channel channel )
	{
		consumer = new QueueingConsumer( channel );
	}

	@Override
	public Consumer getConsumer()
	{
		return consumer;
	}
}
