package org.loadui.jcelery.tasks;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.loadui.jcelery.Job;
import org.loadui.jcelery.base.AbstractWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class InvokeJob extends AbstractJob
{


	private final String task;
	private final String id;
	private final List<Object> args;
	private final Map<Object, Object> kwargs;
	private final Long retries;
	private final String eta;
	private final String expires;
	private final AbstractWorker service;

	public static InvokeJob fromJson( String json, AbstractWorker service )
	{
		Object o = JSONValue.parse( json );
		JSONObject jsonObject = ( JSONObject )o;

		String task = ( String )jsonObject.get( "task" );
		String id = ( String )jsonObject.get( "id" );
		String expires = ( String ) jsonObject.get( "expires" );
		String eta = (String) jsonObject.get( "eta" );
		Long retries = (Long) jsonObject.get( "retries" );
		JSONArray args = ( JSONArray )jsonObject.get( "args" );
		JSONObject kwargs = ( JSONObject )jsonObject.get( "kwargs" );

		Builder builder = new Builder( task, id, service );

		return builder
				.args( args )
				.kwargs( kwargs )
				.expires( expires )
				.retries( retries )
				.eta( eta )
				.build();
	}

	private InvokeJob( Builder b )
	{
		this.task = b.getTask();
		this.id = b.getId();
		this.args = b.getArgs();
		this.kwargs = b.getKwargs();
		this.retries = b.getRetries();
		this.eta = b.getEta();
		this.expires = b.getExpires();
		this.service = b.getService();
	}

	@Override
	protected void respond( Status status, Object result )
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
	protected void respond( Status status ){
		respond( status, "" );
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public String toString()
	{
		return Objects.toStringHelper( this )
				.add( "task", getTask() )
				.add( "id", id )
				.add( "args", args )
				.add( "eta", getEta() )
				.add( "expires", getExpires() )
				.add( "retries", getRetries() )
				.omitNullValues()
				.add( "kwargs", kwargs ).toString();
	}

	public String getMethod()
	{
		return getTask();
	}

	public List getArgs()
	{
		return Lists.newArrayList( args );
	}

	public Map<Object, Object> getKwargs()
	{
		return ImmutableMap.copyOf( kwargs );
	}

	public AbstractWorker getService()
	{
		return service;
	}

	public String getTask()
	{
		return task;
	}

	public Long getRetries()
	{
		return retries;
	}

	public String getEta()
	{
		return eta;
	}

	public String getExpires()
	{
		return expires;
	}

	static class Builder
	{
		private String task;
		private String id;
		private List<Object> args;
		private Map<Object, Object> kwargs;
		private Long retries;
		private String eta;
		private String expires;
		private AbstractWorker service;

		private Builder( String task, String id, AbstractWorker service )
		{
			checkNotNull( task );
			checkNotNull( id );
			checkNotNull( service );

			this.task = task;
			this.id = id;
			this.service = service;
		}

		InvokeJob build()
		{
			return new InvokeJob( this );
		}

		public List<Object> getArgs()
		{
			return args;
		}

		public Builder args( List<Object> args )
		{
			this.args = args;
			return this;
		}

		public Map<Object, Object> getKwargs()
		{
			return kwargs;
		}

		public Builder kwargs( Map<Object, Object> kwargs )
		{
			this.kwargs = kwargs;
			return this;
		}

		public Long getRetries()
		{
			return retries;
		}

		public Builder retries( Long retries )
		{
			this.retries = retries;
			return this;
		}

		public String getEta()
		{
			return eta;
		}

		public Builder eta( String eta )
		{
			this.eta = eta;
			return this;
		}

		public String getExpires()
		{
			return expires;
		}

		public Builder expires( String expires )
		{
			this.expires = expires;
			return this;
		}

		public AbstractWorker getService()
		{
			return service;
		}

		public Builder service( AbstractWorker service )
		{
			this.service = service;
			return this;
		}

		public String getTask()
		{
			return task;
		}

		public String getId()
		{
			return id;
		}
	}
}
