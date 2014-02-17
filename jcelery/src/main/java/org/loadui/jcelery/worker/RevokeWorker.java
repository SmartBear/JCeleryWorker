package org.loadui.jcelery.worker;

import org.loadui.jcelery.base.Exchange;
import org.loadui.jcelery.base.Queue;
import org.loadui.jcelery.base.Worker;

import java.io.IOException;

public class RevokeWorker extends Worker
{
	public RevokeWorker( String host ){
		super( host, Queue.REVOKE, Exchange.RESULTS );
		System.out.println("Waiting for revoke tasks from host: " + host);
	}

	@Override
	public void respond( String id, String response ) throws IOException
	{

	}

	@Override
	protected void run() throws Exception
	{
      while(true){
			Thread.sleep( 2000 );
		}
	}
}
