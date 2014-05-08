package org.loadui.jcelery.base;

import com.rabbitmq.client.ConnectionFactory;
import org.loadui.jcelery.ConnectionProvider;

import static com.google.common.base.Objects.firstNonNull;

public class RabbitProvider implements ConnectionProvider
{
	private ConnectionFactory connectionFactory;

	public RabbitProvider( String host, int port, String username, String password, String vhost  )
	{
		this.connectionFactory = new ConnectionFactory();
		this.connectionFactory.setVirtualHost( vhost );
		this.connectionFactory.setHost( host );
		this.connectionFactory.setPort( port );
		this.connectionFactory.setUsername( username );
		this.connectionFactory.setPassword( password );
	}

	public ConnectionFactory getFactory()
	{
		return connectionFactory;
	}
}
