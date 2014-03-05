package org.loadui.jcelery;

/**
 * @author renato
 */
public interface JobService
{
	void setInvokeHandler( TaskHandler<?> handler );

	void setRevokeHandler( TaskHandler<?> handler );

	void startService();

	void stopService();

	void replaceConnection( ConnectionProvider provider, MessageConsumer invokeConsumer, MessageConsumer revokeConsumer );
}
