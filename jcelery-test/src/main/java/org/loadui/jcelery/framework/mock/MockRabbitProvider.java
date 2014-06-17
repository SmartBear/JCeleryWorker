package org.loadui.jcelery.framework.mock;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.loadui.jcelery.ConnectionProvider;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MockRabbitProvider implements ConnectionProvider, CeleryReceiver
{
	private ConnectionFactory connectionFactory;
	private MockChannel channel;

	Logger log = LoggerFactory.getLogger( MockRabbitProvider.class );

	public MockRabbitProvider()
	{
		initializeConnectionFactoryMock();
	}

	private void initializeConnectionFactoryMock()
	{
		connectionFactory = Mockito.mock( ConnectionFactory.class );
		try
		{
			Connection conn = createConnectionMock();
			Mockito.when( connectionFactory.newConnection() ).thenReturn( conn );
			Mockito.when( connectionFactory.getHost() ).thenReturn( "localhost" );
			Mockito.when( connectionFactory.getPort() ).thenReturn( 808080 );
		}
		catch( IOException e )
		{
			log.error( "cannot create connection mock" );
		}
	}

	private MockChannel createChannelMock()
	{

		return new MockChannel();
	}

	private Connection createConnectionMock()
	{
		try
		{
			channel = createChannelMock();
			Connection conn = Mockito.mock( Connection.class );
			Mockito.when( conn.createChannel() ).thenReturn( channel );
			return conn;
		}
		catch( IOException e )
		{
			return Mockito.mock( Connection.class );
		}
	}


	@Override
	public ConnectionFactory getFactory()
	{
		return connectionFactory;
	}

	@Override
	public String nextDataMessage()
	{
		return channel.nextDataMessage();
	}

	@Override
	public String nextDataMessage( int timeoutInSeconds )
	{
		return channel.nextDataMessage( timeoutInSeconds );
	}

	@Override
	public boolean hasDataMessage()
	{
		return channel.hasDataMessage();
	}

	@Override
	public void clearData()
	{
		channel.clearData();
	}

	@Override
	public void waitForControlMessage( int timeoutInSeconds, String containingString )
	{
		while( channel.hasControlMessage() )
		{
			String msg = channel.nextControlMessage();
			System.out.println("message:" + msg);
			if( msg.contains( containingString ) )
			{
				return;
			}
		}
		String msg = channel.nextControlMessage( timeoutInSeconds );
		if( msg == null )
		{
			throw new RuntimeException( "Did not get any message containing String '" +
					containingString + "' within the timeout" );
		}
	}

	@Override
	public boolean hasControlMessage()
	{
		return channel.hasControlMessage();
	}

	@Override
	public void clearControl()
	{
		channel.clearControl();
	}

}
