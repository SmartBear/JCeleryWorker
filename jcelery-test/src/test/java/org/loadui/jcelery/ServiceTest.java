package org.loadui.jcelery;

import com.rabbitmq.client.Channel;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.loadui.jcelery.base.CeleryService;
import org.loadui.jcelery.tasks.InvokeJob;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ServiceTest
{


	private CeleryService celeryService;
	private MessageConsumerMock invokeConsumer = new MessageConsumerMock();
	private MessageConsumerMock revokeConsumer = new MessageConsumerMock();
	private boolean messageHandled;

	@Before
	public void setup() throws InterruptedException
	{
		celeryService = new CeleryService( new MockRabbitProvider(), new ConsumerProvider()
		{

			@Override
			public MessageConsumer getInvokeConsumer( Channel channel )
			{
				return invokeConsumer;
			}

			@Override
			public MessageConsumer getRevokeConsumer( Channel channel )
			{
				return revokeConsumer;
			}

			@Override
			public void replaceMessageConsumer( MessageConsumer invoker, MessageConsumer revoker )
			{

			}
		} );

		celeryService.setInvokeHandler( new TaskHandler<InvokeJob>()
		{
			@Override
			public void handle( InvokeJob t ) throws IOException, InterruptedException
			{
				System.out.println( "invoke: " + t.getMethod() );
				t.complete( Job.Status.SUCCESS );
			}
		} );

		celeryService.setRevokeHandler( new TaskHandler<InvokeJob>()
		{
			@Override
			public void handle( InvokeJob t ) throws IOException
			{
				switch( t.getMethod() )
				{
					case "revoke":
						t.complete( Job.Status.REVOKED, "" );
				}
			}
		} );

		celeryService.startService();

		Thread.sleep( 1000 );
		System.out.println( "service running?" + celeryService.isServiceRunning() );
	}

	@After
	public void setdown()
	{
		celeryService.stopService();
	}


	@Test
	public void shouldHandleTask() throws InterruptedException
	{
		messageHandled = false;

		celeryService.setInvokeHandler( new TaskHandler<Job>()
		{
			@Override
			public void handle( Job job ) throws Exception
			{
				messageHandled = true;
			}
		} );

		invokeConsumer.sendMessage( TestUtils.createStartMessage( "ID123" ) );

		Thread.sleep( 1000 );

		assertThat( "The message should be handled", messageHandled, is( true ) );
	}

	@Ignore
	@Test
	public void completingJobWithStartedShouldBlockAnyOtherMessages() throws InterruptedException
	{
		celeryService.setInvokeHandler( new TaskHandler<Job>()
		{
			@Override
			public void handle( Job job ) throws Exception
			{
				job.complete( Job.Status.STARTED );
			}
		} );

		invokeConsumer.sendMessage( TestUtils.createStartMessage( "ID123" ) );

		Thread.sleep( 1000 );

		assertThat( "The message should be handled", messageHandled, is( true ) );
	}
}
