package org.loadui.jcelery.internal;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.rabbitmq.client.*;
import org.loadui.jcelery.JobService;
import org.loadui.jcelery.Status;
import org.loadui.jcelery.TaskHandler;

import java.io.IOException;

public class CeleryService extends AbstractExecutionThreadService implements JobService
{
	private final static String QUEUE_NAME = "celery";

	private Status status = Status.PENDING;
	private TaskHandler onTask;
	private Connection connection;
	private Channel channel;

	@Override
	public void setTaskHandler( TaskHandler handler )
	{
		this.onTask = handler;
	}

	public CeleryService() throws IOException {
		this( "localhost" );
	}

	public CeleryService( String host ) throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        connection = factory.newConnection();
	}

    public CeleryService( Connection connection )
    {
        this.connection = connection;
    }

	public void respond(String id, String response) throws IOException
	{
		Channel responseChannel = connection.createChannel();

		String RESPONSE_QUEUE = id.replace( "-", "" );
		responseChannel.queueDeclare( RESPONSE_QUEUE, true, false, true, ImmutableMap.of("x-expires", (Object) 86400000) );
		responseChannel.queueBind( RESPONSE_QUEUE, "celeryresults", RESPONSE_QUEUE );

		AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType( "application/json" ).build();

		channel.basicPublish( "celeryresults", RESPONSE_QUEUE, props, response.getBytes() );
		System.out.println( "Responded to task: " + response );
	}

	@Override
	protected void run() throws Exception
	{
		channel = connection.createChannel();

		channel.queueDeclare( QUEUE_NAME, true, false, false, null );
		System.out.println( "Waiting for tasks from host " + connection.getAddress() + "." );

		QueueingConsumer consumer = new QueueingConsumer( channel );
		channel.basicConsume( QUEUE_NAME, true, consumer );

		while (isRunning()) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());

			System.out.println(delivery.getEnvelope().getRoutingKey()+" @ " + delivery.getEnvelope().getExchange() + " @ " + delivery.getEnvelope().getDeliveryTag()+ " @ "+ message);


			CeleryTask task = CeleryTask.fromJson( message, this );

			if(onTask != null)
			{
				onTask.handle( task ); // This is blocking!
			}

		}
	}

	@Override
	public CeleryService startAsynchronous() {
		startAsync();
		return this;
	}

	@Override
	public CeleryService waitUntilRunning() {
		awaitRunning();
		return this;
	}

	@Override
	public CeleryService stopAsynchronous() {
		stopAsync();
		return this;
	}

	@Override
	public CeleryService waitUntilTerminated() {
		awaitTerminated();
		return this;
	}

}
