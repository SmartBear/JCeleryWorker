package org.loadui.jcelery.api;

import org.loadui.jcelery.api.Task;

import java.util.EventListener;

public interface TaskHandler<T extends Task> extends EventListener
{
	void handle( T t ) throws Exception;
}
