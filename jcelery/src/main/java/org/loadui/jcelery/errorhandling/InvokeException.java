package org.loadui.jcelery.errorhandling;

/**
 * Created by server on 2014-06-18.
 */
public class InvokeException extends CeleryException
{
	public InvokeException( String message )
	{
		super( message );
	}
}
