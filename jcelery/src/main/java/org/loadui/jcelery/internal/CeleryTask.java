package org.loadui.jcelery.internal;

import com.google.common.base.Objects;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.loadui.jcelery.Status;
import org.loadui.jcelery.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class CeleryTask implements Task
{
	public final String task;
	public final String id;
	public final List args;
	public final Map<Object, Object> kwargs;
	public final int retries;
	public final Date eta;
	public final Date expires;
	public final CeleryService service;

	public static CeleryTask fromJson( String json, CeleryService service )
	{
		Object o = JSONValue.parse( json );
		JSONObject jsonObject = ( JSONObject )o;

		String task = ( String )jsonObject.get( "task" );
		String id = ( String )jsonObject.get( "id" );
		Builder builder = new Builder( task, id, service );

		builder.args = ( JSONArray )jsonObject.get( "args" );
		builder.kwargs = ( JSONObject )jsonObject.get( "kwargs" );

		return builder.build();
	}

	private CeleryTask( Builder b )
	{
		this.task = b.task;
		this.id = b.id;
		this.args = b.args;
		this.kwargs = b.kwargs;
		this.retries = b.retries;
		this.eta = b.eta;
		this.expires = b.expires;
		this.service = b.service;
	}

	public void complete( Status status ) throws IOException
	{
		complete( status, "" );
	}

	public void complete( Status status, Object result ) throws IOException
	{
		JSONObject obj = new JSONObject();
		obj.put( "task_id", id );
		obj.put( "status", status.toString() );
		obj.put( "result", result );

		obj.put( "traceback", null );
		obj.put( "children", new ArrayList() );

		service.respond( id, obj.toJSONString() );
	}

	public String toString()
	{
		return Objects.toStringHelper( this )
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
		Map<Object, Object> kwargs;
		int retries;
		Date eta;
		Date expires;
		CeleryService service;

		private Builder( String task, String id, CeleryService service )
		{
			checkNotNull( task );
			checkNotNull( id );
			checkNotNull( service );

			this.task = task;
			this.id = id;
			this.service = service;
		}

		CeleryTask build()
		{
			return new CeleryTask( this );
		}
	}
}
