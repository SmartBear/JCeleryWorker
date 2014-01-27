package org.loadui.jcelery;

import java.io.IOException;

/**
 * @author renato
 */
public interface JobService
{

	void setTaskHandler( TaskHandler handler );

	JobService 	startAsynchronous();

	JobService waitUntilRunning();

	JobService stopAsynchronous();

	JobService waitUntilTerminated();

	boolean isRunning();

	void respond(String id, String response) throws IOException;

}
