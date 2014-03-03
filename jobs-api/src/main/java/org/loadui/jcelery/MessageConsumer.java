package org.loadui.jcelery;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;

public interface MessageConsumer
{
	/**
	 * Blocking call that awaits the next message delivery.
	 */
	public String nextMessage();


	/**
	 * Initializes the underlying consumer and makes it ready to take new Messages.
	 */
	public Consumer initialize( Channel name );

}
