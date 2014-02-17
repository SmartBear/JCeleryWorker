package org.loadui.jcelery.base;

public enum Queue
{

	REVOKE( "celery.pidbox" ),
	CELERY( "celery" );

	private String queue;

	private Queue( String queue ){
		this.queue = queue;
	}

	public String getQueue()
	{
		return queue;
	}
}