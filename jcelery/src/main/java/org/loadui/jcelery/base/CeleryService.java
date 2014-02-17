package org.loadui.jcelery.base;

import org.loadui.jcelery.JobService;
import org.loadui.jcelery.TaskHandler;
import org.loadui.jcelery.worker.MethodWorker;
import org.loadui.jcelery.worker.RevokeWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CeleryService implements JobService
{
	List<Worker> workers;

	private Worker revokeWorker;
	private Worker jobWorker;

	public CeleryService() throws IOException
	{
		this( "localhost" );

	}

	public CeleryService( String host )
	{
		workers = new ArrayList<>();
		revokeWorker = new RevokeWorker( host );
		jobWorker = new MethodWorker( host );

		workers.add( jobWorker );
		workers.add( revokeWorker );
		System.out.println( "CeleryService Initialized" );
	}

	@Override
	public void setJobHandler( TaskHandler handler )
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
		for( Worker worker : workers )
		{
			worker.startAsynchronous();
			worker.waitUntilRunning();
		}
	}

	@Override
	public void stopService()
	{
		for( Worker worker : workers )
		{
			worker.stopAsynchronous();
			worker.waitUntilTerminated();
		}
	}


}
