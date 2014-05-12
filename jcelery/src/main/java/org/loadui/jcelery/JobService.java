package org.loadui.jcelery;

/**
 * @author renato
 */
public interface JobService
{

	void setTaskHandler( TaskHandler<?> handler );

	JobService startAsynchronous();

	JobService waitUntilRunning();

	JobService stopAsynchronous();

	JobService waitUntilTerminated();

	boolean isRunning();

}
