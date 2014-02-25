package org.loadui.jcelery.base;

import org.loadui.jcelery.JobService;
import org.loadui.jcelery.TaskHandler;
import org.loadui.jcelery.worker.InvokeWorker;
import org.loadui.jcelery.worker.RevokeWorker;

import java.util.ArrayList;
import java.util.List;

public class CeleryService implements JobService
{
	private List<AbstractWorker> workers;

	private AbstractWorker revokeWorker;
	private AbstractWorker invokeWorker;


	public CeleryService( InvokeWorker invoker, RevokeWorker revoker ){
		this.workers = new ArrayList<>();
		this.revokeWorker = revoker;
		this.invokeWorker = invoker;
		this.workers.add( invokeWorker );
		this.workers.add( revokeWorker );
	}

	public CeleryService( String host )
	{
		this( new InvokeWorker( host ), new RevokeWorker( host ) );
	}

	public CeleryService( ){
		this( "localhost" );
	}

	@Override
	public void setInvokeHandler( TaskHandler handler )
	{
		this.invokeWorker.setTaskHandler( handler );
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
