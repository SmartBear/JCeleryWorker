package org.loadui.jcelery;

public enum Queue
{
	REVOKE( "celery.pidbox" ),
	CELERY( "celery" );

	private String queue;

	private Queue( String queue )
	{
		this.queue = queue;
	}

	public String getQueue()
	{
		return queue;
	}

}