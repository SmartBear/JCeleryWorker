package org.loadui.jcelery.base;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.rabbitmq.client.*;
import org.loadui.jcelery.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

public abstract class AbstractWorker extends AbstractExecutionThreadService
{
	protected TaskHandler onJob;
	private Connection connection;
	private Channel channel;
	private ConnectionProvider connectionProvider;
	private final Queue queue;
	private final Exchange exchange;
	private MessageConsumer consumer;
	private Logger log = LoggerFactory.getLogger( this.getClass() );

	private static final boolean AMQP_INITIATED_BY_APPLICATION = true;
	private static final boolean AMQP_HARD_ERROR = true;
	private static final boolean AMQP_REQUEUE = true;
	private static final boolean AMQP_DURABLE = true;
	private static final boolean AMQP_EXCLUSIVE = false;
	private static final boolean AMQP_AUTO_DELETE = false;
	private static final boolean AMQP_AUTO_ACK = false;

	public AbstractWorker( ConnectionProvider connectionProvider,
								  Queue queue, Exchange exchange )
	{
		this.connectionProvider = connectionProvider;
		this.queue = queue;
		this.exchange = exchange;
	}

	public AbstractWorker( String host, int port, String username, String password, String vhost,
								  Queue queue, Exchange exchange )
	{
		this( new RabbitProvider( host, port, username, password, vhost ), queue, exchange );
	}

	protected void connect() throws IOException, ShutdownSignalException
	{
      if( consumer != null && consumer.isMock() )
		{
			connection = connectionProvider.getFactory().newConnection();
			channel = connection.createChannel();
			return;
		}

		log.debug( "Connecting to rabbitMQ broker: " + connectionProvider.getFactory().getHost() + ":" + connectionProvider.getFactory().getPort() );

		if( connection == null )
		{
			connection = connectionProvider.getFactory().newConnection();
			channel = connection.createChannel();
			consumer = new RabbitConsumer( channel );
		}
		else
		{
			channel.abort();
			channel = connection.createChannel();
			consumer = new RabbitConsumer( channel );
			channel.basicRecover( true );
		}
		channel.queueDeclare( getQueue(), AMQP_DURABLE, AMQP_EXCLUSIVE, AMQP_AUTO_DELETE, new HashMap<String, Object>() );
		channel.basicConsume( getQueue(), AMQP_AUTO_ACK, consumer.getConsumer() );
	}


	public void replaceConnection( MessageConsumer consumer )
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

	public void respond( String id, String response ) throws IOException
	{
		log.debug( getClass().getSimpleName() + ": Trying to respond " + response + " for job " + id );
		String rabbitId = id.replaceAll( "-", "" );

		AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().contentType( "application/json" ).build();
		channel.exchangeDeclare( getExchange(), "direct", false, false, null );
		channel.basicPublish( getExchange(), rabbitId, properties, response.getBytes() );
	}

	private void nackMessage( QueueingConsumer.Delivery delivery )
	{
		try
		{
			getChannel().basicNack( delivery.getEnvelope().getDeliveryTag(), false, true );
		}
		catch( IOException e )
		{
			log.error( "unable to nack message" );
		}
	}

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

	protected void waitAndRecover( int timeout ) throws InterruptedException, IOException
	{
		Thread.sleep( timeout );
		log.debug( "Attempting connection recovery" );
		connect();
	}

	protected abstract void initialConnection() throws InterruptedException;
}
