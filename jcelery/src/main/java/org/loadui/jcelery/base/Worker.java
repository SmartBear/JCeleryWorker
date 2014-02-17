package org.loadui.jcelery.base;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.loadui.jcelery.TaskHandler;

import java.io.IOException;

public abstract class Worker extends AbstractExecutionThreadService
{
	protected TaskHandler onTask;
	protected Connection connection;
	protected Channel channel;
	protected String host;
	private Queue queue;
	private Exchange exchange;

	public Worker( String host, Queue queue, Exchange exchange )
	{
		this.host = host;
		this.queue = queue;
		this.exchange = exchange;
	}

	protected void createConnectionIfRequired() throws IOException
	{
		if( connection == null && host != null )
		{
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost( host );
			connection = factory.newConnection();
		}
	}

	public void setTaskHandler( TaskHandler<?> handler )
	{
		this.onTask = handler;
	}

	Worker startAsynchronous()
	{
		startAsync();
		return this;
	}

	Worker waitUntilRunning()
	{
		awaitRunning();
		return this;
	}

	Worker stopAsynchronous()
	{
		stopAsync();
		return this;
	}

	Worker waitUntilTerminated()
	{
		awaitTerminated();
		return this;
	}

	public abstract void respond( String id, String response ) throws IOException;

	protected abstract void run() throws Exception;

	public String getQueue()
	{
		return queue.getQueue();
	}

	public String getExchange()
	{
		return exchange.getExchange();
	}
}
