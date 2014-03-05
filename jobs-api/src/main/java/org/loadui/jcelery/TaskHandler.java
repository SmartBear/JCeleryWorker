package org.loadui.jcelery;

import java.util.EventListener;

public interface TaskHandler<T extends Job> extends EventListener
{
	void handle( T t ) throws Exception;
}
