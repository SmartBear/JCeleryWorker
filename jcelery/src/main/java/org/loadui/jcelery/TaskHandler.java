package org.loadui.jcelery;

import java.io.IOException;
import java.util.EventListener;

public interface TaskHandler extends EventListener
{
	void handle(CeleryTask e) throws Exception;
}
