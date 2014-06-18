package org.loadui.jcelery.tasks;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.loadui.jcelery.Job;
import org.loadui.jcelery.base.AbstractWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class RevokeJob extends AbstractJob
{
	private final String method;
	private final String signal;
	private final Boolean terminate;
	private final String serializer;
	private final String task_id;
	private final AbstractWorker service;

	public static RevokeJob fromJson( String json, AbstractWorker service )
	{
		Object o = JSONValue.parse( json );
		JSONObject jsonObject = ( JSONObject )o;

		String method = ( String )jsonObject.get( "method" );
		JSONObject arguments = ( JSONObject )jsonObject.get( "arguments" );
		String signal = ( String )arguments.get( "signal" );
		Boolean terminate = ( Boolean )arguments.get( "terminate" );
		String serializer = ( String )arguments.get( "serializer" );
		String task_id = ( String )arguments.get( "task_id" );
		String destination = ( String )jsonObject.get( "destination" );
		Builder builder = new Builder( method, task_id, service );

		return builder
				.destination( destination )
				.signal( signal )
				.serializer( serializer )
				.terminate( terminate )
				.build();
	}

	private RevokeJob( Builder b )
	{
		this.method = b.getMethod();
		this.signal = b.getSignal();
		this.terminate = b.getTerminate();
		this.task_id = b.getTask_id();
		this.serializer = b.getSerializer();
		this.service = b.getService();
	}

	@Override
	protected void respond( Job.Status status, Object result )
	{
		JSONObject obj = new JSONObject();

		obj.put( "status", Job.Status.REVOKED );
		obj.put( "traceback", null );
		obj.put( "result", result );
		obj.put( "task_id", task_id );
		obj.put( "children", new ArrayList() );

		service.respond( task_id, obj.toJSONString() );
	}

	@Override
	protected void respond( Status status )
	{
		respond( status, "" );
	}

   @Override
	public String getId()
	{
		return task_id;
	}

	@Override
	public String getMethod()
	{
		return method;
	}

	@Override
	public List getArgs()
	{
		return Lists.newArrayList( method, signal, terminate, task_id, serializer );
	}

	@Override
	public String toString()
	{
		return Objects.toStringHelper( this )
				.add( "method", method )
				.add( "signal", signal )
				.add( "terminate", terminate )
				.add( "task_id", task_id )
				.add( "serializer", serializer )
				.omitNullValues()
				.toString();
	}

	static class Builder
	{
		private String method;
		private String signal;
		private Boolean terminate;
		private String serializer;
		private String task_id;
		private AbstractWorker service;
		private String destination;

		private Builder( String method, String task_id, AbstractWorker service )
		{
			checkNotNull( method );
			checkNotNull( task_id );
			checkNotNull( service );
			this.method = method;
			this.task_id = task_id;
			this.service = service;
		}

		RevokeJob build()
		{
			return new RevokeJob( this );
		}

		public Builder signal( String signal )
		{
			this.signal = signal;
			return this;
		}


		public Builder terminate( Boolean terminate )
		{
			this.terminate = terminate;
			return this;
		}

		public Builder serializer( String serializer )
		{
			this.serializer = serializer;
			return this;
		}

		public Builder service( AbstractWorker service )
		{
			this.service = service;
			return this;
		}

		public Builder destination( String destination )
		{
			this.destination = destination;
			return this;
		}

		public String getMethod()
		{
			return method;
		}

		public String getSignal()
		{
			return signal;
		}

		public Boolean getTerminate()
		{
			return terminate;
		}

		public String getSerializer()
		{
			return serializer;
		}

		public String getTask_id()
		{
			return task_id;
		}

		public AbstractWorker getService()
		{
			return service;
		}

		public String getDestination()
		{
			return destination;
		}
	}
}
