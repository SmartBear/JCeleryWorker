package org.loadui.jcelery.base;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.loadui.jcelery.Exchange;
import org.loadui.jcelery.Queue;
import org.loadui.jcelery.TaskHandler;

import java.io.IOException;

public abstract class AbstractWorker extends AbstractExecutionThreadService
{
	protected TaskHandler onJob;
	private Connection connection;
	private Channel channel;
	private ConnectionFactory connectionFactory;
	private Queue queue;
	private Exchange exchange;

	public AbstractWorker( ConnectionFactory connectionFactory,
								  Queue queue, Exchange exchange )
	{
		this.connectionFactory = connectionFactory;
		this.queue = queue;
		this.exchange = exchange;
	}

	public AbstractWorker( String host,
								  Queue queue, Exchange exchange )
	{
		this( new ConnectionFactory(), queue, exchange );
		this.connectionFactory.setHost( host );
	}

	protected void createConnectionIfRequired() throws IOException
	{
		if( getConnection() == null && getConnectionFactory() != null )
		{
			connection = connectionFactory.newConnection();
			channel = connection.createChannel();
		}
	}

	public void setTaskHandler( TaskHandler<?> handler )
	{
		this.onJob = handler;
	}

	AbstractWorker startAsynchronous()
	{
		startAsync();
		return this;
	}

	AbstractWorker waitUntilRunning()
	{
		awaitRunning();
		return this;
	}

	AbstractWorker stopAsynchronous()
	{
		stopAsync();
		return this;
	}

	AbstractWorker waitUntilTerminated()
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

	public Channel getChannel()
	{
		return channel;
	}

	public Connection getConnection()
	{
		return connection;
	}

	public ConnectionFactory getConnectionFactory()
	{
		return connectionFactory;
	}
}
