package org.loadui.jcelery.tasks;

import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Test;
import org.loadui.jcelery.base.AbstractWorker;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;


public class InvokeTest
{
	private InvokeJob job;
	private AbstractWorker worker;

	private String validMinimalInvoke;
	private String validMaximalInvoke;
	private String invalidInvoke;

	@Before
	public void setup() throws IOException
	{
		worker = mock( AbstractWorker.class );

		validMaximalInvoke = firstLineToString( new File( getClass().getResource( "validMaximalInvoke.json" ).getFile() ) );
		validMinimalInvoke = firstLineToString( new File( getClass().getResource( "validMinimalInvoke.json" ).getFile() ) );
		invalidInvoke = firstLineToString( new File( getClass().getResource( "invalidInvoke.json" ).getFile() ) );
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
	public void isMinimalInvokeEvenValid() throws IOException
	{
		job = InvokeJob.fromJson( validMinimalInvoke, worker );
		assertThat( "Starting a Celery job using a minimal invoke-operation still has a method", job.getMethod(), is( "tasks.test.start" ) );
		assertThat( "Starting a Celery job using a minimal invoke-operation still has an ID, and it is a string ", job.getId(), is( "63e12bb7-ac49-44ad-ab7a-8a6a8b0ec3c0" ) );
	}

	@Test
	public void doesMaximalJobHaveAnyNullFields() throws IOException
	{

		job = InvokeJob.fromJson( validMaximalInvoke, worker );
		String noNullFieldsMessage = "In a maximal celery invoke job then no fields are null. ";
		assertThat( noNullFieldsMessage, job.getArgs(), is( not( nullValue() ) ) );
		assertThat( noNullFieldsMessage, job.getKwargs(), is( not( nullValue() ) ) );
		assertThat( noNullFieldsMessage, job.getEta(), is( not( nullValue() ) ) );
		assertThat( noNullFieldsMessage, job.getExpires(), is( not( nullValue() ) ) );
		assertThat( noNullFieldsMessage, job.getRetries(), is( not( nullValue() ) ) );
	}

	@Test( expected = NullPointerException.class )
	public void isTaskThatDoesNotContainAMandatoryJSONFieldShouldReturnNull()
	{
		job = InvokeJob.fromJson( invalidInvoke, worker );
	}
}
