package org.loadui.jcelery.errorhandling;

/**
 * Created by server on 2014-06-18.
 */
public class RevokeException extends CeleryException
{
	public RevokeException( String message ){
		super( message );
	}
}
