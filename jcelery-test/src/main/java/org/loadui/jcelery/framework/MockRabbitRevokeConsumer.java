package org.loadui.jcelery.framework;

public class MockRabbitRevokeConsumer extends AbstractConsumerMock implements CeleryStopper
{
	@Override
	public void stopJob( String id )
	{
		sendMessage( "{\"method\": \"revoke\", \"arguments\": {\"task_id\": \"" + id + "\"}}" );
	}

}
