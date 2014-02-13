package org.loadui.jcelery;

import com.google.common.util.concurrent.Service;

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
