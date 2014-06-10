package org.loadui.jcelery.base;

import com.rabbitmq.client.Channel;
import org.loadui.jcelery.ConsumerProvider;
import org.loadui.jcelery.MessageConsumer;

public class RabbitConsumerProvider implements ConsumerProvider
{
	private MessageConsumer specificInvoker;
	private MessageConsumer specificRevoker;

	@Override
	public MessageConsumer getInvokeConsumer( Channel channel )
	{
		return specificInvoker == null ? new RabbitConsumer( channel ) : specificInvoker;
	}

	@Override
	public MessageConsumer getRevokeConsumer( Channel channel )
	{
		return specificRevoker == null ? new RabbitConsumer( channel ) : specificRevoker;
	}

	@Override
	public void replaceMessageConsumer( MessageConsumer anotherInvoker, MessageConsumer anotherRevoker )
	{
		this.specificInvoker = anotherInvoker;
		this.specificRevoker = anotherRevoker;
	}
}
