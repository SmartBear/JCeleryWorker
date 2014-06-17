package org.loadui.jcelery;

public class TestUtils
{
	private static final String START_JOB = "tasks.controller.start";

	public static String createMessage( String id, String label, String arg )
	{
		return "{\"id\": \"" + id + "\", \"task\": \"" + label + "\", \"args\": [\"" + arg + "\"]}";
	}

	public static String createStartMessage(String id)
	{
		  return createMessage( id, START_JOB, "" );
	}
	public static String createStartMessage(String id, String arg)
	{
		return createMessage( id, START_JOB, arg );
	}
}
