package org.loadui.jcelery;

import com.rabbitmq.client.ConnectionFactory;

public interface ConnectionProvider
{
	public ConnectionFactory getFactory();
}
