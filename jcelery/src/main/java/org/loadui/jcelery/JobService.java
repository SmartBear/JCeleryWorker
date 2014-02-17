package org.loadui.jcelery;

/**
 * @author renato
 */
public interface JobService
{
	void setJobHandler( TaskHandler<?> handler );

	void setRevokeHandler( TaskHandler<?> handler );

	void startService();

	void stopService();
}
