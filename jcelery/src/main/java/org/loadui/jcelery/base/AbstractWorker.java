package org.loadui.jcelery.base;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.loadui.jcelery.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;

public abstract class AbstractWorker extends AbstractExecutionThreadService
{
	protected TaskHandler onJob;
	private Connection connection;
	private Channel channel;
	private ConnectionProvider connectionProvider;
	private final Queue queue;
	private final Exchange exchange;
	private MessageConsumer consumer;
	private ExecutorService executor;

	Logger log = LoggerFactory.getLogger( AbstractWorker.class );

	public AbstractWorker( ConnectionProvider connectionProvider, MessageConsumer consumer,
								  Queue queue, Exchange exchange )
	{
		this.connectionProvider = connectionProvider;
		this.queue = queue;
		this.exchange = exchange;
		this.consumer = consumer;
		this.executor = Executors.newSingleThreadExecutor();
	}

	public AbstractWorker( String host, int port, String username, String password,
								  Queue queue, Exchange exchange )
	{
		this( new RabbitProvider( host, port, username, password ), new RabbitConsumer(), queue, exchange );
	}

	protected void createConnectionIfRequired() throws IOException
	{
		if( ( getConnection() == null && getConnectionProvider() != null ) )
		{
			Callable<Connection> task = new Callable<Connection>()
			{
				@Override
				public Connection call() throws Exception
				{
					return connectionProvider.getFactory().newConnection();
				}
			};

			Future<Connection> future = executor.submit( task );
			try
			{
				connection = future.get( 3, TimeUnit.SECONDS );
				channel = connection.createChannel();
				consumer.initialize( channel );
			}
			catch( Exception e )
			{
				channel = null;
				connection = null;
				stopAsync();
			}
		}else if( getConnectionProvider() == null){
			log.error( "Attempted to open a new connection, but could not find a connection-provider" );
		}
	}

	public void setConnectionProvider( ConnectionProvider provider )
	{
		this.connectionProvider = provider;
	}

	public void setMessageConsumer( MessageConsumer consumer )
	{
		this.consumer = consumer;
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

	public TaskHandler<?> getTaskHandler()
	{
		return onJob;
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

	public ConnectionProvider getConnectionProvider()
	{
		return connectionProvider;
	}


	protected final int DEFAULT_TIMEOUT = 2000;
	protected void waitAndReconnect()
	{
		try
		{
			Thread.sleep( DEFAULT_TIMEOUT );
			connection = null;
			createConnectionIfRequired();
		}
		catch( InterruptedException e )
		{
			//Silent
		}catch( IOException e ){
			log.error( "Unable to create connection to broker. ",e );
		}


	}
}
