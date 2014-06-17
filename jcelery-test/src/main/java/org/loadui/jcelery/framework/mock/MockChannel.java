package org.loadui.jcelery.framework.mock;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.mock;

public class MockChannel implements Channel
{
	public static final String CELERYPIDBOX = "celery.pidbox";
	public static final String CELERYRESULTS = "celeryresults";
	private LinkedBlockingQueue<String> control = new LinkedBlockingQueue<>();
	private LinkedBlockingQueue<String> data = new LinkedBlockingQueue<>();
	private String boundQueue = "";
	private String boundExchange = "";
	private String boundRoutingKey = "";


	private Logger log = LoggerFactory.getLogger( Channel.class );

	@Override
	public int getChannelNumber()
	{
		return 0;
	}

	@Override
	public Connection getConnection()
	{
		return mock( Connection.class );
	}

	@Override
	public void close() throws IOException
	{

	}

	@Override
	public void close( int i, String s ) throws IOException
	{

	}

	@Override
	public boolean flowBlocked()
	{
		return false;
	}

	@Override
	public void abort() throws IOException
	{

	}

	@Override
	public void abort( int i, String s ) throws IOException
	{

	}

	@Override
	public void addReturnListener( ReturnListener returnListener )
	{

	}

	@Override
	public boolean removeReturnListener( ReturnListener returnListener )
	{
		return false;
	}

	@Override
	public void clearReturnListeners()
	{

	}

	@Override
	public void addFlowListener( FlowListener flowListener )
	{

	}

	@Override
	public boolean removeFlowListener( FlowListener flowListener )
	{
		return false;
	}

	@Override
	public void clearFlowListeners()
	{

	}

	@Override
	public void addConfirmListener( ConfirmListener confirmListener )
	{

	}

	@Override
	public boolean removeConfirmListener( ConfirmListener confirmListener )
	{
		return true;
	}

	@Override
	public void clearConfirmListeners()
	{

	}

	@Override
	public Consumer getDefaultConsumer()
	{
		return mock( Consumer.class );
	}

	@Override
	public void setDefaultConsumer( Consumer consumer )
	{

	}

	@Override
	public void basicQos( int i, int i2, boolean b ) throws IOException
	{

	}

	@Override
	public void basicQos( int prefetchCount, boolean global ) throws IOException
	{

	}

	@Override
	public void basicQos( int i ) throws IOException
	{

	}

	@Override
	public void basicPublish( String actualExchange, String routingKey, AMQP.BasicProperties basicProperties, byte[] bytes ) throws IOException
	{
		publish( actualExchange, bytes );
	}

	@Override
	public void basicPublish( String actualExchange, String routingKey, boolean b, AMQP.BasicProperties basicProperties, byte[] bytes ) throws IOException
	{
		publish( actualExchange, bytes );
	}

	@Override
	public void basicPublish( String actualExchange, String routingkey, boolean b, boolean b2, AMQP.BasicProperties basicProperties, byte[] bytes ) throws IOException
	{
		publish( actualExchange, bytes );
	}

	private void publish( String actualExchange, byte[] bytes )
	{
		if( boundExchange != actualExchange )
		{
			boundExchange = actualExchange;
		}
		if( boundExchange == CELERYPIDBOX || boundExchange == CELERYRESULTS )
		{
			saveControlMessage( bytes );
		}
		else
		{
			saveDataMessage( bytes );
		}
	}

	private void saveControlMessage( byte[] bytes )
	{
		try
		{
			log.info( "Saved message: " + new String( bytes ) );
			control.put( new String( bytes ) );
		}
		catch( InterruptedException e )
		{
			log.error( "unable to save message " + new String( bytes ) );
		}
	}

	private void saveDataMessage( byte[] bytes )
	{
		try
		{
			log.info( "Saved message: " + new String ( bytes ) );
			data.put( new String( bytes ) );
		}
		catch( InterruptedException e )
		{
			log.error( "unable to save message " + new String ( bytes ) );
		}
	}


	@Override
	public AMQP.Exchange.DeclareOk exchangeDeclare( String s, String s2 ) throws IOException
	{
		return mock( AMQP.Exchange.DeclareOk.class );
	}

	@Override
	public AMQP.Exchange.DeclareOk exchangeDeclare( String s, String s2, boolean b ) throws IOException
	{
		return mock( AMQP.Exchange.DeclareOk.class );
	}

	@Override
	public AMQP.Exchange.DeclareOk exchangeDeclare( String s, String s2, boolean b, boolean b2, Map<String, Object> stringObjectMap ) throws IOException
	{
		return mock( AMQP.Exchange.DeclareOk.class );
	}

	@Override
	public AMQP.Exchange.DeclareOk exchangeDeclare( String s, String s2, boolean b, boolean b2, boolean b3, Map<String, Object> stringObjectMap ) throws IOException
	{
		return mock( AMQP.Exchange.DeclareOk.class );
	}

	@Override
	public AMQP.Exchange.DeclareOk exchangeDeclarePassive( String s ) throws IOException
	{
		return mock( AMQP.Exchange.DeclareOk.class );
	}

	@Override
	public AMQP.Exchange.DeleteOk exchangeDelete( String s, boolean b ) throws IOException
	{
		return mock( AMQP.Exchange.DeleteOk.class );
	}

	@Override
	public AMQP.Exchange.DeleteOk exchangeDelete( String s ) throws IOException
	{
		return mock( AMQP.Exchange.DeleteOk.class );
	}

	@Override
	public AMQP.Exchange.BindOk exchangeBind( String s, String s2, String s3 ) throws IOException
	{
		return mock( AMQP.Exchange.BindOk.class );
	}

	@Override
	public AMQP.Exchange.BindOk exchangeBind( String s, String s2, String s3, Map<String, Object> stringObjectMap ) throws IOException
	{
		return mock( AMQP.Exchange.BindOk.class );
	}

	@Override
	public AMQP.Exchange.UnbindOk exchangeUnbind( String s, String s2, String s3 ) throws IOException
	{
		return mock( AMQP.Exchange.UnbindOk.class );
	}

	@Override
	public AMQP.Exchange.UnbindOk exchangeUnbind( String s, String s2, String s3, Map<String, Object> stringObjectMap ) throws IOException
	{
		return mock( AMQP.Exchange.UnbindOk.class );
	}

	@Override
	public AMQP.Queue.DeclareOk queueDeclare() throws IOException
	{
		return mock( AMQP.Queue.DeclareOk.class );
	}

	@Override
	public AMQP.Queue.DeclareOk queueDeclare( String s, boolean b, boolean b2, boolean b3, Map<String, Object> stringObjectMap ) throws IOException
	{
		return mock( AMQP.Queue.DeclareOk.class );
	}

	@Override
	public AMQP.Queue.DeclareOk queueDeclarePassive( String s ) throws IOException
	{
		return mock( AMQP.Queue.DeclareOk.class );
	}

	@Override
	public AMQP.Queue.DeleteOk queueDelete( String s ) throws IOException
	{
		return mock( AMQP.Queue.DeleteOk.class );
	}

	@Override
	public AMQP.Queue.DeleteOk queueDelete( String s, boolean b, boolean b2 ) throws IOException
	{
		return mock( AMQP.Queue.DeleteOk.class );
	}

	@Override
	public AMQP.Queue.BindOk queueBind( String q, String e, String rk ) throws IOException
	{
		boundQueue = q;
		boundExchange = e;
		boundRoutingKey = rk;
		return mock( AMQP.Queue.BindOk.class );
	}

	@Override
	public AMQP.Queue.BindOk queueBind( String s, String s2, String s3, Map<String, Object> stringObjectMap ) throws IOException
	{
		return mock( AMQP.Queue.BindOk.class );
	}

	@Override
	public AMQP.Queue.UnbindOk queueUnbind( String s, String s2, String s3 ) throws IOException
	{
		boundExchange = "";
		boundQueue = "";
		boundRoutingKey = "";
		return mock( AMQP.Queue.UnbindOk.class );
	}

	@Override
	public AMQP.Queue.UnbindOk queueUnbind( String s, String s2, String s3, Map<String, Object> stringObjectMap ) throws IOException
	{
		return mock( AMQP.Queue.UnbindOk.class );
	}

	@Override
	public AMQP.Queue.PurgeOk queuePurge( String s ) throws IOException
	{
		return mock( AMQP.Queue.PurgeOk.class );
	}

	@Override
	public GetResponse basicGet( String s, boolean b ) throws IOException
	{
		return mock( GetResponse.class );
	}

	@Override
	public void basicAck( long l, boolean b ) throws IOException
	{
		//noop
	}

	@Override
	public void basicNack( long l, boolean b, boolean b2 ) throws IOException
	{
		//noop
	}

	@Override
	public void basicReject( long l, boolean b ) throws IOException
	{
		//noop
	}

	@Override
	public String basicConsume( String s, Consumer consumer ) throws IOException
	{
		return "";
	}

	@Override
	public String basicConsume( String s, boolean b, Consumer consumer ) throws IOException
	{
		return "";
	}

	@Override
	public String basicConsume( String queue, boolean autoAck, Map<String, Object> arguments, Consumer callback ) throws IOException
	{
		return null;
	}

	@Override
	public String basicConsume( String s, boolean b, String s2, Consumer consumer ) throws IOException
	{
		return "";
	}

	@Override
	public String basicConsume( String s, boolean b, String s2, boolean b2, boolean b3, Map<String, Object> stringObjectMap, Consumer consumer ) throws IOException
	{
		return "";
	}

	@Override
	public void basicCancel( String s ) throws IOException
	{
		//Noop
	}

	@Override
	public AMQP.Basic.RecoverOk basicRecover() throws IOException
	{
		return mock( AMQP.Basic.RecoverOk.class );
	}

	@Override
	public AMQP.Basic.RecoverOk basicRecover( boolean b ) throws IOException
	{
		return mock( AMQP.Basic.RecoverOk.class );
	}

	@Deprecated
	@Override
	public void basicRecoverAsync( boolean b ) throws IOException
	{
		//noop
	}

	@Override
	public AMQP.Tx.SelectOk txSelect() throws IOException
	{
		return mock( AMQP.Tx.SelectOk.class );
	}

	@Override
	public AMQP.Tx.CommitOk txCommit() throws IOException
	{
		return mock( AMQP.Tx.CommitOk.class );
	}

	@Override
	public AMQP.Tx.RollbackOk txRollback() throws IOException
	{
		return mock( AMQP.Tx.RollbackOk.class );
	}

	@Override
	public AMQP.Confirm.SelectOk confirmSelect() throws IOException
	{
		return mock( AMQP.Confirm.SelectOk.class );
	}

	@Override
	public long getNextPublishSeqNo()
	{
		return 0;
	}

	@Override
	public boolean waitForConfirms() throws InterruptedException
	{
		return false;
	}

	@Override
	public boolean waitForConfirms( long l ) throws InterruptedException, TimeoutException
	{
		return false;
	}

	@Override
	public void waitForConfirmsOrDie() throws IOException, InterruptedException
	{
		//noop
	}

	@Override
	public void waitForConfirmsOrDie( long l ) throws IOException, InterruptedException, TimeoutException
	{
		//noop
	}

	@Override
	public void asyncRpc( Method method ) throws IOException
	{
		//noop
	}

	@Override
	public Command rpc( Method method ) throws IOException
	{
		return mock( Command.class );
	}

	@Override
	public void addShutdownListener( ShutdownListener shutdownListener )
	{
		//noop
	}

	@Override
	public void removeShutdownListener( ShutdownListener shutdownListener )
	{
		//noop
	}

	@Override
	public ShutdownSignalException getCloseReason()
	{
		return mock( ShutdownSignalException.class );
	}

	@Override
	public void notifyListeners()
	{
		//noop
	}

	@Override
	public boolean isOpen()
	{
		return true;
	}

	public String nextDataMessage()
	{
		return nextDataMessage( 3 );
	}

	public String nextDataMessage( int timeoutInSeconds )
	{
		try
		{
			String potentialMessage = data.poll( timeoutInSeconds, TimeUnit.SECONDS );
			if( potentialMessage != null)
			{
				return potentialMessage;
			}else{
				return "";
			}
		}
		catch( InterruptedException e )
		{
			log.error( "unable to take message from saved messages." );
		}
		return "";
	}

	public String nextControlMessage()
	{
		return nextControlMessage( 12 );
	}

	public String nextControlMessage( int timeoutInSeconds )
	{
		try
		{
			String potentialMessage = control.poll( timeoutInSeconds, TimeUnit.SECONDS );
			if( potentialMessage != null)
			{
				return new String( potentialMessage );
			}else{
				return "";
			}
		}
		catch( InterruptedException e )
		{
			log.error( "unable to take message from saved messages." );
		}
		return "";
	}

	public boolean hasDataMessage()
	{
		return data.size() > 0;
	}

	public void clearData()
	{
		data.clear();
	}

	public boolean hasControlMessage()
	{
		return control.size() > 0;
	}

	public void clearControl()
	{
		control.clear();
	}
}
