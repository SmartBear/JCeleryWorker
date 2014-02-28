package org.loadui.jcelery.base;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.loadui.jcelery.*;

import java.io.IOException;

public abstract class AbstractWorker extends AbstractExecutionThreadService
{
	protected TaskHandler onJob;
	private Connection connection;
	private Channel channel;
	private final ConnectionProvider connectionFactory;
	private final Queue queue;
	private final Exchange exchange;
	private final MessageConsumer consumer;

	public AbstractWorker( ConnectionProvider connectionFactory, MessageConsumer consumer,
								  Queue queue, Exchange exchange )
	{
		this.connectionFactory = connectionFactory;
		this.queue = queue;
		this.exchange = exchange;
		this.consumer = consumer;
	}

	public AbstractWorker( String host,
								  Queue queue, Exchange exchange )
	{
		this( new RabbitProvider( host ), new RabbitConsumer(), queue, exchange );
	}

	protected void createConnectionIfRequired() throws IOException
	{
		if( getConnection() == null && getConnectionFactory() != null )
		{
			connection = connectionFactory.getFactory().newConnection();
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

	public MessageConsumer getMessageConsumer()
	{
		return consumer;
	}

	public Connection getConnection()
	{
		return connection;
	}

	public ConnectionProvider getConnectionFactory()
	{
		return connectionFactory;
	}
}
