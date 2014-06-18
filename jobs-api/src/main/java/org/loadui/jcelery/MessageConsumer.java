package org.loadui.jcelery;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.QueueingConsumer;

public interface MessageConsumer
{
	public QueueingConsumer.Delivery nextMessage();

	public QueueingConsumer.Delivery nextMessage( int timeout );

   public Consumer getConsumer();
}
