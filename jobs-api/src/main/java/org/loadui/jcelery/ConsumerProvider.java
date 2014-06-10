package org.loadui.jcelery;

import com.rabbitmq.client.Channel;

public interface ConsumerProvider
{
	public MessageConsumer getInvokeConsumer( Channel channel );

	public MessageConsumer getRevokeConsumer( Channel channel );
}
