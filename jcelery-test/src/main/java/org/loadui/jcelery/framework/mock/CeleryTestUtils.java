package org.loadui.jcelery.framework.mock;

public class CeleryTestUtils
{
	private static final String START_JOB = "tasks.controller.startJob";

	public static String startJob(String id, String label, String arg)
	{
		return "{\"id\": \"" + id + "\", \"task\": \"" + label + "\", \"args\": [\"" + arg + "\"]}";
	}

	public static String startJob(String id)
	{
		  return startJob(id, START_JOB, "");
	}
	public static String startJob(String id, String arg)
	{
		return startJob(id, START_JOB, arg);
	}
}
