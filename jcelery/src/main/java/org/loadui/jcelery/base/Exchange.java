package org.loadui.jcelery.base;


public enum Exchange
{
	RESULTS( "celeryresults" ),
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