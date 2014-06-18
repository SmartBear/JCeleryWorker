package org.loadui.jcelery.framework;

import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public class MockRabbitInvokeConsumer extends AbstractConsumerMock implements CeleryStarter
{
	private final String START_JOB = "tasks.controller.start";

	@Override
	public void startJob( String id )
	{
		sendMessage( createSimpleMessage( id, START_JOB, "" ) );

	}

	@Override
	public void startJob( String id, URL json )
	{
		try
		{
			sendMessage( createSimpleMessage( id, START_JOB, urlToString( json ) ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	private String urlToString( URL url ) throws IOException{
		return Resources.toString( url, Charset.defaultCharset() );
	}

	private String createSimpleMessage( String id, String label, String arg )
	{
		return "{\"id\": \"" + id + "\", \"task\": \"" + label + "\", \"args\": [\"" + arg + "\"]}";
	}


}
