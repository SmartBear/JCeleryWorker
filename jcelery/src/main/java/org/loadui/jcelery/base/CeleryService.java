package org.loadui.jcelery.base;

import org.loadui.jcelery.JobService;
import org.loadui.jcelery.TaskHandler;
import org.loadui.jcelery.worker.InvokeWorker;
import org.loadui.jcelery.worker.RevokeWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CeleryService implements JobService
{
	private List<AbstractWorker> workers;

	private AbstractWorker revokeWorker;
	private AbstractWorker jobWorker;

	public CeleryService() throws IOException
	{
		this( "localhost" );
	}

	public CeleryService( String host )
	{
		workers = new ArrayList<>();
		revokeWorker = new RevokeWorker( host );
		jobWorker = new InvokeWorker( host );

		workers.add( jobWorker );
		workers.add( revokeWorker );
	}

	@Override
	public void setInvokeHandler( TaskHandler handler )
	{
		this.jobWorker.setTaskHandler( handler );
	}

	@Override
	public void setRevokeHandler( TaskHandler<?> handler )
	{
		this.revokeWorker.setTaskHandler( handler );
	}

	@Override
	public void startService()
	{
		for( AbstractWorker worker : workers )
		{
			worker.startAsynchronous();
			worker.waitUntilRunning();
		}
	}

	@Override
	public void stopService()
	{
		for( AbstractWorker worker : workers )
		{
			worker.stopAsynchronous();
			worker.waitUntilTerminated();
		}
	}
}
