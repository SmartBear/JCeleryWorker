package org.loadui.jcelery.tasks;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.loadui.jcelery.base.Status;
import org.loadui.jcelery.api.Task;
import org.loadui.jcelery.base.Worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class MethodWorker implements Task
{
	private final String task;
	private final String id;
	private final List<Object> args;
	private final Map<Object, Object> kwargs;
	private final int retries;
	private final Date eta;
	private final Date expires;
	private final Worker service;

	public static MethodWorker fromJson( String json, Worker service )
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

	private MethodWorker( Builder b )
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

	@Override
	public void complete( Status status ) throws IOException
	{
		complete( status, "" );
	}

	@Override
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

	@Override
	public String toString()
	{
		return Objects.toStringHelper( this )
				.add( "task", task )
				.add( "id", id )
				.add( "args", args )
				.add( "kwargs", kwargs ).toString();
	}

	public String getTask()
	{
		return task;
	}

	public String getId()
	{
		return id;
	}

	public List getArgs()
	{
		return Lists.newArrayList( args );
	}

	public Map<Object, Object> getKwargs()
	{
		return ImmutableMap.copyOf( kwargs );
	}

	public int getRetries()
	{
		return retries;
	}

	public Date getEta()
	{
		return eta;
	}

	public Date getExpires()
	{
		return expires;
	}

	public Worker getService()
	{
		return service;
	}

	static class Builder
	{
		String task;
		String id;
		List<Object> args;
		Map<Object, Object> kwargs;
		int retries;
		Date eta;
		Date expires;
		Worker service;

		private Builder( String task, String id, Worker service )
		{
			checkNotNull( task );
			checkNotNull( id );
			checkNotNull( service );

			this.task = task;
			this.id = id;
			this.service = service;
		}

		MethodWorker build()
		{
			return new MethodWorker( this );
		}
	}
}
