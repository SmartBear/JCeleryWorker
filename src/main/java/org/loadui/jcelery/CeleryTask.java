package org.loadui.jcelery;

import com.google.common.base.Objects;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CeleryTask
{
	public final String task;
	public final String id;
	public final List<?> args;
	public final Map<String, Object> kwargs;
	public final int retries;
	public final Date eta;
	public final Date expires;

	public static CeleryTask fromJson(String json)
	{

		Object o = JSONValue.parse( json );
		JSONObject jsonObject = ( JSONObject )o;

		String task = ( String )jsonObject.get( "task" );
		String id = ( String )jsonObject.get( "id" );
		Builder builder = new Builder(task, id);

		builder.args = ( JSONArray )jsonObject.get( "args" );
		builder.kwargs = ( JSONObject )jsonObject.get( "kwargs" );

		return builder.build();
	}

	private CeleryTask( String task, String id, List<?> args, Map<String, Object> kwargs, int retries, Date eta, Date expires )
	{
		this.task = task;
		this.id = id;
		this.args = args;
		this.kwargs = kwargs;
		this.retries = retries;
		this.eta = eta;
		this.expires = expires;
	}

	public String toString()
	{
		return Objects.toStringHelper(this)
				.add( "task", task )
				.add( "id", id )
				.add( "args", args )
				.add( "kwargs", kwargs ).toString();
	}

	static class Builder
	{
		String task;
		String id;
		List<?> args;
		Map<String, Object> kwargs;
		int retries;
		Date eta;
		Date expires;

		private Builder(String task, String id)
		{
			this.task = task;
			this.id = id;
		}

		CeleryTask build()
		{
			return new CeleryTask(task, id, args, kwargs, retries, eta, expires);
		}
	}
}
