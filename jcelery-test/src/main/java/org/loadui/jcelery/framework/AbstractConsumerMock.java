package org.loadui.jcelery.framework;

import com.rabbitmq.client.*;
import org.loadui.jcelery.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

public abstract class AbstractConsumerMock implements MessageConsumer, MessageControl
{
	private final LinkedBlockingQueue<QueueingConsumer.Delivery> messages;

	Logger log = LoggerFactory.getLogger( AbstractConsumerMock.class );

	public AbstractConsumerMock()
	{
		messages = new LinkedBlockingQueue<>();
	}

	@Override
	public QueueingConsumer.Delivery nextMessage()
	{
		return nextMessage( 5000 );
	}

	@Override
	public QueueingConsumer.Delivery nextMessage( int timeout )
	{
		try
		{
			return messages.poll( timeout, TimeUnit.MILLISECONDS );
		}
		catch( InterruptedException e )
		{
			throw new RuntimeException ( "Unable to take message, was interrupted" );
		}
	}

	@Override
	public void sendMessage( String message )
	{
		try
		{
			messages.put( new QueueingConsumer.Delivery( mock( Envelope.class ), new AMQP.BasicProperties(), message.getBytes() ) );
		}
		catch( InterruptedException e )
		{
			throw new RuntimeException( "Unable to send message: " + message, e );
		}
	}

	@Override
	public int sizeOfQueue(){
		return messages.size();
	}

	@Override
	public boolean isQueueEmpty(){
		return messages.isEmpty();
	}

	@Override
	public void clearQueue(){
		messages.clear();
	}

	@Override
	public Consumer getConsumer()
	{
		return mock( Consumer.class );
	}
}
