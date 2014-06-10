package org.loadui.jcelery.base;

import com.rabbitmq.client.Channel;
import org.loadui.jcelery.ConsumerProvider;
import org.loadui.jcelery.MessageConsumer;

public class RabbitConsumerProvider implements ConsumerProvider
{
	@Override
	public MessageConsumer getInvokeConsumer( Channel channel )
	{
		return new RabbitConsumer( channel );
	}

	@Override
	public MessageConsumer getRevokeConsumer( Channel channel )
	{
		return new RabbitConsumer( channel );
	}
}
