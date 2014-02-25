package org.loadui.jcelery.tasks;

import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Test;
import org.loadui.jcelery.base.AbstractWorker;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class RevokeTest
{
	private RevokeJob job;
	private AbstractWorker worker;

	private String revokeRequest;

	@Before
	public void setup() throws IOException
	{
		worker = mock( AbstractWorker.class );

		revokeRequest = firstLineToString( new File( getClass().getResource( "validRevoke.json" ).getFile() ) );
	}

	private String firstLineToString( File file )
	{
		try
		{
			return Files.readFirstLine( file, Charset.defaultCharset() );
		}
		catch( IOException e )
		{
			return "";
		}
	}

	@Test
	public void revokeJobIsValid()
	{
		job = RevokeJob.fromJson( revokeRequest, worker );
		assertThat( "Revoking a Celery job  has a method", job.getMethod(), is( "revoke" ) );
		assertThat( "Revoking a Celery job still has an ID", job.getId(), is( "0adb664ef6e64fc9b1f5381d4f3852a9" ) );
	}
}
