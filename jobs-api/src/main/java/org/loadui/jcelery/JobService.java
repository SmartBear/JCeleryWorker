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

	boolean isServiceRunning();

	void replaceConnection( ConnectionProvider provider, ConsumerProvider consumerProvider );
}
