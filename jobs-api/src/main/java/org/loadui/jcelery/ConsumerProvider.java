package org.loadui.jcelery;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

public interface ConsumerProvider
{
	public MessageConsumer getInvokeConsumer( Channel channel );

	public MessageConsumer getRevokeConsumer( Channel channel );

	public void replaceMessageConsumer( MessageConsumer invoker, MessageConsumer revoker );
}
