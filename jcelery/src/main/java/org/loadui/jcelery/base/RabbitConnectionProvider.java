package org.loadui.jcelery.base;

import com.rabbitmq.client.ConnectionFactory;
import org.loadui.jcelery.ConnectionProvider;

public class RabbitConnectionProvider implements ConnectionProvider
{
	private ConnectionFactory connectionFactory;

	public RabbitConnectionProvider( String host, int port, String username, String password, String vhost )
	{
		this.connectionFactory = new ConnectionFactory();
		this.connectionFactory.setConnectionTimeout( 2000 );
		this.connectionFactory.setVirtualHost( vhost );
		this.connectionFactory.setHost( host );
		this.connectionFactory.setPort( port );
		this.connectionFactory.setUsername( username );
		this.connectionFactory.setPassword( password );
		this.connectionFactory.setAutomaticRecoveryEnabled( true );
		this.connectionFactory.setTopologyRecoveryEnabled( true );
	}

	public ConnectionFactory getFactory()
	{
		return connectionFactory;
	}
}
