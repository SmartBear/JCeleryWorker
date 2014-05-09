package org.loadui.jcelery.base;

import org.loadui.jcelery.ConnectionProvider;
import org.loadui.jcelery.JobService;
import org.loadui.jcelery.MessageConsumer;
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


	public CeleryService( InvokeWorker invoker, RevokeWorker revoker )
	{
		this.workers = new ArrayList<>();
		this.revokeWorker = revoker;
		this.invokeWorker = invoker;
		this.workers.add( invokeWorker );
		this.workers.add( revokeWorker );

	}

	public CeleryService( String host, int port, String username, String password, String vhost  )
	{
		this( new InvokeWorker( host, port, username, password, vhost ), new RevokeWorker( host, port, username, password, vhost ) );
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

	@Override
	public void replaceConnection( ConnectionProvider provider, MessageConsumer invokeConsumer, MessageConsumer revokeConsumer )
	{
		stopService();

		TaskHandler<?> revokeHandler = revokeWorker.getTaskHandler();
		TaskHandler<?> invokeHandler = invokeWorker.getTaskHandler();

		workers.clear();

		invokeWorker = new InvokeWorker( provider, invokeConsumer );
		revokeWorker = new RevokeWorker( provider, revokeConsumer );

		invokeWorker.setTaskHandler( invokeHandler );
		revokeWorker.setTaskHandler( revokeHandler );

		workers.add( invokeWorker );
		workers.add( revokeWorker );

		startService();
	}
}
