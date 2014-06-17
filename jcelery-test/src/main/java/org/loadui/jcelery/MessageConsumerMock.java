package org.loadui.jcelery;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageConsumerMock implements MessageConsumer
{
	private final LinkedBlockingQueue<QueueingConsumer.Delivery> messages;

	Logger log = LoggerFactory.getLogger( MessageConsumerMock.class );

	public MessageConsumerMock()
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
		System.out.println("polling");
		try
		{
			return messages.poll( timeout, TimeUnit.MILLISECONDS );
		}
		catch( InterruptedException e )
		{
			throw new RuntimeException ( "Unable to take message, was interrupted" );
		}
	}

	public void sendMessage( String message )
	{
		System.out.println("message: " + message);
		try
		{
			messages.put( new QueueingConsumer.Delivery( Mockito.mock( Envelope.class ), new AMQP.BasicProperties(), message.getBytes() ) );
		}
		catch( InterruptedException e )
		{
			throw new RuntimeException( "Unable to send message: " + message, e );
		}
	}

	@Override
	public Consumer getConsumer()
	{
		return Mockito.mock( Consumer.class );
	}


}
