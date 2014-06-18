package org.loadui.jcelery.tasks;

import org.loadui.jcelery.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by server on 2014-06-18.
 */
public abstract class AbstractJob implements Job
{
	private boolean inProgress = false;
	protected Logger log = LoggerFactory.getLogger( this.getClass() );

	private void setInProgress( boolean state )
	{
		inProgress = state;
	}

	@Override
	public void complete()
	{
		complete( "" );
	}

	@Override
	public void complete( Object message )
	{
		setInProgress( false );
		respond( Status.SUCCESS, message );
	}


	@Override
	public void start()
	{
		setInProgress( true );
		respond( Status.STARTED );
	}

	@Override
	public void revoke()
	{
		setInProgress( false );
		respond( Status.REVOKED );
	}

	@Override
	public void fail( String reason )
	{
		setInProgress( false );
		respond( Status.FAILURE, reason );
	}

	protected abstract void respond( Status status, Object response );
	protected abstract void respond( Status status );

	@Override
	public boolean isInProgress()
	{
		return inProgress;
	}
}
