package org.loadui.jcelery;


public enum Exchange
{
	CELERY( "celery" ),
	RESULTS("celeryresults"),
	EVENT( "celeryev" );

	private String queue;

	private Exchange( String queue )
	{
		this.queue = queue;
	}

	public String getExchange()
	{
		return queue;
	}
}