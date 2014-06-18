package org.loadui.jcelery.test;

import com.rabbitmq.client.Channel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.loadui.jcelery.ConsumerProvider;
import org.loadui.jcelery.Job;
import org.loadui.jcelery.MessageConsumer;
import org.loadui.jcelery.TaskHandler;
import org.loadui.jcelery.base.CeleryService;
import org.loadui.jcelery.errorhandling.RevokeException;
import org.loadui.jcelery.framework.MockRabbitInvokeConsumer;
import org.loadui.jcelery.framework.MockRabbitRevokeConsumer;
import org.loadui.jcelery.framework.mock.MockRabbitProvider;
import org.loadui.jcelery.tasks.InvokeJob;
import org.loadui.jcelery.tasks.RevokeJob;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.loadui.jcelery.utils.JobUtils.*;

public class TaskManagementTest
{
	private CeleryService celeryService;
	private MockRabbitInvokeConsumer invoker = new MockRabbitInvokeConsumer();
	private MockRabbitRevokeConsumer revoker = new MockRabbitRevokeConsumer();
	private Job currentJob;

	@Before
	public void setup() throws InterruptedException
	{
		celeryService = new CeleryService( new MockRabbitProvider(), new ConsumerProvider()
		{
			@Override
			public MessageConsumer getInvokeConsumer( Channel channel )
			{
				return invoker;
			}

			@Override
			public MessageConsumer getRevokeConsumer( Channel channel )
			{
				return revoker;
			}

			@Override
			public void replaceMessageConsumer( MessageConsumer invoker, MessageConsumer revoker )
			{

			}
		} );

		celeryService.setInvokeHandler( new TaskHandler<InvokeJob>()
		{
			@Override
			public void handle( InvokeJob job ) throws IOException, InterruptedException
			{
				currentJob = job;
				currentJob.start();
			}
		} );

		celeryService.setRevokeHandler( new TaskHandler<RevokeJob>()
		{
			@Override
			public void handle( RevokeJob job ) throws IOException
			{
				if( currentJob == null ){
					throw new RevokeException( "No job is running, nothing may be revoked" );
				}
				if( currentJob.getId().equals( job.getId() )){
					currentJob.revoke();
					job.revoke();
				}
			}
		} );

		celeryService.startService();
	}

	@After
	public void setdown()
	{
		if( currentJob != null){
			currentJob.revoke();
		}
		invoker.clearQueue();
		revoker.clearQueue();
		celeryService.stopService();
	}

	@Test
	public void shouldHandleBasicTask() throws InterruptedException
	{
		invoker.sendMessage( startJob( "JOB" ) );
		waitForTestToStart();
	}

	@Test
	public void whileTestIsInProgressAdditionalJobsAreNotPickedUp() throws InterruptedException
	{
		invoker.startJob( "FIRST JOB" );
		waitForTestToStart();

		invoker.startJob( startJob( "SECOND JOB" ) );
		assertThat( "Work queue is empty because the second job was retrieved during another tests execution", invoker.isQueueEmpty(), is( false ) );

		revoker.stopJob( "FIRST JOB" );
		waitForTestToStop( currentJob );

		assertThat( "Work queue still has the second job in the queue", invoker.isQueueEmpty(), is( true )  );
	}

	private void waitForTestToStart( ) throws InterruptedException{

		waitUntil( "Waiting for test to start", new Callable<Boolean>(){
        	@Override
			public Boolean call() throws Exception
			{
				return currentJob != null && currentJob.isInProgress();
			}
		}, 10, TimeUnit.SECONDS );
	}

	private void waitForTestToStop( final Job job ) throws InterruptedException{
		waitUntil( "Waiting for test to stop", new Callable<Boolean>()
		{
			@Override
			public Boolean call() throws Exception
			{
				return currentJob != null && !job.isInProgress();
			}
		}, 10, TimeUnit.SECONDS );
	}

	private void waitUntilServiceIsRunning() throws InterruptedException{
		waitUntil( "Waiting for service to start", new Callable<Boolean>(){
			@Override
			public Boolean call() throws Exception
			{
				return celeryService.isServiceRunning();
			}
		}, 10, TimeUnit.SECONDS );
	}
}
