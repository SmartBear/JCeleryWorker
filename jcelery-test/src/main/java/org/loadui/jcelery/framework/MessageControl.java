package org.loadui.jcelery.framework;

public interface MessageControl
{
	int sizeOfQueue();

	boolean isQueueEmpty();

	void clearQueue();

	void sendMessage( String message );

}
