package org.loadui.jcelery.base;

import com.rabbitmq.client.ConnectionFactory;
import org.loadui.jcelery.ConnectionProvider;

public class RabbitProvider implements ConnectionProvider
{
	private ConnectionFactory connectionFactory;

	public RabbitProvider( String host )
	{

		this.connectionFactory = new ConnectionFactory();
		this.connectionFactory.setHost( host );
	}

	public ConnectionFactory getFactory()
	{
		return connectionFactory;
	}
}
